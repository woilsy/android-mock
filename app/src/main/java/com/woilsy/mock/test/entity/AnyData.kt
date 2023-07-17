package com.woilsy.mock.test.entity

import com.woilsy.mock.annotations.Mock
import com.woilsy.mock.annotations.MockClass

/**
 * Any的场景一般为可能为多个内容中的一个，一般和某个字段的某个值形成键值对。--暂未实现
 */
class AnyData(
    val type: Int,
    @MockClass(className = "com.woilsy.mock.test.entity.Data1")
    val data: Any,
)

data class Data1(
    val age: Int,
    @Mock("我是自定义的数据")
    val name: String
)