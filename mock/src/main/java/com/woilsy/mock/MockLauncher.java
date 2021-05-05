package com.woilsy.mock;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.woilsy.mock.generate.Generator;
import com.woilsy.mock.options.MockOptions;
import com.woilsy.mock.service.MockService;
import com.woilsy.mock.test.ApiService;
import com.woilsy.mock.utils.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * 启动器，目前支持POST/DELETE/GET/PUT 四种请求<br/>
 * TODO 分析静态url 动态url(@Url String url)形式需要另想办法<br/>
 * TODO 目前获取字段是通过getFields，全字段需要过滤某些默认字段<br/>
 * TODO 返回值为ResponseBody时，暂不支持<br/>
 */
public class MockLauncher {

    private static final Map<String, Type> clsTb = new HashMap<>();

    private static final Generator GENERATOR = new Generator();

    public static void start(Context context, Class<?>... classes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, MockService.class));
        } else {
            context.startService(new Intent(context, MockService.class));
        }
        //解析class内部并插入map
        for (Class<?> cls : classes) {
            try {
                parse(cls);
            } catch (Exception e) {
                println("解析Service失败");
            }
        }
    }

    public static void main(String[] args) {
        parse(ApiService.class);
    }

    private static void parse(Class<?> cls) {
        //解析cls并传递给url管理
        UrlManager urlManager = UrlManager.getInstance();
        //第一步：获取url及数据
        Method[] methods = cls.getMethods();
        Gson gson = new GsonBuilder().create();
        for (Method m : methods) {
            println("====== 解析Method " + m.getName() + " start ======");
            //类型本身一般没有什么意义 需要注意的是该类型中的泛型 以及ResponseBody的处理
            String actUrl = actUrl(m);
            println("url:" + actUrl);
            Object o = actType(m);
            println("data:" + (o == null ? "null" : gson.toJson(o)));
            if (actUrl != null && o != null) {
                urlManager.urlDataMap.put(actUrl, gson.toJson(o));
            }
            println("====== 解析Method " + m.getName() + " end ======");
            println("---------------分割线---------------");
        }
    }

    //分析静态url
    private static String actUrl(Method m) {
        Annotation[] annotations = m.getAnnotations();
        for (Annotation a : annotations) {
            if (a instanceof GET) {
                return transString(((GET) a).value());
            } else if (a instanceof POST) {
                return transString(((POST) a).value());
            } else if (a instanceof DELETE) {
                return transString(((DELETE) a).value());
            } else if (a instanceof PUT) {
                return transString(((PUT) a).value());
            } else {
                println("不支持的请求类型，目前只支持GET POST DELETE PUT");
            }
        }
        return null;
    }

    private static String transString(String s) {
        return s == null || s.isEmpty() ? null : s;
    }

    private static Object actType(Method m) {
        return parseType(m.getGenericReturnType());
    }

    /**
     * @return 返回一个拥有所有mock属性的对象，将其作为value
     */
    private static Object parseType(Type type) {
        return handleType(type, null, null);
    }

    private static Object handleType(Type type, Object parent, Field parentField) {
        if (type instanceof ParameterizedType) {//带参数类型
            // 强制转型为带参数的泛型类型
            Type rawType1 = ((ParameterizedType) type).getRawType();
            if (rawType1 == Map.class) {//需要获取key value的类型再处理
                println("handleType()->map类型，尝试分析创建");
                Map<Object, Object> map = new HashMap<>();
                Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
                if (typeArguments != null && typeArguments.length == 2) {
                    Object key = handleType(typeArguments[0], null, null);
                    Object value = handleType(typeArguments[1], null, null);
                    map.put(key, value);
                }
                return setParentField(parent, parentField, map);
            } else if (rawType1 == List.class) {
                println("handleType()->List类型，尝试分析创建");
                List<Object> objects = new ArrayList<>();
                Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
                if (typeArguments != null && typeArguments.length == 1) {
                    objects.add(handleType(typeArguments[0], null, null));
                }
                return setParentField(parent, parentField, objects);
            } else if (rawType1 == Set.class) {//同上
                println("handleType()->Set类型，尝试分析创建");
                HashSet<Object> objects = new HashSet<>();
                Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
                if (typeArguments != null && typeArguments.length == 1) {
                    objects.add(handleType(typeArguments[0], null, null));
                }
                return setParentField(parent, parentField, objects);
            } else {//
                if (parent == null) {//如果为null 从泛型一开始解析 不用管是Observable、Flow、Single之类的
                    //可能是com.woilsy.mock.test.MockBean2<com.woilsy.mock.test.MockBeanChild>
                    Type[] types = ((ParameterizedType) type).getActualTypeArguments();
                    Type childType = types[0];
                    if (childType == ResponseBody.class) {
                        println("handleType()->ResponseBody类型暂不处理");
                    } else {
                        println("handleType()->childType:" + childType);
                        return createClassObj(childType, null, null);
                    }
                } else {//其他从当前开始分析
                    createClassObj(type, parent, parentField);
                }
            }
        } else if (type instanceof Class<?>) {//class类型
            Object obj = handleClass((Class<?>) type, parent, parentField);
            if (parent == null) {
                return obj;
            }//else do nothing 否则会造成无限循环
        } else if (type instanceof TypeVariable) {//类型变量 name:T bounds:Object
//            TypeVariable表示的是类型变量，它用来反映的是JVM编译该泛型前的信息，例如List<T>中的T就是类型变量，它在编译时需要被转换为一个具体的类型后才能正常使用。
//            该接口常用的方法有3个，分别是：
//            (1) Type[] getBounds()——获取类型变量的上边界，如果未明确声明上边界则默认为Object。例如Class<K extents Person>中K的上边界就是Person。
//            (2) D getGenericDeclaration()——获取声明该类型变量的原始类型，例如Test<K extents Person>中原始类型是Test。
//            (3) String getName()——获取在源码中定义的名字，上例中为K。
            //尝试从map中获取原始类型
            Type actType = clsTb.get(parent.getClass().getName());
            if (actType != null) {
                println("handleType()->尝试从clsTb中获取对象实际泛型类型" + actType);
                handleType(actType, parent, parentField);//直接重进
            } else {
                println("handleType()->从clsTb中获取对象实际泛型类型失败" + type);
            }
        } else if (type instanceof GenericArrayType) {
            //GenericArrayType表示的是数组类型且组成元素时ParameterizedType或TypeVariable，例如List<T>或T[]，该接口只有
            // Type getGenericComponentType()一个方法，它返回数组的组成元素类型。
            println("handleType()->" + type + "暂不处理");
        } else if (type instanceof WildcardType) {
            //例如? extends Number 和 ? super Integer。
            //Wildcard接口有两个方法，分别是：
            //(1) Type[] getUpperBounds()——返回类型变量的上边界。
            //(2) Type[] getLowerBounds()——返回类型变量的下边界。
            println("handleType()->" + type + "暂不处理");
        } else {
            println("handleType()->无法识别的类型:" + type);
        }
        return null;
    }

    //尝试创建class对象
    private static Object createClassObj(Type childType, Object parent, Field parentField) {
        String clsName = "";
        if (childType instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) childType).getRawType();
            clsName = ((Class<?>) rawType).getName();
            //插入泛型类型
            Type[] typeArguments = ((ParameterizedType) childType).getActualTypeArguments();
            if (typeArguments != null && typeArguments.length > 0) {
                //将泛型实际类型传入到map中，因为反射对象会导致泛型丢失
                clsTb.put(clsName, typeArguments[0]);
            }
        } else {//不带泛型的，尝试获取finalObj
            Object obj = handleType(childType, null, null);
            if (obj != null) {
                return obj;
            } else {
                clsName = childType.toString().replace("class ", "");
            }
        }
        println("handleType()->尝试反射" + clsName);
        try {
            return handleClass(Class.forName(clsName), parent, parentField);
        } catch (Exception e) {
            println("handleType()->" + clsName + "反射失败," + e.getMessage());
        }
        return null;
    }

    private static Object handleClass(Class<?> cls, Object parent, Field parentField) {
        Object finalObj = getFinalObj(cls);
        if (finalObj == null) {//表示该类型需要解析
            println("handleClass()->非final类型，需要单独解析" + cls.getName());
            return handleObjClass(cls, parent, parentField);
        } else {//表示该类型不需要解析 直接设置给父类 返回父类 如果父类为null 返回自身
            println("handleClass()->" + cls.getName() + "->final类型，直接返回或设置给parent");
            return parent == null ? finalObj : setParentField(parent, parentField, finalObj);
        }
    }

    //处理对象class
    private static Object handleObjClass(Class<?> cls, Object parent, Field parentField) {
        //获取class对象实例
        Object obj = tryGetClassObj(cls);
        if (obj == null) {
            println("handleObjClass()->获取class实例失败，直接return");
            return null;
        }
        //获取所有字段
        Field[] fields = cls.getFields();
        for (Field f : fields) {
            Type genericType = f.getGenericType();
            println("handleObjClass()->字段：" + f.getName() + " 类型:" + genericType);
            handleType(genericType, obj, f);
        }
        return parent == null ? obj : setParentField(parent, parentField, obj);
    }

    private static Object setParentField(Object parent, Field parentField, Object value) {
        if (parent == null || parentField == null) return null;
        try {
            parentField.setAccessible(true);
            parentField.set(parent, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return parent;
    }

    private static Object tryGetClassObj(Class<?> cls) {
        try {//默认构造器创建
            Constructor<?> constructor = cls.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {//使用不安全的方式创建
            return ClassUtils.allocateInstance(cls);
        }
    }

    private static boolean isFinalType(Class<?> cls) {
        return getFinalObj(cls) != null;
    }

    private static Object getFinalObj(Class<?> cls) {
        return GENERATOR.get(cls);
    }

    private static void println(String msg) {
        if (MockOptions.DEBUG) {
            System.out.println(msg);
        }
    }

}
