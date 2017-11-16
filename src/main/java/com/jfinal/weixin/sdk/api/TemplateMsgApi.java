/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.jfinal.weixin.sdk.api;

import com.demo.wxapi.utils.MethodName;
import com.jfinal.kit.LogKit;
import com.jfinal.weixin.sdk.utils.HttpUtils;

/**
 * 模板消息 API
 * 文档地址：http://mp.weixin.qq.com/wiki/17/304c1885ea66dbedf7dc170d84999a9d.html
 */
public class TemplateMsgApi {

    private static String sendApiUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";
    private static String api_add_template_url = "https://api.weixin.qq.com/cgi-bin/template/api_add_template?access_token=";
    private static String get_all_private_template_url = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token=";
    private static String api_set_industry_url = "https://api.weixin.qq.com/cgi-bin/template/api_set_industry?access_token=";
    /**
     * 发送模板消息
     * @param jsonStr json字符串
     * @return {ApiResult}
     */
    public static ApiResult send(String jsonStr) {
        String jsonResult = HttpUtils.post(sendApiUrl + AccessTokenApi.getAccessToken().getAccessToken(), jsonStr);
        return new ApiResult(jsonResult);
    }
    @MethodName(name = "添加模版")
    public static ApiResult add(String jsonStr) {
        LogKit.info("请求信息："+jsonStr);
        String jsonResult = HttpUtils.post(api_add_template_url + AccessTokenApi.getAccessToken().getAccessToken(), jsonStr);
        return new ApiResult(jsonResult);
    }
    @MethodName(name = "获取模版")
    public static ApiResult get(String jsonStr) {
        LogKit.info("请求信息："+jsonStr);
        String jsonResult = HttpUtils.post(get_all_private_template_url + AccessTokenApi.getAccessToken().getAccessToken(), jsonStr);
        return new ApiResult(jsonResult);
    }
    @MethodName(name = "设置公众号行业")
    public static ApiResult set(String jsonStr) {
        LogKit.info("请求信息："+jsonStr);
        String jsonResult = HttpUtils.post(api_set_industry_url + AccessTokenApi.getAccessToken().getAccessToken(), jsonStr);
        return new ApiResult(jsonResult);
    }
}


