package org.rainark.whuassist.controller

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.rainark.whuassist.config.JsonParam
import org.rainark.whuassist.entity.MapPosition
import org.rainark.whuassist.entity.Notice
import org.rainark.whuassist.exception.cascadeSuccessResponse
import org.rainark.whuassist.mapper.NoticeMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.text.SimpleDateFormat
import java.util.*

@RestController
class NoticeController {

    @Autowired
    lateinit var noticeMapper: NoticeMapper

    @PostMapping("/notice/all")
    fun getAll(@JsonParam msg: String): String {

        var result =
            noticeMapper.selectList(QueryWrapper<Notice>().eq("kind", "notice").orderByDesc("date").last("LIMIT 100"))

        return cascadeSuccessResponse(result)
    }

    @PostMapping("/notice/add")
    fun noticeAdd(
        @JsonParam date: String,
        @JsonParam title: String,
        @JsonParam content: String,
        @JsonParam wechatid: String,
        @JsonParam publisher: String
    ): String {

        var today = getDate()
        var key = date + publisher + title
        var newNotice = Notice(key, date, title, content, publisher, wechatid, "notice")
        if (date.length == today.length) {
            try {
                noticeMapper.insert(newNotice)
            } catch (e: org.springframework.dao.DuplicateKeyException) {
                return "This notification already exists in the database"
            }
        } else {
            return "Time format error, expecting like this : 2022-01-01"
        }
        return "yes"
    }

    @PostMapping("/notice/delete")
    fun noticeDelete(
        @JsonParam date: String,
        @JsonParam title: String,
        @JsonParam publisher: String
    ): String {

        var today = getDate()
        var key = date + publisher + title
        if (date.length == today.length) {
            var search = noticeMapper.selectList(QueryWrapper<Notice>().eq("type", key))
            if (search.size == 0)
                return "This item does not exist in the database"
            noticeMapper.delete(QueryWrapper<Notice>().eq("type", key))
        } else {
            return "Date format is wrong : expected : 2022-01-01"
        }
        return "yes"
    }


    fun getDate(): String {
        var date = SimpleDateFormat()
        date.applyPattern("yyyy-MM-dd")
        var now = Date()
        var result: String = date.format(now)
        return result
    }

}