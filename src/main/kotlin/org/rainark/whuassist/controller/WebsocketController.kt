package org.rainark.whuassist.controller

import com.alibaba.fastjson.JSONObject
import org.rainark.whuassist.config.JsonParam
import org.rainark.whuassist.config.JsonWebsocketHandler
import org.rainark.whuassist.config.WSSession
import org.rainark.whuassist.exception.RequestException
import org.rainark.whuassist.exception.ResponseCode
import org.rainark.whuassist.exception.messagePush
import org.rainark.whuassist.exception.simpleSuccessResponse
import org.rainark.whuassist.util.JwtTokenUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Suppress("unused")
class WebsocketController {
    companion object {
        /** 对方请求聊天 -> 被请求者,请求阶段 */
        const val REQUEST_CHAT = "requestChat"
        /** 对方取消聊天请求 -> 被请求者,请求阶段 */
        const val CANCEL_REQUEST = "cancelChatRequest"
        /** 对方接受聊天请求 -> 请求者，请求阶段 */
        const val ACCEPT_REQUEST = "acceptChatRequest"
        /** 对方拒绝聊天请求 -> 请求者，请求阶段 */
        const val REJECT_REQUEST = "rejectChatRequest"
        /** 对方下线 -> 请求者,请求阶段；聊天双方，聊天阶段 */
        const val TARGET_GO_OFFLINE = "userGoOffline"
        /** 聊天结束 -> 聊天双方，聊天阶段 */
        const val CHAT_CLOSED = "chatClosed"
        /** 聊天消息 -> 聊天双方，聊天阶段 */
        const val CHAT_MESSAGE = "message"
    }
    @Autowired
    lateinit var jwtTokenUtil : JwtTokenUtil
    @Autowired
    lateinit var jsonWebsocketHandler : JsonWebsocketHandler
    /**
     * 用户通过websocket连接后的身份认证函数，不要修改
     * 用户认证之前无法访问其他Websocket接口
     * */
    fun authorization(@JsonParam token : String,
                      session : WSSession
    ) : String {
        session.user = jwtTokenUtil.getUser(token)
        jsonWebsocketHandler.userAuthorizedCallback(session)
        return simpleSuccessResponse()
    }

    /**
     * 当已认证的websocket连接关闭后的回调函数
     * */
    fun afterConnectionClose(session : WSSession) {
        val thisUserId = session.user!!.userId
        session.chatRelations.forEach { (userId, status) ->
            when(status) {
                ChatStatus.REQUESTED -> jsonWebsocketHandler.getUserSession(userId)?.apply {
                        chatRelations.remove(thisUserId)
                        sendMessage(messagePush("action" to CANCEL_REQUEST, "user" to session.user!!))
                    }
                ChatStatus.PENDING, ChatStatus.ESTABLISHED -> jsonWebsocketHandler.getUserSession(userId)?.apply {
                        chatRelations.remove(thisUserId)
                        sendMessage(messagePush("action" to TARGET_GO_OFFLINE, "user" to session.user!!))
                    }
            }
        }
    }

    /**
     * 获取指定用户是否在线
     * */
    fun getUserOnlineStat(@JsonParam userId : Long) : String {
        jsonWebsocketHandler.getUserSession(userId)
            ?: return simpleSuccessResponse("online" to false)
        return simpleSuccessResponse("online" to true)
    }

    /**
     * 发起聊天请求
     * */
    fun requestChat(@JsonParam userId : Long, session : WSSession) : String {
        if(userId == session.user!!.userId)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER, "不能和自己聊天")
        val targetSession = jsonWebsocketHandler.getUserSession(userId)
            ?: throw RequestException(ResponseCode.USER_OFFLINE)
        val duplicate = session.chatRelations[userId]
        if(duplicate != null)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER, when(duplicate) {
                ChatStatus.REQUESTED -> "已向对方发送过请求"
                ChatStatus.PENDING -> "对方已向你发送了请求"
                ChatStatus.ESTABLISHED -> "对方已与你建立聊天"
            })
        targetSession.sendMessage(messagePush("action" to REQUEST_CHAT, "user" to session.user!!))
        session.chatRelations[userId] = ChatStatus.REQUESTED
        targetSession.chatRelations[session.user!!.userId] = ChatStatus.PENDING
        return simpleSuccessResponse()
    }

    /**
     * 处理聊天请求(接受/拒绝)
     * */
    fun handleRequest(@JsonParam targetUserId : Long, @JsonParam accept : Boolean, session : WSSession) : String {
        val status = session.chatRelations[targetUserId]
            ?: throw RequestException(ResponseCode.ILLEGAL_PARAMETER, "没有来自此用户的请求")
        when(status) {
            ChatStatus.REQUESTED -> throw RequestException(ResponseCode.ILLEGAL_PARAMETER, "没有来自此用户的请求")
            ChatStatus.ESTABLISHED -> throw RequestException(ResponseCode.ILLEGAL_PARAMETER, "已经与此用户建立聊天")
            ChatStatus.PENDING -> {
                if(accept) {
                    session.chatRelations[targetUserId] = ChatStatus.ESTABLISHED
                    jsonWebsocketHandler.getUserSession(targetUserId)!!.apply{
                        chatRelations[session.user!!.userId] = ChatStatus.ESTABLISHED
                        sendMessage(messagePush("action" to ACCEPT_REQUEST, "user" to session.user!!))
                    }
                } else {
                    session.chatRelations.remove(targetUserId)
                    jsonWebsocketHandler.getUserSession(targetUserId)!!.apply {
                        chatRelations.remove(session.user!!.userId)
                        sendMessage(messagePush("action" to REJECT_REQUEST, "user" to session.user!!))
                    }
                }
            }
        }
        return simpleSuccessResponse()
    }

    /**
     * 获取当前用户的聊天请求列表
     * */
    fun getRequestList(session : WSSession) : String {
        val list = session.chatRelations.map { JSONObject().apply {
            put("userId", it.key)
            put("status", it.value.name)
        } }
        return simpleSuccessResponse("list" to list)
    }

    /**
     * 结束聊天
     * */
    fun closeChat(@JsonParam userId : Long, session : WSSession) : String {
        session.chatRelations.remove(userId)
        jsonWebsocketHandler.getUserSession(userId)!!.apply {
            chatRelations.remove(session.user!!.userId)
            sendMessage(messagePush("action" to CHAT_CLOSED, "user" to session.user!!))
        }
        return simpleSuccessResponse()
    }

    /**
     * 发送聊天信息
     * */
    fun chat(@JsonParam userId : Long, @JsonParam message : String, session : WSSession) : String {
        if(session.chatRelations[userId] != ChatStatus.ESTABLISHED)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER, "没有建立聊天")
        jsonWebsocketHandler.getUserSession(userId)!!
            .sendMessage(messagePush("action" to CHAT_MESSAGE, "chatMessage" to message, "user" to session.user!!))
        return simpleSuccessResponse()
    }
}


enum class ChatStatus {
    REQUESTED, PENDING, ESTABLISHED
}