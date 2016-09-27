package com.zjc.drivingSchoolS.ui.main.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.mobo.mobolibrary.ui.base.ZBaseFragment;
import com.mobo.mobolibrary.ui.base.adapter.ZBaseRecyclerViewAdapter;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.api.ApiHttpClient;
import com.zjc.drivingSchoolS.api.ResultResponseHandler;
import com.zjc.drivingSchoolS.db.SharePreferences.SharePreferencesUtil;
import com.zjc.drivingSchoolS.db.model.OrderItem;
import com.zjc.drivingSchoolS.ui.receive.ReceiveActivity;
import com.zjc.drivingSchoolS.utils.Constants;
import com.zjc.drivingSchoolS.utils.ConstantsParams;

/**
 * Created by Administrator on 2016/8/17.
 */
public class StudyReceiveAdapter extends ZBaseRecyclerViewAdapter {
    public StudyReceiveAdapter(Context context) {
        super(context);
    }


    @Override
    protected BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new OrderManagerFrgViewHolder(parent);
    }

    class OrderManagerFrgViewHolder extends BaseViewHolder<OrderItem> {
        private TextView tvName;
        private TextView tvStatus;
        private TextView tvPhone;
        private TextView tvTime;
        private TextView tvReceive;
        private TextView tvDistribution;
        private TextView tvMoney;
        private TextView tvTitle;

        public OrderManagerFrgViewHolder(ViewGroup parent) {
            super(parent, R.layout.study_receive_item);
            tvName = $(R.id.order_name);
            tvStatus = $(R.id.order_status);
            tvPhone = $(R.id.order_number);
            tvTime = $(R.id.order_time);
            tvReceive = $(R.id.order_receive);
            tvDistribution = $(R.id.order_distribution);
            tvMoney = $(R.id.order_money);
            tvTitle = $(R.id.order_title);
        }

        @Override
        public void setData(OrderItem service) {
            if (service == null) {
                return;
            }
            tvName.setText(service.getContactsname());
            tvPhone.setText(service.getContactsphone());
            tvTime.setText(service.getCreatetime());
            tvMoney.setText(service.getTotal() + "元");
            tvStatus.setText(ConstantsParams.getStatus(service.getState()));
            tvTitle.setText(service.getTitle());
            //	1.预订成功 2.已支付 3.申请退订 4.已退订 5.消费中 6.已消费 7.待评价 8.已完成 9.已取消
            if (service.getState().equals(ConstantsParams.STUDY_ORDER_ONE)) {
                tvReceive.setVisibility(View.VISIBLE);
                tvDistribution.setVisibility(View.GONE);
            } else if (service.getState().equals(ConstantsParams.STUDY_ORDER_TWO)) {
                tvReceive.setVisibility(View.GONE);
                tvDistribution.setVisibility(View.VISIBLE);
            } else if (service.getState().equals(ConstantsParams.STUDY_ORDER_SEVEN)) {
                tvReceive.setVisibility(View.GONE);
                tvDistribution.setVisibility(View.GONE);
            } else {
                tvReceive.setVisibility(View.GONE);
                tvDistribution.setVisibility(View.GONE);
            }

            tvReceive.setTag(service);
            tvReceive.setOnClickListener(new ReceiveOnClickListener());
            tvDistribution.setTag(service);
            tvDistribution.setOnClickListener(new DistributionOnClickListener());
        }
    }

    /**
     * 分配订单
     */
    class DistributionOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            OrderItem item = (OrderItem) v.getTag();
            startDistributionOrder(item);
        }
    }

    /**
     * 接受订单
     */
    class ReceiveOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            OrderItem item = (OrderItem) v.getTag();
            ReceiveStudyOrder(item);
        }
    }

    private void ReceiveStudyOrder(final OrderItem item) {
        ApiHttpClient.getInstance().receiveStudyOrder(SharePreferencesUtil.getInstance().readUser().getUid(), item.getOrid(), new ResultResponseHandler(getContext(), "正在接单") {

            @Override
            public void onResultSuccess(String result) {
                item.setState(ConstantsParams.STUDY_ORDER_TWO);
                StudyReceiveAdapter.this.notifyDataSetChanged();
                //跳转到分配界面
                startDistributionOrder(item);
            }
        });
    }

    private void startDistributionOrder(OrderItem item) {
        ZBaseFragment fragment = (ZBaseFragment) ((AppCompatActivity) getContext()).getSupportFragmentManager().getFragments().get(0);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.ARGUMENT, item);
        fragment.startActivity(ReceiveActivity.class, bundle);
    }
}
