package com.zjc.drivingSchoolS.widget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.mobo.mobolibrary.ui.base.ZBaseFragment;
import com.zjc.drivingSchoolS.R;


/**
 * Created by Administrator on 2015/7/22.
 */
public abstract class ZBaseSelectFragment extends ZBaseFragment implements View.OnClickListener {
    protected Toolbar mToolbar;
    protected ExpandableRelativeLayout expandableRelativeLayout;
    protected  ToggleButton textView;

    @Nullable
    @Override
    public View onCreateChildView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootChildView = (ViewGroup) inflater.inflate(R.layout.comm_select_fragment, container, false);
        LinearLayout containerChild = (LinearLayout) rootChildView.findViewById(R.id.base_container);
        expandableRelativeLayout = (ExpandableRelativeLayout) rootChildView.findViewById(R.id.expandableLayout);
        expandableRelativeLayout.setOnClickListener(this);
        View child = inflater.inflate(inflateContentView(), containerChild, false);
        containerChild.addView(child);

        mToolbar = (Toolbar) rootChildView.findViewById(R.id.toolbar);
        return rootChildView;
    }

    @Override
    protected void initToolBar() {
        mToolbar.setNavigationIcon(R.drawable.comm_back);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        OnBackPressClick onBackPressClick = new OnBackPressClick();
        mToolbar.setNavigationOnClickListener(onBackPressClick);
        setSelectTitle();
    }

    protected abstract void setSelectTitle();

    protected void setToggle(Toolbar toolbar, String name, CompoundButton.OnCheckedChangeListener onClickListener) {
        textView = (ToggleButton) toolbar.findViewById(R.id.toolbar_title);
        textView.setText(name);
        textView.setOnCheckedChangeListener(onClickListener);
        //设置toolbar默认标题为空
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onClick(View view) {
        textView.setChecked(false);
    }
}
