package org.rainark.whuassist.controller

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import org.rainark.whuassist.config.JsonParam
import org.rainark.whuassist.entity.*
import org.rainark.whuassist.exception.*
import org.rainark.whuassist.mapper.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class HollowController {

    @Autowired
    lateinit var hollowMapper: HollowMapper
    @Autowired
    lateinit var userMapper: UserMapper
    @Autowired
    lateinit var hollowAttitudeMapper: HollowAttitudeMapper
    @Autowired
    lateinit var returnHollowMapper: ReturnHollowMapper<ReturnHollow>
    @Autowired
    lateinit var returnHollowMapperP1: ReturnHollowMapperP1<ReturnHollowP1>
    @Autowired
    lateinit var againstHollowMapper: AgainstHollowMapper<AgainstHollow>
    @Autowired
    lateinit var replyHollowMapper : ReplyHollowMapper
    @Autowired
    lateinit var againstAttitudeMapper : AgainstAttitudeMapper
    @Autowired
    lateinit var againstTextMapper : AgainstTextMapper<ReportHollow>


    @PostMapping("/hollow/createHollow")
    fun createHollow(@JsonParam time:Date
                     ,@JsonParam content : String
                     ,@JsonParam under_post_id : Long
                     ,@JsonParam reply_post_id : Long
                     ,@JsonParam belong_to : Long): String{
        if(content == "")
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"发布内容不为空")
        if(under_post_id.toInt() != 0){
            if(hollowMapper.selectOne(QueryWrapper<Hollow>().eq("hollow_id", under_post_id)) == null)
                throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"帖子所属贴不存在或已删除")
        }
        if(reply_post_id.toInt() != 0){
            if(hollowMapper.selectOne(QueryWrapper<Hollow>().eq("hollow_id",reply_post_id)) == null)
                throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"回复的帖子不存在或者已删除")

        }
        if(userMapper.selectOne(QueryWrapper<User>().eq("user_id",belong_to)) == null)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"帖子所属用户不存在")
        if(hollowMapper.selectOne(QueryWrapper<Hollow>()
                .eq("belong_to",belong_to)
                .eq("time",time))!= null)
            throw RequestException(ResponseCode.UNEXPECTED_EXCEPTION,"请不要重复添加")
        val hollow = Hollow(0,time,content,under_post_id,reply_post_id,belong_to,0,0,0)
        hollowMapper.insert(hollow)
        val hollowThis = hollowMapper.selectOne(QueryWrapper<Hollow>()
            .eq("belong_to",belong_to)
            .eq("time",time))
        if(reply_post_id.toInt() != 0){
            val replyHollow = hollowMapper.selectOne(QueryWrapper<Hollow>().eq("hollow_id",reply_post_id))
                ?: throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"回复的帖子不存在或者已删除")
            replyHollowMapper.insert(ReplyHollowMsg(replyHollow.belong_to, hollowThis.hollowId)
            )
        }
        return simpleSuccessResponse()
    }

    @PostMapping("/hollow/getHollowList")
    fun getHollowList(@JsonParam time : Date,
                      @JsonParam userId: Long): String{
        if(userMapper.selectOne(QueryWrapper<User>().eq("user_id",userId)) == null)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"请求用户不存在")
        val returnListP1 = returnHollowMapperP1.multiSelect(time,0)
        val hollowList : MutableList<ReturnHollow> = ArrayList()
        returnListP1.forEach {
            val attitude = hollowAttitudeMapper.selectOne(QueryWrapper<HollowAttitude>()
                .eq("user_id",userId)
                .eq("hollow_id",it.hollowId))
                ?: HollowAttitude(userId,it.hollowId,0,0,0)
            hollowList.add(ReturnHollow(it.hollowId,
                                        it.time,
                                        it.content,
                                        it.under_post_id,
                                        it.reply_post_id,
                                        it.belong_to,
                                        it.support_num,
                                        it.comfort_num,
                                        it.username,
                                        it.image,
                                        attitude.support_attitude,
                                        attitude.comfort_attitude,
                                        attitude.against_attitude))
        }
        return cascadeSuccessResponse(hollowList)
    }

    @PostMapping("/hollow/getHollowList/below")
    fun getHollowListBelow(@JsonParam time : Date,
                           @JsonParam under_post_id: Long,
                           @JsonParam userId: Long) :String{
        if(userMapper.selectOne(QueryWrapper<User>().eq("user_id",userId)) == null)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"请求用户不存在")
        val returnListP1 = returnHollowMapperP1.multiSelect(time,under_post_id)
        val hollowList : MutableList<ReturnHollow> = ArrayList()
        returnListP1.forEach {
            val attitude = hollowAttitudeMapper.selectOne(QueryWrapper<HollowAttitude>()
                .eq("user_id",userId)
                .eq("hollow_id",it.hollowId))
                ?: HollowAttitude(userId,it.hollowId,0,0,0)
            hollowList.add(ReturnHollow(it.hollowId,
                it.time,
                it.content,
                it.under_post_id,
                it.reply_post_id,
                it.belong_to,
                it.support_num,
                it.comfort_num,
                it.username,
                it.image,
                attitude.support_attitude,
                attitude.comfort_attitude,
                attitude.against_attitude))
        }
        return cascadeSuccessResponse(hollowList)
    }

    @PostMapping("/hollow/support")
    fun hollowSupport(@JsonParam userId:Long,
                      @JsonParam hollowId:Long):String{
        if(userMapper.selectOne(QueryWrapper<User>().eq("user_id",userId)) == null)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"请求用户异常")
        val hollow = hollowMapper.selectOne(QueryWrapper<Hollow>().eq("hollow_id",hollowId))
            ?: throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"点赞的帖子不存在")
        val hollowAttitude = hollowAttitudeMapper.selectOne(QueryWrapper<HollowAttitude>()
            .eq("user_id",userId)
            .eq("hollow_id",hollowId))
            ?: HollowAttitude(userId,hollowId,0,0,0)
        if(hollowAttitude.support_attitude.toInt() == 1){
            hollow.support_num--
            hollowMapper.update(hollow,UpdateWrapper<Hollow>().eq("hollow_id",hollowId))
            hollowAttitude.support_attitude = 0
            hollowAttitudeMapper.update(hollowAttitude,UpdateWrapper<HollowAttitude>()
                .eq("user_id",userId)
                .eq("hollow_id",hollowId))
            return simpleMsgResponse(0,"赞已取消")
        }else{
            hollow.support_num++
            hollowMapper.update(hollow,UpdateWrapper<Hollow>().eq("hollow_id",hollowId))
            hollowAttitude.support_attitude = 1
            if(hollowAttitudeMapper.exists(QueryWrapper<HollowAttitude>()
                    .eq("user_id",userId)
                    .eq("hollow_id",hollowId))){
                hollowAttitudeMapper.update(hollowAttitude,UpdateWrapper<HollowAttitude>()
                    .eq("user_id",userId)
                    .eq("hollow_id",hollowId))
            }else{
                hollowAttitudeMapper.insert(hollowAttitude)
            }
            return simpleMsgResponse(0,"成功点赞")
        }
    }

    @PostMapping("/hollow/comfort")
    fun hollowComfort(@JsonParam userId:Long,
                      @JsonParam hollowId:Long):String{
        val hollow = hollowMapper.selectOne(QueryWrapper<Hollow>().eq("hollow_id",hollowId))
            ?: throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"安慰的帖子不存在")
        if(userMapper.selectOne(QueryWrapper<User>().eq("user_id",userId)) == null)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"请求用户异常")
        val hollowAttitude = hollowAttitudeMapper.selectOne(QueryWrapper<HollowAttitude>()
            .eq("user_id",userId)
            .eq("hollow_id",hollowId))
            ?: HollowAttitude(userId,hollowId,0,0,0)
        if(hollowAttitude.comfort_attitude.toInt() == 1){
            hollow.comfort_num--
            hollowMapper.update(hollow,UpdateWrapper<Hollow>().eq("hollow_id",hollowId))
            hollowAttitude.comfort_attitude = 0
            hollowAttitudeMapper.update(hollowAttitude,UpdateWrapper<HollowAttitude>()
                .eq("user_id",userId)
                .eq("hollow_id",hollowId))
            return simpleMsgResponse(0,"安慰已取消")
        }else{
            hollow.comfort_num++
            hollowMapper.update(hollow,UpdateWrapper<Hollow>().eq("hollow_id",hollowId))
            hollowAttitude.comfort_attitude = 1
            if(hollowAttitudeMapper.exists(QueryWrapper<HollowAttitude>()
                    .eq("user_id",userId)
                    .eq("hollow_id",hollowId))){
                hollowAttitudeMapper.update(hollowAttitude,UpdateWrapper<HollowAttitude>()
                    .eq("user_id",userId)
                    .eq("hollow_id",hollowId))
            }else{
                hollowAttitudeMapper.insert(hollowAttitude)
            }
            return simpleMsgResponse(0,"成功安慰")
        }
    }

    @PostMapping("/hollow/against")
    fun reportGroup(@JsonParam userId : Long,
                    @JsonParam hollowId : Long,
                    @JsonParam reportText : String) : String{
        val hollow = hollowMapper.selectOne(QueryWrapper<Hollow>().eq("hollow_id",hollowId))
            ?: throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"举报的群不存在")
        if(userMapper.selectOne(QueryWrapper<User>().eq("user_id",userId)) == null)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"请求用户异常")
        val hollowAgainstAttitude = againstAttitudeMapper.selectOne(QueryWrapper<HollowAgainst>()
            .eq("user_id",userId)
            .eq("hollow_id",hollowId))
            ?:HollowAgainst(userId,hollowId,0,reportText);
        if(hollowAgainstAttitude.against_attitude.toInt() == 1){
            return simpleMsgResponse(0,"已经举报啦")
        }else{
            hollow.against_num++
            hollowMapper.update(hollow,UpdateWrapper<Hollow>().eq("hollow_id",hollowId))
            hollowAgainstAttitude.against_attitude = 1
            hollowAgainstAttitude.reportText = reportText
            if(againstAttitudeMapper.exists(QueryWrapper<HollowAgainst>()
                    .eq("user_id",userId)
                    .eq("hollow_id",hollowId))){
                againstAttitudeMapper.update(hollowAgainstAttitude,UpdateWrapper<HollowAgainst>()
                    .eq("user_id",userId)
                    .eq("hollow_id",hollowId))
            }else{
                againstAttitudeMapper.insert(hollowAgainstAttitude)
            }
        }
        return simpleMsgResponse(0,"成功举报")

    }

    @PostMapping("/hollow/againstHollowList")
    fun getAgainstList() : String{
        val reportList = againstTextMapper.reportSelect()
        return cascadeSuccessResponse(reportList)
    }

    @PostMapping("/hollow/myHollowList")
    fun getMyHollowList(@JsonParam userId: Long,
                        @JsonParam time: Date) : String{
        if(userMapper.selectOne(QueryWrapper<User>().eq("user_id",userId)) == null)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"请求用户不存在")
        val returnListP1 = returnHollowMapperP1.myHollowSelect(time,userId)
        val hollowList : MutableList<ReturnHollow> = ArrayList()
        returnListP1.forEach {
            val attitude = hollowAttitudeMapper.selectOne(QueryWrapper<HollowAttitude>()
                .eq("user_id",userId)
                .eq("hollow_id",it.hollowId))
                ?: HollowAttitude(userId,it.hollowId,0,0,0)
            hollowList.add(ReturnHollow(it.hollowId,
                it.time,
                it.content,
                it.under_post_id,
                it.reply_post_id,
                it.belong_to,
                it.support_num,
                it.comfort_num,
                it.username,
                it.image,
                attitude.support_attitude,
                attitude.comfort_attitude,
                attitude.against_attitude))
        }
        return cascadeSuccessResponse(hollowList)
    }


    @PostMapping("/hollow/getHollowById")
    fun getHollowById(@JsonParam userId: Long,
                      @JsonParam hollowId: Long) : String{
        if(userMapper.selectOne(QueryWrapper<User>().eq("user_id",userId)) == null)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"请求用户不存在")
        val hollow = hollowMapper.selectOne(QueryWrapper<Hollow>().eq("hollow_id",hollowId))
            ?: throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"所请求帖子不存在或已被删除")
        val belongUser = userMapper.selectOne(QueryWrapper<User>().eq("user_id",hollow.belong_to))
        val attitude = hollowAttitudeMapper.selectOne(QueryWrapper<HollowAttitude>()
            .eq("user_id",hollow.belong_to)
            .eq("hollow_id",hollowId))
            ?: HollowAttitude(userId,hollow.belong_to,0,0,0)
        val returnHollow = ReturnHollow(
            hollow.hollowId,
            hollow.time,
            hollow.content,
            hollow.under_post_id,
            hollow.reply_post_id,
            hollow.belong_to,
            hollow.support_num,
            hollow.comfort_num,
            belongUser.hollow_name,
            belongUser.image,
            attitude.support_attitude,
            attitude.comfort_attitude,
            attitude.against_attitude
        )
        return simpleSuccessResponse("result" to returnHollow)
    }

    @PostMapping("/hollow/deleteHollow")
    fun deleteHollow(@JsonParam hollowId: Long) : String{
        if(hollowMapper.selectOne(QueryWrapper<Hollow>().eq("hollow_id",hollowId)) == null)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"删除的帖子不存在")
        hollowMapper.deleteById(hollowId)
        return simpleSuccessResponse()
    }
}