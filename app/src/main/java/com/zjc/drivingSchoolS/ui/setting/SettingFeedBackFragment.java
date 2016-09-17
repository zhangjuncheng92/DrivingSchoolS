package com.zjc.drivingSchoolS.ui.setting;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.mobo.mobolibrary.ui.base.ZBaseToolBarFragment;
import com.mobo.mobolibrary.util.Util;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.api.ApiHttpClient;
import com.zjc.drivingSchoolS.api.ResultResponseHandler;
import com.zjc.drivingSchoolS.db.SharePreferences.SharePreferencesUtil;

/**
 * @author Z
 * @Filename SettingFeedBackFragment.java
 * @Date 2015.11.30
 * @description 设置-意见反馈界面
 */
public class SettingFeedBackFragment extends ZBaseToolBarFragment implements View.OnClickListener, TextWatcher {

    private EditText edtTitle;
    private EditText edtSend;
    private Button btnSend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int inflateContentView() {
        return R.layout.personal_center_feedback_frg;
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.
                LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        initView();
        initEvent();
    }


    private void initView() {
        btnSend = (Button) rootView.findViewById(R.id.setting_feedback_btnsend);
        edtSend = (EditText) rootView.findViewById(R.id.setting_feedback_edtsend);
        edtTitle = (EditText) rootView.findViewById(R.id.setting_feedback_edtcontact);
    }

    private void initEvent() {
        btnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_feedback_btnsend:
                sendFeedBack();
                break;
        }
    }

    private void sendFeedBack() {
        String content = edtSend.getEditableText().toString();
        String title = edtTitle.getEditableText().toString();
        if (TextUtils.isEmpty(content)) {
            Util.showCustomMsg("请输入反馈内容");
            return;
        }
        if (TextUtils.isEmpty(title)) {
            Util.showCustomMsg("请输入手机号码");
            return;
        }

        ApiHttpClient.getInstance().feedback(content, SharePreferencesUtil.getInstance().readUser().getSchoolname(), title, SharePreferencesUtil.getInstance().readUser().getUid(), new ResultResponseHandler(getActivity(), "正在提交反馈") {

            @Override
            public void onResultSuccess(String result) {
                getFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        //输入验证，不符合条件，按钮显示灰色
        if (TextUtils.isEmpty(edtSend.getEditableText().toString())) {
            btnSend.setEnabled(false);
        } else {
            btnSend.setEnabled(true);
        }
    }

    @Override
    protected void setTitle() {
        setTitle(mToolbar, "意见反馈");
    }


}
