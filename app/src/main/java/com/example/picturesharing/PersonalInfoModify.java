package com.example.picturesharing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.donkingliang.imageselector.utils.ImageSelector;
import com.example.picturesharing.pojo.UserInfo;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalInfoModify extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE = 0x00000011;
    private static final int PERMISSION_WRITE_EXTERNAL_REQUEST_CODE = 0x00000012;
    private ImageView cancelEditing;
    private CircleImageView avatar;
    private LinearLayout l1;
    private LinearLayout l2;
    private LinearLayout l3;
    private final UserInfo info = new UserInfo();
    private Button save;
    private TextView name;
    private TextView sex;
    private TextView introduction;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_personal_info);

        cancelEditing = findViewById(R.id.cancelEditing);
        avatar = findViewById(R.id.clickToChange);
        l1 = findViewById(R.id.modify_username);
        l2 = findViewById(R.id.modify_sex);
        l3 = findViewById(R.id.modify_sign);
        save = findViewById(R.id.save_change);

        // 获取权限
        int hasWriteExternalPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteExternalPermission == PackageManager.PERMISSION_GRANTED) {
            //预加载手机图片。加载图片前，请确保app有读取储存卡权限
            ImageSelector.preload(this);
        } else {
            //没有权限，申请权限。
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_REQUEST_CODE);
        }

        // 点击事件
        avatar.setOnClickListener(this);
        l1.setOnClickListener(this);
        l2.setOnClickListener(this);
        l3.setOnClickListener(this);
        save.setOnClickListener(this);

        name = findViewById(R.id.tv_user);
        sex= findViewById(R.id.tv_sex);
        introduction = findViewById(R.id.tv_sign);

        cancelEditing.setOnClickListener(v -> {
            // 如果个人主页不能刷新数据，可以选择在这里将 UserInfo 对象设置出去
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_CODE && data != null) {
                ArrayList<String> images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
                boolean isCameraImage = data.getBooleanExtra(ImageSelector.IS_CAMERA_IMAGE, false);
//            Log.d("ImageSelector", "是否是拍照图片：" + isCameraImage);
                for (String str : images) {
                    Glide.with(this).load(str).into(this.avatar);
                    info.setImage(str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理权限申请的回调。
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_WRITE_EXTERNAL_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //预加载手机图片
                ImageSelector.preload(this);
            }  //拒绝权限。

        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clickToChange: {
                try {
                    //单选
                    ImageSelector.builder()
                            .useCamera(true) // 设置是否使用拍照
                            .setSingle(true)  //设置是否单选
                            .canPreview(true) //是否点击放大图片查看,，默认为true
                            .start(this, REQUEST_CODE); // 打开相册
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

            case R.id.modify_username: {
                final EditText editText = new EditText(this);
                AlertDialog.Builder inputDialog =
                        new AlertDialog.Builder(this);
                inputDialog.setTitle("请输入用户名").setView(editText);
                inputDialog.setPositiveButton("确定",
                        (dialog, which) -> {
                            info.setName(editText.getText().toString());
                            name.setText(info.getName());
                        }).show();
                break;
            }

            case R.id.modify_sex: {
                final String[] items = {"男", "女", "保密"};
                final int[] yourChoice = {-1};
                AlertDialog.Builder singleChoiceDialog =
                        new AlertDialog.Builder(PersonalInfoModify.this);
                singleChoiceDialog.setTitle("请选择性别");
                // 第二个参数是默认选项，此处设置为0
                singleChoiceDialog.setSingleChoiceItems(items, 0,
                        (dialog, which) -> yourChoice[0] = which);
                singleChoiceDialog.setPositiveButton("确定",
                        (dialog, which) -> {
                            if (yourChoice[0] != -1) {
                                info.setSex(items[yourChoice[0]]);
//                                    String str = items[yourChoice[0]];
                                sex.setText(items[yourChoice[0]]);
                            }
                        });
                singleChoiceDialog.show();
                break;
            }
            case R.id.modify_sign: {
                final EditText editText = new EditText(this);
                AlertDialog.Builder inputDialog =
                        new AlertDialog.Builder(this);
                inputDialog.setTitle("请输入个人介绍").setView(editText);
                inputDialog.setPositiveButton("确定",
                        (dialog, which) -> {
                            info.setSignature(editText.getText().toString());
                            introduction.setText(info.getSignature());
                        }).show();
                break;
            }
            case R.id.save_change: {
                // 保存资料，在上面已经将页面上的数据都封装到了 UserInfo 对象中

                break;
            }
        }
    }
}
