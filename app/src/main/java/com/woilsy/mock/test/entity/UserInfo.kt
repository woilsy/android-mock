package com.woilsy.mock.test.entity

import com.woilsy.mock.annotations.*

data class UserInfo(
    @Mock("我是xxx")
    val nickName: String,
    @MockIgnore
    val avatar: String,

    @Mock("[\"icon1.png\",\"icon2.png\"]")
    val icons: List<String>,

    val address: String,
    @MockStringRange("1", "2", "3")
    val aaa: String,

    @MockIntRange(from = 10, to = 12)
    val age: Int,
    @MockIntRange(1, 2, 3)
    val b: Int,

    @MockBooleanRange(true, false, false, false)
    val c: Boolean,

    @MockCharRange('a', 'b', 'c')
    val d: Char,
    @MockCharRange(from = 'a', to = 'z')
    val e: Char,

    @MockDoubleRange(1.01, 200.01)
    val f: Double,
    @MockDoubleRange(from = 1.0, to = 10.0)
    val g: Double,

    @MockFloatRange(1.01f, 2.02f)
    val h: Float,
    @MockFloatRange(from = 1.01f, to = 1.02f)
    val i: Float,

    @MockLongRange(100, 111)
    val j: Long,
    @MockLongRange(from = 100, to = 111)
    val k: Long,

    )