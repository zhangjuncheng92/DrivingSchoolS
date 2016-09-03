package com.zjc.drivingSchoolS.db.models;

import com.zjc.drivingSchoolS.db.model.AppResponse;

/**
 * 教练Item
 *
 * @author LJ
 * @date 2016年7月21日
 */
public class TeacherItem extends AppResponse {
    /**
     * gender : null
     * orderswitch : true
     * phone : 19874525863
     * photo : /images/nan.jpg
     * stars : null
     * teachername : 大飞哥
     * tid : 010c8c40fea441bba1688b615b29dce2
     */

    private boolean gender;
    private boolean orderswitch;
    private String phone;
    private String photo;
    private float stars;
    private String teachername;
    private String tid;

    public boolean getGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public boolean isOrderswitch() {
        return orderswitch;
    }

    public void setOrderswitch(boolean orderswitch) {
        this.orderswitch = orderswitch;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public float getStars() {
        return stars;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }

    public String getTeachername() {
        return teachername;
    }

    public void setTeachername(String teachername) {
        this.teachername = teachername;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}
