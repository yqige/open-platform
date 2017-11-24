package com.app.wxapi.entity;

import com.alibaba.fastjson.JSONObject;
import com.app.wxapi.utils.StringUtils;
import com.jfinal.kit.LogKit;
import com.jfinal.weixin.sdk.utils.RetryUtils;

import java.io.Serializable;

public class ConfigJson implements RetryUtils.ResultCheck, Serializable {
    private String json;
    private Long expiredTime;
    public ConfigJson(String json) {
        this.json=json;
        LogKit.info("json:"+json);
        JSONObject jsonObject = JSONObject.parseObject(json);
        if(StringUtils.isBlank(jsonObject.getInteger("expires_in")+""))
            expiredTime = System.currentTimeMillis() + ((7000 - 5) * 1000);
        else if(jsonObject.get("authorization_info")!=null)
            expiredTime = System.currentTimeMillis() + ((jsonObject.getJSONObject("authorization_info").getInteger("expires_in") - 5) * 1000);
        else
            expiredTime = System.currentTimeMillis() + ((jsonObject.getInteger("expires_in") - 5) * 1000);
        LogKit.info("expiredTime:"+expiredTime);
    }

    @Override
    public boolean matching() {
        return isAvailable();
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
