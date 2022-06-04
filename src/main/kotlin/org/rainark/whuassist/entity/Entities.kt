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
    var school : Int,
    var hollow_name: String,
    var mbti: Short,
    var image: String,
    var competence : Int,
){

}

@TableName("xhollow")
class Hollow(
    @TableId(type = IdType.AUTO) var hollowId : Long,
    var time : java.util.Date,
    var content : String,
    var under_post_id : Long,
    var reply_post_id : Long,
    var belong_to : Long,
    var support_num : Int,
    var comfort_num : Int,
    var against_num : Int
){

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
    var recommendtotal: Int = 320
    var unrecommendtotal: Int = 320
    var intj: Int = 10
    var intp: Int = 10
    var entj: Int = 10
    var entp: Int = 10

    var infj: Int = 10
    var infp: Int = 10
    var enfj: Int = 10
    var enfp: Int = 10

    var istj: Int = 10
    var isfj: Int = 10
    var estj: Int = 10
    var esfj: Int = 10

    var istp: Int = 10
    var isfp: Int = 10
    var estp: Int = 10
    var esfp: Int = 10

    var unintj: Int = 10
    var unintp: Int = 10
    var unentj: Int = 10
    var unentp: Int = 10

    var uninfj: Int = 10
    var uninfp: Int = 10
    var unenfj: Int = 10
    var unenfp: Int = 10

    var unistj: Int = 10
    var unisfj: Int = 10
    var unestj: Int = 10
    var unesfj: Int = 10

    var unistp: Int = 10
    var unisfp: Int = 10
    var unestp: Int = 10
    var unesfp: Int = 10
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
    var recommendtotal: Int = 320
    var unrecommendtotal: Int = 320
    var intj: Int = 10
    var intp: Int = 10
    var entj: Int = 10
    var entp: Int = 10

    var infj: Int = 10
    var infp: Int = 10
    var enfj: Int = 10
    var enfp: Int = 10

    var istj: Int = 10
    var isfj: Int = 10
    var estj: Int = 10
    var esfj: Int = 10

    var istp: Int = 10
    var isfp: Int = 10
    var estp: Int = 10
    var esfp: Int = 10

    var unintj: Int = 10
    var unintp: Int = 10
    var unentj: Int = 10
    var unentp: Int = 10

    var uninfj: Int = 10
    var uninfp: Int = 10
    var unenfj: Int = 10
    var unenfp: Int = 10

    var unistj: Int = 10
    var unisfj: Int = 10
    var unestj: Int = 10
    var unesfj: Int = 10

    var unistp: Int = 10
    var unisfp: Int = 10
    var unestp: Int = 10
    var unesfp: Int = 10
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
    var recommendtotal: Int = 320
    var unrecommendtotal: Int = 320
    var intj: Int = 10
    var intp: Int = 10
    var entj: Int = 10
    var entp: Int = 10

    var infj: Int = 10
    var infp: Int = 10
    var enfj: Int = 10
    var enfp: Int = 10

    var istj: Int = 10
    var isfj: Int = 10
    var estj: Int = 10
    var esfj: Int = 10

    var istp: Int = 10
    var isfp: Int = 10
    var estp: Int = 10
    var esfp: Int = 10

    var unintj: Int = 10
    var unintp: Int = 10
    var unentj: Int = 10
    var unentp: Int = 10

    var uninfj: Int = 10
    var uninfp: Int = 10
    var unenfj: Int = 10
    var unenfp: Int = 10

    var unistj: Int = 10
    var unisfj: Int = 10
    var unestj: Int = 10
    var unesfj: Int = 10

    var unistp: Int = 10
    var unisfp: Int = 10
    var unestp: Int = 10
    var unesfp: Int = 10
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
    var recommendtotal: Int = 320
    var unrecommendtotal: Int = 320
    var intj: Int = 10
    var intp: Int = 10
    var entj: Int = 10
    var entp: Int = 10

    var infj: Int = 10
    var infp: Int = 10
    var enfj: Int = 10
    var enfp: Int = 10

    var istj: Int = 10
    var isfj: Int = 10
    var estj: Int = 10
    var esfj: Int = 10

    var istp: Int = 10
    var isfp: Int = 10
    var estp: Int = 10
    var esfp: Int = 10

    var unintj: Int = 10
    var unintp: Int = 10
    var unentj: Int = 10
    var unentp: Int = 10

    var uninfj: Int = 10
    var uninfp: Int = 10
    var unenfj: Int = 10
    var unenfp: Int = 10

    var unistj: Int = 10
    var unisfj: Int = 10
    var unestj: Int = 10
    var unesfj: Int = 10

    var unistp: Int = 10
    var unisfp: Int = 10
    var unestp: Int = 10
    var unesfp: Int = 10
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
    var recommendtotal: Int = 320
    var unrecommendtotal: Int = 320
    var intj: Int = 10
    var intp: Int = 10
    var entj: Int = 10
    var entp: Int = 10

    var infj: Int = 10
    var infp: Int = 10
    var enfj: Int = 10
    var enfp: Int = 10

    var istj: Int = 10
    var isfj: Int = 10
    var estj: Int = 10
    var esfj: Int = 10

    var istp: Int = 10
    var isfp: Int = 10
    var estp: Int = 10
    var esfp: Int = 10

    var unintj: Int = 10
    var unintp: Int = 10
    var unentj: Int = 10
    var unentp: Int = 10

    var uninfj: Int = 10
    var uninfp: Int = 10
    var unenfj: Int = 10
    var unenfp: Int = 10

    var unistj: Int = 10
    var unisfj: Int = 10
    var unestj: Int = 10
    var unesfj: Int = 10

    var unistp: Int = 10
    var unisfp: Int = 10
    var unestp: Int = 10
    var unesfp: Int = 10
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
    var recommendtotal: Int = 320
    var unrecommendtotal: Int = 320
    var intj: Int = 10
    var intp: Int = 10
    var entj: Int = 10
    var entp: Int = 10

    var infj: Int = 10
    var infp: Int = 10
    var enfj: Int = 10
    var enfp: Int = 10

    var istj: Int = 10
    var isfj: Int = 10
    var estj: Int = 10
    var esfj: Int = 10

    var istp: Int = 10
    var isfp: Int = 10
    var estp: Int = 10
    var esfp: Int = 10

    var unintj: Int = 10
    var unintp: Int = 10
    var unentj: Int = 10
    var unentp: Int = 10

    var uninfj: Int = 10
    var uninfp: Int = 10
    var unenfj: Int = 10
    var unenfp: Int = 10

    var unistj: Int = 10
    var unisfj: Int = 10
    var unestj: Int = 10
    var unesfj: Int = 10

    var unistp: Int = 10
    var unisfp: Int = 10
    var unestp: Int = 10
    var unesfp: Int = 10
}

@TableName("xgroup")
class Group(
    @TableId (type = IdType.AUTO) var groupId : Long,
    var postId : Long,
    var name: String,
    var number : String,
    var introduction: String,
    var reportNum : Int

){
    var qrCode : String = ""
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

@TableName("xagainst")
class HollowAgainst(
    var userId: Long,
    var hollowId : Long,
    var against_attitude: Short,
    var reportText : String
)