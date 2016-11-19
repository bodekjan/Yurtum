package com.bodekjan.uyweather.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blankj.utilcode.utils.StringUtils;
import com.bodekjan.uyweather.util.CommonHelper;
import com.bodekjan.uyweather.util.MyDatabaseHelper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by bodekjan on 2016/9/6.
 */
public class PlaceLib {
    private static final String NEWCITY = "com.bodekjan.dataallready";
    private static final String QUICKCITY = "com.bodekjan.quickweatherupdate";
    private static final String NETERROR = "com.bodekjan.servererror";
    private MyDatabaseHelper dbHelper;
    private ArrayList<OnePlace> mPlaceList;
    private Context mContext;
    private static PlaceLib mEdataLib;
    private OnePlace newCity;
    private PlaceLib(Context context) throws ParseException {
        mContext=context;
        mPlaceList=new ArrayList<OnePlace>();
        dbHelper= new MyDatabaseHelper(mContext,"weather.db",null, CommonHelper.dbVersion);
        initData();
    }
    private void initData() throws ParseException {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("place",null,null,null,null,null,null);
        if(cursor.getCount()==0){
            cursor.close();
            sqLiteDatabase.close();
            return;
        }
        mPlaceList.clear();
        if(cursor.moveToFirst()){
            do {
                OnePlace place=new OnePlace();
                place.status=cursor.getInt(cursor.getColumnIndex("status"));
                place.city=cursor.getString(cursor.getColumnIndex("city"));
                place.uyCity=cursor.getString(cursor.getColumnIndex("uyCity"));
                place.cityId=cursor.getString(cursor.getColumnIndex("cityId"));
                place.cityPinyin=cursor.getString(cursor.getColumnIndex("cityPinyin"));
                place.curTmp=cursor.getString(cursor.getColumnIndex("curTmp"));
                place.curStatus=cursor.getString(cursor.getColumnIndex("curStatus"));
                place.minTmp=cursor.getString(cursor.getColumnIndex("minTmp"));
                place.maxTmp=cursor.getString(cursor.getColumnIndex("maxTmp"));
                place.quickDate=cursor.getString(cursor.getColumnIndex("quickDate"));
                place.quickTime=cursor.getString(cursor.getColumnIndex("quickTime"));
                place.pm25=cursor.getString(cursor.getColumnIndex("pm25"));
                place.aqi=cursor.getString(cursor.getColumnIndex("aqi"));
                place.rise=cursor.getString(cursor.getColumnIndex("sunrise"));
                place.set=cursor.getString(cursor.getColumnIndex("sunset"));
                place.windSpd=cursor.getString(cursor.getColumnIndex("windSpd"));
                place.todayComf=cursor.getString(cursor.getColumnIndex("todayComf"));
                place.todayCw=cursor.getString(cursor.getColumnIndex("todayCw"));
                place.todayDrsg=cursor.getString(cursor.getColumnIndex("todayDrsg"));
                place.todayFlu=cursor.getString(cursor.getColumnIndex("todayFlu"));
                place.todaySport=cursor.getString(cursor.getColumnIndex("todaySport"));
                place.todayTrav=cursor.getString(cursor.getColumnIndex("todayTrav"));
                place.todayUv=cursor.getString(cursor.getColumnIndex("todayUv"));
                String[] args=new String[]{place.cityId};
                Cursor cursorDay = sqLiteDatabase.query("days",null,"cityId=?",args,null,null,null);
                if(cursorDay.moveToFirst()){
                    do {
                        OneDay day=new OneDay();
                        day.maxTmp=cursorDay.getString(cursorDay.getColumnIndex("maxTmp"));
                        day.minTmp=cursorDay.getString(cursorDay.getColumnIndex("minTmp"));
                        day.wCode=cursorDay.getString(cursorDay.getColumnIndex("wCode"));
                        place.sevenDay.add(day);
                    }while (cursorDay.moveToNext());
                }
                cursorDay.close();
                Cursor cursorHour = sqLiteDatabase.query("hours",null,"cityId=?",args,null,null,null);
                if(cursorHour.moveToFirst()){
                    do {
                        OneHour hour=new OneHour();
                        hour.maxTmp=cursorHour.getString(cursorHour.getColumnIndex("maxTmp"));
                        place.dayDetail.add(hour);
                    }while (cursorHour.moveToNext());
                }
                cursorHour.close();
                mPlaceList.add(place);
            }while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
    }
    public static PlaceLib get(Context context) throws ParseException {
        if(mEdataLib==null){
            mEdataLib = new PlaceLib(context);
        }
        return mEdataLib;
    }
    public ArrayList<OnePlace> getCitys(){
        return mPlaceList;
    }
    public OnePlace getCity(String cityid){
        for(OnePlace c : mPlaceList){
            if(c.cityId==cityid)
                return c;
        }
        return null;
    }
    public boolean checkCity(String cityName){
        for(int i=0; i<mPlaceList.size();i++){
            if(mPlaceList.get(i).city.indexOf(cityName)>-1){
                return true;
            }
        }
        return false;
    }
    public void changeHome(String cityCode){
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues removeHome = new ContentValues();
        removeHome.put("status", 0);
        String[] argsRomove = {"1"};
        ContentValues setHome = new ContentValues();
        setHome.put("status", 1);
        String[] args = {cityCode};
        sqLiteDatabase.update("place", removeHome, "status=?",argsRomove);
        sqLiteDatabase.update("place", setHome, "cityId=?",args);
        try {
            initData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public void removeCity(String cityCode){
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        String cityCause = "cityId=?";//删除的条件
        String[] cityArgs = {cityCode};//删除的条件参数
        sqLiteDatabase.delete("place",cityCause,cityArgs);//执行删除
        String dayCause = "cityId=?";//删除的条件
        String[] dayArgs = {cityCode};//删除的条件参数
        sqLiteDatabase.delete("days",dayCause,dayArgs);//执行删除
        sqLiteDatabase.close();
        try {
            initData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public void setCity(GlobalCity inCity){
        newCity=new OnePlace();
        newCity.city=inCity.zhName;
        newCity.uyCity=inCity.uyName;
        newCity.cityPinyin=inCity.pin;
        new Thread(NewCityInit).start();
    }
    public void quickUpCity(OnePlace city){
        newCity=city;
        new Thread(RefreshCity).start();
    }
    Runnable NewCityInit = new Runnable() {
        @Override
        public void run() {
            try {
                newCity=CommonHelper.checkNewCity(newCity,CommonHelper.checkCity,false);
                if(!newCity.checkService){
                }
                if(newCity.cityId.equals("--")){
                    newCity=CommonHelper.checkNewCity(newCity,CommonHelper.checkCitySecound,true);
                    if(newCity.cityId.equals("--")){
                        Intent intent = new Intent();
                        intent.setAction(NETERROR);
                        mContext.sendBroadcast(intent);
                        return;
                    }
                }
                /* 被百度坑了 */
                String httpUrl="";
                if(StringUtils.isEmpty(newCity.cityId)){
                    String httpArg = "cityname="+ URLEncoder.encode(newCity.city, "utf-8");
                    httpUrl = CommonHelper.quickCityThird + "?" + httpArg;
                }else{
                    String httpArg = "cityid="+newCity.cityId;
                    httpUrl = CommonHelper.quickCity + "?" + httpArg;
                }
                String result=CommonHelper.getNetQuickByKey(httpUrl);
                if(result.equals("err")){
                    Intent intent = new Intent();
                    intent.setAction(NETERROR);
                    mContext.sendBroadcast(intent);
                    return;
                }
                JSONTokener jsonTokener = new JSONTokener(result);
                JSONObject jsonObject=(JSONObject)jsonTokener.nextValue();
                String errNum=jsonObject.getString("errNum");
                if(!errNum.equals("0")){
                    Intent intent = new Intent();
                    intent.setAction(NETERROR);
                    mContext.sendBroadcast(intent);
                    return;
                }
                JSONObject oneCity=jsonObject.getJSONObject("retData");
                if(mPlaceList.size()==0){
                    newCity.status=1;
                }else {
                    newCity.status=0;
                }
                if(newCity.uyCity==null){
                    newCity.uyCity=CommonHelper.translate(newCity.city);
                }
                newCity.curTmp=oneCity.getString("temp");
                newCity.minTmp=oneCity.getString("l_tmp");
                newCity.maxTmp=oneCity.getString("h_tmp");
                newCity.curStatus=oneCity.getString("weather");
                newCity.quickDate="20"+oneCity.getString("date");
                newCity.quickTime=oneCity.getString("time");
                /* 被百度坑了，我能修好 */
                /* 用新API试试看 */
                newCity=CommonHelper.getRealFromWeather(newCity);
                if(newCity.cityPinyin.equals("---")){
                    newCity.cityPinyin=oneCity.getString("pinyin");
                }
                SQLiteDatabase db=dbHelper.getWritableDatabase();
                /* 检查重复 */
                Cursor cursor = db.query("place",null,null,null,null,null,null);
                if(cursor.getCount()!=0){
                    if(cursor.moveToFirst()){
                        do {
                            String cityId=cursor.getString(cursor.getColumnIndex("cityId"));
                            if(newCity.cityId.equals(cityId)){
                                return;
                            }
                        }while (cursor.moveToNext());
                    }
                    cursor.close();
                }else {
                    cursor.close();
                }
                /* 检查完毕 */
                /* 细粒度信息获取 */
                newCity=CommonHelper.getRealFromXML(newCity);
                if(!newCity.xmlService){
                    Intent intent = new Intent();
                    intent.setAction(NETERROR);
                    mContext.sendBroadcast(intent);
                }
                /* 每日详细获取 */
                newCity=CommonHelper.getDetailFromMyServer(mContext,newCity);
                /* 新闻也顺便更新一下 */
                NewsLib.get(mContext).refreshNews();
                if(!newCity.detailService){
                    Intent intent = new Intent();
                    intent.setAction(NETERROR);
                    mContext.sendBroadcast(intent);
                }
                ContentValues values=new ContentValues();
                values.put("status",newCity.status);
                values.put("city",newCity.city);
                values.put("uyCity",newCity.uyCity);
                values.put("cityId",newCity.cityId);
                values.put("cityPinyin",newCity.cityPinyin);
                values.put("curTmp",newCity.curTmp);
                values.put("curStatus",newCity.curStatus);
                values.put("minTmp",newCity.minTmp);
                values.put("maxTmp",newCity.maxTmp);
                values.put("quickDate",newCity.quickDate);
                values.put("quickTime",newCity.quickTime);
                values.put("pm25", newCity.pm25);
                values.put("aqi", newCity.aqi);
                values.put("sunrise", newCity.rise);
                values.put("sunset", newCity.set);
                values.put("windSpd", newCity.windSpd);
                values.put("todayComf", newCity.todayComf);
                values.put("todayCw", newCity.todayCw);
                values.put("todayDrsg", newCity.todayDrsg);
                values.put("todayFlu", newCity.todayFlu);
                values.put("todaySport", newCity.todaySport);
                values.put("todayTrav", newCity.todayTrav);
                values.put("todayUv", newCity.todayUv);
                db.insert("place",null,values);
                db.close();
                SQLiteDatabase db2=dbHelper.getWritableDatabase();
                /* 先清空7天数据，然后加上去 */
                String dayCause = "cityId=?";//删除的条件
                String[] dayArgs = {newCity.cityId};//删除的条件参数
                db2.delete("days",dayCause,dayArgs);//执行删除
                for(int i=0; i<newCity.sevenDay.size();i++){
                    ContentValues dayValues=new ContentValues();
                    dayValues.put("cityId",newCity.cityId);
                    dayValues.put("minTmp",newCity.sevenDay.get(i).minTmp);
                    dayValues.put("maxTmp",newCity.sevenDay.get(i).maxTmp);
                    dayValues.put("wCode",newCity.sevenDay.get(i).wCode);
                    db2.insert("days",null,dayValues);
                }
                /* 先清空当天详细数据，然后加上去 */
                db2.delete("hours",dayCause,dayArgs);//执行删除
                for(int j=0; j<newCity.dayDetail.size();j++){
                    ContentValues dayValues=new ContentValues();
                    dayValues.put("cityId",newCity.cityId);
                    dayValues.put("maxTmp",newCity.dayDetail.get(j).maxTmp);
                    db2.insert("hours",null,dayValues);
                }
                db2.close();
                initData();
                Intent intent = new Intent();
                intent.setAction(NEWCITY);
                mContext.sendBroadcast(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent();
                intent.setAction(NETERROR);
                mContext.sendBroadcast(intent);
            }
        }
    };
    Runnable RefreshCity = new Runnable() {
        @Override
        public void run() {
            try {
                String httpArg = "cityname="+URLEncoder.encode(newCity.city, "utf-8");
                String httpUrl = CommonHelper.quickCity + "?" + httpArg;
                String result=CommonHelper.getNetQuickByKey(httpUrl);
                if(result.equals("err")){
                    Intent intent = new Intent();
                    intent.setAction(NETERROR);
                    mContext.sendBroadcast(intent);
                    return;
                }
                JSONTokener jsonTokener = new JSONTokener(result);
                JSONObject jsonObject=(JSONObject)jsonTokener.nextValue();
                String errNum=jsonObject.getString("errNum");
                if(!errNum.equals("0")){
                    Intent intent = new Intent();
                    intent.setAction(NETERROR);
                    mContext.sendBroadcast(intent);
                    return;
                }
                JSONObject oneCity=jsonObject.getJSONObject("retData");
                newCity.cityId=oneCity.getString("citycode");
                newCity.curTmp=oneCity.getString("temp");
                newCity.minTmp=oneCity.getString("l_tmp");
                newCity.maxTmp=oneCity.getString("h_tmp");
                newCity.curStatus=oneCity.getString("weather");
                newCity.quickDate="20"+oneCity.getString("date");
                newCity.quickTime=oneCity.getString("time");
                newCity.pm25="--";
                /* 查时间是不是有效的 */
                String quick=newCity.quickDate+" "+newCity.quickTime+":00";
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date quickDate=df.parse(quick);
                long time=new Date().getTime()-quickDate.getTime();
                /* 先清空7天数据，然后加上去 */
                String dayCause = "cityId=?";//删除的条件
                String[] dayArgs = {newCity.cityId};//删除的条件参数
                if((CommonHelper.getPastMinutes(time)>CommonHelper.refreshTime)||CommonHelper.getPastMinutes(time)<0){
                    /* 用新API试试看 */
                    newCity=CommonHelper.getRealFromWeather(newCity);
                    newCity=CommonHelper.getRealFromXML(newCity);
                    if(!newCity.xmlService){
                        Intent intent = new Intent();
                        intent.setAction(NETERROR);
                        mContext.sendBroadcast(intent);
                        return;
                    }
                }
                if(CommonHelper.getPastMinutes(time)>CommonHelper.detailTime){
                    newCity=CommonHelper.getDetailFromMyServer(mContext,newCity);
                    if(!newCity.detailService){
//                        Log.e("ERROR","连接详细信息服务器失败");  //这个连不上问题不大，可以不用提示
//                        Intent intent = new Intent();
//                        intent.setAction(NETERROR);
//                        mContext.sendBroadcast(intent);
                    }
                    SQLiteDatabase dbDeleteHour=dbHelper.getWritableDatabase();
                    dbDeleteHour.delete("hours",dayCause,dayArgs);//执行删除
                    dbDeleteHour.close();
                    SQLiteDatabase dbAddHour=dbHelper.getWritableDatabase();
                    for(int j=0; j<newCity.dayDetail.size();j++){
                        ContentValues dayValues=new ContentValues();
                        dayValues.put("cityId",newCity.cityId);
                        dayValues.put("maxTmp",newCity.dayDetail.get(j).maxTmp);
                        dbAddHour.insert("hours",null,dayValues);
                    }
                    dbAddHour.close();
                    SQLiteDatabase dbDeleteDay=dbHelper.getWritableDatabase();
                    dbDeleteDay.delete("days",dayCause,dayArgs);//执行删除
                    dbDeleteDay.close();
                    SQLiteDatabase dbAddDay=dbHelper.getWritableDatabase();
                    for(int j=0; j<newCity.sevenDay.size();j++){
                        ContentValues dayValues=new ContentValues();
                        dayValues.put("cityId",newCity.cityId);
                        dayValues.put("minTmp",newCity.sevenDay.get(j).minTmp);
                        dayValues.put("maxTmp",newCity.sevenDay.get(j).maxTmp);
                        dayValues.put("wCode",newCity.sevenDay.get(j).wCode);
                        dbAddDay.insert("days",null,dayValues);
                    }
                    dbAddDay.close();
                    /* 新闻也顺便更新一下 */
                    NewsLib.get(mContext).refreshNews();
                }
                ContentValues setHome = new ContentValues();
                setHome.put("curTmp", newCity.curTmp);
                setHome.put("curStatus", newCity.curStatus);
                setHome.put("minTmp", newCity.minTmp);
                setHome.put("maxTmp", newCity.maxTmp);
                setHome.put("quickDate", newCity.quickDate);
                setHome.put("quickTime", newCity.quickTime);
                setHome.put("pm25", newCity.pm25);
                setHome.put("aqi", newCity.aqi);
                setHome.put("sunrise", newCity.rise);
                setHome.put("sunset", newCity.set);
                setHome.put("windSpd", newCity.windSpd);
                setHome.put("todayComf", newCity.todayComf);
                setHome.put("todayCw", newCity.todayCw);
                setHome.put("todayDrsg", newCity.todayDrsg);
                setHome.put("todayFlu", newCity.todayFlu);
                setHome.put("todaySport", newCity.todaySport);
                setHome.put("todayTrav", newCity.todayTrav);
                setHome.put("todayUv", newCity.todayUv);
                String[] args = {newCity.cityId};
                SQLiteDatabase db=dbHelper.getWritableDatabase();
                db.update("place", setHome, "cityId=?",args);
                db.close();
                initData();
                Intent intent = new Intent();
                intent.setAction(QUICKCITY);
                mContext.sendBroadcast(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent();
                intent.setAction(NETERROR);
                mContext.sendBroadcast(intent);
            }
        }
    };
}
