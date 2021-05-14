package com.woilsy.mock.test.api;

import com.woilsy.mock.annotations.MockExclude;
import com.woilsy.mock.test.A;
import com.woilsy.mock.test.B;
import com.woilsy.mock.test.C;
import com.woilsy.mock.test.D;
import com.woilsy.mock.test.E;
import com.woilsy.mock.test.entity.HttpResult;
import com.woilsy.mock.test.entity.LoginRequest;
import com.woilsy.mock.test.entity.LoginResponse;
import com.woilsy.mock.test.entity.RegisterRequest;
import com.woilsy.mock.test.entity.UserInfo;

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

public interface ApiService {

    //un support
    @GET
    Call<ResponseBody> test(@Url String url);

    @POST("/user/login")
    Observable<HttpResult<LoginResponse>> login(@Body LoginRequest request);

    @GET("/user/info/ids/{id}")
    Observable<HttpResult<UserInfo>> getUserInfo(@Path("id") String id, @Query("name") String name);

    @FormUrlEncoded
    @POST("/user/info/update")
    Observable<HttpResult<String>> updateUserInfo(@Field("name") String name, @Field("address") String address);

    @POST("/user/logout")
    Observable<HttpResult<Object>> logout();

    @DELETE("/user/log/{extra}/{id}")
    Observable<HttpResult<String>> deleteExtra(@Path("extra") String extra, @Path("id") String id);

//    @HTTP(method = "DELETE", path = "/user/log/{extra}/{id}", hasBody = true)
//    Observable<HttpResult<String>> deleteExtra(@Path("extra") String extra, @Path("id") String id,
//                                               @Body DeleteExtraRequest request);

    @PUT("/user/register")
    Observable<ResponseBody> register(@Body RegisterRequest request);

    @MockExclude
    @GET("/hotkey/json")
    Observable<ResponseBody> getHotKey();

    @POST("/test/generic1")
    Observable<HttpResult<A<B<C<D>>>>> singleGeneric();

    @GET("/test/generic2")
    Observable<HttpResult<E<A<Integer>, B<String>, C<Boolean>>>> multipleGeneric();
}
