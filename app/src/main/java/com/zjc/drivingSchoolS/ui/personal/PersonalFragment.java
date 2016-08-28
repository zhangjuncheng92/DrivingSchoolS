package com.zjc.drivingSchoolS.ui.personal;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPopupWindow;
import com.bigkoo.pickerview.TimePopupWindow;
import com.facebook.drawee.view.SimpleDraweeView;
import com.mobo.mobolibrary.ui.base.ZBaseToolBarFragment;
import com.mobo.mobolibrary.util.Util;
import com.mobo.mobolibrary.util.image.ImageLoader;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.api.ApiHttpClient;
import com.zjc.drivingSchoolS.api.ResultResponseHandler;
import com.zjc.drivingSchoolS.db.SharePreferences.SharePreferencesUtil;
import com.zjc.drivingSchoolS.db.models.UserInfo;
import com.zjc.drivingSchoolS.db.parsers.SchoolLogoResponseParser;
import com.zjc.drivingSchoolS.db.parsers.UserInfoParser;
import com.zjc.drivingSchoolS.db.response.SchoolLogoResponse;
import com.zjc.drivingSchoolS.eventbus.ActionCameraEvent;
import com.zjc.drivingSchoolS.eventbus.ActionMultiPhotoEvent;
import com.zjc.drivingSchoolS.utils.Constants;
import com.zjc.drivingSchoolS.utils.ConstantsParams;
import com.zjc.drivingSchoolS.widget.MultiPhotoDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.greenrobot.event.EventBus;

/**
 * @author Z
 * @Filename PersonalFragment.java
 * @Date 2016.06.06
 * @description 个人中心详情界面
 */
public class PersonalFragment extends ZBaseToolBarFragment implements View.OnClickListener {
    private EditText tvName;
    private EditText tvEmail;
    private TextView tvSex;
    private EditText tvAddress;
    private EditText tvPhone;
    private EditText tvIdCard;
    private SimpleDraweeView sdIcon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void setTitle() {
        setTitle(mToolbar, R.string.personal_detail_title);
        mToolbar.setOnMenuItemClickListener(OnMenuItemClick);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_personal_center, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private Toolbar.OnMenuItemClickListener OnMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int i = item.getItemId();
            if (i == R.id.action_explain) {
                String sex = tvSex.getText().toString().trim();
                String name = tvName.getText().toString().trim();
                String email = tvEmail.getText().toString().trim();
                String address = tvAddress.getText().toString().trim();
                String phone = tvPhone.getText().toString().trim();
                String idCard = tvIdCard.getText().toString().trim();

                if (TextUtils.isEmpty(sex) || TextUtils.isEmpty(name) || TextUtils.isEmpty(email)
                        || TextUtils.isEmpty(address) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(idCard)) {
                    Util.showCustomMsg("请输入完整信息");
                    return true;
                }

                final UserInfo userInfo = SharePreferencesUtil.getInstance().readUser();
                userInfo.setUid(SharePreferencesUtil.getInstance().readUser().getUid());
//                userInfo.setNickname(name);
//                userInfo.setGender((Boolean) tvSex.getTag());
//                userInfo.setBirthday(birth);
//                userInfo.setPhone(phone);
//                userInfo.setEmail(email);
//                userInfo.setQq(qq);
//                userInfo.setAddress(address);
//                userInfo.setIdentityno(idCard);

//                ApiHttpClient.getInstance().updateUserBaseInfo(userInfo, new ResultResponseHandler(getActivity(), "正在保存") {
//                    @Override
//                    public void onResultSuccess(String result) {
//                        SharePreferencesUtil.getInstance().saveUser(userInfo);
//                        getFragmentManager().popBackStack();
//                    }
//                });
            }
            return true;
        }
    };

    @Override
    protected int inflateContentView() {
        return R.layout.personal_frg;
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initView();
        setPersonInfo(SharePreferencesUtil.getInstance().readUser());
    }

    private void initView() {
        tvName = (EditText) rootView.findViewById(R.id.personal_frg_tv_name);
        tvEmail = (EditText) rootView.findViewById(R.id.personal_frg_tv_lat);
        tvSex = (TextView) rootView.findViewById(R.id.personal_frg_tv_sex);
        tvAddress = (EditText) rootView.findViewById(R.id.personal_frg_tv_address);
        tvPhone = (EditText) rootView.findViewById(R.id.personal_frg_tv_phone);
        tvIdCard = (EditText) rootView.findViewById(R.id.personal_frg_tv_id_card);
        sdIcon = (SimpleDraweeView) rootView.findViewById(R.id.personal_main_frg_sd_icon);

        tvSex.setOnClickListener(this);
    }

    private void setPersonInfo(UserInfo userInfo) {
        tvName.setText(userInfo.getSchoolname());
        tvAddress.setText(userInfo.getAddress());
        tvEmail.setText(userInfo.getSchoolname());
        tvPhone.setText(userInfo.getSchoolname());
        tvIdCard.setText(userInfo.getSchoolname());
        ImageLoader.getInstance().displayImage(sdIcon, Constants.BASE_IP + userInfo.getLogo());

        sdIcon.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        InputMethodManager inputmanger = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        int i = v.getId();
        if (i == R.id.personal_main_frg_sd_icon) {
            MultiPhotoDialogFragment dialogFragment1 = new MultiPhotoDialogFragment();
            dialogFragment1.setMax(1);
            dialogFragment1.setAction(ConstantsParams.PHOTO_TYPE_USER);
            dialogFragment1.show(getFragmentManager(), null);
        }
    }

    public void onEvent(ActionMultiPhotoEvent event) {
        if (event.getAction() == ConstantsParams.PHOTO_TYPE_USER) {
            sdIcon.setTag(event.getUris().get(0));
            updateUserBaseInfo(event.getUris().get(0));
        }
    }

    public void onEvent(ActionCameraEvent event) {
        if (event.getAction() == ConstantsParams.PHOTO_TYPE_USER) {
            sdIcon.setTag(event.getUri());
            updateUserBaseInfo(event.getUri());
        }
    }

    private void updateUserBaseInfo(String uri) {
        String id = SharePreferencesUtil.getInstance().readUser().getUid();
        ApiHttpClient.getInstance().upLoadUserImg(id, uri, new ResultResponseHandler(getActivity(), "上传中") {
            @Override
            public void onResultSuccess(String result) {
                //更新用户头像
                UserInfo beforeUserInfo = SharePreferencesUtil.getInstance().readUser();
                SchoolLogoResponse userInfo = new SchoolLogoResponseParser().parseResultMessage(result);
                beforeUserInfo.setLogo(userInfo.getLogourl());
                SharePreferencesUtil.getInstance().saveUser(beforeUserInfo);
                ImageLoader.getInstance().displayImage(sdIcon, Constants.BASE_IP + userInfo.getLogourl());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
