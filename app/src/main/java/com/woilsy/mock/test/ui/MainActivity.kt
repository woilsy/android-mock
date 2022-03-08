package com.woilsy.mock.test.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.woilsy.mock.Mocker
import com.woilsy.mock.test.R
import com.woilsy.mock.test.entity.LoginRequest
import com.woilsy.mock.test.entity.RegisterRequest
import com.woilsy.mock.test.ext.getApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun testRequest(view: View) {
        getApiService()
            .test(Mocker.getMockBaseUrl())
            .enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(p0: Call<ResponseBody?>, p1: Response<ResponseBody?>) {
                    Log.d(TAG, "onResponse: $p1")
                }

                override fun onFailure(p0: Call<ResponseBody?>, p1: Throwable) {
                    Log.e(TAG, "onFailure: ", p1)
                }
            })
    }

    fun testRequest2(view: View) {
        getApiService()
            .test(Mocker.getMockBaseUrl(), "111")
            .enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(p0: Call<ResponseBody?>, p1: Response<ResponseBody?>) {
                    Log.d(TAG, "onResponse: $p1")
                }

                override fun onFailure(p0: Call<ResponseBody?>, p1: Throwable) {
                    Log.e(TAG, "onFailure: ", p1)
                }
            })
    }

    fun login(view: View) {
        getApiService()
            .login(LoginRequest("123", "456"))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "login: succeed $it")
                },
                {
                    Log.e(TAG, "login: error", it)
                }
            )
    }

    fun logout(view: View) {
        getApiService()
            .logout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "logout: succeed $it")
                },
                {
                    Log.e(TAG, "logout: error", it)
                }
            )
    }

    fun getUserInfo(view: View) {
        getApiService()
            .getUserInfo("1", "missss")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "getUserInfo: succeed $it")
                },
                {
                    Log.e(TAG, "getUserInfo: error", it)
                }
            )
    }

    fun redirectRequest(view: View) {
        getApiService()
            .hotKey
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "redirectRequest: succeed $it")
                },
                {
                    Log.e(TAG, "redirectRequest: error", it)
                }
            )
    }

    fun delete(view: View) {
        getApiService()
            .deleteExtra("record", "1")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "delete: succeed $it")
                },
                {
                    Log.e(TAG, "delete: error", it)
                }
            )
    }

    fun singleGeneric(view: View) {
        getApiService()
            .singleGeneric()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "singleGeneric: succeed $it")
                },
                {
                    Log.e(TAG, "singleGeneric: error", it)
                }
            )
    }

    fun multipleGeneric(view: View) {
        getApiService()
            .multipleGeneric()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "multipleGeneric: succeed $it")
                },
                {
                    Log.e(TAG, "multipleGeneric: error", it)
                }
            )
    }

    fun register(view: View) {
        getApiService()
            .register(RegisterRequest("leo", "123456"))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "register: succeed $it")
                },
                {
                    Log.e(TAG, "register: error", it)
                }
            )
    }

    fun update(view: View) {
        getApiService()
            .updateUserInfo("name1", "address1")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "update: succeed $it")
                },
                {
                    Log.e(TAG, "update: error", it)
                }
            )
    }

}