package com.zjc.drivingSchoolS.ui.setting;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobo.mobolibrary.ui.base.ZBaseToolBarFragment;
import com.mobo.mobolibrary.util.Util;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.ui.login.LoginCredittermsFragment;


/**
 * Created by Ones on 2015/11/30.
 * 设置-关于
 */
public class SettingAboutFragment extends ZBaseToolBarFragment implements View.OnClickListener {
    private ImageView ivGood;
    private TextView tvDynamic;
    private TextView tvVersion;
    private TextView tvYstk;
    private TextView tvPhone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected int inflateContentView() {
        return R.layout.personal_center_setting_about;
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        tvDynamic = (TextView) rootView.findViewById(R.id.setting_about_tv_creditterms);
        ivGood = (ImageView) rootView.findViewById(R.id.setting_about_good);
        tvVersion = (TextView) rootView.findViewById(R.id.setting_about_version);
        tvPhone = (TextView) rootView.findViewById(R.id.setting_about_Customer_service_phone);
        tvYstk = (TextView) rootView.findViewById(R.id.setting_about_tv_creditterms);
        tvYstk.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
    }

    private void initData() {
        tvVersion.setText(Util.getLocalVersionName(getContext()) + "版");
    }

    private void initEvent() {
        ivGood.setOnClickListener(this);
        tvPhone.setOnClickListener(this);
        tvDynamic.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_about_good:
                Util.showCustomMsg("随驾联盟");
                break;
            case R.id.setting_about_Customer_service_phone:
                String telephone = tvPhone.getText().toString();
                Util.call(getActivity(), telephone);
                break;
            case R.id.setting_about_tv_creditterms:
                LoginCredittermsFragment loginCredittermsFragment = new LoginCredittermsFragment();
                replaceFrg(loginCredittermsFragment, null);
                break;
        }
    }

    @Override
    protected void setTitle() {
        setTitle(mToolbar, "关于");
    }
}
