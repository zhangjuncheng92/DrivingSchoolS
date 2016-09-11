package com.zjc.drivingSchoolS.db.parsers;

import com.google.gson.reflect.TypeToken;
import com.mobo.mobolibrary.parser.JsonParser;
import com.zjc.drivingSchoolS.db.models.MessageDetail;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MessageDetailParser extends JsonParser<MessageDetail> {

    @Override
    public Type getResultMessageType() {
        return new TypeToken<MessageDetail>() {
        }.getType();
    }

    @Override
    public Type getArrayTypeToken() {
        return new TypeToken<ArrayList<MessageDetail>>() {
        }.getType();
    }

}
