package com.zjc.drivingSchoolS.ui.apply;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.mobo.mobolibrary.ui.base.ZBaseActivity;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.ui.learn.LearnApplyFragment;


/**
 * @author Z
 * @Filename LoginActivity.java
 * @Date 2015.09.14
 * @description 登录activity
 */
public class ApplyActivity extends ZBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_act);
    }

    @Override
    protected void initBaseView() {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        LearnApplyFragment fragment = new LearnApplyFragment();
        trans.addToBackStack(null);
        trans.add(R.id.root, fragment).commit();
    }
}
