package com.woilsy.mock.test.ext

import com.woilsy.mock.test.api.ApiService
import com.woilsy.mock.test.http.HttpManager

fun getApiService(): ApiService {
    return HttpManager.getProxyObject(ApiService::class.java)
}