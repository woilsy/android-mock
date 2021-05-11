package com.woilsy.mock.test;

import com.woilsy.mock.annotations.Mock;
import com.woilsy.mock.type.Type;

public class MockBeanChild {

    @Mock("我是自定义的名字")
    public String name;

    public String nickname1 = "带默认属性的不会被替换";

    @Mock("我是会被解析的值")
    public String nickname2 = "带默认属性的不会被替换，除非带mock注解";

    @Mock(type = Type.IMAGE)
    public String avatar;

    @Mock(type = Type.ADDRESS)
    public String address;

    @Mock(type = Type.AGE)
    public Integer age;

}
