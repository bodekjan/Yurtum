package com.bodekjan.uyweather.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.utils.ImageUtils;
import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.activities.SettingActivity;
import com.bodekjan.uyweather.activities.WebActivity;
import com.bodekjan.uyweather.dialog.NewVerDialog;
import com.bodekjan.uyweather.dialog.SelectLanguageDialog;
import com.bodekjan.uyweather.dialog.SelectZoneDialog;
import com.bodekjan.uyweather.util.CommonHelper;
import com.bodekjan.uyweather.util.MyNotificationManager;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.utils.Utils;
import com.mikepenz.iconics.view.IconicsImageView;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.Locale;

/**
 * Created by bodekjan on 2016/9/14.
 */
public class SettingFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    public static int REQUEST_LANG = 1;
    public static String DIALOG_LANG = "asgsgfgawfasgas";
    public static int REQUEST_NEW = 2;
    public static String DIALOG_NEW = "asgsgfsgasgasggawfasgas";
    public static int REQUEST_ZONE = 3;
    public static String DIALOG_ZONE = "asgsgfsdfgsffasgasggawfasgas";
    LinearLayout languageButton;
    TextView languageText;
    TextView zoneText;
    LinearLayout statusButton;
    LinearLayout zoneButton;
    LinearLayout aboutActiveButton;
    LinearLayout useButton;
    LinearLayout commentButton;
    LinearLayout moneyButton;
    LinearLayout aboutButton;
    LinearLayout versionButton;
    ImageView statusImage;
    TextView versionText;
    TextView newVersion;
    TextView activeStatus;
    Button wechatActive;
    IconicsImageView activeIcon;
    boolean haveNew=false;
    String mPageName;
    public static SettingFragment newInstance(){
        SettingFragment fragment = new SettingFragment();
//        if(point!=null){
//            Bundle bundle=new Bundle();
//            bundle.putSerializable("point",point);
//            fragment.setArguments(bundle);
//        }
        return fragment;
    }
    public SettingFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageName="settingpage";
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, null);
        wechatActive=(Button)view.findViewById(R.id.wechatactive);
        wechatActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) )
                {
                    SettingFragment.this.requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                } else
                {
                    active();
                }
            }
        });
        activeStatus=(TextView)view.findViewById(R.id.activestatus);
        activeIcon=(IconicsImageView)view.findViewById(R.id.activeicon);
        languageButton=(LinearLayout) view.findViewById(R.id.setting_language);
        languageText=(TextView)view.findViewById(R.id.setting_item_lang);
        languageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                SharedPreferences pref=getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
                SelectLanguageDialog dialog = SelectLanguageDialog.newInstance(pref.getInt("lang",0));
                dialog.setTargetFragment(SettingFragment.this, REQUEST_LANG);
                dialog.show(fm, DIALOG_LANG);
            }
        });
        zoneText=(TextView)view.findViewById(R.id.setting_item_zone);
        zoneButton=(LinearLayout) view.findViewById(R.id.setting_timezone);
        zoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                SharedPreferences pref=getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
                SelectZoneDialog dialog = SelectZoneDialog.newInstance(pref.getInt("timezone",0));
                dialog.setTargetFragment(SettingFragment.this, REQUEST_ZONE);
                dialog.show(fm, DIALOG_ZONE);
            }
        });
        statusButton=(LinearLayout) view.findViewById(R.id.setting_statusbar);
        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref=getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=getActivity().getSharedPreferences("settings",Context.MODE_PRIVATE).edit();
                int statusbar=pref.getInt("statusbar",0); // 0 为显示
                switch (statusbar){
                    case 0:
                        editor.putInt("statusbar",1);
                        editor.commit();
                        statusImage.setImageDrawable(getResources().getDrawable(R.drawable.closed));
                        //* 这里nodify通知栏
                        MyNotificationManager.closeNotification(getActivity());
                        break;
                    case 1:
                        editor.putInt("statusbar",0);
                        statusImage.setImageDrawable(getResources().getDrawable(R.drawable.opened));
                        editor.commit();
                        //* 这里nodify通知栏
                        MyNotificationManager.myNotify(getActivity());
                        break;
                }
            }
        });
        aboutActiveButton = (LinearLayout) view.findViewById(R.id.aboutactivebutton);
        aboutActiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),WebActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("url","file:///android_asset/active.html");
                bundle.putSerializable("title",getResources().getText(R.string.title_active).toString());
                i.putExtras(bundle);
                getActivity().startActivity(i);
            }
        });
        useButton = (LinearLayout) view.findViewById(R.id.usebutton);
        useButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),WebActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("url","file:///android_asset/manual.html");
                bundle.putSerializable("title",getResources().getText(R.string.title_learn).toString());
                i.putExtras(bundle);
                getActivity().startActivity(i);
            }
        });
        commentButton = (LinearLayout) view.findViewById(R.id.commentbutton);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SettingActivity)getActivity()).setFragment(SettingActivity.COMMENT);
            }
        });
        moneyButton = (LinearLayout) view.findViewById(R.id.moneybutton);
        moneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SettingActivity)getActivity()).setFragment(SettingActivity.AD);
//                Intent i=new Intent(getActivity(),WebActivity.class);
//                Bundle bundle=new Bundle();
//                bundle.putSerializable("url","file:///android_asset/money.html");
//                bundle.putSerializable("title",getResources().getText(R.string.title_money).toString());
//                i.putExtras(bundle);
//                getActivity().startActivity(i);
            }
        });
        aboutButton = (LinearLayout) view.findViewById(R.id.aboutbutton);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),WebActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("url","file:///android_asset/about.html");
                bundle.putSerializable("title",getResources().getText(R.string.title_about).toString());
                i.putExtras(bundle);
                getActivity().startActivity(i);
            }
        });
        versionButton = (LinearLayout) view.findViewById(R.id.versionbutton);
        versionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(haveNew){
                    SharedPreferences pref=getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
                    String version=pref.getString("version",CommonHelper.appVersion);
                    android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                    NewVerDialog dialog = NewVerDialog.newInstance(0);
                    dialog.setTargetFragment(SettingFragment.this, REQUEST_NEW);
                    dialog.show(fm, DIALOG_NEW);
                    return;
                }
                ((SettingActivity)getActivity()).showSnack(versionButton,getResources().getText(R.string.snack_newst).toString(),1);
            }
        });
        statusImage=(ImageView) view.findViewById(R.id.setting_status_img);
        versionText=(TextView)view.findViewById(R.id.setting_item_version);
        newVersion=(TextView)view.findViewById(R.id.newversiontext);
        return view;
    }
    private void initSettings(){
        SharedPreferences pref=getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=getActivity().getSharedPreferences("settings",Context.MODE_PRIVATE).edit();
//        Log.e("====","有配置!");
        editor.putInt("active",1); //暂时允许，下个版本关掉
        editor.commit();
        int active=pref.getInt("active",0);
        if(active==1){
            wechatActive.setVisibility(View.INVISIBLE);
            activeStatus.setText(getResources().getText(R.string.value_validate));
            activeStatus.setTextColor(getResources().getColor(android.R.color.holo_green_light));
            IconicsDrawable drawable=new IconicsDrawable(getActivity())
                    .icon(MaterialDesignIconic.Icon.gmi_lock_open)
                    .color(Color.GREEN)
                    .sizeDp(24);
            activeIcon.setImageDrawable(drawable);
        }else {
            IconicsDrawable drawable=new IconicsDrawable(getActivity())
                    .icon(MaterialDesignIconic.Icon.gmi_lock)
                    .color(Color.RED)
                    .sizeDp(24);
            activeIcon.setImageDrawable(drawable);
        }
        int language=pref.getInt("lang",-1);
        switch (language){
            case 0:
                languageText.setText(getResources().getText(R.string.value_uyghur));
                break;
            case 1:
                languageText.setText(getResources().getText(R.string.value_chinese));
                break;
            case -1:
                android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                SelectLanguageDialog dialog = SelectLanguageDialog.newInstance(pref.getInt("lang",0));
                dialog.setTargetFragment(SettingFragment.this, REQUEST_LANG);
                dialog.show(fm, DIALOG_LANG);
                break;
        }
        int zone=pref.getInt("timezone",-1);
        switch (zone){ // 0为乌鲁木齐
            case 0:
                zoneText.setText(getResources().getText(R.string.default_zone6));
                break;
            case 1:
                zoneText.setText(getResources().getText(R.string.default_zone8));
                break;
            case -1:
                zoneText.setText(getResources().getText(R.string.default_zone6));
                editor.putInt("timezone",0);
                editor.commit();
                break;
        }
        int bar=pref.getInt("statusbar",-1);
        switch (bar){ // 0为开着状态
            case 0:
                statusImage.setImageDrawable(getResources().getDrawable(R.drawable.opened));
                MyNotificationManager.myNotify(getActivity());
                break;
            case 1:
                statusImage.setImageDrawable(getResources().getDrawable(R.drawable.closed));
                MyNotificationManager.closeNotification(getActivity());
                break;
            case -1:
                statusImage.setImageDrawable(getResources().getDrawable(R.drawable.opened));
                editor.putInt("statusbar",0);
                editor.commit();
                MyNotificationManager.myNotify(getActivity());
                break;
        }
        double version=Double.valueOf(pref.getString("version",CommonHelper.appVersion));
        double myVersion=Double.valueOf(CommonHelper.appVersion);
        if(version>myVersion){
            newVersion.setVisibility(View.VISIBLE);
            haveNew=true;
            ((SettingActivity)getActivity()).showSnack(moneyButton,getResources().getText(R.string.snack_newversion).toString(),0);
            if(haveNew){
                android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                NewVerDialog dialog = NewVerDialog.newInstance(0);
                dialog.setTargetFragment(SettingFragment.this, REQUEST_NEW);
                dialog.show(fm, DIALOG_NEW);
                return;
            }
        }
        versionText.setText(CommonHelper.appVersion);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SharedPreferences pref=getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=getActivity().getSharedPreferences("settings",Context.MODE_PRIVATE).edit();
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    Resources resources = getActivity().getResources();
                    DisplayMetrics dm = resources.getDisplayMetrics();
                    Configuration config = resources.getConfiguration();
                    Intent intent;
                    switch (data.getIntExtra(SelectLanguageDialog.EXTRA_DATA,0)){
                        case 0:
                            editor.putInt("lang",0);
                            editor.commit();
                            languageText.setText(getResources().getText(R.string.value_uyghur));
                            // 应用用户选择语言
                            config.locale = new Locale("uy");
                            resources.updateConfiguration(config, dm);
                            intent = new Intent(getActivity(), SettingActivity.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            // 杀掉进程
                            //android.os.Process.killProcess(android.os.Process.myPid());
                            //System.exit(0);
                            break;
                        case 1:
                            editor.putInt("lang",1);
                            editor.commit();
                            languageText.setText(getResources().getText(R.string.value_chinese));
                            // 应用用户选择语言
                            config.locale = Locale.ENGLISH;
                            resources.updateConfiguration(config, dm);
                            intent = new Intent(getActivity(), SettingActivity.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            // 杀掉进程
                            //android.os.Process.killProcess(android.os.Process.myPid());
                            //System.exit(0);
                            break;
                    }
                    Intent widget=new Intent();
                    widget.setAction("com.bodekjan.homechanged");
                    getActivity().sendBroadcast(widget);
                    return;
                }
                break;
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = Uri.parse(pref.getString("path",""));
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(intent);
                    return;
                }
                break;
            case 3:
                if (resultCode == Activity.RESULT_OK) {
                    switch (data.getIntExtra(SelectZoneDialog.EXTRA_DATA,0)){
                        case 0:
                            editor.putInt("timezone",0);
                            editor.commit();
                            zoneText.setText(getResources().getText(R.string.default_zone6));
                            break;
                        case 1:
                            editor.putInt("timezone",1);
                            editor.commit();
                            zoneText.setText(getResources().getText(R.string.default_zone8));
                            break;
                    }
                    return;
                }
                break;
            default:
                break;
        }
    }
    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        initSettings();
        super.onResume();
    }
    /* 微信宝宝 */
    private void active(){
        IWXAPI wxapi = WXAPIFactory.createWXAPI(getActivity(), "wx8165366cc17f9522");
        //shareText("eeeeeeeeeeeee",wxapi);
        shareWebPage(wxapi);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                active();
            } else
            {
                ((SettingActivity)getActivity()).showSnack(moneyButton,getResources().getText(R.string.snack_wechatpermission).toString(),0);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void shareText(String shareContent,  IWXAPI wxapi) {
        if (!TextUtils.isEmpty(shareContent)) {
            WXTextObject textObj = new WXTextObject();
            textObj.text = shareContent;

            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = textObj;
            // 发送文本类型的消息时，title字段不起作用
            // msg.title = "Title";
            msg.description = shareContent;
            // 构造一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = CommonHelper.buildTransaction("text");
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
            wxapi.sendReq(req);
        }
    }
    private void shareWebPage(IWXAPI wxapi) {
        if(!wxapi.isWXAppInstalled()){
            ((SettingActivity)getActivity()).showSnack(moneyButton,getResources().getText(R.string.snack_haswechat).toString(),0);
            return;
        }
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = CommonHelper.path;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = getResources().getString(R.string.default_wechat);
        msg.description = "";
        Bitmap thumb = BitmapFactory.decodeResource(getResources(),R.mipmap.wechaticon);
        msg.thumbData= ImageUtils.bitmap2Bytes(thumb, Bitmap.CompressFormat.PNG);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        wxapi.sendReq(req);
    }
}
