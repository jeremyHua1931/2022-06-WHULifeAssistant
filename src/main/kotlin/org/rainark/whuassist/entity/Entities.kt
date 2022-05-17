package org.rainark.whuassist.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

import java.sql.Date
import java.sql.Time

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
    var support_attitude : Short,
    var comfort_attitude : Short,
    var against_attitude : Short
)

@TableName("xreplyhollow")
class ReplyHollowMsg(
    var userId: Long,
    @TableId(type = IdType.AUTO) var hollowId: Long
)

@TableName("xnovel")
class Novel(
    @TableId(type = IdType.AUTO)
    val type:String,
    val choice:String,
    val ranks:Int,
    val name:String,
    val author:String,
    val novelurl:String,
    val image:String,
    val category: String,
    val subcategory:String,
    val completionstatus:String,
    val updatedchapter:String,
    val introduction:String
) {
}

@TableName("xmovie")
class Movie(
    @TableId(type = IdType.AUTO)
    val name:String,
    val ranks:Double,
    val detailpage:String,
    val image: String,
    val info:String,
    val description: String,
    val type:String

) {

}