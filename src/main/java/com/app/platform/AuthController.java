package com.app.platform;

import com.app.wxapi.service.PlatformService;
import com.app.wxapi.utils.Exclude;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import com.jfinal.weixin.sdk.kit.MsgEncryptKit;
import com.platform.annotation.Controller;

@Controller(controllerKey = "/")
public class AuthController extends ApiController {
    PlatformService service = enhance(PlatformService.class);
    static String uu = "https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid=:appID&pre_auth_code=:preAuthCode&redirect_uri=:redirectUri";
    @Exclude
    public void index(){
        String appID = PropKit.get("appID");
        String redirectUri = PropKit.get("redirectUri");
        String url=uu.replaceAll(":appID",appID).replaceAll(":redirectUri",redirectUri).replaceAll(":preAuthCode",service.getPreAuthCode());
        setAttr("component_login_page",url);
        render("index/index");
    }
    @Exclude
    public void event(){
        try{
            String inMsgXml = HttpKit.readData(getRequest());
            inMsgXml = MsgEncryptKit.decrypt(inMsgXml, getPara("timestamp"), getPara("nonce"), getPara("msg_signature"));
            service.dealEvent(inMsgXml);
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        renderText("success");
    }

    @Override
    public ApiConfig getApiConfig() {
        return new ApiConfig(PropKit.get("appToken"),PropKit.get("appID"),PropKit.get("appSecret"),true,PropKit.get("encodingKey"));
    }
    @Exclude
    public void callback(){
        try{
            String inMsgXml = HttpKit.readData(getRequest());
//            inMsgXml = MsgEncryptKit.decrypt(inMsgXml, getPara("timestamp"), getPara("nonce"), getPara("msg_signature"));
            service.dealEvent(inMsgXml);
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        renderText("success");
    }
    @Exclude
    public void success(){
        render("index/success");
    }
}
