package com.woilsy.mock.test.api;

import com.woilsy.mock.test.MockBean;
import com.woilsy.mock.type.MockInclude;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.POST;

public interface ApiService2 {

    @POST("/request100")
    Observable<MockBean<List<String>>> getData100();

    @MockInclude
    @POST("/request101")
    Observable<MockBean<List<String>>> getData101();
}
