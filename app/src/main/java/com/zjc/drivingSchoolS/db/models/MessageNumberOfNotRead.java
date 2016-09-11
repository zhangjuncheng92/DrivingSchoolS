package com.zjc.drivingSchoolS.db.models;

import com.zjc.drivingSchoolS.db.model.AppResponse;

/**
 * 消息列表响应
 *
 * @author LJ
 * @date 2016年7月21日
 */
public class MessageNumberOfNotRead extends AppResponse {
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
