package com.woilsy.mock.test.http

import android.content.Context
import com.coder.vincent.sharp_retrofit.call_adapter.flow.FlowCallAdapterFactory
import com.google.gson.Gson
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor
import com.woilsy.mock.debug.MockGetter
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
            //初始化mock
            initMockConfig(context, builder)
        }
        retrofit = Retrofit.Builder()
            .client(builder.build())
            .baseUrl(baseUrl)
            .addCallAdapterFactory(FlowCallAdapterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
    }

    private fun initMockConfig(context: Context, builder: OkHttpClient.Builder) {
//        //可选 相当于更新MockOptions
//        Mocker.init(
//            context,
//            MockOptions.Builder()
//                .enableLog(true)
//                .enableNotification(false)
//                .setMockListRandomSize(1, 3)
//                .setDynamicAccess(true, true)
//                .addRule(MatchRule())
//                .addRule(BaseTypeGenerator())
//                .setDataSource(AssetFileDataSource(context, "mock.json"))
//                .setPort(9090)
//                .build()
//        )
        builder.addInterceptor(MockGetter.getMockInterceptor())
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