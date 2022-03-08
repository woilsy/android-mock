package com.woilsy.mock.test.entity

data class UserInfo(
    val nickName: String,
    val age: Int,
    val avatar: String,
) {
    override fun toString(): String {
        return "UserInfo(nickName='$nickName', age=$age, avatar='$avatar')"
    }
}
