package org.rainark.whuassist.controller

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.apache.tomcat.jni.Address
import org.rainark.whuassist.config.JsonParam
import org.rainark.whuassist.entity.MapPosition
import org.rainark.whuassist.exception.cascadeSuccessResponse
import org.rainark.whuassist.mapper.MapMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.rainark.whuassist.util.LocationNetUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.StringJoiner

@RestController
class MapController {

    @Autowired
    lateinit var mapMapper: MapMapper


    @PostMapping("/map/all")
    fun getAll(@JsonParam msg: String): String {
        var result = mapMapper.selectList(QueryWrapper<MapPosition>().eq("kind", "map"))
        return cascadeSuccessResponse(result)
    }


    @PostMapping("/map/add")
    fun getPosition(
        @JsonParam address: String,
        @JsonParam city: String,
        @JsonParam wechatid: String,
        @JsonParam date: String
    ): String {
        var position = LocationNetUtil.getPosition(address, city)

        var dataFinal = date;
        var today = getDate()
        if (date == "today") {
            dataFinal = today
        }
        var key: String = address.toString() + dataFinal
        if (dataFinal.length == today.length) {
            var newPosition = MapPosition(key, address, city, position[0], position[1], wechatid, "map", dataFinal)
            try {
                mapMapper.insert(newPosition)
            } catch (e: org.springframework.dao.DuplicateKeyException) {
                var result = mapMapper.selectList(QueryWrapper<MapPosition>().eq("primarykey", key))
                return "The database already has this data: ${result[0].toString()}"
            }
        } else {
            return "Time format error, expecting like this : 2022-01-01"
        }
        return "yes"
    }

    @PostMapping("/map/addmanual")
    fun getPositionManual(
        @JsonParam address: String,
        @JsonParam city: String,
        @JsonParam wechatid: String,
        @JsonParam date: String,
        @JsonParam longitude: Double,
        @JsonParam latitude: Double
    ): String {
        var dataFinal = date;
        var today = getDate()
        if (date == "today") {
            dataFinal = today
        }
        var key: String = address.toString() + dataFinal
        if (dataFinal.length == today.length) {
            var newPosition = MapPosition(key, address, city, longitude, latitude, wechatid, "map", dataFinal)
            try {
                mapMapper.insert(newPosition)
            } catch (e: org.springframework.dao.DuplicateKeyException) {
                var result = mapMapper.selectList(QueryWrapper<MapPosition>().eq("primarykey", key))
                return "The database already has this data: ${result[0].toString()}"
            }
        } else {
            return "Time format error, expecting like this : 2022-01-01"
        }
        return "yes"
    }

    @PostMapping("/map/delete")
    fun deleteMap(
        @JsonParam address: String,
        @JsonParam date: String
    ): String {
        var key: String = address + date
        var dataStandardFormat: String = "2022-08-10"
        if (date.length == dataStandardFormat.length) {
            var search = mapMapper.selectList(QueryWrapper<MapPosition>().eq("primarykey", key))
            if (search.size == 0)
                return "This item does not exist in the database"
            mapMapper.delete(QueryWrapper<MapPosition>().eq("primarykey", key))
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