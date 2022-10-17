package com.example.picturesharing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.example.picturesharing.pojo.LogInPojo;
import com.example.picturesharing.pojo.User;
import com.example.picturesharing.pojo.UserData;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean bPwdSwitch = false;
    private EditText username;
    private EditText etPwd;
    private Button button;
    private TextView forgetPassword;
    private TextView remenber;
    private TextView register;
    private LogInPojo info;
    private CheckBox checkBox;
    private ImageView ivPwdSwitch;
    private String spFileName;
    private String accountKey;
    private String accountPassword;
    private String rememberPasswordKey;
    private SharedPreferences spFile;
    private SharedPreferences.Editor editor;
    private  boolean rember = true;
    private SharedPreferences sp;

    public void setInfo(LogInPojo info) {
        this.info = info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // 设置背景图片透明度
//        View view = findViewById(R.id.logIn);
//        view.getBackground().setAlpha(120);

        // 隐藏应用标题
//        ActionBar bar = getSupportActionBar();
//        assert bar != null;
//        bar.hide();

        // 将状态栏颜色设为透明
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // 控件绑定
        ivPwdSwitch = findViewById(R.id.iv_pwd_switch);
        ivPwdSwitch.setOnClickListener(this);

        username = findViewById(R.id.logInUsername);
        etPwd = findViewById(R.id.etPwd);
        checkBox = findViewById(R.id.rememberPassword);

        // 记住密码
        checkBox.setOnClickListener(this);

        // 用户登录
        button = findViewById(R.id.signIn);
        button.setOnClickListener(this);

        // 添加文字下划线
        forgetPassword = findViewById(R.id.forgetPassword);
        forgetPassword.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        forgetPassword.setOnClickListener(this);

        remenber = findViewById(R.id.remember);
        remenber.setOnClickListener(this);


        // 注册页面入口
        register = findViewById(R.id.register);
        register.setOnClickListener(this);

        sp =  getSharedPreferences("userData", Context.MODE_PRIVATE);
        int i = sp.getInt("start",0);
        if(i == 1){
            checkBox.setChecked(true);
            username.setText(sp.getString("userName",""));
            etPwd.setText(sp.getString("userPwd",""));
        }
        else {
            checkBox.setChecked(false);
        }
    }

    // 登录接口
    private void post(String name, String pwd) {
        new Thread(() -> {
            // 请求路径
            String url = "http://47.107.52.7:88/member/photo/user/login?password=" + pwd + "&username=" + name;

            // 添加请求头
            Headers headers = new Headers.Builder()
                    .add("appId", UserData.appId)
                    .add("appSecret", UserData.appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            User u = new User(name, pwd);
            String body = JSON.toJSONString(u);
            System.out.println(body);

            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            // 请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入 callback 进行回调
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, IOException e) {
                        // 请求失败处理
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, Response response) throws IOException {
                        // 请求成功处
                        String jsonData = response.body().string();
                        LogInPojo info = JSON.parseObject(jsonData, LogInPojo.class);
                        System.out.println(info.toString());
                        setInfo(info);
                    }
                });
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signIn: {
                // 在此处添加使用登录接口的代码
                String name = username.getText().toString();
                String pwd = etPwd.getText().toString();


                if (name.equals("") || pwd.equals("")) {
                    Toast.makeText(this, "请输入用户名和密码！", Toast.LENGTH_SHORT).show();
                }//输入为空提示
                // 调用请求函数
                post(name, pwd);

                // 如果 info 域为空则说明没有请求成功
                if (info != null) {
                    switch (info.getMsg()) {
                        case "当前登录用户不存在": {
                            Toast.makeText(this, "请先注册！", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case "密码错误": {
                            Toast.makeText(this, "密码错误！", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case "登录成功": {
                            Intent i = new Intent(MainActivity.this, HomePage.class);
                            startActivity(i);
                            if(checkBox.isChecked()){

                                sp.edit().putString("userName",name).apply();//写入用户
                                sp.edit().putInt("start",1).apply();
                                sp.edit().putString("userPwd",pwd).apply();
                            }else {
                                sp.edit().putInt("start",0).apply();
                            }
                            UserData.avatar = info.getData().getAvatar();
                            UserData.introduce = info.getData().getIntroduce();
                            UserData.sex = info.getData().getSex();
                            UserData.setUserName(info.getData().getUsername());
                            UserData.setUserid(info.getData().getId());
                            UserData.setAppKey(info.getData().getAppKey());

                            break;
                        }
                        default:
                            break;
                    }
                }
                break;
            }
            //记住密码绑定checkbox
            case R.id.remember:{
                checkBox.setChecked(!checkBox.isChecked());
                break;
            }
            case R.id.iv_pwd_switch: {
                // 是否要展示密码明文
                bPwdSwitch = !bPwdSwitch;

                if (bPwdSwitch) {
                    ivPwdSwitch.setImageResource(
                            R.drawable.ic_baseline_visibility_24
                    );
                    etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    ivPwdSwitch.setImageResource(
                            R.drawable.ic_baseline_visibility_off_24
                    );
                    // 将 InputType 的值设置为 129，隐藏密码
                    etPwd.setInputType(
                            InputType.TYPE_TEXT_VARIATION_PASSWORD |
                                    InputType.TYPE_CLASS_TEXT
                    );
                    etPwd.setTypeface(Typeface.DEFAULT);
                }
                break;
            }
            case R.id.register: {
                //注册activity
                System.out.println("Register click");
                String str = "message";
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
                break;
            }
            case R.id.forgetPassword: {
                //挖忘记密码
                Intent i = new Intent(MainActivity.this, HomePage.class);
                startActivity(i);
                break;
            }
            case R.id.rememberPassword: {
                System.out.println("sssssssss");
//            //    记住密码
//                String name1 = username.getText().toString();
//                String pwd1 = etPwd.getText().toString();
//
//                sp.edit().putString("userName",name1).apply();//写入用户
//                sp.edit().putString("userPwd",pwd1).apply();


//


                break;
            }
            default:
                break;
        }
    }
}