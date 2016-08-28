package com.zjc.drivingSchoolS.ui.personal;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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
import com.zjc.drivingSchoolS.db.response.SchoolLogoResponse;
import com.zjc.drivingSchoolS.eventbus.ActionCameraEvent;
import com.zjc.drivingSchoolS.eventbus.ActionMultiPhotoEvent;
import com.zjc.drivingSchoolS.utils.Constants;
import com.zjc.drivingSchoolS.utils.ConstantsParams;
import com.zjc.drivingSchoolS.widget.MultiPhotoDialogFragment;

import de.greenrobot.event.EventBus;

/**
 * @author Z
 * @Filename PersonalFragment.java
 * @Date 2016.06.06
 * @description 个人中心详情界面
 */
public class PersonalFragment extends ZBaseToolBarFragment implements View.OnClickListener {
    private EditText tvName;
    private TextView tvLat;
    private TextView tvSex;
    private EditText tvAddress;
    private EditText tvPhone;
    private EditText tvIdCard;
    private SimpleDraweeView sdIcon;

    //定位相关
    LocationClient mLocClient;
    public MyLocationListener myListener = new MyLocationListener();

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
                String email = tvLat.getText().toString().trim();
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
        initLocation();
        setPersonInfo(SharePreferencesUtil.getInstance().readUser());
    }

    private void initView() {
        tvName = (EditText) rootView.findViewById(R.id.personal_frg_tv_name);
        tvLat = (TextView) rootView.findViewById(R.id.personal_frg_tv_lat);
        tvSex = (TextView) rootView.findViewById(R.id.personal_frg_tv_sex);
        tvAddress = (EditText) rootView.findViewById(R.id.personal_frg_tv_address);
        tvPhone = (EditText) rootView.findViewById(R.id.personal_frg_tv_phone);
        tvIdCard = (EditText) rootView.findViewById(R.id.personal_frg_tv_id_card);
        sdIcon = (SimpleDraweeView) rootView.findViewById(R.id.personal_main_frg_sd_icon);

        tvSex.setOnClickListener(this);
        tvLat.setOnClickListener(this);
    }


    /**
     * 初始化定位功能
     */
    private void initLocation() {
        // 定位初始化
        mLocClient = new LocationClient(getActivity());
        mLocClient.registerLocationListener(myListener);

        LocationClientOption option = new LocationClientOption();

        option.setOpenGps(true);// 打开gps

        option.setAddrType("all");
        option.setPriority(LocationClientOption.NetWorkFirst);
        option.setPriority(LocationClientOption.GpsFirst);       //gps
        option.disableCache(true);
        mLocClient.setLocOption(option);
    }

    private void setPersonInfo(UserInfo userInfo) {
        tvName.setText(userInfo.getSchoolname());
        tvAddress.setText(userInfo.getAddress());
        tvPhone.setText(userInfo.getSchoolname());
        tvIdCard.setText(userInfo.getSchoolname());
        ImageLoader.getInstance().displayImage(sdIcon, Constants.BASE_IP + userInfo.getLogo());

        sdIcon.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.personal_main_frg_sd_icon) {
            MultiPhotoDialogFragment dialogFragment1 = new MultiPhotoDialogFragment();
            dialogFragment1.setMax(1);
            dialogFragment1.setAction(ConstantsParams.PHOTO_TYPE_USER);
            dialogFragment1.show(getFragmentManager(), null);
        } else if (i == R.id.personal_frg_tv_lat) {
            mLocClient.start();
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

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null) {
                return;
            }
            mLocClient.stop();
            updateLatLng(location.getLatitude(), location.getLongitude());
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    private void updateLatLng(double lat, double lng) {
        String id = SharePreferencesUtil.getInstance().readUser().getUid();
        ApiHttpClient.getInstance().updateLatLng(id, lat, lng, new ResultResponseHandler(getActivity(), "更新坐标，请稍等") {
            @Override
            public void onResultSuccess(String result) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mLocClient.stop();
    }
}
