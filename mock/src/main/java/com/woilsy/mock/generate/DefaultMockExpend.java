package com.woilsy.mock.generate;

import com.woilsy.mock.constants.MockDefault;

import java.util.Date;

public class DefaultMockExpend implements MockExpend {

    @Override
    public Date getDate() {
        return new Date();
    }

    @Override
    public String getImage() {
        return MockDefault.IMAGE;
    }

    @Override
    public int getAge() {
        return MockDefault.AGE;
    }

    @Override
    public String getName() {
        return MockDefault.NAME;
    }

    @Override
    public String getAddress() {
        return MockDefault.ADDRESS;
    }

    @Override
    public String getNickName() {
        return MockDefault.NICKNAME;
    }

    @Override
    public String getPhone() {
        return MockDefault.PHONE;
    }
}
