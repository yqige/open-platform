package com.app.wxapi.controller;

import com.app.wxapi.utils.DateUtil;
import com.app.wxapi.utils.WeiXinUtils;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.jfinal.MsgControllerAdapter;
import com.jfinal.weixin.sdk.msg.in.*;
import com.jfinal.weixin.sdk.msg.in.event.*;
import com.jfinal.weixin.sdk.msg.in.speech_recognition.InSpeechRecognitionResults;
import com.jfinal.weixin.sdk.msg.out.OutCustomMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;
import com.jfinal.weixin.sdk.msg.out.OutVoiceMsg;
import com.platform.annotation.Controller;
import com.weixin.face.FaceService;

/**
 * Created by ruieliu on 2017/6/28.
 */
@Controller(controllerKey = "/wxMsg")
public class WxMsgController extends MsgControllerAdapter {
    static Log logger = Log.getLog("log_wx");
//    MemberService memberService = MemberService.service;
    private static final String helpStr = "感谢您的关注 祝您身体健康 生活幸福!";

    @Override
    public ApiConfig getApiConfig() {
        logger.info("appId:"+getPara(0));
        String appId=getPara(0);
        if(StrKit.isBlank(appId))return new ApiConfig();
        return WeiXinUtils.getApiConfig(appId);
    }
    String getMessage() {
        try {
            Record r = Db.findFirst("select*from crm_welcome where id=? and ? between start_time and end_time",1, DateUtil.getNowString(0));
            if (r == null) {
                logger.info("没有记录");
                return helpStr;
            }
            return r.getStr("message");
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return helpStr;
        }

    }
    @Override
    protected void processInTextMsg(InTextMsg inTextMsg) {
        renderOutTextMsg("你发的内容为：" + inTextMsg.getContent());
    }

    @Override
    protected void processInVoiceMsg(InVoiceMsg inVoiceMsg) {
        //转发给多客服PC客户端
//		OutCustomMsg outCustomMsg = new OutCustomMsg(inVoiceMsg);
//		render(outCustomMsg);
        OutVoiceMsg outMsg = new OutVoiceMsg(inVoiceMsg);
        // 将刚发过来的语音再发回去
        outMsg.setMediaId(inVoiceMsg.getMediaId());
        render(outMsg);
    }

    @Override
    protected void processInVideoMsg(InVideoMsg inVideoMsg) {
        /*
		 * 腾讯 api 有 bug，无法回复视频消息，暂时回复文本消息代码测试 OutVideoMsg outMsg = new
		 * OutVideoMsg(inVideoMsg); outMsg.setTitle("OutVideoMsg 发送");
		 * outMsg.setDescription("刚刚发来的视频再发回去"); // 将刚发过来的视频再发回去，经测试证明是腾讯官方的 api
		 * 有 bug，待 api bug 却除后再试 outMsg.setMediaId(inVideoMsg.getMediaId());
		 * render(outMsg);
		 */
        OutTextMsg outMsg = new OutTextMsg(inVideoMsg);
        outMsg.setContent("\t视频消息已成功接收，该视频的 mediaId 为: " + inVideoMsg.getMediaId());
        render(outMsg);
    }

    @Override
    protected void processInShortVideoMsg(InShortVideoMsg inShortVideoMsg) {
        OutTextMsg outMsg = new OutTextMsg(inShortVideoMsg);
        outMsg.setContent("\t视频消息已成功接收，该视频的 mediaId 为: " + inShortVideoMsg.getMediaId());
        render(outMsg);
    }

    @Override
    protected void processInLocationMsg(InLocationMsg inLocationMsg) {
        OutTextMsg outMsg = new OutTextMsg(inLocationMsg);
        outMsg.setContent("已收到地理位置消息:" + "\nlocation_X = " + inLocationMsg.getLocation_X() + "\nlocation_Y = "
                + inLocationMsg.getLocation_Y() + "\nscale = " + inLocationMsg.getScale() + "\nlabel = "
                + inLocationMsg.getLabel());
        render(outMsg);
    }

    @Override
    protected void processInLinkMsg(InLinkMsg inLinkMsg) {
        //转发给多客服PC客户端
        OutCustomMsg outCustomMsg = new OutCustomMsg(inLinkMsg);
        render(outCustomMsg);
    }

    @Override
    protected void processInCustomEvent(InCustomEvent inCustomEvent) {
        logger.info("测试方法：processInCustomEvent()");
        renderNull();
    }

    protected void processInImageMsg(InImageMsg inImageMsg) {
        //转发给多客服PC客户端
//		OutCustomMsg outCustomMsg = new OutCustomMsg(inImageMsg);
//		render(outCustomMsg);
        String picUrl = inImageMsg.getPicUrl();
        String respContent = FaceService.detect(picUrl);
        renderOutTextMsg(respContent);
    }

    /**
     * 实现父类抽方法，处理关注/取消关注消息
     * 不是扫码关注的，会进入此方法
     */
    protected void processInFollowEvent(InFollowEvent inFollowEvent) {
        logger.info("processInFollowEvent.......");
        if (InFollowEvent.EVENT_INFOLLOW_SUBSCRIBE.equals(inFollowEvent.getEvent())) {
            logger.info("关注：" + inFollowEvent.getFromUserName());
            OutTextMsg outMsg = new OutTextMsg(inFollowEvent);
            logger.info("关注返回信息：" + outMsg.getContent());
//            memberService.updateNo(inFollowEvent.getFromUserName());
            outMsg.setContent(getMessage());
            render(outMsg);
        }
        // 如果为取消关注事件，将无法接收到传回的信息
        if (InFollowEvent.EVENT_INFOLLOW_UNSUBSCRIBE.equals(inFollowEvent.getEvent())) {
            logger.info("取消关注：" + inFollowEvent.getFromUserName());
            renderText("");
        }
    }

    @Override
    protected void processInQrCodeEvent(InQrCodeEvent inQrCodeEvent) {
        logger.info("扫码.......");
        if (InQrCodeEvent.EVENT_INQRCODE_SUBSCRIBE.equals(inQrCodeEvent.getEvent())) {
            logger.info("扫码未关注：" + inQrCodeEvent.getFromUserName());
            OutTextMsg outMsg = new OutTextMsg(inQrCodeEvent);
            String key = inQrCodeEvent.getEventKey();
            logger.info(key);
            try {
//                memberService.update(key.split("_")[1], inQrCodeEvent.getFromUserName());
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
            outMsg.setContent(getMessage());
            render(outMsg);
        }
        if (InQrCodeEvent.EVENT_INQRCODE_SCAN.equals(inQrCodeEvent.getEvent())) {
            logger.info("扫码已关注：" + inQrCodeEvent.getFromUserName());
            OutTextMsg outMsg = new OutTextMsg(inQrCodeEvent);
            String key = inQrCodeEvent.getEventKey();
            logger.info("key:" + key);
            try {
//               memberService.update(key, inQrCodeEvent.getFromUserName());
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
            outMsg.setContent(getMessage());
            render(outMsg);
        }

    }

    @Override
    protected void processInLocationEvent(InLocationEvent inLocationEvent) {
        logger.info("发送地理位置事件：" + inLocationEvent.getFromUserName());
        OutTextMsg outMsg = new OutTextMsg(inLocationEvent);
        outMsg.setContent("地理位置是：" + inLocationEvent.getLatitude());
//        render(outMsg);
    }

    @Override
    protected void processInMassEvent(InMassEvent inMassEvent) {
        logger.info("测试方法：processInMassEvent()");
        renderNull();
    }

    /**
     * 实现父类抽方法，处理自定义菜单事件
     */
    protected void processInMenuEvent(InMenuEvent inMenuEvent) {
        logger.info("菜单事件：" + inMenuEvent.getFromUserName());
        OutTextMsg outMsg = new OutTextMsg(inMenuEvent);
        outMsg.setContent("菜单事件内容是：" + inMenuEvent.getEventKey());
//
        render(outMsg);
    }

    @Override
    protected void processInSpeechRecognitionResults(InSpeechRecognitionResults inSpeechRecognitionResults) {
        logger.info("语音识别事件：" + inSpeechRecognitionResults.getFromUserName());
        OutTextMsg outMsg = new OutTextMsg(inSpeechRecognitionResults);
        outMsg.setContent("语音识别内容是：" + inSpeechRecognitionResults.getRecognition());
        render(outMsg);
    }

    @Override
    protected void processInTemplateMsgEvent(InTemplateMsgEvent inTemplateMsgEvent) {
        logger.info("测试方法：processInTemplateMsgEvent()");
        renderNull();
    }

}
