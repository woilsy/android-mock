package com.woilsy.mock.parse;

import com.woilsy.mock.annotations.Mock;
import com.woilsy.mock.generate.Rule;
import com.woilsy.mock.options.MockOptions;
import com.woilsy.mock.utils.ClassUtils;
import com.woilsy.mock.utils.LogUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
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

/**
 * mock数据解析器
 */
public class MockParse {

    private final MockOptions mMockOptions;
    /**
     * key值存的是xxx.xxx.A，value为A<xxx>泛型中的xxx，通过clsTb可以获取到A的实际泛型
     */
    private final Map<String, List<Type>> clsTb = new HashMap<>();
    /**
     * 函数标记，用于区分入口
     */
    private boolean isMethodStart = false;

    public MockParse(MockOptions mMockOptions) {
        this.mMockOptions = mMockOptions;
    }

    public Object parseClass(Class<?> cls) {
        return parseType(cls);
    }

    public Object parseType(Type type) {
        return handleType(type, null, null, true);
    }

    //难点：有时需要返回值，有时需要直接设置到Field中，如何区分？以及如何在递归中进行合适的逻辑处理？
    //解：每一层只需要处理自己与上一级的关系就好了
    private Object handleType(Type type, Object parent, Field parentField, boolean selfOrParent) {
        if (type instanceof ParameterizedType) {
            Type rawType1 = ((ParameterizedType) type).getRawType();
            if (rawType1 == Map.class) {//需要获取key value的类型再处理
                LogUtil.i("()->map带泛型，尝试分析创建" + type);
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
                LogUtil.i("()->List带泛型，尝试分析创建" + type);
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
                LogUtil.i("()->Set带泛型，尝试分析创建" + type);
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
                    LogUtil.i("()->Response不处理，可自行在mock数据文件中预设值");
                    return null;
                } else {
                    if (isMethodStart) {
                        clsTb.clear();
                        isMethodStart = false;
                        Type type1 = actualTypeArguments[0];
                        LogUtil.i("()->等待" + type1 + "创建后返回");
                        return handleType(type1, null, null, true);//处理完毕后返回参数1
                    } else {
                        LogUtil.i("()->正在处理" + type);
                        Type rawType = ((ParameterizedType) type).getRawType();
                        if (rawType instanceof Class<?>) {//带泛型的cls final类型是不可能带泛型的
                            Class<?> rawClass = (Class<?>) rawType;
                            for (Type t : actualTypeArguments) {
                                putClassArgument(rawClass.getName(), t);
                            }
                            Object obj = handleType(rawType, parent, parentField, true);
                            return selfOrParent ? obj : setParentField(parent, parentField, obj);
                        } else {
                            LogUtil.i("()->不可能存在的情况");
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
                String key = parent.getClass().getName();
                Type actType = getAndRemoveType(key);
                if (actType != null) {
                    LogUtil.i("()->尝试从clsTb中获取对象实际泛型类型" + actType);
                    Object obj = handleType(actType, parent, parentField, true);
                    return selfOrParent ? obj : setParentField(parent, parentField, obj);
                } else {
                    LogUtil.i("()->从clsTb中获取对象实际泛型类型失败" + type);
                }
            } else {
                LogUtil.i("()->parent为null，无法处理:" + type);
            }
            return null;
        } else if (type instanceof GenericArrayType) {
            //GenericArrayType表示的是数组类型且组成元素时ParameterizedType或TypeVariable，例如List<T>或T[]，该接口只有
            // Type getGenericComponentType()一个方法，它返回数组的组成元素类型。
            LogUtil.i("()->GenericArrayType类型" + type + "暂不处理");
            return null;
        } else if (type instanceof WildcardType) {
            //例如? extends Number 和 ? super Integer。
            //Wildcard接口有两个方法，分别是：
            //(1) Type[] getUpperBounds()——返回类型变量的上边界。
            //(2) Type[] getLowerBounds()——返回类型变量的下边界。
            LogUtil.i("()->WildcardType类型" + type + "暂不处理");
            return null;
        } else {
            LogUtil.i("()->暂不处理的类型:" + type);
            return null;
        }
    }

    private void putClassArgument(String name, Type childType) {
        LogUtil.i("()->插入Map中->key->" + name + " value->" + childType);
        List<Type> typeList = clsTb.get(name);
        if (typeList == null) {
            List<Type> lts = new ArrayList<>();
            lts.add(childType);
            clsTb.put(name, lts);
        } else {
            typeList.add(childType);
        }
    }

    private Type getAndRemoveType(String name) {
        List<Type> types = clsTb.get(name);
        if (types == null || types.isEmpty()) {
            return null;
        } else {//拿完就移除
            Type type = types.get(0);
            types.remove(0);
            return type;
        }
    }

    private Object handleObjFromCls(Class<?> cls, Object parent, Field parentField) {
        Object finalObj = getFinalObj(cls, parentField);
        if (finalObj == null) {//可变对象
            LogUtil.i("()->class类型，解析class后返回");
            return getClsObj(cls);
        } else {
            LogUtil.i("()->final类型，直接返回:" + finalObj);
            return finalObj;
        }
    }


    //只处理带Mock注解的情况，其他情况直接返回null表示没有通过该注解获取到内容
    private Object getMockFieldData(Class<?> cls, Field field) {
        if (field == null) return null;
        Mock mock = field.getAnnotation(Mock.class);
        if (mock != null) {
            //如果data有值 尝试将其转换为对应的值 一般是基本类型从string转为xxx等，需要匹配cls
            com.woilsy.mock.type.Type type = mock.type();
            String data = mock.value();
            if (!data.isEmpty()) {
                return ClassUtils.stringToClass(data, cls);
            } else {//为空 采用默认值
                Rule rule = mMockOptions.getRule();
                switch (type) {
                    case AGE:
                        return rule.getAge();
                    case NAME:
                        return rule.getName();
                    case PHONE:
                        return rule.getPhone();
                    case ADDRESS:
                        return rule.getAddress();
                    case NICKNAME:
                        return rule.getNickName();
                    case IMAGE:
                        return rule.getImage();
                    default://NONE
                        break;
                }
            }
        }
        return null;
    }

    //纯粹的尝试获取Final字段 需要先判定是否为可解析类型。。。可解析时 直接返回 不可解析时返回null
    private Object getFinalObj(Class<?> cls, Field parentField) {
        if (mMockOptions == null || mMockOptions.getRule() == null) return null;
        try {
            Object data = getMockFieldData(cls, parentField);
            if (data != null) {//带mock注解，且解析出数据
                return data;
            } else {//如果没有Mock注解，那么该是什么就返回什么
                return mMockOptions.getRule().get(cls);
            }
        } catch (Exception e) {
            LogUtil.e("()->生成" + cls + "失败", e);
            return null;
        }
    }

    private Object newClassInstance(Class<?> cls) {
        try {//默认构造器创建
            Constructor<?> constructor = cls.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {//使用不安全的方式创建
            LogUtil.e("()->构造器创建失败，尝试使用Unsafe创建:" + e.getMessage());
            try {
                return ClassUtils.allocateInstance(cls);
            } catch (Exception e2) {
                LogUtil.e("()->尝试使用Unsafe创建失败:" + e.getMessage());
                return null;
            }
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
                    Type genericType = f.getGenericType();
                    if (o == null) {//表示没有默认值 需要mock数据
                        LogUtil.i("()->字段：" + f.getName() + " 类型:" + genericType + " start==>");
                        handleType(genericType, obj, f, false);
                        LogUtil.i("()->字段：" + f.getName() + " 类型:" + genericType + " end<==");
                    } else {
                        if (hasMockAnnotation(f) && genericType instanceof Class) {
                            LogUtil.i("()->字段：" + f.getName() + "有默认值，但有Mock标记，需要处理");
                            setParentField(obj, f, getMockFieldData((Class<?>) genericType, f));
                        } else {
                            LogUtil.i("()->字段：" + f.getName() + "已有默认值，无需处理");
                        }
                    }
                } catch (Exception e) {
                    LogUtil.e("()->处理字段时出错：", e);
                }
            }
        }
        return obj;
    }

    private boolean hasMockAnnotation(Field field) {
        return field.getAnnotation(Mock.class) != null;
    }

    private Object setParentField(Object parent, Field parentField, Object value) {
        if (parent == null || parentField == null) return null;
        try {
            parentField.setAccessible(true);
            parentField.set(parent, value);
        } catch (Exception e) {
            LogUtil.e("()->设置字段时出错:" + e.getMessage());
        }
        return parent;
    }
}