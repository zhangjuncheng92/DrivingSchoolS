package com.zjc.drivingSchoolS.ui.order;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.mobo.mobolibrary.ui.base.adapter.ZBaseRecyclerViewAdapter;
import com.mobo.mobolibrary.ui.divideritem.HorizontalDividerItemDecoration;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.db.models.CommStatus;
import com.zjc.drivingSchoolS.ui.order.adapter.OrderMainAdapter;
import com.zjc.drivingSchoolS.utils.ConstantsParams;
import com.zjc.drivingSchoolS.widget.CommStatusAdapter;
import com.zjc.drivingSchoolS.widget.ZBaseSelectFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Z
 * @Filename ConsultListActivity.java
 * @Date 2016.09.14
 * @description 我的会诊控制器
 */
public class OrderMainFragment extends ZBaseSelectFragment {
    private EasyRecyclerView statusRecyclerView;
    private CommStatusAdapter statusAdapter;

    public ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private StudyListFragment startFragment;
    private ApplyListFragment receiveFragment;

    private ViewPager mPager;// 页卡内容
    private TabLayout mTabLayout;

    @Override
    protected int inflateContentView() {
        return R.layout.study_main_pager_act;
    }

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
        statuses.add(new CommStatus(ConstantsParams.STUDY_ORDER_ALL_TEXT, ConstantsParams.NOTICE_ALL));
        statuses.add(new CommStatus(ConstantsParams.STUDY_ORDER_ONE_TXT, ConstantsParams.NOTICE_UNREAD));
        statuses.add(new CommStatus(ConstantsParams.STUDY_ORDER_EIGHT_TXT, ConstantsParams.NOTICE_READ));
        statusAdapter.addAll(statuses);
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

            //调用子界面刷新
            startFragment.refreshByTitle(status.getStatus());
            receiveFragment.refreshByTitle(status.getStatus());
        }
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InitViewPager();
        initTab();
    }

    private void InitViewPager() {
        mPager = (ViewPager) rootView.findViewById(R.id.myconferenceVPager);
        //会诊列表界面
        startFragment = new StudyListFragment();
        receiveFragment = new ApplyListFragment();
        mFragmentList.add(startFragment);
        mFragmentList.add(receiveFragment);
        mPager.setAdapter(new OrderMainAdapter(getChildFragmentManager(), mFragmentList));
    }

    private void initTab() {
        mTabLayout = (TabLayout) rootView.findViewById(R.id.personal_act_tab);
        mTabLayout.setupWithViewPager(mPager);
    }
}
