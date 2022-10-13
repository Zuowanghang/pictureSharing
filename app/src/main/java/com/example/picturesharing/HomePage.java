package com.example.picturesharing;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.donkingliang.imageselector.utils.ImageSelector;
import com.example.picturesharing.databinding.ActivityHomePageBinding;
import com.example.picturesharing.ui.dashboard.DashboardFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity {
    private static final int REQUEST_CODE = 0x00000011;
    private static final int PERMISSION_WRITE_EXTERNAL_REQUEST_CODE = 0x00000012;
    private ActivityHomePageBinding binding;

    private DashboardFragment dashboardFragment = new DashboardFragment();

    private ArrayList<String> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //去除标题栏
//        ActionBar bar = this.getSupportActionBar();
//        if (bar != null) {
//            bar.hide();
//            System.out.println("已经隐藏了 ActionBar ");
//        } else{
//            System.out.println("问题不在导航栏");
//        }

        // 将状态栏颜色设为透明
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home_page);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        // 只支持单选
////        if (requestCode == 0) {
////            // 从相册返回数据
////            if (data != null) {
////                uri = data.getData();   // 得到图片的全路径
////                imageView.setImageURI(uri);  // 显示图片
////                bitmap = GetPhoto.photoToBit(getActivity(), uri);   // 获取图片二进制
////            }
////        } else {
////
////        }
//
//
//    }
}