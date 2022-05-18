package org.rainark.whuassist.mapper

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Select
import org.rainark.whuassist.entity.*
import org.rainark.whuassist.entity.*
import org.springframework.stereotype.Component
import java.util.Date

@Component
interface UserMapper : BaseMapper<User>

@Component
interface HollowMapper : BaseMapper<Hollow>

@Component
interface HollowAttitudeMapper : BaseMapper<HollowAttitude>

@Component
interface ReplyHollowMapper : BaseMapper<ReplyHollowMsg>

@Component
interface ReturnHollowMapper<T> : BaseMapper<T> {
    @Select("SELECT xhollow.hollow_id,time,content,under_post_id," +
            "reply_post_id,belong_to,support_num,comfort_num,username,image," +
            "support_attitude,comfort_attitude,against_attitude " +
            "FROM (xhollow JOIN xuser ON belong_to = user_id) " +
            "JOIN xhattitude ON xhattitude.hollow_id = xhollow.hollow_id " +
            "WHERE (time < #{time} AND under_post_id = #{under_post_id} " +
            "AND xhattitude.user_id = #{user_id}) ORDER BY time DESC " +
            "LIMIT 10")
    fun multiSelect(time : Date,under_post_id : Long,user_id : Long) : List<ReturnHollow>

    @Select("SELECT xhollow.hollow_id,time,content,under_post_id," +
            "reply_post_id,belong_to,support_num,comfort_num,username,image," +
            "support_attitude,comfort_attitude,against_attitude " +
            "FROM (xhollow JOIN xuser ON belong_to = user_id) " +
            "JOIN xhattitude ON xhattitude.hollow_id = xhollow.hollow_id " +
            "WHERE (time < #{time} AND belong_to = #{user_id} " +
            "AND xhattitude.user_id = #{user_id}) ORDER BY time DESC " +
            "LIMIT 10")
    fun myHollowSelect(time: Date,user_id: Long) : List<ReturnHollow>
}

@Component
interface AgainstHollowMapper<T> : BaseMapper<T>{
    @Select("SELECT hollow_id,time,content,under_post_id," +
            "reply_post_id,belong_to,against_num,username " +
            "FROM xhollow JOIN xuser ON belong_to = user_id " +
            "WHERE  against_num > 19 ORDER BY time")
    fun againstSelect() : List<AgainstHollow>
}

@Component
interface ReplyHollowMsgMapper<T> : BaseMapper<T>{
    @Select("SELECT xhollow.hollow_id,time,content,under_post_id," +
            "reply_post_id,belong_to,username,image " +
            "FROM (xhollow JOIN xuser ON belong_to = xuser.user_id) " +
            "JOIN xreplyhollow ON xreplyhollow.hollow_id = xhollow.hollow_id " +
            "WHERE (time < #{time} AND xreplyhollow.user_id = #{user_id}) " +
            "ORDER BY time DESC LIMIT 10")
    fun replyHollowSelect(time : Date,user_id : Long) : List<ReplyHollow>
}

@Component
interface MovieMapper : BaseMapper<Movie>

@Component
interface NovelMapper : BaseMapper<Novel>

@Component
interface TVMapper: BaseMapper<TV>
