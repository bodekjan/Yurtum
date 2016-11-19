package com.bodekjan.uyweather.util;

import android.widget.BaseAdapter;

/**
 * Created by bodekjan on 2016/9/8.
 */
public abstract class BaseSwipListAdapter extends BaseAdapter {

    public boolean getSwipEnableByPosition(int position){
        return true;
    }
}
