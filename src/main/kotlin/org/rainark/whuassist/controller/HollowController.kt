package org.rainark.whuassist.controller

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import org.rainark.whuassist.config.JsonParam
import org.rainark.whuassist.entity.ATTITUDE_SUPPORT
import org.rainark.whuassist.entity.Hollow
import org.rainark.whuassist.entity.HollowAttitude
import org.rainark.whuassist.entity.User
import org.rainark.whuassist.exception.*
import org.rainark.whuassist.mapper.HollowAttitudeMapper
import org.rainark.whuassist.mapper.HollowMapper
import org.rainark.whuassist.mapper.UserMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import java.util.regex.Pattern

@RestController
class HollowController {

    @Autowired
    lateinit var hollowMapper: HollowMapper
    @Autowired
    lateinit var userMapper: UserMapper
    @Autowired
    lateinit var hollowAttitudeMapper: HollowAttitudeMapper


    @PostMapping("/hollow/createHollow")
    fun createHollow(@JsonParam time:Date
                     ,@JsonParam content : String
                     ,@JsonParam under_post_id : Long
                     ,@JsonParam reply_post_id : Long
                     ,@JsonParam belong_to : Long): String{
        if(content == "")
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"发布内容不为空")
        if(under_post_id.toInt() != 0){
            if(hollowMapper.selectOne(QueryWrapper<Hollow>().eq("under_post_id", under_post_id)) == null)
                throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"帖子所属贴不存在或已删除")
        }
        if(reply_post_id.toInt() != 0){
            if(hollowMapper.selectOne(QueryWrapper<Hollow>().eq("reply_post_id",reply_post_id)) == null)
                throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"回复的帖子不存在或者已删除")
        }
        if(userMapper.selectOne(QueryWrapper<User>().eq("user_id",belong_to)) == null)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"帖子所属用户不存在")
        val hollow = Hollow(0,time,content,under_post_id,reply_post_id,belong_to)
        hollowMapper.insert(hollow)
        return simpleSuccessResponse()
    }

    @PostMapping("/hollow/getHollowList")
    fun getHollowList(@JsonParam time : Date): String{
        val hollowList = hollowMapper.selectList(QueryWrapper<Hollow>()
            .orderByDesc("time")
            .lt("time",time)
            .eq("under_post_id",0)
            .last("LIMIT 10")
            )
        return cascadeSuccessResponse(hollowList)
    }

    @PostMapping("/hollow/getHollowList/below")
    fun getHollowListBelow(@JsonParam time : Date
                           ,@JsonParam under_post_id: Int) :String{
        val hollowList = hollowMapper.selectList(QueryWrapper<Hollow>()
            .orderByDesc("time")
            .lt("time",time)
            .eq("under_post_id",under_post_id)
            .last("LIMIT 10")
        )
        return cascadeSuccessResponse(hollowList)
    }

    @PostMapping("/hollow/support")
    fun hollowSupport(@JsonParam userId:Long,
                      @JsonParam supportHollowId:Long):String{
        if(hollowAttitudeMapper.selectOne(QueryWrapper<HollowAttitude>()
                .eq("user_id",userId)
                .eq("hollow_id",supportHollowId)
                .eq("attitude", ATTITUDE_SUPPORT))!=null)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"已经点过赞了")
        val hollow = hollowMapper.selectOne(QueryWrapper<Hollow>().eq("hollow_id",supportHollowId))
            ?: throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"点赞的帖子不存在")
        hollow.support_num++
        hollowMapper.update(hollow,UpdateWrapper<Hollow>().eq("hollow_id",supportHollowId))
        val hollowAttitude = HollowAttitude(userId,supportHollowId,ATTITUDE_SUPPORT)
        hollowAttitudeMapper.insert(hollowAttitude)
        return simpleSuccessResponse()
    }

    @PostMapping("/hollow/comfort")
    fun hollowComfort(@JsonParam userId:Long,
                      @JsonParam comfortHollowId:Long):String{
        TODO()
    }





}