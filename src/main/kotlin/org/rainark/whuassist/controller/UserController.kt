package org.rainark.whuassist.controller

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.rainark.whuassist.config.JsonParam
import org.rainark.whuassist.entity.Group
import org.rainark.whuassist.entity.ReplyHollowMsg
import org.rainark.whuassist.entity.ReturnHollow
import org.rainark.whuassist.entity.User
import org.rainark.whuassist.exception.RequestException
import org.rainark.whuassist.exception.ResponseCode
import org.rainark.whuassist.exception.cascadeSuccessResponse
import org.rainark.whuassist.exception.simpleSuccessResponse
import org.rainark.whuassist.mapper.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Date

@RestController
class UserController {

    @Autowired
    lateinit var userMapper: UserMapper
    @Autowired
    lateinit var replyHollowMapper: ReplyHollowMapper
    @Autowired
    lateinit var replyHollowMsgMapper: ReplyHollowMsgMapper<ReturnHollow>
    @Autowired
    lateinit var groupMapper: GroupMapper

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

    @PostMapping("/user/replyHollowList")
    fun getReplyHollowList(@JsonParam time : Date,
                           @JsonParam userId : Long) : String{
        if(userMapper.selectOne(QueryWrapper<User>().eq("user_id",userId)) == null)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"请求用户不存在")
        val replyHollowList = replyHollowMsgMapper.replyHollowSelect(time,userId)
        return cascadeSuccessResponse(replyHollowList)
    }

    @PostMapping("/user/finishReplyHollow")
    fun finishReplyHollow(@JsonParam userId: Long,
                          @JsonParam hollowId : Long) : String{
        if(userMapper.selectOne(QueryWrapper<User>().eq("user_id",userId)) == null)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"请求用户不存在")
        val replyHollow = replyHollowMapper.selectOne(QueryWrapper<ReplyHollowMsg>().eq("hollow_id",hollowId))
            ?: throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"已阅的帖子不存在")
        if(replyHollow.userId != userId)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"非法用户请求")
        replyHollowMapper.deleteById(replyHollow)
        return simpleSuccessResponse()
    }

    @PostMapping("/user/readReplyHollowNum")
    fun readReplyHollowNum(@JsonParam userId: Long) : String{
        if(userMapper.selectOne(QueryWrapper<User>().eq("user_id",userId)) == null)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"请求用户不存在")
        val count : Long = replyHollowMapper.selectCount(QueryWrapper<ReplyHollowMsg>().eq("user_id",userId))
        return simpleSuccessResponse("number" to count)
    }

    /**
     * 查看用户自己发布的群
     */
    @PostMapping("/user/findGroup")
    fun userGroupList(@JsonParam userId: Long) : String{
        val groupList = groupMapper.selectList(QueryWrapper<Group>()
            .eq("post_id",userId))
            ?: throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"不存在的用户")
        return cascadeSuccessResponse(groupList)
    }

}