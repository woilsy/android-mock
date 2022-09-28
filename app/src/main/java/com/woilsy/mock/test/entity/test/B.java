package com.woilsy.mock.test.entity.test;

import java.util.List;

public class B<T> {

    public String b;

    public List<T> btls;

    @Override
    public String toString() {
        return "B{" +
                "b='" + b + '\'' +
                ", btls=" + btls +
                '}';
    }
}
