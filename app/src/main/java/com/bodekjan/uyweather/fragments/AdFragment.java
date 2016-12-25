package com.bodekjan.uyweather.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.blankj.utilcode.utils.ImageUtils;
import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.activities.SettingActivity;
import com.bodekjan.uyweather.activities.SettingActivity;
import com.bodekjan.uyweather.util.CommonHelper;
import com.bodekjan.uyweather.util.MyNotificationManager;
import com.bodekjan.uyweather.widget.MultiImageSelectorX;
import com.neopixl.pixlui.components.button.Button;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

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

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by bodekjan on 2016/9/14.
 */
public class AdFragment extends Fragment {
    public static int MY_PERMISSIONS_REQUEST_READ_PHONE= 213;
    LinearLayout adButton;
    ImageView adSwitch;
    String mPageName;
    Button selectImage;
    ImageView localPic;
    LinearLayout localPicArea;
    public String imagePath="--";
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
                int ad=pref.getInt("wallpaper",0); // 0 为显示
                switch (ad){
                    case 0:
                        editor.putInt("wallpaper",1);
                        editor.commit();
                        adSwitch.setImageDrawable(getResources().getDrawable(R.drawable.closed));
                        localPicArea.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        editor.putInt("wallpaper",0);
                        adSwitch.setImageDrawable(getResources().getDrawable(R.drawable.opened));
                        editor.commit();
                        localPicArea.setVisibility(View.GONE);
                        break;
                }
            }
        });

        selectImage=(Button)view.findViewById(R.id.selectimage);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) )
                {
                    ActivityCompat.requestPermissions(((SettingActivity)getActivity()),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_PHONE);
                    // 这里加一下权限
                }else{
                    MultiImageSelectorX.create(getActivity())
                            .showCamera(false) // 是否显示相机. 默认为显示
                            .count(1) // 最大选择图片数量, 默认为9. 只有在选择模式为多选时有效
                            .single() // 单选模式
                            .start(AdFragment.this, 1000);
                }
            }
        });
        localPic=(ImageView)view.findViewById(R.id.localpic);
        localPicArea=(LinearLayout)view.findViewById(R.id.localpicarea);
        initSettings();
        return view;
    }
    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }
    public void initSettings(){
        SharedPreferences.Editor editor=getActivity().getSharedPreferences("settings",Context.MODE_PRIVATE).edit();
        SharedPreferences pref=getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        int ad=pref.getInt("wallpaper",-1);
        switch (ad){ // 0为开着状态
            case 0:
                adSwitch.setImageDrawable(getResources().getDrawable(R.drawable.opened));
                localPicArea.setVisibility(View.GONE);
                break;
            case 1:
                adSwitch.setImageDrawable(getResources().getDrawable(R.drawable.closed));
                localPicArea.setVisibility(View.VISIBLE);
                break;
            case -1:
                adSwitch.setImageDrawable(getResources().getDrawable(R.drawable.opened));
                editor.putInt("wallpaper",0);
                editor.commit();
                localPicArea.setVisibility(View.GONE);
                break;
        }
        String wallPic=pref.getString("wallpaperPic","--");
        if(!wallPic.equals("--")){
            try {
                Bitmap bitmap= BitmapFactory.decodeFile(wallPic);
                bitmap= ImageUtils.compressByScale(bitmap,640,1134);
                localPic.setImageBitmap(bitmap);
            }catch (Exception e){

            }

        }
    }
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);  //这个super可不能落下，否则可能回调不了

        switch(requestCode){
            case 1000:
                if(resultCode == getActivity().RESULT_OK){
                    List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    // 处理你自己的逻辑 ....
                    imagePath=path.get(0).toString();
                    Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
                    bitmap= ImageUtils.compressByScale(bitmap,640,1134);
                    localPic.setImageBitmap(bitmap);
                    SharedPreferences.Editor editor=getActivity().getSharedPreferences("settings",Context.MODE_PRIVATE).edit();
                    editor.putString("wallpaperPic",imagePath);
                    editor.commit();
                    ((SettingActivity)getActivity()).showSnack(localPicArea,getResources().getText(R.string.snack_sharesuccess).toString(),1);
                }
                break;
        }
    }
}
