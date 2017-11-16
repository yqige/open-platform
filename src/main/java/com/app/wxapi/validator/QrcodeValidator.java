package com.app.wxapi.validator;

import com.app.wxapi.utils.RegexUtils;
import com.app.wxapi.utils.StringUtils;
import com.app.wxapi.utils.code.ErrorUtils;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.validate.Validator;

import java.util.List;


public class QrcodeValidator extends Validator {
    @Override
    protected void validate(Controller c) {
        validateRequired(0, "phoneMsg", "请输入手机号!");
        if (StringUtils.isNotBlank(c.getPara(0)))
            if (!RegexUtils.match(RegexUtils.PHONE, c.getPara(0)))
                addError("phoneMsg", "手机号格式不对");
        validateRequired(1, "tenant_idMsg", "请输入门店🆔!");
        List<Record> tenant_ids = Db.find("select 1 from b_tenant where id=? or tenant_id=?", c.getPara(1), c.getPara(1));
        if (tenant_ids.size() == 0)
            addError("tenant_idMsg", "门店不存在");
        validateRequired(2, "receiveidMsg", "请输入扩展人🆔!");
        List<Record> receiveids = Db.find("select 1 from user_info where id=?", c.getPara(2));
        if (receiveids.size() == 0)
            addError("receiveidMsg", "扩展人不存在");
    }

    @Override
    protected void handleError(Controller c) {
        ErrorUtils.handleError(c);
    }
}
