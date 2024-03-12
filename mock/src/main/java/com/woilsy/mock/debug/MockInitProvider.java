package com.woilsy.mock.debug;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * 用于初始化Mocker，初始化还需要添加拦截器才会完成。
 */
public class MockInitProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        try {
            Class<?> mockerClass = Class.forName("com.woilsy.mock.Mocker");
            Class<?> optionClass = Class.forName("com.woilsy.mock.options.MockOptions");
            Method defaultMethod1 = optionClass.getMethod("getDefault");
            Object defaultOptions = defaultMethod1.invoke(null);
            Method method = mockerClass.getMethod("init", Context.class, optionClass);
            method.invoke(null, getContext(), defaultOptions);
            Log.d(Constants.TAG, "onCreate: 正常加载并初始化");
        } catch (Exception e) {
            Log.e(Constants.TAG, "can not find class", e);
        }
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}