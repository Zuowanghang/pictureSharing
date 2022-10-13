package com.example.picturesharing.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.picturesharing.ItemFragment;
import com.example.picturesharing.R;
import com.example.picturesharing.adapter.MyFragmentTitleAdapter;
import com.example.picturesharing.databinding.FragmentHomeBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private List<Fragment> mFragmentList;
    private List<String> titleList;
    private MyFragmentTitleAdapter adapter;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewPager = view.findViewById(R.id.homeViewPager);
        mTabLayout = view.findViewById(R.id.tabLayout);

        // 初始化数据
        initData();

        adapter = new MyFragmentTitleAdapter(getChildFragmentManager(), mFragmentList, titleList);
        mViewPager.setAdapter(adapter);

        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initData() {
        mFragmentList = new ArrayList<>();

        // New 出一堆 VPFragment 来，然后添加进 List 中
//        ItemFragment f1 = ItemFragment.newInstance("关注");
//        ItemFragment f2 = ItemFragment.newInstance("发现");
//
//        mFragmentList.add(f1);
//        mFragmentList.add(f2);
//
//        titleList = new ArrayList<>();
//        titleList.add("关注");
//        titleList.add("发现");
    }
}