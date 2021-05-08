package com.woilsy.mock.test;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.POST;

public interface ApiService {

//    @GET
//    Call<ResponseBody> test(@Url String url);

    //    @GET("/re1")
//    Call<String> getData();
//
//    @GET("/re2")
//    Call<List<String>> getData0();

    @POST("/request1")
    Call<MockBean<List<String>>> getData1();
//
//    @POST("/request1")
//    Call<MockBean<List<String>>> getData1();
//
//    @POST("/request2")
//    Call<List<MockBean<String>>> getData2();


}
