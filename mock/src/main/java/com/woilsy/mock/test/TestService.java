package com.woilsy.mock.test;

import retrofit2.Call;
import retrofit2.http.POST;

public interface TestService {

    @POST("/re0")
    Call<MockBeanChild> getData0();

//    @POST("/re000")
//    Call<E<A<Integer>, B<Long>, C<String>>> getData000();

//    @MockExclude
//    @POST("/re1")
//    Call<A<D>> getData1();
//
//    @MockInclude
//    @POST("/re2")
//    Call<A<B<C<List<D>>>>> getData2();

//    @POST("/re3")
//    Call<A<List<B<List<Integer>>>>> getData3();

    //    @POST("/re4")
//    Call<MockBean<List<String>>> getData4();
//
//    @POST("/re5")
//    Call<List<MockBean<String>>> getData5();
//
//    @POST("/re6")
//    Call<MockBean2<String>> getData6();
//
//    @POST("/re7")
//    Call<MockBean2<List<String>>> getData7();
//
//    @POST("/re8")
//    Call<MockBean2<List<MockBean<String>>>> getData8();
//
//    @POST("/re9")
//    Call<MockBean2<MockBean<List<String>>>> getData9();
//
//    @POST("/re10")
//    Call<List<A<B<List<String>>>>> getData10();

}
