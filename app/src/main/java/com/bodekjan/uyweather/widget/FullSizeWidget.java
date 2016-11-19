package com.bodekjan.uyweather.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;


import com.blankj.utilcode.utils.SizeUtils;
import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.activities.MainActivity;
import com.bodekjan.uyweather.model.OnePlace;
import com.bodekjan.uyweather.model.PlaceLib;
import com.bodekjan.uyweather.model.WeatherStatus;
import com.bodekjan.uyweather.util.CommonHelper;
import com.bodekjan.uyweather.util.WeatherTranslator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bodekjan on 2016/9/12.
 */
public class FullSizeWidget extends AppWidgetProvider
{
    private static Timer myTimer;
    private static int index = 0;
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
        super.onEnabled(context);
        //在插件被创建的时候这里会被调用， 所以我们在这里开启一个timer 每秒执行一次
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        Log.e("AAA","桌面组建更新了");
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
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.e("AAA","收到换城市广播");
        //当判断到是该事件发过来时， 我们就获取插件的界面， 然后将index自加后传入到textview中
        System.out.println("onReceive");
        if(intent.getAction().equals(broadCastString))
        {
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
        }
        super.onReceive(context, intent);
    }
    private void setValues(Context context,OnePlace homeCity){
        try {
            if (homeCity != null) {
                RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_fullsize);
                Intent fullIntent = new Intent(context, MainActivity.class);
                PendingIntent Pfullintent = PendingIntent.getActivity(context, 0, fullIntent, 0);
                rv.setOnClickPendingIntent(R.id.full_widgetcontainer, Pfullintent);
                WeatherStatus status = WeatherTranslator.weatherTextTranslator(context, homeCity.curStatus);
                BitmapDrawable bdLarge = (BitmapDrawable) context.getResources().getDrawable(status.getIconId());
                Bitmap bitmapLarge = bdLarge.getBitmap();
                String[] dayLabels = CommonHelper.daysLabel(context, homeCity.quickDate + " " + homeCity.quickTime);
                if (homeCity.city != null || !homeCity.city.equals("--")) {
                    for (int i = 0; i < homeCity.sevenDay.size(); i++) {
                        if (i == 0) {
                            rv.setImageViewBitmap(R.id.day5label, buildUpdate(context, dayLabels[i]));
                            rv.setTextViewText(R.id.day5temp, homeCity.maxTmp + "°/" + homeCity.minTmp + "°");
                            rv.setImageViewBitmap(R.id.day5image, bitmapLarge);
                        } else if (i == 1) {
                            rv.setImageViewBitmap(R.id.day4label, buildUpdate(context, dayLabels[i]));
                            rv.setTextViewText(R.id.day4temp, homeCity.sevenDay.get(i).maxTmp + "°/" + homeCity.sevenDay.get(i).minTmp + "°");
                            BitmapDrawable bd = (BitmapDrawable) WeatherTranslator.weatherCodeToDrawable(context, homeCity.sevenDay.get(i).wCode);
                            Bitmap bitmap = bd.getBitmap();
                            rv.setImageViewBitmap(R.id.day4image, bitmap);
                        } else if (i == 2) {
                            rv.setImageViewBitmap(R.id.day3label, buildUpdate(context, dayLabels[i]));
                            rv.setTextViewText(R.id.day3temp, homeCity.sevenDay.get(i).maxTmp + "°/" + homeCity.sevenDay.get(i).minTmp + "°");
                            BitmapDrawable bd = (BitmapDrawable) WeatherTranslator.weatherCodeToDrawable(context, homeCity.sevenDay.get(i).wCode);
                            Bitmap bitmap = bd.getBitmap();
                            rv.setImageViewBitmap(R.id.day3image, bitmap);
                        } else if (i == 3) {
                            rv.setImageViewBitmap(R.id.day2label, buildUpdate(context, dayLabels[i]));
                            rv.setTextViewText(R.id.day2temp, homeCity.sevenDay.get(i).maxTmp + "°/" + homeCity.sevenDay.get(i).minTmp + "°");
                            BitmapDrawable bd = (BitmapDrawable) WeatherTranslator.weatherCodeToDrawable(context, homeCity.sevenDay.get(i).wCode);
                            Bitmap bitmap = bd.getBitmap();
                            rv.setImageViewBitmap(R.id.day2image, bitmap);
                        } else if (i == 4) {
                            rv.setImageViewBitmap(R.id.day1label, buildUpdate(context, dayLabels[i]));
                            rv.setTextViewText(R.id.day1temp, homeCity.sevenDay.get(i).maxTmp + "°/" + homeCity.sevenDay.get(i).minTmp + "°");
                            BitmapDrawable bd = (BitmapDrawable) WeatherTranslator.weatherCodeToDrawable(context, homeCity.sevenDay.get(i).wCode);
                            Bitmap bitmap = bd.getBitmap();
                            rv.setImageViewBitmap(R.id.day1image, bitmap);
                        }
                    }
                }
                //将该界面显示到插件中
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                ComponentName componentName = new ComponentName(context, FullSizeWidget.class);
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
        paint.setTextAlign(Paint.Align.CENTER);
        myCanvas.drawText(time, SizeUtils.dp2px(context,32), SizeUtils.dp2px(context,10), paint);
        return myBitmap;
    }
}