package com.zjc.drivingSchoolS.db.parsers;

import com.google.gson.reflect.TypeToken;
import com.mobo.mobolibrary.parser.JsonParser;
import com.zjc.drivingSchoolS.db.response.TeacherListResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TeacherListResponseParser extends JsonParser<TeacherListResponse> {

    @Override
    public Type getResultMessageType() {
        return new TypeToken<TeacherListResponse>() {
        }.getType();
    }

    @Override
    public Type getArrayTypeToken() {
        return new TypeToken<ArrayList<TeacherListResponse>>() {
        }.getType();
    }

}
