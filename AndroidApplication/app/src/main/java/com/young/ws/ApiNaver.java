package com.young.ws;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ApiNaver {
    static public String clientId = "KEY";// 애플리케이션 클라이언트 아이디값";
    static public String clientSecret = "KEY";// 애플리케이션 클라이언트 시크릿값";\
    public static String main(String movieName) {
        int display = 1; // 검색결과갯수. 최대100개

        StringBuilder sb = null;
        BufferedReader br=null;
        String link = null;
        try {
            String text = URLEncoder.encode(movieName, "utf-8");
            String apiURL = "https://openapi.naver.com/v1/search/movie.json?query=" + text + "&display=" + display ;
            Log.d(Contants.LOG_TAG,apiURL);

            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("X-Naver-Client-Id", clientId);
            requestHeaders.put("X-Naver-Client-Secret", clientSecret);
            String responseBody = get(apiURL,requestHeaders);

            link = parseData(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return link;
    }
    private static String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }

        } catch (IOException e) {
            Log.d(Contants.LOG_TAG,"API 요청과 응답 실패");
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            Log.d(Contants.LOG_TAG,"API URL이 잘못되었습니다.");
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            Log.d(Contants.LOG_TAG,"연결이 실패했습니다.");
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            Log.d(Contants.LOG_TAG,"API 응답을 읽는데 실패했습니다.");
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

    private static String parseData(String responseBody) {
        String link=null;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(responseBody.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                link = item.getString("link");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return link;

    }
}
