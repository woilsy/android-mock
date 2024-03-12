package com.woilsy.mock.data;

import com.woilsy.mock.entity.MockData;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * Mock data from file
 */
public class FileDataSource implements DataSource {

    private final File file;

    public FileDataSource(File file) {
        this.file = file;
    }

    @Override
    public List<MockData> getMockData() {
        try {
            if (file != null && file.exists()) {
                return new InputStreamDataSource(new FileInputStream(file)).getMockData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
