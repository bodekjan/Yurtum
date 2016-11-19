package com.bodekjan.uyweather.activities;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.blankj.utilcode.utils.NetworkUtils;
import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.service.TimeService;
import com.bodekjan.uyweather.service.WeatherService;
import com.bodekjan.uyweather.util.CommonHelper;
import com.mikepenz.iconics.view.IconicsImageView;
import com.special.ResideMenu.ResideMenu;
import com.wandoujia.ads.sdk.Ads;

public class TranslateActivity extends MyBaseActivity implements Runnable{
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 2;
    private IconicsImageView mMenu;
    Button translateButton;
    EditText translateText;
    TextView translateValue;
    View bannerView;
    FrameLayout bannerArea;
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
        setContentView(R.layout.activity_translate);
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
        translateButton = (Button) findViewById(R.id.translatesubmit);
        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Boolean>() {
                    String result;
                    @Override
                    protected void onPreExecute() {
                        result = translateText.getText().toString();
                    }
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        try {
                            result = CommonHelper.globalTranslate(result);
                            return true;
                        } catch (Exception e) {
                            return false;
                        }
                    }
                    @Override
                    protected void onPostExecute(Boolean success) {
                        if (success) {
                            if(result.equals("")){
                                translateValue.setText("aaaaaaaaaaaaaaaa");
                            }else {
                                translateValue.setText(result);
                            }
                        } else {
                            translateValue.setText("aaaaaaaaaaaaaaaa");
                        }
                    }
                }.execute();
            }
        });
        translateText = (EditText) findViewById(R.id.translatetext);
        translateText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                translateText.setGravity(Gravity.LEFT);
                translateText.setHint("");
                return false;
            }
        });
        translateValue = (TextView) findViewById(R.id.translateresult);
        Thread thread=new Thread(this);
        thread.start();
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
    final Handler UploadData = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            //广告的
            if(msg.what == 1){
                if(NetworkUtils.isConnected(TranslateActivity.this)){
                    //Ads.showInterstitial(MainActivity.this, CommonHelper.INTERSTITIAL);
                    final ViewGroup container = (ViewGroup) findViewById(R.id.bannerview);
                    bannerArea = (FrameLayout) findViewById(R.id.bannerarea);
                    bannerArea.setVisibility(View.VISIBLE);
                    bannerView = Ads.createBannerView(TranslateActivity.this, CommonHelper.BANNER);
                    container.addView(bannerView, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));
                    bannerView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(event.getAction()==event.ACTION_DOWN){
                                bannerArea.setVisibility(View.INVISIBLE);
                            }
                            return false;
                        }
                    });
                }
            }
        }
    };
    @Override
    public void run()
    {
        Intent intent = new Intent(this,WeatherService.class);
        startService(intent);
        Intent intentTime = new Intent(this, TimeService.class);
        this.startService(intentTime);
        SharedPreferences pref=TranslateActivity.this.getSharedPreferences("settings", Context.MODE_PRIVATE);
        int ad=pref.getInt("ad",-1); // 0 为显示
        if(ad!=-1){
            if(NetworkUtils.isConnected(TranslateActivity.this)){
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        try {
                            if ((ContextCompat.checkSelfPermission(TranslateActivity.this,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) )
                            {
                                ActivityCompat.requestPermissions(TranslateActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                            } else
                            {
                                SharedPreferences pref=TranslateActivity.this.getSharedPreferences("settings", Context.MODE_PRIVATE);
                                int ad=pref.getInt("ad",-1);
                                if(ad!=1){
                                    Ads.init(TranslateActivity.this, CommonHelper.APP_ID, CommonHelper.SECRET_KEY);
                                    return true;
                                }
                                return false;
                            }
                            return false;
                        } catch (Exception e) {
                            Log.e("ads-sample", "error", e);
                            return false;
                        }
                    }
                    @Override
                    protected void onPostExecute(Boolean success) {
                        if (success) {
                            /**
                             * pre load
                             */
                            //Ads.preLoad(CommonHelper.INTERSTITIAL, Ads.AdFormat.interstitial);
                            Ads.preLoad(CommonHelper.BANNER, Ads.AdFormat.banner);
                            Message message = new Message();
                            message.what = 1;
                            UploadData.sendMessage(message);
                        } else {
                        }
                    }
                }.execute();
            }
        }
    }
}
