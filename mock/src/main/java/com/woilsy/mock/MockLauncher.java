package com.woilsy.mock;

//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;

import com.google.gson.Gson;
import com.woilsy.mock.test.ApiService;
import com.woilsy.mock.test.MockBean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class MockLauncher {

//    public static void start(Context context, Class<?>... classes) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(new Intent(context, MockService.class));
//        } else {
//            context.startService(new Intent(context, MockService.class));
//        }
//        //解析class内部并插入map
//        for (Class<?> cls : classes) {
//            parse(cls);
//        }
//    }

    public static void main(String[] args) {
        parse(ApiService.class);
    }

    private static void parse(Class<?> cls) {
        MockBean<List<String>> mockBean = new MockBean<>();
        mockBean.a = "";
        mockBean.bean2 = new MockBean.Bean2();
        mockBean.data = new ArrayList<>();
        String json = new Gson().toJson(mockBean);
        System.out.println(json);
        //解析cls并传递给url管理
        UrlManager urlManager = UrlManager.getInstance();
        //第一步：获取url
        Method[] methods = cls.getMethods();
        for (Method m : methods) {
            System.out.println("====== 解析Method " + m.getName() + " start ======");
            //类型本身一般没有什么意义 需要注意的是该类型中的泛型 以及ResponseBody的处理
            Type type = m.getGenericReturnType();
            System.out.println("顶层类型:" + type);
            actTypes(type);
            System.out.println("====== 解析Method " + m.getName() + " end ======");
        }
        String url = "";
        //第二步：获取数据
        Object bean = null;
        String data = new Gson().toJson(bean);

        //第三步：插入数据
        urlManager.urlDataMap.put(url, data);
    }

    private static void actTypes(Type type) {
        if (type instanceof ParameterizedType) {//带泛型
            // 强制转型为带参数的泛型类型
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            if (types.length > 0) {//只需要处理第一个
                Type childType = types[0];
                System.out.println("子参数类型:" + childType);
                if (childType == ResponseBody.class) {//不知道咋处理 不管了
                    //需要特殊处理的类型 也可以不处理
                } else {//需要根据层级一层一层处理
                    try {
                        String clsName = "";
                        if (childType instanceof ParameterizedType) {
                            Type rawType = ((ParameterizedType) childType).getRawType();
                            System.out.println("真实类型:" + rawType);
                            clsName = ((Class<?>) rawType).getName();
                        } else {
                            clsName = childType.toString();
                        }
                        System.out.println("尝试反射" + clsName);
                        Class<?> cls = Class.forName(clsName);
                        Field[] fields = cls.getFields();
                        for (Field f : fields) {
                            System.out.println(f.getName() + "↓");
                            System.out.println("isSynthetic:" + f.isSynthetic());
                            actTypes(f.getType());//进入递归
                        }
                    } catch (ClassNotFoundException e) {
                        System.out.println("未找到" + childType + "反射创建失败");
                    }
                }
            }
        } else {//不带泛型的类型：分为基本类型和其他类型，可以直接尝试转换
            Class<?> cls = (Class<?>) type;
            System.out.println("最终类型:" + type);
        }
    }

    //限制1 getFields()必须为public修饰
}
