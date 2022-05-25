package org.rainark.whuassist.mapper

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.rainark.whuassist.entity.*
import org.springframework.stereotype.Component
import java.util.*

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
    @Select(
        "SELECT xhollow.hollow_id,time,content,under_post_id," +
                "reply_post_id,belong_to,username,image " +
                "FROM (xhollow JOIN xuser ON belong_to = xuser.user_id) " +
                "JOIN xreplyhollow ON xreplyhollow.hollow_id = xhollow.hollow_id " +
                "WHERE (time < #{time} AND xreplyhollow.user_id = #{user_id}) " +
                "ORDER BY time DESC LIMIT 10"
    )
    fun replyHollowSelect(time: Date, user_id: Long): List<ReplyHollow>
}

@Component
interface MovieMapper : BaseMapper<Movie> {
    @Update("TRUNCATE TABLE xmovie;")
    fun truncate()

    @Select("select * from xmovie;")
    fun selectAll(): List<Movie>
}

@Component
interface MovieAllMapper : BaseMapper<MovieAll> {
    @Select("select * from xmovieall;")
    fun selectAll(): List<Movie>
}


@Component
interface NovelMapper : BaseMapper<Novel> {
    @Update("TRUNCATE TABLE xnovel;")
    fun truncate()

    @Select("select * from xnovel;")
    fun selectAll(): List<Novel>
}

@Component
interface NovelALLMapper : BaseMapper<NovelALL> {
}

@Component
interface TVMapper : BaseMapper<TV>

@Component
interface TVAllMapper : BaseMapper<TVAll>

@Component
interface NovelAttitudeMapper : BaseMapper<NovelAttitude>

@Component
interface MovieAttitudeMapper : BaseMapper<MovieAttitude>

@Component
interface TVAttitudeMapper : BaseMapper<TVAttitude>

@Component
interface GroupMapper : BaseMapper<Group>

@Component
interface GroupAttitudeMapper : BaseMapper<GroupAttitude>

@Component
interface ReportGroupMapper<T> : BaseMapper<T>{
    @Select("SELECT xgroup.group_id,xuser.user_id,username,xgroup.name AS group_name," +
            "report_text,report_num " +
            "FROM (xgattitude JOIN xuser ON xgattitude.user_id = xuser.user_id) " +
            "JOIN xgroup ON xgroup.group_id = xgattitude.group_id " +
            "WHERE  report_num > 19 ORDER BY group_id")
    fun reportSelect() : List<ReportGroup>
}