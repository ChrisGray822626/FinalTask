package com.example.chris.finaltask;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TabHost;

import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends TabActivity
{
    private List<PersonalInfo> personalInfoList = new ArrayList<>();


    private RoundedImageView accountImageView;
    private RecyclerView infoRecyclerView;
    private String id;
    private String nickName;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        TabHost tabHost = getTabHost();
        LayoutInflater.from(this).inflate(R.layout.activity_main,
                tabHost.getTabContentView(), true);
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("标签页一").setContent(R.id.tab1));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("标签页二").setContent(R.id.tab2));
        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("我的").setContent(R.id.tab3));

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        accountImageView = (RoundedImageView)findViewById(R.id.accountPhoto);
        infoRecyclerView = (RecyclerView)findViewById(R.id.infoRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        infoRecyclerView.setLayoutManager(linearLayoutManager);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getPersonalInfo();
            }
        });
    }


    private void getPersonalInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .get()
                            .url("http://47.102.131.72:8080/account/" + id)
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            //失败调用
                            Log.e("return", "onFailure: ");
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            //成功调用
                            Log.e("return", "onResponse: ");
                            final String responseData = response.body().string();
                            Log.d("test", responseData);
                            parseJSONWithJSONObject(responseData);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                String url = "http://47.102.131.72:8080/account/photo/" + id + ".jpg";
                final Bitmap bitmap = returnBitMap(url);
                accountImageView.post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        accountImageView.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();
    }

    private void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            PersonalInfo personalInfo;
            personalInfo = new PersonalInfo("账号",id);
            personalInfoList.add(personalInfo);
            String name = jsonObject.getString("name");
            personalInfo = new PersonalInfo("姓名",name);
            personalInfoList.add(personalInfo);
            nickName = jsonObject.getString("nickName");
            personalInfo = new PersonalInfo("昵称",nickName);
            personalInfoList.add(personalInfo);
            String sex = jsonObject.getString("sex");
            personalInfo = new PersonalInfo("性别",sex);
            personalInfoList.add(personalInfo);
            String old = jsonObject.getString("old");
            personalInfo = new PersonalInfo("年龄",old);
            personalInfoList.add(personalInfo);
            String email = jsonObject.getString("email");
            personalInfo = new PersonalInfo("邮箱",email);
            personalInfoList.add(personalInfo);
            String school = jsonObject.getString("school");
            personalInfo = new PersonalInfo("学校",school);
            personalInfoList.add(personalInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PersonalInfoAdapter personalInfoAdapter = new PersonalInfoAdapter(personalInfoList);
                infoRecyclerView.setAdapter(personalInfoAdapter);
            }
        });
    }

    private Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
