package org.rainark.whuassist.util

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.rainark.whuassist.entity.Movie
import org.rainark.whuassist.entity.Novel
import org.rainark.whuassist.entity.TV
import org.rainark.whuassist.mapper.MovieMapper
import org.rainark.whuassist.mapper.NovelMapper
import org.rainark.whuassist.mapper.TVMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
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
    lateinit var movieMapper: MovieMapper

    @Autowired
    lateinit var novelMapper: NovelMapper

    @Autowired
    lateinit var TVMapper: TVMapper

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
    @Scheduled(cron = "0 */5 * * * ?")
    fun updateCrawlToMySQL() {
        if (recordCount == 0) {
            println("Init database at startup......")
        } else {
            println("Update database for the $recordCount time......")
        }
        recordCount++
        println("Start to update recommended list and it takes about twenty seconds...." + "       " + LocalDateTime.now())
        //1-获取最新的爬虫结果
        var resultYuepiao = ArrayList<Novel>()
        var resultRecommend = ArrayList<Novel>()
        var resultMMYuepiao = ArrayList<Novel>()
        var resultMMRecommend = ArrayList<Novel>()
        var resultMovie = ArrayList<Movie>()
        var resultTV = ArrayList<TV>()
        resultYuepiao = NovelNetUtil.getNovels(10, "", "yuepiao", "")
        resultRecommend = NovelNetUtil.getNovels(10, "", "recom", "")
        resultMMYuepiao = NovelNetUtil.getNovels(10, "mm", "yuepiao", "")
        resultMMRecommend = NovelNetUtil.getNovels(10, "mm", "recom", "")
        resultMovie = MovieNetUtil.getMovies(0, 10, true)
        resultTV = TVNetUtil.getTVs(0, 10, true)

        resultMovie.sortWith(Comparator { movie, t1 ->
            +
            if (movie.ranks >= t1.ranks) -1 else {
                1
            }
        })
        resultTV.sortWith(Comparator { TV, t1 ->
            if (TV.ranks >= t1.ranks) -1 else {
                1
            }
        })

//        printNovelCrawl(resultYuepiao)
//        printNovelCrawl(resultRecommend)
//        printNovelCrawl(resultMMYuepiao)
//        printNovelCrawl(resultMMRecommend)
//        printMovieCrawl(resultMovie)
//        printTVCrawl(resultTV)

//        println("Start to update database ...." + "             " + LocalDateTime.now())
        novelMapper.delete(QueryWrapper<Novel>().eq("kind", "novel"))
        resultYuepiao.addAll(resultRecommend)
        resultYuepiao.addAll(resultMMYuepiao)
        resultYuepiao.addAll(resultMMRecommend)
        for (x in resultYuepiao) {
            novelMapper.insert(x)
        }

        movieMapper.delete(QueryWrapper<Movie>().eq("type", "movie"))
        for (x in resultMovie) {
            movieMapper.insert(x)
        }

        TVMapper.delete(QueryWrapper<TV>().eq("type", "TV"))
        for (x in resultTV) {
            TVMapper.insert(x)
        }
//        println("Updating  database  is finished ...." + "      " + LocalDateTime.now())
        println("recommended list is already refreshed" + "     " + LocalDateTime.now())

    }
    //3-更新SQL

    private fun printNovelCrawl(result: ArrayList<Novel>) {
        for (x in result) {
            println(x.ranks.toString() + " " + x.type + " : " + x.name + " " + x.author + " " + x.novelurl)
        }
    }

    private fun printMovieCrawl(result: ArrayList<Movie>) {
        for (x in result) {
            println(x.ranks.toString() + " : " + x.name)
        }
    }

    private fun printTVCrawl(result: ArrayList<TV>) {
        for (x in result) {
            println(x.ranks.toString() + " : " + x.name)
        }
    }

}
