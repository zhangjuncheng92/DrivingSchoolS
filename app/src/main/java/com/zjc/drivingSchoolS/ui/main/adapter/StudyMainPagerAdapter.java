package com.zjc.drivingSchoolS.ui.main.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.mobo.mobolibrary.ui.base.adapter.FragmentViewPagerAdapter;
import com.zjc.drivingSchoolS.utils.ConstantsParams;

import java.util.List;

/**
 * Created by Administrator on 2015/7/22.
 */
public class StudyMainPagerAdapter extends FragmentViewPagerAdapter {
    private String tabTitles[] = new String[]{ConstantsParams.STUDY_TYPE_ALL_TEXT, ConstantsParams.STUDY_TYPE_VIP_TEXT, ConstantsParams.STUDY_TYPE_COMM_TEXT};

    public StudyMainPagerAdapter(FragmentManager fm, List<Fragment> lists) {
        super(fm, lists);
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
