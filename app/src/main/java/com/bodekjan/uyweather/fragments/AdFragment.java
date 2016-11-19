package com.bodekjan.uyweather.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.activities.SettingActivity;
import com.bodekjan.uyweather.util.CommonHelper;
import com.bodekjan.uyweather.util.MyNotificationManager;
import com.neopixl.pixlui.components.button.Button;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bodekjan on 2016/9/14.
 */
public class AdFragment extends Fragment {
    LinearLayout adButton;
    ImageView adSwitch;
    String mPageName;
    public static AdFragment newInstance(){
        AdFragment fragment = new AdFragment();
//        if(point!=null){
//            Bundle bundle=new Bundle();
//            bundle.putSerializable("point",point);
//            fragment.setArguments(bundle);
//        }
        return fragment;
    }
    public AdFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageName="settingpage";
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ad, null);
        ((SettingActivity)getActivity()).changeTitle(getResources().getText(R.string.adv_title).toString());
        adButton=(LinearLayout) view.findViewById(R.id.setting_adswitch);
        adSwitch=(ImageView) view.findViewById(R.id.setting_ad_img);
        adButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref=getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=getActivity().getSharedPreferences("settings",Context.MODE_PRIVATE).edit();
                int ad=pref.getInt("ad",0); // 0 为显示
                switch (ad){
                    case 0:
                        editor.putInt("ad",1);
                        editor.commit();
                        adSwitch.setImageDrawable(getResources().getDrawable(R.drawable.closed));
                        //* 这里nodify通知栏
                        break;
                    case 1:
                        editor.putInt("ad",0);
                        adSwitch.setImageDrawable(getResources().getDrawable(R.drawable.opened));
                        editor.commit();
                        //* 这里nodify通知栏
                        break;
                }
            }
        });
        SharedPreferences.Editor editor=getActivity().getSharedPreferences("settings",Context.MODE_PRIVATE).edit();
        SharedPreferences pref=getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        int ad=pref.getInt("ad",-1);
        switch (ad){ // 0为开着状态
            case 0:
                adSwitch.setImageDrawable(getResources().getDrawable(R.drawable.opened));
                break;
            case 1:
                adSwitch.setImageDrawable(getResources().getDrawable(R.drawable.closed));
                break;
            case -1:
                adSwitch.setImageDrawable(getResources().getDrawable(R.drawable.opened));
                editor.putInt("ad",0);
                editor.commit();
                break;
        }
        return view;
    }
    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }
}
