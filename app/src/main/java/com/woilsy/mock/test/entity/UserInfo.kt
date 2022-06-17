package com.woilsy.mock.test.entity

data class UserInfo(
    val nickName: String,
    val age: Int,
    val avatar: String,
    val address: String,
    val aaa: String,
) {

    override fun toString(): String {
        return "UserInfo(nickName='$nickName', age=$age, avatar='$avatar', address='$address')"
    }
}
