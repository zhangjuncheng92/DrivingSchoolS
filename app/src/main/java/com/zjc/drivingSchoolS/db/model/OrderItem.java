package com.zjc.drivingSchoolS.db.model;

/**
 * 订单列表Item
 *
 * @author LJ
 * @date 2016年7月21日
 */
public class OrderItem extends AppResponse {


    /**
     * contactsname : 张俊陈
     * contactsphone : 13797039695
     * orderid : SJ160827592280000005
     * orid : 678de123f41046028591cb6e36508104
     * starttime : 2016-08-29 20:35:00
     * state : 1
     * title : 科目三培训学车订单
     * total : 200.0
     * uid : 42164b4381024eb1b6d6b9c0836aaa59
     */

    private String contactsname;
    private String contactsphone;
    private String orderid;
    private String orid;
    private String starttime;
    private String state;
    private String title;
    private double total;
    private String uid;
    /**
     * createtime :
     */

    private String createtime;


    public String getContactsname() {
        return contactsname;
    }

    public void setContactsname(String contactsname) {
        this.contactsname = contactsname;
    }

    public String getContactsphone() {
        return contactsphone;
    }

    public void setContactsphone(String contactsphone) {
        this.contactsphone = contactsphone;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getOrid() {
        return orid;
    }

    public void setOrid(String orid) {
        this.orid = orid;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
}
