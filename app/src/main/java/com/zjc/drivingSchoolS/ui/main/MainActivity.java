package com.zjc.drivingSchoolS.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mobo.mobolibrary.util.image.ImageLoader;
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
import com.zjc.drivingSchoolS.ui.order.OrderManagerActivity;
import com.zjc.drivingSchoolS.ui.personal.PersonalActivity;
import com.zjc.drivingSchoolS.utils.Constants;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ImageView imgOne;
    private ImageView imgTwo;
    private ImageView imgThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);

        initToolBar();
        initView();
        initOrder();
        initPerson();
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //设置标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initView() {
        imgOne = (ImageView) findViewById(R.id.main_one);
        imgTwo = (ImageView) findViewById(R.id.main_two);
        imgThree = (ImageView) findViewById(R.id.main_three);

        imgOne.setOnClickListener(this);
        imgTwo.setOnClickListener(this);
        imgThree.setOnClickListener(this);
    }


    private void initOrder() {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        StudyReceiveFragment fragment = new StudyReceiveFragment();
        trans.add(R.id.main_map, fragment, "StudyReceiveFragment").show(fragment).commit();
    }

    /**
     * 初始化个人中心
     */
    private void initPerson() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        initHeaderView(headerView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        getNoticeNumber();
        toolbar.setNavigationIcon(R.drawable.icon_usercenter);
    }


    /**
     * 初始化头部控件
     *
     * @param headerView
     */
    private void initHeaderView(View headerView) {
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
            super.onBackPressed();
        }
    }

    public void OnHeaderItemClick(View view) {
        int id = view.getId();

        if (id == R.id.main_action_apply_history) {
            Intent intent = new Intent(MainActivity.this, ApplyActivity.class);
            startActivity(intent);
        } else if (id == R.id.main_action_history) {
            Intent intent = new Intent(MainActivity.this, OrderManagerActivity.class);
            startActivity(intent);
        } else if (id == R.id.main_action_teacher) {
            Intent intent = new Intent(MainActivity.this, TeacherManagerActivity.class);
            startActivity(intent);
        } else if (id == R.id.main_action_notice) {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        } else if (id == R.id.main_action_modify_password) {

        } else if (id == R.id.main_action_logout) {
            logout();
        }
    }

    public void HeaderViewOnClick(View view) {
        Intent intent = new Intent(MainActivity.this, PersonalActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.main_one) {

        }
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

    private void logout() {
        JPushUtil.setAliasAndTags();
        SharePreferencesUtil.getInstance().setLogin(false);
        SharePreferencesUtil.getInstance().removePwd();
        //跳转到登录
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    /**
     * 登出
     *
     * @param event
     */
    public void onEventMainThread(MainLogoutEvent event) {
        logout();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
