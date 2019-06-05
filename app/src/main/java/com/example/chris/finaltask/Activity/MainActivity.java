package com.example.chris.finaltask.Activity;

import android.app.DatePickerDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chris.finaltask.Adapter.ConferenceInfoAdapter;
import com.example.chris.finaltask.Adapter.PersonalInfoAdapter;
import com.example.chris.finaltask.Class.ConferenceInfo;
import com.example.chris.finaltask.Class.PersonalInfo;
import com.example.chris.finaltask.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.AlertDialog.THEME_HOLO_DARK;

public class MainActivity extends TabActivity
{
    private List<PersonalInfo> personalInfoList = new ArrayList<>();
    private List<ConferenceInfo> conferenceInfoList = new ArrayList<>();

    private RecyclerView personalInfoRecyclerView;
    private RecyclerView conferenceInfoRecyclerView;
    private Button chooseStartTime;
    private Button chooseEndTime;
    private Button generateResult;
    private TextView nickNameTextView;

    private Calendar calendar = Calendar.getInstance();
    private String id;
    private String nickName;
    private String location;
    private String character;
    private String origin;
    private String process;
    private String result;
    private Integer startYear = 0;
    private Integer endYear = 0;
    private Integer compareTo;
    private Date startDate;
    private Date endDate;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        TabHost tabHost = getTabHost();
        LayoutInflater.from(this).inflate(R.layout.activity_main,
                tabHost.getTabContentView(), true);
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("会议").setContent(R.id.tab1));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("推演").setContent(R.id.tab2));
        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("我的").setContent(R.id.tab3));

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        personalInfoRecyclerView = (RecyclerView)findViewById(R.id.personalInfoRecyclerView);
        conferenceInfoRecyclerView = (RecyclerView)findViewById(R.id.conferenceInfoRecyclerView);

        chooseStartTime = (Button)findViewById(R.id.chooseStartTime);
        chooseStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MainActivity.this,THEME_HOLO_DARK,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int mon, int day) {
                        chooseStartTime.setText(String.format("%d年%d月%d日",year,mon + 1,day));
                        startYear = year;
                        mon ++ ;
                        String date = year +"-" + mon + "-" + day;
                        try {
                            startDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Log.d("test",startDate.toString());
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        chooseEndTime = (Button)findViewById(R.id.chooseEndTime);
        chooseEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MainActivity.this,THEME_HOLO_DARK,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int mon, int day) {
                        chooseEndTime.setText(String.format("%d年%d月%d日",year,mon + 1,day));
                        endYear = year;
                        mon ++ ;
                        String date = year +"-" + mon + "-" + day;
                        try {
                            endDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Log.d("test",endDate.toString());
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        generateResult = (Button)findViewById(R.id.generateResult);
        generateResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView locationTextView = (TextView)findViewById(R.id.location);
                TextView characterTextView = (TextView)findViewById(R.id.character);
                TextView originTextView = (TextView)findViewById(R.id.origin);
                TextView processTextView = (TextView)findViewById(R.id.process);
                TextView resultTextView = (TextView)findViewById(R.id.result);
                TextView inferenceResultTextView = (TextView)findViewById(R.id.inferenceResult);
                location = locationTextView.getText().toString();
                character = characterTextView.getText().toString();
                origin = originTextView.getText().toString();
                process = processTextView.getText().toString();
                result = resultTextView.getText().toString();
                compareTo = endDate.compareTo(startDate);
                Log.d("test",compareTo.toString());
                Log.d("test",startDate.toString());
                Log.d("test",endDate.toString());
                if( startYear == 0 ||endYear == 0 )
                    Toast.makeText(MainActivity.this,"请选择时间！",Toast.LENGTH_SHORT).show();
                else if( compareTo < 0)
                    Toast.makeText(MainActivity.this,"结束时间不得早于开始时间",Toast.LENGTH_SHORT).show();
                else if( location.isEmpty() || character.isEmpty() || origin.isEmpty() || process.isEmpty() || result.isEmpty() )
                    Toast.makeText(MainActivity.this,"内容不得为空",Toast.LENGTH_SHORT).show();
                else {
                    save();
                    Toast.makeText(MainActivity.this,"生成推演结果成功",Toast.LENGTH_SHORT).show();
                    inferenceResultTextView.setText(load());
                }
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getConferenceInfo();
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getPersonalInfo();
            }
        });
    }
    private void getConferenceInfo(){
        for( int i = 0; i < 5; i ++ ){
            String name = "9102泛银河梦模拟联合国大会";
            String date = "Feb 29-31,2019";
            String city = "Sol";
            String sponsor = "清明团银河帝国中央委员会";
            ConferenceInfo conferenceInfo = new ConferenceInfo(name,date,city,sponsor);
            conferenceInfoList.add(conferenceInfo);
        }
        ConferenceInfoAdapter adapter = new ConferenceInfoAdapter(conferenceInfoList);
        conferenceInfoRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        conferenceInfoRecyclerView.setLayoutManager(linearLayoutManager);
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
    }

    private void parseJSONWithJSONObject(String jsonData) {
        try {
            nickNameTextView = (TextView)findViewById(R.id.nickName);
            JSONObject jsonObject = new JSONObject(jsonData);
            PersonalInfo personalInfo;
            personalInfo = new PersonalInfo("账号",id);
            personalInfoList.add(personalInfo);
            String name = jsonObject.getString("name");
            personalInfo = new PersonalInfo("姓名",name);
            personalInfoList.add(personalInfo);
            nickName = jsonObject.getString("nickname");
            personalInfo = new PersonalInfo("昵称", nickName);
            nickNameTextView.setText(nickName);
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
                PersonalInfoAdapter adapter = new PersonalInfoAdapter(personalInfoList);
                personalInfoRecyclerView.setAdapter(adapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                personalInfoRecyclerView.setLayoutManager(linearLayoutManager);
            }
        });
    }

    private void save(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String data = "，" + location +
                "，由于" + origin +
                "，" + character + "经过" + process +
                "，最终" + result;
        Log.d("data",data);
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = openFileOutput("data", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));

            if(compareTo == 0){
                writer.write(dateFormat.format(startDate));
            }
            else{
                writer.write("从" + dateFormat.format(startDate) + "到" + dateFormat.format(endDate));
            }
            writer.write(data + "。");
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(writer != null)
                    writer.close();
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }

    private String load(){
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            in = openFileInput("data");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ( (line = reader.readLine()) != null ){
                content.append(line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if( reader != null ){
                try {
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }
}
