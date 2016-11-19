package com.bodekjan.uyweather.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.blankj.utilcode.utils.SizeUtils;
import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.model.OnePlace;
import com.bodekjan.uyweather.model.PlaceLib;
import com.bodekjan.uyweather.util.BaseSwipListAdapter;
import com.bodekjan.uyweather.util.CommonHelper;
import com.bodekjan.uyweather.util.MyNotificationManager;
import com.mikepenz.iconics.view.IconicsImageView;
import com.special.ResideMenu.ResideMenu;

import java.text.ParseException;
import java.util.ArrayList;

public class CityActivity extends MyBaseActivity {
    private IconicsImageView mMenu;
    PlaceLib mPlaceList;
    private ArrayList<OnePlace> cityList;
    AppAdapter listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLanguage();
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(android.R.color.holo_blue_light));
        }
        setContentView(R.layout.activity_city);
        uyFace = Typeface.createFromAsset(getAssets(), "fonts/ALKATIP.TTF");
        mMenu=(IconicsImageView)findViewById(R.id.citymenu);
        mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resideMenu.isOpened()){
                    resideMenu.closeMenu();
                }else{
                    resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT); // or ResideMenu.DIRECTION_RIGHT
                }
            }
        });
        initMenu();
        try {
            mPlaceList= PlaceLib.get(this);
            cityList=mPlaceList.getCitys();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /* 城市列表操作 */
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                openItem.setBackground(new ColorDrawable(getResources().getColor(android.R.color.holo_blue_dark)));
                openItem.setWidth(SizeUtils.dp2px(CityActivity.this,90));
                openItem.setIcon(R.mipmap.home);
                openItem.setTitleColor(getResources().getColor(android.R.color.holo_blue_light));
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(android.R.color.holo_red_dark)));
                deleteItem.setWidth(SizeUtils.dp2px(CityActivity.this,90));
                deleteItem.setIcon(R.mipmap.delete);
                menu.addMenuItem(deleteItem);
                menu.addMenuItem(openItem);
            }
        };
        listView=(SwipeMenuListView)findViewById(R.id.citylist);
        // set creator
        listView.setMenuCreator(creator);
        listAdapter=new AppAdapter();
        listView.setAdapter(listAdapter);
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 1:
                        mPlaceList.changeHome(cityList.get(position).cityId);
                        cityList=mPlaceList.getCitys();
                        listAdapter.notifyDataSetChanged();
                        Intent intent = new Intent();
                        intent.setAction(WIDGETUPDATE);
                        sendBroadcast(intent);
                        /* 状态栏修好 */
                        SharedPreferences pref=getSharedPreferences("settings", Context.MODE_PRIVATE);
                        int statusbar=pref.getInt("statusbar",0); // 0 为显示
                        switch (statusbar){
                            case 0:
                                //* 这里nodify通知栏
                                MyNotificationManager.myNotify(CityActivity.this,null);

                                break;
                            case 1:
                                MyNotificationManager.closeNotification(CityActivity.this);
                                break;
                        }
                        break;
                    case 0:
                        if(cityList.get(position).status==1){
                            showSnack(listView,getResources().getText(R.string.snack_homeerr).toString(),0);
                            break;
                        }
                        mPlaceList.removeCity(cityList.get(position).cityId);
                        cityList=mPlaceList.getCitys();
                        listAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
        showSnack(listView,getResources().getText(R.string.snack_swip).toString(),1);
    }
    @Override
    public void onResume() {
        CommonHelper.activities.add(this);
        super.onResume();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(resideMenu.isOpened())
            resideMenu.closeMenu();
    }
    class AppAdapter extends BaseSwipListAdapter {
        @Override
        public int getCount() {
            return cityList.size();
        }
        @Override
        public OnePlace getItem(int position) {
            return cityList.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.item_list_city, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            OnePlace item = getItem(position);
            if(lang==0){
                holder.tv_name.setText(item.uyCity);
            }else {
                holder.tv_name.setText(item.city);
            }

            if(item.status!=0){
                holder.home_icon.setVisibility(View.VISIBLE);
            }else{
                holder.home_icon.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }
        class ViewHolder {
            TextView tv_name;
            IconicsImageView home_icon;
            public ViewHolder(View view) {
                tv_name = (TextView) view.findViewById(R.id.listcity_name);
                home_icon = (IconicsImageView) view.findViewById(R.id.cityhomeicon);
                view.setTag(this);
            }
        }
        @Override
        public boolean getSwipEnableByPosition(int position) {
            if(position % 2 == 0){
                return false;
            }
            return true;
        }
    }
}
