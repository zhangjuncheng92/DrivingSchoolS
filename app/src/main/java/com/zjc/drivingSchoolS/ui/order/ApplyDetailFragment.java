package com.zjc.drivingSchoolS.ui.order;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mobo.mobolibrary.ui.base.ZBaseToolBarFragment;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.api.ApiHttpClient;
import com.zjc.drivingSchoolS.api.ResultResponseHandler;
import com.zjc.drivingSchoolS.db.SharePreferences.SharePreferencesUtil;
import com.zjc.drivingSchoolS.db.models.ApplyOrderDetail;
import com.zjc.drivingSchoolS.db.parsers.ApplyOrderDetailParser;
import com.zjc.drivingSchoolS.utils.Constants;
import com.zjc.drivingSchoolS.utils.ConstantsParams;


/**
 * @author Z
 * @Filename StudyDetailFragment.java
 * @Date 2015.10.28
 * @description 预约详情界面
 */
public class ApplyDetailFragment extends ZBaseToolBarFragment {
    private String orderid;
    private ApplyOrderDetail orderDetail;
    private Button btnCommit;

    private TextView tvStatus;

    private TextView tvEducation;
    private TextView tvStudent;
    private TextView tvStudentPhone;
    private TextView tvFee;

    private TextView tvReceiveTime;
    private TextView tvSchool;

    private TextView tvOrderNo;
    private TextView tvOrderTime;


    /**
     * 传入需要的参数，设置给arguments
     */
    public static ApplyDetailFragment newInstance(String bean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.ARGUMENT, bean);
        ApplyDetailFragment fragment = new ApplyDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            orderid = (String) bundle.getSerializable(Constants.ARGUMENT);
        }
    }

    @Override
    protected int inflateContentView() {
        return R.layout.apply_detail_frg;
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (orderid != null) {
            initEmptyLayout(rootView);
            initView();
            getDetailById();
        }
    }

    private void initView() {
        tvStudent = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_student);
        tvStudentPhone = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_student_phone);
        tvStatus = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_status);
        tvEducation = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_education);
        tvFee = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_fee);


        tvReceiveTime = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_receive_time);
        tvSchool = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_school);

        tvOrderNo = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_NO);
        tvOrderTime = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_order_time);
    }

    private void getDetailById() {
        ApiHttpClient.getInstance().getApplyDetail(SharePreferencesUtil.getInstance().readUser().getUid(), orderid, new ResultResponseHandler(getActivity(), getEmptyLayout()) {
            @Override
            public void onResultSuccess(String result) {
                orderDetail = new ApplyOrderDetailParser().parseResultMessage(result);
                setInfo();
            }
        });
    }

    @Override
    protected void setTitle() {
        setTitle(mToolbar, "预约详情");
    }

    private void setTextView(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setText("");
        } else {
            textView.setText(text);
        }
    }

    private void setInfo() {
        tvStatus.setText(ConstantsParams.getStatus(orderDetail.getState()));
        setTextView(tvEducation, orderDetail.getEducation());
        tvFee.setText(orderDetail.getTotal() + "");

        setTextView(tvStudent, orderDetail.getContactsname());
        setTextView(tvStudentPhone, orderDetail.getContactsphone());
        setTextView(tvReceiveTime, orderDetail.getTaketime());
        setTextView(tvSchool, orderDetail.getTname());

        setTextView(tvOrderNo, orderDetail.getOrid());
        setTextView(tvOrderTime, orderDetail.getOrdertime());
    }
}
