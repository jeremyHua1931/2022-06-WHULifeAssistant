package org.rainark.whuassist.util

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.rainark.whuassist.entity.Novel
import org.rainark.whuassist.entity.NovelALL
import org.rainark.whuassist.mapper.NovelALLMapper
import org.rainark.whuassist.mapper.NovelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.sql.SQLException
import java.time.LocalDateTime


@Component
class UpdateNovel {

    @Autowired
    lateinit var novelMapper: NovelMapper

    @Autowired
    lateinit var novelALLMapper: NovelALLMapper

    companion object {
        var recordCount: Int = 0
        var updateTimeNew: String = LocalDateTime.now().toString()
        var updateTimeOld: String = LocalDateTime.now().toString()
    }

    fun updateNovel() {

        println("${LocalDateTime.now()} : Start to update novel list....")
        updateTimeNew = LocalDateTime.now().toString()


        //Part1-处理小说
        //novel 表(一共40条数据)记录最新一次热门显示的小说榜单, 每更新一次便清空, 清空前将本次推荐数更新至novelALL表, 重新新建时把最新拉取到的小说榜单信息的推荐树与总表同步
        //novelALL为总表,记录每一本小说的历史情况
        //日常使用时,总表不发生改变, 只有novel发生改变(针对推荐数的变化)

        //1-将novel表推荐数同步至总表
        var novelOld = novelMapper.selectList(QueryWrapper<Novel>().eq("crawltime", updateTimeOld))
        if (novelOld.size != 0) {
            for (x in novelOld) {
                var typeOldTmp: String = x.name + x.author
                var novelALLTest = novelALLMapper.selectList(QueryWrapper<NovelALL>().eq("type", typeOldTmp))
                if (novelALLTest.size == 1) {
                    novelALLTest[0].crawltime = x.crawltime
                    novelALLTest[0].recommendtotal = x.recommendtotal
                    novelALLTest[0].unrecommendtotal = x.unrecommendtotal
                    novelALLTest[0].intj = x.intj
                    novelALLTest[0].intp = x.intp
                    novelALLTest[0].entj = x.entj
                    novelALLTest[0].entp = x.entp
                    novelALLTest[0].infj = x.infj
                    novelALLTest[0].infp = x.infp
                    novelALLTest[0].enfj = x.enfj
                    novelALLTest[0].enfp = x.enfp
                    novelALLTest[0].istj = x.istj
                    novelALLTest[0].isfj = x.isfj
                    novelALLTest[0].estj = x.estj
                    novelALLTest[0].esfj = x.esfj
                    novelALLTest[0].istp = x.istp
                    novelALLTest[0].isfp = x.isfp
                    novelALLTest[0].estp = x.estp
                    novelALLTest[0].esfp = x.esfp
                    novelALLTest[0].unintj = x.unintj
                    novelALLTest[0].unintp = x.unintp
                    novelALLTest[0].unentj = x.unentj
                    novelALLTest[0].unentp = x.unentp
                    novelALLTest[0].uninfj = x.uninfj
                    novelALLTest[0].uninfp = x.uninfp
                    novelALLTest[0].unenfj = x.unenfj
                    novelALLTest[0].unenfp = x.unenfp
                    novelALLTest[0].unistj = x.unistj
                    novelALLTest[0].unisfj = x.unisfj
                    novelALLTest[0].unestj = x.unestj
                    novelALLTest[0].unesfj = x.unesfj
                    novelALLTest[0].unistp = x.unistp
                    novelALLTest[0].unisfp = x.unisfp
                    novelALLTest[0].unestp = x.unestp
                    novelALLTest[0].unesfp = x.unesfp
                    novelALLMapper.updateById(novelALLTest[0])
                }
            }
        } else if (novelOld.size == 0) {
            println("${LocalDateTime.now()} : Novelall table Init!! This occurs only during initialization")
        }


        //2-清空novel表,并爬取
        novelMapper.truncate()

        var resultYuepiao = ArrayList<Novel>()
        var resultRecommend = ArrayList<Novel>()
        var resultMMYuepiao = ArrayList<Novel>()
        var resultMMRecommend = ArrayList<Novel>()

        resultYuepiao = NovelNetUtil.getNovels(10, "", "yuepiao", "", updateTimeNew)
        resultRecommend = NovelNetUtil.getNovels(10, "", "recom", "", updateTimeNew)
        resultMMYuepiao = NovelNetUtil.getNovels(10, "mm", "yuepiao", "", updateTimeNew)
        resultMMRecommend = NovelNetUtil.getNovels(10, "mm", "recom", "", updateTimeNew)
        resultYuepiao.addAll(resultRecommend)
        resultYuepiao.addAll(resultMMYuepiao)
        resultYuepiao.addAll(resultMMRecommend)

        //3-novel表同步总表推荐数
        for (x in resultYuepiao) {
            val typeTmp = x.name + x.author
            val novelALLOld = novelALLMapper.selectList(
                QueryWrapper<NovelALL>().eq("type", typeTmp)
            )
            if (novelALLOld.size == 0) {
                //第一次出现将书的基本信息更新到总表
                val novelAll = NovelALL(
                    typeTmp,
                    x.crawltime,
                    x.name,
                    x.author,
                    x.novelurl,
                    x.image,
                    x.category,
                    x.subcategory,
                    x.completionstatus,
                    x.updatedchapter,
                    x.introduction
                )
                try {
                    novelALLMapper.insert(novelAll)
                } catch (e: org.springframework.dao.DuplicateKeyException) {
                    println("Duplicate key: Failed to push the novel table to the novelall table before update novel table")
                }
            } else if (novelALLOld.size == 1) {
                //如果热门表中出现了总表中的小说, 将热门表中小说的最新信息同步到总表
                println("${LocalDateTime.now()} : novelall table has this novel: ${x.name} ")
                var tmp = novelALLOld[0]
                tmp.crawltime = x.crawltime
                tmp.completionstatus = x.completionstatus
                novelALLMapper.updateById(tmp)
                //更新至novel表
                // 读取总表的推荐数
                x.recommendtotal = tmp.recommendtotal
                x.unrecommendtotal = tmp.unrecommendtotal
                x.intj = tmp.intj
                x.intp = tmp.intp
                x.entj = tmp.entj
                x.entp = tmp.entp
                x.infj = tmp.infj
                x.infp = tmp.infp
                x.enfj = tmp.enfj
                x.enfp = tmp.enfp
                x.istj = tmp.istj
                x.isfj = tmp.isfj
                x.estj = tmp.estj
                x.esfj = tmp.esfj
                x.istp = tmp.istp
                x.isfp = tmp.isfp
                x.estp = tmp.estp
                x.esfp = tmp.esfp
                x.unintj = tmp.unintj
                x.unintp = tmp.unintp
                x.unentj = tmp.unentj
                x.unentp = tmp.unentp
                x.uninfj = tmp.uninfj
                x.uninfp = tmp.uninfp
                x.unenfj = tmp.unenfj
                x.unenfp = tmp.unenfp
                x.unistj = tmp.unistj
                x.unisfj = tmp.unisfj
                x.unestj = tmp.unestj
                x.unesfj = tmp.unesfj
                x.unistp = tmp.unistp
                x.unisfp = tmp.unisfp
                x.unestp = tmp.unestp
                x.unesfp = tmp.unesfp
            }
            try {
                novelMapper.insert(x)
            } catch (e: org.springframework.dao.DuplicateKeyException) {
                println("Duplicate key: update novel table with getting some information with novelall table")
            }

        }
        updateTimeOld = updateTimeNew
        println("${LocalDateTime.now()} : novel list has beend completed !")
    }
}