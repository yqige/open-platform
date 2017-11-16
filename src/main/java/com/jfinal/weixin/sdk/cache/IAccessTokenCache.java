package com.jfinal.weixin.sdk.cache;

public interface IAccessTokenCache {

    // 默认超时时间7200秒 35秒用于程序执行误差
    final int DEFAULT_TIME_OUT = 7200 - 35;

    <T> T get(String key);

    void set(String key, Object value);

    void remove(String key);

}
