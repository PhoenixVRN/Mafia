package com.fenix.app.util;

import com.google.gson.Gson;

public final class JsonUtil {

    public static String Serialize(Object obj){
        return new Gson().toJson(obj);
    }

    public static <T> T Parse(Class<T> cls, String json){
        return new Gson().fromJson(json, cls);
    }
}
