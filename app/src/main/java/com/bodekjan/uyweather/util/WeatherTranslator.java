package com.bodekjan.uyweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.model.WeatherStatus;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.weather_icons_typeface_library.WeatherIcons;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by bodekjan on 2016/9/10.
 */
public class WeatherTranslator {
    public static String fluTranslate(String flu){
        if(flu==null) return flu;
        switch (flu){
            case "少发":
                flu="بۈگۈنكى ھاۋارايىدا زۇكامداپ قالمايسىز";
                break;
            case "较易发":
                flu="بۈگۈنكى ھاۋارايىدا ئاساسەن زۇكامداپ قالمايسىز";
                break;
            case "易发":
                flu="بۈگۈنكى ھاۋارايىدا زۇكامداپ قالىسىز دېققەت قىلىڭ";
                break;
            case "极易发":
                flu="بۈگۈنكى ھاۋارايىدا بەك ئاسان زۇكامداپ قالىسىز دېققەت قىلىڭ";
                break;
            default:
                flu="";
                break;
        }
        return flu;
    }
    public static String dresTranslate(String flu){
        if(flu==null) return flu;
        switch (flu){
            case "热":
                flu="بۈگۈن ھاۋا ئىسسىق بولىدۇ، يالاڭ كىيىنىڭ";
                break;
            case "炎热":
                flu="بۈگۈن بەك ئىسسىق بولىدۇ، يالاڭ كىيىنىڭ";
                break;
            case "较舒适":
                flu="بۈگۈن تېمپراتۇرا نورمال، مۇۋاپىق كىيىنىڭ";
                break;
            case "舒适":
                flu="بۈگۈن ھاۋا ياخشى، مۇۋاپىق كىيىنىڭ";
                break;
            case "冷":
                flu="بۈگۈن ھاۋا سوغوق، قېلىنراق كىيىنىڭ";
                break;
            case "较冷":
                flu="بۈگۈن ھاۋا خېلە سوغوق، قېلىن كىيىنىڭ";
                break;
            case "寒冷":
                flu="بۈگۈن ھاۋا بەك سوغوق، قېلىن كىيىنىڭ";
                break;
            default:
                flu="";
                break;
        }
        return flu;
    }
    public static String travelTranslate(String flu){
        if(flu==null) return flu;
        switch (flu){
            case "适宜":
                flu="بۈگۈنكى ھاۋا ساياھەت قىلىشقا بەك ماس كىلىدۇ";
                break;
            case "较适宜":
                flu="بۈگۈنكى ھاۋا ساياھەت قىلىشقا بىرقەدەر ماس كېلىدۇ";
                break;
            case "一般":
                flu="بۈگۈنكى ھاۋا ساياھەت قىلىشقا ئانچە ماس كەلمەيدۇ";
                break;
            case "较不宜":
                flu="بۈگۈنكى ھاۋا ساياھەت قىلىشقا ئانچە ماس كەلمەيدۇ";
                break;
            case "不宜":
                flu="بۈگۈنكى ھاۋا ساياھەت قىلىشقا ماس كەلمەيدۇ";
                break;
            default:
                flu="";
                break;
        }
        return flu;
    }
    public static String sportTranslate(String flu){
        if(flu==null) return flu;
        switch (flu){
            case "适宜":
                flu="بۈگۈنكى ھاۋا تەنھەرىكەت قىلىشقا ماس كېلىدۇ";
                break;
            case "较适宜":
                flu="بۈگۈنكى ھاۋا تەنھەرىكەت قىلىشقا بىرئاز ماس كىلىدۇ";
                break;
            case "一般":
                flu="بۈگۈنكى ھاۋا تەنھەرىكەت قىلىشقا ئانچە ماس كەلمەيدۇ";
                break;
            case "较不宜":
                flu="بۈگۈنكى ھاۋا تەنھەرىكەت قىلىشقا ئانچە ماس كەلمەيدۇ";
                break;
            case "不宜":
                flu="بۈگۈنكى ھاۋا تەنھەرىكەت قىلىشقا ماس كەلمەيدۇ";
                break;
            default:
                flu="";
                break;
        }
        return flu;
    }
    public static String carTranslate(String flu){
        if(flu==null) return flu;
        switch (flu){
            case "适宜":
                flu="بۈگۈنكى ھاۋا ماشىنا يۇيۇشقا ماس كېلىدۇ";
                break;
            case "较适宜":
                flu="ماشىنا يۇيۇشقا بىرقەدەر ماس كېلىدۇ";
                break;
            case "一般":
                flu="ماشىنا يۇيۇشقا ئانچە ماس كەلمەيدۇ";
                break;
            case "较不宜":
                flu="ماشىنا يۇيۇشقا ئانچە ماس كەلمەيدۇ";
                break;
            case "不宜":
                flu="ماشىنا يۇيۇشقا ماس كەلمەيدۇ";
                break;

            default:
                flu="";
                break;
        }
        return flu;
    }
    public static String airTranslate(int aqi, Context context){
        if(aqi<=50){
            return context.getResources().getText(R.string.air_verygood).toString();
        }else if(aqi<=100 && aqi>50){
            return context.getResources().getText(R.string.air_good).toString();
        }else if(aqi<=150 && aqi>100){
            return context.getResources().getText(R.string.air_bad).toString();
        }else if(aqi<=250 && aqi>150){
            return context.getResources().getText(R.string.air_verybad).toString();
        }else if(aqi>250){
            return context.getResources().getText(R.string.air_extremelybad).toString();
        }
        return "--";
    }
    public static String comfTranslate(String flu){
        if(flu==null) return flu;
        switch (flu){
            case "舒适":
                flu="بۈگۈنكى ھاۋا يېقىشلىق";
                break;
            case "较舒适":
                flu="بۈگۈنكى ھاۋا بىرقەدەر يېقىشلىق";
                break;
            case "较不舒适":
                flu="بۈگۈنكى ھاۋا ئادەمنى بىئارام قىلىدۇ";
                break;
            case "很不舒适":
                flu="بۈگۈنكى ھاۋا ئادەمنى بەك بىئارام قىلىدۇ";
                break;
            default:
                flu="";
                break;
        }
        return flu;
    }
    public static WeatherStatus weatherTextTranslator(Context context,String zhText){
        WeatherStatus status=new WeatherStatus();
        status.setBgCode(R.mipmap.clearday);
        status.setuText(zhText);
        if(zhText==null){
            status.setIconId(R.mipmap.w01);
            return status;
        }
        switch (zhText){
            case "--":
                status.setuText(context.getResources().getText(R.string.default_title).toString());
                status.setIconId(R.mipmap.w01);
                break;
            case "晴":
                status.setuText(context.getResources().getText(R.string.wss_sunny).toString());
                status.setIconId(R.mipmap.w00);
                status.setBgCode(findBackground("sunny"));
                break;
            case "多云":
                status.setuText(context.getResources().getText(R.string.wss_cloudy).toString());
                status.setIconId(R.mipmap.w01);
                status.setBgCode(findBackground("cloud"));
                break;
            case "阴":
                status.setuText(context.getResources().getText(R.string.wss_overcast).toString());
                status.setIconId(R.mipmap.w02);
                status.setBgCode(findBackground("cloud"));
                break;
            case "阵雨":
                status.setuText(context.getResources().getText(R.string.wss_showerrain).toString());
                status.setIconId(R.mipmap.w03);
                status.setBgCode(findBackground("rain"));
                break;
            case "雷阵雨":
                status.setuText(context.getResources().getText(R.string.wss_thundershower).toString());
                status.setIconId(R.mipmap.w04);
                status.setBgCode(findBackground("rain"));
                break;
            case "雷阵雨伴有冰雹":
                status.setuText(context.getResources().getText(R.string.wss_hail).toString());
                status.setIconId(R.mipmap.w05);
                status.setBgCode(findBackground("rain"));
                break;
            case "雨夹雪":
                status.setuText(context.getResources().getText(R.string.wss_sleet).toString());
                status.setIconId(R.mipmap.w06);
                status.setBgCode(findBackground("snow"));
                break;
            case "小雨":
                status.setuText(context.getResources().getText(R.string.wss_lightrain).toString());
                status.setIconId(R.mipmap.w07);
                status.setBgCode(findBackground("rain"));
                break;
            case "中雨":
                status.setuText(context.getResources().getText(R.string.wss_moderaterain).toString());
                status.setIconId(R.mipmap.w08);
                status.setBgCode(findBackground("rain"));
                break;
            case "大雨":
                status.setuText(context.getResources().getText(R.string.wss_heavyrain).toString());
                status.setIconId(R.mipmap.w09);
                status.setBgCode(findBackground("rain"));
                break;
            case "暴雨":
                status.setuText(context.getResources().getText(R.string.wss_storm).toString());
                status.setIconId(R.mipmap.w10);
                status.setBgCode(findBackground("rain"));
                break;
            case "大暴雨":
                status.setuText(context.getResources().getText(R.string.wss_heavystorm).toString());
                status.setIconId(R.mipmap.w11);
                status.setBgCode(findBackground("rain"));
                break;
            case "特大暴雨":
                status.setuText(context.getResources().getText(R.string.wss_severestorm).toString());
                status.setIconId(R.mipmap.w12);
                status.setBgCode(findBackground("rain"));
                break;
            case "阵雪":
                status.setuText(context.getResources().getText(R.string.wss_snowflurry).toString());
                status.setIconId(R.mipmap.w13);
                status.setBgCode(findBackground("snow"));
                break;
            case "小雪":
                status.setuText(context.getResources().getText(R.string.wss_lightsnow).toString());
                status.setIconId(R.mipmap.w14);
                status.setBgCode(findBackground("snow"));
                break;
            case "中雪":
                status.setuText(context.getResources().getText(R.string.wss_moderatesnow).toString());
                status.setIconId(R.mipmap.w15);
                status.setBgCode(findBackground("snow"));
                break;
            case "大雪":
                status.setuText(context.getResources().getText(R.string.wss_heavysnow).toString());
                status.setIconId(R.mipmap.w16);
                status.setBgCode(findBackground("snow"));
                break;
            case "暴雪":
                status.setuText(context.getResources().getText(R.string.wss_snowstorm).toString());
                status.setIconId(R.mipmap.w17);
                status.setBgCode(findBackground("snow"));
                break;
            case "雾":
                status.setuText(context.getResources().getText(R.string.wss_foggy).toString());
                status.setIconId(R.mipmap.w18);
                status.setBgCode(findBackground("cloud"));
                break;
            case "冻雨":
                status.setuText(context.getResources().getText(R.string.wss_freezingrain).toString());
                status.setIconId(R.mipmap.w19);
                status.setBgCode(findBackground("rain"));
                break;
            case "沙尘暴":
                status.setuText(context.getResources().getText(R.string.wss_duststorm).toString());
                status.setIconId(R.mipmap.w20);
                status.setBgCode(findBackground("sand"));
                break;
            case "小到中雨":
                status.setuText(context.getResources().getText(R.string.wss_minmidrain).toString());
                status.setIconId(R.mipmap.w21);
                status.setBgCode(findBackground("rain"));
                break;
            case "中到大雨":
                status.setuText(context.getResources().getText(R.string.wss_midmaxrain).toString());
                status.setIconId(R.mipmap.w22);
                status.setBgCode(findBackground("rain"));
                break;
            case "大到暴雨":
                status.setuText(context.getResources().getText(R.string.wss_maxheavyrain).toString());
                status.setIconId(R.mipmap.w23);
                status.setBgCode(findBackground("rain"));
                break;
            case "暴雨到大暴雨":
                status.setuText(context.getResources().getText(R.string.wss_maxmaxrain).toString());
                status.setIconId(R.mipmap.w24);
                status.setBgCode(findBackground("rain"));
                break;
            case "大暴雨到特大暴雨":
                status.setuText(context.getResources().getText(R.string.wss_maxmaxrain).toString());
                status.setIconId(R.mipmap.w25);
                status.setBgCode(findBackground("rain"));
                break;
            case "小到中雪":
                status.setuText(context.getResources().getText(R.string.wss_minmidsnow).toString());
                status.setIconId(R.mipmap.w26);
                status.setBgCode(findBackground("snow"));
                break;
            case "中到大雪":
                status.setuText(context.getResources().getText(R.string.wss_midmaxsnow).toString());
                status.setIconId(R.mipmap.w27);
                status.setBgCode(findBackground("snow"));
                break;
            case "大到暴雪":
                status.setuText(context.getResources().getText(R.string.wss_maxheavysnow).toString());
                status.setIconId(R.mipmap.w28);
                status.setBgCode(findBackground("snow"));
                break;
            case "浮尘":
                status.setuText(context.getResources().getText(R.string.wss_dust).toString());
                status.setIconId(R.mipmap.w29);
                status.setBgCode(findBackground("sand"));
                break;
            case "扬沙":
                status.setuText(context.getResources().getText(R.string.wss_dust).toString());
                status.setIconId(R.mipmap.w30);
                status.setBgCode(findBackground("sand"));
                break;
            case "强沙尘暴":
                status.setuText(context.getResources().getText(R.string.wss_sandstorm).toString());
                status.setIconId(R.mipmap.w31);
                status.setBgCode(findBackground("sand"));
                break;
            case "霾":
                status.setuText(context.getResources().getText(R.string.wss_haze).toString());
                status.setIconId(R.mipmap.w32);
                status.setBgCode(findBackground("cloud"));
                break;
            case "无":
                status.setuText(context.getResources().getText(R.string.wss_nodata).toString());
                status.setIconId(R.mipmap.w01);
                status.setBgCode(findBackground("cloud"));
                break;
            default:
                status.setuText(context.getResources().getText(R.string.wss_nodata).toString());
                status.setIconId(R.mipmap.w01);
                status.setBgCode(findBackground("cloud"));
                break;
        }
        return status;
    }
    public static Drawable weatherCodeToDrawable(Context context,String code){
        Drawable re=context.getResources().getDrawable(R.mipmap.w01);
        if(code==null) return re;
        switch (code){
            case "100":
                re=context.getResources().getDrawable(R.mipmap.w00);
                break;
            case "101":
                re=context.getResources().getDrawable(R.mipmap.w01);
                break;
            case "102":
                re=context.getResources().getDrawable(R.mipmap.w01);
                break;
            case "103":
                re=context.getResources().getDrawable(R.mipmap.w01);
                break;
            case "104":
                re=context.getResources().getDrawable(R.mipmap.w02);
                break;
            case "200":
                re=context.getResources().getDrawable(R.mipmap.w02);
                break;
            case "201":
                re=context.getResources().getDrawable(R.mipmap.w02);
                break;
            case "202":
                re=context.getResources().getDrawable(R.mipmap.w02);
                break;
            case "203":
                re=context.getResources().getDrawable(R.mipmap.w02);
                break;
            case "204":
                re=context.getResources().getDrawable(R.mipmap.w02);
                break;
            case "205":
                re=context.getResources().getDrawable(R.mipmap.w30);
                break;
            case "206":
                re=context.getResources().getDrawable(R.mipmap.w30);
                break;
            case "207":
                re=context.getResources().getDrawable(R.mipmap.w30);
                break;
            case "208":
                re=context.getResources().getDrawable(R.mipmap.w30);
                break;
            case "209":
                re=context.getResources().getDrawable(R.mipmap.w30);
                break;
            case "210":
                re=context.getResources().getDrawable(R.mipmap.w30);
                break;
            case "211":
                re=context.getResources().getDrawable(R.mipmap.w30);
                break;
            case "212":
                re=context.getResources().getDrawable(R.mipmap.w30);
                break;
            case "213":
                re=context.getResources().getDrawable(R.mipmap.w30);
                break;
            case "300":
                re=context.getResources().getDrawable(R.mipmap.w03);
                break;
            case "301":
                re=context.getResources().getDrawable(R.mipmap.w03);
                break;
            case "302":
                re=context.getResources().getDrawable(R.mipmap.w04);
                break;
            case "303":
                re=context.getResources().getDrawable(R.mipmap.w04);
                break;
            case "304":
                re=context.getResources().getDrawable(R.mipmap.w05);
                break;
            case "305":
                re=context.getResources().getDrawable(R.mipmap.w07);
                break;
            case "306":
                re=context.getResources().getDrawable(R.mipmap.w08);
                break;
            case "307":
                re=context.getResources().getDrawable(R.mipmap.w09);
                break;
            case "308":
                re=context.getResources().getDrawable(R.mipmap.w10);
                break;
            case "309":
                re=context.getResources().getDrawable(R.mipmap.w07);
                break;
            case "310":
                re=context.getResources().getDrawable(R.mipmap.w10);
                break;
            case "311":
                re=context.getResources().getDrawable(R.mipmap.w11);
                break;
            case "312":
                re=context.getResources().getDrawable(R.mipmap.w12);
                break;
            case "313":
                re=context.getResources().getDrawable(R.mipmap.w19);
                break;
            case "400":
                re=context.getResources().getDrawable(R.mipmap.w14);
                break;
            case "401":
                re=context.getResources().getDrawable(R.mipmap.w15);
                break;
            case "402":
                re=context.getResources().getDrawable(R.mipmap.w16);
                break;
            case "403":
                re=context.getResources().getDrawable(R.mipmap.w17);
                break;
            case "404":
                re=context.getResources().getDrawable(R.mipmap.w06);
                break;
            case "405":
                re=context.getResources().getDrawable(R.mipmap.w06);
                break;
            case "406":
                re=context.getResources().getDrawable(R.mipmap.w06);
                break;
            case "407":
                re=context.getResources().getDrawable(R.mipmap.w13);
                break;
            case "500":
                re=context.getResources().getDrawable(R.mipmap.w18);
                break;
            case "501":
                re=context.getResources().getDrawable(R.mipmap.w18);
                break;
            case "502":
                re=context.getResources().getDrawable(R.mipmap.w32);
                break;
            case "503":
                re=context.getResources().getDrawable(R.mipmap.w30);
                break;
            case "504":
                re=context.getResources().getDrawable(R.mipmap.w29);
                break;
            case "505":
                re=context.getResources().getDrawable(R.mipmap.w29);
                break;
            case "506":
                re=context.getResources().getDrawable(R.mipmap.w29);
                break;
            case "507":
                re=context.getResources().getDrawable(R.mipmap.w20);
                break;
            case "508":
                re=context.getResources().getDrawable(R.mipmap.w31);
                break;
            case "900":
                re=context.getResources().getDrawable(R.mipmap.w00);
                break;
            case "901":
                re=context.getResources().getDrawable(R.mipmap.w01);
                break;
            case "902":
                re=context.getResources().getDrawable(R.mipmap.w01);
                break;
        }
        return re;
    }
    public static int findBackground(String how){
        int reBack=R.mipmap.clearday;
        DateFormat df = new SimpleDateFormat("HH:mm");
        String time=df.format(new Date());
        boolean day=true;
        if(time.indexOf("00:")!=-1 || time.indexOf("01:")!=-1 || time.indexOf("02:")!=-1 || time.indexOf("03:")!=-1 || time.indexOf("04:")!=-1 || time.indexOf("05:")!=-1 || time.indexOf("06:")!=-1 || time.indexOf("19:")!=-1 || time.indexOf("20:")!=-1 || time.indexOf("21:")!=-1 || time.indexOf("22:")!=-1 || time.indexOf("23:")!=-1)
        {
            day=false;
        }
        switch (how){
            case "sunny":
                if(day){
                    reBack=R.mipmap.clearday;
                }else {
                    reBack=R.mipmap.clearnight;
                }
                break;
            case "cloud":
                if(day){
                    reBack=R.mipmap.cloudday;
                }else {
                    reBack=R.mipmap.cloudnight;
                }
                break;
            case "rain":
                if(day){
                    reBack=R.mipmap.rainyday;
                }else {
                    reBack=R.mipmap.rainynight;
                }
                break;
            case "snow":
                if(day){
                    reBack=R.mipmap.snowday;
                }else {
                    reBack=R.mipmap.snownight;
                }
                break;
            case "sand":
                if(day){
                    reBack=R.mipmap.sandday;
                }else {
                    reBack=R.mipmap.sandday;
                }
                break;
        }
        return reBack;
    }
    public static String setTimeZone(Context context, String time){
        try{
            String[] temps=time.split(":");
            SharedPreferences pref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
            int zone = pref.getInt("timezone", -1);
            int timeVal=0;
            // 应用用户选择语言
            if (zone == 0 || zone ==-1) {
                timeVal=Integer.valueOf(temps[0])-2;
            } else if (zone == 1) {
                timeVal=Integer.valueOf(temps[0]);
            }
            time=timeVal+":"+temps[1];
        }catch (Exception e){

        }
        return time;
    }
}
