package com.woilsy.mock.parse.generator;

import com.woilsy.mock.Mocker;
import com.woilsy.mock.parse.MockOptionsAgent;
import com.woilsy.mock.utils.LogUtil;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

abstract class AbsTypeGenerator implements TypeGenerator {

    private final MockOptionsAgent mockOptionsAgent;

    public AbsTypeGenerator(MockOptionsAgent mockOptions) {
        this.mockOptionsAgent = mockOptions;
    }

    @NotNull
    public MockOptionsAgent getMockOptions() {
        return mockOptionsAgent;
    }

    protected void setParentField(Object parent, Field parentField, Object value) {
        if (parent == null || parentField == null) return;
        try {
            parentField.setAccessible(true);
            parentField.set(parent, value);
        } catch (Exception e) {
            loge("()->设置字段时出错:" + e.getMessage());
        }
    }

    protected Object superGenerateType(Type type, Field typeField,Object parent) {
        TypeGenerator typeGenerator = TypeParseChooser.findType(type, mockOptionsAgent);
        return typeGenerator == null ? null : typeGenerator.generateType(type, typeField,parent);
    }

    @NotNull
    protected Map<String, List<Type>> getTypeListMap() {
        return TypeParseChooser.typeListMap;
    }

    protected void logi(String msg) {
        if (Mocker.getMockOption().isShowParseLog()) {
            LogUtil.i(msg);
        }
    }

    protected void loge(String msg) {
        if (Mocker.getMockOption().isShowParseLog()) {
            LogUtil.e(msg);
        }
    }

}