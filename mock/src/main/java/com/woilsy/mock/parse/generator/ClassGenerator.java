package com.woilsy.mock.parse.generator;

import com.woilsy.mock.annotations.*;
import com.woilsy.mock.generate.Rule;
import com.woilsy.mock.parse.MockOptionsAgent;
import com.woilsy.mock.utils.ClassUtils;
import com.woilsy.mock.utils.GsonUtil;
import com.woilsy.mock.utils.MockRangeUtil;
import okhttp3.ResponseBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class ClassGenerator extends AbsTypeGenerator {

    public ClassGenerator(MockOptionsAgent mockOptionsAgent) {
        super(mockOptionsAgent);
    }

    @Override
    public Object generateType(Type type, Field typeField, Object parent) {
        return generatorObjFromCls((Class<?>) type, typeField);
    }

    /**
     * 处理对象类型，先尝试直接获取，如果不行再对其进行先字段分解再生成。
     *
     * @param parentField cls在父类中的字段对象，通过这个字段可以读取其注解以及创建后设置到该字段中。
     */
    private Object generatorObjFromCls(Class<?> cls, Field parentField) {
        Object finalObj = generatorFinalObj(cls, parentField);
        if (finalObj == null) {//尝试解析对象
            logi("()->class类型，解析class后返回" + cls);
            Object clsObj = generatorClsObj(cls);
            logi("()->class类型，已解析class后返回" + cls);
            return clsObj;
        } else {
            logi("()->final类型，直接返回:" + finalObj);
            return finalObj;
        }
    }

    /**
     * 获取最终对象，比如String，Integer等类型的数据通过规则进行生成。
     */
    private Object generatorFinalObj(Class<?> cls, Field parentField) {
        //纯粹的尝试获取Final字段 需要先判定是否为可解析类型。。。可解析时 直接返回 不可解析时返回null
        if (getMockOptions().getRules() == null) return null;
        try {
            if (parentField != null) {
                Object data = getMockFieldData(cls, parentField.getAnnotations());
                if (data != null) {//带mock注解，且解析出数据
                    return data;
                }
            }
        } catch (Exception e) {
            loge("()->生成final字段" + cls + "失败");
            e.printStackTrace();
        }
        return getImpl(cls, parentField);
    }

    /**
     * 通过class获取带有mock数据字段的新的实例
     */
    private Object generatorClsObj(Class<?> cls) {
        if (!checkClass(cls)) {
            loge("()->" + cls.getName() + "不支持创建，直接返回");
            return null;
        }
        Object obj = ClassUtils.newClassInstance(cls);
        if (obj != null) {
            Field[] fields = cls.getDeclaredFields();
            for (Field f : fields) {
                try {
                    Type genericType = f.getGenericType();
                    if (genericType instanceof ParameterizedType) {
                        Type rawType = ((ParameterizedType) genericType).getRawType();
                        handleField(obj, rawType, f);
                    } else {
                        handleField(obj, genericType, f);
                    }
                } catch (Exception e) {
                    loge("()->处理字段时出错");
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }

    private boolean checkClass(Class<?> cls) {
        return cls != ResponseBody.class;
    }

    private void handleField(Object obj, Type type, Field f) throws IllegalAccessException {
        if (type instanceof Class) {
            if (checkField(f)) {
                Object fieldData = getMockFieldData((Class<?>) type, f.getAnnotations());
                if (fieldData == null) {
                    logi("()->" + f.getName() + "没有mock注解数据，使用默认方式");
                    handleFieldType(f, obj);
                } else {
                    logi("()->解析mock注解成功，直接赋值到字段中" + f.getName());
                    setParentField(obj, f, fieldData);
                }
            }
        } else {
            handleFieldType(f, obj);
        }
    }

    private boolean checkField(Field field) {
        MockIgnore annotation = field.getAnnotation(MockIgnore.class);
        if (annotation != null) {
            logi("()->字段" + field.getName() + "被标记为排除");
            return false;
        } else {
            return true;
        }
    }

    private void handleFieldType(Field field, Object parent) throws IllegalAccessException {
        Type genericType = field.getGenericType();
        field.setAccessible(true);
        Object o = field.get(parent);
        if (o == null) {
            logi("()->字段：" + field.getName() + " 类型:" + genericType + " start==>");
            Object ooo = superGenerateType(genericType, field, parent);
            setParentField(parent, field, ooo);
            logi("()->字段：" + field.getName() + " 类型:" + genericType + " end<==");
        } else {
            logi("()->字段：" + field.getName() + "已有默认值，无需处理");
        }
    }

    /**
     * 处理Mock系列注解
     *
     * @return 其他情况直接返回null表示没有通过该注解获取到内容
     */
    private Object getMockFieldData(Class<?> cls, Annotation[] annotations) {
        if (annotations == null) return null;
        for (Annotation annotation : annotations) {
            if (annotation instanceof Mock) {
                //如果data有值 尝试将其转换为对应的值 一般是基本类型从string转为xxx等，需要匹配cls
                String data = ((Mock) annotation).value();
                if (!data.isEmpty()) {
                    boolean isJsonObject = data.startsWith("{") && data.endsWith("}");
                    boolean isJsonArray = data.startsWith("[") && data.endsWith("]");
                    if (isJsonArray || isJsonObject) {
                        String typeString = isJsonArray ? "JsonArray类型" : "JsonObject类型";
                        logi("()->" + typeString + "，尝试解析 " + data);
                        Object jsonObject = GsonUtil.jsonToObj(data, cls);
                        if (jsonObject != null) {
                            return jsonObject;
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
            } else if (annotation instanceof MockClass) {
                Class<?> value = ((MockClass) annotation).value();
                if (cls == Object.class) {
                    return ClassUtils.newClassInstance(value);
                }
            }
        }
        return null;
    }

    private Object getImpl(Class<?> cls, Field typeField) {
        List<Rule> rules = getMockOptions().getRules();
        for (Rule rule : rules) {
            Object impl = rule.getImpl(cls, typeField == null ? null : typeField.getName());
            if (impl != null) {
                return impl;
            }
        }
        return null;
    }
}
