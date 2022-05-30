package org.rainark.whuassist.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LocationNetUtil {
    private static final String CHROME_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36";

    private static final String INDEX_REQUEST_URL = "https://restapi.amap.com/v3/geocode/geo?";
    private static final String key = "a036b65d2b7d11dcebd8179001348840";

    public static void main(String[] args) throws IOException, ParseException {
    }

    public static double[] getPosition(String address, String city) throws IOException {
        double[] result = new double[2];
        URL request = new URL(INDEX_REQUEST_URL + "address=" + address + "&city=" + city + "&key=" + key);
        HttpURLConnection connection = (HttpURLConnection) request.openConnection();
        connection.setRequestMethod("GET");
        connection.addRequestProperty("User-Agent", CHROME_AGENT);
        if (connection.getResponseCode() != 200)
            throw new IOException("Failed to connect to target website:" + connection.getResponseCode());
        String responseJson = inputStreamToString(connection.getInputStream());
        JSONObject jsonObject = JSON.parseObject(responseJson);
        JSONArray jsonArray = JSONArray.parseArray((jsonObject.getString("geocodes")));
        JSONObject obj = jsonArray.getJSONObject(0);
        System.out.println(obj.getString("formatted_address"));
        System.out.println(obj.getString("location"));
        String[] result1 = obj.getString("location").toString().split(",");
        result[0] = Double.parseDouble(result1[0]);
        result[1] = Double.parseDouble(result1[1]);
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
}
