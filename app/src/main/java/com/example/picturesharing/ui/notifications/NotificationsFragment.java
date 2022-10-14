package com.example.picturesharing.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.picturesharing.AboutUs;
import com.example.picturesharing.MySettings;
import com.example.picturesharing.PersonalInfoModify;
import com.example.picturesharing.adapter.MyFragmentTitleAdapter;
import com.example.picturesharing.databinding.FragmentNotificationsBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class NotificationsFragment extends Fragment {
    private Button modify;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FragmentNotificationsBinding binding;
    private List<Fragment> fragmentList;
    private List<String> titleList;
    private MyFragmentTitleAdapter adapter;
    private ImageView menu;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        viewPager = view.findViewById(R.id.personalViewPager);
//        tabLayout = view.findViewById(R.id.tabLayoutPersonal);
//
//        // 初始化数据
//        initData();
//
//        adapter = new MyFragmentTitleAdapter(getChildFragmentManager(), fragmentList, titleList);
//        viewPager.setAdapter(adapter);
//        tabLayout.setupWithViewPager(viewPager);
//    }
//
//    private void initData() {
//        fragmentList = new ArrayList<>();
//        titleList = new ArrayList<>();
//
//        fragmentList.add(VPFragment.newInstance("动态", ""));
//        fragmentList.add(VPFragment.newInstance("收藏", ""));
//        fragmentList.add(VPFragment.newInstance("赞过", ""));
//
//        titleList.add("关注");
//        titleList.add("收藏");
//        titleList.add("赞过");
//    }
}