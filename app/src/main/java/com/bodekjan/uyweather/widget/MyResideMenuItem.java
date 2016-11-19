package com.bodekjan.uyweather.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bodekjan.uyweather.R;
import com.mikepenz.iconics.IconicsDrawable;
import com.special.ResideMenu.ResideMenuItem;

/**
 * Created by bodekjan on 2016/9/8.
 */
public class MyResideMenuItem extends ResideMenuItem {

    /** menu item  icon  */
    private ImageView iv_icon;
    /** menu item  title */
    private TextView tv_title;

    public MyResideMenuItem(Context context) {
        super(context);
        initViews(context);
    }

    public MyResideMenuItem(Context context, Typeface uyFace, IconicsDrawable icon, int title) {
        super(context);
        initViews(context);
        iv_icon.setImageDrawable(icon);
        tv_title.setText(title);
        tv_title.setTypeface(uyFace);
    }

    public MyResideMenuItem(Context context, Typeface uyFace, IconicsDrawable icon, String title) {
        super(context);
        initViews(context);
        iv_icon.setImageDrawable(icon);
        tv_title.setText(title);
        tv_title.setTypeface(uyFace);
    }

    private void initViews(Context context){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.residemenu_item, this);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setTextSize(14);
    }

    /**
     * set the icon color;
     *
     * @param icon
     */
    public void setIcon(int icon){
        iv_icon.setImageResource(icon);
    }

    /**
     * set the title with resource
     * ;
     * @param title
     */
    public void setTitle(int title){
        tv_title.setText(title);
    }

    /**
     * set the title with string;
     *
     * @param title
     */
    public void setTitle(String title){
        tv_title.setText(title);
    }
}