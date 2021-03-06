package com.zjc.drivingSchoolS.ui.notification;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.alertdialogpro.AlertDialogPro;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.mobo.mobolibrary.ui.base.adapter.ZBaseRecyclerViewAdapter;
import com.mobo.mobolibrary.ui.divideritem.HorizontalDividerItemDecoration;
import com.mobo.mobolibrary.ui.widget.empty.EmptyLayout;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.api.ApiHttpClient;
import com.zjc.drivingSchoolS.api.ResultResponseHandler;
import com.zjc.drivingSchoolS.db.SharePreferences.SharePreferencesUtil;
import com.zjc.drivingSchoolS.db.model.JPushNotification;
import com.zjc.drivingSchoolS.db.model.MessageItem;
import com.zjc.drivingSchoolS.db.models.CommStatus;
import com.zjc.drivingSchoolS.db.parser.MessageListResponseParser;
import com.zjc.drivingSchoolS.db.response.MessageListResponse;
import com.zjc.drivingSchoolS.eventbus.JPushNotificationStateEvent;
import com.zjc.drivingSchoolS.eventbus.JpushNotificationEvent;
import com.zjc.drivingSchoolS.ui.login.LoginActivity;
import com.zjc.drivingSchoolS.ui.notification.adapter.NotificationsItemAdapter;
import com.zjc.drivingSchoolS.utils.ConstantsParams;
import com.zjc.drivingSchoolS.widget.CommStatusAdapter;
import com.zjc.drivingSchoolS.widget.ZBaseSelectFragment;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * @author Z
 * @Filename NotificationFragment.java
 * @Date 2016.06.13
 * @description 转诊单通知
 */
public class NotificationFragment extends ZBaseSelectFragment implements ZBaseRecyclerViewAdapter.OnItemClickListener, ZBaseRecyclerViewAdapter.OnItemLongClickListener, ZBaseRecyclerViewAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    private String noticeStatus = ConstantsParams.NOTICE_ALL;
    private NotificationsItemAdapter mAdapter;
    private EasyRecyclerView mRecyclerView;
    private MessageItem messageItem;

    private EasyRecyclerView statusRecyclerView;
    private CommStatusAdapter statusAdapter;

    @Override
    protected void setSelectTitle() {
        setToggle(mToolbar, ConstantsParams.NOTICE_ALL_TEXT, new OnTitleClick());

        statusRecyclerView = (EasyRecyclerView) expandableRelativeLayout.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        statusRecyclerView.setLayoutManager(layoutManager);
        statusRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .colorResId(R.color.comm_divider).sizeResId(R.dimen.comm_divider_line).build());
        statusAdapter = new CommStatusAdapter(getActivity());
        statusAdapter.setOnItemClickLitener(new OnTitleItemClick());
        statusRecyclerView.setAdapter(statusAdapter);

        List<CommStatus> statuses = new ArrayList<>();
        statuses.add(new CommStatus(ConstantsParams.NOTICE_ALL_TEXT, ConstantsParams.NOTICE_ALL));
        statuses.add(new CommStatus(ConstantsParams.NOTICE_UNREAD_TEXT, ConstantsParams.NOTICE_UNREAD));
        statuses.add(new CommStatus(ConstantsParams.NOTICE_READ_TEXT, ConstantsParams.NOTICE_READ));
        statusAdapter.addAll(statuses);
    }

    /**
     * 标题列表点击事件
     */
    class OnTitleItemClick implements ZBaseRecyclerViewAdapter.OnItemClickListener {
        @Override
        public void onItemClick(View view, int position) {
            CommStatus status = (CommStatus) statusAdapter.getItem(position);

            textView.setChecked(false);
            textView.setText(status.getName());
            statusAdapter.setIndex(position);

            noticeStatus = status.getStatus();
            mRecyclerView.getSwipeToRefresh().setRefreshing(true);
            onRefresh();
        }
    }

    /**
     * 标题点击事件
     */
    class OnTitleClick implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            expandableRelativeLayout.toggle();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
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
        getNotice(getEmptyLayout());
    }

    private void initView() {
        mRecyclerView = (EasyRecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .sizeResId(R.dimen.comm_divider_line)
                .colorResId(R.color.transparent)
                .build());
        mRecyclerView.setRefreshListener(this);
    }

    private void initAdapter() {
        mAdapter = new NotificationsItemAdapter(getActivity());
        mAdapter.setOnItemClickLitener(this);
        mAdapter.setOnItemLongClickListener(this);
        mAdapter.setMore(R.layout.view_more, this);
        mAdapter.setNoMore(R.layout.view_nomore);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {
        ApiHttpClient.getInstance().getMessageByTags(SharePreferencesUtil.getInstance().readUser().getUid(), ConstantsParams.PAGE_START, noticeStatus, new ResultResponseHandler(getActivity(), mRecyclerView) {

            @Override
            public void onResultSuccess(String result) {
                MessageListResponse orderListResponse = new MessageListResponseParser().parseResultMessage(result);
                mAdapter.clear();
                mAdapter.addAll(orderListResponse.getMsgitems());
                isLoadFinish(orderListResponse.getMsgitems().size());
            }
        });
    }

    private void getNotice(EmptyLayout emptyLayout) {
        ApiHttpClient.getInstance().getMessageByTags(SharePreferencesUtil.getInstance().readUser().getUid(), ConstantsParams.PAGE_START, noticeStatus, new ResultResponseHandler(getActivity(), emptyLayout) {
            @Override
            public void onResultSuccess(String result) {
                MessageListResponse orderListResponse = new MessageListResponseParser().parseResultMessage(result);
                mAdapter.addAll(orderListResponse.getMsgitems());
                isLoadFinish(orderListResponse.getMsgitems().size());
            }
        });
    }

    @Override
    public void onLoadMore() {
        int start = mAdapter.getCount();
        ApiHttpClient.getInstance().getMessageByTags(SharePreferencesUtil.getInstance().readUser().getUid(), start, noticeStatus, new ResultResponseHandler(getActivity()) {

            @Override
            public void onResultSuccess(String result) {
                MessageListResponse orderListResponse = new MessageListResponseParser().parseResultMessage(result);
                mAdapter.addAll(orderListResponse.getMsgitems());
                isLoadFinish(orderListResponse.getMsgitems().size());
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
    public void onItemClick(View view, int position) {
        Gson gson = new Gson();
        messageItem = ((MessageItem) mAdapter.getItem(position));


        //如果消息未读，则修改状态
        if (!messageItem.getIsread()) {
            updateMessagesReadState(messageItem);
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        mRecyclerView.setTag(position);
//        showDeleteDialog();
    }

    private void showDeleteDialog() {
        AlertDialogPro.Builder builder = new AlertDialogPro.Builder(getActivity());
        builder.setTitle("温馨提示：");
        builder.setMessage("确认删除该条通知消息？");
        builder.setNegativeButton("确认",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        deleteMessages((JPushNotification) mAdapter.getItem((int) mRecyclerView.getTag()));
                        //  deleteMessages((JPushNotification) mAdapter.getItem(currentIndex));
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


    private void deleteMessages(JPushNotification item) {

//        ApiHttpClient.getInstance().deleteMessages(SharePreferencesUtil.getInstance().readUser().getId() + "", item.getMessageId(), new ResultResponseHandlerOfDialog<JPushNotification>(getActivity(), "请稍等", new JPushNotificationParser()) {
//
//            @Override
//            public void onResultSuccess(List<JPushNotification> result) {
//                Util.showCustomMsg("已经成功删除该条通知信息");
//                //更新接口数据
//                mAdapter.setNotifyOnChange(false);
//                mAdapter.remove(mAdapter.getItem(currentIndex));
//                mAdapter.notifyItemRemoved(currentIndex);
//                mAdapter.setNotifyOnChange(true);
//            }
//        });
    }

    @Override
    public void sendRequestData() {
        if (SharePreferencesUtil.getInstance().isLogin()) {
            getNotice(getEmptyLayout());
        } else {
            startActivity(LoginActivity.class);
        }
    }

    /**
     * 收到消息通知广播
     *
     * @param event
     */
    public void onEvent(JpushNotificationEvent event) {
        if (SharePreferencesUtil.getInstance().isLogin()) {
            if (mAdapter.getCount() > 0) {
                getEmptyLayout().setVisibility(View.GONE);
                onRefresh();
            } else {
                getNotice(getEmptyLayout());
            }
        }
    }

    /**
     * 更新消息状态为已读
     *
     * @param item
     */
    private void updateMessagesReadState(final MessageItem item) {
//        ApiHttpClient.getInstance().updateMessagesReadState(SharePreferencesUtil.getInstance().readUser().getId(), item.getMid(), new ResultResponseHandler(getActivity(), "加载详情") {
//            @Override
//            public void onResultSuccess(String result) {
//
//                EventBus.getDefault().post(new JPushNotificationStateEvent(item));
//            }
//        });
    }

    /**
     * 更新消息状态广播
     *
     * @param event
     */
    public void onEventMainThread(JPushNotificationStateEvent event) {
//        int count = mAdapter.getCount();
//        for (int i = 0; i < count; i++) {
//            JPushNotification jPushNotification = (JPushNotification) mAdapter.getItem(i);
//            if (jPushNotification.getId() == event.getJPushNotification().getId()) {
//                jPushNotification.setState(ConstantsParams.NOTIFICATION_YES);
//                mAdapter.notifyItemChanged(i);
//                break;
//            }
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 获取上转转诊单详情
     *
     * @param id
     */
    private void getReferralUpById(String id) {

//        ApiHttpClient.getInstance().getReferralUpById(id, new ResultResponseHandlerOfDialog<HmsUpReferral>(getActivity(), "请稍等", new HmsUpReferralParser()) {
//
//            @Override
//            public void onResultSuccess(List<HmsUpReferral> result) {
//                //我的上转接诊
//                Bundle bundle = new Bundle();
//                bundle.putSerializable(Constants.ARGUMENT, result.get(0));
//                startActivity(ReceptionDetailActivity.class, bundle);
//            }
//        });
    }
}
