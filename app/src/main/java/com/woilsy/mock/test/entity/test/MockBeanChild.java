package com.woilsy.mock.test.entity.test;

import com.woilsy.mock.annotations.Mock;

import java.util.List;

public class MockBeanChild {

    @Mock("我是自定义的名字")
    public String name;

    public String nickname1 = "带默认属性的不会被替换";

    @Mock("默认值已被替换")
    public String nickname2 = "带默认属性的不会被替换，除非带mock注解";

    @Mock("{\"a\":\"1234\"}")
    public ChildA childA;

    @Mock("[]")
    public List<String> ls;

    public String avatar;

    public String address;

    public Integer age;

    static class ChildA {
        public String a;
    }
}
