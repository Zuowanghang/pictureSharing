package com.example.picturesharing.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class MyFragmentTitleAdapter extends FragmentStatePagerAdapter {
    // fragment 对象集合，无论是哪一种 Fragment，只要是继承自 Fragment 类，就都可以使用它来使 TabLayout 跟随 Fragment 滑动
    private List<Fragment> fragmentList;
    private List<String> titleList;

    // 构造函数
    public MyFragmentTitleAdapter(@NonNull FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm);
        this.fragmentList = fragments;
        this.titleList = titles;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList == null ? null : fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList == null ? 0 : fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int pos) {
        return titleList == null ? null : titleList.get(pos);
    }
}
