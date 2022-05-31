package org.rainark.whuassist.config

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import org.rainark.whuassist.controller.ChatStatus
import org.rainark.whuassist.controller.ExceptionController
import org.rainark.whuassist.controller.WebsocketController
import org.rainark.whuassist.entity.User
import org.rainark.whuassist.exception.RequestException
import org.rainark.whuassist.exception.ResponseCode
import org.rainark.whuassist.util.unpackCause
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.full.functions
import kotlin.reflect.full.starProjectedType

@Component
class JsonWebsocketHandler : TextWebSocketHandler() {
    @Autowired
    lateinit var exceptionController : ExceptionController
    @Autowired
    lateinit var websocketController : WebsocketController
    @Autowired
    lateinit var fastJsonParameterResolver : FastJsonParameterResolver
    val sessionMap = ConcurrentHashMap<String, WSSession>()
    companion object {
        const val authorizationMethod = "authorization"
        const val authorizedUserPrefix = "USER_"
    }
    private val methodHeader = "method"
    private val logger = LoggerFactory.getLogger(JsonWebsocketHandler::class.java)
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            processMessage(session, message)
        } catch (e : Exception) {
            sessionMap[session.id]!!.sendMessage(exceptionController.handelUnexpectedException(
                (if(e is InvocationTargetException) e.unpackCause() else e) as Exception
            ))
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.info("Connection established")
        sessionMap[session.id] = WSSession(session)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        logger.info("Connection established")
        val ws = sessionMap.remove(session.id)!!
        if(ws.user == null) return
        sessionMap.remove("${authorizedUserPrefix}${ws.user!!.userId}")
        websocketController.afterConnectionClose(ws)
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        logger.error("Transport error", exception)
        session.close(CloseStatus.PROTOCOL_ERROR)
        sessionMap.remove(session.id)
    }

    fun processMessage(session : WebSocketSession, message : TextMessage) {
        val json = JSON.parseObject(message.payload)
        if(!json.containsKey(methodHeader))
            throw RequestException(ResponseCode.PARAMETER_MISSING, methodHeader)
        val method = json.getString(methodHeader)
        if(sessionMap[session.id]!!.user == null && method != authorizationMethod)
            throw RequestException(ResponseCode.FORBIDDEN)
        val func = websocketController::class.functions.find { it.name == method && it.visibility == KVisibility.PUBLIC }
            ?: throw RequestException(ResponseCode.METHOD_NOT_FOUND, "method[$method]")
        callControllerMethod(session, func, json.getJSONObject("data") ?: JSONObject())
    }

    fun callControllerMethod(session : WebSocketSession, method : KFunction<*>, parameters : JSONObject){
        val arguments = method.parameters.associateWith {
            when(it.type) {
                WebsocketController::class.starProjectedType -> websocketController
                WSSession::class.starProjectedType -> sessionMap[session.id]
                User::class.starProjectedType -> session.attributes[authorizationMethod] as User
                else -> fastJsonParameterResolver.resolveArgument0(parameters, KotlinJsonParameter(it))
            }
        }
        val result = method.callBy(arguments).toString()
        sessionMap[session.id]!!.sendMessage(result)
    }

    fun userAuthorizedCallback(session : WSSession) {
        sessionMap["$authorizedUserPrefix${session.user!!.userId}"] = session
    }

    fun getUserSession(userId : Long) = sessionMap["$authorizedUserPrefix$userId"]
}
/**
 * 建立的Websocket链接。用户打开小程序时应尽快建立链接，以便接收聊天请求。
 * 包含同意聊天的用户信息，Websocket连接关闭时这些信息不会保留。
 * */
class WSSession(ws : WebSocketSession) {
    private val session : ConcurrentWebSocketSessionDecorator
    var user : User? = null
    val chatRelations = ConcurrentHashMap<Long, ChatStatus>()
    init {
        session = ConcurrentWebSocketSessionDecorator(ws, 30 * 1000, 4096)
    }
    fun sendMessage(message : String) = session.sendMessage(TextMessage(message))
}