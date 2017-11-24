package com.app.wxapi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.app.wxapi.entity.ConfigJson;
import com.app.wxapi.utils.StringUtils;
import com.app.wxapi.utils.threadPool.ThreadPoolKit;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.*;
import com.jfinal.weixin.sdk.cache.IAccessTokenCache;
import com.jfinal.weixin.sdk.kit.ParaMap;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.jfinal.weixin.sdk.utils.RetryUtils;
import com.jfinal.weixin.sdk.utils.XmlHelper;
import com.platform.mvc.base.BaseService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 *用于获取平台权限相关凭据
 */
public class PlatformService extends BaseService{
    static IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();
    static String COMPONENT_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/component/api_component_token";
    static String PRE_AUTH_CODE = "https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=xxxx";
    static String GET_AUTHORIZER_TOKEN_WITH_CODE = "https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token=xxxx";
    static String REFRESH_AUTHORIZER_TOKEN = "https://api.weixin.qq.com/cgi-bin/component/api_authorizer_token?component_access_token=xxxx";
    static String GET_AUTHORIZER_INFO = "https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_info?component_access_token=xxxx";
    static String GET_AUTHORIZER_OPTION = "https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_option?component_access_token=xxxx";
    static String SET_AUTHORIZER_OPTION = "https://api.weixin.qq.com/cgi-bin/component/api_set_authorizer_option?component_access_token=xxxx";
    static String GET_AUTHORIZER_JSTICKET = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=xxxx&type=jsapi";
    static String GET_AUTHORIZER_MENU = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=xxxx";
    static String SET_AUTHORIZER_MENU = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=xxxx";
    static String CLEAR_AUTHORIZER_QUOTA = "https://api.weixin.qq.com/cgi-bin/clear_quota?access_token=xxxx";
    static String OAUTH2_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/component/access_token?appid=APPID&code=CODE&grant_type=authorization_code&component_appid=xxxx&component_access_token=xxxx";
    static String SNS_USER_INFO = "https://api.weixin.qq.com/sns/userinfo?access_token=xxxx&openid=xxxx&lang=zh_CN";
    static String ADD_AUTHORIZER_TEMPLATE = "https://api.weixin.qq.com/cgi-bin/template/api_add_template?access_token=xxxx";
    static String GET_AUTHORIZER_TEMPLATE_LIST = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token=xxxx";
    static String SEND_AUTHORIZER_TEMPLATE = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=xxxx";
    static String SEND_AUTHORIZER_MESSAGE = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=xxxx";
    static String GENERATE_TEMP_QRCODE = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=xxxx";
    static Log log = Log.getLog("log_wx");
    public void dealEvent(String inMsgXml) {
        XmlHelper xmlHelper = XmlHelper.of(inMsgXml);
        String infoType = xmlHelper.getString("//InfoType");
        switch (infoType){
            case "component_verify_ticket":
                String verifyTicket = xmlHelper.getString("//ComponentVerifyTicket");
                Number createTime = xmlHelper.getNumber("//CreateTime");
                dealTicket(verifyTicket, createTime.longValue());
                break;

            case "unauthorized":
                log.info("取消授权事件");
                String authorizerAppid = xmlHelper.getString("//AuthorizerAppid");
                dealUnauthorization(authorizerAppid);
                break;

            case "authorized":
                log.info("公众号授权事件");
                authorizerAppid = xmlHelper.getString("//AuthorizerAppid");
                String authorizationCode = xmlHelper.getString("//AuthorizationCode");
                dealAuthorization(authorizerAppid, authorizationCode);
                break;

            case "updateauthorized":
                log.info("公众号授权更新事件");
                authorizerAppid = xmlHelper.getString("//AuthorizerAppid");
                authorizationCode = xmlHelper.getString("//AuthorizationCode");
                dealUpdateAuthorization(authorizerAppid, authorizationCode);
                break;
        }

    }

    private void dealUpdateAuthorization(String authorizerAppid, String authorizationCode) {
        JSONObject authorization_info = getAuthorizationInfo(authorizerAppid, authorizationCode);
        Db.use("platform").update("update auth_info set status=0,verify_ticket=? where app_id= ?",authorization_info.getString("authorizer_refresh_token"),authorizerAppid);
    }

    private void dealAuthorization(String authorizerAppid, String authorizationCode) {
        //获取authorizer_refresh_token
        JSONObject authorization_info = getAuthorizationInfo(authorizerAppid, authorizationCode);
        //保存接口调用凭据刷新令牌：authorizer_refresh_token
        Record r = Db.use("platform").findFirst("select * from auth_info where app_id = ?",authorizerAppid);
        if(r==null)
            Db.use("platform").update("insert into auth_info (app_id,verify_ticket)values( ?, ?)",authorizerAppid,authorization_info.getString("authorizer_refresh_token"));
        else
            Db.use("platform").update("update auth_info set status=0,verify_ticket=? where app_id= ?",authorizerAppid,authorization_info.getString("authorizer_refresh_token"));
        try {

        }catch (Exception e){

        }
        ThreadPoolKit.execute(()->{
            //处理公众号行业、模版等等
            dealAppAllInfo(authorizerAppid);
        });
    }

    private void dealAppAllInfo(String authorizerAppid) {
        log.info("开始处理//处理公众号行业、模版等等");
        //设置行业

    }

    private void dealUnauthorization(String authorizerAppid) {
        Db.use("platform").update("update auth_info set status=1 where app_id= ?",authorizerAppid);
    }

    private void dealTicket(String verifyTicket, long l) {
        Db.use("platform").update("update auth_info set verify_ticket=? , exp_time=? where app_id='platform'",verifyTicket,l-3000);
    }
    JSONObject getAuthorizationInfo(String authorizerAppid, String authorizationCode){
        String key = authorizerAppid + ':' + "auth";
        final ParaMap pm = ParaMap.create("component_appid", PropKit.get("appID")).put("authorization_code", authorizationCode);
        ConfigJson configJson ;
        // 最多三次请求
        configJson = RetryUtils.retryOnException(3,()->
                new ConfigJson(HttpUtils.post(GET_AUTHORIZER_TOKEN_WITH_CODE.replaceAll("xxxx",getComponentAccessToken()), JsonKit.toJson(pm.getData())))
        );
        JSONObject jsonObject = JSONObject.parseObject(configJson.getJson());
        JSONObject authorization_info = jsonObject.getJSONObject("authorization_info");
        JSONObject jsonStr = new JSONObject();
        jsonStr.put("access_token",authorization_info.getString("authorizer_access_token"));
        jsonStr.put("expires_in",authorization_info.getIntValue("expires_in"));
        AccessToken accessToken = new AccessToken(jsonStr.toJSONString());
        accessTokenCache.set(key,accessToken);
        return authorization_info;
    }

    /**
     * 获取预授权码pre_auth_code
     * todo 不进行缓存
     * @return
     */
    public String getPreAuthCode() {
        String key = PropKit.get("appID") + ':' + "preAuthCode";
        final ParaMap pm = ParaMap.create("component_appid", PropKit.get("appID"));
        ConfigJson configJson = accessTokenCache.get(key);
        if(configJson==null || !configJson.isAvailable()) {
            // 最多三次请求
            configJson = RetryUtils.retryOnException(3, () ->
                    new ConfigJson(HttpUtils.post(PRE_AUTH_CODE.replaceAll("xxxx", getComponentAccessToken()), JsonKit.toJson(pm.getData())))
            );
            accessTokenCache.set(key, configJson);
        }
        return JSONObject.parseObject(configJson.getJson()).getString("pre_auth_code");
    }

    /**
     * 获取第三方平台component_access_token
     * @return
     */
    public static String getComponentAccessToken(){
        String key = PropKit.get("appID") + ':' + "componentAccessToken";
        final ParaMap pm = ParaMap.create("component_appid", PropKit.get("appID")).put("component_appsecret", PropKit.get("appSecret")).put("component_verify_ticket",Db.use("platform").queryStr("select verify_ticket from auth_info where app_id= 'platform'"));
        ConfigJson configJson = accessTokenCache.get(key);
        if(configJson==null || !configJson.isAvailable()){
            // 最多三次请求
            configJson = RetryUtils.retryOnException(3,()->
                    new ConfigJson(HttpUtils.post(COMPONENT_ACCESS_TOKEN, JsonKit.toJson(pm.getData())))
            );
            accessTokenCache.set(key, configJson);
        }
        return JSONObject.parseObject(configJson.getJson()).getString("component_access_token");
    }

    /**
     * 获取授权方AccessToken
     * @return
     */
    public static String getAuthorizerAccessToken(String authorizerAppid){
        String key = authorizerAppid + ':' + "auth";
        final ParaMap pm = ParaMap.create("component_appid", PropKit.get("appID")).put("authorizer_appid", authorizerAppid).put("authorizer_refresh_token",Db.use("platform").queryStr("select verify_ticket from auth_info where app_id= ?",authorizerAppid));
        AccessToken accessToken = accessTokenCache.get(key);
        if(accessToken==null || !accessToken.isAvailable()){
            // 最多三次请求
            ConfigJson configJson = RetryUtils.retryOnException(3,()->
                    new ConfigJson(HttpUtils.post(REFRESH_AUTHORIZER_TOKEN.replaceAll("xxxx", getComponentAccessToken()), JsonKit.toJson(pm.getData())))
            );
            JSONObject jsonObject = JSONObject.parseObject(configJson.getJson());
            JSONObject jsonStr = new JSONObject();
            jsonStr.put("access_token",jsonObject.getString("authorizer_access_token"));
            jsonStr.put("expires_in",jsonObject.getIntValue("expires_in"));
            accessToken = new AccessToken(jsonStr.toJSONString());
            accessTokenCache.set(key, accessToken);
        }
        return accessToken.getAccessToken();
    }
    /**
     * 获取授权方公众号
     * @return
     */
    public static void getAuthorizerInfo(String authorizerAppid){
        final ParaMap pm = ParaMap.create("component_appid", PropKit.get("appID")).put("authorizer_appid", authorizerAppid);
            // 最多三次请求
        ConfigJson configJson = RetryUtils.retryOnException(3,()->
                new ConfigJson(HttpUtils.post(GET_AUTHORIZER_INFO.replaceAll("xxxx", getComponentAccessToken()), JsonKit.toJson(pm.getData())))
        );
        Db.use("platform").update("update auth_info set field_1 = ? where app_id = ?",configJson.getJson(),authorizerAppid);
    }

    /**
     * 获取关注的用户信息
     * @param c
     * @param key
     * @return
     */
    public static String handleOpenid(Controller c, String key) {
        String code = c.getPara("code");
        log.info("通过code换取网页授权access_token");
        log.info("code:-->" + code);
        SnsAccessToken snsAccessToken = SnsAccessTokenApi.getSnsAccessToken(c.getSessionAttr("appId")+"", code,PropKit.get("appID"),getComponentAccessToken());
        String token = snsAccessToken.getAccessToken();
        String openId = snsAccessToken.getOpenid();
        if (StringUtils.isBlank(token)) {
            String url = null;
            try {
                url = URLEncoder.encode(PropKit.get("serverUrl") + key, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            url = SnsAccessTokenApi.getAuthorizeURL(c.getSessionAttr("appId")+"",false,PropKit.get("appID"), url );
            c.redirect(url);
        } else {
            //拉取用户信息(需scope为 snsapi_userinfo)
            ApiResult apiResult = SnsApi.getUserInfo(token, openId);
            JSONObject jsonObject = JSON.parseObject(apiResult.getJson());
            log.info(jsonObject.toJSONString());
            c.setAttr("headimgurl", jsonObject.getString("headimgurl"));
            c.setAttr("nickName", jsonObject.getString("nickname"));
            c.setAttr("openId", openId);
        }
        return openId;
    }
}
