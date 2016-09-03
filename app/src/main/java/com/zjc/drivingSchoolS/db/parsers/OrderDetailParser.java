package com.zjc.drivingSchoolS.db.parsers;

import com.google.gson.reflect.TypeToken;
import com.mobo.mobolibrary.parser.JsonParser;
import com.zjc.drivingSchoolS.db.models.OrderDetail;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class OrderDetailParser extends JsonParser<OrderDetail> {

    @Override
    public Type getResultMessageType() {
        return new TypeToken<OrderDetail>() {
        }.getType();
    }

    @Override
    public Type getArrayTypeToken() {
        return new TypeToken<ArrayList<OrderDetail>>() {
        }.getType();
    }

}
