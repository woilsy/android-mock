package com.woilsy.mock.generate;

import android.util.Log;

import com.woilsy.mock.entity.KeywordGroup;
import com.woilsy.mock.utils.ArrayUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 匹配规则，根据提前插入的数据进行返回
 */
public class MatchRule implements Rule {

    private static final String TAG = "DictionaryRule";

    private final Random random = new Random();

    protected final Map<String, KeywordGroup> KEYWORD_MAP = new HashMap<>();

    public static final String KEY_IMAGE = "image";

    public static final String KEY_AVATAR = "avatar";

    public static final String KEY_ADDRESS = "address";

    public static final String KEY_NAME = "name";

    public static final String KEY_AGE = "age";

    public MatchRule() {
        addKeyGroups();
    }

    protected void addKeyGroups() {
        KEYWORD_MAP.put(KEY_IMAGE, new KeywordGroup() {
            @Override
            public boolean match(String name) {
                return contain(name, "image", "photo", "cover", "img");
            }

            @Override
            public Object getValue() {
                return randomString(
                        "https://img1.baidu.com/it/u=2385973243,4274081283&fm=253&fmt=auto&app=120&f=JPEG?w=889&h=500",
                        "https://img0.baidu.com/it/u=140845661,3789939106&fm=253&fmt=auto&app=120&f=JPEG?w=1200&h=675",
                        "https://img0.baidu.com/it/u=2204761105,2366024135&fm=253&fmt=auto&app=120&f=JPEG?w=889&h=500",
                        "https://img1.baidu.com/it/u=3042160575,2573878710&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500",
                        "https://img1.baidu.com/it/u=3079431989,3499240801&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=729",
                        "https://img2.baidu.com/it/u=1595173768,2923508947&fm=253&fmt=auto&app=120&f=JPEG?w=1422&h=800",
                        "https://img1.baidu.com/it/u=2241334221,3271234001&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500",
                        "https://img0.baidu.com/it/u=1710998115,3226552578&fm=253&fmt=auto&app=138&f=JPG?w=500&h=281",
                        "https://img0.baidu.com/it/u=53228683,224729453&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500",
                        "https://img0.baidu.com/it/u=3612023169,2712561776&fm=253&fmt=auto&app=120&f=JPEG?w=889&h=500"
                );
            }
        });
        KEYWORD_MAP.put(KEY_AGE, new KeywordGroup() {
            @Override
            public boolean match(String name) {
                return contain(name, "age", "nianling", "nianLing", "Age");
            }

            @Override
            public Object getValue() {
                return random.nextInt(40) + 1;
            }
        });
        KEYWORD_MAP.put(KEY_ADDRESS, new KeywordGroup() {
            @Override
            public boolean match(String name) {
                return contain(name, "address", "adress", "dizhi", "location");
            }

            @Override
            public Object getValue() {
                return randomString("重庆市南岸区江南大道8号", "北京市朝阳区建国路93号院", "上海市闵行区浦江镇永跃路360号");
            }
        });
        KEYWORD_MAP.put(KEY_NAME, new KeywordGroup() {
            @Override
            public boolean match(String name) {
                return contain(name, "name", "nickName", "nickname", "userName", "mingzi", "xingming", "Name");
            }

            @Override
            public Object getValue() {
                return "小王" + random.nextInt(1000);
            }
        });

        KEYWORD_MAP.put(KEY_AVATAR, new KeywordGroup() {
            @Override
            public boolean match(String name) {
                return contain(name, "avatar", "touxiang", "touXiang", "userAvatar");
            }

            @Override
            public Object getValue() {
                return randomString(
                        "https://img0.baidu.com/it/u=1886948907,3561179897&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500",
                        "https://img0.baidu.com/it/u=2345040374,2305350496&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500",
                        "https://img0.baidu.com/it/u=2108312897,3953735456&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500",
                        "https://img0.baidu.com/it/u=4118684170,3710675978&fm=253&fmt=auto&app=138&f=JPEG?w=510&h=500",
                        "https://img0.baidu.com/it/u=1125089834,1274132805&fm=253&fmt=auto&app=120&f=JPEG?w=800&h=800",
                        "https://img0.baidu.com/it/u=4188580931,2142872269&fm=253&fmt=auto&app=120&f=JPEG?w=800&h=800",
                        "https://img0.baidu.com/it/u=1237356756,3545004668&fm=253&fmt=auto&app=138&f=JPEG?w=400&h=400"
                );
            }
        });
    }

    protected boolean contain(String key, String... keys) {
        return ArrayUtil.contain(key, keys);
    }

    private String randomString(String... items) {
        if (items.length == 0) return null;
        return items[random.nextInt(items.length)];
    }

    protected KeywordGroup findGroup(String key) {
        for (KeywordGroup value : KEYWORD_MAP.values()) {
            if (value.match(key)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public Object getImpl(Class<?> cls, String name) {
        if (name == null || name.isEmpty()) return null;
        KeywordGroup group = findGroup(name);
        if (group != null) {
            Object value = group.getValue();
            if (value.getClass() == cls) {
                return value;
            } else {
                Log.e(TAG, "getImpl: 类型不匹配，无法返回该数据" + value);
            }
        }
        return null;
    }

}
