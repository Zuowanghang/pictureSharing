package com.example.picturesharing;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.picturesharing.adapter.ImageTitleAdapter;
import com.example.picturesharing.pojo.DataBean;
import com.youth.banner.Banner;
import com.youth.banner.config.BannerConfig;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.util.BannerUtils;


public class ShareDetails extends AppCompatActivity {
    private ImageView back;
    private Banner banner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_details);

        // 将状态栏颜色设为透明
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        back = findViewById(R.id.exitDetail);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        banner = findViewById(R.id.banner);
        // 设置 Adapter
        banner.setAdapter(new ImageTitleAdapter(DataBean.getTestData()));
        // 图片轮播指示器
        banner.setIndicator(new CircleIndicator(this));
        // 设置指示器选中后的颜色
        banner.setIndicatorSelectedColorRes(R.color.personalData);
        // 指示器位置
        banner.setIndicatorGravity(IndicatorConfig.Direction.CENTER);
        banner.setIndicatorMargins(new IndicatorConfig.Margins(0, 0,
                BannerConfig.INDICATOR_MARGIN, (int) BannerUtils.dp2px(12)));
        banner.addBannerLifecycleObserver(this);
    }
}