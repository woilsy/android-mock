package com.woilsy.mock.test.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.woilsy.mock.Mocker
import com.woilsy.mock.test.R
import com.woilsy.mock.test.api.ApiService2
import com.woilsy.mock.test.entity.LoginRequest
import com.woilsy.mock.test.entity.RegisterRequest
import com.woilsy.mock.test.ext.getApiService
import com.woilsy.mock.test.http.HttpManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("CheckResult")
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
            .test(Mocker.getMockBaseUrl() + "/hello", "123")
            .enqueue(object : Callback<List<String>?> {
                override fun onResponse(p0: Call<List<String>?>, p1: Response<List<String>?>) {
                    Log.d(TAG, "onResponse: $p1")
                }

                override fun onFailure(p0: Call<List<String>?>, p1: Throwable) {
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

    fun getStringList(view: View) {
        getApiService()
            .normalList
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "getStringList: $it")
                },
                {
                    Log.e(TAG, "getStringList: ", it)
                }
            )
    }

    fun getUserList(view: View) {
        getApiService()
            .userList
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d(TAG, "getUserList: $it")
                },
                {
                    Log.e(TAG, "getUserList: ", it)
                }
            )
    }

    fun suspend1(view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = HttpManager.getProxyObject(ApiService2::class.java).method1()
                Log.d(TAG, "suspend1: $data")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun suspend2(view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            HttpManager
                .getProxyObject(ApiService2::class.java)
                .method2()
                .catch {
                    it.printStackTrace()
                }
                .collect {
                    Log.d(TAG, "suspend2: $it")
                }
        }
    }

    fun getObject(view: View) {
        getApiService()
            .anyData
            .subscribe(
                {
                    Log.d(TAG, "getObject: ${it.data}")
                }, {
                    it.printStackTrace()
                }
            )
    }

}