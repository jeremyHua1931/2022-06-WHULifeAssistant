package org.rainark.whuassist.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import org.rainark.whuassist.entity.*;

/**
 * Legacy class for fetching novel info from QiDian.
 *
 * @author Hua Zhangzhao
 * Dependencies: HTTPComponents(HTTPClient), JSON-java.
 */
public class NovelNetUtil {

    private static final String CHROME_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36";

    private static final String IndexURL = "https://www.qidian.com/rank";

    private static final int onePageNovelNumber = 20;

    /**
     * Get the novel list through movie.QiDian.com.
     * It's not recommended to fetch lots of novels at once,
     * or this method will be blocking for a long time.
     * <p>
     * 榜单类型:
     * [yuepiao(月票榜),hotsales(畅销榜),readIndex(阅读指数榜),recom(推荐版),collect(收藏版) ,signnewbook(签约作者新书榜),  pubnewbook(公众作者新书榜),
     * newfans(粉丝榜),vipup(VIP作品更新榜),vipcollect(VIP作品收藏版) ,vipreward(打赏榜),newsign(新人签约新书榜),newauthor(新人作者新书榜)
     *
     * @param len         (len >0, is positive integer) The numbers of novel that will be in the returned list.
     * @param gender      ("" or "mm") if reader is a girl, the indexURL is  <a href="https://www.qidian.com/rank/mm">https://www.qidian.com/rank/mm</a>
     * @param novelChoice (榜单类型) Select the type of novel ranking list, the indexURL is <a href="https://www.qidian.com/rank/">https://www.qidian.com/rank/</a>榜单/
     * @param time        ("" or "2020" or "2019-9-8")  if the time only includes year(eg: "2021") , the rank will be the year rank
     * @return ArrayList<Novel>
     * <p>
     * eg: ArrayList<Novel> result=new ArrayList<>();
     * result=getNovels(10, "", "yuepiao","")
     * result=getNovels(10, "", "recom","")
     * result=getNovels(10, "mm", "yuepiao","")
     * result=getNovels(10, "mm", "recom","")
     */
    public static ArrayList<Novel> getNovels(int len, String gender, String novelChoice, String time) throws IOException {

        ArrayList<Novel> result = new ArrayList<>();
        String crawlType = gender + novelChoice;


        //计数
        int totalNumber = 0;

        //解析时间, null自动设为当前时间榜单
        String selectTime;
        if (!time.equals("")) {
            selectTime = spiltTime(time);
        } else {

            SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
            sdf.applyPattern("yyyy-MM-dd");// a为am/pm的标记
            Date date = new Date();// 获取当前时间
//            System.out.println("现在时间：" + sdf.format(date)); // 输出已经格式化的现在时间（24小时制）
            selectTime = spiltTime(sdf.format(date));
        }
//        System.out.println(selectTime);

        //设置需要除page以外的url
        String urlTMP;
        if (gender.equals("")) {
            urlTMP = IndexURL + "/" + novelChoice + "/" + selectTime;

        } else if (gender.equals("mm")) {
            urlTMP = IndexURL + "/mm/" + novelChoice + "/" + selectTime;
        } else {
            urlTMP = "https://www.qidian.com/rank/yuepiao/" + selectTime;
        }

        System.out.println(urlTMP);

        for (int i = 0; i < (len / onePageNovelNumber + 1); i++) {

            int count = 0;

            String finalURL;
            if (i == 0) {
                finalURL = urlTMP;
            } else {
                finalURL = urlTMP + "-page" + Integer.toString(i + 1) + "/";
            }

            URL request = new URL(finalURL);
            HttpURLConnection connection = (HttpURLConnection) request.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", CHROME_AGENT);
            if (connection.getResponseCode() != 200)
                throw new IOException("Failed to connect to target website:" + connection.getResponseCode());
            String responseJson = inputStreamToString(connection.getInputStream());

            //A list of novels on one page
            Document document1 = Jsoup.parse(responseJson);
            Elements novelList = Objects.requireNonNull(document1.getElementById("book-img-text")).getElementsByTag("li");

//            System.out.println("在 " + (i + 1) + " 页读取, URL 为 " + finalURL);
            while (totalNumber < len && count < onePageNovelNumber) {
                Element tmp = novelList.get(count);
                String name = String.valueOf(((Element) tmp).getElementsByTag("h2").text());
                String author = String.valueOf(Objects.requireNonNull(tmp.getElementsByClass("author").first()).getElementsByClass("name").text());
                String introduction = String.valueOf(tmp.getElementsByClass("intro").text());
                String update = String.valueOf(tmp.getElementsByClass("update").text()).replace("最新更新 ","");
                String completionStatus = Objects.requireNonNull(tmp.getElementsByClass("author").first()).getElementsByTag("span").text();
                String subcategory = Objects.requireNonNull(tmp.getElementsByClass("author").first()).getElementsByClass("go-sub-type").text();
                Document doucument2 = Jsoup.parse(tmp.toString());
                String url1 = Objects.requireNonNull(doucument2.select("a[href]").first()).attr("href");
                String url2 = Objects.requireNonNull(doucument2.select("img[src]").first()).attr("src");
                Document doucument3 = Jsoup.parse(Objects.requireNonNull(tmp.getElementsByClass("book-mid-info").first()).getElementsByClass("author").toString());
                String category = ((Element) doucument3).select("a[href]").get(1).text();
                String url11 = "https:" + url1;
                String url21 = "https:" + url2;

                String key0 = gender + novelChoice + Integer.toString(count + 1);
                Novel novelTmp = new Novel(key0, crawlType, count + 1, name, author, url11, url21, category, subcategory, completionStatus, update, introduction);
                result.add(novelTmp);

                totalNumber++;
                count++;

//                System.out.println(" rank: " + totalNumber);
//                System.out.println(" name: " + name);
//                System.out.println(" author:" + author);
//                System.out.println(" novelURL:" + novelURL);
//                System.out.println(" image:" + image);
//                System.out.println(" update:" + update);
//                System.out.println(" category: " + category);
//                System.out.println(" subcategory: " + subcategory);
//                System.out.println(" completionStatus: " + completionStatus);
//                System.out.println(" introduction:" + introduction);

            }
        }
//        System.out.println("一共打印 " + totalNumber + " 本小说");
        return result;
    }

    private static String inputStreamToString(InputStream is) throws IOException {
        InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
        StringBuilder builder = new StringBuilder();
        char[] cbuf = new char[128];
        int size;
        while ((size = reader.read(cbuf)) != -1)
            builder.append(cbuf, 0, size);
        return builder.toString();
    }

    public static String spiltTime(String time) {
        String[] temp;
        String symbol = "-";
        temp = time.split(symbol);
        String month0;
        if (temp.length != 1) {
            month0 = temp[1].toString();
            String month1 = String.format("%02d", Integer.valueOf(month0));
            return "year-" + temp[0].toString() + "-month" + month1;
        } else {
            month0 = "";
            return "year" + temp[0].toString();
        }
    }

    ////    test code
    public static void main(String[] args) throws IOException {
        ArrayList<Novel> result = new ArrayList<>();
        result = getNovels(10, "", "yuepiao", "");
        for (int i = 0; i < result.size(); i++) {
            System.out.println("第 " + (i + 1) + " 本小说, 名字为:  " + result.get(i).getName());
        }
        System.out.println("一共有 " + result.size() + " 本小说");
    }

}
