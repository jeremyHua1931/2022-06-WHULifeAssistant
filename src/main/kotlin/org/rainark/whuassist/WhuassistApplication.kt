package org.rainark.whuassist

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@MapperScan("org.rainark.whuassist.mapper")
@EnableScheduling
class WhuassistApplication

fun main(args : Array<String>) {
    runApplication<WhuassistApplication>(*args)
}
