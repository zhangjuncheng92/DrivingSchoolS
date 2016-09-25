package com.zjc.drivingSchoolS.ui.main;


import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.ui.order.ApplyListFragment;

public enum MainTab {

    HOME(0, R.string.title_study, R.drawable.tab_icon_main, StudyMainFragment.class),

    INFO(1, R.string.title_apply_history, R.drawable.tab_icon_notice, ApplyListFragment.class);

    private int idx;
    private int resName;
    private int resIcon;
    private Class<?> clz;

    private MainTab(int idx, int resName, int resIcon, Class<?> clz) {
        this.idx = idx;
        this.resName = resName;
        this.resIcon = resIcon;
        this.clz = clz;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getResName() {
        return resName;
    }

    public void setResName(int resName) {
        this.resName = resName;
    }

    public int getResIcon() {
        return resIcon;
    }

    public void setResIcon(int resIcon) {
        this.resIcon = resIcon;
    }

    public Class<?> getClz() {
        return clz;
    }

    public void setClz(Class<?> clz) {
        this.clz = clz;
    }
    }
