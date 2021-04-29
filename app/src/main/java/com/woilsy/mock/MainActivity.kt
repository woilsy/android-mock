package com.woilsy.mock

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.woilsy.mock.ext.getApiService
import com.woilsy.mock.options.MockOptions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btn_start).setOnClickListener { httpTest() }
    }

    private fun httpTest() {
        getApiService()
            .test(MockOptions.BASE_URL)
            .enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: retrofit2.Response<ResponseBody?>
                ) {
                    Log.d("LEO", "onResponse: 返回了")
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Log.e("LEO", "onFailure: 网络请求出错！！！！", t)
                }
            })
    }
}