package com.zjc.drivingSchoolS.ui.order;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mobo.mobolibrary.ui.base.ZBaseToolBarFragment;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.api.ApiHttpClient;
import com.zjc.drivingSchoolS.api.ResultResponseHandler;
import com.zjc.drivingSchoolS.db.SharePreferences.SharePreferencesUtil;
import com.zjc.drivingSchoolS.db.models.OrderDetail;
import com.zjc.drivingSchoolS.db.parsers.OrderDetailParser;
import com.zjc.drivingSchoolS.utils.Constants;
import com.zjc.drivingSchoolS.utils.ConstantsParams;


/**
 * @author Z
 * @Filename StudyDetailFragment.java
 * @Date 2015.10.28
 * @description 预约详情界面
 */
public class StudyDetailFragment extends ZBaseToolBarFragment implements View.OnClickListener {
    private String orderid;
    private OrderDetail orderDetail;
    private Button btnCommit;

    private TextView tvStatus;

    private TextView tvSubject;
    private TextView tvCart;
    private TextView tvStudent;
    private TextView tvLength;
    private TextView tvStudentPhone;
    private TextView tvFee;

    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvSchool;
    private TextView tvTeacher;

    private TextView tvOrderNo;
    private TextView tvOrderTime;


    /**
     * 传入需要的参数，设置给arguments
     */
    public static StudyDetailFragment newInstance(String bean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.ARGUMENT, bean);
        StudyDetailFragment fragment = new StudyDetailFragment();
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
    protected void setTitle() {
        setTitle(mToolbar, "预约详情");
    }

    @Override
    protected int inflateContentView() {
        return R.layout.study_detail_frg;
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
        tvStatus = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_status);
        tvSubject = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_subject);
        tvCart = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_cart);
        tvLength = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_length);
        tvFee = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_fee);


        tvStudent = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_student);
        tvStudentPhone = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_student_phone);
        tvStartTime = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_start_time);
        tvEndTime = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_end_time);
        tvSchool = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_school);
        tvTeacher = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_teacher);

        tvOrderNo = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_NO);
        tvOrderTime = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_order_time);
    }

    private void getDetailById() {
        ApiHttpClient.getInstance().getOrderDetailById(SharePreferencesUtil.getInstance().readUser().getUid(), orderid, new ResultResponseHandler(getActivity(), getEmptyLayout()) {
            @Override
            public void onResultSuccess(String result) {
                orderDetail = new OrderDetailParser().parseResultMessage(result);
                setInfo();
            }
        });
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

        setTextView(tvCart, orderDetail.getCarsname());
        setTextView(tvSubject, orderDetail.getSubjectname());
        setTextView(tvLength, orderDetail.getNumber() + "");
        tvFee.setText(orderDetail.getTotal() + "");

        setTextView(tvStudent, orderDetail.getContactsname());
        setTextView(tvStudentPhone, orderDetail.getContactsphone());
        setTextView(tvStartTime, orderDetail.getStarttime());
        setTextView(tvEndTime, orderDetail.getEndtime());
        setTextView(tvSchool, orderDetail.getSname());
        setTextView(tvTeacher, orderDetail.getTname());

        setTextView(tvOrderNo, orderDetail.getOrid());
        setTextView(tvOrderTime, orderDetail.getOrdertime());
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
    }
}
