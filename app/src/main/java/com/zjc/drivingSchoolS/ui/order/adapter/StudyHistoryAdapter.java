package com.zjc.drivingSchoolS.ui.order.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.mobo.mobolibrary.ui.base.adapter.ZBaseRecyclerViewAdapter;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.api.ApiHttpClient;
import com.zjc.drivingSchoolS.api.ResultResponseHandler;
import com.zjc.drivingSchoolS.db.SharePreferences.SharePreferencesUtil;
import com.zjc.drivingSchoolS.db.model.OrderItem;
import com.zjc.drivingSchoolS.utils.ConstantsParams;

/**
 * Created by Administrator on 2016/8/17.
 */
public class StudyHistoryAdapter extends ZBaseRecyclerViewAdapter {
    public StudyHistoryAdapter(Context context) {
        super(context);
    }


    @Override
    protected BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new OrderManagerFrgViewHolder(parent);
    }

    class OrderManagerFrgViewHolder extends BaseViewHolder<OrderItem> {
        private OrderItem orderItem;
        private TextView mTvName;
        private TextView mTvStatus;
        private TextView mTvNumber;
        private TextView mTvTime;
        private TextView tvCancel;
        private TextView tvAgree;
        private TextView tvDisagree;
        private TextView mTvMoney;

        public OrderManagerFrgViewHolder(ViewGroup parent) {
            super(parent, R.layout.study_order_item);
            mTvName = $(R.id.order_name);
            mTvStatus = $(R.id.order_status);
            mTvNumber = $(R.id.order_number);
            mTvTime = $(R.id.order_time);
            tvCancel = $(R.id.order_cancel);
            tvAgree = $(R.id.order_agree);
            tvDisagree = $(R.id.order_disagree);
            mTvMoney = $(R.id.order_money);
        }

        @Override
        public void setData(OrderItem service) {
            orderItem = service;
            mTvName.setText(service.getTitle());
            mTvNumber.setText(service.getOrderid());
            mTvTime.setText(service.getStarttime());
            mTvMoney.setText(service.getTotal() + "元");
            mTvStatus.setText(ConstantsParams.getStatus(service.getState()));
            //	1.预订成功 2.已支付 3.申请退订 4.已退订 5.消费中 6.培训完成 7.待评价 8.已完成 9.已取消
            if (service.getState().equals(ConstantsParams.STUDY_ORDER_THREE)) {
                tvCancel.setVisibility(View.GONE);
                tvAgree.setVisibility(View.VISIBLE);
                tvDisagree.setVisibility(View.VISIBLE);
            } else {
                tvCancel.setVisibility(View.GONE);
                tvAgree.setVisibility(View.GONE);
                tvDisagree.setVisibility(View.GONE);
            }
            tvAgree.setOnClickListener(new AgreeOnClickListener());
            tvDisagree.setOnClickListener(new DisAssessOnClickListener());
        }

        /**
         * 同意事件
         */
        class AgreeOnClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                showAgreeDialog();
            }
        }

        private void showAgreeDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setTitle("取消提示");
            builder.setMessage("确认同意退订此订单？");
            builder.setNegativeButton("确认",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            agreeStudyOrder(true);
                        }
                    });
            builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            builder.show();
        }


        /**
         * 不同意事件
         */
        class DisAssessOnClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                showDisagreeDialog();
            }
        }

        private void showDisagreeDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setTitle("取消提示");
            builder.setMessage("确认不同意退订此订单？");
            builder.setNegativeButton("确认",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            agreeStudyOrder(false);
                        }
                    });
            builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            builder.show();
        }

        private void agreeStudyOrder(final boolean isAgree) {
            ApiHttpClient.getInstance().studyOrderIsAgree(SharePreferencesUtil.getInstance().readUser().getUid(), orderItem.getOrid(), isAgree, new ResultResponseHandler(getContext(), "正在处理订单") {

                @Override
                public void onResultSuccess(String result) {
                    if (isAgree) {
                        orderItem.setState(ConstantsParams.STUDY_ORDER_FOUR);
                    } else {
                        orderItem.setState(ConstantsParams.STUDY_ORDER_TWO);
                    }
                    StudyHistoryAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }
}
