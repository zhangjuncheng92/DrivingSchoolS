package com.zjc.drivingSchoolS.db.parsers;

import com.google.gson.reflect.TypeToken;
import com.mobo.mobolibrary.parser.JsonParser;
import com.zjc.drivingSchoolS.db.response.TeacherList;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TeacherListResponseParser extends JsonParser<TeacherList> {

    @Override
    public Type getResultMessageType() {
        return new TypeToken<TeacherList>() {
        }.getType();
    }

    @Override
    public Type getArrayTypeToken() {
        return new TypeToken<ArrayList<TeacherList>>() {
        }.getType();
    }

}
