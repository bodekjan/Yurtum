package com.bodekjan.uyweather.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.blankj.utilcode.utils.StringUtils;
import com.bodekjan.uyweather.util.CommonHelper;
import com.bodekjan.uyweather.util.MyDatabaseHelper;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bodekjan on 2016/9/6.
 */
public class NewsLib {
    private MyDatabaseHelper dbHelper;
    private ArrayList<OneNews> mNewsList;
    private Context mContext;
    private static NewsLib mEdataLib;
    private OnePlace newCity;
    private NewsLib(Context context) throws ParseException {
        mContext=context;
        mNewsList=new ArrayList<OneNews>();
        dbHelper= new MyDatabaseHelper(mContext,"weather.db",null, CommonHelper.dbVersion);
        initData();
    }
    private void initData() throws ParseException {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("news",null,null,null,null,null,null);
        if(cursor.getCount()==0){
            cursor.close();
            sqLiteDatabase.close();
            return;
        }
        mNewsList.clear();
        if(cursor.moveToFirst()){
            do {
                OneNews news=new OneNews();
                news.link=cursor.getString(cursor.getColumnIndex("link"));
                news.text=cursor.getString(cursor.getColumnIndex("newstext"));
                mNewsList.add(news);
            }while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
    }
    public static NewsLib get(Context context) throws ParseException {
        if(mEdataLib==null){
            mEdataLib = new NewsLib(context);
        }
        return mEdataLib;
    }
    public ArrayList<OneNews> getNews(){
        return mNewsList;
    }
    public void refreshNews(){
        new Thread(NewNewsInit).start();
    }
    Runnable NewNewsInit = new Runnable() {
        @Override
        public void run() {
            SQLiteDatabase db=dbHelper.getWritableDatabase();
            try {
                /* 检查重复 */
                List<OneNews> newsList=new ArrayList<OneNews>();
                newsList=CommonHelper.getNews();
                if(newsList.size()==0){
                    return;
                }
                ContentValues values=new ContentValues();
                db.execSQL("delete from news");
                for(int i=0; i<newsList.size();i++){
                    values.put("link",newsList.get(i).link);
                    values.put("newstext",newsList.get(i).text);
                    db.insert("news",null,values);
                }
                db.close();
            } catch (Exception e) {
                db.close();
            }
        }
    };
}
