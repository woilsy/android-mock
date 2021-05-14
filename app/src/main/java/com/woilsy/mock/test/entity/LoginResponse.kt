package com.woilsy.mock.test.entity

data class LoginResponse(
    val userInfo: UserInfo

) {
    override fun toString(): String {
        return "LoginResponse(userInfo=$userInfo)"
    }
}
