package com.example.xuqi.qqdemo.view;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.example.xuqi.qqdemo.R;
import com.example.xuqi.qqdemo.adapter.GuidViewPagerAdapter;
import com.example.xuqi.qqdemo.util.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuqi on 17/3/23.
 * 引导页
 */

public class GuidActivity extends BaseActivity implements View.OnClickListener {
    private ViewPager mViewPager;
    //容器
    private List<View> mList = new ArrayList<>();
    private View view1, view2, view3;
    private GuidViewPagerAdapter guidAdapter;
    private ImageView point1, point2, point3,iv_back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
        L.d("guidactivity");
    }

    private void initView() {
        point1 = (ImageView) findViewById(R.id.point1);
        point2 = (ImageView) findViewById(R.id.point2);
        point3 = (ImageView) findViewById(R.id.point3);

        mViewPager = (ViewPager) findViewById(R.id.mViewPager);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);

        view1 = View.inflate(this, R.layout.guide_fragment_one, null);
        view2 = View.inflate(this, R.layout.guide_fragment_two, null);
        view3 = View.inflate(this, R.layout.guide_fragment_three, null);
        view3.findViewById(R.id.btn_start).setOnClickListener(this);
        mList.add(view1);
        mList.add(view2);
        mList.add(view3);

        guidAdapter = new GuidViewPagerAdapter(mList);
        mViewPager.setAdapter(guidAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                L.i("position = " + position);
                switch (position) {
                    case 0:
                        setPointImg(true, false, false);
                        iv_back.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        setPointImg(false, true, false);
                        iv_back.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        setPointImg(false, false, true);
                        iv_back.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    // 设置小圆点的选中效果
    private void setPointImg(boolean isSelected1, boolean isSelected2, boolean isSelected3) {
        if (isSelected1) {
            point1.setBackgroundResource(R.drawable.point_on);
        } else {
            point1.setBackgroundResource(R.drawable.point_off);
        }

        if (isSelected2) {
            point2.setBackgroundResource(R.drawable.point_on);
        } else {
            point2.setBackgroundResource(R.drawable.point_off);
        }

        if (isSelected3) {
            point3.setBackgroundResource(R.drawable.point_on);
        } else {
            point3.setBackgroundResource(R.drawable.point_off);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 引导页第三页点击时间
            case R.id.btn_start:
            // 引导页第一第二页点击
            case R.id.iv_back:
                showActivity(MainActivity.class);
                finish();
                break;
        }
    }
}
