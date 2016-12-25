package com.bodekjan.uyweather;

import android.app.Application;

import com.bodekjan.uyweather.util.MyCrashHandler;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;


/**
 * Created by bodekjan on 2016/6/12.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PlatformConfig.setWeixin("wx8165366cc17f9522", "092b4d79f963cf738aacc282b20afd94");
        UMShareAPI.get(this);
        //Config.DEBUG = true;
        MyCrashHandler mCustomCrashHandler = MyCrashHandler.getInstance();
        mCustomCrashHandler.setCustomCrashHanler(getApplicationContext());
    }
}
