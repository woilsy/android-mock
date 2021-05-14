package com.woilsy.mock.test.entity

data class HttpResult<T>(
    val data: T?,
    val code: Int,
    val msg: String
)
