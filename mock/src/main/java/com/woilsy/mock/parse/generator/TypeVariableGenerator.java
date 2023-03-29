package com.woilsy.mock.parse.generator;

import com.woilsy.mock.parse.MockOptionsAgent;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

public class TypeVariableGenerator extends AbsTypeGenerator {

    public TypeVariableGenerator(MockOptionsAgent mockOptionsAgent) {
        super(mockOptionsAgent);
    }

    @Override
    public Object generateType(Type type, Object parent, Field parentField, boolean selfOrParent) {
        /*
         * TypeVariable表示的是类型变量，它用来反映的是JVM编译该泛型前的信息，例如List<T>中的T就是类型变量，它在
         * 编译时需要被转换为一个具体的类型后才能正常使用。该接口常用的方法有3个，分别是：
         * (1) Type[] getBounds()——获取类型变量的上边界，如果未明确声明上边界则默认为Object。例如Class<K extents Person>中K的上边界就是Person。
         * (2) D getGenericDeclaration()——获取声明该类型变量的原始类型，例如Test<K extents Person>中原始类型是Test。
         * (3) String getName()——获取在源码中定义的名字，上例中为K。
         */
        if (parent != null) {
            String key = parent.getClass().getName();
            Type actType = getAndRemoveType(key);//尝试从map中获取原始类型
            if (actType != null) {
                logi("()->尝试从clsTb中获取对象实际泛型类型" + actType);
                Object obj = superGenerateType(actType, parent, parentField, true);
                return selfOrParent ? obj : setParentField(parent, parentField, obj);
            } else {
                logi("()->从clsTb中获取对象实际泛型类型失败" + type);
            }
        } else {
            logi("()->parent为null，无法处理:" + type);
        }
        return null;
    }

    private Type getAndRemoveType(String name) {
        List<Type> types = getTypeListMap().get(name);
        if (types == null || types.isEmpty()) {
            return null;
        } else {//拿完就移除
            Type type = types.get(0);
            types.remove(0);
            return type;
        }
    }
}