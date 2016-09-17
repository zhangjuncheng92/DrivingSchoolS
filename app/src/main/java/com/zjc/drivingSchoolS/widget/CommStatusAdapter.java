package com.zjc.drivingSchoolS.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.mobo.mobolibrary.ui.base.adapter.ZBaseRecyclerViewAdapter;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.db.models.CommStatus;


/**
 * Created by Administrator on 2015/7/22.
 */
public class CommStatusAdapter extends ZBaseRecyclerViewAdapter {
    private int index;

    public CommStatusAdapter(Context context) {
        super(context);
    }

    @Override
    protected BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new MainServiceViewHolder(parent);
    }

    class MainServiceViewHolder extends BaseViewHolder<CommStatus> {
        private TextView mTv_name;
        private ImageView imgStatus;

        public MainServiceViewHolder(ViewGroup parent) {
            super(parent, R.layout.comm_status_item);
            mTv_name = $(R.id.view_area_list_tv_name);
            imgStatus = $(R.id.status);
        }

        @Override
        public void setData(CommStatus service) {
            mTv_name.setText(service.getName());
            if (this.getAdapterPosition() == getIndex()) {
                imgStatus.setVisibility(View.VISIBLE);
            } else {
                imgStatus.setVisibility(View.GONE);
            }
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
        this.notifyDataSetChanged();
    }
}
