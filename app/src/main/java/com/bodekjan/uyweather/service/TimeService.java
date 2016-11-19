package com.bodekjan.uyweather.service;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.util.CommonHelper;
import com.bodekjan.uyweather.widget.ExtraSizeWidget;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bodekjan on 2016/10/21.
 */
public class TimeService extends Service {
    private RemoteViews remoteViews;
    private Timer timer;
    @Override
    public IBinder onBind(Intent intent) {
        // do nothing
        return null;
    }

    // 广播接收者去接收系统每分钟的提示广播，来更新时间
//    private BroadcastReceiver mTimePickerBroadcast = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            updateTime();
//        }
//    };
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        remoteViews = new RemoteViews(this.getPackageName(), R.layout.widget_extrasize);
        timer = new Timer();
        DateFormat df = new SimpleDateFormat("ss");
        int mSeconds=1000;
        int seconds=Integer.valueOf(df.format(new Date()));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                remoteViews.setTextViewText(R.id.extratime, dateFormat.format(date));
                //将该界面显示到插件中
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(TimeService.this);
                ComponentName componentName = new ComponentName(TimeService.this,ExtraSizeWidget.class);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }
        },0, 20*mSeconds);
    }

//    private void updateTime() {
//        Intent intent=new Intent();
//        intent.setAction("com.bodekjan.timeupdate");
//        sendBroadcast(intent);
//    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        registerReceiver();// 注册广播
//        updateTime();
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }
//    private void registerReceiver() {
//        IntentFilter updateIntent = new IntentFilter();
//        updateIntent.addAction("android.intent.action.TIME_TICK");
//        updateIntent.addAction("android.intent.action.TIME_SET");
//        updateIntent.addAction("android.intent.action.DATE_CHANGED");
//        updateIntent.addAction("android.intent.action.TIMEZONE_CHANGED");
//        registerReceiver(mTimePickerBroadcast, updateIntent);
//    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
//        if (mTimePickerBroadcast != null)
//        {
//            unregisterReceiver(mTimePickerBroadcast);
//        }
        timer=null;
    }
}