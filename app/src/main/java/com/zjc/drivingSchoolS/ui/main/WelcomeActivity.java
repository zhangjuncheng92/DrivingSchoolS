package com.zjc.drivingSchoolS.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import com.zjc.drivingSchoolS.R;
import com.zjc.drivingSchoolS.db.SharePreferences.SharePreferencesUtil;
import com.zjc.drivingSchoolS.ui.login.LoginActivity;


public class WelcomeActivity extends Activity {
    private static final long WAIT_TIME = 2000;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        init();
    }

    private void init() {
        try {
            PackageManager pm = this.getPackageManager();//context为当前Activity上下文
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
            int curVersionCode = pi.versionCode;
            int oldVersionCode = SharePreferencesUtil.getInstance().getVersionCode();
            if (curVersionCode != oldVersionCode) {
                //更新了应用
                SharePreferencesUtil.getInstance().setVersionCode(curVersionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (SharePreferencesUtil.getInstance().isLogin()) {
                toActivity(MainActivity.class);
            } else {
                toActivity(LoginActivity.class);
            }
        }
    }

    private void toActivity(final Class<?> target) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent intent = new Intent(WelcomeActivity.this, target);
                startActivity(intent);
                finish();
            }
        }, WAIT_TIME);
    }
}
