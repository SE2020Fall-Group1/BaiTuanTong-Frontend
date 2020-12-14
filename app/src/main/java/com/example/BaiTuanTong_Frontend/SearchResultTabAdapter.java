package com.example.BaiTuanTong_Frontend;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class SearchResultTabAdapter extends FragmentStateAdapter {

    private List<Fragment> fragments;

    public SearchResultTabAdapter(@NonNull FragmentActivity fa, List<Fragment> FragmentList) {
        super(fa);
        fragments = FragmentList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
