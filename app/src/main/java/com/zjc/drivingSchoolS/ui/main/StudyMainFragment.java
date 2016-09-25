package com.zjc.drivingSchoolS.ui.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.mobo.mobolibrary.ui.base.ZBaseFragment;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.ui.main.adapter.StudyMainPagerAdapter;
import com.zjc.drivingSchoolS.utils.ConstantsParams;

import java.util.ArrayList;

/**
 * @author Z
 * @Filename ConsultListActivity.java
 * @Date 2016.09.14
 * @description 我的会诊控制器
 */
public class StudyMainFragment extends ZBaseFragment {
    public ArrayList<Fragment> mFragmentList = new ArrayList<>();

    private ViewPager mPager;// 页卡内容
    private TabLayout mTabLayout;

    @Override
    protected int inflateContentView() {
        return R.layout.study_main_pager_act;
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InitViewPager();
        initTab();
    }

    private void InitViewPager() {
        mPager = (ViewPager) rootView.findViewById(R.id.myconferenceVPager);
        //会诊列表界面
        StudyReceiveFragment startFragment = StudyReceiveFragment.newInstance(ConstantsParams.STUDY_TYPE_ALL);
        StudyReceiveFragment receiveFragment = StudyReceiveFragment.newInstance(ConstantsParams.STUDY_TYPE_VIP);
        StudyReceiveFragment finishFragment = StudyReceiveFragment.newInstance(ConstantsParams.STUDY_TYPE_COMM);
        mFragmentList.add(startFragment);
        mFragmentList.add(receiveFragment);
        mFragmentList.add(finishFragment);
        mPager.setAdapter(new StudyMainPagerAdapter(getChildFragmentManager(), mFragmentList));
    }

    private void initTab() {
        mTabLayout = (TabLayout) rootView.findViewById(R.id.personal_act_tab);
        mTabLayout.setupWithViewPager(mPager);
    }
}
