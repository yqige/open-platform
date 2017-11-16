package com.app.wxapi.service;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.platform.mvc.base.BaseService;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class OrderService extends BaseService {

    private static Logger log = Logger.getLogger(OrderService.class);

    /**
     * 缓存
     */
    public static final OrderService service = Enhancer.enhance(OrderService.class);
    public Page getOrders(String member_id, Integer page, Integer size) {
        Page<Record> pages = Db.paginate(page, size, Db.getSqlPara("order.getOrders", member_id));
        List<Object> list = new ArrayList<Object>();
        pages.getList().forEach(record->list.add(JSONObject.parse(record.toJson())));
        return pages;
    }

    public Object getOrderInfo(Integer id) {
        Record r = Db.findFirst(Db.getSqlPara("order.getOrderInfo", id));
        List<Record> rs = Db.find(Db.getSqlPara("order.getGoodsList", id));
        List payInfo =Db.find(Db.getSqlPara("order.payInfo",id));
        r.set("payInfo",payInfo);
        r.set("list", rs);
        return JSONObject.parse(r.toJson());
    }
}
