package com.woilsy.mock.test;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiService {

    @GET
    Call<ResponseBody> test(@Url String url);

    @POST("123")
    Observable<MockBean<List<String>>> getSomeThing();

}
