package com.example.picturesharing.ui.home;

import android.content.Intent;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.picturesharing.ItemFragment;
import com.example.picturesharing.R;
import com.example.picturesharing.ShareDetails;
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

//        Button button = view.findViewById(R.id.btn);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(requireActivity(), ShareDetails.class);
//                startActivity(i);
//            }
//        });

        // ???????????????
        initData();

        adapter = new MyFragmentTitleAdapter(getChildFragmentManager(), mFragmentList, titleList);
        mViewPager.setAdapter(adapter);

        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initData() {
        mFragmentList = new ArrayList<>();

        // New ????????? VPFragment ????????????????????? List ???
//        ItemFragment f1 = ItemFragment.newInstance("??????");
        ItemFragment f2 = ItemFragment.newInstance("??????");
//
//        mFragmentList.add(f1);
        mFragmentList.add(f2);
//
        titleList = new ArrayList<>();
//        titleList.add("??????");
        titleList.add("??????");
    }
}