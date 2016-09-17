package com.zjc.drivingSchoolS.ui.setting;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mobo.mobolibrary.ui.base.ZBaseToolBarFragment;
import com.qihoo.updatesdk.lib.UpdateHelper;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.eventbus.MainLogoutEvent;
import com.zjc.drivingSchoolS.ui.login.LoginCredittermsFragment;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 15.11.6.
 */
public class SettingFragment extends ZBaseToolBarFragment implements View.OnClickListener {

    private RelativeLayout mRelay_update;
    private RelativeLayout mRelay_terms;
    private RelativeLayout mRelayAbout;
    private RelativeLayout mRelayFeedBack;
    private RelativeLayout mRelayModify;

    private ToggleButton tbJpush;

    private ProgressDialog progressDialog;
    private TextView logout;

    @Override
    protected void setTitle() {
        setTitle(mToolbar, R.string.title_more);
    }

    @Override
    protected int inflateContentView() {
        return R.layout.personal_center_setting_frg;
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initView();
        initListener();
        initJpushButton();
    }

    private void initView() {
        mRelay_update = (RelativeLayout) rootView.findViewById(R.id.setting_relay_update);
        mRelay_terms = (RelativeLayout) rootView.findViewById(R.id.setting_relay_service);
        mRelayAbout = (RelativeLayout) rootView.findViewById(R.id.setting_relay_about);
        mRelayFeedBack = (RelativeLayout) rootView.findViewById(R.id.setting_relay_advice);
        mRelayModify = (RelativeLayout) rootView.findViewById(R.id.setting_relay_modify);
        tbJpush = (ToggleButton) rootView.findViewById(R.id.personal_center_set_frg_jpush_button);
        logout = (TextView) rootView.findViewById(R.id.personal_center_setting_tv_logout);
    }

    private void initListener() {
        mRelay_update.setOnClickListener(this);
        mRelay_terms.setOnClickListener(this);
        mRelayAbout.setOnClickListener(this);
        mRelayFeedBack.setOnClickListener(this);
        mRelayModify.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    private void logout() {
        EventBus.getDefault().post(new MainLogoutEvent());
        getActivity().finish();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.personal_center_setting_tv_logout:
                logout();
                break;
            case R.id.setting_relay_service:
                LoginCredittermsFragment credittermsFragment = new LoginCredittermsFragment();
                replaceFrg(credittermsFragment, null);
                break;
            case R.id.setting_relay_update:
                checkUpdata();
                break;
            case R.id.setting_relay_about:
                SettingAboutFragment aboutFragment = new SettingAboutFragment();
                replaceFrg(aboutFragment, null);
                break;
            case R.id.setting_relay_advice:
                SettingFeedBackFragment feedBackFragment = new SettingFeedBackFragment();
                replaceFrg(feedBackFragment, null);
                break;
            case R.id.setting_relay_modify:
                SettingModifyFragment modifyFragment = new SettingModifyFragment();
                replaceFrg(modifyFragment, null);
                break;
        }
    }

    private void initJpushButton() {
        if (JPushInterface.isPushStopped(getActivity())) {
            tbJpush.setChecked(false);
        } else {
            tbJpush.setChecked(true);
        }
        tbJpush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (tbJpush.isChecked()) {
                    JPushInterface.resumePush(getActivity());
                } else {
                    JPushInterface.stopPush(getActivity());
                }
            }
        });
    }

    private void checkUpdata() {
        UpdateHelper.getInstance().manualUpdate("com.zjc.drivingSchoolS");
    }
}
