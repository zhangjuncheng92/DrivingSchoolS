package com.zjc.drivingSchoolS.ui.order;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.mobo.mobolibrary.ui.base.ZBaseActivity;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.ui.apply.ApplyListFragment;
import com.zjc.drivingSchoolS.ui.main.StudyListFragment;

/**
 * Created by Administrator on 2016/8/17.
 */
public class OrderManagerActivity extends ZBaseActivity implements CompoundButton.OnCheckedChangeListener {
    private RadioButton rbTop1; // 按医生预约
    private RadioButton rbTop2; // 按时间预约
    private StudyListFragment studyFragment;
    private StudyListFragment applyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_manager_act);
        initTitle();
        initView();
        initFragment();
    }

    private void initTitle() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.comm_back);
        setSupportActionBar(mToolbar);
        OnBackPressClick onBackPressClick = new OnBackPressClick();
        mToolbar.setNavigationOnClickListener(onBackPressClick);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initView() {
        rbTop1 = (RadioButton) findViewById(R.id.topic_button1);
        rbTop2 = (RadioButton) findViewById(R.id.topic_button2);

        rbTop1.setOnCheckedChangeListener(this);
        rbTop2.setOnCheckedChangeListener(this);
    }

    private void initFragment() {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.addToBackStack(null);
        studyFragment = new StudyListFragment();
        applyFragment = new StudyListFragment();
        trans.add(R.id.root, studyFragment, "StudyListFragment").add(R.id.root, applyFragment, "ApplyListFragment").show(studyFragment).hide(applyFragment).commit();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int i = buttonView.getId();
        FragmentTransaction trans = getFragmentTransaction();
        if (i == R.id.topic_button1) {
            if (isChecked) {
                trans.show(studyFragment).hide(applyFragment).commit();
            }
        } else if (i == R.id.topic_button2) {
            if (isChecked) {
                trans.show(applyFragment).hide(studyFragment).commit();
            }
        }
    }

    public FragmentTransaction getFragmentTransaction() {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(com.mobo.mobolibrary.R.anim.activity_open_enter_slide_in_left, com.mobo.mobolibrary.R.anim.activity_open_exit_fade_back, com.mobo.mobolibrary.R.anim.activity_close_enter_fade_forward, com.mobo.mobolibrary.R.anim.activity_close_exit_slide_out_right);
        return trans;
    }
}
