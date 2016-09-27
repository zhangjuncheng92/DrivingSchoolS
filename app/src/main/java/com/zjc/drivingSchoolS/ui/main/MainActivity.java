package com.zjc.drivingSchoolS.ui.main;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mobo.mobolibrary.ui.base.ZBaseActivity;
import com.mobo.mobolibrary.util.Util;
import com.mobo.mobolibrary.util.image.ImageLoader;
import com.qihoo.updatesdk.lib.UpdateHelper;
import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.api.ApiHttpClient;
import com.zjc.drivingSchoolS.api.ResultResponseHandler;
import com.zjc.drivingSchoolS.db.SharePreferences.SharePreferencesUtil;
import com.zjc.drivingSchoolS.db.models.MessageNumberOfNotRead;
import com.zjc.drivingSchoolS.db.models.UserInfo;
import com.zjc.drivingSchoolS.db.parsers.MessageNumberOfNotReadParser;
import com.zjc.drivingSchoolS.eventbus.MainLogoutEvent;
import com.zjc.drivingSchoolS.jpush.JPushUtil;
import com.zjc.drivingSchoolS.ui.apply.ApplyActivity;
import com.zjc.drivingSchoolS.ui.collect.TeacherManagerActivity;
import com.zjc.drivingSchoolS.ui.login.LoginActivity;
import com.zjc.drivingSchoolS.ui.notification.NotificationActivity;
import com.zjc.drivingSchoolS.ui.order.OrderMainActivity;
import com.zjc.drivingSchoolS.ui.order.back.OrderManagerActivity;
import com.zjc.drivingSchoolS.ui.personal.PersonalActivity;
import com.zjc.drivingSchoolS.ui.setting.SettingActivity;
import com.zjc.drivingSchoolS.utils.Constants;

import de.greenrobot.event.EventBus;

public class MainActivity extends ZBaseActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);

        initToolBar();
        initView();
        initPerson();
        checkIsUpData();
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //设置标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_active) {
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            ImageView imageView = new ImageView(MainActivity.this);
            imageView.setImageResource(R.drawable.active);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.setContentView(imageView);
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.real_tab_content);

        mTabHost.getTabWidget().setShowDividers(0);
        initTabs();
    }

    private void initTabs() {
        MainTab[] tabs = MainTab.values();
        final int size = tabs.length;
        for (int i = 0; i < size; i++) {
            MainTab mainTab = tabs[i];
            TabHost.TabSpec tab = mTabHost.newTabSpec(getString(mainTab.getResName()));

            View indicator = getLayoutInflater().inflate(R.layout.main_indicator_tab, null);
            ImageView icon = (ImageView) indicator.findViewById(R.id.tab_icon);
            icon.setImageResource(mainTab.getResIcon());
            TextView title = (TextView) indicator.findViewById(R.id.tab_title);
            title.setText(getString(mainTab.getResName()));
            tab.setIndicator(indicator);
            tab.setContent(new TabHost.TabContentFactory() {

                @Override
                public View createTabContent(String tag) {
                    return new View(MainActivity.this);
                }
            });
            mTabHost.addTab(tab, mainTab.getClz(), null);
        }
    }

    /**
     * 初始化个人中心
     */
    private void initPerson() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationIcon(R.drawable.icon_usercenter);
    }


    /**
     * 初始化头部控件
     */
    private void initHeaderView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        //设置头像，名字等
        UserInfo userInfo = SharePreferencesUtil.getInstance().readUser();
        SimpleDraweeView sdIcon = (SimpleDraweeView) headerView.findViewById(R.id.personal_main_frg_sd_icon);
        TextView tvName = (TextView) headerView.findViewById(R.id.personal_main_frg_tv_name);
        TextView tvNo = (TextView) headerView.findViewById(R.id.personal_main_frg_tv_no);
        ImageLoader.getInstance().displayImage(sdIcon, Constants.BASE_IP + userInfo.getLogo());
        tvName.setText(userInfo.getSchoolname() + "");
        tvNo.setText(userInfo.getAddress());
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mTabHost.getCurrentTab() == 0) {
                exit();
            } else {
                mTabHost.setCurrentTab(0);
            }
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initHeaderView();
        getNoticeNumber();
    }

    public void OnHeaderItemClick(View view) {
        int id = view.getId();

        if (id == R.id.main_action_apply_history) {
            Intent intent = new Intent(MainActivity.this, ApplyActivity.class);
            startActivity(intent);
        } else if (id == R.id.main_action_history) {
            Intent intent = new Intent(MainActivity.this, OrderMainActivity.class);
            startActivity(intent);
        } else if (id == R.id.main_action_teacher) {
            Intent intent = new Intent(MainActivity.this, TeacherManagerActivity.class);
            startActivity(intent);
        } else if (id == R.id.main_action_notice) {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        } else if (id == R.id.main_action_more) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        }
    }

    public void HeaderViewOnClick(View view) {
        Intent intent = new Intent(MainActivity.this, PersonalActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
    }

    private void getNoticeNumber() {
        ApiHttpClient.getInstance().getNoReadMessage(SharePreferencesUtil.getInstance().readUser().getUid(), new ResultResponseHandler(this) {

            @Override
            public void onResultSuccess(String result) {
                MessageNumberOfNotRead message = new MessageNumberOfNotReadParser().parseResultMessage(result);
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                View headerView = navigationView.getHeaderView(0);
                TextView tvMessage = (TextView) headerView.findViewById(R.id.main_tv_message);
                if (message.getCount() > 0) {
                    tvMessage.setText(message.getCount() + "");
                    tvMessage.setVisibility(View.VISIBLE);
                } else {
                    tvMessage.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 登出
     *
     * @param event
     */
    public void onEventMainThread(MainLogoutEvent event) {
        logout();
    }

    private void logout() {
        JPushUtil.setAliasAndTags(this);
        SharePreferencesUtil.getInstance().setLogin(false);
        SharePreferencesUtil.getInstance().removePwd();
        //跳转到登录
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void checkIsUpData() {
        UpdateHelper.getInstance().autoUpdate("com.zjc.drivingSchoolS");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    boolean isExit;

    public void exit() {
        if (!isExit) {
            isExit = true;
            Util.showCustomMsg("再按一次退出程序");
            mHandler.sendEmptyMessageDelayed(0, 3000);
        } else {
            finish();
        }
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };
}
