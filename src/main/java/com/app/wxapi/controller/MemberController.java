package com.app.wxapi.controller;

import com.alibaba.fastjson.JSONObject;
import com.app.wxapi.service.MemberService;
import com.app.wxapi.utils.DateUtil;
import com.app.wxapi.utils.MethodName;
import com.app.wxapi.utils.WeiXinUtils;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.platform.annotation.Controller;
import weixin.popular.util.JsonUtil;

import java.util.HashMap;

@Controller(controllerKey = "/member")
public class MemberController extends ApiController {
    MemberService memberService=MemberService.service;
    static Log logger = Log.getLog("log_wx");
    private static final String helpStr = "感谢您的关注 祝您身体健康 生活幸福!";
    @MethodName(name = "关注更新会员信息")
    public void updateMember(){
        try {
            memberService.update(getPara("key"),getSessionAttr("openId"),getSessionAttr("appId"));
            renderJson("message","");
        }catch (Exception e){
            e.printStackTrace();
            if(e.getMessage().equals("phone_error"))
            renderJson("message","输入的手机号和该微信绑定的手机号不一致,请联系导购修改");
            else renderJson("message","会员绑定失败，请重新关注");
        }

    }
    @MethodName(name = "关注更新会员信息openId")
    public void updateNo(){
        try {
            //todo 调用接口查询tenant_id
            String appId=getSessionAttr("appId");
            logger.info("调用接口查询tenant_id");
            String tenant_id=null;
            HashMap hashMap =new HashMap();
            hashMap.put("app_id",appId);
            String json= HttpUtils.post(PropKit.get("linkOrder")+"/woa/api/findTenantId", JsonUtil.toJSONString(hashMap));
            logger.info("json:"+json);
            JSONObject info = JSONObject.parseObject(json);
            if(info.getString("status").equals("0")){
                tenant_id=info.getString("tenant_id");
            }
            memberService.updateNo(getSessionAttr("openId"),tenant_id,appId);
            renderJson("message","");
        }catch (Exception e){
            e.printStackTrace();
            renderJson("message","会员绑定失败，请重新关注");
        }

    }
    @MethodName(name = "发送消息")
    public void sendMessage(){
        String message=helpStr;
        try {
            Record r = Db.findFirst("select*from crm_welcome where app_id=? and ? between start_time and end_time",getSessionAttr("appId"), DateUtil.getNowString(0));
            if (r == null) {
                logger.info("没有记录");
            }else
            message= r.getStr("message");
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        logger.info("message:"+message);
//        WeiXinUtils.sendMessages(getSessionAttr("openId"),message,"text");
        renderJson("message",message);
    }

    @Override
    public ApiConfig getApiConfig() {
        logger.info("获取公众号信息");
        logger.info("appId:-----"+getSessionAttr("appId"));
        return WeiXinUtils.getApiConfig(getSessionAttr("appId"));
    }
}
