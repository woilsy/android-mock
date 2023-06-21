package com.woilsy.mock.test.api

import com.woilsy.mock.annotations.MockObj
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

@MockObj
interface ApiService2 {

    @GET("/user/method1")
    suspend fun method1(): List<String>

    //不要加suspend 否则会被默认的CallAdapter转换为Call<Flow<String>>
    @GET("/user/method2")
    fun method2(): Flow<String>

}