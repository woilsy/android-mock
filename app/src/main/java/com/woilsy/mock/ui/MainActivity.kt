package com.woilsy.mock.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.woilsy.mock.R
import com.woilsy.mock.ext.getApiService
import com.woilsy.mock.options.MockOptions
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btn_start1).setOnClickListener { httpTest1() }
        findViewById<View>(R.id.btn_start2).setOnClickListener { httpTest2() }
        findViewById<View>(R.id.btn_start3).setOnClickListener { httpTest3() }
        findViewById<View>(R.id.btn_start4).setOnClickListener { httpTest4() }
    }

    private fun httpTest1() {
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

    private fun httpTest2() {
        getApiService()
            .data1
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d("LEO", "httpTest2: 请求成功:$it")
                },
                {
                    Log.e("LEO", "httpTest2: 请求失败", it)
                }
            )
    }

    private fun httpTest3() {
        getApiService()
            .data1
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d("LEO", "httpTest2: 请求成功:$it")
                },
                {
                    Log.e("LEO", "httpTest2: 请求失败", it)
                }
            )
    }

    private fun httpTest4() {
        getApiService()
            .data1
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d("LEO", "httpTest2: 请求成功:$it")
                },
                {
                    Log.e("LEO", "httpTest2: 请求失败", it)
                }
            )
    }
}