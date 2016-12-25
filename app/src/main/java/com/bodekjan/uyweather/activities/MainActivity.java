package com.bodekjan.uyweather.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.blankj.utilcode.utils.ConvertUtils;
import com.blankj.utilcode.utils.ImageUtils;
import com.blankj.utilcode.utils.NetworkUtils;
import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.dialog.ShareDialog;
import com.bodekjan.uyweather.fragments.CityFragment;
import com.bodekjan.uyweather.model.GlobalCity;
import com.bodekjan.uyweather.model.PlaceLib;
import com.bodekjan.uyweather.service.LocalService;
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
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.wandoujia.ads.sdk.Ads;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
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
    public boolean isBannerReady=false;
    Drawable backDrawable;
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
        /* 准备图片 */
        SharedPreferences pref=getSharedPreferences("settings", Context.MODE_PRIVATE);
        String wallPic=pref.getString("wallpaperPic","--");
        int wall=pref.getInt("wallpaper",-1);
        if(wall==1 && !wallPic.equals("--")){
            try {
                Bitmap bitmap= BitmapFactory.decodeFile(wallPic);
                bitmap= ImageUtils.compressByScale(bitmap,640,1134);
                backDrawable=ConvertUtils.bitmap2Drawable(getResources(),bitmap);
            }catch (Exception e){
                backDrawable=null;
            }
        }
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
                /* 分享功能 */
                screenShot();
                ShareDialog.Builder builder = new ShareDialog.Builder(MainActivity.this);
                builder.setMessage("");
                builder.setTitle(getResources().getString(R.string.title_share));
                builder.create().show();

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
//    public Bitmap takeScreenshot() {
//        mCityPager.setDrawingCacheEnabled(true);
//        return mCityPager.getDrawingCache();
//    }
//获取是否存在NavigationBar
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }
    private void screenShot()
    {
        if(isBannerReady){
            if(bannerArea.getVisibility()==View.VISIBLE) bannerView.setVisibility(View.GONE);
        }
        // 获取状态栏高度
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int homeBarHeight=0;
        if(checkDeviceHasNavigationBar(this)){
            homeBarHeight=144;
        }
        //获取当前屏幕的大小
        int width = getWindow().getDecorView().getRootView().getWidth();
        int height = getWindow().getDecorView().getRootView().getHeight();
        //生成相同大小的图片
        Bitmap temBitmap = Bitmap.createBitmap( 480, 800, Bitmap.Config.ARGB_8888 );
        //找到当前页面的跟布局
        View view =  getWindow().getDecorView().getRootView();
        //设置缓存
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        //从缓存中获取当前屏幕的图片
        temBitmap = view.getDrawingCache();
        temBitmap = Bitmap.createBitmap(temBitmap, 0, statusBarHeight, width, height
                - statusBarHeight-homeBarHeight);
        Bitmap qrCode;
        SharedPreferences pref = getSharedPreferences("settings", Context.MODE_PRIVATE);
        int language = pref.getInt("lang", -1);
        if(language!=0){
            qrCode=BitmapFactory.decodeResource(getResources(), R.drawable.qrcodezh);
        }else {
            qrCode=BitmapFactory.decodeResource(getResources(), R.drawable.qrcode);
        }
        temBitmap=mergeBitmap_TB(temBitmap,qrCode,false);
        temBitmap=ImageUtils.compressByScale(temBitmap,600,1000);
        view.destroyDrawingCache();
        //输出到sd卡
        if (true) {
            File file = new File(CommonHelper.screenShotPic);
            try {
                File fPath = new File("/storage/emulated/0/yurtum");
                if (!file.exists()) {
                    fPath.mkdirs();
                    file.createNewFile();
                }
                FileOutputStream foStream = new FileOutputStream(file);
                temBitmap.compress(Bitmap.CompressFormat.PNG, 100, foStream);
                foStream.flush();
                foStream.close();
                qrCode.recycle();
                temBitmap.recycle();
            } catch (Exception e) {
                Log.i("Show", e.toString());
            }
        }
        if(isBannerReady){
            if(bannerArea.getVisibility()==View.GONE) bannerView.setVisibility(View.VISIBLE);
        }
    }
    public static Bitmap mergeBitmap_TB(Bitmap topBitmap, Bitmap bottomBitmap, boolean isBaseMax) {
        if (topBitmap == null || topBitmap.isRecycled()
                || bottomBitmap == null || bottomBitmap.isRecycled()) {
            return null;
        }
        int width = 0;
        if (isBaseMax) {
            width = topBitmap.getWidth() > bottomBitmap.getWidth() ? topBitmap.getWidth() : bottomBitmap.getWidth();
        } else {
            width = topBitmap.getWidth() < bottomBitmap.getWidth() ? topBitmap.getWidth() : bottomBitmap.getWidth();
        }
        Bitmap tempBitmapT = topBitmap;
        Bitmap tempBitmapB = bottomBitmap;

        if (topBitmap.getWidth() != width) {
            tempBitmapT = Bitmap.createScaledBitmap(topBitmap, width, (int)(topBitmap.getHeight()*1f/topBitmap.getWidth()*width), false);
        } else if (bottomBitmap.getWidth() != width) {
            tempBitmapB = Bitmap.createScaledBitmap(bottomBitmap, width, (int)(bottomBitmap.getHeight()*1f/bottomBitmap.getWidth()*width), false);
        }

        int height = tempBitmapT.getHeight() + tempBitmapB.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Rect topRect = new Rect(0, 0, tempBitmapT.getWidth(), tempBitmapT.getHeight());
        Rect bottomRect  = new Rect(0, 0, tempBitmapB.getWidth(), tempBitmapB.getHeight());

        Rect bottomRectT  = new Rect(0, tempBitmapT.getHeight(), width, height);

        canvas.drawBitmap(tempBitmapT, topRect, topRect, null);
        canvas.drawBitmap(tempBitmapB, bottomRect, bottomRectT, null);
        return bitmap;
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
        SharedPreferences pref=getSharedPreferences("settings", Context.MODE_PRIVATE);
        String wallPic=pref.getString("wallpaperPic","--");
        int wall=pref.getInt("wallpaper",-1);
        if(wall==1 && !wallPic.equals("--")){
            try {
                if(backDrawable==null){
                    Bitmap bitmap= BitmapFactory.decodeFile(wallPic);
                    bitmap= ImageUtils.compressByScale(bitmap,640,1134);
                    backDrawable=ConvertUtils.bitmap2Drawable(getResources(),bitmap);
                }
                if(backDrawable==null){
                    weatherBg.setBackgroundResource(resource);
                }else {
                    weatherBg.setBackground(backDrawable);
                }

            }catch (Exception e){
                weatherBg.setBackgroundResource(resource);
            }
        }else{
            weatherBg.setBackgroundResource(resource);
        }

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
                    isBannerReady=true;
                }
            }
        }
    };
    @Override
    public void run()
    {
        Intent intent = new Intent(this,WeatherService.class);
        startService(intent);
        Intent intentTime = new Intent(this, LocalService.class);
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
