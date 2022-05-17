package org.rainark.whuassist.util

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.rainark.whuassist.entity.Movie
import org.rainark.whuassist.entity.Novel
import org.rainark.whuassist.mapper.MovieMapper
import org.rainark.whuassist.mapper.NovelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UpdateCrawlResult {
    @Autowired
    lateinit var movieMapper: MovieMapper
    @Autowired
    lateinit var novelMapper: NovelMapper

    fun updateCrawlToMySQL() {
//        var test = Novel("2232358", "yuepiao", 1, "宅猪", "32", "2", "3", "1", " 2", " 3", " 4", "5")
//        novelMapper.insert(test)

        //1-获取最新的爬虫结果
        var resultYuepiao = ArrayList<Novel>()
        var resultRecommend = ArrayList<Novel>()
        var resultMMYuepiao = ArrayList<Novel>()
        var resultMMRecommend = ArrayList<Novel>()
        resultYuepiao = NovelNetUtil.getNovels(10, "", "yuepiao", "")
        resultRecommend = NovelNetUtil.getNovels(10, "", "recom", "")
        resultMMYuepiao = NovelNetUtil.getNovels(10, "mm", "yuepiao", "")
        resultMMRecommend = NovelNetUtil.getNovels(10, "mm", "recom", "")
        var resultMovie = ArrayList<Movie>()
        resultMovie = MovieNetUtil.getMovies(0, 10, true)
        resultMovie.sortWith(Comparator { movie, t1 ->
            if (movie.ranks >= t1.ranks) -1 else {
                1
            }
        })
//        printNovelCrawl(resultYuepiao)
//        printNovelCrawl(resultRecommend)
//        printNovelCrawl(resultMMYuepiao)
//        printNovelCrawl(resultMMRecommend)
//        printMovieCrawl(resultMovie)

        novelMapper.delete(QueryWrapper<Novel>().eq("choice","recom"))
        novelMapper.delete(QueryWrapper<Novel>().eq("choice","mmrecom"))
        novelMapper.delete(QueryWrapper<Novel>().eq("choice","yuepiao"))
        novelMapper.delete(QueryWrapper<Novel>().eq("choice","mmyuepiao"))
        println("The Novel table is cleared")

        resultYuepiao.addAll(resultRecommend)
        resultYuepiao.addAll(resultMMYuepiao)
        resultYuepiao.addAll(resultMMRecommend)
        for( x in resultYuepiao){
            novelMapper.insert(x)
        }
        println("The Novel table is refreshed")

        println("The Movie table is cleared")
        movieMapper.delete(QueryWrapper<Movie>().eq("type","movie"))
        for(x in resultMovie){
            movieMapper.insert(x)
        }
        println("The Movie table is refreshed")

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




}
