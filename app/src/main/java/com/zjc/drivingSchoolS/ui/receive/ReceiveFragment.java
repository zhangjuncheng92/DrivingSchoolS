package com.zjc.drivingSchoolS.ui.receive;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.mobo.mobolibrary.ui.base.ZBaseToolBarFragment;
import com.mobo.mobolibrary.ui.base.adapter.ZBaseRecyclerViewAdapter;
import com.mobo.mobolibrary.ui.divideritem.HorizontalDividerItemDecoration;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.api.ApiHttpClient;
import com.zjc.drivingSchoolS.api.ResultResponseHandler;
import com.zjc.drivingSchoolS.db.SharePreferences.SharePreferencesUtil;
import com.zjc.drivingSchoolS.db.model.OrderItem;
import com.zjc.drivingSchoolS.db.parsers.TeacherListResponseParser;
import com.zjc.drivingSchoolS.db.response.TeacherList;
import com.zjc.drivingSchoolS.ui.receive.adapter.ReceiveAdapter;
import com.zjc.drivingSchoolS.utils.Constants;
import com.zjc.drivingSchoolS.utils.ConstantsParams;

/**
 * Created by Administrator on 2016/8/18.
 */
public class ReceiveFragment extends ZBaseToolBarFragment implements SwipeRefreshLayout.OnRefreshListener, ZBaseRecyclerViewAdapter.OnItemClickListener, ZBaseRecyclerViewAdapter.OnLoadMoreListener {
    private OrderItem orderItem;
    private EasyRecyclerView mRecyclerView;
    private ReceiveAdapter mAdapter;

    /**
     * 传入需要的参数，设置给arguments
     */
    public static ReceiveFragment newInstance(OrderItem bean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.ARGUMENT, bean);
        ReceiveFragment fragment = new ReceiveFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            orderItem = (OrderItem) bundle.getSerializable(Constants.ARGUMENT);
        }
    }

    @Override
    protected void setTitle() {
        setTitle(mToolbar, R.string.title_distribution);
    }

    @Override
    protected int inflateContentView() {
        return R.layout.comm_recycler_view_frg;
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initEmptyLayout(rootView);
        initView();
        initAdapter();
        findCollectList();
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (EasyRecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .colorResId(R.color.comm_divider)
                .sizeResId(R.dimen.comm_divider_line)
                .build());
        mRecyclerView.setRefreshListener(this);
    }

    private void initAdapter() {
        mAdapter = new ReceiveAdapter(getActivity(), orderItem);
        mAdapter.setOnItemClickLitener(this);
        mAdapter.setMore(R.layout.view_more, this);
        mAdapter.setNoMore(R.layout.view_nomore);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        //跳转到详情
//        TeacherCollectItem teacherCollectItem = (TeacherCollectItem) mAdapter.getItem(position);
//        TeacherDetailFragment collectDetailFragment = TeacherDetailFragment.newInstance(teacherCollectItem);
//        replaceFrg(collectDetailFragment, null);
    }

    @Override
    public void onRefresh() {
        ApiHttpClient.getInstance().getTeacherList(SharePreferencesUtil.getInstance().readUser().getUid(), ConstantsParams.PAGE_START, ConstantsParams.TEACHER_ENABLE, new ResultResponseHandler(getActivity(), mRecyclerView) {

            @Override
            public void onResultSuccess(String result) {
                TeacherList response = new TeacherListResponseParser().parseResultMessage(result);
                mAdapter.clear();
                mAdapter.addAll(response.getTcitems());
                isLoadFinish(response.getTcitems().size());
            }
        });
    }

    private void findCollectList() {
        ApiHttpClient.getInstance().getTeacherList(SharePreferencesUtil.getInstance().readUser().getUid(), ConstantsParams.PAGE_START, ConstantsParams.TEACHER_ENABLE, new ResultResponseHandler(getActivity(), getEmptyLayout()) {

            @Override
            public void onResultSuccess(String result) {
                TeacherList response = new TeacherListResponseParser().parseResultMessage(result);
                mAdapter.addAll(response.getTcitems());
                isLoadFinish(response.getTcitems().size());
            }
        });
    }

    @Override
    public void onLoadMore() {
        int start = mAdapter.getCount();
        ApiHttpClient.getInstance().getTeacherList(SharePreferencesUtil.getInstance().readUser().getUid(), start, ConstantsParams.TEACHER_ENABLE, new ResultResponseHandler(getActivity()) {

            @Override
            public void onResultSuccess(String result) {
                TeacherList response = new TeacherListResponseParser().parseResultMessage(result);
                mAdapter.addAll(response.getTcitems());
                isLoadFinish(response.getTcitems().size());
            }
        });
    }

    /**
     * 加载完成
     */
    public boolean isLoadFinish(int size) {
        if (size < ConstantsParams.PAGE_SIZE) {
            mAdapter.stopMore();
            mAdapter.setNoMore(R.layout.view_nomore);
            return true;
        }
        return false;
    }

    @Override
    public void sendRequestData() {
        findCollectList();
    }
}
