package com.woilsy.mock.parse.generator;

import com.woilsy.mock.parse.MockOptionsAgent;
import com.woilsy.mock.utils.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;


public class WildcardTypeGenerator extends AbsTypeGenerator {

    public WildcardTypeGenerator(MockOptionsAgent mockOptions) {
        super(mockOptions);
    }

    @Override
    public Object generateType(Type type, Field typeField, Object parent) {
        /*
         * 例如? extends Number 和 ? super Integer。
         * Wildcard接口有两个方法，分别是：
         * (1) Type[] getUpperBounds()——返回类型变量的上边界。
         * (2) Type[] getLowerBounds()——返回类型变量的下边界。
         */
        WildcardType wt = (WildcardType) type;
        Type childType = findValidType(wt);
        LogUtil.i("()->WildcardType的类型" + childType);
        return childType == null ? null : superGenerateType(childType, typeField, parent);
    }

    /**
     * 查找一个有用的类型
     */
    private Type findValidType(WildcardType wt) {
        Type[] lowerBounds = wt.getLowerBounds();
        Type[] upperBounds = wt.getUpperBounds();
        Type lowerType = null;
        int invalidCount = 0;
        for (Type lowerBound : lowerBounds) {
            if (lowerBound == Object.class) {
                invalidCount++;
            } else {
                lowerType = lowerBound;
            }
        }
        //如果上方不满足条件
        if (lowerType == null || invalidCount > 0) {
            invalidCount = 0;
            Type upperType = null;
            for (Type upperBound : upperBounds) {
                if (upperBound == Object.class) {
                    invalidCount++;
                } else {
                    upperType = upperBound;
                }
            }
            if (invalidCount == 0 && upperType != null) {
                return upperType;
            } else {
                return lowerType == null ? upperType : lowerType;
            }
        } else {
            return lowerType;
        }
    }
}
