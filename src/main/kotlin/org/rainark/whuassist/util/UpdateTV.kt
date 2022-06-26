package org.rainark.whuassist.util

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.rainark.whuassist.entity.Movie
import org.rainark.whuassist.entity.MovieAll
import org.rainark.whuassist.entity.TV
import org.rainark.whuassist.entity.TVAll
import org.rainark.whuassist.mapper.TVAllMapper
import org.rainark.whuassist.mapper.TVMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.sql.SQLException
import java.time.LocalDateTime

@Component
class UpdateTV {

    @Autowired
    lateinit var tvMapper: TVMapper

    @Autowired
    lateinit var tvAllMapper: TVAllMapper

    companion object {
        var recordCount: Int = 0
        var updateTimeNew: String = LocalDateTime.now().toString()
    }

    fun updateTV() {
        println("${LocalDateTime.now()} : Start to update TV list....")
        updateTimeNew = LocalDateTime.now().toString()

        var tvJudge = tvAllMapper.selectList(QueryWrapper<TVAll>().eq("crawltime", "old"))
        if (recordCount == 0 && tvJudge.size == 0) {
            println("${LocalDateTime.now()}  Start to craw  TVs  .....")
            var resultTV = TVNetUtil.getTVs(0, 20, true, "old", true)
            println("${LocalDateTime.now()}  Start to init TVALL Table....." + "       " + LocalDateTime.now())
            for (x in resultTV) {
                if (x.ranks >= 7.5) {
                    var TVALL1 =
                        TVAll(x.name, x.crawltime, x.ranks, x.detailpage, x.image, x.info, x.description, x.type)
                    var TVALL = addTVALLInfo(TVALL1)
                    try {
                        tvAllMapper.insert(TVALL)
                    } catch (e: org.springframework.dao.DuplicateKeyException) {
                        println("Duplicate key : " + x.name)
                    }
                }
            }
            recordCount++;
        }


        var resultTVOld = tvMapper.selectList(QueryWrapper<TV>().eq("crawltime", "old"))
        var resultTVOld2 = tvMapper.selectList(QueryWrapper<TV>().eq("crawltime", "new"))
        resultTVOld.addAll(resultTVOld2)
        //清空之前同步
        if (resultTVOld.size != 0) {
            for (x in resultTVOld) {
                if (x.ranks >= 7.5) {
                    var resultTVOldTmp = tvAllMapper.selectList(QueryWrapper<TVAll>().eq("name", x.name))
                    if (resultTVOldTmp.size == 1) {
                        resultTVOldTmp[0].crawltime = "old"
                        resultTVOldTmp[0].recommendtotal = x.recommendtotal
                        resultTVOldTmp[0].unrecommendtotal = x.unrecommendtotal
                        resultTVOldTmp[0].intj = x.intj
                        resultTVOldTmp[0].intp = x.intp
                        resultTVOldTmp[0].entj = x.entj
                        resultTVOldTmp[0].entp = x.entp
                        resultTVOldTmp[0].infj = x.infj
                        resultTVOldTmp[0].infp = x.infp
                        resultTVOldTmp[0].enfj = x.enfj
                        resultTVOldTmp[0].enfp = x.enfp
                        resultTVOldTmp[0].istj = x.istj
                        resultTVOldTmp[0].isfj = x.isfj
                        resultTVOldTmp[0].estj = x.estj
                        resultTVOldTmp[0].esfj = x.esfj
                        resultTVOldTmp[0].istp = x.istp
                        resultTVOldTmp[0].isfp = x.isfp
                        resultTVOldTmp[0].estp = x.estp
                        resultTVOldTmp[0].esfp = x.esfp
                        resultTVOldTmp[0].unintj = x.unintj
                        resultTVOldTmp[0].unintp = x.unintp
                        resultTVOldTmp[0].unentj = x.unentj
                        resultTVOldTmp[0].unentp = x.unentp
                        resultTVOldTmp[0].uninfj = x.uninfj
                        resultTVOldTmp[0].uninfp = x.uninfp
                        resultTVOldTmp[0].unenfj = x.unenfj
                        resultTVOldTmp[0].unenfp = x.unenfp
                        resultTVOldTmp[0].unistj = x.unistj
                        resultTVOldTmp[0].unisfj = x.unisfj
                        resultTVOldTmp[0].unestj = x.unestj
                        resultTVOldTmp[0].unesfj = x.unesfj
                        resultTVOldTmp[0].unistp = x.unistp
                        resultTVOldTmp[0].unisfp = x.unisfp
                        resultTVOldTmp[0].unestp = x.unestp
                        resultTVOldTmp[0].unesfp = x.unesfp
                        tvAllMapper.updateById(resultTVOldTmp[0])
                    }
                }
            }
        } else if (resultTVOld.size == 0) {
            println("${LocalDateTime.now()} : TVall table Init!! This occurs only during initialization")
        }

        tvMapper.truncate()

        var resultTVNew = TVNetUtil.getTVs(0, 10, true, "new", false)

        for (x in resultTVNew) {
            if (x.ranks >= 7.5) {
                var resultTVAllTmp = tvAllMapper.selectList(QueryWrapper<TVAll>().eq("name", x.name))
                if (resultTVAllTmp.size == 0) {
                    var resultTVAllTMP1 =
                        TVAll(x.name, "old", x.ranks, x.detailpage, x.image, x.info, x.description, x.type)

                    var resultTVAllTMP = addTVALLInfo(resultTVAllTMP1)
                    try {
                        tvAllMapper.insert(resultTVAllTMP)
                    } catch (e: org.springframework.dao.DuplicateKeyException) {
                        println("Duplicate key")
                    }

                } else if (resultTVAllTmp.size == 1) {
                    println("${LocalDateTime.now()} : tvall table has this tv: ${x.name} ")

                    resultTVAllTmp[0].crawltime = "old"
                    tvAllMapper.updateById(resultTVAllTmp[0])

                    x.recommendtotal = resultTVAllTmp[0].recommendtotal
                    x.unrecommendtotal = resultTVAllTmp[0].unrecommendtotal
                    x.intj = resultTVAllTmp[0].intj
                    x.intp = resultTVAllTmp[0].intp
                    x.entj = resultTVAllTmp[0].entj
                    x.entp = resultTVAllTmp[0].entp
                    x.infj = resultTVAllTmp[0].infj
                    x.infp = resultTVAllTmp[0].infp
                    x.enfj = resultTVAllTmp[0].enfj
                    x.enfp = resultTVAllTmp[0].enfp
                    x.istj = resultTVAllTmp[0].istj
                    x.isfj = resultTVAllTmp[0].isfj
                    x.estj = resultTVAllTmp[0].estj
                    x.esfj = resultTVAllTmp[0].esfj
                    x.istp = resultTVAllTmp[0].istp
                    x.isfp = resultTVAllTmp[0].isfp
                    x.estp = resultTVAllTmp[0].estp
                    x.esfp = resultTVAllTmp[0].esfp
                    x.unintj = resultTVAllTmp[0].unintj
                    x.unintp = resultTVAllTmp[0].unintp
                    x.unentj = resultTVAllTmp[0].unentj
                    x.unentp = resultTVAllTmp[0].unentp
                    x.uninfj = resultTVAllTmp[0].uninfj
                    x.uninfp = resultTVAllTmp[0].uninfp
                    x.unenfj = resultTVAllTmp[0].unenfj
                    x.unenfp = resultTVAllTmp[0].unenfp
                    x.unistj = resultTVAllTmp[0].unistj
                    x.unisfj = resultTVAllTmp[0].unisfj
                    x.unestj = resultTVAllTmp[0].unestj
                    x.unesfj = resultTVAllTmp[0].unesfj
                    x.unistp = resultTVAllTmp[0].unistp
                    x.unisfp = resultTVAllTmp[0].unisfp
                    x.unestp = resultTVAllTmp[0].unestp
                    x.unesfp = resultTVAllTmp[0].unesfp
                }
            }
            try {
                var TVNew = addTVInfo(x)
                tvMapper.insert(TVNew)
            } catch (e: org.springframework.dao.DuplicateKeyException) {
                println("Duplicate key: update tv table")
            }

        }
        println("${LocalDateTime.now()} : TV list has beend completed !")

    }


    fun addTVInfo(x: TV): TV {
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

    fun addTVALLInfo(x: TVAll): TVAll {
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