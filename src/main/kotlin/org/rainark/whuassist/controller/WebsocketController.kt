package org.rainark.whuassist.controller

import org.rainark.whuassist.config.JsonParam
import org.rainark.whuassist.config.JsonWebsocketHandler
import org.rainark.whuassist.config.WSSession
import org.rainark.whuassist.exception.simpleSuccessResponse
import org.rainark.whuassist.util.JwtTokenUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Suppress("unused")
class WebsocketController {
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


}