package com.woilsy.mock.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.woilsy.mock.Mocker;
import com.woilsy.mock.options.MockOptions;

/**
 * 用于初始化Mocker，初始化还需要添加拦截器才会完成。
 */
public class MockInitProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        Mocker.init(getContext(), MockOptions.getDefault());
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