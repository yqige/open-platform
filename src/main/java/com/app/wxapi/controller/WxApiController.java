package com.demo.wxapi.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.wxapi.entity.Menu;
import com.demo.wxapi.utils.*;
import com.demo.wxapi.validator.WxapiValidator;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.*;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.platform.annotation.Controller;
import weixin.popular.util.JsonUtil;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by ruieliu on 2017/6/29.
 */
@Controller(controllerKey = "/wxapi")
public class WxApiController extends ApiController {
    static Log logger = Log.getLog("log_wx");
    static HashMap hashMapTem=new HashMap();
    static {
        hashMapTem.put("TM00247","购买成功通知-1");
        hashMapTem.put("OPENTM206905995","商品退货提醒-2");
        hashMapTem.put("OPENTM204526726","储值卡消费提醒-3");
        hashMapTem.put("OPENTM406440024","储值余额变动通知-4");
        hashMapTem.put("TM00141","会员充值通知-5");
    }
    public ApiConfig getApiConfig() {
        logger.info("获取公众号信息");
        logger.info("appId:-----"+getSessionAttr("appId"));
        String appId=getSessionAttr("appId");
        if(StrKit.isBlank(appId))return new ApiConfig();
        return WeiXinUtils.getApiConfig(appId);
    }

    /**
     * 创建菜单
     */
    public void createMenu() {
        String str = "";
        Menu menu = new Menu();
        menu.setview("购买记录", PropKit.get("pt.url") +getSessionAttr("appId")+ "/wxbrand/orders");
        menu.setview("我的收藏", PropKit.get("pt.url") + getSessionAttr("appId")+ "/wxbrand/collection");
        menu.setview("我", PropKit.get("pt.url") + getSessionAttr("appId")+ "/wxbrand/index");
        HashMap hm = menu.getButton();
        ApiResult apiResult = MenuApi.createMenu(JsonUtil.toJSONString(hm));
        if (apiResult.isSucceed())
            renderText(apiResult.getJson());
        else
            renderText(apiResult.getErrorMsg());
    }

    /**
     * 获取公众号关注用户
     */
    public void getFollowers() {
        ApiResult apiResult = UserApi.getFollows();
        JSONObject r = apiResult.get("data");
        JSONArray arr = r.getJSONArray("openid");
        String msg = "【我的收藏】内容已清空！";
        HashMap hashMap = new HashMap();
        hashMap.put("touser", arr);
        hashMap.put("msgtype", "text");
        HashMap ha = new HashMap();
        ha.put("content", msg);
        hashMap.put("text", ha);
        hashMap.put("clientmsgid", DateUtil.getNowLong());
        ApiResult m = MessageApi.send(JsonUtil.toJSONString(hashMap));
        renderText(m.getJson());
    }

    /**
     * 获取用户信息
     */
    public void getUserInfo() {
        ApiResult apiResult = UserApi.getUserInfo("ohbweuNYB_heu_buiBWZtwgi4xzU");
        renderText(apiResult.getJson());
    }
    @Exclude
    @MethodName(name = "发送支付模板消息")
    public void sendMsg() {
        HashMap hashMap = new HashMap();
        try {
            logger.info("id--->" + getPara(0));
            HashMap hm =new HashMap();
            hm.put("order_id",getPara(0));
            String json= HttpUtils.post(PropKit.get("linkOrder")+"/woa/api/findInfoByOrderId",JsonUtil.toJSONString(hm));
            logger.info("findInfoByOrderId_json:"+json);
            JSONObject info = JSONObject.parseObject(json);
            String status = info.getString("status");
            if(status.equals("0")){
                JSONObject order = JSONObject.parseObject(info.getString("orderInfo"));
                JSONObject appTemplate = JSONObject.parseObject(info.getString("appTemplate"));
                String openId = info.getString("openid");
                String template_id = appTemplate.getString("template_id");
                String appId = appTemplate.getString("app_id");
                String ms = "您在【" + order.getString("name") + "】的订单已成功完成";
                String url = PropKit.get("serverUrl") + "/favorites/getOrderInfo?id=" + getPara(0)+"&appId="+getSessionAttr("appId")+"&openId="+openId;
                if (order.getIntValue("order_type") == 2) {
                    ms = "您在【" + order.getString("name") + "】的订单已成功退款";
                }
                if (order.getIntValue("order_type") == 2) {
                    String str = TemplateData.New()
                            .setTouser(openId)
                            .setTemplate_id(template_id)
                            .setTopcolor("#743A3A")
                            .setUrl(url)
                            .add("first", ms, "#743A3A")
                            .add("keyword1", order.getString("order_no"), "#0000FF")
                            .add("keyword2", "服饰", "#0000FF")
                            .add("keyword3", order.getIntValue("order_qty") + "", "#0000FF")
                            .add("keyword4", order.getBigDecimal("actual_amt") + "", "#0000FF")
                            .add("remark", "非常抱歉给您带来了不便 ！期待您下次光临，祝生活愉快！", "#008000")
                            .build();
                    sendMessage(str,appId);
                } else {
                    String str1 = TemplateData.New()
                            .setTouser(openId)
                            .setTemplate_id(template_id)
                            .setTopcolor("#743A3A")
                            .setUrl(url)
                            .add("first", ms, "#743A3A")
                            .add("product", "服饰", "#0000FF")
                            .add("price", order.getBigDecimal("actual_amt") + "", "#0000FF")
                            .add("time", DateUtil.DateToString(new Date(), 0), "#0000FF")
                            .add("remark", "感谢您的支持 欢迎您再次光临 祝生活愉快", "#008000")
                            .build();
                    sendMessage(str1,appId);
                }
            }
            hashMap.put("code", "1");
            hashMap.put("message", "推送成功");
        } catch (Exception e) {
            hashMap.put("code", "0");
            hashMap.put("message", e.getMessage());
        } finally {
            renderJson(hashMap);
        }
    }

    /**
     * 获取参数二维码
     */
    public void getQrcode() {
        String str = "{\"expire_seconds\": 604800, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": 123}}}";
        ApiResult apiResult = QrcodeApi.create(str);
        renderText(apiResult.getJson());

//        String str = "{\"action_name\": \"QR_LIMIT_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"123\"}}}";
//        ApiResult apiResult = QrcodeApi.create(str);
//        renderText(apiResult.getJson());
    }

    /**
     * 测试输出的结果
     * create>>{"ticket":"gQFo8DoAAAAAAAAAASxodHRwOi8vd2VpeGluLnFxLmNvbS9xL0cwT21FZjNtM3RXbmd3REF6Ml82AAIEzyFQVwMEAAAAAA==","url":"http:\/\/weixin.qq.com\/q\/G0OmEf3m3tWngwDAz2_6"}
     * qrcodeUrl:https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=gQFo8DoAAAAAAAAAASxodHRwOi8vd2VpeGluLnFxLmNvbS9xL0cwT21FZjNtM3RXbmd3REF6Ml82AAIEzyFQVwMEAAAAAA==
     */
    public void getQrcode2() {
        ApiResult apiResult = QrcodeApi.createPermanent(100);
        String qrcodeUrl = QrcodeApi.getShowQrcodeUrl(apiResult.getStr("ticket"));
        renderText("create>>" + apiResult.getJson() + " qrcodeUrl:" + qrcodeUrl);
    }

    /**
     * 长链接转成短链接
     */
    public void getShorturl() {
        String str = "{\"action\":\"long2short\"," +
                "\"long_url\":\"http://wap.koudaitong.com/v2/showcase/goods?alias=128wi9shh&spm=h56083&redirect_count=1\"}";
        ApiResult apiResult = ShorturlApi.getShorturl(str);
        renderText(apiResult.getJson());
    }

    /**
     * 获取客服聊天记录
     */
    public void getRecord() {
        String str = "{\n" +
                "    \"endtime\" : 987654321,\n" +
                "    \"pageindex\" : 1,\n" +
                "    \"pagesize\" : 10,\n" +
                "    \"starttime\" : 123456789\n" +
                " }";
        ApiResult apiResult = CustomServiceApi.getRecord(str);
        renderText(apiResult.getJson());
    }

    /**
     * 获取微信服务器IP地址
     */
    public void getCallbackIp() {
        ApiResult apiResult = CallbackIpApi.getCallbackIp();
        renderText(apiResult.getJson());
    }
    @Exclude
    @Clear
    public void errorHtml(){
        render("/include/error.html");
    }
    @Exclude
    @MethodName(name = "会员充值通知")
    public void storeAdd() {
        HashMap hashMap = new HashMap();
        try {
            String status = getPara("status");//充值状态
            String openId = getPara("openId");//会员openid
            String template_id = getPara("template_id");//消息模版id
            String memberNum = getPara("memberNum");//会员编号
            String amount = getPara("amount");//充值金额
            String appId = getPara("appId");//公众号appId
            String ms;
            if(status.equals("0"))
                ms="您好，您正在进行会员充值。";
            else  ms="您好，您已成功进行会员充值。";
            String str = TemplateData.New()
                    .setTouser(openId)
                    .setTemplate_id(template_id)
                    .setTopcolor("#743A3A")
                    .add("first", ms, "#743A3A")
                    .add("accountType", "储值卡号", "#0000FF")
                    .add("account", memberNum, "#0000FF")
                    .add("amount", amount, "#0000FF")
                    .add("result", status.equals("0")?"充值失败":"充值成功", "#0000FF")
                    .add("remark", "如有疑问，请至附近门店联系我们。", "#008000")
                    .build();
            sendMessage(str,appId);
            hashMap.put("code", "1");
            hashMap.put("message", "推送成功");
        } catch (Exception e) {
            hashMap.put("code", "0");
            hashMap.put("message", e.getMessage());
        } finally {
            renderJson(hashMap);
        }
    }
    @Exclude
    @MethodName(name = "储值卡消费提醒")
    public void storeChange() {
        HashMap hashMap = new HashMap();
        try {
            String openId = getPara("openId");//会员openid
            String template_id = getPara("template_id");//消息模版id
            String memberNum = getPara("memberNum");//会员编号
            String memberName = getPara("memberName");//会员姓名
            String amount = getPara("amount");//消费金额
            String amountBefore = getPara("amountBefore");//消费前余额
            String amountAfter = getPara("amountAfter");//消费后余额
            String appId = getPara("appId");//公众号appId
            String ms = "储值卡消费提醒";
            String str = TemplateData.New()
                    .setTouser(openId)
                    .setTemplate_id(template_id)
                    .setTopcolor("#743A3A")
                    .add("first", ms, "#743A3A")
                    .add("keyword1", memberName, "#0000FF")
                    .add("keyword2", memberNum, "#0000FF")
                    .add("keyword3", amount, "#0000FF")
                    .add("keyword4", amountBefore, "#0000FF")
                    .add("keyword4", amountAfter, "#0000FF")
                    .add("remark", DateUtil.getNowString(1)+" 感谢你的使用。", "#008000")
                    .build();
            sendMessage(str,appId);
            hashMap.put("code", "1");
            hashMap.put("message", "推送成功");
        } catch (Exception e) {
            hashMap.put("code", "0");
            hashMap.put("message", e.getMessage());
        } finally {
            renderJson(hashMap);
        }
    }
    @Exclude
    @MethodName(name = "储值余额变动通知")
    public void storeBack() {
        HashMap hashMap = new HashMap();
        try {
            String openId = getPara("openId");//会员openid
            String template_id = getPara("template_id");//消息模版id
            String reason = getPara("reason");//变动原因
            String changeAmount = getPara("changeAmount");//变动明细
            String amount = getPara("amount");//当前余额
            String appId = getPara("appId");//公众号appId
            String ms = "您的储值余额产生了变动。";
            String str = TemplateData.New()
                    .setTouser(openId)
                    .setTemplate_id(template_id)
                    .setTopcolor("#743A3A")
                    .add("first", ms, "#743A3A")
                    .add("keyword1", reason, "#0000FF")
                    .add("keyword2", changeAmount, "#0000FF")
                    .add("keyword3", amount, "#0000FF")
                    .add("remark", DateUtil.getNowString(1)+" 感谢您的支持。", "#008000")
                    .build();
            sendMessage(str,appId);
            hashMap.put("code", "1");
            hashMap.put("message", "推送成功");
        } catch (Exception e) {
            hashMap.put("code", "0");
            hashMap.put("message", e.getMessage());
        } finally {
            renderJson(hashMap);
        }
    }
    void sendMessage(String string,String appId){
        new Thread(() -> {
            ApiConfigKit.setThreadLocalApiConfig(getApiConfig(appId));
            logger.info(TemplateMsgApi.send(string).getJson());
        }).start();
    }

    private ApiConfig getApiConfig(String appId) {
        return WeiXinUtils.getApiConfig(appId);
    }
    @Before(WxapiValidator.class)
    @MethodName(name = "批量添加模版消息")
    public void apiAddTemplates() {
        JSONObject jsonObject = new JSONObject();
        try {
            hashMapTem.keySet().forEach(key->{
                ApiResult re = TemplateMsgApi.add(new JSONObject(Kv.by("template_id_short", key)).toJSONString());
                logger.info("模版返回信息：" + re.getJson());
                if(re.isSucceed()){
                    String[] in = hashMapTem.get(key).toString().split("-");
                    String template_id = re.get("template_id");
                    String appId = getSessionAttr("appId");
                    Record record = new Record();
                    record.set("template_id",template_id);
                    record.set("app_id",appId);
                    record.set("field_2",in[0]);
                    record.set("template_type",in[1]);
                    Db.save("crm_app_template",record);
                    Db.update("update dicts set value=? where name='随机数' and object='刷新appInfo' and field='刷新appInfo'", StringUtils.random(32, StringUtils.RandomType.ALL));
                }
                jsonObject.put(key.toString(),re);
            });
            renderJson(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            renderText("error");
        }
    }
    @Before(WxapiValidator.class)
    @MethodName(name = "添加模版消息")
    public void apiAddTemplate() {
        try {
            ApiResult re = TemplateMsgApi.add(new JSONObject(Kv.by("template_id_short", getPara("template_id_short"))).toJSONString());
            logger.info("模版返回信息：" + re.getJson());
            if(re.isSucceed()){
                String[] in = hashMapTem.get(getPara("template_id_short")).toString().split("-");
                String template_id = re.get("template_id");
                String appId = getSessionAttr("appId");
                Record record = new Record();
                record.set("template_id",template_id);
                record.set("app_id",appId);
                record.set("field_2",in[0]);
                record.set("template_type",in[1]);
                Db.save("crm_app_template",record);
                Db.update("update dicts set value=? where name='随机数' and object='刷新appInfo' and field='刷新appInfo'", StringUtils.random(32, StringUtils.RandomType.ALL));
            }
            renderJson(re.getJson());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            renderText("error");
        }
    }
    @Before(WxapiValidator.class)
    @MethodName(name = "设置行业")
    public void apiSetIndustry(){
        try {
            ApiResult re = TemplateMsgApi.set(new JSONObject(Kv.by("industry_id1", "31").set("industry_id2","41")).toJSONString());
            logger.info("模版返回信息："+re.getJson());
            Db.update("update dicts set value=? where name='随机数' and object='刷新appInfo' and field='刷新appInfo'", StringUtils.random(32, StringUtils.RandomType.ALL));
            renderJson(re.getJson());
        }catch (Exception e){
            logger.error(e.getMessage());
            renderText("error");
        }
    }
    @Before(WxapiValidator.class)
    @MethodName(name = "获取模版列表")
    public void apiGetTemplates(){
        try {
            ApiResult re = TemplateMsgApi.get(new JSONObject(Kv.by("industry_id1", "31")).toJSONString());
            logger.info("模版返回信息："+re.getJson());
            Db.update("update dicts set value=? where name='随机数' and object='刷新appInfo' and field='刷新appInfo'", StringUtils.random(32, StringUtils.RandomType.ALL));
            renderJson(re.getJson());
        }catch (Exception e){
            logger.error(e.getMessage());
            renderText("error");
        }
    }
    @Before(WxapiValidator.class)
    @Exclude
    @MethodName(name = "刷新appInfo")
    public void refAppInfo(){
        LogKit.info("开始刷新appInfo");
        WeiXinUtils.loadAppInfo();
        Db.update("update dicts set value=? where name='随机数' and object='刷新appInfo' and field='刷新appInfo'", StringUtils.random(32, StringUtils.RandomType.ALL));
        renderText("ok");
    }
}
