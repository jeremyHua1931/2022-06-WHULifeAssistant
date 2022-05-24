package org.rainark.whuassist.entity

import com.baomidou.mybatisplus.annotation.TableName

/*
    0 表示未表明态度
    1  表示支持
    -1  表示反对
 */
@TableName("novelattitude")
class NovelAttitude(
    var wechatid: String = "null",
    var name: String = "null",
    var author: String = "null",
    var attitude: Int = 0
) {
}

@TableName("movieattitude")
class MovieAttitude
    (
    var wechatid: String = "null",
    var name: String = "null",
    var ranks: Double = 0.0,
    var attitude: Int = 0
) {
}

@TableName("tvattitude")
class TVAttitude
    (
    var wechatid: String = "null",
    var name: String = "null",
    var ranks: Double = 0.0,
    var attitude: Int = 0
) {
}