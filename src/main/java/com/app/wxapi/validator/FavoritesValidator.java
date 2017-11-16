package com.app.wxapi.validator;

import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.validate.Validator;

/**
 * Created by ruieliu on 2017/7/2.
 */
public class FavoritesValidator extends Validator {
    static Log logger = Log.getLog("log_wx");
    @Override
    protected void validate(Controller controller) {
        if (controller.getRequest().getRequestURI().indexOf("getNumForMember") > -1)
            if (!controller.getRequest().getRemoteHost().equals(PropKit.get("ip")))
                addError("error", "请求IP非法");
        logger.info(controller.getRequest().getRequestURI() + "----" + controller.getRequest().getRemoteHost());
    }

    @Override
    protected void handleError(Controller controller) {
        logger.info("FavoritesValidator_error：" + controller.getAttrForStr(controller.getAttrNames().nextElement()));
        controller.renderNull();
    }
}
