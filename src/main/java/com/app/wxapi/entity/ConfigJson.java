package com.app.wxapi.entity;

import com.jfinal.weixin.sdk.utils.RetryUtils;

import java.io.Serializable;

public class ConfigJson implements RetryUtils.ResultCheck, Serializable {
    private String json;
    private Long expiredTime;
    public ConfigJson(String json) {
        this.json=json;
        expiredTime = System.currentTimeMillis() + ((7000 - 5) * 1000);
    }

    @Override
    public boolean matching() {
        return false;
    }

    @Override
    public String getJson() {
        return json;
    }
    public boolean isAvailable() {
        if (expiredTime < System.currentTimeMillis())
            return false;
        return true;
    }
}
