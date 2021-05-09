package com.woilsy.mock;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.woilsy.mock.generate.Generator;
import com.woilsy.mock.options.MockOptions;
import com.woilsy.mock.service.MockService;
import com.woilsy.mock.test.TestService;
import com.woilsy.mock.type.Image;
import com.woilsy.mock.type.Images;
import com.woilsy.mock.utils.ClassUtils;
import com.woilsy.mock.utils.GsonUtil;

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
import java.util.Random;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * 启动器，目前支持POST/DELETE/GET/PUT 四种请求<br/>
 * <br/>
 * 如果需要修改部分配置，如log打印，可以通过MockOptions设置<br/>
 * <br/>
 * 某些策略说明：<br/>
 * 1,如果字段有默认值，那么不会处理该字段，直接返回默认值数据。<br/>
 * 2,返回值为ResponseBody时或需要自定义mock数据时，需要自行新建一个mock数据文件，以<br/>
 * [<br/>
 * {<br/>
 * "url":"/xxx"<br/>
 * "data":{} <br/>
 * }<br/>
 * ]<br/>
 * 的形式传入，可以放在assets文件中，上线前删除该文件，在启动前调用MockUrlData.addFromXXX导入。<br/>
 * 3,如果使用了动态url(@Url String url)，由于其可能不访问MockOptions.BASE_URL，所以暂时无法处理。<br/>
 */
public class MockLauncher {

    private final Map<String, Type> clsTb = new HashMap<>();

    private Generator generator;

    private MockOptions mockOptions;

    private boolean isMethodStart = false;

    private MockLauncher() {

    }

    public static void start(Context context, MockOptions options, Class<?>... classes) {
        MockLauncher launcher = new MockLauncher();
        launcher.initByOptions(context, options);
        launcher.startMockService(context);
        launcher.parseClasses(classes);
    }

    public static void main(String[] args) {
        MockLauncher launcher = new MockLauncher();
        //
        MockOptions mockOptions = MockOptions.getDefault();
        launcher.generator = new Generator(mockOptions.rule);
        launcher.mockOptions = mockOptions;
        //
        launcher.parseClasses(TestService.class);
    }

    private void initByOptions(Context context, MockOptions options) {
        MockOptions actMockOptions = options == null ? MockOptions.getDefault() : options;
        generator = new Generator(actMockOptions.rule);
        mockOptions = actMockOptions;
        //导入数据
        MockUrlData.add(actMockOptions.mockData);
        MockUrlData.addFromAssets(context, actMockOptions.mockDataAssetsPath);
    }

    private void startMockService(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, MockService.class));
        } else {
            context.startService(new Intent(context, MockService.class));
        }
    }

    private void parseClasses(Class<?>... classes) {
        for (Class<?> cls : classes) {
            try {
                parse(cls);
            } catch (Exception e) {
                println("解析Service失败，以下为错误信息↓");
                e.printStackTrace();
            }
        }
    }

    private void parse(Class<?> cls) {
        //第一步：获取url及数据
        Method[] methods = cls.getMethods();
        for (Method m : methods) {
            println("====== 解析Method " + m.getName() + " start ======");
            //init
            isMethodStart = true;
            clsTb.clear();//清除map记录 重新尝试
            //类型本身一般没有什么意义 需要注意的是该类型中的泛型 以及ResponseBody的处理
            String actUrl = actUrl(m);
            println("url:" + actUrl);
            boolean containsKey = MockUrlData.contain(actUrl);
            if (containsKey) {
                println("该url已由其他mock数据占用，无需静态解析");
            } else {
                Object o = actType(m);
                println("data:" + (o == null ? "null" : GsonUtil.toJson(o)));
                MockUrlData.add(actUrl, o);
            }
            println("====== 解析Method " + m.getName() + " end ======");
            println("---------------分割线---------------");
        }
    }

    //分析静态url
    private String actUrl(Method m) {
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

    private String transString(String s) {
        return s == null || s.isEmpty() ? null : s;
    }

    private Object actType(Method m) {
        return parseType(m.getGenericReturnType());
    }

    /**
     * @return 返回一个拥有所有mock属性的对象，将其作为value
     */
    private Object parseType(Type type) {
        return handleType(type, null, null, true);
    }

    //难点：有时需要返回值，有时需要直接设置到Field中，如何区分？以及如何在递归中进行合适的逻辑处理？
    //解：每一层只需要处理自己与上一级的关系就好了
    private Object handleType(Type type, Object parent, Field parentField, boolean selfOrParent) {
        if (type instanceof ParameterizedType) {
            Type rawType1 = ((ParameterizedType) type).getRawType();
            if (rawType1 == Map.class) {//需要获取key value的类型再处理
                println("()->map带泛型，尝试分析创建" + type);
                Map<Object, Object> map = new HashMap<>();
                Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
                if (actualTypeArguments.length == 2) {
                    Object key = handleType(actualTypeArguments[0], parent, null, true);
                    Object value = handleType(actualTypeArguments[1], parent, null, true);
                    if (key != null && value != null) {
                        map.put(key, value);
                    }
                }
                return selfOrParent ? map : setParentField(parent, parentField, map);
            } else if (rawType1 == List.class) {//List<T> List<Bean<T>> 第一种情况如果parent为null则找不到泛型
                println("()->List带泛型，尝试分析创建" + type);
                List<Object> ls = new ArrayList<>();
                //只处理List的第一层泛型
                Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
                if (actualTypeArguments.length == 1) {
                    Object obj = handleType(actualTypeArguments[0], parent, null, true);
                    if (obj != null) {
                        ls.add(obj);
                    }
                }
                return selfOrParent ? ls : setParentField(parent, parentField, ls);
            } else if (rawType1 == Set.class) {//同上
                println("()->Set带泛型，尝试分析创建" + type);
                Set<Object> set = new HashSet<>();
                //只处理List的第一层泛型
                Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
                if (actualTypeArguments.length == 1) {
                    Object obj = handleType(actualTypeArguments[0], parent, null, true);
                    if (obj != null) {
                        set.add(obj);
                    }
                }
                return selfOrParent ? set : setParentField(parent, parentField, set);
            } else {
                Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
                if (actualTypeArguments.length == 1 && actualTypeArguments[0] == ResponseBody.class) {
                    println("()->Response不处理，可自行在mock数据文件中预设值");
                    return null;
                } else {
                    if (isMethodStart) {
                        isMethodStart = false;
                        Type type1 = actualTypeArguments[0];
                        println("()->等待" + type1 + "创建后返回");
                        return handleType(type1, null, null, true);//处理完毕后返回参数1
                    } else {
                        println("()->正在处理" + type);
                        Type rawType = ((ParameterizedType) type).getRawType();
                        if (rawType instanceof Class<?>) {//带泛型的cls final类型是不可能带泛型的
                            Class<?> rawClass = (Class<?>) rawType;
                            //插入泛型类型
                            if (actualTypeArguments.length >= 1) {
                                println("()->插入Map中->key:" + rawClass.getName() + " value:" + actualTypeArguments[0]);
                                clsTb.put(rawClass.getName(), actualTypeArguments[0]);
                            }
                            Object o = handleType(rawType, parent, parentField, true);
                            return selfOrParent ? o : setParentField(parent, parentField, o);
                        } else {
                            println("()->不可能存在的情况");
                            return null;
                        }
                    }
                }
            }
        } else if (type instanceof Class<?>) {//class类型
            Object obj = handleObjFromCls((Class<?>) type, parent, parentField);
            return selfOrParent ? obj : setParentField(parent, parentField, obj);
        } else if (type instanceof TypeVariable) {//类型变量 name:T bounds:Object
//            TypeVariable表示的是类型变量，它用来反映的是JVM编译该泛型前的信息，例如List<T>中的T就是类型变量，它在编译时需要被转换为一个具体的类型后才能正常使用。
//            该接口常用的方法有3个，分别是：
//            (1) Type[] getBounds()——获取类型变量的上边界，如果未明确声明上边界则默认为Object。例如Class<K extents Person>中K的上边界就是Person。
//            (2) D getGenericDeclaration()——获取声明该类型变量的原始类型，例如Test<K extents Person>中原始类型是Test。
//            (3) String getName()——获取在源码中定义的名字，上例中为K。
            //尝试从map中获取原始类型
            if (parent != null) {
                Type actType = clsTb.get(parent.getClass().getName());
                if (actType != null) {
                    println("()->尝试从clsTb中获取对象实际泛型类型" + actType);
                    Object obj = handleType(actType, parent, parentField, true);
                    return selfOrParent ? obj : setParentField(parent, parentField, obj);
                } else {
                    println("()->从clsTb中获取对象实际泛型类型失败" + type);
                }
            } else {
                println("()->parent为null，无法处理:" + type);
            }
            return null;
        } else if (type instanceof GenericArrayType) {
            //GenericArrayType表示的是数组类型且组成元素时ParameterizedType或TypeVariable，例如List<T>或T[]，该接口只有
            // Type getGenericComponentType()一个方法，它返回数组的组成元素类型。
            println("()->GenericArrayType类型" + type + "暂不处理");
            return null;
        } else if (type instanceof WildcardType) {
            //例如? extends Number 和 ? super Integer。
            //Wildcard接口有两个方法，分别是：
            //(1) Type[] getUpperBounds()——返回类型变量的上边界。
            //(2) Type[] getLowerBounds()——返回类型变量的下边界。
            println("()->WildcardType类型" + type + "暂不处理");
            return null;
        } else {
            println("()->暂不处理的类型:" + type);
            return null;
        }
    }

    private Object handleObjFromCls(Class<?> cls, Object parent, Field parentField) {
        Object finalObj = getFinalObj(cls, parentField);
        if (finalObj == null) {//可变对象
            return getClsObj(cls);
        } else {
            return finalObj;
        }
    }

    private Object getClsObj(Class<?> cls) {
        Object obj = newClassInstance(cls);
        if (obj != null) {
            Field[] fields = cls.getDeclaredFields();
            for (Field f : fields) {
                try {
                    f.setAccessible(true);
                    Object o = f.get(obj);
                    if (o == null) {//表示没有默认值 需要mock数据
                        Type genericType = f.getGenericType();
                        println("()->字段：" + f.getName() + " 类型:" + genericType + " start==>");
                        handleType(genericType, obj, f, false);
                        println("()->字段：" + f.getName() + " 类型:" + genericType + " end<==");
                    } else {
                        println("()->字段：" + f.getName() + "已有默认值，无需处理");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }

    private Object setParentField(Object parent, Field parentField, Object value) {
        if (parent == null || parentField == null) return null;
        try {
            parentField.setAccessible(true);
            parentField.set(parent, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return parent;
    }

    private Object newClassInstance(Class<?> cls) {
        try {//默认构造器创建
            Constructor<?> constructor = cls.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {//使用不安全的方式创建
            return ClassUtils.allocateInstance(cls);
        }
    }

    private Object getFinalObj(Class<?> cls, Field parentField) {
        if (parentField != null) {//如果有特殊注解 那就不走生成器
            Image image = parentField.getAnnotation(Image.class);
            if (image != null) {
                String defaultUrl = image.value();
                if (defaultUrl.isEmpty() && mockOptions != null) {
                    List<String> images = mockOptions.images;
                    List<String> actImages = images == null || images.size() == 0 ? Images.get() : images;
                    if (actImages.size() > 0) {
                        int index = new Random().nextInt(actImages.size());
                        return actImages.get(index);
                    }
                } else {
                    return defaultUrl;
                }
            }
        }
        return generator == null ? null : generator.get(cls);
    }

    private void println(String msg) {
        if (mockOptions != null && mockOptions.debug) {
            System.out.println(msg);
        }
    }

}
