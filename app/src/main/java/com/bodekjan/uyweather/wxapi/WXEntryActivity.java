package com.bodekjan.uyweather.wxapi;
import com.umeng.weixin.callback.WXCallbackActivity;

/**
 * Created by bodekjan on 2016/10/19.
 */
public class WXEntryActivity extends WXCallbackActivity{//} implements IWXAPIEventHandler {
//    TextView wechatResult;
//    private IWXAPI api;
//    ImageView wechatResultImage;
//    Button backSetting;
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        checkLanguage();
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.setStatusBarColor(getResources().getColor(android.R.color.holo_blue_light));
//        }
//        setContentView(R.layout.activity_wechatcallback);
//        wechatResult=(TextView)findViewById(R.id.shareresulttext);
//        wechatResultImage=(ImageView) findViewById(R.id.shareresultimage);
//        backSetting=(Button) findViewById(R.id.backsetting);
//        backSetting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                WXEntryActivity.this.finish();
//            }
//        });
//        api = WXAPIFactory.createWXAPI(this, "wx8165366cc17f9522", false);
//        api.handleIntent(getIntent(), this);
//    }
//    @Override
//    public void onReq(BaseReq baseReq) {
//
//    }
//
//    @Override
//    public void onResp(BaseResp baseResp) {
//        int result=baseResp.errCode;
//        Toast.makeText(this,result+"", Toast.LENGTH_LONG);
//        if(result==0){
//            wechatResult.setText(getResources().getText(R.string.status_active));
//            IconicsDrawable drawable=new IconicsDrawable(this)
//                    .icon(MaterialDesignIconic.Icon.gmi_check_circle)
//                    .color(Color.GREEN)
//                    .sizeDp(24);
//            wechatResultImage.setImageDrawable(drawable);
//            SharedPreferences.Editor editor=this.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
//            editor.putInt("active",1);
//            editor.commit();
//        }else{
//            wechatResult.setText(getResources().getText(R.string.status_deactive));
//            IconicsDrawable drawable=new IconicsDrawable(this)
//                    .icon(MaterialDesignIconic.Icon.gmi_close_circle)
//                    .color(Color.RED)
//                    .sizeDp(24);
//            wechatResultImage.setImageDrawable(drawable);
//        }
//    }
//    public void checkLanguage() {
//        SharedPreferences pref = getSharedPreferences("settings", Context.MODE_PRIVATE);
//        int language = pref.getInt("lang", -1);
//        Resources resources = this.getResources();
//        DisplayMetrics dm = resources.getDisplayMetrics();
//        Configuration config = resources.getConfiguration();
//        // 应用用户选择语言
//        if (language == 0) {
//            config.locale = new Locale("uy");
//        } else if (language == 1) {
//            config.locale = Locale.SIMPLIFIED_CHINESE;
//        }
//        resources.updateConfiguration(config, dm);
//    }
}