package com.app.wxapi.validator;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
 * Created by ruieliu on 2017/6/29.
 */
public class CodeValidator extends Validator {
    @Override
    protected void validate(Controller controller) {
        validateRequiredString("codes", "codes", "请输入codes!");
    }

    @Override
    protected void handleError(Controller controller) {

    }
}
