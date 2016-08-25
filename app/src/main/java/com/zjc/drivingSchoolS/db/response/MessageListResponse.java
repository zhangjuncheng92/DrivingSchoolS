package com.zjc.drivingSchoolS.db.response;

import com.zjc.drivingSchoolS.db.model.AppResponse;
import com.zjc.drivingSchoolS.db.model.MessageItem;

import java.util.List;

/**
 * 消息列表响应
 *
 * @author LJ
 * @date 2016年7月21日
 */
public class MessageListResponse extends AppResponse {
    private List<MessageItem> msgitems;// 消息列表对象

    public List<MessageItem> getMsgitems() {
        return msgitems;
    }

    public void setMsgitems(List<MessageItem> msgitems) {
        this.msgitems = msgitems;
    }

}
