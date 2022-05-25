package org.rainark.whuassist.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import org.rainark.whuassist.exception.RequestException
import org.rainark.whuassist.exception.ResponseCode

@TableName("xuser")
class User(
    @TableId(type = IdType.AUTO) var userId : Long,
    var wechatId : String,
    var username : String,
    var phone : String,
    var school : Int
){
    var hollow_name: String = ""
    var mbti: Short = 0
    var image: String = ""
    var competence : Int = 0
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
    @TableId
    val type: String,
    val choice: String,
    val crawltime: String,
    val ranks: Int,
    val name: String,
    val author: String,
    val novelurl: String,
    val image: String,
    val category: String,
    val subcategory: String,
    val completionstatus: String,
    val updatedchapter: String,
    val introduction: String
) {
    var myattitude: Int = 0
    var recommendtotal: Int = 0
    var unrecommendtotal: Int = 0
    var intj: Int = 0
    var intp: Int = 0
    var entj: Int = 0
    var entp: Int = 0

    var infj: Int = 0
    var infp: Int = 0
    var enfj: Int = 0
    var enfp: Int = 0

    var istj: Int = 0
    var isfj: Int = 0
    var estj: Int = 0
    var esfj: Int = 0

    var istp: Int = 0
    var isfp: Int = 0
    var estp: Int = 0
    var esfp: Int = 0

    var unintj: Int = 0
    var unintp: Int = 0
    var unentj: Int = 0
    var unentp: Int = 0

    var uninfj: Int = 0
    var uninfp: Int = 0
    var unenfj: Int = 0
    var unenfp: Int = 0

    var unistj: Int = 0
    var unisfj: Int = 0
    var unestj: Int = 0
    var unesfj: Int = 0

    var unistp: Int = 0
    var unisfp: Int = 0
    var unestp: Int = 0
    var unesfp: Int = 0
}

@TableName("xnovelall")
class NovelALL(
    @TableId
    val type: String,
    var crawltime: String,
    val name: String,
    val author: String,
    val novelurl: String,
    val image: String,
    val category: String,
    val subcategory: String,
    var completionstatus: String,
    val updatedchapter: String,
    val introduction: String
) {
    var recommendtotal: Int = 0
    var unrecommendtotal: Int = 0
    var intj: Int = 0
    var intp: Int = 0
    var entj: Int = 0
    var entp: Int = 0

    var infj: Int = 0
    var infp: Int = 0
    var enfj: Int = 0
    var enfp: Int = 0

    var istj: Int = 0
    var isfj: Int = 0
    var estj: Int = 0
    var esfj: Int = 0

    var istp: Int = 0
    var isfp: Int = 0
    var estp: Int = 0
    var esfp: Int = 0

    var unintj: Int = 0
    var unintp: Int = 0
    var unentj: Int = 0
    var unentp: Int = 0

    var uninfj: Int = 0
    var uninfp: Int = 0
    var unenfj: Int = 0
    var unenfp: Int = 0

    var unistj: Int = 0
    var unisfj: Int = 0
    var unestj: Int = 0
    var unesfj: Int = 0

    var unistp: Int = 0
    var unisfp: Int = 0
    var unestp: Int = 0
    var unesfp: Int = 0
}

@TableName("xmovie")
class Movie(
    val name: String,
    val crawltime: String,
    val ranks: Double,
    val detailpage: String,
    val image: String,
    val info: String,
    val description: String,
    @TableId
    val type: String
) {
    var myattitude: Int = 0
    var recommendtotal: Int = 0
    var unrecommendtotal: Int = 0
    var intj: Int = 0
    var intp: Int = 0
    var entj: Int = 0
    var entp: Int = 0

    var infj: Int = 0
    var infp: Int = 0
    var enfj: Int = 0
    var enfp: Int = 0

    var istj: Int = 0
    var isfj: Int = 0
    var estj: Int = 0
    var esfj: Int = 0

    var istp: Int = 0
    var isfp: Int = 0
    var estp: Int = 0
    var esfp: Int = 0

    var unintj: Int = 0
    var unintp: Int = 0
    var unentj: Int = 0
    var unentp: Int = 0

    var uninfj: Int = 0
    var uninfp: Int = 0
    var unenfj: Int = 0
    var unenfp: Int = 0

    var unistj: Int = 0
    var unisfj: Int = 0
    var unestj: Int = 0
    var unesfj: Int = 0

    var unistp: Int = 0
    var unisfp: Int = 0
    var unestp: Int = 0
    var unesfp: Int = 0
}

@TableName("xmovieall")
class MovieAll(

    val name: String,
    var crawltime: String,
    val ranks: Double,
    val detailpage: String,
    val image: String,
    val info: String,
    val description: String,
    @TableId
    val type: String

) {
    var recommendtotal: Int = 0
    var unrecommendtotal: Int = 0
    var intj: Int = 0
    var intp: Int = 0
    var entj: Int = 0
    var entp: Int = 0

    var infj: Int = 0
    var infp: Int = 0
    var enfj: Int = 0
    var enfp: Int = 0

    var istj: Int = 0
    var isfj: Int = 0
    var estj: Int = 0
    var esfj: Int = 0

    var istp: Int = 0
    var isfp: Int = 0
    var estp: Int = 0
    var esfp: Int = 0

    var unintj: Int = 0
    var unintp: Int = 0
    var unentj: Int = 0
    var unentp: Int = 0

    var uninfj: Int = 0
    var uninfp: Int = 0
    var unenfj: Int = 0
    var unenfp: Int = 0

    var unistj: Int = 0
    var unisfj: Int = 0
    var unestj: Int = 0
    var unesfj: Int = 0

    var unistp: Int = 0
    var unisfp: Int = 0
    var unestp: Int = 0
    var unesfp: Int = 0
}

@TableName("xtv")
class TV(
    @TableId
    var name: String,
    val crawltime: String,
    var ranks: Double,
    var detailpage: String,
    var image: String,
    var info: String,
    var description: String,
    var type: String
) {
    var myattitude: Int = 0
    var recommendtotal: Int = 0
    var unrecommendtotal: Int = 0
    var intj: Int = 0
    var intp: Int = 0
    var entj: Int = 0
    var entp: Int = 0

    var infj: Int = 0
    var infp: Int = 0
    var enfj: Int = 0
    var enfp: Int = 0

    var istj: Int = 0
    var isfj: Int = 0
    var estj: Int = 0
    var esfj: Int = 0

    var istp: Int = 0
    var isfp: Int = 0
    var estp: Int = 0
    var esfp: Int = 0

    var unintj: Int = 0
    var unintp: Int = 0
    var unentj: Int = 0
    var unentp: Int = 0

    var uninfj: Int = 0
    var uninfp: Int = 0
    var unenfj: Int = 0
    var unenfp: Int = 0

    var unistj: Int = 0
    var unisfj: Int = 0
    var unestj: Int = 0
    var unesfj: Int = 0

    var unistp: Int = 0
    var unisfp: Int = 0
    var unestp: Int = 0
    var unesfp: Int = 0
}

@TableName("xtvall")
class TVAll(
    @TableId
    var name: String,
    var crawltime: String,
    var ranks: Double,
    var detailpage: String,
    var image: String,
    var info: String,
    var description: String,
    var type: String

) {
    var recommendtotal: Int = 0
    var unrecommendtotal: Int = 0
    var intj: Int = 0
    var intp: Int = 0
    var entj: Int = 0
    var entp: Int = 0

    var infj: Int = 0
    var infp: Int = 0
    var enfj: Int = 0
    var enfp: Int = 0

    var istj: Int = 0
    var isfj: Int = 0
    var estj: Int = 0
    var esfj: Int = 0

    var istp: Int = 0
    var isfp: Int = 0
    var estp: Int = 0
    var esfp: Int = 0

    var unintj: Int = 0
    var unintp: Int = 0
    var unentj: Int = 0
    var unentp: Int = 0

    var uninfj: Int = 0
    var uninfp: Int = 0
    var unenfj: Int = 0
    var unenfp: Int = 0

    var unistj: Int = 0
    var unisfj: Int = 0
    var unestj: Int = 0
    var unesfj: Int = 0

    var unistp: Int = 0
    var unisfp: Int = 0
    var unestp: Int = 0
    var unesfp: Int = 0
}

@TableName("xgroup")
class Group(
    @TableId (type = IdType.AUTO) var groupId : Long,
    var postId : Long,
    var name: String,
    var number : String,
    var qrCode : String,
    var introduction: String,
    var reportNum : Int

){
    public fun checkValid(){
        if(name == ""){
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"请输入非空的群名")
        }
        if(name.length > 19){
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"群名输入过长")
        }
        if(number == ""){
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"请输入非空的群号")
        }
        if(number.length > 19){
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"群号输入过长，请输入有效的群号")
        }
        if(qrCode == ""){
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"群描述不能为空")
        }
        if(qrCode.length > 99){
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"群描述过长，群描述不能超过100字符")
        }
        if(introduction == ""){
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"请输入非空的群描述")
        }
        if(introduction.length > 255){
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"群描述不能超过255个字符")
        }
    }
}

@TableName("xgattitude")
class GroupAttitude(
    var userId: Long,
    var groupId : Long,
    var report_attitude : Short,
    var reportText : String
)