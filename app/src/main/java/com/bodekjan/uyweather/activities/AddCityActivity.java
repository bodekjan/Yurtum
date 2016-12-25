package com.bodekjan.uyweather.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.blankj.utilcode.utils.NetworkUtils;
import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.model.GlobalCity;
import com.bodekjan.uyweather.model.PlaceLib;
import com.bodekjan.uyweather.util.CommonHelper;
import com.bodekjan.uyweather.util.LocationFinder;
import com.bodekjan.uyweather.widget.ExpandableHeightGridView;
import com.bodekjan.uyweather.widget.MyCityButton;
import com.mikepenz.iconics.view.IconicsImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddCityActivity extends MyBaseActivity {
    private ExpandableHeightGridView hotCitys;
    private ExpandableHeightGridView altayCity;
    private ExpandableHeightGridView iliCity;
    private ExpandableHeightGridView turpanCity;
    private ExpandableHeightGridView korlaCity;
    private ExpandableHeightGridView aksuCity;
    private ExpandableHeightGridView hotanCity;
    private ExpandableHeightGridView kaxkaCity;
    private ExpandableHeightGridView tarCity;
    private ExpandableHeightGridView atuxCity;
    private ExpandableHeightGridView borCity;
    private ExpandableHeightGridView sanjiCity;
    LinearLayout searchResultLabelBar;
    LinearLayout locationBar;
    ScrollView xjCityBar;
    Map<String,JSONArray> xjCitys=new HashMap<String,JSONArray>();
    IconicsImageView arrow;
    np.EditText searchText;
    np.TextView searchStatus;
    np.Button currentCity;
    np.Button searchedButton;
    GlobalCity thisCity;
    GlobalCity searchedCity;
    PlaceLib places;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLanguage();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(android.R.color.holo_blue_light));
        }
        setContentView(R.layout.activity_add_city);
        uyFace = Typeface.createFromAsset(getAssets(), "fonts/ALKATIP.TTF");
        thisCity=new GlobalCity();
        try {
            places= PlaceLib.get(this);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        initLayout();
        /* 定位当前城市 */
        new FindMyCity().execute("aa");
    }
    @Override
    public void onResume() {
        CommonHelper.activities.add(this);
        super.onResume();
    }
    private void initLayout(){
        initXjCitys();
        searchResultLabelBar=(LinearLayout)findViewById(R.id.search_labelbar);searchResultLabelBar.setVisibility(View.GONE);
        locationBar=(LinearLayout)findViewById(R.id.search_locationbar);
        xjCityBar=(ScrollView) findViewById(R.id.scrollView);
        currentCity=(np.Button)findViewById(R.id.curcity);
        currentCity.setEnabled(false);
        currentCity.setText(getResources().getText(R.string.status_locationg));
        currentCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!NetworkUtils.isConnected(AddCityActivity.this)){
                    showSnack(searchResultLabelBar,getResources().getText(R.string.snack_neterr).toString(),0);
                    return;
                }
                if(!checkActive()) return;
                Intent intent = getIntent();
                Bundle data = new Bundle();
                data.putSerializable("city", thisCity);
                intent.putExtras(data);
                // 设置SecondActivity的结果码(resultCode)，并设置在当前结束后退回去的Activity
                AddCityActivity.this.setResult(0, intent);
                AddCityActivity.this.finish();
            }
        });
        searchedButton=(np.Button)findViewById(R.id.searchedcity);
        searchedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!NetworkUtils.isConnected(AddCityActivity.this)){
                    showSnack(searchResultLabelBar,getResources().getText(R.string.snack_neterr).toString(),0);
                    return;
                }
                if(!checkActive()) return;
                Intent intent = getIntent();
                Bundle data = new Bundle();
                data.putSerializable("city", searchedCity);
                intent.putExtras(data);
                // 设置SecondActivity的结果码(resultCode)，并设置在当前结束后退回去的Activity
                AddCityActivity.this.setResult(0, intent);
                AddCityActivity.this.finish();
            }
        });
        arrow=(IconicsImageView)findViewById(R.id.backarrow);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                // 设置SecondActivity的结果码(resultCode)，并设置在当前结束后退回去的Activity
                AddCityActivity.this.setResult(-1, intent);
                AddCityActivity.this.finish();
            }
        });
        searchStatus=(np.TextView)findViewById(R.id.search_status);
        searchText=(np.EditText)findViewById(R.id.searchtext);
        searchText.setOnKeyListener(new MyOnSerachListener());
        initGridView(hotCitys,R.id.hotcitylist,"hotCity");
        initGridView(altayCity,R.id.altaycitylist,"altayCity");
        initGridView(iliCity,R.id.ilicitylist,"iliCity");
        initGridView(turpanCity,R.id.turpancitylist,"turpanCity");
        initGridView(korlaCity,R.id.korlacitylist,"korlaCity");
        initGridView(aksuCity,R.id.aksucitylist,"aksuCity");
        initGridView(hotanCity,R.id.hotancitylist,"hotanCity");
        initGridView(kaxkaCity,R.id.kaxkacitylist,"kaxkaCity");
        initGridView(tarCity,R.id.tarcitylist,"tarCity");
        initGridView(atuxCity,R.id.atuxcitylist,"atuxCity");
        initGridView(borCity,R.id.borcitylist,"borCity");
        initGridView(sanjiCity,R.id.sanjicitylist,"sanjiCity");
    }
    class MyOnSerachListener  implements EditText.OnKeyListener{
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER&& event.getAction() == KeyEvent.ACTION_DOWN) {
                InputMethodManager imm = (InputMethodManager) v.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);}
                new SearchCity().execute("aaa");
                searchResultLabelBar.setVisibility(View.VISIBLE);
                locationBar.setVisibility(View.GONE);
                xjCityBar.setVisibility(View.GONE);
                return true;
            }
            return false;
        }
    }
    public void initGridView(ExpandableHeightGridView gView,int layout,String data){
        gView=(ExpandableHeightGridView)findViewById(layout);
        gView.setExpanded(true);
        if(lang==0){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) { //V.17
                gView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){ //V.21
                    gView.setHorizontalSpacing((int) -4);
                }
            }
        }
        ArrayList<GlobalCity> hotJavaCitys=new ArrayList<GlobalCity>();
        for(int i=0; i<xjCitys.get(data).length();i++){
            try {
                JSONObject city=xjCitys.get(data).getJSONObject(i);
                GlobalCity javaCity=new GlobalCity();
                javaCity.uyName=city.getString("uyname");
                javaCity.zhName=city.getString("zhname");
                javaCity.parent=data;
                hotJavaCitys.add(javaCity);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        gView.setAdapter(new MyListAdapter(hotJavaCitys));
        gView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!NetworkUtils.isConnected(AddCityActivity.this)){
                    showSnack(searchResultLabelBar,getResources().getText(R.string.snack_neterr).toString(),0);
                    return;
                }
                if(!checkActive()) return;
                GlobalCity city=new GlobalCity();
                try {
                    String currentParent=((MyCityButton)view).getParentCity();
                    city.zhName=((JSONObject)xjCitys.get(currentParent).get(position)).getString("zhname");
                    city.uyName=((JSONObject)xjCitys.get(currentParent).get(position)).getString("uyname");
                    city.pin="--";
                    if(((JSONObject)xjCitys.get(currentParent).get(position)).has("pinyin")){
                        city.pin=((JSONObject)xjCitys.get(currentParent).get(position)).getString("pinyin");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(places.checkCity(city.zhName)){
                    showSnack(searchResultLabelBar,getResources().getText(R.string.snack_hascity).toString(),1);
                    return;
                }
                Intent intent = getIntent();
                Bundle data = new Bundle();
                data.putSerializable("city", city);
                intent.putExtras(data);
                // 设置SecondActivity的结果码(resultCode)，并设置在当前结束后退回去的Activity
                AddCityActivity.this.setResult(0, intent);
                AddCityActivity.this.finish();
            }
        });
    }
    public boolean checkActive(){
        SharedPreferences pref=AddCityActivity.this.getSharedPreferences("settings", Context.MODE_PRIVATE);
        int active=pref.getInt("active",0);
        try {
            if(PlaceLib.get(AddCityActivity.this).getCitys().size()>=2){
                if(active==0){
                    AddCityActivity.this.showSnack(searchResultLabelBar,getResources().getText(R.string.snack_active).toString(),0);
                    return false;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public void initXjCitys(){
        AssetManager assetManager = this.getAssets();
        try {
            InputStream is = assetManager.open("xjcity.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer stringBuffer = new StringBuffer();
            String str = null;
            while((str = br.readLine())!=null){
                stringBuffer.append(str);
            }
            JSONTokener jsonTokener = new JSONTokener(stringBuffer.toString());
            try {
                JSONObject jsonObject=(JSONObject)jsonTokener.nextValue();
                xjCitys.put("hotCity",jsonObject.getJSONArray("hotcity"));
                xjCitys.put("altayCity",jsonObject.getJSONArray("altaycity"));
                xjCitys.put("iliCity",jsonObject.getJSONArray("ilicity"));
                xjCitys.put("turpanCity",jsonObject.getJSONArray("turpancity"));
                xjCitys.put("korlaCity",jsonObject.getJSONArray("korlacity"));
                xjCitys.put("aksuCity",jsonObject.getJSONArray("aksucity"));
                xjCitys.put("hotanCity",jsonObject.getJSONArray("hotancity"));
                xjCitys.put("kaxkaCity",jsonObject.getJSONArray("kaxkacity"));
                xjCitys.put("tarCity",jsonObject.getJSONArray("tarcity"));
                xjCitys.put("atuxCity",jsonObject.getJSONArray("atuxcity"));
                xjCitys.put("borCity",jsonObject.getJSONArray("borcity"));
                xjCitys.put("sanjiCity",jsonObject.getJSONArray("sanjicity"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    class MyListAdapter extends ArrayAdapter<GlobalCity> {
        public MyListAdapter(ArrayList<GlobalCity> notes){
            super(AddCityActivity.this,0,notes);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                convertView = AddCityActivity.this.getLayoutInflater().inflate(R.layout.btn_hotcity,null);
            }
            GlobalCity c = getItem(position);
            ((MyCityButton)convertView).setParentCity(c.parent);
            np.TextView titleTextView = (np.TextView)convertView.findViewById(R.id.hotcity);
            if(lang==0) {
                titleTextView.setText(c.uyName);
            }else{
                titleTextView.setText(c.zhName);
            }
            return convertView;
        }
    }
    private class FindMyCity extends AsyncTask<String, Integer, String> {
        GlobalCity reCity=new GlobalCity();
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
        }
        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            if(!NetworkUtils.isConnected(AddCityActivity.this)){
                return "neterr";
            }
            String ip=LocationFinder.getIp();
            reCity=LocationFinder.getCity(ip,AddCityActivity.this);
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
                    currentCity.setText(getResources().getString(R.string.status_failed));
                }else if(result.equals("neterr")){
                    currentCity.setText(getResources().getString(R.string.status_failed));
                    showSnack(searchResultLabelBar,getResources().getText(R.string.snack_neterr).toString(),0);
                }else{
                    if(lang==0) {
                        if(reCity.uyName.equals("")){
                            currentCity.setText(getResources().getString(R.string.status_failed));
                        }else{
                            currentCity.setText(reCity.uyName);
                        }
                    }else{
                        currentCity.setText(reCity.zhName);
                    }

                    if(!places.checkCity(result)){
                        if(!reCity.uyName.equals("")){
                            currentCity.setEnabled(true);
                        }
                    }
                    thisCity.zhName=result;
                    thisCity.uyName=reCity.uyName;
                    thisCity.pin="----";
                }
            }
        }
        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {

        }
    }
    private class SearchCity extends AsyncTask<String, Integer, String> {
        GlobalCity reCity=new GlobalCity();
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            searchStatus.setText(getResources().getText(R.string.status_searching));
            reCity.zhName=searchText.getText().toString();
        }
        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            if(!NetworkUtils.isConnected(AddCityActivity.this)){
                return "neterr";
            }
            reCity=CommonHelper.searchCity(reCity.zhName,0);
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
                if(result.equals("--")){
                    searchStatus.setText(R.string.status_notfound);
                }else if(result.equals("neterr")){
                    searchStatus.setText(getResources().getString(R.string.status_failed));
                    showSnack(searchResultLabelBar,getResources().getText(R.string.snack_neterr).toString(),0);
                }else{
                    if(reCity.uyName.length()>12){
                        reCity.uyName=reCity.zhName;
                    }
                    reCity=CommonHelper.findErrorCity(reCity,AddCityActivity.this);
                    searchStatus.setText(R.string.status_searchresult);
                    searchedButton.setText(reCity.uyName+" ("+reCity.zhName+")");
                    searchedButton.setVisibility(View.VISIBLE);
                    searchedCity=reCity;
                    searchedCity.pin="---";
                }
            }
        }
        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {

        }
    }
}
