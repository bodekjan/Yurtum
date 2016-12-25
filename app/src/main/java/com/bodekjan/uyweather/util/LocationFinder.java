package com.bodekjan.uyweather.util;

import android.content.Context;
import android.util.Log;

import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.model.GlobalCity;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by bodekjan on 2016/9/7.
 */
public class LocationFinder {
    public static String getIp() {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        String httpUrl = CommonHelper.checkIp;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
            }
            reader.close();
            result = sbf.toString();
            JSONTokener jsonTokener = new JSONTokener(result);
            JSONObject jsonObject=(JSONObject)jsonTokener.nextValue();
            result=jsonObject.getString("ip");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public static GlobalCity getCity(String httpArg, Context context) {
        GlobalCity globalCity=new GlobalCity();
        httpArg="ip="+httpArg;
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        String httpUrl = CommonHelper.ipFindCity + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey","742f1f61e7d1f794e64bd23959420c87");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
            if(result.indexOf("DOCTYPE")>-1){
                Log.e("EEE","这个ip查找不靠谱哈!");
                globalCity.zhName="err";
                return globalCity;
            }
            JSONTokener jsonTokener = new JSONTokener(result);
            JSONObject jsonObject=(JSONObject)jsonTokener.nextValue();
            if(!jsonObject.getString("errNum").equals("0")){
                globalCity.zhName="err";
                return globalCity;
            }
            JSONObject cityBody=jsonObject.getJSONObject("retData");
            result=cityBody.getString("city");
            globalCity.zhName=result;
        } catch (Exception e) {
            globalCity.zhName="err";
            return globalCity;
        }
        globalCity.zhName=getActualyCity(globalCity.zhName);
        globalCity.zhName=CommonHelper.cityCleaner(globalCity.zhName);
        globalCity.uyName= CommonHelper.translate(globalCity.zhName);
        if(globalCity.zhName.equals(globalCity.uyName)){
            globalCity.uyName= "";
        }
        globalCity=CommonHelper.findErrorCity(globalCity,context);
        if(globalCity.uyName.length()>15){
            globalCity.uyName= "";
        }
        Log.e("AAAA",globalCity.zhName);
        return globalCity;
    }
    private static String getActualyCity(String city){
        switch (city){
            case "巴音郭楞蒙古自治州":
                city="库尔勒";
                break;
            case "伊犁哈萨克自治州":
                city="伊宁";
                break;
            case "博尔塔拉蒙古自治州":
                city="博乐";
                break;
            case "昌吉回族自治州":
                city="昌吉";
                break;
            case "克孜勒苏柯尔克孜自治州":
                city="阿图什";
                break;
        }
        return city;
    }
}
