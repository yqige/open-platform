package com.app.wxapi.interceptor;

import com.app.wxapi.render.CusErrorRender;
import com.app.wxapi.utils.MethodName;
import com.app.wxapi.utils.StringUtils;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Const;
import com.jfinal.core.Controller;
import com.jfinal.render.Render;

public class MenuInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        System.out.println("Before method invoking");
        Controller c = inv.getController();
        MethodName mn = null;
        try {
            mn = inv.getMethod().getAnnotation(MethodName.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            inv.invoke();
            Render r = c.getRender();
            if(r!=null){
                if(r.getView()!=null&&(!r.getView().endsWith(".jsp")&&!r.getView().endsWith(Const.DEFAULT_VIEW_EXTENSION)))
                r.setView(r.getView()+ Const.DEFAULT_VIEW_EXTENSION);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("After method invoking");
            //判断是否ajax请求
            String header = c.getHeader("X-Requested-With");
            boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(header);
            String msg = formatException(e);
            if ("1/0".equals(msg)) c.render(new CusErrorRender(404, "/favorites/error.html"));
            else if (isAjax) {
                msg = new StringBuilder().append("{\"status\":\"0\",\"msg\":\"")
                        .append(msg).append("\"}").toString();
                c.renderJson(msg);
            } else {
                String redirectUrl = c.getHeader("referer");
                if (StringUtils.isBlank(redirectUrl)) {
                    redirectUrl = mn == null ? inv.getMethodName() : mn.name();
                }
                c.setAttr("message", msg);
                c.setAttr("redirectUrl", redirectUrl);
                c.render("/include/failed.html");
            }
        }

    }

    /**
     * 格式化异常信息，用于友好响应用户
     *
     * @param e
     * @return
     */
    private static String formatException(Exception e) {
        String message = null;
        Throwable ourCause = e;
        while ((ourCause = e.getCause()) != null) {
            e = (Exception) ourCause;
        }
        String eClassName = e.getClass().getName();
        //一些常见异常提示
        if ("java.lang.NumberFormatException".equals(eClassName)) {
            message = "请输入正确的数字";
        } else if ("java.lang.NullPointerException".equals(eClassName)) {
            message = "信息不完整，操作失败";
        } else if (e instanceof RuntimeException) {
            message = e.getMessage();
            if (StringUtils.isBlank(message)) message = e.toString();
        }

        //获取默认异常提示
        if (StringUtils.isBlank(message)) {
            message = "系统繁忙,请稍后再试";
        }
        //替换特殊字符
        message = message.replaceAll("\"", "'");
        return message;
    }
}
