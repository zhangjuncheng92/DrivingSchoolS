package com.zjc.drivingSchoolS.ui.apply;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.mobo.mobolibrary.ui.base.ZBaseActivity;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.utils.Constants;


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
        if (getIntent().getExtras() == null) {
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            ApplyReceiveFragment fragment = new ApplyReceiveFragment();
            trans.addToBackStack(null);
            trans.add(R.id.root, fragment).commit();
        } else if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(Constants.ARGUMENT)) {
//            String orId = (String) getIntent().getExtras().getSerializable(Constants.ARGUMENT);
//            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
//            ApplyDetailFragment fragment = ApplyDetailFragment.newInstance((orId));
//            trans.addToBackStack(null);
//            trans.add(R.id.root, fragment).commit();
        }
    }
}
