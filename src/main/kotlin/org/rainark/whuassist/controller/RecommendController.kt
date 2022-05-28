package org.rainark.whuassist.controller

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.rainark.whuassist.config.JsonParam
import org.rainark.whuassist.entity.*
import org.rainark.whuassist.exception.cascadeSuccessResponse
import org.rainark.whuassist.mapper.*

import org.rainark.whuassist.util.UpdateCrawlResult

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Legacy class for handling the interaction of front-end recommendation pages
 * including four types of novel recommended list: crawlNovel()
 *           hot movie recommended list: crawlMovie()
 *           hot tv recommended list: crawlTV()
 * Also including some test controllers:
 *           update recommend lists in database (MySQL): crawlRefresh()
 *
 * @author Hua ZhangZhao
 */

@RestController
class RecommendController {

    @Autowired
    lateinit var updateCrawlResult: UpdateCrawlResult

    @Autowired
    lateinit var movieMapper: MovieMapper

    @Autowired
    lateinit var novelMapper: NovelMapper

    @Autowired
    lateinit var tvMapper: TVMapper

    @Autowired
    lateinit var novelAttitudeMapper: NovelAttitudeMapper

    @Autowired
    lateinit var movieAttitudeMapper: MovieAttitudeMapper

    @Autowired
    lateinit var tvAttitudeMapper: TVAttitudeMapper

    @Autowired
    lateinit var userMapper: UserMapper

    @Autowired
    lateinit var novelALLMapper: NovelALLMapper

    @Autowired
    lateinit var movieAllMapper: MovieAllMapper

    @PostMapping("/recom/refresh")
    fun crawlRefresh(@JsonParam msg: String): String {
        updateCrawlResult.updateCrawlToMySQL()
        return "success"
    }

    @PostMapping("/recom/novel")
    fun crawlNovel(
        @JsonParam msg: String,
        @JsonParam wechatid: String
    ): String {
//        println("Test : ${wechatid}")
        val novelListYuepiao = novelMapper.selectList(QueryWrapper<Novel>().orderByAsc("ranks").eq("choice", "yuepiao"))
        val novelListRecommend = novelMapper.selectList(QueryWrapper<Novel>().orderByAsc("ranks").eq("choice", "recom"))
        val novelListMMYuepiao =
            novelMapper.selectList(QueryWrapper<Novel>().orderByAsc("ranks").eq("choice", "mmyuepiao"))
        val novelListMMRecommend =
            novelMapper.selectList(QueryWrapper<Novel>().orderByAsc("ranks").eq("choice", "mmrecom"))

        novelListYuepiao.addAll(novelListRecommend)
        novelListYuepiao.addAll(novelListMMYuepiao)
        novelListYuepiao.addAll(novelListMMRecommend)

        val UserAttitude = novelAttitudeMapper.selectList(QueryWrapper<NovelAttitude>().eq("wechatid", wechatid))
        for (x in novelListYuepiao) {
            for (y in UserAttitude) {
                if (y.name == x.name && y.author == x.author) {
                    x.myattitude = y.attitude
                }
            }
        }
        return cascadeSuccessResponse(novelListYuepiao)
    }

    @PostMapping("/recom/movie")
    fun crawlMovie(
        @JsonParam msg: String,
        @JsonParam wechatid: String
    ): String {
        var movieList = movieMapper.selectList(QueryWrapper<Movie>().orderByDesc("ranks").eq("crawltime", "hot"));
        var movieAllList = movieAllMapper.selectList(QueryWrapper<MovieAll>().last("ORDER BY RAND() LIMIT 10"));
        val UserAttitude = movieAttitudeMapper.selectList(QueryWrapper<MovieAttitude>().eq("wechatid", wechatid))
        movieAllList.sortWith(Comparator { movie, t1 ->
            +
            if (movie.ranks >= t1.ranks) -1 else {
                1
            }
        })
        for (x in movieAllList) {
            movieList.add(movieTrans(x))
            try {
                movieMapper.insert(movieTrans(x))
            } catch (e: org.springframework.dao.DuplicateKeyException) {
                println("movie table already has this novel : ${x.name}")
            }
        }
        for (x in movieList) {
            for (y in UserAttitude) {
                if (y.name == x.name && y.ranks == x.ranks) {
                    x.myattitude = y.attitude
                }
            }
        }
        return cascadeSuccessResponse(movieList)
    }

    @PostMapping("/recom/tv")
    fun crawlTV(
        @JsonParam msg: String,
        @JsonParam wechatId: String
    ): String {
        val tvList = tvMapper.selectList(QueryWrapper<TV>().orderByDesc("ranks"));
        val UserAttitude = tvAttitudeMapper.selectList(QueryWrapper<TVAttitude>().eq("wechatid", wechatId))
        for (x in tvList) {
            for (y in UserAttitude) {
                if (y.name == x.name && y.ranks == x.ranks) {
                    x.myattitude = y.attitude
                }
            }
        }
        return cascadeSuccessResponse(tvList)
    }

    @PostMapping("/recom/attitude/novel")
    fun NovelEdit(
        @JsonParam wechatid: String,
        @JsonParam personality: String,
        @JsonParam name: String,
        @JsonParam author: String,
        @JsonParam attitude: Int
    ): String {
        // var user=userMapper.selectList(QueryWrapper<User>().eq("wechat_id",wechatId))
        // var person=editNovelAttitude(x,user[0].personality)
        if (attitude != 0 && attitude != 1 && attitude != -1) {
            return "Attitude sign is wrong"
        }
        var novelList = novelMapper.selectList(QueryWrapper<Novel>().eq("name", name).eq("author", author))
        val UserAttitude = novelAttitudeMapper.selectList(
            QueryWrapper<NovelAttitude>().eq("wechatid", wechatid).eq("name", name).eq("author", author)
        )
        if (UserAttitude.size == 1) {
            println("修改时已发表过态度,1->-1,-1->1,-1->0,1->0 ")
            for (x in novelList) {
                //只处理前后态度发生变化
                if (attitude == 1 && UserAttitude[0].attitude == -1) {
                    x.unrecommendtotal--
                    x.recommendtotal++
                    var result1 = editNovelAttitude(x, personality, attitude)
                    var result = editNovelAttitude(result1, getOppositePersonality(personality), attitude * (-1))
                    novelMapper.updateById(result)
                } else if (attitude == -1 && UserAttitude[0].attitude == 1) {
                    x.unrecommendtotal++
                    x.recommendtotal--
                    var result1 = editNovelAttitude(x, personality, attitude)
                    var result = editNovelAttitude(result1, getOppositePersonality(personality), attitude * (-1))
                    novelMapper.updateById(result)
                } else if (attitude == 0 && UserAttitude[0].attitude == 1) {
                    x.recommendtotal--
                    var result = editNovelAttitude(x, personality, -1)
                    novelMapper.updateById(result)
                } else if (attitude == 0 && UserAttitude[0].attitude == -1) {
                    x.unrecommendtotal--
                    var result = editNovelAttitude(x, getOppositePersonality(personality), -1)
                    novelMapper.updateById(result)
                } else if (attitude == 1 && UserAttitude[0].attitude == 1) {
                    return "Nothing to edit"
                } else if (attitude == -1 && UserAttitude[0].attitude == -1) {
                    return "Nothing to edit"
                }
            }
        } else {
            if (attitude == 0)
                return "Nothing to edit"
            println("开始发表态度, 0->1, 0->-1")
            for (x in novelList) {
                if (attitude == 1) {
                    x.recommendtotal++
                    var result = editNovelAttitude(x, personality, 1)
                    novelMapper.updateById(result)
                } else if (attitude == -1) {
                    x.unrecommendtotal++
                    var result = editNovelAttitude(x, getOppositePersonality(personality), 1)
                    novelMapper.updateById(result)
                }
            }
        }
        //更改个人对于此小说的态度
        var attitudeNovel = NovelAttitude(wechatid, name, author, attitude)
        if (UserAttitude.size == 1) {
            novelAttitudeMapper.delete(
                QueryWrapper<NovelAttitude>().eq("wechatid", wechatid).eq("name", name).eq("author", author)
            )
        }
        if (attitude != 0) {
            novelAttitudeMapper.insert(attitudeNovel)
        }
        return "yes";
    }

    @PostMapping("/recom/attitude/movie")
    fun movieEdit(
        @JsonParam wechatid: String,
        @JsonParam personality: String,
        @JsonParam name: String,
        @JsonParam ranks: Double,
        @JsonParam attitude: Int
    ): String {
        // var user=userMapper.selectList(QueryWrapper<User>().eq("wechat_id",wechatId))
        // var person=editNovelAttitude(x,user[0].personality)

        if (attitude != 0 && attitude != 1 && attitude != -1) {
            return "Attitude sign is wrong"
        }

        var movieList = movieMapper.selectList(QueryWrapper<Movie>().eq("name", name).eq("ranks", ranks))
        val UserAttitude = movieAttitudeMapper.selectList(
            QueryWrapper<MovieAttitude>().eq("wechatid", wechatid).eq("name", name).eq("ranks", ranks)
        )

        if (UserAttitude.size == 1) {
            println("修改时已发表过态度,1->-1,-1->1,-1->0,1->0 ")
            for (x in movieList) {
                if (attitude == 1 && UserAttitude[0].attitude == -1) {
                    x.unrecommendtotal--
                    x.recommendtotal++
                    var result1 = editMovieAttitude(x, personality, attitude)
                    var result = editMovieAttitude(result1, getOppositePersonality(personality), attitude * (-1))
                    movieMapper.updateById(result)
                } else if (attitude == -1 && UserAttitude[0].attitude == 1) {
                    x.unrecommendtotal++
                    x.recommendtotal--
                    var result1 = editMovieAttitude(x, personality, attitude)
                    var result = editMovieAttitude(result1, getOppositePersonality(personality), attitude * (-1))
                    movieMapper.updateById(result)
                } else if (attitude == 0 && UserAttitude[0].attitude == 1) {
                    x.recommendtotal--
                    var result = editMovieAttitude(x, personality, -1)
                    movieMapper.updateById(result)
                } else if (attitude == 0 && UserAttitude[0].attitude == -1) {
                    x.unrecommendtotal--
                    var result = editMovieAttitude(x, getOppositePersonality(personality), -1)
                    movieMapper.updateById(result)
                } else if (attitude == 1 && UserAttitude[0].attitude == 1) {
                    return "Nothing to edit"
                } else if (attitude == -1 && UserAttitude[0].attitude == -1) {
                    return "Nothing to edit"
                }
            }
        } else {
            if (attitude == 0)
                return "Nothing to edit"
            println("开始发表态度, 0->1, 0->-1")
            for (x in movieList) {
                if (attitude == 1) {
                    x.recommendtotal++
                    var result = editMovieAttitude(x, personality, 1)
                    movieMapper.updateById(result)
                } else if (attitude == -1) {
                    x.unrecommendtotal++
                    var result = editMovieAttitude(x, getOppositePersonality(personality), 1)
                    movieMapper.updateById(result)
                }
            }

        }

        //更改个人对于此电影的态度
        var attitudeMovie = MovieAttitude(wechatid, name, ranks, attitude)
        if (UserAttitude.size == 1) {
            movieAttitudeMapper.delete(
                QueryWrapper<MovieAttitude>().eq("wechatid", wechatid).eq("name", name).eq("ranks", ranks)
            )
        }
        if (attitude != 0) {
            movieAttitudeMapper.insert(attitudeMovie)
        }
        return "yes"
    }

    @PostMapping("/recom/attitude/tv")
    fun tvEdit(
        @JsonParam wechatid: String,
        @JsonParam personality: String,
        @JsonParam name: String,
        @JsonParam ranks: Double,
        @JsonParam attitude: Int
    ): String {
        // var user=userMapper.selectList(QueryWrapper<User>().eq("wechat_id",wechatId))
        // var person=editNovelAttitude(x,user[0].personality)
        if (attitude != 0 && attitude != 1 && attitude != -1) {
            return "Attitude sign is wrong"
        }

        var tvList = tvMapper.selectList(QueryWrapper<TV>().eq("name", name).eq("ranks", ranks))
        val UserAttitude = tvAttitudeMapper.selectList(
            QueryWrapper<TVAttitude>().eq("wechatid", wechatid).eq("name", name).eq("ranks", ranks)
        )

        if (UserAttitude.size == 1) {
            println("修改时已发表过态度,1->-1,-1->1,-1->0,1->0 ")
            for (x in tvList) {
                if (attitude == 1 && UserAttitude[0].attitude == -1) {
                    x.unrecommendtotal--
                    x.recommendtotal++
                    var result1 = editTVAttitude(x, personality, attitude)
                    var result = editTVAttitude(result1, getOppositePersonality(personality), attitude * (-1))
                    tvMapper.updateById(result)
                } else if (attitude == -1 && UserAttitude[0].attitude == 1) {
                    x.unrecommendtotal++
                    x.recommendtotal--
                    var result1 = editTVAttitude(x, personality, attitude)
                    var result = editTVAttitude(result1, getOppositePersonality(personality), attitude * (-1))
                    tvMapper.updateById(result)
                } else if (attitude == 0 && UserAttitude[0].attitude == 1) {
                    x.recommendtotal--
                    var result = editTVAttitude(x, personality, -1)
                    tvMapper.updateById(result)
                } else if (attitude == 0 && UserAttitude[0].attitude == -1) {
                    x.unrecommendtotal--
                    var result = editTVAttitude(x, getOppositePersonality(personality), -1)
                    tvMapper.updateById(result)
                } else if (attitude == 1 && UserAttitude[0].attitude == 1) {
                    return "Nothing to edit"
                } else if (attitude == -1 && UserAttitude[0].attitude == -1) {
                    return "Nothing to edit"
                }
            }
        } else {
            if (attitude == 0)
                return "Nothing to edit"
            println("开始发表态度, 0->1, 0->-1")
            for (x in tvList) {
                if (attitude == 1) {
                    x.recommendtotal++
                    var result = editTVAttitude(x, personality, 1)
                    tvMapper.updateById(result)
                } else if (attitude == -1) {
                    x.unrecommendtotal++
                    var result = editTVAttitude(x, getOppositePersonality(personality), 1)
                    tvMapper.updateById(result)
                }
            }

        }
        //更改个人对于此电视剧的态度
        var attitudeTV = TVAttitude(wechatid, name, ranks, attitude)
        if (UserAttitude.size == 1) {
            tvAttitudeMapper.delete(
                QueryWrapper<TVAttitude>().eq("wechatid", wechatid).eq("name", name).eq("ranks", ranks)
            )
        }
        if (attitude != 0) {
            tvAttitudeMapper.insert(attitudeTV)
        }
        return "yes"
    }


    fun getOppositePersonality(personality: String): String {
        var result: String = ""
        when (personality) {
            "intj" -> result = "unintj"
            "intp" -> result = "unintp"
            "entj" -> result = "unentj"
            "entp" -> result = "unentp"
            "infj" -> result = "uninfj"
            "infp" -> result = "uninfp"
            "enfj" -> result = "unenfj"
            "enfp" -> result = "unenfp"
            "istj" -> result = "unistj"
            "isfj" -> result = "unisfj"
            "estj" -> result = "unestj"
            "esfj" -> result = "unesfj"
            "istp" -> result = "unistp"
            "isfp" -> result = "unisfp"
            "estp" -> result = "unestp"
            "esfp" -> result = "unesfp"
        }
        return result
    }

    fun editNovelAttitude(novel: Novel, personality: String, attitude: Int): Novel {
        when (personality) {
            "intj" -> novel.intj += attitude
            "intp" -> novel.intp += attitude
            "entj" -> novel.entj += attitude
            "entp" -> novel.entp += attitude
            "infj" -> novel.infj += attitude
            "infp" -> novel.infp += attitude
            "enfj" -> novel.enfj += attitude
            "enfp" -> novel.enfp += attitude
            "istj" -> novel.istj += attitude
            "isfj" -> novel.isfj += attitude
            "estj" -> novel.estj += attitude
            "esfj" -> novel.esfj += attitude
            "istp" -> novel.istp += attitude
            "isfp" -> novel.isfp += attitude
            "estp" -> novel.estp += attitude
            "esfp" -> novel.esfp += attitude
            "unintj" -> novel.unintj += attitude
            "unintp" -> novel.unintp += attitude
            "unentj" -> novel.unentj += attitude
            "unentp" -> novel.unentp += attitude
            "uninfj" -> novel.uninfj += attitude
            "uninfp" -> novel.uninfp += attitude
            "unenfj" -> novel.unenfj += attitude
            "unenfp" -> novel.unenfp += attitude
            "unistj" -> novel.unistj += attitude
            "unisfj" -> novel.unisfj += attitude
            "unestj" -> novel.unestj += attitude
            "unesfj" -> novel.unesfj += attitude
            "unistp" -> novel.unistp += attitude
            "unisfp" -> novel.unisfp += attitude
            "unestp" -> novel.unestp += attitude
            "unesfp" -> novel.unesfp += attitude
        }
        return novel
    }

    fun editMovieAttitude(movie: Movie, personality: String, attitude: Int): Movie {
        when (personality) {
            "intj" -> movie.intj += attitude
            "intp" -> movie.intp += attitude
            "entj" -> movie.entj += attitude
            "entp" -> movie.entp += attitude
            "infj" -> movie.infj += attitude
            "infp" -> movie.infp += attitude
            "enfj" -> movie.enfj += attitude
            "enfp" -> movie.enfp += attitude
            "istj" -> movie.istj += attitude
            "isfj" -> movie.isfj += attitude
            "estj" -> movie.estj += attitude
            "esfj" -> movie.esfj += attitude
            "istp" -> movie.istp += attitude
            "isfp" -> movie.isfp += attitude
            "estp" -> movie.estp += attitude
            "esfp" -> movie.esfp += attitude
            "unintj" -> movie.unintj += attitude
            "unintp" -> movie.unintp += attitude
            "unentj" -> movie.unentj += attitude
            "unentp" -> movie.unentp += attitude
            "uninfj" -> movie.uninfj += attitude
            "uninfp" -> movie.uninfp += attitude
            "unenfj" -> movie.unenfj += attitude
            "unenfp" -> movie.unenfp += attitude
            "unistj" -> movie.unistj += attitude
            "unisfj" -> movie.unisfj += attitude
            "unestj" -> movie.unestj += attitude
            "unesfj" -> movie.unesfj += attitude
            "unistp" -> movie.unistp += attitude
            "unisfp" -> movie.unisfp += attitude
            "unestp" -> movie.unestp += attitude
            "unesfp" -> movie.unesfp += attitude
        }
        return movie
    }

    fun editTVAttitude(tv: TV, personality: String, attitude: Int): TV {
        when (personality) {
            "intj" -> tv.intj += attitude
            "intp" -> tv.intp += attitude
            "entj" -> tv.entj += attitude
            "entp" -> tv.entp += attitude
            "infj" -> tv.infj += attitude
            "infp" -> tv.infp += attitude
            "enfj" -> tv.enfj += attitude
            "enfp" -> tv.enfp += attitude
            "istj" -> tv.istj += attitude
            "isfj" -> tv.isfj += attitude
            "estj" -> tv.estj += attitude
            "esfj" -> tv.esfj += attitude
            "istp" -> tv.istp += attitude
            "isfp" -> tv.isfp += attitude
            "estp" -> tv.estp += attitude
            "esfp" -> tv.esfp += attitude
            "unintj" -> tv.unintj += attitude
            "unintp" -> tv.unintp += attitude
            "unentj" -> tv.unentj += attitude
            "unentp" -> tv.unentp += attitude
            "uninfj" -> tv.uninfj += attitude
            "uninfp" -> tv.uninfp += attitude
            "unenfj" -> tv.unenfj += attitude
            "unenfp" -> tv.unenfp += attitude
            "unistj" -> tv.unistj += attitude
            "unisfj" -> tv.unisfj += attitude
            "unestj" -> tv.unestj += attitude
            "unesfj" -> tv.unesfj += attitude
            "unistp" -> tv.unistp += attitude
            "unisfp" -> tv.unisfp += attitude
            "unestp" -> tv.unestp += attitude
            "unesfp" -> tv.unesfp += attitude
        }
        return tv
    }

    fun movieTrans(x: MovieAll): Movie {
        var tmp = Movie(x.name, "old", x.ranks, x.detailpage, x.image, x.info, x.description, x.type)
        tmp.recommendtotal = x.recommendtotal
        tmp.unrecommendtotal = x.unrecommendtotal
        tmp.intj = x.intj
        tmp.intp = x.intp
        tmp.entj = x.entj
        tmp.entp = x.entp
        tmp.infj = x.infj
        tmp.infp = x.infp
        tmp.enfj = x.enfj
        tmp.enfp = x.enfp
        tmp.istj = x.istj
        tmp.isfj = x.isfj
        tmp.estj = x.estj
        tmp.esfj = x.esfj
        tmp.istp = x.istp
        tmp.isfp = x.isfp
        tmp.estp = x.estp
        tmp.esfp = x.esfp
        tmp.unintj = x.unintj
        tmp.unintp = x.unintp
        tmp.unentj = x.unentj
        tmp.unentp = x.unentp
        tmp.uninfj = x.uninfj
        tmp.uninfp = x.uninfp
        tmp.unenfj = x.unenfj
        tmp.unenfp = x.unenfp
        tmp.unistj = x.unistj
        tmp.unisfj = x.unisfj
        tmp.unestj = x.unestj
        tmp.unesfj = x.unesfj
        tmp.unistp = x.unistp
        tmp.unisfp = x.unisfp
        tmp.unestp = x.unestp
        tmp.unesfp = x.unesfp
        return tmp
    }

}

