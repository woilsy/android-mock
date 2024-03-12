package com.woilsy.mock.data;

import com.woilsy.mock.entity.MockData;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Mock data from is
 */
public class InputStreamDataSource implements DataSource {

    private final InputStream inputStream;

    public InputStreamDataSource(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public List<MockData> getMockData() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            while (br.ready()) {
                String line = br.readLine();
                sb.append(line.trim().replaceAll(" ", ""));
            }
            br.close();
            return new JsonDataSource(sb.toString()).getMockData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
