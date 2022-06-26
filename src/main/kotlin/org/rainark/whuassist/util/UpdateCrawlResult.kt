package org.rainark.whuassist.util

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.rainark.whuassist.entity.Movie
import org.rainark.whuassist.entity.Novel
import org.rainark.whuassist.entity.NovelALL
import org.rainark.whuassist.entity.TV
import org.rainark.whuassist.exception.RequestException
import org.rainark.whuassist.exception.ResponseCode
import org.rainark.whuassist.mapper.MovieMapper
import org.rainark.whuassist.mapper.NovelALLMapper
import org.rainark.whuassist.mapper.NovelMapper
import org.rainark.whuassist.mapper.TVMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.Date
import javax.annotation.PostConstruct

/**
 * Legacy class for using crawl results to update recommend list
 * including four types of novel lists from QiDian
 *           hot movie list from DouBan
 *           hot tv list from DouBan
 *  There are 10 of each kind, a total of 60
 *  @author Hua Zhangzhao
 *  Dependencies: NovelNetUtil.java, MovieNetUtil.java, TVNetUtil.java, Mappers.kt
 */

@Component
class UpdateCrawlResult {

    @Autowired
    lateinit var updateNovel: UpdateNovel

    @Autowired
    lateinit var updateMovie: UpdateMovie

    @Autowired
    lateinit var updateTV: UpdateTV

    /**
     * Delete some tables in database and insert crawl results to these tables
     * including xnovel table, xmovie table and xtv table
     * How to use(kotlin):
     *    @Autowired
     *    lateinit var updateCrawlResult: UpdateCrawlResult
     *    updateCrawlResult.updateCrawlToMySQL()
     */
    companion object {
        var recordCount: Int = 0
    }

    //    cron="0 */1 * * * ?"         测试本功能时,每一分钟执行一次
    //    cron="0 */5 * * * ?"         测试其他功能时,每五分钟执行一次
    //    cron="0 0 4 * * ?"           正常部署时,每天凌晨四点执行一次
    @PostConstruct
    @Scheduled(cron = "0 0 4 * * ?")
    fun updateCrawlToMySQL() {

        if (recordCount == 0) {
            println("Init database at startup......")
        } else {
            println("Update database for the $recordCount time......")
        }
        recordCount++

//        updateNovel.updateNovel()
//        updateTV.updateTV()
//        updateMovie.updateMovie()

        println("${LocalDateTime.now()} : recommended list has beend refreshed !")
    }

}
