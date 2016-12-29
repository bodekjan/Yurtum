package com.bodekjan.uyweather.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.blankj.utilcode.utils.NetworkUtils;
import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.model.GlobalCity;
import com.bodekjan.uyweather.model.OneDay;
import com.bodekjan.uyweather.model.OneHour;
import com.bodekjan.uyweather.model.OneNews;
import com.bodekjan.uyweather.model.OnePlace;
import com.github.promeg.pinyinhelper.Pinyin;
import com.mikepenz.iconics.IconicsDrawable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bodekjan on 2016/9/7.
 */
public class CommonHelper {
    public static final String INTERSTITIAL = "***";
    public static final String BANNER = "***";
    public static final String APP_ID = "***";
    public static final String SECRET_KEY = "***";
    public static final String appVersion="1.7";
    public static final int dbVersion=6;
    public static int serviceTime=30;
    public static int refreshTime=60;
    public static int detailTime=180;
    public static String checkIp="***";
    //public static String ipFindCity = "http://apis.baidu.com/showapi_open_bus/ip/ip"; /* 出错率很高*/
    public static String ipFindCity = "***";
    public static String checkCity = "***";
    public static String checkCitySecound = "***";
    public static String quickCity = "***";
    public static String quickCitySecound="***";
    public static String quickCityThird = "***";
    public static String quickCityWeather = "***";
    public static String dayDetail="***";
    public static String commentUrl="***";
    public static String translate="***";
    public static String quickNews = "***";
    public static int NOTIFICATION=55;
    public static String screenShotPic="***";
    /* 微信的 */
    public static String path="***";
    public static ArrayList<AppCompatActivity> activities=new ArrayList<AppCompatActivity>();
    public static String buildTransaction(String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
    /* 为了微信 */
    public static Bitmap getWechatWhite(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] colorArray = new int[w * h];
        int n = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int color = getMixtureWhite(bitmap.getPixel(j, i));
                colorArray[n++] = color;
            }
        }
        return Bitmap.createBitmap(colorArray, w, h, Bitmap.Config.ARGB_8888);
    }
    private static int getMixtureWhite(int color) {
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.rgb(getSingleMixtureWhite(red, alpha), getSingleMixtureWhite

                        (green, alpha),
                getSingleMixtureWhite(blue, alpha));
    }
    private static int getSingleMixtureWhite(int color, int alpha) {
        int newColor = color * alpha / 255 + 255 - alpha;
        return newColor > 255 ? 255 : newColor;
    }
    /* 为了微信结束 */
    public static int getPastMinutes(long time){
        if((int)(time/1000)/60<0){
            int newTime=(int)(time/1000)/60+120;
                if(newTime<0){
                    newTime=newTime-120;
                    newTime=1440+newTime;
                }
            return newTime;
        }
        return (int)(time/1000)/60;
    }
    public static OnePlace checkNewCity(OnePlace city,String path,boolean reCheck) throws UnsupportedEncodingException {
        city.cityId="--";
        String httpArg = "cityname="+ URLEncoder.encode(city.city, "utf-8");
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        String httpUrl = path + "?" + httpArg;
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey",  "***");
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
            JSONTokener jsonTokener = new JSONTokener(result);
            JSONObject jsonObject=(JSONObject)jsonTokener.nextValue();
            String errNum=jsonObject.getString("errNum");
            if(!errNum.equals("0")){
                city.cityId="--";
                return city;
            }
            JSONObject oneCity=jsonObject.getJSONObject("retData");
            if(reCheck){
                city.cityId=oneCity.getString("citycode");
            }else {
                city.cityId=oneCity.getString("cityCode");
            }
            char chs[]=city.city.toCharArray();
            if(city.cityPinyin.equals("--") || city.cityPinyin.equals("----")){
                city.cityPinyin="";
                for(int i=0;i<chs.length;i++){
                    if(chs[i]=='什'){
                        city.cityPinyin+="shi";
                    }else {
                        city.cityPinyin+=Pinyin.toPinyin(chs[i]);
                    }
                }
                city.cityPinyin=city.cityPinyin.toLowerCase();
            }
            city.checkService=true;
        } catch (Exception e) {
            e.printStackTrace();
            city.checkService=false;
        }
        return city;
    }
//    public static String getNetQuickByKey(String path){
//        BufferedReader reader = null;
//        String result = null;
//        StringBuffer sbf = new StringBuffer();
//        String httpUrl = path;
//        try {
//            URL url = new URL(httpUrl);
//            HttpURLConnection connection = (HttpURLConnection) url
//                    .openConnection();
//            connection.setRequestMethod("GET");
//            // 填入apikey到HTTP header
//            connection.setRequestProperty("apikey",  "***");
//            connection.connect();
//            InputStream is = connection.getInputStream();
//            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//            String strRead = null;
//            while ((strRead = reader.readLine()) != null) {
//                sbf.append(strRead);
//                sbf.append("\r\n");
//            }
//            reader.close();
//            result = sbf.toString();
//            return result;
//        } catch (Exception e) {
//            return "err";
//        }
//    }
    public static OnePlace getRealFromWeather(OnePlace city){
        BufferedReader readerSecound = null;
        String resultSecoundX = null;
        StringBuffer sbfSecound = new StringBuffer();
        String httpUrlSecound = CommonHelper.quickCityWeather + city.cityId;
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
            resultSecoundX = sbfSecound.toString();
            JSONTokener jsonTokenerThird = new JSONTokener(resultSecoundX);
            JSONObject jsonObject=(JSONObject)jsonTokenerThird.nextValue();
            JSONObject data=jsonObject.getJSONObject("data");
            try{
                city.aqi=data.getString("aqi");
            }catch (Exception e){
                city.aqi="--";
            }
            city.curTmp=data.getString("wendu");
            JSONArray days=data.getJSONArray("forecast");
            JSONObject today=days.getJSONObject(0);
            String high=today.getString("high");
            high=high.replace("高温","");
            high=high.replace("℃","");
            high=high.replace(" ","");
            city.maxTmp=high;
            String low=today.getString("low");
            low=low.replace("低温","");
            low=low.replace("℃","");
            low=low.replace(" ","");
            city.minTmp=low;
            String cur=today.getString("type");
            city.curStatus=cur;
            return city;
        } catch (Exception e) {
            return city;
        }
    }
    public static OnePlace getRealFromXML(OnePlace city) throws UnsupportedEncodingException {
        String httpArgSecound = "city="+URLEncoder.encode(city.city, "utf-8");
        BufferedReader readerSecound = null;
        String resultSecound = null;
        StringBuffer sbfSecound = new StringBuffer();
        String httpUrlSecound = CommonHelper.quickCitySecound + "?" + httpArgSecound;
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
            int tmpStart=resultSecound.indexOf("<wendu>");
            int tmpStop=resultSecound.indexOf("</wendu>");
            int humStart=resultSecound.indexOf("<shidu>");
            int humStop=resultSecound.indexOf("</shidu>");
            int timeStart=resultSecound.indexOf("<updatetime>");
            int timeStop=resultSecound.indexOf("</updatetime>");
            int windStart=resultSecound.indexOf("<fengli>");
            int windStop=resultSecound.indexOf("</fengli>");
            int dayStart=resultSecound.indexOf("<date_1>");
            int dayStop=resultSecound.indexOf("</date_1>");
            int sunRiseStart=resultSecound.indexOf("<sunrise_1>");
            int sunRiseEnd=resultSecound.indexOf("</sunrise_1>");
            int sunSetStart=resultSecound.indexOf("<sunset_1>");
            int sunSetEnd=resultSecound.indexOf("</sunset_1>");
            String wendu=resultSecound.substring(tmpStart+7,tmpStop);
            String shidu=resultSecound.substring(humStart+7,humStop);
            String shortTime=resultSecound.substring(timeStart+12,timeStop);
            String feng=resultSecound.substring(windStart+8,windStop);
            String today=resultSecound.substring(dayStart+8,dayStop);
            city.rise=resultSecound.substring(sunRiseStart+11,sunRiseEnd);
            city.set=resultSecound.substring(sunSetStart+10,sunSetEnd);
            today=today.replace("日","");
            today=today.replace("星期一","");
            today=today.replace("星期二","");
            today=today.replace("星期三","");
            today=today.replace("星期四","");
            today=today.replace("星期五","");
            today=today.replace("星期六","");
            today=today.replace("星期日","");
            today=today.replace("星期","");
            Date nowTime=new Date();
            DateFormat dfDay = new SimpleDateFormat("yyyy-MM");
            DateFormat fullDay = new SimpleDateFormat("yyyy-MM-dd");
            /* 2016-11-04 解决日期超过问题 */
            today=dfDay.format(nowTime)+"-"+ (Integer.valueOf(today));
            long todayLog=fullDay.parse(today).getTime()+1000*60*60*24;
            today=fullDay.format(new Date(todayLog));
            /* 2016-11-04 这个问题应该解决了 */
            city.quickDate=today;
            city.quickTime=shortTime;
            city.pm25=shidu.replace("%","");
            city.curTmp=wendu;
            city.windSpd=feng.replace("级","");
//            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//            if(df.format(new Date()).equals(city.quickDate) || checkZore(shortTime)){//
//                city.quickTime=shortTime;
//                city.quickDate=df.format(new Date());
//            }else{
//                if(!df.format(new Date()).equals(city.quickDate) && !checkZore(shortTime)){
//                    city.quickTime=shortTime;
//                }
//            }
            /* 时间负值问题解决 */
            DateFormat dfS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date quickDate= null;
            String quick=city.quickDate+" "+city.quickTime+":00";
            try {
                quickDate = dfS.parse(quick);
            } catch (ParseException e) {
                e.printStackTrace();
            }
//            long longTime=new Date().getTime()-quickDate.getTime();
//            if(longTime<0){
//                today=dfDay.format(nowTime)+"-"+ (Integer.valueOf(tudayTemp));
//                city.quickDate=today;
//            }
            city.xmlService=true;
            return city;
        } catch (Exception e) {
            city.xmlService=false;
            return city;
        }
    }
    public static List<OneNews> getNews() throws UnsupportedEncodingException {
        BufferedReader readerSecound = null;
        String resultSecound = null;
        StringBuffer sbfSecound = new StringBuffer();
        String httpUrlSecound = CommonHelper.quickNews;
        List<OneNews> newsList=new ArrayList<OneNews>();
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
            String[] newsString=resultSecound.split("<li class=\"t\">");
            for(int i=0; i<6;i++){
                String tempItem=newsString[i];
                String link="";
                String text="";
                link=tempItem.substring(tempItem.indexOf("href")+23,tempItem.indexOf("shtml")-1);
                text=tempItem.substring(tempItem.indexOf("blank\" >"),tempItem.indexOf("</a>"));
                text=text.replace("blank\" >","");
                OneNews news=new OneNews();
                news.link=link;
                news.text=text;
                newsList.add(news);
            }
            return newsList;
        } catch (Exception e) {
            return newsList;
        }
    }
    public static OnePlace getDetailFromMyServer(Context context,OnePlace city){
        try {
            String httpArgThird =city.cityId; // 本来的城市拼音改成城市id
            BufferedReader readerThird = null;
            String resultThird = null;
            StringBuffer sbfThird = new StringBuffer();
            String httpUrlThird = CommonHelper.dayDetail + httpArgThird;
            URL urlThird = new URL(httpUrlThird);
            HttpURLConnection connectionThird = (HttpURLConnection) urlThird
                    .openConnection();
            connectionThird.setRequestMethod("GET");
            connectionThird.setConnectTimeout(6000);
            // 填入apikey到HTTP header
            connectionThird.connect();
            InputStream isThird = connectionThird.getInputStream();
            readerThird = new BufferedReader(new InputStreamReader(isThird, "UTF-8"));
            String strReadThird = null;
            while ((strReadThird = readerThird.readLine()) != null) {
                sbfThird.append(strReadThird);
            }
            readerThird.close();
            resultThird = sbfThird.toString();
            JSONTokener jsonTokenerThird = new JSONTokener(resultThird);
            JSONObject jsonObjectThird=(JSONObject)jsonTokenerThird.nextValue();
            /* app 更新部分 */
            SharedPreferences.Editor editor=context.getSharedPreferences("settings",Context.MODE_PRIVATE).edit();
            String version=jsonObjectThird.getString("appver");
            String path=jsonObjectThird.getString("apppath");
            String ad=jsonObjectThird.getString("appad");
            editor.putInt("ad",Integer.valueOf(ad));
            editor.commit();
            if(!version.equals(CommonHelper.appVersion+"")){
                editor.putString("version",version);
                editor.putString("path",path);
                editor.commit();
            }
            String errNumThird=jsonObjectThird.getString("status");
            if(errNumThird.equals("success")){
                city.sevenDay.clear();
                city.dayDetail.clear();
                String datas=jsonObjectThird.getString("data");
                jsonObjectThird=new JSONObject(datas);
                city.slowDateTime=jsonObjectThird.getString("cityTime");
                        /* 第一天[今天] */
                OneDay day1=new OneDay();
                day1.maxTmp=jsonObjectThird.getString("day1Max");
                day1.minTmp=jsonObjectThird.getString("day1Min");
                day1.wCode=jsonObjectThird.getString("day1Icon");
                city.sevenDay.add(day1);
                        /* 第一天[今天] */
                OneDay day2=new OneDay();
                day2.maxTmp=jsonObjectThird.getString("day2Max");
                day2.minTmp=jsonObjectThird.getString("day2Min");
                day2.wCode=jsonObjectThird.getString("day2Icon");
                city.sevenDay.add(day2);
                        /* 第一天[今天] */
                OneDay day3=new OneDay();
                day3.maxTmp=jsonObjectThird.getString("day3Max");
                day3.minTmp=jsonObjectThird.getString("day3Min");
                day3.wCode=jsonObjectThird.getString("day3Icon");
                city.sevenDay.add(day3);
                        /* 第一天[今天] */
                OneDay day4=new OneDay();
                day4.maxTmp=jsonObjectThird.getString("day4Max");
                day4.minTmp=jsonObjectThird.getString("day4Min");
                day4.wCode=jsonObjectThird.getString("day4Icon");
                city.sevenDay.add(day4);
                        /* 第一天[今天] */
                OneDay day5=new OneDay();
                day5.maxTmp=jsonObjectThird.getString("day5Max");
                day5.minTmp=jsonObjectThird.getString("day5Min");
                day5.wCode=jsonObjectThird.getString("day5Icon");
                city.sevenDay.add(day5);
                        /* 第一天[今天] */
                OneDay day6=new OneDay();
                day6.maxTmp=jsonObjectThird.getString("day6Max");
                day6.minTmp=jsonObjectThird.getString("day6Min");
                day6.wCode=jsonObjectThird.getString("day6Icon");
                city.sevenDay.add(day6);
                        /* 第一天[今天] */
                OneDay day7=new OneDay();
                day7.maxTmp=jsonObjectThird.getString("day7Max");
                day7.minTmp=jsonObjectThird.getString("day7Min");
                day7.wCode=jsonObjectThird.getString("day7Icon");
                city.sevenDay.add(day7);
                /* 每日舒适度 */
                city.todayComf=jsonObjectThird.getString("todayComf");
                city.todayCw=jsonObjectThird.getString("todayCw");
                city.todayDrsg=jsonObjectThird.getString("todayDrsg");
                city.todayFlu=jsonObjectThird.getString("todayFlu");
                city.todaySport=jsonObjectThird.getString("todaySport");
                city.todayTrav=jsonObjectThird.getString("todayTrav");
                city.todayUv=jsonObjectThird.getString("todayUv");
                        /* 一天中的数据 */
                String[] strHour=new String[8];
                strHour[0]=jsonObjectThird.getString("today1").equals("null")?"0":jsonObjectThird.getString("today1");
                strHour[1]=jsonObjectThird.getString("today2").equals("null")?"0":jsonObjectThird.getString("today2");
                strHour[2]=jsonObjectThird.getString("today3").equals("null")?"0":jsonObjectThird.getString("today3");
                strHour[3]=jsonObjectThird.getString("today4").equals("null")?"0":jsonObjectThird.getString("today4");
                strHour[4]=jsonObjectThird.getString("today5").equals("null")?"0":jsonObjectThird.getString("today5");
                strHour[5]=jsonObjectThird.getString("today6").equals("null")?"0":jsonObjectThird.getString("today6");
                strHour[6]=jsonObjectThird.getString("today7").equals("null")?"0":jsonObjectThird.getString("today7");
                strHour[7]=jsonObjectThird.getString("today8").equals("null")?"0":jsonObjectThird.getString("today8");
                for(int i=0; i<strHour.length;i++){
                    OneHour hour=new OneHour();
                    hour.maxTmp=strHour[i];
                    city.dayDetail.add(hour);
                }
            }
            try{
                if(!jsonObjectThird.getString("aqi").equals("--")){
                    city.aqi=jsonObjectThird.getString("aqi");
                }
            }catch (Exception e){
            }
            city.detailService=true;
            return city;
        } catch (Exception e) {
            city.detailService=false;
            return city;
        }
    }
//    private static boolean checkZore(String time){//慢速更新08点钟才有新数据
//        if(time.indexOf("00:")==-1 && time.indexOf("01:")==-1 && time.indexOf("02:")==-1 && time.indexOf("03:")==-1 && time.indexOf("04:")==-1 && time.indexOf("05:")==-1 && time.indexOf("06:")==-1 && time.indexOf("07:")==-1){
//            return false;
//        }
//        return true;
//    }
    public static GlobalCity searchCity(String cityZh,int times){
        cityZh=cityCleaner(cityZh);
        GlobalCity reCity=new GlobalCity();
        reCity.zhName=cityZh;
        String httpArg = "cityname="+cityZh;
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        String httpUrl = checkCitySecound + "?" + httpArg;
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey",  "***");
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
            JSONTokener jsonTokener = new JSONTokener(result);
            JSONObject jsonObject=(JSONObject)jsonTokener.nextValue();
            String errNum=jsonObject.getString("errNum");
            if(!errNum.equals("0")){
                if(times<3){
                    reCity=searchCity(cityZh,++times);
                    return reCity;
                }else {
                    reCity.zhName="--";
                    return reCity;
                }
            }
            JSONObject oneCity=jsonObject.getJSONObject("retData");
            reCity.zhName=oneCity.getString("city");
            reCity.uyName=translate(reCity.zhName);
        } catch (Exception e) {
            reCity.zhName="--";
            e.printStackTrace();
        }
        return reCity;
    }
    public static String cityCleaner(String city){
        city=city.replace("市","");
        city=city.replace("县","");
        city=city.replace("地区","");
        return city;
    }
    public static String translate(String zh){
        /* 新的程序 */
        URL url = null;
        HttpURLConnection http = null;
        StringBuffer sbfThird = new StringBuffer();
        try {
            url = new URL("http://www.mzywfy.org.cn/ajaxservlet");
            http = (HttpURLConnection) url.openConnection();
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setUseCaches(false);
            http.setConnectTimeout(50000);//设置连接超时
            //如果在建立连接之前超时期满，则会引发一个 java.net.SocketTimeoutException。超时时间为零表示无穷大超时。
            http.setReadTimeout(50000);//设置读取超时
            //如果在数据可读取之前超时期满，则会引发一个 java.net.SocketTimeoutException。超时时间为零表示无穷大超时。
            http.setRequestMethod("POST");
            // http.setRequestProperty("Content-Type","text/xml; charset=UTF-8");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.connect();
            String param = "&src_text=" + URLEncoder.encode(zh, "utf-8")
                    + "&from=" + "zh"
                    + "&to=" + "uy"
                    + "&url=" + "2";
            OutputStreamWriter osw = new OutputStreamWriter(http.getOutputStream(), "utf-8");
            osw.write(param);
            osw.flush();
            osw.close();
            if (http.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream(), "utf-8"));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    sbfThird.append(inputLine);
                }
                in.close();
                JSONTokener jsonTokener = new JSONTokener(sbfThird.toString());
                JSONObject jsonObject = (JSONObject)jsonTokener.nextValue();
                String uyCity = jsonObject.getString("tgt_text");
                uyCity=uyCity.replace("<br>", "");
                uyCity=uyCity.replace("، ", "");
                if(uyCity.indexOf("|")!=-1){
                    zh=translateTilmach(zh);
                    return zh;
                }else if(uyCity.indexOf("<")!=-1){
                    zh=translateTilmach(zh);
                    return zh;
                }else if(uyCity.indexOf("ERROR")!=-1){
                    zh=translateTilmach(zh);
                    return zh;
                }else if(uyCity.indexOf("error")!=-1){
                    zh=translateTilmach(zh);
                    return zh;
                }
                return uyCity;
            }
            return zh;
        } catch (Exception e) {
            zh=translateTilmach(zh);
            return zh;
        } finally {
            if (http != null) http.disconnect();
        }
    }
    public static String translateTilmach(String zh){
        /* 新的程序 */
        URL url = null;
        HttpURLConnection http = null;
        StringBuffer sbfThird = new StringBuffer();
        try {
            url = new URL("http://www.tilmach.cn/Home/DoTranslate");
            http = (HttpURLConnection) url.openConnection();
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setUseCaches(false);
            http.setConnectTimeout(50000);//设置连接超时
            //如果在建立连接之前超时期满，则会引发一个 java.net.SocketTimeoutException。超时时间为零表示无穷大超时。
            http.setReadTimeout(50000);//设置读取超时
            //如果在数据可读取之前超时期满，则会引发一个 java.net.SocketTimeoutException。超时时间为零表示无穷大超时。
            http.setRequestMethod("POST");
            // http.setRequestProperty("Content-Type","text/xml; charset=UTF-8");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.connect();
            String param ="&sourceLang=" + "zh-CN"
                    + "&source=" + URLEncoder.encode(zh, "utf-8")
                    + "&targetLang=" + "ug-CN";
            OutputStreamWriter osw = new OutputStreamWriter(http.getOutputStream(), "utf-8");
            osw.write(param);
            osw.flush();
            osw.close();
            if (http.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream(), "utf-8"));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    sbfThird.append(inputLine);
                }
                in.close();
                JSONTokener jsonTokener = new JSONTokener(sbfThird.toString());
                JSONObject jsonObject = (JSONObject)jsonTokener.nextValue();
                String uyCity = jsonObject.getString("data");
                uyCity=uyCity.replace("<br>", "");
                uyCity=uyCity.replace("\n", "");
                uyCity=uyCity.replace("، ", "");
                if(uyCity.indexOf("|")!=-1){
                    return zh;
                }else if(uyCity.indexOf("<")!=-1){
                    return zh;
                }else if(uyCity.indexOf("ERROR")!=-1){
                    return zh;
                }else if(uyCity.indexOf("error")!=-1){
                    return zh;
                }
                return uyCity;
            }
            return zh;
        } catch (Exception e) {
            return zh;
        } finally {
            if (http != null) http.disconnect();
        }
    }
    public static String globalTranslate(String raw) throws UnsupportedEncodingException {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        String httpUrl = translate + URLEncoder.encode(raw, "utf-8");
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
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
            JSONTokener jsonTokener = new JSONTokener(result);
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
            String uyResult=jsonObject.getString("uyresult");
            return uyResult;
        }catch (Exception e){
            return raw;
        }
    }
    public static String[] daysLabel(Context context,String time){
        String quick=time+":00";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date quickDate=df.parse(quick);
            int today=quickDate.getDay();
            String[] days=new String[8];
            days[0]=context.getResources().getText(R.string.weather_today).toString();
            for(int i=0; i<7;i++){
                int temp=today+i;
                temp=temp%7;
                String tempLabel="";
                switch (temp){
                    case 0:
                        tempLabel=context.getResources().getText(R.string.weather_mon).toString();
                        break;
                    case 1:
                        tempLabel=context.getResources().getText(R.string.weather_tue).toString();
                        break;
                    case 2:
                        tempLabel=context.getResources().getText(R.string.weather_wed).toString();
                        break;
                    case 3:
                        tempLabel=context.getResources().getText(R.string.weather_thu).toString();
                        break;
                    case 4:
                        tempLabel=context.getResources().getText(R.string.weather_fri).toString();
                        break;
                    case 5:
                        tempLabel=context.getResources().getText(R.string.weather_sat).toString();
                        break;
                    case 6:
                        tempLabel=context.getResources().getText(R.string.weather_sun).toString();
                        break;
                }
                days[i+1]=tempLabel;
            }
            return days;
        } catch (ParseException e) {
            return null;
        }
    }
    public static GlobalCity findErrorCity(GlobalCity globalCity, Context context){
        /* 阿克苏定位 */
        if(globalCity.zhName.equals(context.getResources().getText(R.string.err_aksuzh))){
            globalCity.uyName= context.getResources().getText(R.string.err_aksu).toString();
        }
        /* 上海定位 */
        if(globalCity.zhName.equals(context.getResources().getText(R.string.err_shanghaizh))){
            globalCity.uyName= context.getResources().getText(R.string.err_shanghai).toString();
        }
        /* 北京定位 */
        if(globalCity.zhName.equals(context.getResources().getText(R.string.err_beijingzh))){
            globalCity.uyName= context.getResources().getText(R.string.err_beijing).toString();
        }
        return globalCity;
    }
}



