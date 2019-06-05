package com.example.chris.finaltask.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chris.finaltask.R;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText UsernameEdit;
    private EditText PasswordEdit;
    private Button Login;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private CheckBox remember;
    private String status = "null";
    private String name;
    private String pass;
    private String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        UsernameEdit = (EditText)findViewById(R.id.username);
        PasswordEdit = (EditText)findViewById(R.id.password);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        remember = (CheckBox)findViewById(R.id.remenber_pass) ;
        boolean isRemember = preferences.getBoolean("remember_password",false);
        if(isRemember){
            String username = preferences.getString("username","");
            String password = preferences.getString("password","");
            UsernameEdit.setText(username);
            PasswordEdit.setText(password);
            remember.setChecked(true);
        }

        Login = (Button)findViewById(R.id.login);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = UsernameEdit.getText().toString();
                pass = PasswordEdit.getText().toString();
                if(name.isEmpty()||pass.isEmpty()){
                    Toast.makeText(LoginActivity.this
                            ,"用户名或密码不能为空!"
                            , Toast.LENGTH_SHORT).show();
                }
                else
                    login();
            }
        });
    }

    private void login(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .get()
                            .url("http://47.102.131.72:8080/account/" + name)
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            //失败调用
                            Log.e("return", "onFailure: " );
                        }
                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            //成功调用
                            Log.e("return", "onResponse: " );
                            final String responseData  = response.body().string();
                            Log.d("test",responseData);

                            parseStaffJSONObject(responseData);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private  void parseStaffJSONObject(String jsonData){
        try{
            final JSONObject jsonObject = new JSONObject(jsonData);
            status = jsonObject.optString("status ");
            Log.d("status",status);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (status == "404" || status == "500") {
                            Toast.makeText(LoginActivity
                                            .this, "该用户不存在"
                                    , Toast.LENGTH_SHORT).show();
                        } else {
                            password = jsonObject.getString("password");
                            if (pass.equals(password)) {
                                editor = preferences.edit();
                                if (remember.isChecked()) {
                                    editor.putBoolean("remember_password", true);
                                    editor.putString("password", pass);
                                    editor.putString("username", name);
                                } else {
                                    editor.clear();
                                }
                                editor.apply();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("id", name);
                                startActivity(intent);
                                Toast.makeText(LoginActivity.this
                                        , "登录成功"
                                        , Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this
                                        , "用户名或密码错误"
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
