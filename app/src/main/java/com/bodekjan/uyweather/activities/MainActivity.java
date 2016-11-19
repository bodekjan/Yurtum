package com.bodekjan.uyweather.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.blankj.utilcode.utils.ImageUtils;
import com.blankj.utilcode.utils.NetworkUtils;
import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.fragments.CityFragment;
import com.bodekjan.uyweather.model.GlobalCity;
import com.bodekjan.uyweather.model.OneDay;
import com.bodekjan.uyweather.model.OnePlace;
import com.bodekjan.uyweather.model.PlaceLib;
import com.bodekjan.uyweather.model.WeatherStatus;
import com.bodekjan.uyweather.service.TimeService;
import com.bodekjan.uyweather.service.WeatherService;
import com.bodekjan.uyweather.util.CommonHelper;
import com.bodekjan.uyweather.util.LocationFinder;
import com.bodekjan.uyweather.util.MyNotificationManager;
import com.bodekjan.uyweather.util.WeatherTranslator;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.iconics.view.IconicsImageView;
import com.mikepenz.iconics.view.IconicsTextView;
import com.special.ResideMenu.ResideMenu;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.wandoujia.ads.sdk.Ads;

import java.text.ParseException;
import java.util.ArrayList;

public class MainActivity extends MyBaseActivity implements Runnable{
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 2;
    private static final String NEWCITY = "com.bodekjan.dataallready";
    private static final String QUICKCITY = "com.bodekjan.quickweatherupdate";
    private static final String NETERROR = "com.bodekjan.servererror";
    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    CityFragment currentFragment;
    private ViewPager mCityPager;
    FragmentAdapter pagerAdapter;
    PlaceLib places;
    private IconicsTextView mTitle;
    private IconicsImageView mMenu;
    private IconicsImageView mAdd;
    private IconicsImageView mShare;
    private long exitTime = 0;
    FrameLayout weatherBg;
    View bannerView;
    FrameLayout bannerArea;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate())); /*为了加速启动*/
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
        }
        setContentView(R.layout.activity_main);
        uyFace = Typeface.createFromAsset(getAssets(), "fonts/ALKATIP.TTF");
        initLayout();
        Thread thread=new Thread(this);
        thread.start();
        /* 统计 */
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }
    public void initLayout(){
        super.initMenu();
        weatherBg=(FrameLayout)findViewById(R.id.weatherbg);
        mCityPager=(ViewPager)findViewById(R.id.citypager);
        mTitle=(IconicsTextView)findViewById(R.id.citytitle);
        mTitle.setTypeface(uyFace);
        mMenu=(IconicsImageView)findViewById(R.id.menu);
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
        mShare=(IconicsImageView)findViewById(R.id.share);
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref=MainActivity.this.getSharedPreferences("settings", Context.MODE_PRIVATE);
                int active=pref.getInt("active",0);
                if(active==0){
                    MainActivity.this.showSnack(mCityPager,getResources().getText(R.string.snack_active).toString(),0);
                    return;
                }
                OnePlace onePlace=places.getCitys().get(mCityPager.getCurrentItem());
                IWXAPI wxapi = WXAPIFactory.createWXAPI(MainActivity.this, "wx8165366cc17f9522");
                String cityName=getResources().getString(R.string.value_wplease);
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = CommonHelper.path;
                WXMediaMessage msg = new WXMediaMessage(webpage);
                WeatherStatus status= WeatherTranslator.weatherTextTranslator(MainActivity.this,onePlace.curStatus);
                msg.title = String.format(cityName, onePlace.uyCity.replace(" ","") , onePlace.curTmp, status.getuText());
                msg.description = "";
                Bitmap thumb = BitmapFactory.decodeResource(getResources(),status.getIconId());
                thumb=CommonHelper.getWechatWhite(thumb);
                msg.thumbData= ImageUtils.bitmap2Bytes(thumb, Bitmap.CompressFormat.PNG);
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = String.valueOf(System.currentTimeMillis());
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                wxapi.sendReq(req);
            }
        });
        mAdd=(IconicsImageView)findViewById(R.id.add);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddCityActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }
    private void initViewPager(int index){
        try {
            places=PlaceLib.get(this);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mFragmentList.clear();
        int homeIndex=0;
        if(places.getCitys().size()==0){ //没有选择城市的话
            new FindFristCity().execute("aaa");
            return;
        }
        for(int i=0; i<places.getCitys().size();i++){
            mFragmentList.add(CityFragment.newInstance(places.getCitys().get(i)));
            if(places.getCitys().get(i).status==1){
                homeIndex=i;
            }
        }
        /* 如果没数据，就显示空的 */
        if(places.getCitys().size()==0){
            mFragmentList.add(CityFragment.newInstance(null));
        }

        pagerAdapter=new FragmentAdapter(getSupportFragmentManager(), mFragmentList);

        mCityPager.setAdapter(pagerAdapter);
        if(index!=-1){
            mCityPager.setCurrentItem(index);
            if(lang==0){
                changeTitle(places.getCitys().get(index).uyCity,places.getCitys().get(index).status, WeatherTranslator.weatherTextTranslator(MainActivity.this,places.getCitys().get(index).curStatus).getBgCode());
            }else {
                changeTitle(places.getCitys().get(index).city,places.getCitys().get(index).status, WeatherTranslator.weatherTextTranslator(MainActivity.this,places.getCitys().get(index).curStatus).getBgCode());
            }
        }else if(places.getCitys().size()==0){
            mCityPager.setCurrentItem(homeIndex);
        }else {
            mCityPager.setCurrentItem(homeIndex);
            if(lang==0){
                changeTitle(places.getCitys().get(homeIndex).uyCity,places.getCitys().get(homeIndex).status, WeatherTranslator.weatherTextTranslator(MainActivity.this,places.getCitys().get(homeIndex).curStatus).getBgCode());
            }else {
                changeTitle(places.getCitys().get(homeIndex).city,places.getCitys().get(homeIndex).status, WeatherTranslator.weatherTextTranslator(MainActivity.this,places.getCitys().get(homeIndex).curStatus).getBgCode());
            }
        }
        mCityPager.setPageTransformer(true, new CubeOutTransformer());
        mCityPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                currentFragment=(CityFragment) MainActivity.this.mFragmentList.get(position);
                changeTitle(currentFragment.getFragmentTitle(),currentFragment.getFragmentStatus(), WeatherTranslator.weatherTextTranslator(MainActivity.this,currentFragment.getFragmentBg()).getBgCode());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void changeTitle(String text,int isHome,int resource){
        if(isHome==1){
            mTitle.setText(text+" {gmi-home}");
        }else {
            mTitle.setText(text);
        }
        weatherBg.setBackgroundResource(resource);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }
    /* fragment adapter */
    class FragmentAdapter extends FragmentStatePagerAdapter
    {
        private ArrayList<Fragment> fragmentsList;
        public FragmentAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragmentsList = fragments;
        }
        @Override
        public int getCount() {
            return fragmentsList.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragmentsList.get(arg0);
        }
        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }
        /**
         * 每次更新完成ViewPager的内容后，调用该接口，此处复写主要是为了让导航按钮上层的覆盖层能够动态的移动
         */
        @Override
        public void finishUpdate(ViewGroup container)
        {
            super.finishUpdate(container);//这句话要放在最前面，否则会报错
            //获取当前的视图是位于ViewGroup的第几个位置，用来更新对应的覆盖层所在的位置
            currentFragment=(CityFragment) MainActivity.this.mFragmentList.get(mCityPager.getCurrentItem());
//            changeTitle(currentFragment.getFragmentTitle(),currentFragment.getFragmentStatus());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0 && resultCode == 0) {
            if(intent==null){
                return;
            }
            Bundle bundleData = intent.getExtras();
            GlobalCity resultCity = (GlobalCity) bundleData.getSerializable("city");
            places.setCity(resultCity);
            MainActivity.this.showSnack(mCityPager,getResources().getText(R.string.snack_newcity).toString(),1);
        }
    }
    @Override
    public void onPause(){
        this.unregisterReceiver(newCityReceiver);
        this.unregisterReceiver(quickCityReceiver);
        this.unregisterReceiver(netErrorReceiver);
        MobclickAgent.onPause(this);
        super.onPause();
    }
    @Override
    public void onResume() {
        CommonHelper.activities.add(this);
        IntentFilter filter_dynamic = new IntentFilter();
        filter_dynamic.addAction(NEWCITY);
        this.registerReceiver(newCityReceiver, filter_dynamic);
        IntentFilter filter_dynamic2 = new IntentFilter();
        filter_dynamic2.addAction(QUICKCITY);
        this.registerReceiver(quickCityReceiver, filter_dynamic2);
        IntentFilter filter_dynamic3 = new IntentFilter();
        filter_dynamic3.addAction(NETERROR);
        this.registerReceiver(netErrorReceiver, filter_dynamic3);
        initViewPager(-1);
        MobclickAgent.onResume(this);
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(resideMenu.isOpened())
            resideMenu.closeMenu();
    }

    /* receivers */
    private BroadcastReceiver newCityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(NEWCITY)) {
                initViewPager(mFragmentList.size());
                mCityPager.setCurrentItem(mFragmentList.size());
                Intent intentWidget = new Intent();
                intentWidget.setAction(WIDGETUPDATE);
                sendBroadcast(intentWidget);
                SharedPreferences pref=MainActivity.this.getSharedPreferences("settings", Context.MODE_PRIVATE);
                int bar=pref.getInt("statusbar",-1);
                if (bar==0){
                    MyNotificationManager.myNotify(MainActivity.this,null);
                }
            }
        }
    };
    /* receivers */
    private BroadcastReceiver quickCityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(QUICKCITY)) {
                int pageIndex=mCityPager.getCurrentItem();
                initViewPager(pageIndex);
                Intent intentWidget = new Intent();
                intentWidget.setAction(WIDGETUPDATE);
                sendBroadcast(intentWidget);
            }
        }
    };
    /* receivers */
    private BroadcastReceiver netErrorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(NETERROR)) {
                if(currentFragment!=null)
                    currentFragment.closeUploadIcon();
                MainActivity.this.showSnack(mCityPager,getResources().getText(R.string.snack_servererr).toString(),0);
            }
        }
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                showSnack(mTitle,getResources().getText(R.string.snack_quit).toString(),1);
                exitTime = System.currentTimeMillis();
            } else {
                for(int i=0; i<CommonHelper.activities.size();i++){
                    CommonHelper.activities.get(i).finish();
                }
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private class FindFristCity extends AsyncTask<String, Integer, String> {
        GlobalCity reCity=new GlobalCity();
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
        }
        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            if(!NetworkUtils.isConnected(MainActivity.this)){
                return "neterr";
            }
            String ip= LocationFinder.getIp();
            reCity=LocationFinder.getCity(ip,MainActivity.this);
            return reCity.zhName;
        }
        //onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(Integer... progresses) {
        }
        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String result) {
            if(result!=null){
                if(result.equals("err")){
                    showSnack(mCityPager,getResources().getText(R.string.snack_locationerr).toString(),0);
                    GlobalCity thisCity=new GlobalCity();
                    thisCity.zhName=getResources().getText(R.string.default_zhcity).toString();
                    thisCity.uyName=getResources().getText(R.string.default_uycity).toString();
                    thisCity.pin="----";
                    places.setCity(thisCity);
                }else if(result.equals("neterr")){
                    showSnack(mCityPager,getResources().getText(R.string.snack_neterr).toString(),0);
                }else{
                    GlobalCity thisCity=new GlobalCity();
                    thisCity.zhName=result;
                    thisCity.uyName=reCity.uyName;
                    if(reCity.uyName.equals("")){
                        thisCity.zhName=getResources().getText(R.string.default_zhcity).toString();
                        thisCity.uyName=getResources().getText(R.string.default_uycity).toString();
                        showSnack(mCityPager,getResources().getText(R.string.snack_locationerr).toString(),0);
                    }
                    thisCity.pin="----";
                    places.setCity(thisCity);
                }
            }
        }
        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {

        }
    }
    final Handler UploadData = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what == 2){
                if(NetworkUtils.isWifiConnected(MainActivity.this)){
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                }
            }
             //广告的
            if(msg.what == 1){
                if(NetworkUtils.isConnected(MainActivity.this)){
                    //Ads.showInterstitial(MainActivity.this, CommonHelper.INTERSTITIAL);
                    final ViewGroup container = (ViewGroup) findViewById(R.id.bannerview);
                    bannerArea = (FrameLayout) findViewById(R.id.bannerarea);
                    bannerArea.setVisibility(View.VISIBLE);
                    bannerView = Ads.createBannerView(MainActivity.this, CommonHelper.BANNER);
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
        SharedPreferences pref=MainActivity.this.getSharedPreferences("settings", Context.MODE_PRIVATE);
        double version=Double.valueOf(pref.getString("version",CommonHelper.appVersion));
        double myVersion=Double.valueOf(CommonHelper.appVersion);
        if(version>myVersion){
            Message message = new Message();
            message.what = 2;
            UploadData.sendMessage(message);
        }
        int ad=pref.getInt("ad",-1); // 0 为显示
        if(ad!=-1){
            if(NetworkUtils.isConnected(MainActivity.this)){
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        try {
                            if ((ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) )
                            {
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                            } else
                            {
                                SharedPreferences pref=MainActivity.this.getSharedPreferences("settings", Context.MODE_PRIVATE);
                                int ad=pref.getInt("ad",-1);
                                if(ad!=1){
                                    Ads.init(MainActivity.this, CommonHelper.APP_ID, CommonHelper.SECRET_KEY);
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
