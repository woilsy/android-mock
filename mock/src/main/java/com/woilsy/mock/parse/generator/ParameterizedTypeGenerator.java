package com.woilsy.mock.parse.generator;

import com.woilsy.mock.parse.MockOptionsAgent;
import okhttp3.ResponseBody;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class ParameterizedTypeGenerator extends AbsTypeGenerator {

    private final Random random = new Random();

    public ParameterizedTypeGenerator(MockOptionsAgent mockOptionsAgent) {
        super(mockOptionsAgent);
    }

    @Override
    public Object startGenerateType(Type type) {
        //清除关系
        getTypeListMap().clear();
        //直接处理参数1
        Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
        Type type1 = actualTypeArguments[0];
        logi("()->等待" + type1 + "创建后返回");
        return superGenerateType(type1, null, null, true);//处理完毕后返回参数1
    }

    @Override
    public Object generateType(Type type, Object parent, Field parentField, boolean selfOrParent) {
        Type rawType1 = ((ParameterizedType) type).getRawType();
        if (rawType1 == Map.class) {//需要获取key value的类型再处理
            logi("()->map带泛型，尝试分析创建" + type);
            Map<Object, Object> map = new HashMap<>();
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            if (actualTypeArguments.length == 2) {
                Object key = superGenerateType(actualTypeArguments[0], parent, null, true);
                Object value = superGenerateType(actualTypeArguments[1], parent, null, true);
                if (key != null && value != null) {
                    map.put(key, value);
                }
            }
            return selfOrParent ? map : setParentField(parent, parentField, map);
        } else if (rawType1 == List.class) {//List<T> List<Bean<T>> 第一种情况如果parent为null则找不到泛型
            logi("()->List带泛型，尝试分析创建" + type);
            List<Object> ls = new ArrayList<>();
            handleCollection((ParameterizedType) type, parent, ls);
            return selfOrParent ? ls : setParentField(parent, parentField, ls);
        } else if (rawType1 == Set.class) {//同上
            logi("()->Set带泛型，尝试分析创建" + type);
            Set<Object> set = new HashSet<>();
            handleCollection((ParameterizedType) type, parent, set);
            return selfOrParent ? set : setParentField(parent, parentField, set);
        } else {
            ParameterizedType pzt = (ParameterizedType) type;
            Type[] actualTypeArguments = pzt.getActualTypeArguments();
            if (actualTypeArguments.length == 1 && actualTypeArguments[0] == ResponseBody.class) {
                logi("()->ResponseBody不处理，可自行通过DataSource预设值");
                return null;
            } else {
                logi("()->正在处理" + type);
                Type rawType = pzt.getRawType();
                if (rawType instanceof Class<?>) {//带泛型的cls final类型是不可能带泛型的
                    Class<?> rawClass = (Class<?>) rawType;
                    for (Type t : actualTypeArguments) {
                        putClassArgument(rawClass.getName(), t);
                    }
                    Object obj = superGenerateType(rawType, parent, parentField, true);
                    return selfOrParent ? obj : setParentField(parent, parentField, obj);
                } else {
                    logi("()->未曾预想的情况");
                    return null;
                }
            }
        }
    }

    private void putClassArgument(String name, Type childType) {
        logi("()->插入Map中->key->" + name + " value->" + childType);
        Map<String, List<Type>> typeListMap = getTypeListMap();
        List<Type> typeList = typeListMap.get(name);
        if (typeList == null) {
            List<Type> lts = new ArrayList<>();
            lts.add(childType);
            typeListMap.put(name, lts);
        } else {
            typeList.add(childType);
        }
    }

    /**
     * 处理Collection的数据问题
     */
    private void handleCollection(ParameterizedType type, Object parent, Collection<Object> collection) {
        Type[] actualTypeArguments = type.getActualTypeArguments();
        MockOptionsAgent mockOptions = getMockOptions();
        if (actualTypeArguments.length == 1) {
            boolean mockListCountRandom = mockOptions.isMockListCountRandom();
            int mockListSize;
            if (mockListCountRandom) {
                int minMockListSize = mockOptions.getMinMockListSize();
                int maxMockListSize = mockOptions.getMaxMockListSize();
                if (minMockListSize > maxMockListSize) {
                    minMockListSize = mockOptions.getMaxMockListSize();
                    maxMockListSize = mockOptions.getMinMockListSize();
                }
                int range = maxMockListSize - minMockListSize + 1;
                mockListSize = random.nextInt(range) + minMockListSize;//[0+min,max]
            } else {
                mockListSize = mockOptions.getMockListSize();
            }
            if (mockListSize != 0) {
                //由于getAndRemoveType会移除value，导致只能获取一次示例 所以在此基础上循环生成第一个就好
                Object one = superGenerateType(actualTypeArguments[0], parent, null, true);
                if (one != null) {
                    collection.add(one);
                    for (int i = 1; i < mockListSize; i++) {
                        Object o = superGenerateType(one.getClass(), null, null, true);
                        if (o != null) {
                            collection.add(o);
                        }
                    }
                }
            }
        }
    }
}
