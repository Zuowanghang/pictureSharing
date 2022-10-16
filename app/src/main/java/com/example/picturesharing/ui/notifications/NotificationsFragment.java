package com.example.picturesharing.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.picturesharing.BlankFragment;
import com.example.picturesharing.MySettings;
import com.example.picturesharing.PersonalInfoModify;
import com.example.picturesharing.R;
import com.example.picturesharing.adapter.MyFragmentTitleAdapter;
import com.example.picturesharing.databinding.FragmentNotificationsBinding;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsFragment extends Fragment {
    private Button modify;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FragmentNotificationsBinding binding;
    private List<Fragment> fragmentList;
    private List<String> titleList;
    private MyFragmentTitleAdapter adapter;
    private ImageView menu;
    private CircleImageView topImage;
    private AppBarLayout appBarLayout;
    private NestedScrollView scrollView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        topImage = binding.topImage;
        topImage.setVisibility(View.INVISIBLE);

        appBarLayout = binding.appBarLayout;
        appBarLayout.addOnOffsetChangedListener(new AppBarListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.COLLAPSED) {
                    topImage.setVisibility(View.VISIBLE);
                } else {
                    topImage.setVisibility(View.GONE);
                }
            }
        });

        // 解决 nestedScrollView 嵌套 ViewPager 时不能左右滑动切换页面的问题
        scrollView = binding.nestedScrollView;
        scrollView.setFillViewport(true);

        modify = binding.modify;
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(requireActivity(), PersonalInfoModify.class);
                startActivity(i);
            }
        });

        menu = binding.menu;
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(requireActivity(), MySettings.class);
                startActivity(i);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.personalViewPager);
        tabLayout = view.findViewById(R.id.tabLayoutPersonal);

        // 初始化数据
        initData();

        adapter = new MyFragmentTitleAdapter(getChildFragmentManager(), fragmentList, titleList);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initData() {
        fragmentList = new ArrayList<>();
        titleList = new ArrayList<>();

        fragmentList.add(BlankFragment.newInstance("动态", ""));
        fragmentList.add(CollectFragment.newInstance("收藏"));
        fragmentList.add(HasLikeFragment.newInstance("赞过"));
        fragmentList.add(SavePictureFragment.newInstance("草稿箱"));

        titleList.add("关注");
        titleList.add("收藏");
        titleList.add("赞过");
        titleList.add("草稿箱");
    }

    public static abstract class AppBarListener implements AppBarLayout.OnOffsetChangedListener {
        enum State {
            EXPANDED,//展开
            COLLAPSED,//折叠
            INTERMEDIATE//中间状态
        }

        private State mCurrentState = State.INTERMEDIATE;

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (verticalOffset == 0) {
                if (mCurrentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED);
                }
                mCurrentState = State.EXPANDED;
            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                if (mCurrentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED);
                }
                mCurrentState = State.COLLAPSED;
            } else {
                if (mCurrentState != State.INTERMEDIATE) {
                    onStateChanged(appBarLayout, State.INTERMEDIATE);
                }
                mCurrentState = State.INTERMEDIATE;
            }
        }

        public abstract void onStateChanged(AppBarLayout appBarLayout, State state);
    }
}