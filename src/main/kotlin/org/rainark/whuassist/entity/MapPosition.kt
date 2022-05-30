package org.rainark.whuassist.entity

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.util.Date

@TableName("map")
class MapPosition(
    @TableId
    var primarykey: String,
    var address: String,
    var city: String,
    var longitude: Double,
    var latitude: Double,
    var wechatid: String,
    var kind: String,
    var dates: String
) {


}