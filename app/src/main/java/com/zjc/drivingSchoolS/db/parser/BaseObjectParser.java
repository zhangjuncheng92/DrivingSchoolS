package com.zjc.drivingSchoolS.db.parser;

import com.google.gson.reflect.TypeToken;
import com.mobo.mobolibrary.parser.JsonParser;
import com.zjc.drivingSchoolS.db.model.AppResponse;

import java.lang.reflect.Type;

public class BaseObjectParser extends JsonParser<AppResponse> {

    @Override
    public Type getResultMessageType() {
        return new TypeToken<AppResponse>() {
        }.getType();
    }

    @Override
    public Type getArrayTypeToken() {
        return new TypeToken<AppResponse>() {
        }.getType();
    }

}
