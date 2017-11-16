package com.demo.wxapi.model;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.weixin.model.Order;

/**
 * Created by ruieliu on 2017/7/3.
 */
public class MappingKit_ {
    public static void mapping(ActiveRecordPlugin arp) {
        arp.addMapping("b_order", Order.class);
    }
}
