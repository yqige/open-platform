package com.app.wxapi.validator;

import com.app.wxapi.utils.code.ErrorUtils;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.validate.Validator;

public class WxapiValidator extends Validator {
    @Override
    protected void validate(Controller c) {
        validateRequired("message", "message", "订单id不可为空!");
        String str = Db.queryStr("select value from dicts where name='随机数' and object='刷新appInfo' and field='刷新appInfo'");
        if(!str.equals(c.getPara("message")))
            addError("message", "非法请求");
    }

    @Override
    protected void handleError(Controller c) {
        ErrorUtils.handleError(c);
    }
}
