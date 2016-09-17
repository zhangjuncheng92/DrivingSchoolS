package com.zjc.drivingSchoolS.ui.setting;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.mobo.mobolibrary.ui.base.ZBaseActivity;
import com.zjc.drivingSchoolS.R;

/**
 * Created by Administrator on 15.11.6.
 */
public class SettingActivity extends ZBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_act);
    }

    @Override
    protected void initBaseView() {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        SettingFragment fragment = new SettingFragment();
        trans.addToBackStack(null);
        trans.add(R.id.root, fragment).commit();
    }
}
