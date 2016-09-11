package com.zjc.drivingSchoolS.ui.order;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
    private TextView tvCouponFee;
    private TextView tvPayFee;
    private TextView tvStudent;
    private TextView tvLength;
    private TextView tvStudentPhone;
    private TextView tvFee;

    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvSchool;
    private TextView tvTeacher;
    private TextView tvSchoolCollect;
    private TextView tvTeacherCollect;
    private TextView tvAssess;

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
        tvCouponFee = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_couponfee);
        tvPayFee = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_payfee);
        tvLength = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_length);
        tvFee = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_fee);


        tvStudent = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_student);
        tvStudentPhone = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_student_phone);
        tvStartTime = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_start_time);
        tvEndTime = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_end_time);
        tvSchool = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_school);
        tvTeacher = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_teacher);
        tvSchoolCollect = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_school_collect);
        tvTeacherCollect = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_teacher_collect);
        tvAssess = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_assess);

        tvOrderNo = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_NO);
        tvOrderTime = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_order_time);


        tvAssess.setOnClickListener(this);
        tvSchoolCollect.setOnClickListener(this);
        tvTeacherCollect.setOnClickListener(this);
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

        setTextView(tvSubject, orderDetail.getSubjectname());
        setTextView(tvLength, orderDetail.getNumber() + "");
        tvFee.setText(orderDetail.getTotal() + "元");
        tvCouponFee.setText(orderDetail.getDiscount() + "元");
        tvPayFee.setText(orderDetail.getPayment() + "元");

        setTextView(tvStudent, orderDetail.getContactsname());
        setTextView(tvStudentPhone, orderDetail.getContactsphone());

        //	1.预订成功 2.已支付 3.申请退订 4.已退订 5.消费中 6.培训完成 7.待评价 8.已完成 9.已取消
        if (orderDetail.getState().equals(ConstantsParams.STUDY_ORDER_ONE)) {

        } else if (orderDetail.getState().equals(ConstantsParams.STUDY_ORDER_TWO) || orderDetail.getState().equals(ConstantsParams.STUDY_ORDER_THREE) || orderDetail.getState().equals(ConstantsParams.STUDY_ORDER_FOUR)) {
            //已接单
            ((View) tvSchool.getParent().getParent()).setVisibility(View.VISIBLE);
            ((View) tvTeacher.getParent().getParent()).setVisibility(View.VISIBLE);

            //退订状态，显示退订时间
            if (orderDetail.getState().equals(ConstantsParams.STUDY_ORDER_FOUR)) {
                TextView tvCancelTime = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_unsubscribe_time);
                setTextView(tvCancelTime, orderDetail.getRefundtime());
                ((View) tvCancelTime.getParent().getParent()).setVisibility(View.VISIBLE);
            }
        } else if (orderDetail.getState().equals(ConstantsParams.STUDY_ORDER_FIVE) || orderDetail.getState().equals(ConstantsParams.STUDY_ORDER_SIX)
                || orderDetail.getState().equals(ConstantsParams.STUDY_ORDER_SEVEN) || orderDetail.getState().equals(ConstantsParams.STUDY_ORDER_EIGHT)) {
            //已开始练车
            ((View) tvStartTime.getParent().getParent()).setVisibility(View.VISIBLE);
            ((View) tvEndTime.getParent().getParent()).setVisibility(View.VISIBLE);
            ((View) tvSchool.getParent().getParent()).setVisibility(View.VISIBLE);
            ((View) tvTeacher.getParent().getParent()).setVisibility(View.VISIBLE);

            //显示评价
            if (orderDetail.getState().equals(ConstantsParams.STUDY_ORDER_EIGHT)) {
                ((View) tvAssess.getParent().getParent()).setVisibility(View.VISIBLE);
            }
        } else if (orderDetail.getState().equals(ConstantsParams.STUDY_ORDER_NINE)) {
            //已取消
            TextView tvCancelTime = (TextView) rootView.findViewById(R.id.order_detail_frg_tv_cancel_time);
            setTextView(tvCancelTime, orderDetail.getCanceltime());
            ((View) tvCancelTime.getParent().getParent()).setVisibility(View.VISIBLE);
        } else {
        }
        setTextView(tvStartTime, orderDetail.getStarttime());
        setTextView(tvEndTime, orderDetail.getEndtime());
        setTextView(tvSchool, orderDetail.getSname());
        setTextView(tvTeacher, orderDetail.getTname());

        setTextView(tvOrderNo, orderDetail.getOrid());
        setTextView(tvOrderTime, orderDetail.getOrdertime());
    }


    private void showCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("温馨提示");
        builder.setMessage("是否确定取消预约单？");
        builder.setNegativeButton("确认",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
        builder.setPositiveButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.order_detail_frg_tv_assess) {
            //点击已评价
//            StudyAssessResultFrg frg = StudyAssessResultFrg.newInstance(orderDetail);
//            replaceFrg(frg, null);
        }
    }
}
