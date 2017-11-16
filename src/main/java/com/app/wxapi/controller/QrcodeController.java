package com.app.wxapi.controller;

import com.alibaba.fastjson.JSONObject;
import com.app.wxapi.utils.Exclude;
import com.app.wxapi.utils.MethodName;
import com.app.wxapi.utils.StringUtils;
import com.app.wxapi.utils.WeiXinUtils;
import com.app.wxapi.validator.QrcodeValidator;
import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.QrcodeApi;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.platform.annotation.Controller;
import weixin.popular.util.JsonUtil;

import java.util.HashMap;

/**
 * Created by ruieliu on 2017/6/28.
 */
@Controller(controllerKey = "/qrcode")
public class QrcodeController extends ApiController {
    static Log logger = Log.getLog("log_wx");
    public static String phone = "99999999999";
    private String appId="";
    public ApiConfig getApiConfig() {
        logger.info("获取公众号信息");
        String tenant_id = getPara(1);
        HashMap hashMap =new HashMap();
        hashMap.put("tenant_id",tenant_id);
        String json=HttpUtils.post(PropKit.get("linkOrder")+"/woa/api/findAppInfo",JsonUtil.toJSONString(hashMap));
        logger.info("json:"+json);
        JSONObject info = JSONObject.parseObject(json);
        if(info.getString("status").equals("0")){
            appId=JSONObject.parseObject(info.getString("appInfo")).getString("app_id");
        }
        return WeiXinUtils.getApiConfig(appId);
    }
    @Exclude
    @MethodName(name = "吸纳会员二维码")
    @Before(QrcodeValidator.class)
    public void getQrcode() {
        logger.info("根据门店查询appId");
        logger.info("appId:"+appId);
        logger.info("进入绑定订单的新方法");
        Record re = Db.findFirst("select * from crm_member_info where phone=? and openid is not null and app_id=?", getPara(0),appId);
        StringBuffer scene_str = new StringBuffer(System.currentTimeMillis() + ",");
        scene_str.append(getPara(0)).append(",").append(getPara(1)).append(",")
                .append(getPara(2));
        if(StringUtils.isNotBlank(getPara(3))){//订单号
            scene_str.append(",").append(getPara(3));
        }
        logger.info("关注二维码携带的信息--->" + scene_str.toString());
        if (re == null){
            logger.warn("生成新的二维码");
            renderQrCode(QrcodeApi.createTemporary(300, scene_str.toString()).getStr("url"),300,300,'H');
        }
        else redirect(PropKit.get("img_url"));
    }
}
