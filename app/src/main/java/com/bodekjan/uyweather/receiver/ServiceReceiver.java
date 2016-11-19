package com.bodekjan.uyweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by bodekjan on 2016/9/12.
 */
public class ServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, com.bodekjan.uyweather.service.WeatherService.class);
        context.startService(i);
        Log.e("AAA","广播要跑了!");
    }
}
