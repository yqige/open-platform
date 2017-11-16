package com.app.wxapi.utils;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;

/**
 * Created by ruieliu on 2017/6/30.
 */
public class RedisUtils {
    public static void ca() {
        Cache ca = Redis.use("openid");
    }

    public static void set(String key, Object value) {
        Cache cache = Redis.use("openid");
        cache.set(key, value);
    }

    public static <T> Object get(String key) {
        Cache cache = Redis.use("openid");
        return cache.get(key);
    }

    public static void intset(String key, int i, String value) {
        Cache cache = Redis.use("openid");
        cache.setex(key, i, value);
    }

}
