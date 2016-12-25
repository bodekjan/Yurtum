package com.bodekjan.uyweather.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.widget.RemoteViews;

import com.blankj.utilcode.utils.SizeUtils;
import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.activities.MainActivity;
import com.bodekjan.uyweather.model.OnePlace;
import com.bodekjan.uyweather.model.PlaceLib;
import com.bodekjan.uyweather.model.WeatherStatus;
import com.bodekjan.uyweather.service.LocalService;
import com.bodekjan.uyweather.service.RemoteService;
import com.bodekjan.uyweather.util.CommonHelper;
import com.bodekjan.uyweather.util.WeatherTranslator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by bodekjan on 2016/9/12.
 */
public class ExtraSizeWidget extends AppWidgetProvider
{
    int lang=0;
    boolean haveNew=false;
    String message="";
    private Context context;
    //定义我们要发送的事件
    private final String broadCastString = "com.bodekjan.homechanged";
    @Override
    public void onDeleted(Context context, int[] appWidgetIds)
    {
        // TODO Auto-generated method stub
        super.onDeleted(context, appWidgetIds);
        System.out.println("onDeleted");
    }
    @Override
    public void onEnabled(Context context)
    {
        this.context=context;
        SharedPreferences pref=context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        checkLanguage(context,pref.getInt("lang", -1));
        lang=pref.getInt("lang", -1);
        double version=Double.valueOf(pref.getString("version",CommonHelper.appVersion));
        double myVersion=Double.valueOf(CommonHelper.appVersion);
        if(version>myVersion){
            haveNew=true;
            message=context.getResources().getText(R.string.snack_newversion).toString();
        }
        try {
            ArrayList<OnePlace> places=PlaceLib.get(context).getCitys();
            if(places.size()==0){
                return;
            }
            OnePlace homeCity= new OnePlace();
            for(int i=0; i<places.size();i++) {
                if (places.get(i).status == 1) {
                    homeCity = places.get(i);
                }
            }
            setValues(context,homeCity);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(context, LocalService.class);
        Intent intentR = new Intent(context, RemoteService.class);
        context.startService(intent);
        context.startService(intentR);
        super.onEnabled(context);
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Intent intent = new Intent(context, LocalService.class);
        Intent intentR = new Intent(context, RemoteService.class);
        context.stopService(intent);
        context.stopService(intentR);
    }
    @Override
    public void onReceive(Context context, Intent intent)
    {
        this.context=context;
        if(intent.getAction().equals(broadCastString)) {
            SharedPreferences pref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
            checkLanguage(context,pref.getInt("lang", -1));
            lang=pref.getInt("lang", -1);
            double version=Double.valueOf(pref.getString("version",CommonHelper.appVersion));
            double myVersion=Double.valueOf(CommonHelper.appVersion);
            if(version>myVersion){
                haveNew = true;
                message = context.getResources().getText(R.string.snack_newversion).toString();
            }
            try {
                ArrayList<OnePlace> places = PlaceLib.get(context).getCitys();
                if (places.size() == 0) {
                    return;
                }
                OnePlace homeCity = new OnePlace();
                for (int i = 0; i < places.size(); i++) {
                    if (places.get(i).status == 1) {
                        homeCity = places.get(i);
                    }
                }
                setValues(context, homeCity);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String action = intent.getAction();
        if (action.equals("android.intent.action.USER_PRESENT")) {// 用户唤醒设备时启动服务
            context.startService(new Intent(context, LocalService.class));
            context.startService(new Intent(context, RemoteService.class));
            updateTime();
        } else if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            context.startService(new Intent(context, LocalService.class));
            context.startService(new Intent(context, RemoteService.class));
            updateTime();
        }
        super.onReceive(context, intent);
    }
    private void setValues(Context context,OnePlace homeCity){
        try {
            if (homeCity != null) {
                RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_extrasize);
                Intent fullIntent = new Intent(context, MainActivity.class);
                PendingIntent Pfullintent = PendingIntent.getActivity(context, 0, fullIntent, 0);
                rv.setOnClickPendingIntent(R.id.extra_widgetcontainer, Pfullintent);
                if (lang == 0) {
                    rv.setImageViewBitmap(R.id.extra_widgetcity, buildUpdate(context, homeCity.uyCity));
                } else {
                    rv.setImageViewBitmap(R.id.extra_widgetcity, buildUpdate(context, homeCity.city));
                }
                WeatherStatus status = WeatherTranslator.weatherTextTranslator(context, homeCity.curStatus);
                rv.setImageViewBitmap(R.id.extrastatus, buildUpdateLong(context, status.getuText()));
                rv.setTextViewText(R.id.extramaxmin, homeCity.maxTmp + "°/" + homeCity.minTmp + "°");
                rv.setTextViewText(R.id.extratemp, homeCity.curTmp + "°");
                BitmapDrawable bdLarge = (BitmapDrawable) context.getResources().getDrawable(status.getIconId());
                Bitmap bitmapLarge = bdLarge.getBitmap();
                rv.setImageViewBitmap(R.id.extra_widgeticon, bitmapLarge);
                String[] dayLabels = CommonHelper.daysLabel(context, homeCity.quickDate + " " + homeCity.quickTime);
                if (!homeCity.city.equals("--")) {
                    for (int i = 0; i < homeCity.sevenDay.size(); i++) {
                        if (i == 1) {
                            rv.setImageViewBitmap(R.id.extraday5label, buildUpdate(context, dayLabels[i]));
                            rv.setTextViewText(R.id.extraday5temp, homeCity.sevenDay.get(i).maxTmp + "°/" + homeCity.sevenDay.get(i).minTmp + "°");
                            BitmapDrawable bd = (BitmapDrawable) WeatherTranslator.weatherCodeToDrawable(context, homeCity.sevenDay.get(i).wCode);
                            Bitmap bitmap = bd.getBitmap();
                            rv.setImageViewBitmap(R.id.extraday5image, bitmap);
                        } else if (i == 2) {
                            rv.setImageViewBitmap(R.id.extraday4label, buildUpdate(context, dayLabels[i]));
                            rv.setTextViewText(R.id.extraday4temp, homeCity.sevenDay.get(i).maxTmp + "°/" + homeCity.sevenDay.get(i).minTmp + "°");
                            BitmapDrawable bd = (BitmapDrawable) WeatherTranslator.weatherCodeToDrawable(context, homeCity.sevenDay.get(i).wCode);
                            Bitmap bitmap = bd.getBitmap();
                            rv.setImageViewBitmap(R.id.extraday4image, bitmap);
                        } else if (i == 3) {
                            rv.setImageViewBitmap(R.id.extraday3label, buildUpdate(context, dayLabels[i]));
                            rv.setTextViewText(R.id.extraday3temp, homeCity.sevenDay.get(i).maxTmp + "°/" + homeCity.sevenDay.get(i).minTmp + "°");
                            BitmapDrawable bd = (BitmapDrawable) WeatherTranslator.weatherCodeToDrawable(context, homeCity.sevenDay.get(i).wCode);
                            Bitmap bitmap = bd.getBitmap();
                            rv.setImageViewBitmap(R.id.extraday3image, bitmap);
                        }
                    }
                }
                //将该界面显示到插件中
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                ComponentName componentName = new ComponentName(context, ExtraSizeWidget.class);
                appWidgetManager.updateAppWidget(componentName, rv);
            }
        }catch (Exception e){
            return;
        }
    }
    public Bitmap buildUpdate(Context context,String time)
    {
        Bitmap myBitmap = Bitmap.createBitmap(SizeUtils.dp2px(context,64), SizeUtils.dp2px(context,18), Bitmap.Config.ARGB_4444);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        Typeface clock = Typeface.createFromAsset(context.getAssets(),"fonts/ALKATIP.TTF");
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(clock);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(SizeUtils.dp2px(context,13));
        paint.setTextAlign(Paint.Align.RIGHT);
        myCanvas.drawText(time, SizeUtils.dp2px(context,64), SizeUtils.dp2px(context,10), paint);
        return myBitmap;
    }
    public Bitmap buildUpdateLong(Context context,String time)
    {
        Bitmap myBitmap = Bitmap.createBitmap(SizeUtils.dp2px(context,200), SizeUtils.dp2px(context,18), Bitmap.Config.ARGB_4444);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        Typeface clock = Typeface.createFromAsset(context.getAssets(),"fonts/ALKATIP.TTF");
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(clock);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(SizeUtils.dp2px(context,13));
        paint.setTextAlign(Paint.Align.LEFT);
        if(haveNew){
            paint.setColor(Color.YELLOW);
            myCanvas.drawText(message, SizeUtils.dp2px(context,0), SizeUtils.dp2px(context,10), paint);
        }else{
            paint.setColor(Color.WHITE);
            myCanvas.drawText(time, SizeUtils.dp2px(context,0), SizeUtils.dp2px(context,10), paint);
        }
        return myBitmap;
    }
    public void checkLanguage(Context context,int language) {
        Resources resources = context.getResources();
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
    public void updateTime(){
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_extrasize);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        remoteViews.setTextViewText(R.id.extratime, dateFormat.format(date));
        //将该界面显示到插件中
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context,ExtraSizeWidget.class);
        appWidgetManager.updateAppWidget(componentName, remoteViews);
    }
}