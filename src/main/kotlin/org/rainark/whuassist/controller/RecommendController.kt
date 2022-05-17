package org.rainark.whuassist.controller

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.rainark.whuassist.config.JsonParam
import org.rainark.whuassist.entity.Movie
import org.rainark.whuassist.entity.Novel
import org.rainark.whuassist.exception.cascadeSuccessResponse
import org.rainark.whuassist.mapper.MovieMapper
import org.rainark.whuassist.mapper.NovelMapper
import org.rainark.whuassist.mapper.UserMapper

import org.rainark.whuassist.util.NovelNetUtil
import org.rainark.whuassist.util.UpdateCrawlResult

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RecommendController {

    @Autowired
    lateinit var updateCrawlResult: UpdateCrawlResult

    @Autowired
    lateinit var movieMapper: MovieMapper

    @Autowired
    lateinit var novelMapper: NovelMapper

    @PostMapping("/recom/refresh")
    fun crawlRefresh(@JsonParam msg:String){
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
        novelListYuepiao.addAll(novelListRecommend);
        novelListYuepiao.addAll(novelListMMYuepiao);
        novelListYuepiao.addAll(novelListMMRecommend);
        for(x in novelListYuepiao){
            print(x.ranks)
            print(" ")
            println(x.name)
        }
        println(novelListYuepiao.size)

        return cascadeSuccessResponse(novelListYuepiao)

    }

    @PostMapping("/recom/movie")
    fun crawlMovie(@JsonParam msg: String): String {
        val movieList = movieMapper.selectList(QueryWrapper<Movie>()
            .orderByAsc("ranks"));

        return cascadeSuccessResponse(movieList)


    }

}