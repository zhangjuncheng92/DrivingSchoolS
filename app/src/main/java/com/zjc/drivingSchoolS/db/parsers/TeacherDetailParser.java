package com.zjc.drivingSchoolS.db.parsers;

import com.google.gson.reflect.TypeToken;
import com.mobo.mobolibrary.parser.JsonParser;
import com.zjc.drivingSchoolS.db.models.TeacherDetail;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TeacherDetailParser extends JsonParser<TeacherDetail> {

    @Override
    public Type getResultMessageType() {
        return new TypeToken<TeacherDetail>() {
        }.getType();
    }

    @Override
    public Type getArrayTypeToken() {
        return new TypeToken<ArrayList<TeacherDetail>>() {
        }.getType();
    }

}
