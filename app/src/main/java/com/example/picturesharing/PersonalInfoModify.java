package com.example.picturesharing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.donkingliang.imageselector.utils.ImageSelector;
import com.example.picturesharing.MyToast.MyApplication;
import com.example.picturesharing.placeholder.PlaceholderContent;
import com.example.picturesharing.pojo.PostImage;
import com.example.picturesharing.pojo.UserData;
import com.example.picturesharing.pojo.UserInfo;
import com.example.picturesharing.ui.notifications.MyPostPictureFragment;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    private String path;
    LoadingDialog ld;
    private int n;

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

        // ????????????
        int hasWriteExternalPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteExternalPermission == PackageManager.PERMISSION_GRANTED) {
            //???????????????????????????????????????????????????app????????????????????????
            ImageSelector.preload(this);
        } else {
            //??????????????????????????????
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_REQUEST_CODE);
        }

        // ????????????
        avatar.setOnClickListener(this);
        l1.setOnClickListener(this);
        l2.setOnClickListener(this);
        l3.setOnClickListener(this);
        save.setOnClickListener(this);

        name = findViewById(R.id.tv_user);
        sex = findViewById(R.id.tv_sex);
        introduction = findViewById(R.id.tv_sign);

        cancelEditing.setOnClickListener(v -> {
            // ??????????????????????????????????????????????????????????????? UserInfo ??????????????????
            finish();
        });
        if(UserData.sex == 0){
            sex.setText("???");
        }else {
            sex.setText("???");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_CODE && data != null) {
                ArrayList<String> images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
                boolean isCameraImage = data.getBooleanExtra(ImageSelector.IS_CAMERA_IMAGE, false);
//            Log.d("ImageSelector", "????????????????????????" + isCameraImage);
                for (String str : images) {
                    Glide.with(this).load(str).into(this.avatar);
                    path = str;
                    System.out.println("?????????" + path);
                    info.setImage(str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ??????????????????????????????
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_WRITE_EXTERNAL_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //?????????????????????
                ImageSelector.preload(this);
            }  //???????????????

        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clickToChange: {
                try {
                    //??????
                    ImageSelector.builder()
                            .useCamera(true) // ????????????????????????
                            .setSingle(true)  //??????????????????
                            .canPreview(true) //??????????????????????????????,????????????true
                            .start(this, REQUEST_CODE); // ????????????
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

            case R.id.modify_username: {
                final EditText editText = new EditText(this);
                AlertDialog.Builder inputDialog =
                        new AlertDialog.Builder(this);
                inputDialog.setTitle("??????????????????").setView(editText);
                inputDialog.setPositiveButton("??????",
                        (dialog, which) -> {
                            info.setName(editText.getText().toString());
                            name.setText(info.getName());
                        }).show();
                break;
            }

            case R.id.modify_sex: {

                final String[] items = {"???", "???",};
                final int[] yourChoice = {-1};
                AlertDialog.Builder singleChoiceDialog =
                        new AlertDialog.Builder(PersonalInfoModify.this);
                singleChoiceDialog.setTitle("???????????????");
                // ????????????????????????????????????????????????0
                singleChoiceDialog.setSingleChoiceItems(items, 0,
                        (dialog, which) -> yourChoice[0] = which);
                singleChoiceDialog.setPositiveButton("??????",
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
                inputDialog.setTitle("?????????????????????").setView(editText);
                inputDialog.setPositiveButton("??????",
                        (dialog, which) -> {
                            info.setSignature(editText.getText().toString());
                            introduction.setText(info.getSignature());
                        }).show();
                break;
            }
            case R.id.save_change: {
                // ?????????????????????????????????????????????????????????????????? UserInfo ?????????
                System.out.println(path + "??????");
                if (path == null) {
                    Toast.makeText(PersonalInfoModify.this, "???????????????", Toast.LENGTH_SHORT).show();
                } else {
                    if (!name.getText().toString().equals("") && !sex.getText().toString().equals("") && !introduction.getText().toString().equals("")) {
                        ld = new LoadingDialog(this);
                        ld.setLoadingText("???????????????")
                                .setSuccessText("????????????")//??????????????????????????????
                                .setFailedText("????????????")
                                .setInterceptBack(true)
                                .show();
                        postPicture(path, name.getText().toString(), sex.getText().toString(), introduction.getText().toString());
                    } else {
                        Toast.makeText(MyApplication.getContext(), "???????????????????????????????????????", Toast.LENGTH_SHORT).show();
                    }
                }


                break;
            }
        }
    }


    private void post(String path, String mName, String mSex, String mIntroduction) {

        new Thread(() -> {
             n = 0;
            switch (mSex) {
                case "???":
                    n = 1;
                    break;
                case "???":
                    n = 0;
                default:
                    break;
            }
            // url??????
            String url = "http://47.107.52.7:88/member/photo/user/update";

            // ?????????
            Headers headers = new Headers.Builder()
                    .add("appId", UserData.appId)
                    .add("appSecret", UserData.appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();


            // ?????????
            // PS.??????????????????????????????????????????????????????????????????fastjson???????????????json???
//            System.out.println("????????????"+path+"id"+UserData.getUserid()+"????????????"+introduction.getText().toString()+"name"+UserData.getUserName());
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("avatar", path);
            bodyMap.put("id", "" + UserData.getUserid());
            bodyMap.put("introduce", "" + mIntroduction);
            bodyMap.put("sex", "" + n);
            bodyMap.put("username", "" + mName);

            // ???Map??????????????????????????????????????????
            String body = JSON.toJSONString(bodyMap);
            System.out.println(body);
            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            //??????????????????
            Request request = new Request.Builder()
                    .url(url)
                    // ???????????????????????????
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //?????????????????????callback????????????
                client.newCall(request).enqueue(callback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * ??????
     */
    private final Callback callback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            //TODO ??????????????????
            e.printStackTrace();
        }

        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            //TODO ??????????????????

            // ??????????????????json???
            String jsonData = response.body().string();
            Log.i("????????????", jsonData);
            // ??????json???????????????????????????
            PlaceholderContent data;
            data = JSON.parseObject(jsonData, PlaceholderContent.class);
            if (data.getCode() == 200) {
                PersonalInfoModify.this.runOnUiThread(() -> {
                    ld.loadSuccess();
                    UserData.setUserName(name.getText().toString());
                    UserData.introduce=introduction.getText().toString();
                 if(n == 0){
                     UserData.sex = 0;
                 }else {
                     UserData.sex = 1;
                 }

                });

            }else {

                PersonalInfoModify.this.runOnUiThread(() -> {
                    ld.loadFailed();

                });
            }
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Looper.prepare();
//
//                        if(data.getCode() == 200 ){
//                            Toasty.success(MyApplication.getContext(), "??????????????????!", Toast.LENGTH_SHORT, true).show();
//
//                        }else {
//                            Toasty.error(MyApplication.getContext(), "??????????????????!", Toast.LENGTH_SHORT, true).show();
//                        }
//                        Looper.loop();
//                    }
//                }).start();

        }


    };


    private void postPicture(String path, String mName, String mSex, String mIntroduction) {
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                //TODO ??????????????????
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, Response response) throws IOException {
                //TODO ??????????????????

                // ??????????????????json???
                //
                String jsonData = response.body().string();
                Log.d("??????????????????", jsonData);
                // ??????json???????????????????????????
                PostImage data;
                data = JSON.parseObject(jsonData, PostImage.class);

                if (data.getCode() == 200) {
                    String[] url = data.getData().getImageUrlList();
                    post(url[0], mName, mSex, mIntroduction);
                }
                //?????????????????????????????????

            }
        };

        new Thread(() -> {


            // url??????
            String url = "http://47.107.52.7:88/member/photo/image/upload";
            Headers headers = new Headers.Builder()
                    .add("appId", UserData.appId)
                    .add("appSecret", UserData.appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();


            MediaType mediaType = MediaType.parse("multipart/form-data");//???????????????????????????????????????
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            //?????????????????????
            System.out.println(path + "??????");
            String fname = path;
            File tempfile = new File(fname);
            //?????????????????????????????????????????????
            builder.setType(MultipartBody.FORM)

                    .addFormDataPart( //???Builder?????????????????????
                            "fileList",  //???????????????
                            tempfile.getName(), //?????????????????????????????????????????????
                            RequestBody.create(MediaType.parse("multipart/form-data"), tempfile)//??????RequestBody???????????????????????????
                    );

            MultipartBody requestBody = builder.build();
            Request request = new Request.Builder()
                    .headers(headers)
                    .url(url)
                    .post(requestBody)
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //?????????????????????callback????????????
                client.newCall(request).enqueue(callback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }


    public static class ResponseBody<T> {

        /**
         * ???????????????
         */
        private int code;
        /**
         * ??????????????????
         */
        private String msg;
        /**
         * ????????????
         */
        private T data;

        public ResponseBody() {
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public T getData() {
            return data;
        }

        @NonNull
        @Override
        public String toString() {
            return "ResponseBody{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    ", data=" + data +
                    '}';
        }
    }


    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyApplication.getContext());
        builder.setTitle("??????");
        builder.setMessage("????????????????????????");
        builder.setPositiveButton("??????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.setNegativeButton("??????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
