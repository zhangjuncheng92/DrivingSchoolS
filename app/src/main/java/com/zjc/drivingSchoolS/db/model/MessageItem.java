package com.zjc.drivingSchoolS.db.model;


import com.zjc.drivingSchoolS.db.models.MessageDetail;

/**
 * 消息列表Item
 *
 * @author LJ
 * @date 2016年7月21日
 */
public class MessageItem {
    private String mid;// 消息id

    private String title;// 消息标题

    private boolean isread;// 是否已读，1.已读  0.未读

    private String creatdate;// 发送时间

    private boolean isOpen;

    private MessageDetail messageDetail;

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getIsread() {
        return isread;
    }

    public void setIsread(boolean isread) {
        this.isread = isread;
    }

    public String getCreatdate() {
        return creatdate;
    }

    public void setCreatdate(String creatdate) {
        this.creatdate = creatdate;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public MessageDetail getMessageDetail() {
        return messageDetail;
    }

    public void setMessageDetail(MessageDetail messageDetail) {
        this.messageDetail = messageDetail;
    }
}
