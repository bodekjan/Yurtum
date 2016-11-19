package com.bodekjan.uyweather.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bodekjan on 2016/9/6.
 */
public class OneDay implements Serializable {
    public String wTime; //城市名称
    public String wCode; //天气编号
    public String wTxt; //天气编号描述
    public String minTmp; //最低温度
    public String maxTmp; //最高温度
}
