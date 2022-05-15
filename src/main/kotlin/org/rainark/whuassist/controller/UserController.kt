package org.rainark.whuassist.controller

import org.rainark.whuassist.config.JsonParam
import org.rainark.whuassist.entity.User
import org.rainark.whuassist.exception.RequestException
import org.rainark.whuassist.exception.ResponseCode
import org.rainark.whuassist.exception.simpleSuccessResponse
import org.rainark.whuassist.mapper.UserMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {

    @Autowired
    lateinit var userMapper: UserMapper

    @PostMapping("/user/register")
    fun register(@JsonParam wechatId : String,
                 @JsonParam username : String,
                 @JsonParam phone : String,
                 @JsonParam school : Int): String{
        //todo 判断wechatId的格式符合要求
        if(username == "")
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"请输入用户名称")
        if(phone == "")
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"请输入手机号")
        //todo 判断学校是否在学校表中
        val user = User(0,wechatId,username, phone, school)
        userMapper.insert(user)
        return simpleSuccessResponse()
    }
}