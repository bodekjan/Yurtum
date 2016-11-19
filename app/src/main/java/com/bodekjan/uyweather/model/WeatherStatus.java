package com.bodekjan.uyweather.model;

/**
 * Created by bodekjan on 2016/9/10.
 */
public class WeatherStatus {
    public String getuText() {
        return uText;
    }

    public void setuText(String uText) {
        this.uText = uText;
    }

    private String uText;

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    private int iconId;

    public int getBgCode() {
        return bgCode;
    }

    public void setBgCode(int bgCode) {
        this.bgCode = bgCode;
    }

    private int bgCode;
}
