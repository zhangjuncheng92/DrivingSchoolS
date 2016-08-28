package com.zjc.drivingSchoolS.ui.receive;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.mobo.mobolibrary.ui.base.ZBaseActivity;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.db.model.OrderItem;
import com.zjc.drivingSchoolS.db.model.School;
import com.zjc.drivingSchoolS.utils.Constants;


/**
 * @author Z
 * @Filename LearnActivity.java
 * @Date 2015.09.14
 * @description 登录activity
 */
public class ReceiveActivity extends ZBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_act);
    }

    @Override
    protected void initBaseView() {
        if (getIntent().getExtras() == null) {

        } else if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(Constants.ARGUMENT)) {
            OrderItem orderItem = (OrderItem) getIntent().getExtras().getSerializable(Constants.ARGUMENT);
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            ReceiveFragment fragment = ReceiveFragment.newInstance(orderItem);
            trans.addToBackStack(null);
            trans.add(R.id.root, fragment).commit();
        }
    }
}
