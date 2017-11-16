package com.app.index;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.app.wxapi.service.MemberService;
import com.app.wxapi.utils.MethodName;
import com.app.wxapi.utils.StringUtils;
import com.app.wxapi.utils.WeiXinUtils;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.UserApi;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import com.platform.annotation.Controller;

/**
 * 本 app 仅表达最为粗浅的 jfinal 用法，更为有价值的实用的企业级用法
 * 详见 JFinal 俱乐部: http://jfinal.com/club
 * todo 不支持多公众号
 * IndexController
 */
@Controller(controllerKey = "/app")
public class IndexController extends ApiController {
    static Log logger = Log.getLog("log_wx");
    static MemberService memberService = Enhancer.enhance(MemberService.class);

    /**
     * 个人首页
     */
    @MethodName(name = "个人首页")
    @Before({IndexValidator.class})
    public void index() {
        String openId = getSessionAttr("openId");
        logger.info("获取到的openid:--->" + openId);
        if (StringUtils.isNotBlank(openId)) {
            Record r = memberService.find(openId);
            if (r == null) throw new RuntimeException("1/0");
            logger.info(openId);
            ApiResult apiResult = UserApi.getUserInfo(openId);
            JSONObject jsonObject = JSON.parseObject(apiResult.getJson());
            setAttr("nickName", jsonObject.getString("nickname"));
            setAttr("headimgurl", jsonObject.getString("headimgurl"));
            setAttr("memmbertype", r.getStr("card_name"));
            setAttr("money", r.getBigDecimal("money"));
            setAttr("memmernum", r.getStr("memmernum"));
            setAttr("members_point", r.getBigDecimal("members_point"));
            setAttr("discount", r.get("discount", 1000));
            setAttr("pt_url", PropKit.get("pt.url"));
        }else throw new RuntimeException("1/0");
    }

    @Override
    public ApiConfig getApiConfig() {
        logger.info("获取公众号信息");
        logger.info("appId:-----"+getSessionAttr("appId"));
        return WeiXinUtils.getApiConfig(getSessionAttr("appId"));
    }
}





