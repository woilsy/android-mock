package com.woilsy.mock.test.api;

import com.woilsy.mock.annotations.MockExclude;
import com.woilsy.mock.annotations.MockObj;
import com.woilsy.mock.test.entity.HttpResult;
import com.woilsy.mock.test.entity.LoginRequest;
import com.woilsy.mock.test.entity.LoginResponse;
import com.woilsy.mock.test.entity.PageBean;
import com.woilsy.mock.test.entity.RegisterRequest;
import com.woilsy.mock.test.entity.UserInfo;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

@MockObj
public interface ApiService {

    @GET
    Call<ResponseBody> test(@Url String url);

    @POST
    @FormUrlEncoded
    Call<List<String>> test(@Url String url, @Field("xxx") String a);

    @POST("/user/login")
    Observable<HttpResult<LoginResponse>> login(@Body LoginRequest request);

    @GET("/user/info/ids/{id}")
    Observable<HttpResult<UserInfo>> getUserInfo(@Path("id") String id, @Query("name") String name);

    @FormUrlEncoded
    @POST("/user/info/update")
    Observable<HttpResult<String>> updateUserInfo(@Field("name") String name, @Field("address") String address);

    @DELETE("/user/log/{extra}/{id}")
    Observable<HttpResult<String>> deleteExtra(@Path("extra") String extra, @Path("id") String id);

    @PUT("/user/register")
    Observable<ResponseBody> register(@Body RegisterRequest request);

    @MockExclude
    @GET("/hotkey/json")
    Observable<ResponseBody> getHotKey();

    @GET("/test/normalLs")
    Observable<HttpResult<List<String>>> getNormalList();

    @GET("/test/userList")
    Observable<HttpResult<PageBean<UserInfo>>> getUserList();

}

