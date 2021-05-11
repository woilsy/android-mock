package com.woilsy.mock.test;

import java.util.List;

public class A<T> {

    public T at;

    public B<C<List<Integer>>> blls;

    @Override
    public String toString() {
        return "A{" +
                "at=" + at +
                ", blls=" + blls +
                '}';
    }
}
