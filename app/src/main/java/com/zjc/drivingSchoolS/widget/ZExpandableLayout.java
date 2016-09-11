package com.zjc.drivingSchoolS.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andexert.expandablelayout.library.ExpandableLayout;
import com.google.gson.JsonArray;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.api.ApiHttpClient;
import com.zjc.drivingSchoolS.api.ResultResponseHandler;
import com.zjc.drivingSchoolS.db.SharePreferences.SharePreferencesUtil;
import com.zjc.drivingSchoolS.db.model.MessageItem;
import com.zjc.drivingSchoolS.db.models.MessageDetail;
import com.zjc.drivingSchoolS.db.parsers.MessageDetailParser;
import com.zjc.drivingSchoolS.ui.notification.adapter.NotificationsItemAdapter;

/**
 * Created by asus1 on 2016/9/1.
 */
public class ZExpandableLayout extends ExpandableLayout implements View.OnClickListener, View.OnLongClickListener {
    private NotificationsItemAdapter mAdapter;
    private int position;

    public ZExpandableLayout(Context context) {
        super(context);
    }

    public ZExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZExpandableLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(NotificationsItemAdapter notificationsItemAdapter, int adapterPosition) {
        this.position = adapterPosition;
        this.mAdapter = notificationsItemAdapter;
        getHeaderRelativeLayout().setOnClickListener(this);
        getHeaderRelativeLayout().setOnLongClickListener(this);

        MessageItem item = (MessageItem) mAdapter.getItem(position);
        if (item.isOpen()) {
            getContentRelativeLayout().setVisibility(VISIBLE);
            setContentLayout(item);
        } else {
            getContentRelativeLayout().setVisibility(GONE);
        }
    }

    @Override
    public void onClick(View view) {
        MessageItem item = (MessageItem) mAdapter.getItem(position);
        if (item.isOpen()) {
            hide();
            item.setOpen(false);
        } else {
            item.setOpen(true);
            show();
            setContentLayout(item);
        }
    }

    public void setContentLayout(MessageItem item) {
        if (item.getMessageDetail() == null) {
            getNoticeDetail((MessageItem) mAdapter.getItem(position));
        } else {
            setDetail(item.getMessageDetail());
        }
    }

    public void getNoticeDetail(MessageItem messageItem) {
        ApiHttpClient.getInstance().getNoticeDetail(SharePreferencesUtil.getInstance().readUser().getUid(), messageItem.getMid(), new ResultResponseHandler(getContext()) {
            @Override
            public void onResultSuccess(String result) {
                MessageDetail messageDetail = new MessageDetailParser().parseResultMessage(result);
                ((MessageItem) mAdapter.getItem(position)).setMessageDetail(messageDetail);
                setDetail(messageDetail);
                setIsRead();
            }
        });
    }

    public void setDetail(MessageDetail messageDetail) {
        TextView tvContent = (TextView) findViewById(R.id.notification_content_item_tv);
        tvContent.setText(messageDetail.getContext());
    }


    public void setIsRead() {
        ImageView imgIsRead = (ImageView) findViewById(R.id.notification_type_item_icon);
        TextView tvTitle = (TextView) findViewById(R.id.notification_type_item_tv_title);
        TextView tvStatus = (TextView) findViewById(R.id.notification_type_item_tv_status);
        imgIsRead.setSelected(false);
        tvTitle.setSelected(true);
        tvStatus.setSelected(true);
        tvStatus.setText("已读");
    }

    @Override
    public boolean onLongClick(View view) {
        showDeleteDialog();
        return true;
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("温馨提示：");
        builder.setMessage("确认删除该条通知消息？");
        builder.setNegativeButton("确认",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteMessages((MessageItem) mAdapter.getItem(position));
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


    private void deleteMessages(MessageItem item) {
        JsonArray array = new JsonArray();
        array.add(item.getMid());

        ApiHttpClient.getInstance().deleteNotice(SharePreferencesUtil.getInstance().readUser().getUid() + "", array, new ResultResponseHandler(getContext(), "请稍等") {

            @Override
            public void onResultSuccess(String result) {
                //更新接口数据
                mAdapter.remove(mAdapter.getItem(position));
                mAdapter.notifyItemRemoved(position);
            }
        });
    }
}
