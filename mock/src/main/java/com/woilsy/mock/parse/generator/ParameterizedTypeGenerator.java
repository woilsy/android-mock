package com.woilsy.mock.parse.generator;

import com.woilsy.mock.parse.MockOptionsAgent;
import com.woilsy.mock.utils.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import kotlinx.coroutines.flow.Flow;
import okhttp3.ResponseBody;

public class ParameterizedTypeGenerator extends AbsTypeGenerator {

    private final Random random = new Random();

    public ParameterizedTypeGenerator(MockOptionsAgent mockOptionsAgent) {
        super(mockOptionsAgent);
    }

    @Override
    public Object startGenerateType(Type type) {
        //清除关系
        getTypeListMap().clear();
        //处理参数
        Type type1 = getHandleType((ParameterizedType) type);
        logi("()->等待" + type1 + "创建后返回");
        return superGenerateType(type1, null, null);//处理完毕后返回参数
    }

    /**
     * 有时候需要直接返回， 比如 suspend fun method1(): List<String>
     * 有时候需要返回参数1，比如 fun method1(): Observable<List<String>>
     * 目前直接返回索引0，suspend fun 会以其他形式(WildcardTyp)进入逻辑处理中，此处不影响。
     */
    private Type getHandleType(ParameterizedType pType) {
        return pType.getActualTypeArguments()[0];
    }

    @Override
    public Object generateType(Type type, Field typeField, Object parent) {
        Type rawType1 = ((ParameterizedType) type).getRawType();
        if (rawType1 == Map.class) {//需要获取key value的类型再处理
            logi("()->map带泛型，尝试分析创建" + type);
            Map<Object, Object> map = new HashMap<>();
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            if (actualTypeArguments.length == 2) {
                Object key = superGenerateType(actualTypeArguments[0], null, null);
                Object value = superGenerateType(actualTypeArguments[1], null, null);
                if (key != null && value != null) {
                    map.put(key, value);
                }
            }
            return map;
        } else if (rawType1 == List.class) {//List<T> List<Bean<T>> 第一种情况如果parent为null则找不到泛型
            logi("()->List带泛型，尝试分析创建" + type);
            List<Object> ls = new ArrayList<>();
            handleCollection((ParameterizedType) type, ls, parent);
            return ls;
        } else if (rawType1 == Set.class) {//同上
            logi("()->Set带泛型，尝试分析创建" + type);
            Set<Object> set = new HashSet<>();
            handleCollection((ParameterizedType) type, set, parent);
            return set;
        } else {
            return handleOtherType(type, typeField, parent);
        }
    }

    /**
     * 处理HttpRsp<ResponseBody>、HttpRsp<A<B>>的情况
     */
    private Object handleOtherType(Type type, Field typeField, Object parent) {
        ParameterizedType pzt = (ParameterizedType) type;
        Type[] actualTypeArguments = pzt.getActualTypeArguments();
        if (actualTypeArguments.length >= 1) {
            if (actualTypeArguments[0] == ResponseBody.class) {
                logi("()->ResponseBody不处理，可自行通过DataSource预设值");
            } else {
                return doHandle(pzt, typeField, parent);
            }
        } else {
            loge("()->未曾预想的情况1");
        }
        return null;
    }

    private Object doHandle(ParameterizedType pzt, Field typeField, Object parent) {
        Type rawType = pzt.getRawType();
        try {
            if (rawType == Flow.class) {
                Type argument = pzt.getActualTypeArguments()[0];
                logi("()->是个Flow，将" + argument + "拿去处理");
                return superGenerateType(argument, typeField, parent);
            }
        } catch (NoClassDefFoundError ignore) {

        }
        //带泛型的cls final类型String类的是不可能带泛型的
        if (rawType instanceof Class<?>) {
            Class<?> rawClass = (Class<?>) rawType;
            //可能存在多个泛型 此处做记录
            for (Type t : pzt.getActualTypeArguments()) {
                putClassArgument(rawClass.getName(), t);
            }
            return superGenerateType(rawType, typeField, parent);
        } else {
            logi("()->未曾预想的情况");
        }
        return null;
    }

    /**
     * 将class中的泛型信息插入到map中
     */
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
    private void handleCollection(ParameterizedType type, Collection<Object> collection, Object parent) {
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
                LogUtil.i("()->" + type + "生成Child:" + actualTypeArguments[0] + "，其值为parent对应的泛型");
                Object one = superGenerateType(actualTypeArguments[0], null, parent);
                if (one != null) {
                    collection.add(one);
                    for (int i = 1; i < mockListSize; i++) {
                        Object o = superGenerateType(one.getClass(), null, null);
                        if (o != null) {
                            collection.add(o);
                        }
                    }
                }
            }
        }
    }
}
