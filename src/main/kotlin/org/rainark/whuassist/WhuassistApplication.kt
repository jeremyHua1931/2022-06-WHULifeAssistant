package org.rainark.whuassist

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@MapperScan("org.rainark.whuassist.mapper")
class WhuassistApplication

fun main(args : Array<String>) {
    runApplication<WhuassistApplication>(*args)
}
