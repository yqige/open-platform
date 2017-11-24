package com.app.wxapi.utils;

import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Javen
 * 2016年4月3日
 */
public class SMSUtils {
    static Log log = Log.getLog(SMSUtils.class);
    /* 短信配置资源 */
    private static final Prop prop = PropKit.use("sms.properties");
    private final static String SMS_URL = "";
    /* 短信配置详情 */
    private static final String SMS_NAME = prop.get("name");
    private static final String SMS_PWD = prop.get("pwd");
    private static final String SMS_CONTENT_REGISTER_CODE = prop.get("content_register_code");
    private static final String SMS_CONTENT_FORGET_CODE = prop.get("content_forget_code");
    private static final String CONTENT_REGISTER_NOTIFY = prop.get("content_register_notify");
    private static final String CONTENT_ORDER_NOTIFY = prop.get("content_order_notify");
    private static final String SMS_TYPE = prop.get("type");

    public enum SendSMSType {
        REGISTER, FORGET, ORDER_NOTIFY, REGISTER_NOTIFY
    }

    /**
     * 发送短信验证码
     *
     * @param mobile
     * @param code
     * @param sendSMSType
     * @return
     */
    public static int SMSCode(String mobile, String code, String nickName, String courseName, String uesrMobile, String courseCount, SendSMSType sendSMSType) {
        String type = sendSMSType.name().toLowerCase();
        String content = "";
        int res_code = -9;
        try {
            Map<String, String> queryParas = new HashMap<String, String>();
            queryParas.put("name", SMS_NAME);
            queryParas.put("pwd", SMS_PWD);
            if (type.equals("register")) {
                content = StringUtils.replace(SMS_CONTENT_REGISTER_CODE, "@", code);
            } else if (type.equals("forget")) {
                content = StringUtils.replace(SMS_CONTENT_FORGET_CODE, "@", code);
            } else if (type.equals("order_notify")) {
                content = StringUtils.replace(CONTENT_ORDER_NOTIFY, "@", nickName, courseName, courseCount);
            } else if (type.equals("register_notify")) {
                content = StringUtils.replace(CONTENT_REGISTER_NOTIFY, "@", nickName, uesrMobile);
            }
            queryParas.put("content", content);
            queryParas.put("mobile", mobile);
            queryParas.put("type", SMS_TYPE);
            String result = HttpKit.post(SMS_URL, queryParas, "");
            log.error("发送短信返回结果：" + result);
            res_code = Integer.parseInt(result.split(",")[0]);
        } catch (Exception e) {
            res_code = -10;
            log.error("send sms to " + mobile + " error:" + e.getMessage());
        }
        return res_code;
    }

    public static void send(String mobile) {
        String url = PropKit.get("sms_url");// 应用地址
        if (url == null) {
            log.info("没有url.....");
            return;
        }
        String account = PropKit.get("sms_account");// 账号
        String pswd = PropKit.get("sms_pswd");// 密码

        boolean needstatus = true;// 是否需要状态报告，需要true，不需要false
        String extno = null;// 扩展码
        log.info("开始.....");
        int msg = (int) (Math.random() * (9999 - 1000 + 1)) + 1000;// 短信内容
        RedisUtils.intset(mobile, 60, msg + "");
        try {
            String returnString = HttpSender.batchSend(url, account, pswd, mobile, msg + "", needstatus, extno);
            if (returnString == null) {
                log.info("发送失败...");
            }
            log.info("发送成功...");
            System.out.println(returnString);
            log.info(returnString);

            System.out.println(returnString);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
