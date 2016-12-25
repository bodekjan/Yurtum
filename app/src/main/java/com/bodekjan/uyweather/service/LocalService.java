package com.bodekjan.uyweather.service;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.aidl.IProcessService;
import com.bodekjan.uyweather.widget.ExtraSizeWidget;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bodekjan on 2016/10/21.
 */
public class LocalService extends Service {
    String TAG = "LocalService";
    private RemoteViews remoteViews;
    private Timer timer;
    private LocalBinder mLocalBinder;
    private LocalServiceConnection mLocalServiceConn;
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        timer=null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mLocalBinder = new LocalBinder();

        if (mLocalServiceConn == null) {
            mLocalServiceConn = new LocalServiceConnection();
        }
        /* 时间更新 */
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
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(LocalService.this);
                ComponentName componentName = new ComponentName(LocalService.this,ExtraSizeWidget.class);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }
        },0, 20*mSeconds);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }

    /**
     * 通过AIDL实现进程间通信
     */
    class LocalBinder extends IProcessService.Stub {
        @Override
        public String getServiceName() throws RemoteException {
            return "LocalService";
        }
    }

    /**
     * 连接远程服务
     */
    class LocalServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                // 与远程服务通信
                IProcessService process = IProcessService.Stub.asInterface(service);
                Log.i(TAG, "连接" + process.getServiceName() + "服务成功");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // RemoteException连接过程出现的异常，才会回调,unbind不会回调
            // 监测，远程服务已经死掉，则重启远程服务

            // 启动远程服务
            startService(new Intent(LocalService.this, RemoteService.class));

            // 绑定远程服务
            bindService(new Intent(LocalService.this, RemoteService.class), mLocalServiceConn, Context.BIND_IMPORTANT);
        }
    }
}