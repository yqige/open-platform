package com.app.wxapi.validator;

import com.app.wxapi.utils.code.ErrorUtils;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
 * Created by ruieliu on 2017/6/29.
 */
public class ResValidator extends Validator {
    @Override
    protected void validate(Controller controller) {
        validateRequiredString("nonce", "nonceMsg", "请输入nonce!");
        validateRequiredString("timestamp", "timestampMsg", "请输入timestamp!");
        validateRequiredString("signature", "signatureMsg", "请输入signature!");
    }

    @Override
    protected void handleError(Controller controller) {
        ErrorUtils.handleError(controller);
    }
}
