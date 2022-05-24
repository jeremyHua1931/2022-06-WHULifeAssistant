package org.rainark.whuassist.controller

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import org.rainark.whuassist.config.JsonParam
import org.rainark.whuassist.entity.Group
import org.rainark.whuassist.entity.GroupAttitude
import org.rainark.whuassist.entity.ReportGroup
import org.rainark.whuassist.entity.User
import org.rainark.whuassist.exception.*
import org.rainark.whuassist.mapper.GroupAttitudeMapper
import org.rainark.whuassist.mapper.GroupMapper
import org.rainark.whuassist.mapper.ReportGroupMapper
import org.rainark.whuassist.mapper.UserMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GroupController {

    @Autowired
    lateinit var groupMapper: GroupMapper
    @Autowired
    lateinit var groupAttitudeMapper: GroupAttitudeMapper
    @Autowired
    lateinit var userMapper : UserMapper
    @Autowired
    lateinit var reportGroupMapper: ReportGroupMapper<ReportGroup>

    @PostMapping("/group/addgroup")
    fun addGroup(@JsonParam postId : Long,
                 @JsonParam name : String,
                 @JsonParam number: String,
                 @JsonParam qcCode : String,
                 @JsonParam introduction : String) : String{
        if(userMapper.selectOne(QueryWrapper<User>().eq("user_id",postId)) == null)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"请求用户异常")
        val groupNew = Group(0,postId,name,number,qcCode,introduction,0)
        groupNew.checkValid()
        groupMapper.insert(groupNew)
        return simpleSuccessResponse()
    }

    /**
     * 下面的是全群展览
     */
    @PostMapping("/group/list")
    fun groupList(@JsonParam id : Long) :String{
        val groupList = groupMapper.selectList(QueryWrapper<Group>()
            .orderBy(true,true,"post_id")
            .gt("post_id",id)
            .last("limit 10"))
        return cascadeSuccessResponse(groupList)
    }
    /**
     * 对群搜索
     */
    @PostMapping("/group/findbyid")
    fun findGroupById(@JsonParam groupId : String) :String{
        val groupList = groupMapper.selectList(QueryWrapper<Group>()
            .eq("group_id",groupId))
            ?: throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"不存在的用户")
        return cascadeSuccessResponse(groupList)
    }

    /**
     * 随机选取10个群进行展览
     */
    @PostMapping("/group/randomList")
    fun randomGroupList() : String{
        val groupList = groupMapper.selectList(QueryWrapper<Group>().orderByDesc("group_id"))
        val returnList : List<Group>
        if(groupList.size < 10){
            returnList = groupList
        }else{
            returnList = groupList.shuffled().take(10)
        }
        return cascadeSuccessResponse(returnList)

    }

    /**
     * 对名称模糊搜索
     */
    @PostMapping("/group/find")
    fun findGroup(@JsonParam name : String) :String{
        val groupList = groupMapper.selectList(QueryWrapper<Group>()
            .like("name",name))
        return cascadeSuccessResponse(groupList)
    }

    /**
     * 按照id删除群
     */
    @PostMapping("/group/deletebyid")
    fun deleteGroupById(@JsonParam groupId : String) :String{
        if(groupMapper.selectOne(QueryWrapper<Group>().eq("group_id",groupId))==null)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"所删帖子不存在")
        groupMapper.delete(QueryWrapper<Group>().eq("group_id",groupId))
        return simpleSuccessResponse("msg" to "删除成功")
    }

    /**
     * 举报群
     */
    @PostMapping("/group/report")
    fun reportGroup(@JsonParam userId : Long,
                    @JsonParam groupId : Long,
                    @JsonParam reportText : String) : String{
        val group = groupMapper.selectOne(QueryWrapper<Group>().eq("group_id",groupId))
            ?: throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"举报的群不存在")
        if(userMapper.selectOne(QueryWrapper<User>().eq("user_id",userId)) == null)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"请求用户异常")
        val groupAttitude = groupAttitudeMapper.selectOne(QueryWrapper<GroupAttitude>()
            .eq("user_id",userId)
            .eq("group_id",groupId))
            ?:GroupAttitude(userId,groupId,0,reportText);
        if(groupAttitude.report_attitude.toInt() == 1){
            return simpleMsgResponse(0,"已经举报啦")
        }else{
            group.reportNum++
            groupMapper.update(group,UpdateWrapper<Group>().eq("group_id",groupId))
            groupAttitude.report_attitude = 1
            groupAttitude.reportText = reportText
            if(groupAttitudeMapper.exists(QueryWrapper<GroupAttitude>()
                    .eq("user_id",userId)
                    .eq("group_id",groupId))){
                groupAttitudeMapper.update(groupAttitude,UpdateWrapper<GroupAttitude>()
                    .eq("user_id",userId)
                    .eq("group_id",groupId))
            }else{
                groupAttitudeMapper.insert(groupAttitude)
            }
        }
        return simpleMsgResponse(0,"成功举报")

    }


    /**
     * 举报群_管理员有对应的展示举报信息
     */
    @PostMapping("/group/reportGroupList")
    fun getReportList() : String{
        val reportList = reportGroupMapper.reportSelect()
        return cascadeSuccessResponse(reportList)
    }

    /**
     * 对用户进行封禁?
     */

}