package com.zjc.drivingSchoolS.db.parsers;

import com.google.gson.reflect.TypeToken;
import com.mobo.mobolibrary.parser.JsonParser;
import com.zjc.drivingSchoolS.db.models.UserInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class UserInfoParser extends JsonParser<UserInfo> {

    @Override
    public Type getResultMessageType() {
        return new TypeToken<UserInfo>() {
        }.getType();
    }

    @Override
    public Type getArrayTypeToken() {
        return new TypeToken<ArrayList<UserInfo>>() {
        }.getType();
    }

}
