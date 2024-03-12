package com.woilsy.mock.parse;

import com.woilsy.mock.options.MockOptions;
import com.woilsy.mock.parse.generator.TypeGenerator;
import com.woilsy.mock.parse.generator.TypeParseChooser;

import java.lang.reflect.Type;

/**
 * mock数据解析器，通过传递Type即可生成实体类
 */
public class MockParse implements TypeParser {

    private final MockOptions mMockOptions;

    public MockParse(MockOptions mMockOptions) {
        this.mMockOptions = mMockOptions;
    }

    public Object parseClass(Class<?> cls) {
        return parseType(cls);
    }

    @Override
    public Object parseType(Type type) {
        return handleType(type);
    }

    private Object handleType(Type type) {
        TypeGenerator typeGenerator = TypeParseChooser.findType(type, new RealMockOptionsAgent(mMockOptions));
        return typeGenerator == null ? null : typeGenerator.startGenerateType(type);
    }
}
