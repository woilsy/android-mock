package com.woilsy.mock.test;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.POST;

public interface ApiService {
    //情景1
//    @GET
//    Call<ResponseBody> test(@Url String url);
    //情景2
//    @GET("/re2")
//    Call<String> getData();
    //情景3
//    @GET("/re3")
//    Call<List<String>> getData0();
    //情景4
//    @POST("/re5")
//    Call<MockBean<List<String>>> getData1();
    // 情景5
//    @POST("/re5")
//    Call<List<MockBean<String>>> getData1();

    //情景6
//    @POST("/re6")
//    Call<MockBean2<String>> getData6();

    //情景7
//    @POST("/re7")
//    Call<MockBean2<List<String>>> getData7();

    //情景8
//    @POST("/re8")
//    Call<MockBean2<List<MockBean<String>>>> getData8();

    //情景9
//    @POST("/re9")
//    Call<MockBean2<List<MockBean<String>>>> getData9();

    //情景10
    @POST("/re10")
    Call<MockBean2<MockBean<List<String>>>> getData10();

}
