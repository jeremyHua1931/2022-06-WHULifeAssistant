package org.rainark.whuassist.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.rainark.whuassist.entity.Movie;
import org.rainark.whuassist.entity.TV;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Legacy class for fetching movie info from DouBan.
 *
 * @author Hua Zhangzhao
 * Dependencies: HTTPComponents(HTTPClient), JSON-java.
 */
public class TVNetUtil {
    private static final String CHROME_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36";
    private static final String INDEX_REQUEST_URL = "https://movie.douban.com/j/search_subjects?type=tv&tag=%E7%83%AD%E9%97%A8";
    private static final String PAGE_OFFSET = "page_start";
    private static final String PAGE_MAX = "page_limit";

    /**
     * Get the recommend movie list through movie.douban.com.
     * It's not recommended to fetch lots of movies at once,
     * or this method will be blocking for a long time.
     *
     * @param offset        Starting from the given movie number.
     * @param len           The numbers of movie that will be in the returned list.
     * @param resolveDetail Whether to include detail information of a movie,
     *                      detail information requires extra http requests.
     */

    public static ArrayList<TV> getTVs(int offset, int len, boolean resolveDetail) throws IOException {

        URL request = new URL(INDEX_REQUEST_URL + "&" + PAGE_OFFSET + "=" + offset + "&" + PAGE_MAX + "=" + (offset + len * 2));
//        System.out.println(request);
        HttpURLConnection connection = (HttpURLConnection) request.openConnection();
        connection.setRequestMethod("GET");
        connection.addRequestProperty("User-Agent", CHROME_AGENT);
        if (connection.getResponseCode() != 200)
            throw new IOException("Failed to connect to target website:" + connection.getResponseCode());
        String responseJson = inputStreamToString(connection.getInputStream());
        JSONObject obj = JSON.parseObject(responseJson);
        JSONArray list = obj.getJSONArray("subjects");
        ArrayList<TV> result = new ArrayList<>();

        JSONObject tobject;
        int nullCount = 0;
        int flag = 0;
        int i = -1;
        do {
            i = i + 1;
            flag = 0;
            for (; i < len + nullCount; i++) {
                tobject = list.getJSONObject(i);
                //The '/' symbols in the response JSON is represented as '\/', so it's necessary to replace back before use.
                String detailPage = tobject.getString("url");
                String image = tobject.getString("cover");
                Double ranks = tobject.getDouble("rate");
                //The '/' in movie name will disturb poster file saving.
                String name = tobject.getString("title").replace("/", "丨");

                String[] res0 = new String[2];
                if (resolveDetail) {
                    res0 = parseDetailPage(tobject.getString("url"));
                }
                if (ranks == null) {
                    if (flag == 0) {
                        nullCount = 0;
                    }
                    flag = 1;
                    nullCount++;
                } else {
                    TV temp = new TV(name, ranks, detailPage, image, " ", " ", "TV");
                    result.add(temp);
                }
            }
        } while (nullCount != 0 && flag == 1);
        return result;
    }

    /**
     * Parse the detail page of a movie.
     * In order to get the information that is inexistent in response JSON.
     *
     * @return A Movie object containing only description and info.
     */
    public static String[] parseDetailPage(String url) throws IOException {
        URL request = new URL(url);
//        Movie result=new Movie();
        HttpURLConnection connection = (HttpURLConnection) request.openConnection();
        connection.setRequestMethod("GET");
        connection.addRequestProperty("User-Agent", CHROME_AGENT);
        if (connection.getResponseCode() != 200)
            throw new IOException("Failed to connect to target website:" + connection.getResponseCode());
        String html = inputStreamToString(connection.getInputStream());
        //Process HTML content to get desired information.
        String infoSection = html.split("<div id=\"info\">")[1]
                .split("<div id=\"interest_sectl\">")[0]
                .trim();
        String processedInfo = infoSection.replace(" ", "")
                .replaceAll("<.*?>", "")
                .trim();
        String depictSection = html.split("<div class=\"related-info\" style=\"margin-bottom:-10px;\">")[1]
                .split("<div id=\"dale_movie_subject_banner_after_intro\">")[0]
                .trim();
        String processedDepict = depictSection
                //Sometimes '©豆瓣' will appear in this section, make sure it is deleted.
                .replace("&copy;豆瓣", "")
                //Replace Chinese space with English space, since \\s cannot recognize Chinese space.
                .replace((char) 12288, ' ')
                .replaceAll("\\s", "")
                .replaceAll("<.*?>", "")
                //Remove depict header: xxx简介
                .split("······", 2)[1]
                .trim();

        String[] tmp = new String[2];
        tmp[0] = processedInfo;
        tmp[1] = processedDepict;

        return tmp;
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
}
