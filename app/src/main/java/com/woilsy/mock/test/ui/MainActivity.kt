package com.woilsy.mock.test.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.woilsy.mock.options.MockOptions
import com.woilsy.mock.test.R
import com.woilsy.mock.test.ext.getApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btn_start0).setOnClickListener { httpTest0() }
        findViewById<View>(R.id.btn_start1).setOnClickListener { httpTest1() }
        findViewById<View>(R.id.btn_start2).setOnClickListener { httpTest2() }
        findViewById<View>(R.id.btn_start3).setOnClickListener { httpTest3() }
        findViewById<View>(R.id.btn_start4).setOnClickListener { httpTest4() }
        findViewById<View>(R.id.btn_start5).setOnClickListener { httpTest5() }
        findViewById<View>(R.id.btn_start6).setOnClickListener { httpTest6() }
        findViewById<View>(R.id.btn_start7).setOnClickListener { httpTest7() }
        findViewById<View>(R.id.btn_start8).setOnClickListener { httpTest8() }
        findViewById<View>(R.id.btn_start9).setOnClickListener { httpTest9() }
    }

    private fun httpTest0() {
        getApiService()
            .test(MockOptions.BASE_URL)
            .enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: retrofit2.Response<ResponseBody?>
                ) {
                    Log.d(TAG, "onResponse: 返回了")
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Log.e(TAG, "onFailure: 网络请求出错！！！！", t)
                }
            })
    }

    private fun httpTest1() {
        getApiService()
            .data1
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "httpTest1: 请求成功:$it")
                },
                {
                    Log.e(TAG, "httpTest1: 请求失败", it)
                }
            )
    }

    private fun httpTest2() {
        getApiService()
            .data2
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "httpTest2: 请求成功:$it")
                },
                {
                    Log.e(TAG, "httpTest2: 请求失败", it)
                }
            )
    }

    private fun httpTest3() {
        getApiService()
            .data3
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "httpTest3: 请求成功:$it")
                },
                {
                    Log.e(TAG, "httpTest3: 请求失败", it)
                }
            )
    }

    private fun httpTest4() {
        getApiService()
            .data4
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "httpTest4: 请求成功:$it")
                },
                {
                    Log.e(TAG, "httpTest4: 请求失败", it)
                }
            )
    }

    private fun httpTest5() {
        getApiService()
            .data5
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "httpTest5: 请求成功:$it")
                },
                {
                    Log.e(TAG, "httpTest5: 请求失败", it)
                }
            )
    }

    private fun httpTest6() {
        getApiService()
            .getData6("id222")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "httpTest6: 请求成功:$it")
                },
                {
                    Log.e(TAG, "httpTest6: 请求失败", it)
                }
            )
    }

    private fun httpTest7() {
        getApiService()
            .hotKey
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "httpTest7: 请求成功:$it")
                },
                {
                    Log.e(TAG, "httpTest7: 请求失败", it)
                }
            )
    }

    private fun httpTest8() {
        getApiService()
            .login("woilsy", "wszbmmqd")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "httpTest8: 请求成功:$it")
                },
                {
                    Log.e(TAG, "httpTest8: 请求失败", it)
                }
            )
    }

    private fun httpTest9() {

    }
}