package com.bodekjan.uyweather.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by bodekjan on 2016/9/6.
 */
public class OnePlace implements Serializable {
    public int status;
    public String city; //城市名称
    public String uyCity; //维语名称
    public String cityId; //城市编号
    public String cityPinyin; //城市编号
    public String quickDate; //快速更新日期
    public String quickTime; //快速更新时间
    public String slowDateTime; //慢速更新时间
    public String wCode; //天气编号
    public String wTxt; //天气编号描述
    public String hum; //湿度
    public String curTmp; //当前温度
    public String curStatus; //当前温度
    public String windSpd; //风速
    public String minTmp; //最低温度
    public String maxTmp; //最高温度
    public String pm25; //pm2.5值
    public String aqi; //空气质量
    public String rise; //太阳出来
    public String set; //太阳下来
    public String todayComf; //舒适度
    public String todayCw; //洗车
    public String todayDrsg; //衣服指数
    public String todayFlu; //感冒
    public String todaySport; //运动指数
    public String todayTrav; //旅游指数
    public String todayUv; //紫外线
    public boolean detailService;
    public boolean xmlService;
    public boolean checkService;
    public ArrayList<OneDay> sevenDay=new ArrayList<OneDay>();
    public ArrayList<OneHour> dayDetail=new ArrayList<OneHour>();
}
