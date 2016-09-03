package com.zjc.drivingSchoolS.ui.collect.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.mobo.mobolibrary.ui.base.adapter.ZBaseRecyclerViewAdapter;
import com.mobo.mobolibrary.util.image.ImageLoader;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.db.models.TeacherItem;
import com.zjc.drivingSchoolS.utils.Constants;

/**
 * Created by Administrator on 2016/8/18.
 */
public class TeacherItemAdapter extends ZBaseRecyclerViewAdapter {

    public TeacherItemAdapter(Context context) {
        super(context);
    }


    @Override
    protected BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new CollectManagerFrgViewHolder(parent);
    }

    class CollectManagerFrgViewHolder extends BaseViewHolder<TeacherItem> {
        private TextView tvName;
        private TextView tvContent;
        private TextView tvDistribution;
        private RatingBar rbScore;
        private SimpleDraweeView mImage;

        public CollectManagerFrgViewHolder(ViewGroup parent) {
            super(parent, R.layout.teacher_item);
            rbScore = $(R.id.assess_detail_list_rb_score);
            mImage = $(R.id.personal_center_img_person);
            tvName = $(R.id.collect_name);
            tvContent = $(R.id.collect_content);
            tvDistribution = $(R.id.receive_item_tv_distribution);
        }

        @Override
        public void setData(TeacherItem service) {
            ImageLoader.getInstance().displayImage(mImage, Constants.BASE_IP + service.getPhoto());
            //设置评分条是否只能看，不能改
            rbScore.setIsIndicator(true);
            rbScore.setRating(Float.parseFloat(String.valueOf(service.getStars())));
            tvName.setText(service.getTeachername());
            if (service.isOrderswitch()) {
                tvContent.setText("接单中");
            } else {
                tvContent.setText("休息中");
            }
        }
    }
}
