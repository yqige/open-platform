package com.app.wxapi.utils.code;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.app.wxapi.utils.RedisUtils;
import com.app.wxapi.utils.StringUtils;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.SnsAccessToken;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;
import com.jfinal.weixin.sdk.api.SnsApi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;

public class ErrorUtils {
    static Log logger = Log.getLog("log_wx");
    public static void handleError(Controller controller) {
        HashMap hm = new HashMap();
        Enumeration<String> ls = controller.getAttrNames();
        while (ls.hasMoreElements()) {
            String ne = ls.nextElement();
            if(!ne.equals("CTX"))
            hm.put(ne, controller.getAttrForStr(ne));
        }
        controller.renderJson(hm);
    }

    public static String handleOpenid(Controller c, String key) {
        String code = c.getPara("code");
        logger.info("通过code换取网页授权access_token");
        logger.info("code:-->" + code);
        SnsAccessToken snsAccessToken = SnsAccessTokenApi.getSnsAccessToken(PropKit.get("appid"), PropKit.get("appSecret"), code);
        String token = snsAccessToken.getAccessToken();
        String openId = snsAccessToken.getOpenid();
        if (StringUtils.isBlank(token)) {
            String url = null;
            try {
                url = URLEncoder.encode(PropKit.get("serverUrl") + key, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            url = SnsAccessTokenApi.getAuthorizeURL(PropKit.get("appid"), url, false);
            c.redirect(url);
        } else {
            //拉取用户信息(需scope为 snsapi_userinfo)
            ApiResult apiResult = SnsApi.getUserInfo(token, openId);
            RedisUtils.intset(c.getSession().getId(), 604800, openId);
            JSONObject jsonObject = JSON.parseObject(apiResult.getJson());
            logger.info(jsonObject.toJSONString());
            c.setAttr("headimgurl", jsonObject.getString("headimgurl"));
            c.setAttr("nickName", jsonObject.getString("nickname"));
            c.setAttr("openId", openId);
        }
        return openId;
    }
}

