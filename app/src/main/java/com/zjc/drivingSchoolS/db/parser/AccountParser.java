package com.zjc.drivingSchoolS.db.parser;

import com.google.gson.reflect.TypeToken;
import com.mobo.mobolibrary.parser.JsonParser;
import com.zjc.drivingSchoolS.db.model.Account;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AccountParser extends JsonParser<Account> {

    @Override
    public Type getResultMessageType() {
        return new TypeToken<Account>() {
        }.getType();
    }

    @Override
    public Type getArrayTypeToken() {
        return new TypeToken<ArrayList<Account>>() {
        }.getType();
    }

}
