package com.bodekjan.uyweather.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.util.MyNotificationManager;
import com.bodekjan.uyweather.widget.MyResideMenuItem;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikepenz.weather_icons_typeface_library.WeatherIcons;
import com.special.ResideMenu.ResideMenu;

import java.util.Locale;

/**
 * Created by bodekjan on 2016/9/12.
 */
public class MyBaseActivity extends AppCompatActivity implements View.OnClickListener{
    protected final String WIDGETUPDATE = "com.bodekjan.homechanged";
    ResideMenu resideMenu;
    Typeface uyFace;
    MyResideMenuItem homeItem;
    MyResideMenuItem cityItem;
    MyResideMenuItem settItem;
    MyResideMenuItem langItem;
    MyResideMenuItem compItem;
    SwipeMenuListView listView;
    public int lang=0; //0 为维语，1为汉语
    public void initMenu(){
        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.mipmap.menubg);
        resideMenu.attachToActivity(this);
        IconicsDrawable home=new IconicsDrawable(this)
                .icon(WeatherIcons.Icon.wic_day_sunny)
                .color(Color.WHITE)
                .sizeDp(22);
        IconicsDrawable city=new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_home)
                .color(Color.WHITE)
                .sizeDp(22);
        IconicsDrawable setting=new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_settings)
                .color(Color.WHITE)
                .sizeDp(22);
        IconicsDrawable translate=new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_globe)
                .color(Color.WHITE)
                .sizeDp(22);
        IconicsDrawable compass=new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_compass)
                .color(Color.WHITE)
                .sizeDp(22);
        homeItem=new MyResideMenuItem(this , uyFace,  home,getResources().getString(R.string.weather_mtitle));
        homeItem.setOnClickListener(this);
        cityItem=new MyResideMenuItem(this , uyFace,  city,getResources().getString(R.string.city_title));
        cityItem.setOnClickListener(this);
        settItem=new MyResideMenuItem(this , uyFace,  setting,getResources().getString(R.string.setting_title));
        settItem.setOnClickListener(this);
        langItem=new MyResideMenuItem(this , uyFace,  translate,getResources().getString(R.string.translate_title));
        langItem.setOnClickListener(this);
        compItem=new MyResideMenuItem(this , uyFace,  compass,getResources().getString(R.string.compass_title));
        compItem.setOnClickListener(this);
        resideMenu.addMenuItem(homeItem,  ResideMenu.DIRECTION_RIGHT); // or  ResideMenu.DIRECTION_RIGHT
        resideMenu.addMenuItem(cityItem,  ResideMenu.DIRECTION_RIGHT); // or  ResideMenu.DIRECTION_RIGHT
        resideMenu.addMenuItem(langItem,  ResideMenu.DIRECTION_RIGHT); // or  ResideMenu.DIRECTION_RIGHT
        resideMenu.addMenuItem(compItem,  ResideMenu.DIRECTION_RIGHT); // or  ResideMenu.DIRECTION_RIGHT
        resideMenu.addMenuItem(settItem,  ResideMenu.DIRECTION_RIGHT); // or  ResideMenu.DIRECTION_RIGHT
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);
        resideMenu.setScaleValue(0.7f);
    }
    @Override
    public void onClick(View view) {
        Intent intent=null;
        if (view == homeItem){
            intent=new Intent(this,MainActivity.class);
        }else if (view == cityItem){
            intent=new Intent(this,CityActivity.class);
        }else if (view == settItem){
            intent=new Intent(this,SettingActivity.class);
        }else if (view == compItem){
            intent=new Intent(this,CompassActivity.class);
        }else if (view == langItem){
            intent=new Intent(this,TranslateActivity.class);
        }
        startActivity(intent);
    }
    public void showSnack(View view,String content,int code){
        Snackbar snackbar = Snackbar.make(view, content, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setGravity(Gravity.RIGHT);
        textView.setTypeface(uyFace);
        textView.setTextColor(getResources().getColor(R.color.textcolor));
        switch (code){
            case 0: //错误，网络连接错误或者别的错误
                sbView.setBackgroundColor(getResources().getColor(R.color.snackbgerr));
                break;
            case 1: //常规
                sbView.setBackgroundColor(getResources().getColor(R.color.snackbgreg));
                break;
            case -1: //调试
                sbView.setBackgroundColor(getResources().getColor(R.color.snackbgdbg));
                break;
        }
        snackbar.show();
    }
    public void initPrefrence(){
        SharedPreferences pref=this.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=this.getSharedPreferences("settings",Context.MODE_PRIVATE).edit();
        int bar=pref.getInt("statusbar",-1);
        switch (bar){ // 0为开着状态
            case 0:
                MyNotificationManager.myNotify(this);
                break;
            case 1:
                MyNotificationManager.closeNotification(this);
                break;
            case -1:
                editor.putInt("statusbar",0);
                editor.commit();
                MyNotificationManager.myNotify(this);
                break;
        }
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
            lang=0;
        } else if (language == 1) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
            lang=1;
        } else if (language == -1) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        }
        resources.updateConfiguration(config, dm);
    }
}

