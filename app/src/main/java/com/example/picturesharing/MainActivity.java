package com.example.picturesharing;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.InputType;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.example.picturesharing.loginAndRegister.Register;
import com.example.picturesharing.pojo.LogInPojo;
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

public class MainActivity extends AppCompatActivity {
    private boolean bPwdSwitch = false;
    private EditText username;
    private EditText etPwd;
    private Button button;
    private TextView forgetPassword;
    private TextView register;
    private LogInPojo info;

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

        final ImageView ivPwdSwitch = findViewById(R.id.iv_pwd_switch);
        username = findViewById(R.id.logInUsername);
        etPwd = findViewById(R.id.etPwd);


        ivPwdSwitch.setOnClickListener(v -> {
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
        });

        button = findViewById(R.id.signIn);
        button.setOnClickListener(view12 -> {
            // 在此处添加使用登录接口的代码
            String name = username.getText().toString();
            String pwd = etPwd.getText().toString();

            if (name.equals("") || pwd.equals("")) {
                Toast.makeText(this, "请输入用户名和密码！", Toast.LENGTH_SHORT).show();
            }

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
                        Toast.makeText(this, "密码错错误！", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "登录成功": {
                        Intent i = new Intent(MainActivity.this, HomePage.class);
                        startActivity(i);
                        break;
                    }
                    default:
                        break;
                }
            }
        });

        // 添加文字下划线
        forgetPassword = findViewById(R.id.forgetPassword);
        forgetPassword.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        forgetPassword.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, HomePage.class);
            startActivity(i);
        });

        register = findViewById(R.id.register);
        register.setOnClickListener(v -> {
            System.out.println("Register click");
            String str = "message";
            Intent intent = new Intent(MainActivity.this, Register.class);
            startActivity(intent);
        });
    }

    private void post(String name, String pwd) {
        new Thread(() -> {
           // 请求路径
            String url = "http://47.107.52.7:88/member/photo/user/login?password=" + pwd + "&username=" + name;

            // 添加请求头
            Headers headers = new Headers.Builder()
                    .add("appId", "37f41c68f373453187f22ba15da4b36a")
                    .add("appSecret", "654010ffb1570029c496491c6769a29712897")
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
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }
}