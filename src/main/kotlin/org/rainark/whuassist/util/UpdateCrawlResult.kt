package org.rainark.whuassist.util

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.rainark.whuassist.entity.Movie
import org.rainark.whuassist.entity.Novel
import org.rainark.whuassist.entity.TV
import org.rainark.whuassist.mapper.MovieMapper
import org.rainark.whuassist.mapper.NovelMapper
import org.rainark.whuassist.mapper.TVMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime

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

    fun updateCrawlToMySQL() {
        println("Start to update recommended list and it needs some time...." + "       " + LocalDateTime.now())
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

        println("Start to update database ...." + "      " + LocalDateTime.now())
        novelMapper.delete(QueryWrapper<Novel>().eq("kind", "novel"))
        println("The Novel table is cleared")
        resultYuepiao.addAll(resultRecommend)
        resultYuepiao.addAll(resultMMYuepiao)
        resultYuepiao.addAll(resultMMRecommend)
        for (x in resultYuepiao) {
            novelMapper.insert(x)
        }
        println("The Novel table is refreshed" + "       " + LocalDateTime.now())

        movieMapper.delete(QueryWrapper<Movie>().eq("type", "movie"))
        println("The Movie table is cleared" + "         " + LocalDateTime.now())
        for (x in resultMovie) {
            movieMapper.insert(x)
        }
        println("The Movie table is refreshed" + "       " + LocalDateTime.now())

        TVMapper.delete(QueryWrapper<TV>().eq("type", "TV"))
        println("The TV table is cleared" + "            " + LocalDateTime.now())
        for (x in resultTV) {
            TVMapper.insert(x)
        }
        println("The TV table is refreshed" + "          " + LocalDateTime.now())
        println("recommended list is already refreshed" + "          " + LocalDateTime.now())

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
