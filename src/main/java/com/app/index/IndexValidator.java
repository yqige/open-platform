package com.app.index;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.validate.Validator;

/**
 * Created by ruieliu on 2017/6/30.
 */
public class IndexValidator extends Validator {
    static Log logger = Log.getLog(IndexValidator.class);

    @Override
    protected void validate(Controller controller) {
//        validateRequiredString("code", "codeMsg", "请输入code!");
    }

    @Override
    protected void handleError(Controller controller) {
        controller.renderJson("用户信息已过期...");
    }
}
