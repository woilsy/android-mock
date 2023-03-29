package com.woilsy.mock.parse.generator;

import com.woilsy.mock.annotations.*;
import com.woilsy.mock.generate.Rule;
import com.woilsy.mock.parse.MockOptionsAgent;
import com.woilsy.mock.utils.ClassUtils;
import com.woilsy.mock.utils.GsonUtil;
import com.woilsy.mock.utils.MockRangeUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class ClassGenerator extends AbsTypeGenerator {

    public ClassGenerator(MockOptionsAgent mockOptionsAgent) {
        super(mockOptionsAgent);
    }

    @Override
    public Object generateType(Type type, Object parent, Field parentField, boolean selfOrParent) {
        Object obj = handleObjFromCls((Class<?>) type, parentField);
        return selfOrParent ? obj : setParentField(parent, parentField, obj);
    }

    /**
     * 处理对象类型，先尝试直接获取，如果不行再对其进行先字段分解再生成。
     *
     * @param parentField cls在父类中的字段对象，通过这个字段可以读取其注解以及创建后设置到该字段中。
     */
    private Object handleObjFromCls(Class<?> cls, Field parentField) {
        Object finalObj = getFinalObj(cls, parentField);
        if (finalObj == null) {//可变对象
            logi("()->class类型，解析class后返回");
            return getClsObj(cls);
        } else {
            logi("()->final类型，直接返回:" + finalObj);
            return finalObj;
        }
    }

    /**
     * 获取最终对象，比如String，Integer等类型的数据通过规则进行生成。
     */
    private Object getFinalObj(Class<?> cls, Field parentField) {
        //纯粹的尝试获取Final字段 需要先判定是否为可解析类型。。。可解析时 直接返回 不可解析时返回null
        if (getMockOptions().getRules() == null) return null;
        try {
            Object data = getMockFieldData(cls, parentField);
            if (data != null) {//带mock注解，且解析出数据
                return data;
            }
        } catch (Exception e) {
            loge("()->生成final字段" + cls + "失败");
            e.printStackTrace();
        }
        return getImpl(cls, parentField);
    }

    /**
     * 通过class获取带有mock数据字段的新的示例
     */
    private Object getClsObj(Class<?> cls) {
        Object obj = newClassInstance(cls);
        if (obj != null) {
            Field[] fields = cls.getDeclaredFields();
            for (Field f : fields) {
                try {
                    Type genericType = f.getGenericType();
                    if (genericType instanceof ParameterizedType) {
                        Type rawType = ((ParameterizedType) genericType).getRawType();
                        if (rawType instanceof Class) {
                            handleMockFieldType(obj, (Class<?>) rawType, f);
                        } else {
                            handleFieldType(f, obj);
                        }
                    } else if (genericType instanceof Class) {
                        handleMockFieldType(obj, (Class<?>) genericType, f);
                    } else {
                        handleFieldType(f, obj);
                    }
                } catch (Exception e) {
                    loge("()->处理字段时出错");
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }

    /**
     * 通过class生成一个对象
     */
    private Object newClassInstance(Class<?> cls) {
        try {//默认构造器创建
            Constructor<?>[] constructors = cls.getDeclaredConstructors();
            for (Constructor<?> constructor : constructors) {
                int len = constructor.getParameterTypes().length;
                if (len == 0) {
                    constructor.setAccessible(true);
                    return constructor.newInstance();
                }
            }
        } catch (Exception e) {//使用不安全的方式创建
            loge("()->构造器创建失败，尝试使用Unsafe创建:");
        }
        return unsafeCreate(cls);
    }

    /**
     * 使用Gson UNSAFE方式直接操作内存创建对象
     */
    private Object unsafeCreate(Class<?> cls) {
        try {
            return ClassUtils.allocateInstance(cls);
        } catch (Exception e2) {
            loge("()->尝试使用Unsafe创建失败:" + e2.getMessage());
            return null;
        }
    }

    private void handleMockFieldType(Object obj, Class<?> cls, Field f) throws IllegalAccessException {
        Object fieldData = getMockFieldData(cls, f);
        if (fieldData == null) {
            logi("()->解析mock注解失败，使用默认方式");
            handleFieldType(f, obj);
        } else {
            logi("()->解析mock注解成功，直接赋值到字段中");
            setParentField(obj, f, fieldData);
        }
    }

    private void handleFieldType(Field f, Object obj) throws IllegalAccessException {
        Type genericType = f.getGenericType();
        f.setAccessible(true);
        Object o = f.get(obj);
        if (o == null) {
            logi("()->字段：" + f.getName() + " 类型:" + genericType + " start==>");
            superGenerateType(genericType, obj, f, false);
            logi("()->字段：" + f.getName() + " 类型:" + genericType + " end<==");
        } else {
            logi("()->字段：" + f.getName() + "已有默认值，无需处理");
        }
    }

    //只处理带Mock注解的情况，其他情况直接返回null表示没有通过该注解获取到内容
    private Object getMockFieldData(Class<?> cls, Field field) {
        if (field == null) return null;
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof Mock) {
                //如果data有值 尝试将其转换为对应的值 一般是基本类型从string转为xxx等，需要匹配cls
                String data = ((Mock) annotation).value();
                if (!data.isEmpty()) {
                    boolean isJsonObject = data.startsWith("{") && data.endsWith("}");
                    boolean isJsonArray = data.startsWith("[") && data.endsWith("]");
                    if (isJsonObject) {
                        logi("()->是个jsonObject类型，尝试解析 " + data);
                        Object jsonObject = GsonUtil.jsonToObj(data, cls);
                        if (jsonObject != null) {
                            return jsonObject;
                        }
                    } else if (isJsonArray) {
                        logi("()->是个jsonArray类型，尝试解析 " + data);
                        Object jsonArray = GsonUtil.jsonToObj(data, cls);
                        if (jsonArray != null) {
                            return jsonArray;
                        }
                    }
                    return ClassUtils.stringToClass(data, cls);
                }
            } else if (annotation instanceof MockBooleanRange) {
                return MockRangeUtil.booleanRange((MockBooleanRange) annotation);
            } else if (annotation instanceof MockCharRange) {
                return MockRangeUtil.charRange((MockCharRange) annotation);
            } else if (annotation instanceof MockDoubleRange) {
                return MockRangeUtil.doubleRange((MockDoubleRange) annotation);
            } else if (annotation instanceof MockFloatRange) {
                return MockRangeUtil.floatRange((MockFloatRange) annotation);
            } else if (annotation instanceof MockIntRange) {
                return MockRangeUtil.intRange((MockIntRange) annotation);
            } else if (annotation instanceof MockLongRange) {
                return MockRangeUtil.longRange((MockLongRange) annotation);
            } else if (annotation instanceof MockStringRange) {
                return MockRangeUtil.stringRange((MockStringRange) annotation);
            }
        }
        return getImpl(cls, field);
    }

    private Object getImpl(Class<?> cls, Field parentField) {
        List<Rule> rules = getMockOptions().getRules();
        for (Rule rule : rules) {
            Object impl = rule.getImpl(cls, parentField == null ? null : parentField.getName());
            if (impl != null) {
                return impl;
            }
        }
        return null;
    }
}
