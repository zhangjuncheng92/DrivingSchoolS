package com.zjc.drivingSchoolS.db.parsers;

import com.google.gson.reflect.TypeToken;
import com.mobo.mobolibrary.parser.JsonParser;
import com.zjc.drivingSchoolS.db.models.MessageNumberOfNotRead;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MessageNumberOfNotReadParser extends JsonParser<MessageNumberOfNotRead> {

    @Override
    public Type getResultMessageType() {
        return new TypeToken<MessageNumberOfNotRead>() {
        }.getType();
    }

    @Override
    public Type getArrayTypeToken() {
        return new TypeToken<ArrayList<MessageNumberOfNotRead>>() {
        }.getType();
    }

}
