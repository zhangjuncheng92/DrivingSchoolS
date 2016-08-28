package com.zjc.drivingSchoolS.db.models;

import java.io.Serializable;


public class UserInfo implements Serializable {

    /**
     * code : 200
     * latitude : 30.503949
     * longitude : 114.439444
     * message : 登陆成功
     * synopsis :
     */

    private String uid;// 用户ID

    private String loginname;// 用户名

    private String schoolname;// 驾校名称

    private String logo;// 驾校LOGO

    private String address;// 驾校地址

    private String website;// 驾校主页

    private String contacts;// 驾校联系人

    private String contactsphone;// 联系人电话

    private double latitude;
    private double longitude;
    private String synopsis;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getSchoolname() {
        return schoolname;
    }

    public void setSchoolname(String schoolname) {
        this.schoolname = schoolname;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getContactsphone() {
        return contactsphone;
    }

    public void setContactsphone(String contactsphone) {
        this.contactsphone = contactsphone;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
}
