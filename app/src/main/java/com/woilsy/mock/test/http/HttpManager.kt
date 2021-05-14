package com.woilsy.mock.test.http

import android.content.Context
import com.google.gson.Gson
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor
import com.woilsy.mock.test.BuildConfig

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

object HttpManager {

    private val serviceMap: HashMap<Class<*>, Any> = HashMap(5)

    private var retrofit: Retrofit? = null

    /**
     * @param context 需要它的原因在于有时候需要使用缓存技术
     */
    fun init(context: Context, baseUrl: String) {
        val builder = OkHttpClient
            .Builder()
            .writeTimeout(1000 * 60.toLong(), TimeUnit.SECONDS)
            .connectTimeout(1000 * 60.toLong(), TimeUnit.SECONDS)
            .readTimeout(1000 * 60.toLong(), TimeUnit.SECONDS)
            .hostnameVerifier { _, _ -> true }
        if (BuildConfig.DEBUG) {
            val logger = Logger.getLogger("Http")
            builder.addInterceptor(LogInterceptor { logger.log(Level.INFO, it) })
        }
        retrofit = Retrofit.Builder()
            .client(builder.build())
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getProxyObject(tClass: Class<T>): T {
        return if (serviceMap.containsKey(tClass)) serviceMap[tClass] as T
        else {
            val t: T = retrofit?.create(tClass) ?: throw NullPointerException("没有执行初始化")
            serviceMap[tClass] = t!!
            t
        }
    }
}