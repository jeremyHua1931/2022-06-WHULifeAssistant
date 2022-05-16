package org.rainark.whuassist.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.sql.Date
import java.sql.Time

const val ATTITUDE_NULL : Short = 0
const val ATTITUDE_SUPPORT : Short = 1
const val ATTITUDE_COMFORT : Short = 2
const val ATTITUDE_AGAINST : Short = 3

@TableName("xuser")
class User(
    @TableId(type = IdType.AUTO) var userId : Long,
    var wechatId : String,
    var username : String,
    var phone : String,
    var school : Int
){
    var hollow_name : String = ""
    var mbti : Short = 0
    var image : String = ""
}

@TableName("xhollow")
class Hollow(
    @TableId(type = IdType.AUTO) var hollowId : Long,
    var time : java.util.Date,
    var content : String,
    var under_post_id : Long,
    var reply_post_id : Long,
    var belong_to : Long
){
    var support_num : Int = 0
    var comfort_num : Int = 0
    var against_num : Int = 0
}
@TableName("xhattitude")
class HollowAttitude(
    var userId: Long,
    var hollowId: Long,
    var attitude : Short
)