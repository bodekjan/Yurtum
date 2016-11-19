package com.bodekjan.uyweather.activities;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.fragments.AdFragment;
import com.bodekjan.uyweather.fragments.CommentFragment;
import com.bodekjan.uyweather.fragments.SettingFragment;
import com.bodekjan.uyweather.util.CommonHelper;
import com.mikepenz.iconics.view.IconicsImageView;
import com.mikepenz.iconics.view.IconicsTextView;
import com.special.ResideMenu.ResideMenu;

import np.TextView;

public class SettingActivity extends MyBaseActivity {
    private IconicsImageView mMenu;
    FragmentManager fragmentManager;
    private SettingFragment indexFragment;
    private CommentFragment commentFragment;
    private AdFragment adFragment;
    private TextView mTitle;
    public static final String INDEX="indexpage";
    public static final String COMMENT="commentpage";
    public static final String AD="commXAentpage";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setContentView(R.layout.activity_setting);
        mTitle=(TextView) findViewById(R.id.citytitle);
        fragmentManager=getSupportFragmentManager();
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
        setFragment(INDEX);
    }
    public void setFragment(String page){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (page){
            case INDEX:
                if(indexFragment==null) indexFragment= SettingFragment.newInstance();
                if (!indexFragment.isAdded()) {
                    transaction.replace(R.id.fragmentcontainer, indexFragment);
                    transaction.addToBackStack(INDEX);
                    transaction.commit();
                } else {
                    transaction.show(indexFragment);
                }
                break;
            case COMMENT:
                if(commentFragment==null) commentFragment= CommentFragment.newInstance();
                if (!commentFragment.isAdded()) {
                    transaction.replace(R.id.fragmentcontainer, commentFragment);
                    transaction.addToBackStack(COMMENT);
                    transaction.commit();
                } else {
                    transaction.show(commentFragment);
                }
                break;
            case AD:
                if(adFragment==null) adFragment= AdFragment.newInstance();
                if (!adFragment.isAdded()) {
                    transaction.replace(R.id.fragmentcontainer, adFragment);
                    transaction.addToBackStack(COMMENT);
                    transaction.commit();
                } else {
                    transaction.show(adFragment);
                }
                break;
            default:
                setFragment(INDEX);
        }
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
    public void changeTitle(String text){
        mTitle.setText(text);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if(fragmentManager.getBackStackEntryCount()>1){
                fragmentManager.popBackStack();
                changeTitle(getResources().getText(R.string.setting_title).toString());
                return true;
            }
        }
        finish();
        return super.onKeyDown(keyCode, event);
    }
}