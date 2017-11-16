package com.app.wxapi.controller;

import com.app.wxapi.validator.ResValidator;
import com.jfinal.aop.Before;
import com.platform.annotation.Controller;
import weixin.popular.util.SignatureUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ruieliu on 2017/6/29.
 */
@Controller(controllerKey = "/res")
public class ResController extends BaseController {
    @Before(ResValidator.class)
    public void res() {
        String signature = getPara("signature");
        String echostr = getPara("echostr");
        Map<String, String> map = new HashMap();
        String checksig = SignatureUtil.generateEventMessageSignature("majianbo", getPara("nonce"), getPara("timestamp"));
        if (signature.equals(checksig)) renderJson(echostr);
    }
    @Deprecated
    public void testNum(){
        HashMap hm=new HashMap();
        hm.put("key","1,16723456890,34,345");
        hm.put("appId","123");
        hm.put("openId", null);
//        new Thread(()-> HttpUtils.get("http://127.0.0.1:8080/wx-brand/member/updateNo",hm)).start();
        renderNull();
    }

}
