package com.woilsy.mock.test.entity

import com.woilsy.mock.annotations.Mock
import com.woilsy.mock.type.Type

data class UserInfo(
    val nickName: String,
    val age: Int,
    @Mock(type = Type.IMAGE)
    val avatar: String,
) {
    override fun toString(): String {
        return "UserInfo(nickName='$nickName', age=$age, avatar='$avatar')"
    }
}
