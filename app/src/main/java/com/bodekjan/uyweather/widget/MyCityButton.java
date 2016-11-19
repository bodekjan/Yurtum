package com.bodekjan.uyweather.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by bodekjan on 2016/9/11.
 */
public class MyCityButton extends LinearLayout {
    public String getParentCity() {
        return parentCity;
    }

    public void setParentCity(String parentCity) {
        this.parentCity = parentCity;
    }

    private String parentCity;
    public MyCityButton(Context context) {
        super(context);
    }

    public MyCityButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCityButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
