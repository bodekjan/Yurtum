package com.bodekjan.uyweather.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by bodekjan on 2016/6/8.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_PLACE="create table place("
            +"id integer primary key autoincrement,"
            +"status integer,"
            +"city varchar(255),"
            +"uyCity varchar(255),"
            +"cityId varchar(255),"
            +"cityPinyin varchar(255),"
            +"quickDate varchar(255),"
            +"quickTime varchar(255),"
            +"slowDateTime varchar(255),"
            +"wCode varchar(255),"
            +"wTxt varchar(255),"
            +"hum varchar(255),"
            +"curTmp varchar(255),"
            +"curStatus varchar(255),"
            +"minTmp varchar(255),"
            +"maxTmp varchar(255),"
            +"windSpd varchar(255),"
            +"pm25 varchar(255),"
            +"aqi varchar(255),"
            +"sunrise varchar(255),"
            +"sunset varchar(255),"
            +"todayComf varchar(255),"
            +"todayCw varchar(255),"
            +"todayDrsg varchar(255),"
            +"todayFlu varchar(255),"
            +"todaySport varchar(255),"
            +"todayTrav varchar(255),"
            +"todayUv varchar(255))";
    public static final String CREATE_DAYS="create table days("
            +"id integer primary key autoincrement,"
            +"cityId varchar(255),"
            +"wTime varchar(255),"
            +"wCode varchar(255),"
            +"wTxt varchar(255),"
            +"minTmp varchar(255),"
            +"maxTmp varchar(255))";
    public static final String CREATE_HOURS="create table hours("
            +"id integer primary key autoincrement,"
            +"cityId varchar(255),"
            +"wTime varchar(255),"
            +"minTmp varchar(255),"
            +"maxTmp varchar(255))";
    public static final String CREATE_NEWS="create table news("
            +"id integer primary key autoincrement,"
            +"link varchar(255),"
            +"newstext varchar(255))";
    private Context mContext;
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PLACE);
        db.execSQL(CREATE_DAYS);
        db.execSQL(CREATE_HOURS);
        db.execSQL(CREATE_NEWS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists place");
        db.execSQL("drop table if exists days");
        db.execSQL("drop table if exists hours");
        db.execSQL("drop table if exists news");
        onCreate(db);
    }
}
