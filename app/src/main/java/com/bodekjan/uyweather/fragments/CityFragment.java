package com.bodekjan.uyweather.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.blankj.utilcode.utils.NetworkUtils;
import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.activities.MainActivity;
import com.bodekjan.uyweather.activities.WebActivity;
import com.bodekjan.uyweather.model.NewsLib;
import com.bodekjan.uyweather.model.OneDay;
import com.bodekjan.uyweather.model.OneNews;
import com.bodekjan.uyweather.model.OnePlace;
import com.bodekjan.uyweather.model.PlaceLib;
import com.bodekjan.uyweather.model.WeatherStatus;
import com.bodekjan.uyweather.util.CommonHelper;
import com.bodekjan.uyweather.util.WeatherTranslator;
import com.bodekjan.uyweather.widget.PMMeter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.mikepenz.iconics.view.IconicsImageView;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bodekjan on 2016/9/7.
 */
public class CityFragment extends Fragment {
    PullRefreshLayout layout;
    String fragmentTitle="--";
    String fragmentCode;
    String fragmentBg;
    int fragmentStatus=0;
    OnePlace myPlace;
    OnePlace defaultPlace;
    boolean moreDays=true;
    ImageView bigWeather;
    TextView curAqi;
    TextView curAqiText;
    /*控件列表*/
    TextView curTmp;
    TextView curStatus;
    TextView upStatus;
    TextView curPm;
    TextView maxMin;
    TextView curWind;
    TextView sunRise;
    TextView sunSet;
    /* 每日新闻 */
    TextView newsOne;
    TextView newsTwo;
    TextView newsTree;
    TextView newsFour;
    TextView newsFive;
    TextView newsSix;
    /* 七天控件列表 */
    LinearLayout dayTwo;
    LinearLayout dayThree;
    LinearLayout dayFour;
    LinearLayout dayFive;
    LinearLayout daySix;
    LinearLayout daySeven;
    LinearLayout dayEight;
    LinearLayout dayMore;
    TextView dayMoreLabel;
    /* 图表的*/
    LineChart mChart;
    ArrayList<Entry> chartMaxVals;
    int chartMin=50;
    int chartMax=-50;
    PMMeter meter;
    MainActivity parent;
    List<OneNews> newsList;

    private static final String TAG = "TestFragment";
    public String getFragmentTitle(){
        return fragmentTitle;
    }
    public String getFragmentCode(){
        return fragmentCode;
    }
    public int getFragmentStatus(){
        return fragmentStatus;
    }
    public String getFragmentBg(){
        return fragmentBg;
    }
    public static CityFragment newInstance(OnePlace place) {
        CityFragment newFragment = new CityFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("city",place);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        defaultPlace();
        myPlace = args.getSerializable("city") != null ? (OnePlace)args.getSerializable("city") : defaultPlace;
        parent=(MainActivity)getActivity();
    }
    public void defaultPlace(){
        defaultPlace=new OnePlace();
        defaultPlace.city="--";
        defaultPlace.uyCity="--";
        defaultPlace.cityId="--";
        defaultPlace.wCode="--";
        defaultPlace.wTxt="--";
        defaultPlace.hum="--";
        defaultPlace.curTmp="--";
        defaultPlace.curStatus="--";
        defaultPlace.windSpd="--";
        defaultPlace.minTmp="--";
        defaultPlace.maxTmp="--";
        defaultPlace.pm25="--"; //就用湿度吧
        defaultPlace.aqi = "--";
        defaultPlace.rise = "--";
        defaultPlace.set = "--";
        for(int i=0; i<7;i++){
            OneDay day=new OneDay();
            day.maxTmp="--";
            day.minTmp="--";
            day.wTime="--";
            day.wCode="--";
            defaultPlace.sevenDay.add(day);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        layout = (PullRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
        // listen refresh event
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkUpdate();
            }
        });
        bigWeather=(ImageView) view.findViewById(R.id.bigweather);
        curAqi=(TextView) view.findViewById(R.id.aqival);
        curAqiText=(TextView) view.findViewById(R.id.aqitext);
        curStatus=(TextView)view.findViewById(R.id.curstatus);
        weatherTextToDrawable(myPlace.curStatus);
        if(parent.lang==0){
            fragmentTitle=myPlace.uyCity;
        }else{
            fragmentTitle=myPlace.city;
        }
        fragmentCode=myPlace.cityId;
        fragmentStatus=myPlace.status;
        fragmentBg=myPlace.curStatus;
        curTmp=(TextView)view.findViewById(R.id.curtmp);
        curTmp.setText(myPlace.curTmp+"°");
        upStatus=(TextView)view.findViewById(R.id.upstatus);
        upStatus.setText(getPastTime(myPlace.quickDate,myPlace.quickTime));
        curPm=(TextView)view.findViewById(R.id.pm);
        String humpValue=getResources().getString(R.string.value_hump);
        curPm.setText(String.format(humpValue, myPlace.pm25+"%"));
        maxMin=(TextView)view.findViewById(R.id.maxmintmp);
        maxMin.setText(myPlace.maxTmp+"° / "+myPlace.minTmp+"°");
        curWind=(TextView)view.findViewById(R.id.wind);
        String[] dayLabels=CommonHelper.daysLabel(getActivity(),myPlace.quickDate+" "+myPlace.quickTime);
        /* Today date */
        TextView dayTwoTime=(TextView)view.findViewById(R.id.daytwotime);
        Date today=new Date();
        DateFormat dfMonth = new SimpleDateFormat("MM");
        DateFormat dfDay = new SimpleDateFormat("dd");
        String todayValue=getResources().getString(R.string.value_todaydate);
        dayTwoTime.setText(String.format(todayValue, dfMonth.format(today),dfDay.format(today),dayLabels[7]));

        String sunRiseValue=getResources().getString(R.string.value_sunup);
        sunRise=(TextView)view.findViewById(R.id.sunrise);
        sunRise.setText(String.format(sunRiseValue, WeatherTranslator.setTimeZone(getActivity(),myPlace.rise)));

        String sunSetValue=getResources().getString(R.string.value_sunset);
        sunRise=(TextView)view.findViewById(R.id.sunset);
        sunRise.setText(String.format(sunSetValue, WeatherTranslator.setTimeZone(getActivity(),myPlace.set)));

        String windValue=getResources().getString(R.string.value_windspeed);
        curWind.setText(String.format(windValue, myPlace.windSpd));
        dayTwo=(LinearLayout)view.findViewById(R.id.daytwo);
        dayThree=(LinearLayout)view.findViewById(R.id.daythree);
        dayFour=(LinearLayout)view.findViewById(R.id.dayfour);
        dayFive=(LinearLayout)view.findViewById(R.id.dayfive);
        daySix=(LinearLayout)view.findViewById(R.id.daysix);
        daySeven=(LinearLayout)view.findViewById(R.id.dayseven);
        dayEight=(LinearLayout)view.findViewById(R.id.dayeight);
        dayMore=(LinearLayout)view.findViewById(R.id.moredays);
        dayMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(moreDays){
                    dayFive.setVisibility(View.GONE);
                    daySix.setVisibility(View.GONE);
                    daySeven.setVisibility(View.GONE);
                    dayEight.setVisibility(View.GONE);
                    moreDays=false;
                    dayMoreLabel.setText(getResources().getText(R.string.weather_sevenday));
                }else{
                    dayFive.setVisibility(View.VISIBLE);
                    daySix.setVisibility(View.VISIBLE);
                    daySeven.setVisibility(View.VISIBLE);
                    dayEight.setVisibility(View.VISIBLE);
                    moreDays=true;
                    dayMoreLabel.setText(getResources().getText(R.string.weather_threeday));
                }
            }
        });
        TextView dayThreeTime=(TextView)view.findViewById(R.id.daythreetime);
        TextView dayFourTime=(TextView)view.findViewById(R.id.dayfourtime);
        TextView dayFiveTime=(TextView)view.findViewById(R.id.dayfivetime);
        TextView daySixTime=(TextView)view.findViewById(R.id.daysixtime);
        TextView daySevenTime=(TextView)view.findViewById(R.id.dayseventime);
        TextView dayEightTime=(TextView)view.findViewById(R.id.dayeighttime);
        dayMoreLabel=(TextView)view.findViewById(R.id.daymorelabel);dayMoreLabel.setText(getResources().getText(R.string.weather_threeday));
        TextView dayTwoTmp=(TextView)view.findViewById(R.id.daytwotmp);
        TextView dayThreeTmp=(TextView)view.findViewById(R.id.daythreetmp);
        TextView dayFourTmp=(TextView)view.findViewById(R.id.dayfourtmp);
        TextView dayFiveTmp=(TextView)view.findViewById(R.id.dayfivetmp);
        TextView daySixTmp=(TextView)view.findViewById(R.id.daysixtmp);
        TextView daySevenTmp=(TextView)view.findViewById(R.id.dayseventmp);
        TextView dayEightTmp=(TextView)view.findViewById(R.id.dayeighttmp);
        IconicsImageView dayTwoIcon=(IconicsImageView)view.findViewById(R.id.daytwoicon);
        IconicsImageView dayThreeIcon=(IconicsImageView)view.findViewById(R.id.daythreeicon);
        IconicsImageView dayFourIcon=(IconicsImageView)view.findViewById(R.id.dayfouricon);
        IconicsImageView dayFiveIcon=(IconicsImageView)view.findViewById(R.id.dayfiveicon);
        IconicsImageView daySixIcon=(IconicsImageView)view.findViewById(R.id.daysixicon);
        IconicsImageView daySevenIcon=(IconicsImageView)view.findViewById(R.id.daysevenicon);
        IconicsImageView dayEightIcon=(IconicsImageView)view.findViewById(R.id.dayeighticon);
        if(parent.lang==0){
            TextView todayComf=(TextView)view.findViewById(R.id.comf);todayComf.setText(WeatherTranslator.comfTranslate(myPlace.todayComf));
            TextView todayCw=(TextView)view.findViewById(R.id.cw);todayCw.setText(WeatherTranslator.carTranslate(myPlace.todayCw));
            TextView todayDrsg=(TextView)view.findViewById(R.id.drsg);todayDrsg.setText(WeatherTranslator.dresTranslate(myPlace.todayDrsg));
            TextView todayFlu=(TextView)view.findViewById(R.id.flu);todayFlu.setText(WeatherTranslator.fluTranslate(myPlace.todayFlu));
            TextView todaySport=(TextView)view.findViewById(R.id.sport);todaySport.setText(WeatherTranslator.travelTranslate(myPlace.todaySport));
            TextView todayTrav=(TextView)view.findViewById(R.id.trav);todayTrav.setText(WeatherTranslator.sportTranslate(myPlace.todayTrav));
        }else {
            TextView todayComf=(TextView)view.findViewById(R.id.comf);todayComf.setText(myPlace.todayComf);
            TextView todayCw=(TextView)view.findViewById(R.id.cw);todayCw.setText(myPlace.todayCw);
            TextView todayDrsg=(TextView)view.findViewById(R.id.drsg);todayDrsg.setText(myPlace.todayDrsg);
            TextView todayFlu=(TextView)view.findViewById(R.id.flu);todayFlu.setText(myPlace.todayFlu);
            TextView todaySport=(TextView)view.findViewById(R.id.sport);todaySport.setText(myPlace.todaySport);
            TextView todayTrav=(TextView)view.findViewById(R.id.trav);todayTrav.setText(myPlace.todayTrav);
        }
        if(!myPlace.city.equals("--")){
            /* 有可用城市时 */
            for(int i=0; i<myPlace.sevenDay.size();i++){
                if(i==0){
                    dayTwoTmp.setText(myPlace.maxTmp+"° / "+myPlace.minTmp+"°");
                    dayTwoIcon.setImageDrawable(WeatherTranslator.weatherCodeToDrawable(getActivity(),myPlace.sevenDay.get(i).wCode));
                }else if(i==1){
                    dayThreeTime.setText(dayLabels[i]);
                    dayThreeTmp.setText(myPlace.sevenDay.get(i).maxTmp+"° / "+myPlace.sevenDay.get(i).minTmp+"°");
                    dayThreeIcon.setImageDrawable(WeatherTranslator.weatherCodeToDrawable(getActivity(),myPlace.sevenDay.get(i).wCode));
                }else if(i==2){
                    dayFourTime.setText(dayLabels[i]);
                    dayFourTmp.setText(myPlace.sevenDay.get(i).maxTmp+"° / "+myPlace.sevenDay.get(i).minTmp+"°");
                    dayFourIcon.setImageDrawable(WeatherTranslator.weatherCodeToDrawable(getActivity(),myPlace.sevenDay.get(i).wCode));
                }else if(i==3){
                    dayFiveTime.setText(dayLabels[i]);
                    dayFiveTmp.setText(myPlace.sevenDay.get(i).maxTmp+"° / "+myPlace.sevenDay.get(i).minTmp+"°");
                    dayFiveIcon.setImageDrawable(WeatherTranslator.weatherCodeToDrawable(getActivity(),myPlace.sevenDay.get(i).wCode));
                }else if(i==4){
                    daySixTime.setText(dayLabels[i]);
                    daySixTmp.setText(myPlace.sevenDay.get(i).maxTmp+"° / "+myPlace.sevenDay.get(i).minTmp+"°");
                    daySixIcon.setImageDrawable(WeatherTranslator.weatherCodeToDrawable(getActivity(),myPlace.sevenDay.get(i).wCode));
                }else if(i==5){
                    daySevenTime.setText(dayLabels[i]);
                    daySevenTmp.setText(myPlace.sevenDay.get(i).maxTmp+"° / "+myPlace.sevenDay.get(i).minTmp+"°");
                    daySevenIcon.setImageDrawable(WeatherTranslator.weatherCodeToDrawable(getActivity(),myPlace.sevenDay.get(i).wCode));
                }else if(i==6){
                    dayEightTime.setText(dayLabels[i]);
                    dayEightTmp.setText(myPlace.sevenDay.get(i).maxTmp+"° / "+myPlace.sevenDay.get(i).minTmp+"°");
                    dayEightIcon.setImageDrawable(WeatherTranslator.weatherCodeToDrawable(getActivity(),myPlace.sevenDay.get(i).wCode));
                }
            }
            mChart = (LineChart) view.findViewById(R.id.chart1);
            /* 给chart赋值 */
            chartMaxVals = new ArrayList<Entry>();
            for(int j=0;j< myPlace.dayDetail.size();j++){
                int temp=0;
                temp+=j*3;
                chartMaxVals.add(new Entry(temp,Float.valueOf(myPlace.dayDetail.get(j).maxTmp)));
                if(Integer.valueOf(myPlace.dayDetail.get(j).maxTmp)>chartMax){
                    chartMax=Integer.valueOf(myPlace.dayDetail.get(j).maxTmp);
                }
                if(Integer.valueOf(myPlace.dayDetail.get(j).maxTmp)<chartMin){
                    chartMin=Integer.valueOf(myPlace.dayDetail.get(j).maxTmp);
                }
            }
            initChart();
        }
        /* PM2.5 仪表盘 */
        meter=(PMMeter) view.findViewById(R.id.meter);
        meter.setMaxValue(180);
        meter.setMinValue(0);
        meter.setStartRadian(160);
        meter.setEndRadian(360);
        if((myPlace.aqi==null) || (myPlace.aqi.equals("--")))
        {
            meter.setValue(1);
            curAqi.setText(getResources().getText(R.string.wss_nodata));
            curAqiText.setText(getResources().getText(R.string.wss_nodata));
        }else {
            meter.setValue(Integer.valueOf(myPlace.aqi));
            String aqiValue=getResources().getString(R.string.value_aqi);
            curAqi.setText(String.format(aqiValue, myPlace.aqi));
            String aqiText=getResources().getString(R.string.value_aqitxt);
            curAqiText.setText(String.format(aqiText, WeatherTranslator.airTranslate(Integer.valueOf(myPlace.aqi),getActivity())));
        }
        /* 新闻 */
        newsOne= (TextView) view.findViewById(R.id.newsone);
        newsTwo= (TextView) view.findViewById(R.id.newstwo);
        newsTree= (TextView) view.findViewById(R.id.newstree);
        newsFour= (TextView) view.findViewById(R.id.newsfour);
        newsFive= (TextView) view.findViewById(R.id.newsfive);
        newsSix= (TextView) view.findViewById(R.id.newssix);
        try {
            newsList=NewsLib.get(getActivity()).getNews();
            SharedPreferences pref=getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
            int language=pref.getInt("lang",-1);
            if(newsList.size()!=0 && language == 0){
                LinearLayout newsArea= (LinearLayout) view.findViewById(R.id.newsarea);
                newsArea.setVisibility(View.VISIBLE);
                newsOne.setText(newsList.get(0).text);
                newsOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(getActivity(),WebActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("url","http://www.hawar.cn/mobile/?do=show&mid="+newsList.get(0).link);
                        bundle.putSerializable("title",getResources().getText(R.string.title_news).toString());
                        i.putExtras(bundle);
                        getActivity().startActivity(i);
                    }
                });
                newsTwo.setText(newsList.get(1).text);
                newsTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(getActivity(),WebActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("url","http://www.hawar.cn/mobile/?do=show&mid="+newsList.get(1).link);
                        bundle.putSerializable("title",getResources().getText(R.string.title_news).toString());
                        i.putExtras(bundle);
                        getActivity().startActivity(i);
                    }
                });
                newsTree.setText(newsList.get(2).text);
                newsTree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(getActivity(),WebActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("url","http://www.hawar.cn/mobile/?do=show&mid="+newsList.get(2).link);
                        bundle.putSerializable("title",getResources().getText(R.string.title_news).toString());
                        i.putExtras(bundle);
                        getActivity().startActivity(i);
                    }
                });
                newsFour.setText(newsList.get(3).text);
                newsFour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(getActivity(),WebActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("url","http://www.hawar.cn/mobile/?do=show&mid="+newsList.get(3).link);
                        bundle.putSerializable("title",getResources().getText(R.string.title_news).toString());
                        i.putExtras(bundle);
                        getActivity().startActivity(i);
                    }
                });
                newsFive.setText(newsList.get(4).text);
                newsFive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(getActivity(),WebActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("url","http://www.hawar.cn/mobile/?do=show&mid="+newsList.get(4).link);
                        bundle.putSerializable("title",getResources().getText(R.string.title_news).toString());
                        i.putExtras(bundle);
                        getActivity().startActivity(i);
                    }
                });
                newsSix.setText(newsList.get(5).text);
                newsSix.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(getActivity(),WebActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("url","http://www.hawar.cn/mobile/?do=show&mid="+newsList.get(5).link);
                        bundle.putSerializable("title",getResources().getText(R.string.title_news).toString());
                        i.putExtras(bundle);
                        getActivity().startActivity(i);
                    }
                });
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return view;
    }
    public void checkUpdate(){
        if(myPlace==null || myPlace.quickDate==null){
            layout.setRefreshing(false); //不用更新
            return;
        }
        if(!NetworkUtils.isConnected(getActivity())){
            layout.setRefreshing(false); //没网不更新
            ((MainActivity)getActivity()).showSnack(dayMore,getResources().getText(R.string.snack_neterr).toString(),0);
            return;
        }
        String quick=myPlace.quickDate+" "+myPlace.quickTime+":00";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date quickDate=df.parse(quick);
            long time=new Date().getTime()-quickDate.getTime();
            if((CommonHelper.getPastMinutes(time)>CommonHelper.refreshTime) || (CommonHelper.getPastMinutes(time)<0)){
                upStatus.setText(getResources().getText(R.string.status_refreshing));
                // 已经超过60分钟了该更新一下
                layout.setRefreshing(true);
                PlaceLib.get(getActivity()).quickUpCity(myPlace);
            }else{
                layout.setRefreshing(false); //不用更新
                ((MainActivity)getActivity()).showSnack(dayMore,getResources().getText(R.string.snack_weather).toString(),1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public void closeUploadIcon(){
        upStatus.setText(getPastTime(myPlace.quickDate,myPlace.quickTime));
        if(layout!=null){
            layout.setRefreshing(false);
        }
    }
    public String getPastTime(String date,String time){
        if(date==null) return "--"; // 没有可用城市
        String strTime="";
        String quick=myPlace.quickDate+" "+myPlace.quickTime+":00";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date quickDate= null;
        try {
            quickDate = df.parse(quick);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long longTime=new Date().getTime()-quickDate.getTime();
        int minute=CommonHelper.getPastMinutes(longTime);
        if(minute<0){
            String stringValue=getResources().getString(R.string.value_pastminute);
            strTime=String.format(stringValue, 44);
        }else if(minute<60){
            String stringValue=getResources().getString(R.string.value_pastminute);
            strTime=String.format(stringValue, minute);
        }else {
            String stringValue=getResources().getString(R.string.value_pasthour);
            strTime=String.format(stringValue, (int)(minute/60));
        }
        return strTime;
    }
    private void initChart() {
        mChart.setViewPortOffsets(50, 0, 16, 60);
            // no description text
        mChart.setDescription("");
            // enable touch gestures
        mChart.setTouchEnabled(true);
            // enable scaling and dragging
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(true);
            // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);
            //mChart.setMaxHighlightDistance(400);
        XAxis x = mChart.getXAxis();
        x.setLabelCount(8, false);
        x.setEnabled(true);
        //x.setTypeface(tf);
        x.setTextColor(Color.WHITE);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(false);
        x.setAxisLineColor(Color.WHITE);
        YAxis y = mChart.getAxisLeft();
        y.setLabelCount(8, false);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);
        y.setAxisMinValue(chartMin-4);
        y.setAxisMaxValue(chartMax+4);
        mChart.getAxisRight().setEnabled(true);
        LineDataSet set1 = new LineDataSet(chartMaxVals, "maxtmp");
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set1.setCubicIntensity(0.02f);
        set1.setDrawFilled(false);
        set1.setDrawCircles(true);
        set1.setDrawValues(true);
        set1.setCircleColor(getResources().getColor(android.R.color.holo_blue_light));
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setColor(getResources().getColor(android.R.color.holo_blue_light));
        set1.setFillAlpha(100);
        set1.setDrawHorizontalHighlightIndicator(false);
        set1.setFillFormatter(new FillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return -10;
            }
        });
        LineData data;
        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            data =  mChart.getLineData();
            data.clearValues();
            data.removeDataSet(0);
            data.addDataSet(set1);
        }else {
            data = new LineData(set1);
        }
        data.setValueTextSize(9f);
        data.setDrawValues(true);
        data.setValueTextColor(getResources().getColor(android.R.color.holo_blue_light));
        mChart.setData(data);
        mChart.getLegend().setEnabled(false);
        mChart.animateXY(2000, 2000);
            // dont forget to refresh the drawing
        mChart.invalidate();
    }
    public void weatherTextToDrawable(String code){
        WeatherStatus status= WeatherTranslator.weatherTextTranslator(getActivity(),code);
        bigWeather.setImageDrawable(getResources().getDrawable(status.getIconId()));
        curStatus.setText(status.getuText());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
