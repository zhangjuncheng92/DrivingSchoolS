package com.zjc.drivingSchoolS.jpush;

import android.content.Context;

import com.mobo.mobolibrary.logs.Logs;
import com.zjc.drivingSchoolS.api.ApiHttpClient;
import com.zjc.drivingSchoolS.api.ResultResponseHandler;
import com.zjc.drivingSchoolS.app.MApp;
import com.zjc.drivingSchoolS.db.SharePreferences.SharePreferencesUtil;
import com.zjc.drivingSchoolS.db.models.UserInfo;

import cn.jpush.android.api.JPushInterface;

/**
 * 推送工具类
 */
public class JPushUtil {

    /**
     * 推送标记
     */
    //极光推送，科室标识
    public static final String JP_DEPT_FLAG = "D";
    //极光推送，机构标识
    public static final String JP_ORGANIZATION_FLAG = "O";

    /**
     * 上线版标记
     */
    //极光推送，科室标识
    public static final String APP_DEBUG = "debug";
    //极光推送，机构标识
    public static final String APP_RELEASE = "release";


    public static void initJPush() {
        //推送
        JPushInterface.init(MApp.getInstance().getApplicationContext());
    }

    /**
     * 拼接推送标记
     *
     * @param depart
     * @return
     */
    public static String getJPushTagByDepart(int depart) {
        if (Logs.issIsLogEnabled()) {
            return APP_DEBUG + "_D_" + depart;
        } else {
            return APP_RELEASE + "_D_" + depart;
        }
    }

    /**
     * 设置别名和标记
     */
    public static void setAliasAndTags(Context context) {
        try {
            //调用JPush API设置Alias
            if (SharePreferencesUtil.getInstance().isLogin()) {
                UserInfo userInfo = SharePreferencesUtil.getInstance().readUser();
                ApiHttpClient.getInstance().registerJPush(userInfo.getUid(), JPushInterface.getRegistrationID(context), new ResultResponseHandler(context) {

                    @Override
                    public void onResultSuccess(String result) {
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
