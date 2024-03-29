package com.woilsy.mock.data;

import android.content.Context;

import com.woilsy.mock.entity.MockData;

import java.io.IOException;
import java.util.List;

/**
 * Mock data from file in 'assets'
 * 可通过以下语句进行排除
 * buildTypes {
 *         release {
 *             aaptOptions {
 *                 ignoreAssetsPattern '!aaa.js:!bbb.css:!ccc.json:'
 *             }
 *         }
 * }
 */
public class AssetFileDataSource implements DataSource {

    private final Context context;

    private final String fileName;

    public AssetFileDataSource(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    @Override
    public List<MockData> getMockData() {
        try {
            return new InputStreamDataSource(context.getAssets().open(fileName)).getMockData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
