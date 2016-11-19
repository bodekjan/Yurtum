package com.bodekjan.uyweather.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.blankj.utilcode.utils.SizeUtils;
import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.activities.MainActivity;
import com.bodekjan.uyweather.activities.SettingActivity;
import com.bodekjan.uyweather.model.OnePlace;
import com.bodekjan.uyweather.model.PlaceLib;
import com.bodekjan.uyweather.model.WeatherStatus;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by bodekjan on 2016/9/14.
 */
public class MyNotificationManager {
    public static void myNotify(Context context,OnePlace place){
        try {
            if(place==null){
                ArrayList<OnePlace> places= null;
                try {
                    places = PlaceLib.get(context).getCitys();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(places.size()==0){
                    return;
                }
                for(int i=0; i<places.size();i++) {
                    if (places.get(i).status == 1) {
                        place = places.get(i);
                    }
                }
            }
            /* Notification */
            SharedPreferences pref=context.getSharedPreferences("settings", Context.MODE_PRIVATE);
            int language = pref.getInt("lang", -1);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            // Creates an explicit intent for an Activity in your app
            mBuilder.setDefaults(Notification.FLAG_NO_CLEAR);
            mBuilder.setOngoing(true);
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_noti);
            if(language==0){
                rv.setImageViewBitmap(R.id.ncity, buildUpdate(context,place.uyCity));
            }else {
                rv.setImageViewBitmap(R.id.ncity, buildUpdate(context,place.city));
            }
            WeatherStatus status= WeatherTranslator.weatherTextTranslator(context,place.curStatus);
            Intent resultIntent = new Intent(context, MainActivity.class);
            double version=Double.valueOf(pref.getString("version",CommonHelper.appVersion));
            double myVersion=Double.valueOf(CommonHelper.appVersion);
            if(version>myVersion){
                rv.setImageViewBitmap(R.id.ncurtext, buildUpdateLong(context,context.getResources().getText(R.string.snack_newversion).toString()));
                resultIntent = new Intent(context, SettingActivity.class);
            }else {
                rv.setImageViewBitmap(R.id.ncurtext, buildUpdateLong(context,status.getuText()));
            }
            BitmapDrawable bdLarge = (BitmapDrawable) context.getResources().getDrawable(status.getIconId());
            Bitmap bitmapLarge = bdLarge.getBitmap();
            mBuilder.setSmallIcon(status.getIconId());
            rv.setImageViewBitmap(R.id.nweather, bitmapLarge);
            rv.setTextViewText(R.id.nmaxmin, place.maxTmp+"°/"+place.minTmp+"°");
            rv.setTextViewText(R.id.ncurrent, place.curTmp+"°");
            mBuilder.setContent(rv);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(CommonHelper.NOTIFICATION, mBuilder.build());
        }catch (Exception e){
            return;
        }
    }
    public static void myNotify(Context context){
        try {

            OnePlace place=null;
            if(place==null){
                ArrayList<OnePlace> places= null;
                try {
                    places = PlaceLib.get(context).getCitys();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(places.size()==0){
                    return;
                }
                for(int i=0; i<places.size();i++) {
                    if (places.get(i).status == 1) {
                        place = places.get(i);
                    }
                }
            }
            /* Notification */
            SharedPreferences pref=context.getSharedPreferences("settings", Context.MODE_PRIVATE);
            int language = pref.getInt("lang", -1);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            // Creates an explicit intent for an Activity in your app
            mBuilder.setDefaults(Notification.FLAG_NO_CLEAR);
            mBuilder.setOngoing(true);
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_noti);
            if(language==0){
                rv.setImageViewBitmap(R.id.ncity, buildUpdate(context,place.uyCity));
            }else {
                rv.setImageViewBitmap(R.id.ncity, buildUpdate(context,place.city));
            }
            WeatherStatus status= WeatherTranslator.weatherTextTranslator(context,place.curStatus);
            Intent resultIntent = new Intent(context, MainActivity.class);
            double version=Double.valueOf(pref.getString("version",CommonHelper.appVersion));
            double myVersion=Double.valueOf(CommonHelper.appVersion);
            if(version>myVersion){
                rv.setImageViewBitmap(R.id.ncurtext, buildUpdateLong(context,context.getResources().getText(R.string.snack_newversion).toString()));
                resultIntent = new Intent(context, SettingActivity.class);
            }else {
                rv.setImageViewBitmap(R.id.ncurtext, buildUpdateLong(context,status.getuText()));
            }
            BitmapDrawable bdLarge = (BitmapDrawable) context.getResources().getDrawable(status.getIconId());
            Bitmap bitmapLarge = bdLarge.getBitmap();
            mBuilder.setSmallIcon(status.getIconId());
            rv.setImageViewBitmap(R.id.nweather, bitmapLarge);
            rv.setTextViewText(R.id.nmaxmin, place.maxTmp+"°/"+place.minTmp+"°");
            rv.setTextViewText(R.id.ncurrent, place.curTmp+"°");
            mBuilder.setContent(rv);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(CommonHelper.NOTIFICATION, mBuilder.build());
        }catch (Exception e){
            return;
        }
    }
    public static void closeNotification(Context context){
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(CommonHelper.NOTIFICATION);
    }
    public static Bitmap buildUpdate(Context context,String time)
    {
        Bitmap myBitmap = Bitmap.createBitmap(SizeUtils.dp2px(context,64), SizeUtils.dp2px(context,20), Bitmap.Config.ARGB_4444);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        Typeface clock = Typeface.createFromAsset(context.getAssets(),"fonts/ALKATIP.TTF");
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(clock);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(context.getResources().getColor(R.color.notification_text));
        paint.setTextSize(SizeUtils.dp2px(context,14));
        paint.setTextAlign(Paint.Align.RIGHT);
        myCanvas.drawText(time, SizeUtils.dp2px(context,64), SizeUtils.dp2px(context,10), paint);
        return myBitmap;
    }
    public static Bitmap buildUpdateLong(Context context,String time)
    {
        Bitmap myBitmap = Bitmap.createBitmap(SizeUtils.dp2px(context,180), SizeUtils.dp2px(context,20), Bitmap.Config.ARGB_4444);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        Typeface clock = Typeface.createFromAsset(context.getAssets(),"fonts/ALKATIP.TTF");
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(clock);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(context.getResources().getColor(R.color.notification_text));
        paint.setTextSize(SizeUtils.dp2px(context,13));
        paint.setTextAlign(Paint.Align.LEFT);
        myCanvas.drawText(time, SizeUtils.dp2px(context,0), SizeUtils.dp2px(context,14), paint);
        return myBitmap;
    }
}
