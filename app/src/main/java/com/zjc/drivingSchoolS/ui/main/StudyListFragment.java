package com.zjc.drivingSchoolS.ui.main;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.mobo.mobolibrary.ui.base.ZBaseFragment;
import com.mobo.mobolibrary.ui.base.adapter.ZBaseRecyclerViewAdapter;
import com.mobo.mobolibrary.ui.divideritem.HorizontalDividerItemDecoration;
import com.mobo.mobolibrary.ui.widget.empty.EmptyLayout;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.api.ApiHttpClient;
import com.zjc.drivingSchoolS.api.ResultResponseHandler;
import com.zjc.drivingSchoolS.db.SharePreferences.SharePreferencesUtil;
import com.zjc.drivingSchoolS.db.model.OrderItem;
import com.zjc.drivingSchoolS.db.parser.OrderListResponseParser;
import com.zjc.drivingSchoolS.db.response.OrderListResponse;
import com.zjc.drivingSchoolS.eventbus.StudyDistributionEvent;
import com.zjc.drivingSchoolS.ui.main.adapter.StudyOrderAdapter;
import com.zjc.drivingSchoolS.utils.Constants;
import com.zjc.drivingSchoolS.utils.ConstantsParams;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/8/17.
 */
public class StudyListFragment extends ZBaseFragment implements SwipeRefreshLayout.OnRefreshListener, ZBaseRecyclerViewAdapter.OnLoadMoreListener, ZBaseRecyclerViewAdapter.OnItemClickListener {
    private String orderStatus;
    private EasyRecyclerView mRecyclerView;
    private StudyOrderAdapter mAdapter;

    /**
     * 传入需要的参数，设置给arguments
     */
    public static StudyListFragment newInstance(String bean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.ARGUMENT, bean);
        StudyListFragment fragment = new StudyListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            orderStatus = (String) bundle.getSerializable(Constants.ARGUMENT);
        }
    }

    @Override
    protected int inflateContentView() {
        return R.layout.order_manager_frg;
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initEmptyLayout(rootView);
        initView();
        initAdapter();
        findOrders();
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (EasyRecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .colorResId(R.color.comm_divider)
                .sizeResId(R.dimen.comm_divider_one)
                .build());
        mRecyclerView.setRefreshListener(this);
    }

    private void initAdapter() {
        mAdapter = new StudyOrderAdapter(getActivity());
        mAdapter.setOnItemClickLitener(this);
        mAdapter.setMore(R.layout.view_more, this);
        mAdapter.setNoMore(R.layout.view_nomore);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        //跳转到预约详情界面
        OrderItem orderItem = (OrderItem) mAdapter.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.ARGUMENT, orderItem.getOrid());
//        startActivity(StudyOrderActivity.class, bundle);
    }

    @Override
    public void onRefresh() {
        ApiHttpClient.getInstance().findOrders(SharePreferencesUtil.getInstance().readUser().getUid(), ConstantsParams.PAGE_START, new ResultResponseHandler(getActivity(), getEmptyLayout()) {

            @Override
            public void onResultSuccess(String result) {
                OrderListResponse orderListResponse = new OrderListResponseParser().parseResultMessage(result);
                mAdapter.clear();
                mAdapter.addAll(orderListResponse.getOrderitems());
                isLoadFinish(orderListResponse.getOrderitems().size());
            }
        });
    }

    private void findOrders() {
        ApiHttpClient.getInstance().findOrders(SharePreferencesUtil.getInstance().readUser().getUid(), ConstantsParams.PAGE_START, new ResultResponseHandler(getActivity(), getEmptyLayout()) {

            @Override
            public void onResultSuccess(String result) {
                OrderListResponse orderListResponse = new OrderListResponseParser().parseResultMessage(result);
                mAdapter.addAll(orderListResponse.getOrderitems());
                isLoadFinish(orderListResponse.getOrderitems().size());
            }
        });
    }

    @Override
    public void onLoadMore() {
        int start = mAdapter.getCount();
        ApiHttpClient.getInstance().findOrders(SharePreferencesUtil.getInstance().readUser().getUid(), start, new ResultResponseHandler(getActivity()) {

            @Override
            public void onResultSuccess(String result) {
                OrderListResponse orderListResponse = new OrderListResponseParser().parseResultMessage(result);
                mAdapter.addAll(orderListResponse.getOrderitems());
                isLoadFinish(orderListResponse.getOrderitems().size());
            }
        });
    }

    /**
     * 加载完成
     */
    public boolean isLoadFinish(int size) {
        if (size == 0) {
            getEmptyLayout().setErrorType(EmptyLayout.NODATA_ENABLE_CLICK);
            return true;
        }

        if (size < ConstantsParams.PAGE_SIZE) {
            mAdapter.stopMore();
            mAdapter.setNoMore(R.layout.view_nomore);
            return true;
        }
        return false;
    }


    /**
     * 分配预约单成功，删除订单
     *
     * @param event
     */
    public void onEventMainThread(StudyDistributionEvent event) {
        deleteStudyOrder(event.getOrderItem());
    }

    private void deleteStudyOrder(OrderItem item) {
        //更新接口数据
        for (int i = 0; i < mAdapter.getCount(); i++) {
            OrderItem orderItem = (OrderItem) mAdapter.getItem(i);
            if (orderItem.getOrid().equals(item.getOrid())) {
                mAdapter.remove(mAdapter.getItem(i));
                mAdapter.notifyItemRemoved(i);
                break;
            }
        }
    }

    @Override
    public void sendRequestData() {
        findOrders();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
