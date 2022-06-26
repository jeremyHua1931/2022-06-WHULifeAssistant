package org.rainark.whuassist.controller

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.rainark.whuassist.config.JsonParam
import org.rainark.whuassist.entity.Group
import org.rainark.whuassist.entity.ReplyHollowMsg
import org.rainark.whuassist.entity.ReturnHollow
import org.rainark.whuassist.entity.User
import org.rainark.whuassist.exception.*
import org.rainark.whuassist.mapper.*
import org.rainark.whuassist.util.JwtTokenUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.StandardCharsets
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
    @Autowired
    lateinit var jwtTokenUtil : JwtTokenUtil

    @PostMapping("/user/register")
    fun register(
        @JsonParam wechatId: String,
        @JsonParam username: String,
        @JsonParam image: String
    ): String {
        if (username == "")
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER, "请输入用户名称")
        if (image.length > 254)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER, "头像导入出错")
        val user = User(0, wechatId, username, "null", 0, "", 0, image, 0)
        userMapper.insert(user)
        return simpleSuccessResponse()
    }

    @PostMapping("/user/mbti")
    fun setMbti(
        @JsonParam userId: Long,
        @JsonParam mbti: Int
    ): String {

        val user = userMapper.selectOne(QueryWrapper<User>().eq("user_id", userId))
            ?: throw RequestException(ResponseCode.ILLEGAL_PARAMETER, "所请求用户不存在")
        user.mbti = mbti.toShort()
        userMapper.update(user, QueryWrapper<User>().eq("user_id", userId))
        return simpleSuccessResponse()
    }

    @PostMapping("/user/login")
    fun login(@JsonParam wechatId: String): String {
        if (wechatId == "")
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER, "不能使用的微信Id登录")
        val user = userMapper.selectOne(QueryWrapper<User>().eq("wechat_id", wechatId))
            ?: throw RequestException(ResponseCode.ILLEGAL_PARAMETER, "请使用有效的微信Id登录")
        return simpleSuccessResponse("user" to user, "token" to jwtTokenUtil.createJWT(user.userId, user.username))
    }

    @PostMapping("/user/putimage")
    fun putImage(@JsonParam userId: Long,
                 @JsonParam image : String) : String{
        val user = userMapper.selectOne(QueryWrapper<User>().eq("user_id",userId))
            ?: throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"所请求用户不存在")
        if(image.length > 100)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"输入图片长度过长")
        user.image = image
        userMapper.update(user,QueryWrapper<User>().eq("user_id",userId))
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
    fun userGroupList(@JsonParam userId: Long): String {
        val groupList = groupMapper.selectList(
            QueryWrapper<Group>()
                .eq("post_id", userId)
        )
            ?: throw RequestException(ResponseCode.ILLEGAL_PARAMETER, "不存在的用户")
        return cascadeSuccessResponse(groupList)
    }

    @PostMapping("/user/puthollowname")
    fun setHollowName(
        @JsonParam userId: Long,
        @JsonParam holllowName: String
    ): String {
        val user = userMapper.selectOne(QueryWrapper<User>().eq("user_id", userId))
            ?: throw RequestException(ResponseCode.ILLEGAL_PARAMETER, "所请求用户不存在")
        if (holllowName.toByteArray(StandardCharsets.UTF_8).size > 31)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER, "输入用户昵称过长")
        user.hollow_name = holllowName
        userMapper.update(user, QueryWrapper<User>().eq("user_id", userId))
        return simpleSuccessResponse()
    }

}