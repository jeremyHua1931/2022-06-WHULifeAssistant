package org.rainark.whuassist.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId



class ReturnHollow(
    var hollowId : Long,
    var time : java.util.Date,
    var content : String,
    var under_post_id : Long,
    var reply_post_id : Long,
    var belong_to : Long,
    var support_num : Int,
    var comfort_num : Int,
    var username : String, //todo（）后面需要改成hollowname
    var image : String,
    var support_attitude : Short,
    var comfort_attitude : Short,
    var against_attitude : Short
)

class AgainstHollow(
    var hollowId : Long,
    var time : java.util.Date,
    var content : String,
    var under_post_id : Long,
    var reply_post_id : Long,
    var belong_to : Long,
    var against_num : Int,
    var username : String
)

class ReplyHollow(
    var hollowId : Long,
    var time : java.util.Date,
    var content : String,
    var under_post_id : Long,
    var reply_post_id : Long,
    var belong_to : Long,
    var username : String, //todo（）后面需要改成hollowname
    var image : String
)

class ReportGroup(
    var groupId : Long,
    var userId : Long,
    var username: String,
    var groupName: String,
    var reportText : String,
    var reportNum : Int
)