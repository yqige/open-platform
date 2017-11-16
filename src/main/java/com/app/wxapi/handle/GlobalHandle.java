package com.app.wxapi.handle;


import com.app.wxapi.config.plugin.ControllerPlugin;
import com.app.wxapi.utils.StringUtils;
import com.jfinal.handler.Handler;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class GlobalHandle extends Handler{
    static Log logger = Log.getLog("log_wx");
    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        String appId = request.getParameter("appId");
        String openId = request.getParameter("openId");
        logger.info("appId:"+appId);
        logger.info("openId:"+openId);
        logger.info("url:"+target);
        if(target.indexOf("/wxMsg/")>-1)
            appId=target.replace("/wxMsg/","");
        HttpSession session = request.getSession();
        if(StringUtils.isNotBlank(appId)&&StringUtils.isNotBlank(openId)){
            session.setAttribute("appId",appId);
            session.setAttribute("openId",openId);
        }
        ApiConfig apiConfig=ApiConfigKit.getAccessTokenCache().get("app_info:"+session.getAttribute("appId"));
        request.setAttribute("CTX", PropKit.get("static_url"));
        if(isError(target)){
            if(!isPass(session)||apiConfig==null)
            try {
                response.sendRedirect(request.getContextPath()+"/wxapi/errorHtml");
                isHandled[0]=true;
            } catch (IOException e) {
                e.printStackTrace();
            }else next.handle(target, request, response, isHandled);
        }
        else
            next.handle(target, request, response, isHandled);
    }

    Boolean isError(String target){
        //获取关注二维码和发消息特殊处理
        if(ControllerPlugin.contains(target)||target.indexOf("/wxMsg/")>-1)
            return false;
        return true;
    }
    Boolean isPass(HttpSession session){
        if(session.getAttribute("appId")!=null&&session.getAttribute("openId")!=null)
        return true;
        else return false;
    }
}
