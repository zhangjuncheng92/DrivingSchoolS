package com.zjc.drivingSchoolS.ui.order.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.mobo.mobolibrary.ui.base.adapter.FragmentViewPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2015/7/22.
 */
public class OrderMainAdapter extends FragmentViewPagerAdapter {
    private String tabTitles[] = new String[]{"学车订单", "报名订单"};

    public OrderMainAdapter(FragmentManager fm, List<Fragment> lists) {
        super(fm, lists);
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
