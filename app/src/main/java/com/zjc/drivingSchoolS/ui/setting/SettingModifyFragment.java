package com.zjc.drivingSchoolS.ui.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.mobo.mobolibrary.ui.base.ZBaseToolBarFragment;
import com.mobo.mobolibrary.util.Util;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.api.ApiHttpClient;
import com.zjc.drivingSchoolS.api.ResultResponseHandler;
import com.zjc.drivingSchoolS.db.SharePreferences.SharePreferencesUtil;


/**
 * @author Z
 * @Filename SettingModifyFragment.java
 * @Date 2015-10-24
 * @description 修改密码界面
 */
public class SettingModifyFragment extends ZBaseToolBarFragment implements View.OnClickListener {
    private TextView tvCommit;
    private EditText edtOld;
    private EditText edtNew;
    private EditText edtConfirm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 得到布局id
     *
     * @return
     */
    @Override
    protected int inflateContentView() {
        return R.layout.setting_modify_frg;
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initView();
    }


    private void initView() {
        tvCommit = (TextView) rootView.findViewById(R.id.setting_modify_frg_commit);
        edtOld = (EditText) rootView.findViewById(R.id.setting_modify_frg_old);
        edtNew = (EditText) rootView.findViewById(R.id.setting_modify_frg_new);
        edtConfirm = (EditText) rootView.findViewById(R.id.setting_modify_frg_confirm);

        tvCommit.setOnClickListener(this);
        edtNew.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO) {
                    toModify();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.setting_modify_frg_commit) {
            toModify();
        }
    }

    /**
     * 请求网络验证登录
     */
    public void toModify() {
        String old = edtOld.getEditableText().toString().trim();
        String newPawd = edtNew.getEditableText().toString().trim();
        String confirm = edtConfirm.getEditableText().toString().trim();
        if (TextUtils.isEmpty(old) || TextUtils.isEmpty(newPawd) || TextUtils.isEmpty(confirm)) {
            Util.showCustomMsg("请输入完整信息");
            return;
        }
        if (!old.equals(SharePreferencesUtil.getInstance().readPwd())) {
            Util.showCustomMsg("旧密码输入错误");
            return;
        }
        if (!newPawd.equals(confirm)) {
            Util.showCustomMsg("两次新密码输入不一致，请重新输入");
            return;
        }
        ApiHttpClient.getInstance().updatePwd(SharePreferencesUtil.getInstance().readUser().getUid(), confirm, new ResultResponseHandler(getActivity(), "正在修改密码") {

            @Override
            public void onResultSuccess(String result) {
                SharePreferencesUtil.getInstance().savePwd(edtNew.getEditableText().toString());
                getFragmentManager().popBackStack();
            }
        });
    }

    @Override
    protected void setTitle() {
        setTitle(mToolbar, "修改密码");
    }
}
