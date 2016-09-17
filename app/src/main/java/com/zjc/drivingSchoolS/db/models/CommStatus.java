package com.zjc.drivingSchoolS.db.models;

import java.io.Serializable;

/**
 * Created by asus1 on 2016/8/16.
 */
public class CommStatus implements Serializable {
    private String name;

    private String status;

    private boolean isCheck;

    public CommStatus(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public CommStatus(String name, String status, boolean isCheck) {
        this.name = name;
        this.status = status;
        this.isCheck = isCheck;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
