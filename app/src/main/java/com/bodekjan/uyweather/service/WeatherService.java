package com.bodekjan.uyweather.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;

import com.blankj.utilcode.utils.NetworkUtils;
import com.bodekjan.uyweather.model.OneDay;
import com.bodekjan.uyweather.model.OneHour;
import com.bodekjan.uyweather.model.OnePlace;
import com.bodekjan.uyweather.model.PlaceLib;
import com.bodekjan.uyweather.model.WeatherStatus;
import com.bodekjan.uyweather.receiver.ServiceReceiver;
import com.bodekjan.uyweather.util.CommonHelper;
import com.bodekjan.uyweather.util.MyDatabaseHelper;
import com.bodekjan.uyweather.util.MyNotificationManager;
import com.bodekjan.uyweather.util.WeatherTranslator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by bodekjan on 2016/9/12.
 */
public class WeatherService extends Service {
    private MyLocalBinder mBinder = new MyLocalBinder();
    //int language;
    @Override
    public void onCreate() {
        super.onCreate();
//        SharedPreferences pref=this.getSharedPreferences("settings", Context.MODE_PRIVATE);
//        language=pref.getInt("lang",0);

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!NetworkUtils.isConnected(getApplicationContext())){
                    return;
                }
                OnePlace place=new OnePlace();
                /* 获取本地最新数据，并查找最新地震信息 */
                MyDatabaseHelper dbHelper= new MyDatabaseHelper(getApplicationContext(),"weather.db",null, CommonHelper.dbVersion);
                SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
                Cursor cursor = sqLiteDatabase.query("place",null,null,null,null,null,null);
                if(cursor.getCount()<1){
                    cursor.close();
                    dbHelper.close();
                    return;
                }
                if(cursor.moveToFirst()){
                    do {
                        int isHome=cursor.getInt(cursor.getColumnIndex("status"));
                        if(isHome==1){
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
                        }
                    }while (cursor.moveToNext());
                }
                cursor.close();
                sqLiteDatabase.close();
                try {
                    String quick=place.quickDate+" "+ place.quickTime+":00";
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Date quickDate=df.parse(quick);
                        long time=new Date().getTime()-quickDate.getTime();
                        if(CommonHelper.getPastMinutes(time)>CommonHelper.refreshTime || CommonHelper.getPastMinutes(time)<0){
                            PlaceLib.get(getApplicationContext()).quickUpCity(place);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    checkLanguage();
                    /* 刷新状态栏 */
                    SharedPreferences pref=WeatherService.this.getSharedPreferences("settings", Context.MODE_PRIVATE);
                    int bar=pref.getInt("statusbar",-1);
                    if (bar==0){
                        MyNotificationManager.myNotify(WeatherService.this,place);
                    }
                    /* 刷新桌面 */
                    Intent intent=new Intent();
                    intent.setAction("com.bodekjan.homechanged");  //widget 更新一下数据
                    sendBroadcast(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour=CommonHelper.serviceTime*60*1000; //300*1000 是5分钟
        long triggerAtTime= SystemClock.elapsedRealtime()+anHour;
        Intent i=new Intent(this, ServiceReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    class MyLocalBinder extends Binder {
        public WeatherService getServiceInstance(){
            return WeatherService.this;
        }
        //...这里也可以继续写方法对外提供
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    public void checkLanguage() {
        SharedPreferences pref = getSharedPreferences("settings", Context.MODE_PRIVATE);
        int language = pref.getInt("lang", -1);
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        // 应用用户选择语言
        if (language == 0) {
            config.locale = new Locale("uy");
        } else if (language == 1) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        }
        resources.updateConfiguration(config, dm);
    }
}