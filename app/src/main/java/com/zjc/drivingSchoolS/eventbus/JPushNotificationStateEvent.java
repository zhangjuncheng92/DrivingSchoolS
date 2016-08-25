package com.zjc.drivingSchoolS.eventbus;


import com.zjc.drivingSchoolS.db.model.MessageItem;

/**
 * Created by Administrator on 2015/7/14.
 */
public class JPushNotificationStateEvent {
    MessageItem messageItem;

    public JPushNotificationStateEvent(MessageItem messageItem) {
        this.messageItem = messageItem;
    }

    public MessageItem getMessageItem() {
        return messageItem;
    }
}
