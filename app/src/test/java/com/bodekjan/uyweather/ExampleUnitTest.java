package com.bodekjan.uyweather;

import android.util.Log;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    String httpUrl = "http://apis.baidu.com/heweather/weather/free";
    String httpArg = "city=tulufan";
    String jsonResult = request(httpUrl, httpArg);
    String xx=jsonResult+"";

    public static String request(String httpUrl, String httpArg) {
        BufferedReader readerSecound = null;
        String resultSecound = null;
        StringBuffer sbfSecound = new StringBuffer();
        String httpUrlSecound = "http://www.hawar.cn/index.shtml";
        try {
            URL urlSecound = new URL(httpUrlSecound);
            HttpURLConnection connectionSecound = (HttpURLConnection) urlSecound
                    .openConnection();
            connectionSecound.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connectionSecound.connect();
            InputStream isSecound = connectionSecound.getInputStream();
            readerSecound = new BufferedReader(new InputStreamReader(isSecound, "UTF-8"));
            String strReadSecound = null;
            while ((strReadSecound = readerSecound.readLine()) != null) {
                sbfSecound.append(strReadSecound);
                sbfSecound.append("\r\n");
            }
            readerSecound.close();
            resultSecound = sbfSecound.toString();
            int start=resultSecound.indexOf("<ul class=\"list_hat3\">");
            int end=resultSecound.indexOf("</ul></div><!--完毕 -->");
            resultSecound=resultSecound.substring(start,end);
            resultSecound=resultSecound.replace("<ul class=\"list_hat3\">","");
            String[] newsString=resultSecound.split("</li>");
            for(int i=0; i<newsString.length;i++){
                String tempItem=newsString[i];
                String link="";
                String text="";
                link=tempItem.substring(tempItem.indexOf("href")+6,tempItem.indexOf("shtml")+5);
                text=tempItem.substring(tempItem.indexOf("blank\" >"),tempItem.indexOf("</a>"));
                text=text.replace("blank\" >","");
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

}