package com.woilsy.mock.test;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiService {

    @GET
    Call<ResponseBody> test(@Url String url);

    @POST("/request1")
    Call<MockBean<List<String>>> getData1();

    @GET("/request2")
    Call<MockBean<String>> getData2();

    @GET("/request3")
    Call<String> getData3();

    @GET("/request4")
    Call<MockBean<MockBean2<MockBeanChild>>> getData4();

    @GET("/request5")
    Call<MockBean<MockBean2<List<MockBeanChild>>>> getData5();

}
