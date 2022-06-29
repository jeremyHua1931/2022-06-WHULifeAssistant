package org.rainark.whuassist.util

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.rainark.whuassist.entity.Movie
import org.rainark.whuassist.entity.MovieAll
import org.rainark.whuassist.mapper.MovieAllMapper
import org.rainark.whuassist.mapper.MovieMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class UpdateMovie {
    @Autowired
    lateinit var movieMapper: MovieMapper

    @Autowired
    lateinit var movieAllMapper: MovieAllMapper

    companion object {
        var recordCount: Int = 0
        var updateTimeNew: String = "hot"
        var updateTimeOld: String = "hot"
    }

    fun updateMovie() {

        println("${LocalDateTime.now()}  Start to update Movie List....")
        //初始化总表

        var movieJudge = movieAllMapper.selectAll()
        if (recordCount == 0 && movieJudge.size == 0) {
            var resultMovie: ArrayList<Movie>
//            resultMovie = MovieNetUtil.getMovies(0, 20, true,true)
            println("${LocalDateTime.now()}  Start to craw  moives  .....")
            resultMovie = MovieNetUtil.getMovies(0, 100, true, true, "old")
            println("${LocalDateTime.now()}  Start to init MovieALL Table....." + "       " + LocalDateTime.now())
            for (x in resultMovie) {
                if (x.ranks >= 7.5) {
                    var MovieALLtmp =
                        MovieAll(x.name, x.crawltime, x.ranks, x.detailpage, x.image, x.info, x.description, x.type)
                    var MovieALL = addMovieALLInfo(MovieALLtmp)
                    try {
                        movieAllMapper.insert(MovieALL)
                    } catch (e: org.springframework.dao.DuplicateKeyException) {
                        println("Duplicate key : " + x.name)
                    }
                }
            }
            recordCount++
        }

        //清空之前 : 将高分热门电影的推荐数据更新到总表--->高分热门电影必定已在总表记录(具体见下面第三部分)
        var movieOld = movieMapper.selectAll()
        if (movieOld.size != 0) {
            for (x in movieOld) {
                if (x.ranks >= 7.5) {
                    var tmp: String = x.name + x.ranks.toString()
                    var MovieAllTmp = movieAllMapper.selectList(QueryWrapper<MovieAll>().eq("type", tmp))
                    if (MovieAllTmp.size == 1) {
                        MovieAllTmp[0].crawltime = "old"
                        MovieAllTmp[0].recommendtotal = x.recommendtotal
                        MovieAllTmp[0].unrecommendtotal = x.unrecommendtotal
                        MovieAllTmp[0].intj = x.intj
                        MovieAllTmp[0].intp = x.intp
                        MovieAllTmp[0].entj = x.entj
                        MovieAllTmp[0].entp = x.entp
                        MovieAllTmp[0].infj = x.infj
                        MovieAllTmp[0].infp = x.infp
                        MovieAllTmp[0].enfj = x.enfj
                        MovieAllTmp[0].enfp = x.enfp
                        MovieAllTmp[0].istj = x.istj
                        MovieAllTmp[0].isfj = x.isfj
                        MovieAllTmp[0].estj = x.estj
                        MovieAllTmp[0].esfj = x.esfj
                        MovieAllTmp[0].istp = x.istp
                        MovieAllTmp[0].isfp = x.isfp
                        MovieAllTmp[0].estp = x.estp
                        MovieAllTmp[0].esfp = x.esfp
                        MovieAllTmp[0].unintj = x.unintj
                        MovieAllTmp[0].unintp = x.unintp
                        MovieAllTmp[0].unentj = x.unentj
                        MovieAllTmp[0].unentp = x.unentp
                        MovieAllTmp[0].uninfj = x.uninfj
                        MovieAllTmp[0].uninfp = x.uninfp
                        MovieAllTmp[0].unenfj = x.unenfj
                        MovieAllTmp[0].unenfp = x.unenfp
                        MovieAllTmp[0].unistj = x.unistj
                        MovieAllTmp[0].unisfj = x.unisfj
                        MovieAllTmp[0].unestj = x.unestj
                        MovieAllTmp[0].unesfj = x.unesfj
                        MovieAllTmp[0].unistp = x.unistp
                        MovieAllTmp[0].unisfp = x.unisfp
                        MovieAllTmp[0].unestp = x.unestp
                        MovieAllTmp[0].unesfp = x.unesfp

                        movieAllMapper.updateById(MovieAllTmp[0])

                    }
                }
            }
        } else if (movieOld.size == 0) {
            println("${LocalDateTime.now()} : Moiveall table Init!! This occurs only during initialization")
        }



        var resultMovieNew = MovieNetUtil.getMovies(0, 10, true, false, updateTimeNew)
        movieMapper.truncate()
        for (x in resultMovieNew) {
            if (x.ranks > 7.5) {
                var MovieAllTmp = movieAllMapper.selectList(QueryWrapper<MovieAll>().eq("name", x.name))
                if (MovieAllTmp.size == 0) {
                    var MovieAllNewTmp =
                        MovieAll(x.name, "old", x.ranks, x.detailpage, x.image, x.info, x.description, x.type)
                    var MovieAllNew = addMovieALLInfo(MovieAllNewTmp)
                    try {
                        movieAllMapper.insert(MovieAllNew)
                    } catch (e: org.springframework.dao.DuplicateKeyException) {
                        println("Duplicate key: insert new high movie into movieall table ")
                    }
                } else if (MovieAllTmp.size == 1) {
                    println("${LocalDateTime.now()} : movieall table has this novel: ${x.name} ")

                    MovieAllTmp[0].crawltime = "old"
                    movieAllMapper.updateById(MovieAllTmp[0])

                    x.recommendtotal = MovieAllTmp[0].recommendtotal
                    x.unrecommendtotal = MovieAllTmp[0].unrecommendtotal
                    x.intj = MovieAllTmp[0].intj
                    x.intp = MovieAllTmp[0].intp
                    x.entj = MovieAllTmp[0].entj
                    x.entp = MovieAllTmp[0].entp
                    x.infj = MovieAllTmp[0].infj
                    x.infp = MovieAllTmp[0].infp
                    x.enfj = MovieAllTmp[0].enfj
                    x.enfp = MovieAllTmp[0].enfp
                    x.istj = MovieAllTmp[0].istj
                    x.isfj = MovieAllTmp[0].isfj
                    x.estj = MovieAllTmp[0].estj
                    x.esfj = MovieAllTmp[0].esfj
                    x.istp = MovieAllTmp[0].istp
                    x.isfp = MovieAllTmp[0].isfp
                    x.estp = MovieAllTmp[0].estp
                    x.esfp = MovieAllTmp[0].esfp
                    x.unintj = MovieAllTmp[0].unintj
                    x.unintp = MovieAllTmp[0].unintp
                    x.unentj = MovieAllTmp[0].unentj
                    x.unentp = MovieAllTmp[0].unentp
                    x.uninfj = MovieAllTmp[0].uninfj
                    x.uninfp = MovieAllTmp[0].uninfp
                    x.unenfj = MovieAllTmp[0].unenfj
                    x.unenfp = MovieAllTmp[0].unenfp
                    x.unistj = MovieAllTmp[0].unistj
                    x.unisfj = MovieAllTmp[0].unisfj
                    x.unestj = MovieAllTmp[0].unestj
                    x.unesfj = MovieAllTmp[0].unesfj
                    x.unistp = MovieAllTmp[0].unistp
                    x.unisfp = MovieAllTmp[0].unisfp
                    x.unestp = MovieAllTmp[0].unestp
                    x.unesfp = MovieAllTmp[0].unesfp
                }
            }
            try {
                var movieNew = addMovieInfo(x)
                movieMapper.insert(movieNew)
            } catch (e: org.springframework.dao.DuplicateKeyException) {
                println("Duplicate key: update movie table")
            }
        }
        println("${LocalDateTime.now()} : movie list has been completed !")
    }


    //评论数随机赋值
    fun addMovieInfo(x: Movie): Movie {
        var y: Int = x.ranks.toInt()

        x.intj = y
        x.intp = y
        x.entj = y
        x.entp = y
        x.infj = y
        x.infp = y
        x.enfj = y
        x.enfp = y
        x.istj = y
        x.isfj = y
        x.estj = y
        x.esfj = y
        x.istp = y
        x.isfp = y
        x.estp = y
        x.esfp = y
        x.unintj = 10 - y
        x.unintp = 10 - y
        x.unentj = 10 - y
        x.unentp = 10 - y
        x.uninfj = 10 - y
        x.uninfp = 10 - y
        x.unenfj = 10 - y
        x.unenfp = 10 - y
        x.unistj = 10 - y
        x.unisfj = 10 - y
        x.unestj = 10 - y
        x.unesfj = 10 - y
        x.unistp = 10 - y
        x.unisfp = 10 - y
        x.unestp = 10 - y
        x.unesfp = 10 - y
        x.recommendtotal = 16 * y
        x.unrecommendtotal = 16 * (10 - y)
        return x
    }

    fun addMovieALLInfo(x: MovieAll): MovieAll {
        var y: Int = x.ranks.toInt()
        x.recommendtotal = 16 * y
        x.unrecommendtotal = 16 * (10 - y)
        x.intj = y
        x.intp = y
        x.entj = y
        x.entp = y
        x.infj = y
        x.infp = y
        x.enfj = y
        x.enfp = y
        x.istj = y
        x.isfj = y
        x.estj = y
        x.esfj = y
        x.istp = y
        x.isfp = y
        x.estp = y
        x.esfp = y
        x.unintj = 10 - y
        x.unintp = 10 - y
        x.unentj = 10 - y
        x.unentp = 10 - y
        x.uninfj = 10 - y
        x.uninfp = 10 - y
        x.unenfj = 10 - y
        x.unenfp = 10 - y
        x.unistj = 10 - y
        x.unisfj = 10 - y
        x.unestj = 10 - y
        x.unesfj = 10 - y
        x.unistp = 10 - y
        x.unisfp = 10 - y
        x.unestp = 10 - y
        x.unesfp = 10 - y
        return x
    }
}