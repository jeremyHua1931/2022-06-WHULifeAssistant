package org.rainark.whuassist.controller

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.rainark.whuassist.config.JsonParam
import org.rainark.whuassist.entity.Movie
import org.rainark.whuassist.entity.Novel
import org.rainark.whuassist.entity.TV
import org.rainark.whuassist.exception.cascadeSuccessResponse
import org.rainark.whuassist.mapper.MovieMapper
import org.rainark.whuassist.mapper.NovelMapper
import org.rainark.whuassist.mapper.TVMapper
import org.rainark.whuassist.mapper.UserMapper

import org.rainark.whuassist.util.NovelNetUtil
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
    lateinit var TVMapper: TVMapper

    @PostMapping("/recom/refresh")
    fun crawlRefresh(@JsonParam msg: String) {
        updateCrawlResult.updateCrawlToMySQL()
    }

    @PostMapping("/recom/novel")
    fun crawlNovel(@JsonParam msg: String): String {

        val novelListYuepiao = novelMapper.selectList(
            QueryWrapper<Novel>()
                .orderByAsc("ranks")
                .eq("choice", "yuepiao")
        )
        val novelListRecommend = novelMapper.selectList(
            QueryWrapper<Novel>()
                .orderByAsc("ranks")
                .eq("choice", "recom")
        )
        val novelListMMYuepiao = novelMapper.selectList(
            QueryWrapper<Novel>()
                .orderByAsc("ranks")
                .eq("choice", "mmyuepiao")
        )
        val novelListMMRecommend = novelMapper.selectList(
            QueryWrapper<Novel>()
                .orderByAsc("ranks")
                .eq("choice", "mmrecom")
        )
        novelListYuepiao.addAll(novelListRecommend)
        novelListYuepiao.addAll(novelListMMYuepiao)
        novelListYuepiao.addAll(novelListMMRecommend)
        return cascadeSuccessResponse(novelListYuepiao)
    }

    @PostMapping("/recom/movie")
    fun crawlMovie(@JsonParam msg: String): String {
        val movieList = movieMapper.selectList(
            QueryWrapper<Movie>()
                .orderByDesc("ranks")
        );
        return cascadeSuccessResponse(movieList)
    }

    @PostMapping("/recom/tv")
    fun crawlTV(@JsonParam msg: String): String {
        val tvList = TVMapper.selectList(
            QueryWrapper<TV>()
                .orderByDesc("ranks")
        );
        return cascadeSuccessResponse(tvList)
    }

}