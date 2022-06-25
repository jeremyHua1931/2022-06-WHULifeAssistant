package org.rainark.whuassist.controller

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import org.apache.tomcat.util.http.fileupload.IOUtils
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
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import javax.servlet.http.HttpServletResponse


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

    @Value("\${file.uploadQcFolder}")
    private val uploadFolder: String? = null

    @PostMapping("/group/addgroup")
    fun addGroup(@RequestParam postId : Long,
                 @RequestParam name : String,
                 @RequestParam number: String,
                 @RequestParam file : MultipartFile,
                 @RequestParam introduction : String) : String{
        if(userMapper.selectOne(QueryWrapper<User>().eq("user_id",postId)) == null)
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"请求用户异常")
        if(groupMapper.selectList(QueryWrapper<Group>()
                .eq("name",name)
                .eq("number",number)).isNotEmpty())
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"该群已存在请勿重复添加")

        val groupNew = Group(0,postId,name,number,introduction,0,"")
        groupNew.checkValid()
        groupMapper.insert(groupNew)

        val groupget = groupMapper.selectOne(QueryWrapper<Group>()
            .eq("name",name)
            .eq("number",number))
        val groupId = groupget.groupId

        val imageIn = file.bytes
        if(file.isEmpty){
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"传入的图片不能为空")
        }
        if(file.size > 1*1024*1024){
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"传入图片过大")
        }
        val fileType = file.contentType
        val types = ArrayList<String>()
        types.add("image/jpeg")
        types.add("image/png")
        types.add("image/jpg")
        if(fileType !in types){
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"上传失败，不允许上传" + fileType + "类型的图片")
        }

        val fileName = file.originalFilename
        val prefix: String = fileName?.substring(fileName.lastIndexOf(".") + 1) as String
        print(file.name)
        val fileExist = File("$uploadFolder$groupId.$prefix")
        if(fileExist.exists()){
            throw RequestException(ResponseCode.ILLEGAL_PARAMETER,"二维码已经上传过了")
        }
        val out = FileOutputStream("$uploadFolder$groupId.$prefix")
        out.write(imageIn)
        out.flush()
        out.close()

        groupget.qrCode = "$groupId.$prefix"
        groupMapper.update(groupget,UpdateWrapper<Group>().eq("group_id",groupId))
        return simpleSuccessResponse()
    }

    /**
     * 下面的是全群展览
     */
    @PostMapping("/group/list")
    fun groupList(@JsonParam id : Long) :String{
        val groupList = groupMapper.selectList(QueryWrapper<Group>()
            .orderBy(true,true,"group_id")
            .gt("group_id",id)
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

        val group = groupMapper.selectOne(QueryWrapper<Group>().eq("group_id",groupId))
        if(group.qrCode.isNotEmpty()) {
            val fileExist = File("$uploadFolder${group.qrCode}")
            if (fileExist.exists()) {
                fileExist.delete()
            }
        }
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

    @GetMapping("/group/image")
    fun getImage(@RequestParam groupId: Long, response : HttpServletResponse){
        val group = groupMapper.selectOne(QueryWrapper<Group>().eq("group_id",groupId))
        if(group.qrCode.isEmpty())
            throw RequestException(ResponseCode.TARGET_NOT_FOUND,"请求的二维码未上传")

        val fileName = group.qrCode
        val prefix: String = fileName?.substring(fileName.lastIndexOf(".") + 1) as String
        print(prefix)
        val fileExist = File("$uploadFolder${group.qrCode}")
        print(fileExist)
        if(!fileExist.exists()){
            throw RequestException(ResponseCode.TARGET_NOT_FOUND,"请求的二维码不存在")
        }
        response.contentType = "image/$prefix"

        val instream = FileInputStream(fileExist)
        IOUtils.copy(instream,response.outputStream)


    }



}