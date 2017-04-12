package com.example.xuqi.qqdemo.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by xuqi on 17/3/23.
 * 引导页ViewPager的适配器
 */

public class GuidViewPagerAdapter extends PagerAdapter{
    public List <View> mFragments;
    public GuidViewPagerAdapter(List<View> mFragments) {
        this.mFragments = mFragments;
    }
    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager) container).addView(mFragments.get(position));
        return mFragments.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(mFragments.get(position));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
