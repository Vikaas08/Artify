package com.example.hw4_cs571_spring_25;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private final Fragment[] fragments;
    private final String[] fragmentTitles;
    public ViewPagerAdapter(@NonNull FragmentActivity fa, Fragment[] fragments, String[] fragmentTitles) {
        super(fa);
        this.fragments = fragments;
        this.fragmentTitles = fragmentTitles;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments[position];
    }

    @Override
    public int getItemCount() {
        return fragments.length;
    }

    public CharSequence getPageTitle(int position) {
        return fragmentTitles[position];
    }
}
