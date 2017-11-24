package com.app.wxapi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.app.wxapi.model.Favorites;
import com.app.wxapi.model.memberinfo.MemberInfo;
import com.app.wxapi.service.FavoritesService;
import com.app.wxapi.service.MemberService;
import com.app.wxapi.service.OrderService;
import com.app.wxapi.utils.DateUtil;
import com.app.wxapi.utils.MethodName;
import com.app.wxapi.utils.StringUtils;
import com.app.wxapi.utils.WeiXinUtils;
import com.app.wxapi.validator.CodeValidator;
import com.app.wxapi.validator.FavoritesValidator;
import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.*;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import com.jfinal.weixin.sdk.utils.Base64Utils;
import com.jfinal.weixin.sdk.utils.JsonUtils;
import com.platform.annotation.Controller;
import weixin.popular.util.JsUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ruieliu on 2017/6/27.
 * todo 目前不支持多公众号
 */
@Controller(controllerKey = "/favorites")
public class FavoritesController extends ApiController {

    public static final FavoritesService service = FavoritesService.service;
    public static final MemberService memberService = MemberService.service;
    public static final OrderService orderService = OrderService.service;
    static Log logger = Log.getLog("log_wx");

    /**
     * 个人收藏列表
     */
    @MethodName(name = "个人收藏列表")
    @Before({FavoritesValidator.class})
    public void list() {
        String openId = getSessionAttr("openId");
        String configJson = JsUtil.generateConfigJson(JsTicketApi.getTicket(JsTicketApi.JsApiType.jsapi).getTicket(), false, ApiConfigKit.getApiConfig().getAppId()
                , PropKit.get("serverUrl") + "/favorites/list", "scanQRCode", "chooseImage");
        List<Favorites> list = service.list(openId);
        for (int i = 0; i < list.size(); i++) {
            Favorites favorites = list.get(i);
            favorites.setPicdateStr(DateUtil.DateToString(favorites.getCreateDate(), 0));
        }
        Record r = memberService.find(openId);
        if (r == null) throw new RuntimeException("1/0");
        System.out.println(configJson);
        setAttr("configJson", configJson);
        logger.info("configJson:"+JsonUtils.toJson(configJson));
        setAttr("list", list);
        setAttr("memmernum", r.getStr("memmernum"));
    }

    /**
     * 更新个人收藏
     */
    @MethodName(name = "更新个人收藏")
    @Before(FavoritesValidator.class)
    public void saveOrUpdate() {
        String localId = getPara("localId");
        String code = getPara("code");
        String openid = getSessionAttr("openId");
        service.saveOrUpdate(localId,code,openid);
        renderJson("status", "200");
    }

    /**
     * 前往会员修改页面
     */
    @MethodName(name = "前往会员修改页面")
    @Before(FavoritesValidator.class)
    public void editMemInfo() {
        String openId = getSessionAttr("openId");
        if (openId != null) {
            Record r = memberService.find(openId.toString());
            if(r.getDate("birthday")==null)
                try {
                    r.set("birthday",DateUtil.StringToDate("1900-01-01 00:00:00",0));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            logger.debug(r.getDate("birthday").toString());
            if (DateUtil.getYear(r.getDate("birthday")) != 1900)
                setAttr("ro", "readonly");
            setAttr("memberInfo", r);
            render("editMemInfo");
        } else renderNull();
    }

    /**
     * 保存会员信息
     */
    @MethodName(name = "保存会员信息")
    @Before(FavoritesValidator.class)
    public void saveMemInfo() {
        MemberInfo memberInfo = getModel(MemberInfo.class);
        try {
            memberService.update(memberInfo,getSessionAttr("appId"));
            render("ok.html");
        } catch (RuntimeException r) {
            if (r.getMessage().equals("手机号已使用")) {
                setAttr("error", "0");
                render("error.html");
            }
        }
    }

    /**
     * 收藏条码生成二维码
     */
    @MethodName(name = "收藏条码生成二维码")
    @Before({CodeValidator.class, FavoritesValidator.class})
    public void createOrder() {
        String codes = getPara("codes");
        String[] code = codes.split("\\*");
        List list = new ArrayList();
        for (int i = 0; i < code.length; i++) {
            logger.info(code[i]);
            if (!list.contains(code[i]))
                list.add(code[i]);
        }
        logger.info(StringUtils.join(list, "*"));
        setAttr("codes", StringUtils.join(list, "*"));
        setAttr("size", list.size() - 1);
    }

    @Override
    public ApiConfig getApiConfig() {
        logger.info("获取公众号信息");
        logger.info("appId:-----"+getSessionAttr("appId"));
        return WeiXinUtils.getApiConfig(getSessionAttr("appId"));
    }

    /**
     * 获取系统用户二维码
     */
    @MethodName(name = "获取系统用户二维码")
    @Before(FavoritesValidator.class)
    public void getUserQrcode() {
        String openId = getSessionAttr("openId");
        String time = DateUtil.DateToString(new Date(), 0);
        Record r = memberService.find(openId);
        String qrcode = r.getStr("memmernum") + "," + time;
        if (getParaToInt("type") == 5)
            qrcode = Base64Utils.encode(r.getStr("memmernum") + "," + time);
        ApiResult apiResult = UserApi.getUserInfo(openId);
        JSONObject jsonObject = JSON.parseObject(apiResult.getJson());
        String nickName = jsonObject.getString("nickname");
        String headimgurl = jsonObject.getString("headimgurl");
        setAttr("memmbertype", r.getStr("card_name"));
        setAttr("money", r.getBigDecimal("money"));
        setAttr("memmernum", r.getStr("memmernum"));
        setAttr("qrcode", "#" + qrcode);
        setAttr("nickName", nickName);
        setAttr("headimgurl", headimgurl);
        if (getParaToInt("type") == 1)
            render("memberCode");
        else if (getParaToInt("type") == 2) render("recharge");
    }

    /**
     * 获取个人订单列表
     */
    @MethodName(name = "获取个人订单列表")
    public void getOrders() {
        String openId = getSessionAttr("openId");

        logger.info("获取到的openid:--->" + openId);
        if (StringUtils.isNotBlank(openId)) {
            Record r = memberService.find(openId);
            if (r == null) throw new RuntimeException("1/0");
            else {
                Page pages = orderService.getOrders(memberService.find(openId).getInt("id").toString(),
                        getParaToInt("page", 1),
                        getParaToInt("size", 10));
                setAttr("page", pages);
            }
            render("orderList");
        }
    }

    /**
     * 获取个人订单
     */
    @MethodName(name = "获取个人订单")
    public void getOrdersJson() {
        String openId = getSessionAttr("openId");
        logger.info("获取到的openid:--->" + openId);
        if (openId != null) {
            Page pages = orderService.getOrders(memberService.find(openId.toString()).getInt("id").toString(),
                    getParaToInt("page", 1),
                    getParaToInt("size", 10));
            renderJson(pages);
        } else renderNull();
    }
    /**
     * 获取订单信息
     */
    @MethodName(name = "获取订单信息")
    @Before(FavoritesValidator.class)
    public void getOrderInfo() {
        Object json = orderService.getOrderInfo(getParaToInt("id"));
        setAttr("order", json);
        render("orderInfo");
    }

    /**
     * 获取订单二维码
     */
    @MethodName(name = "获取订单二维码")
    @Before(FavoritesValidator.class)
    public void getOrderQrcode() {
        Object json = orderService.getOrderInfo(getParaToInt("id"));
        setAttr("order", json);
        render("orderQRCode");
    }

    /**
     * 获取会员编号
     */
    @MethodName(name = "获取会员编号")
    @Before(FavoritesValidator.class)
    public void getNumForMember() {
        renderJson("num", memberService.getNumForMember());
    }
}
