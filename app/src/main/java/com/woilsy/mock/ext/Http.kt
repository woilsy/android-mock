package com.woilsy.mock.ext

import com.woilsy.mock.api.ApiService
import com.woilsy.mock.http.HttpManager

fun getApiService(): ApiService {
    return HttpManager.getProxyObject(ApiService::class.java)
}