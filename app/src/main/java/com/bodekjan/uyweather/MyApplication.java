package com.bodekjan.uyweather;

import android.app.Application;
import android.content.Context;

import com.bodekjan.uyweather.receiver.TimeReceiver;
import com.bodekjan.uyweather.receiver.TimeReceiverGuard;
import com.bodekjan.uyweather.service.TimeService;
import com.bodekjan.uyweather.service.TimeServiceGuard;
import com.bodekjan.uyweather.util.MyCrashHandler;
import com.marswin89.marsdaemon.DaemonApplication;
import com.marswin89.marsdaemon.DaemonConfigurations;


/**
 * Created by bodekjan on 2016/6/12.
 */
public class MyApplication extends DaemonApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        MyCrashHandler mCustomCrashHandler = MyCrashHandler.getInstance();
        mCustomCrashHandler.setCustomCrashHanler(getApplicationContext());
    }
    @Override
    protected DaemonConfigurations getDaemonConfigurations() {
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
                "com.bodekjan.uyweather:process1",
                TimeService.class.getCanonicalName(),
                TimeReceiver.class.getCanonicalName());

        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
                "com.bodekjan.uyweather:process2",
                TimeServiceGuard.class.getCanonicalName(),
                TimeReceiverGuard.class.getCanonicalName());

        DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
        //return new DaemonConfigurations(configuration1, configuration2);//listener can be null
        return new DaemonConfigurations(configuration1, configuration2, listener);
    }
    class MyDaemonListener implements DaemonConfigurations.DaemonListener{
        @Override
        public void onPersistentStart(Context context) {
        }

        @Override
        public void onDaemonAssistantStart(Context context) {
        }

        @Override
        public void onWatchDaemonDaed() {
        }
    }
    @Override
    public void attachBaseContextByDaemon(Context base) {
        super.attachBaseContextByDaemon(base);
    }
}
