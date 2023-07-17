package com.woilsy.mock.test.entity

import com.woilsy.mock.annotations.MockClass

/**
 * Any的场景一般为可能为多个内容中的一个，一般和某个字段的某个值形成键值对。--暂未实现
 */
class AnyData(
    val type: Int,
    @MockClass(Data1::class)
    val data: Any,
)

data class Data1(
    val age: Int,
    val name: String
)