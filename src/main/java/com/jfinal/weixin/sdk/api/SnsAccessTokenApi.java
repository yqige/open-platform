/**
 * Copyright (c) 2011-2015, Unas 小强哥 (unas@qq.com).
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.jfinal.weixin.sdk.api;

import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.kit.ParaMap;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.jfinal.weixin.sdk.utils.RetryUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 网页授权获取 access_token API
 */
public class SnsAccessTokenApi {
    private static String url = "https://api.weixin.qq.com/sns/oauth2/access_token?grant_type=authorization_code";
    private static String authorize_uri = "https://open.weixin.qq.com/connect/oauth2/authorize";
    private static String qrconnect_url = "https://open.weixin.qq.com/connect/qrconnect";
    private static String url_plat = "https://api.weixin.qq.com/sns/oauth2/component/access_token?appid=APPID&code=CODE&grant_type=authorization_code&component_appid=COMPONENT_APPID&component_access_token=COMPONENT_ACCESS_TOKEN";
    private static String authorize_uri_plat = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE&component_appid=COMPONENT_APPID#wechat_redirect";
    /**
     * 生成Authorize链接
     *
     * @param appId        应用id
     * @param redirect_uri 回跳地址
     * @param snsapiBase   snsapi_base（不弹出授权页面，只能拿到用户openid）snsapi_userinfo（弹出授权页面，这个可以通过 openid 拿到昵称、性别、所在地）
     * @return url
     */
    public static String getAuthorizeURL(String appId, String redirect_uri, boolean snsapiBase) {
        return getAuthorizeURL(appId, redirect_uri, null, snsapiBase);
    }

    /**
     * 生成Authorize链接
     *
     * @param appId       应用id
     * @param redirectUri 回跳地址
     * @param state       重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节
     * @param snsapiBase  snsapi_base（不弹出授权页面，只能拿到用户openid）snsapi_userinfo（弹出授权页面，这个可以通过 openid 拿到昵称、性别、所在地）
     * @return url
     */
    public static String getAuthorizeURL(String appId, String redirectUri, String state, boolean snsapiBase) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", appId);
        params.put("response_type", "code");
        params.put("redirect_uri", redirectUri);
        // snsapi_base（不弹出授权页面，只能拿到用户openid）
        // snsapi_userinfo（弹出授权页面，这个可以通过 openid 拿到昵称、性别、所在地）
        if (snsapiBase) {
            params.put("scope", "snsapi_base");
        } else {
            params.put("scope", "snsapi_userinfo");
        }
        if (StrKit.isBlank(state)) {
            params.put("state", "wx#wechat_redirect");
        } else {
            params.put("state", state.concat("#wechat_redirect"));
        }
        String para = PaymentKit.packageSign(params, false);
        return authorize_uri + "?" + para;
    }


    /**
     * 生成网页二维码授权链接
     *
     * @param appId        应用id
     * @param redirect_uri 回跳地址
     * @return url
     */
    public static String getQrConnectURL(String appId, String redirect_uri) {
        return getQrConnectURL(appId, redirect_uri, null);
    }

    /**
     * 生成网页二维码授权链接
     *
     * @param appId        应用id
     * @param redirect_uri 回跳地址
     * @param state        重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节
     * @return url
     */
    public static String getQrConnectURL(String appId, String redirect_uri, String state) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", appId);
        params.put("response_type", "code");
        params.put("redirect_uri", redirect_uri);
        params.put("scope", "snsapi_login");
        if (StrKit.isBlank(state)) {
            params.put("state", "wx#wechat_redirect");
        } else {
            params.put("state", state.concat("#wechat_redirect"));
        }
        String para = PaymentKit.packageSign(params, false);
        return qrconnect_url + "?" + para;
    }

    /**
     * 通过code获取access_token
     *
     * @param code   第一步获取的code参数
     * @param appId  应用唯一标识
     * @param secret 应用密钥AppSecret
     * @return SnsAccessToken
     */
    public static SnsAccessToken getSnsAccessToken(String appId, String secret, String code) {
        final Map<String, String> queryParas = ParaMap.create("appid", appId).put("secret", secret).put("code", code).getData();
        return RetryUtils.retryOnException(3, ()->new SnsAccessToken(HttpUtils.get(url, queryParas)));
    }

    /**
     * 通过code获取access_token
     * @param appId
     * @param code
     * @param appID
     * @param componentAccessToken
     * @return
     */
    public static SnsAccessToken getSnsAccessToken(String appId, String code, String appID, String componentAccessToken) {
        return RetryUtils.retryOnException(3, ()->new SnsAccessToken(HttpUtils.get(url_plat.replaceAll("APPID",appId)
                .replaceAll("CODE",code).replaceAll("COMPONENT_APPID",appID)
                .replaceAll("COMPONENT_ACCESS_TOKEN",componentAccessToken))));
    }

    /**
     * 生成Authorize链接
     * @param appId
     * @param snsapiBase
     * @param appID
     * @param url
     * @return
     */
    public static String getAuthorizeURL(String appId, boolean snsapiBase, String appID, String url) {
        if (snsapiBase) {
            authorize_uri_plat = authorize_uri_plat.replaceAll("SCOPE", "snsapi_base");
        } else {
            authorize_uri_plat = authorize_uri_plat.replaceAll("SCOPE", "snsapi_userinfo");
        }
        return authorize_uri_plat.replaceAll("APPID",appId).replaceAll("COMPONENT_APPID",appID).replaceAll("REDIRECT_URI",url);
    }
}
