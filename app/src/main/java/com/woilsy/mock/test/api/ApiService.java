package com.woilsy.mock.test.api;

import com.woilsy.mock.test.MockBean;
import com.woilsy.mock.test.MockBean2;
import com.woilsy.mock.test.MockBeanChild;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ApiService {

    @GET
    Call<ResponseBody> test(@Url String url);

    @POST("/request1")
    Observable<MockBean<List<String>>> getData1();

    @GET("/request2")
    Observable<MockBean<String>> getData2();

    @GET("/request3")
    Observable<String> getData3();

    @GET("/request4")
    Observable<MockBean<MockBean2<MockBeanChild>>> getData4();

    @GET("/request5")
    Observable<MockBean<MockBean2<List<MockBeanChild>>>> getData5();

    @GET("/request6/{id}")
    Observable<String> getData6(@Path("id") String id);


}
