package com.bodekjan.uyweather.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.utils.NetworkUtils;
import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.activities.SettingActivity;
import com.bodekjan.uyweather.activities.WebActivity;
import com.bodekjan.uyweather.dialog.SelectLanguageDialog;
import com.bodekjan.uyweather.model.GlobalCity;
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
import org.apache.http.impl.client.DefaultTargetAuthenticationHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by bodekjan on 2016/9/14.
 */
public class CommentFragment extends Fragment {
    EditText commentText;
    Button commentSubmit;
    String mPageName;
    public static CommentFragment newInstance(){
        CommentFragment fragment = new CommentFragment();
//        if(point!=null){
//            Bundle bundle=new Bundle();
//            bundle.putSerializable("point",point);
//            fragment.setArguments(bundle);
//        }
        return fragment;
    }
    public CommentFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageName="settingpage";
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, null);
        ((SettingActivity)getActivity()).changeTitle(getResources().getText(R.string.comment_title).toString());
        commentText=(EditText)view.findViewById(R.id.commenttext);
        commentSubmit=(Button) view.findViewById(R.id.commentsubmit);
        commentSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentText.getText().length()<6){
                    ((SettingActivity)getActivity()).showSnack(commentSubmit,getResources().getText(R.string.snack_commentnull).toString(),0);
                }else{
                    SharedPreferences.Editor editor=getActivity().getSharedPreferences("settings",Context.MODE_PRIVATE).edit();
                    SharedPreferences pref=getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
                    long comment=pref.getLong("comment",-1l);
                    if(comment==-1l){
                        //要提交
                        new NewComment().execute("");
                        editor.putLong("comment",new Date().getTime());
                        editor.commit();
                        return;
                    }
                    if((new Date().getTime()-comment)<12000){
                        ((SettingActivity)getActivity()).showSnack(commentSubmit,getResources().getText(R.string.snack_commenttoofast).toString(),1);
                        return;
                    }
                    //要提交
                    new NewComment().execute("");
                }
            }
        });
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
    private class NewComment extends AsyncTask<String, Integer, String> {
        String comment;
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            commentSubmit.setText(getActivity().getResources().getText(R.string.status_commenting));
            comment=commentText.getText().toString();
        }
        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            if(!NetworkUtils.isConnected(getActivity())){
                return "neterr";
            }
            try {
                NameValuePair comm = new BasicNameValuePair("comment", comment);
                List<NameValuePair> commentList = new ArrayList<NameValuePair>();
                commentList.add(comm);
                HttpEntity requestHttpEntity = new UrlEncodedFormEntity(commentList, HTTP.UTF_8);
                HttpPost httpPost = new HttpPost(CommonHelper.commentUrl);
                // 将请求体内容加入请求中
                httpPost.setEntity(requestHttpEntity);
                // 需要客户端对象来发送请求
                HttpClient httpClient = new DefaultHttpClient();
                // 发送请求
                HttpResponse response = httpClient.execute(httpPost);
                // 显示响应
                HttpEntity httpEntity = response.getEntity();
                InputStream inputStream = httpEntity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        inputStream));
                String result = "";
                String line = "";
                while (null != (line = reader.readLine()))
                {
                    result += line;
                }
                if(result.equals("success")){
                    return "success";
                }else {
                    return null;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        //onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(Integer... progresses) {

        }
        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String result) {
            if(result!=null){
                if(result.equals("neterr")){
                    ((SettingActivity)getActivity()).showSnack(commentSubmit,getResources().getText(R.string.snack_neterr).toString(),0);
                    commentSubmit.setText(getActivity().getResources().getText(R.string.snack_neterr));
                    return;
                }
                if(result.equals("success")){
                    SharedPreferences.Editor editor=getActivity().getSharedPreferences("settings",Context.MODE_PRIVATE).edit();
                    editor.putLong("comment",new Date().getTime());
                    editor.commit();
                    commentSubmit.setText(getActivity().getResources().getText(R.string.status_commented));
                    ((SettingActivity)getActivity()).showSnack(commentSubmit,getResources().getText(R.string.snack_commentokk).toString(),1);
                }
            }else {
                ((SettingActivity)getActivity()).showSnack(commentSubmit,getResources().getText(R.string.snack_commenterror).toString(),0);
                commentSubmit.setText(getActivity().getResources().getText(R.string.snack_commenterror));
            }
        }
        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {

        }
    }
}
