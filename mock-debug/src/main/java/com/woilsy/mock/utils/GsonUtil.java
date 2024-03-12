package com.woilsy.mock.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GsonUtil {

    private static Gson GSON = new GsonBuilder().create();

    public static void replaceGson(Gson gson) {
        GsonUtil.GSON = gson;
    }

    public static String toJson(Object object) {
        try {
            return GSON.toJson(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T jsonToObj(String json, Type type) {
        try {
            return GSON.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> List<T> jsonToList(String json, Class<T> childCls) {
        try {
            ParameterizedType parameterizedType = $Gson$Types.newParameterizedTypeWithOwner(null, ArrayList.class, childCls);
            return GSON.fromJson(json, parameterizedType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
