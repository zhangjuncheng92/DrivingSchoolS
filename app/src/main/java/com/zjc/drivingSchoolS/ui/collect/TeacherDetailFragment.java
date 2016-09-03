package com.zjc.drivingSchoolS.ui.collect;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mobo.mobolibrary.ui.base.ZBaseToolBarFragment;
import com.mobo.mobolibrary.util.image.ImageLoader;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.api.ApiHttpClient;
import com.zjc.drivingSchoolS.api.ResultResponseHandler;
import com.zjc.drivingSchoolS.db.SharePreferences.SharePreferencesUtil;
import com.zjc.drivingSchoolS.db.models.TeacherDetail;
import com.zjc.drivingSchoolS.db.models.TeacherItem;
import com.zjc.drivingSchoolS.db.parsers.TeacherDetailParser;
import com.zjc.drivingSchoolS.utils.Constants;

/**
 * Created by Administrator on 2016/8/24.
 */
public class TeacherDetailFragment extends ZBaseToolBarFragment {
    private TeacherItem teacherItem;
    private TeacherDetail teacherCollectDetail;

    private SimpleDraweeView sdIcon;
    private RatingBar rbScore;
    private TextView tvDes;
    private TextView tvTelephone;
    private TextView tvSchoolName;
    private TextView tvName;
    private TextView tvSex;
    private TextView tvAge;

    /**
     * 传入需要的参数，设置给arguments
     */
    public static TeacherDetailFragment newInstance(TeacherItem bean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.ARGUMENT, bean);
        TeacherDetailFragment fragment = new TeacherDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            teacherItem = (TeacherItem) bundle.getSerializable(Constants.ARGUMENT);
        }
    }

    @Override
    protected void setTitle() {
        setTitle(mToolbar, "教练详情");
    }

    @Override
    protected int inflateContentView() {
        return R.layout.teacher_detail_frg;
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initEmptyLayout(rootView);
        initView();
        getDetailById();
    }

    private void initView() {
        sdIcon = (SimpleDraweeView) rootView.findViewById(R.id.personal_main_frg_sd_icon);
        tvAge = (TextView) rootView.findViewById(R.id.collect_detail_age);
        rbScore = (RatingBar) rootView.findViewById(R.id.assess_detail_list_rb_score);
        tvDes = (TextView) rootView.findViewById(R.id.collect_detail_des);
        tvTelephone = (TextView) rootView.findViewById(R.id.collect_detail_telephone);
        tvSchoolName = (TextView) rootView.findViewById(R.id.collect_detail_schoolname);
        tvName = (TextView) rootView.findViewById(R.id.collect_detail_name);
        tvSex = (TextView) rootView.findViewById(R.id.collect_detail_sex);

        ImageLoader.getInstance().displayImage(sdIcon, Constants.BASE_IP + teacherItem.getPhoto());
        rbScore.setIsIndicator(true);
        rbScore.setRating(Float.parseFloat(String.valueOf(teacherItem.getStars())));
        tvTelephone.setText(teacherItem.getPhone());
        tvSchoolName.setText(teacherItem.getTeachername());
        tvName.setText(teacherItem.getTeachername());
        if (teacherItem.getGender()) {
            tvSex.setText(R.string.personal_sex_men);
        } else {
            tvSex.setText(R.string.personal_sex_women);
        }
    }

    private void getDetailById() {
        ApiHttpClient.getInstance().getTeacherDetail(SharePreferencesUtil.getInstance().readUser().getUid(), teacherItem.getTid(), new ResultResponseHandler(getActivity(), getEmptyLayout()) {
            @Override
            public void onResultSuccess(String result) {
                teacherCollectDetail = new TeacherDetailParser().parseResultMessage(result);
                setInfo();
            }
        });
    }

    private void setInfo() {
        if (!TextUtils.isEmpty(teacherCollectDetail.getSynopsis())) {
            tvDes.setText(teacherCollectDetail.getSynopsis());
        }
        tvAge.setText(teacherCollectDetail.getExperience() + "");
    }
}
