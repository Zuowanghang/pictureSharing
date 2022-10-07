package com.example.picturesharing.loginAndRegister;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.example.picturesharing.HomePage;
import com.example.picturesharing.MainActivity;
import com.example.picturesharing.R;
import com.example.picturesharing.pojo.LogInPojo;
import com.example.picturesharing.pojo.RegisterInfo;
import com.example.picturesharing.pojo.User;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Register extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private EditText confirm;
    private RegisterInfo info;
    private LogInPojo logInPojo;

    public void setInfo(RegisterInfo info) {
        this.info = info;
    }

    public void setLogInPojo(LogInPojo logInPojo) {
        this.logInPojo = logInPojo;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // 绑定视图
        Button register = findViewById(R.id.registerBtn);
        Button logIn = findViewById(R.id.logInBtn);
        username = findViewById(R.id.register_name);
        password = findViewById(R.id.register_password);
        confirm = findViewById(R.id.password_repeat);

        // 隐藏应用标题
        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.hide();

        // 将状态栏颜色设为透明
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // 注册按钮响应事件
        register.setOnClickListener(v -> {
            // 一定要获取点击时输入框中的内容，放在监听器外面就会一直为空
            String name = username.getText().toString();
            String pwd = password.getText().toString();
            String conf = confirm.getText().toString();

            if (name.equals("") || pwd.equals("") || conf.equals("")) {
                Toast.makeText(this, "请输入用户名、密码并重复密码！", Toast.LENGTH_SHORT).show();
            }

            // 验证两次输入的密码是否一致
            if (pwd.equals(conf)) {
                register(name, pwd);
            } else {
                Toast.makeText(this, "两次输入的密码不一致，请重试！", Toast.LENGTH_SHORT).show();
            }

            if (info != null) {
                if (info.getMsg() == null)
                    Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, info.getMsg(), Toast.LENGTH_SHORT).show();
            }
        });

        // 登录按钮点击事件
        logIn.setOnClickListener(v -> {
            String name = username.getText().toString();
            String pwd = password.getText().toString();

            if (name.equals("") || pwd.equals("")) {
                Toast.makeText(this, "请输入用户名和密码！", Toast.LENGTH_SHORT).show();
            }

            // 登录功能，读取 EditText 控件中的内容以及 用户 id 之后封装成 User 对象
            logIn(name, pwd);

            // 如果 logInPojo 域为空则说明没有请求成功
            if (logInPojo != null) {
                switch (logInPojo.getMsg()) {
                    case "当前登录用户不存在": {
                        Toast.makeText(this, "请先注册！", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "密码错误": {
                        Toast.makeText(this, "密码错误！", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "登录成功": {
                        String str = "userInfo";
                        Intent i = new Intent(this, HomePage.class);
                        i.putExtra(str, logInPojo.toString());
                        startActivity(i);
                        break;
                    }
                    default:
                        break;
                }
            }
        });
    }

    private void register(String name, String pwd) {
        new Thread(() -> {

            // 请求路径
            String url = "http://47.107.52.7:88/member/photo/user/register";

            // 添加请求头
            Headers headers = new Headers.Builder()
                    .add("appId", "833c135f35b54bc5a2f3f9efa81ea3ef")
                    .add("appSecret", "79728c6d6ae0cf10e419ab6575ced66594951")
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
                        //TODO 请求失败处理
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(@NonNull Call call, Response response) throws IOException {
                        //TODO 请求成功处
                        String jsonData = response.body().string();
                        RegisterInfo info = JSON.parseObject(jsonData, RegisterInfo.class);
                        setInfo(info);
                        System.out.println(info.toString());
                    }
                });
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    private void logIn(String name, String pwd) {
        new Thread(() -> {
            // 请求路径
            String url = "http://47.107.52.7:88/member/photo/user/login?password=" + pwd + "&username=" + name;

            // 添加请求头
            Headers headers = new Headers.Builder()
                    .add("appId", "833c135f35b54bc5a2f3f9efa81ea3ef")
                    .add("appSecret", "79728c6d6ae0cf10e419ab6575ced66594951")
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
                        //TODO 请求失败处理
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(@NonNull Call call, Response response) throws IOException {
                        //TODO 请求成功处
                        String jsonData = response.body().string();
                        LogInPojo info = JSON.parseObject(jsonData, LogInPojo.class);
                        System.out.println(info.toString());
                        setLogInPojo(info);
                    }
                });
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }
}
