package com.zjc.drivingSchoolS.ui.receive.adapter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.mobo.mobolibrary.ui.base.adapter.ZBaseRecyclerViewAdapter;
import com.mobo.mobolibrary.util.image.ImageLoader;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.api.ApiHttpClient;
import com.zjc.drivingSchoolS.api.ResultResponseHandler;
import com.zjc.drivingSchoolS.db.SharePreferences.SharePreferencesUtil;
import com.zjc.drivingSchoolS.db.model.OrderItem;
import com.zjc.drivingSchoolS.db.models.TeacherItem;
import com.zjc.drivingSchoolS.eventbus.StudyDistributionEvent;
import com.zjc.drivingSchoolS.utils.Constants;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/8/18.
 */
public class ReceiveAdapter extends ZBaseRecyclerViewAdapter {
    private OrderItem orderItem;

    public ReceiveAdapter(Context context, OrderItem o) {
        super(context);
        orderItem = o;
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
            super(parent, R.layout.receive_item);
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
            tvContent.setText(service.getPhone());
            tvName.setText(service.getTeachername());

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
            TeacherItem item = (TeacherItem) v.getTag();
            distributionStudyOrder(item);
        }
    }

    private void distributionStudyOrder(final TeacherItem item) {
        ApiHttpClient.getInstance().distributionStudyOrder(SharePreferencesUtil.getInstance().readUser().getUid(), orderItem.getOrid(), item.getTid(), new ResultResponseHandler(getContext(), "正在接单") {

            @Override
            public void onResultSuccess(String result) {
                //方式通知到首页面
                EventBus.getDefault().post(new StudyDistributionEvent(orderItem));
                ((AppCompatActivity) getContext()).finish();
            }
        });
    }
}
