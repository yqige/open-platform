package com.app.wxapi.config.plugin;

import com.app.wxapi.controller.BaseController;
import com.app.wxapi.utils.Exclude;
import com.jfinal.config.Routes;
import com.jfinal.plugin.IPlugin;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import com.jfinal.weixin.sdk.jfinal.MsgControllerAdapter;
import com.platform.annotation.Controller;
import com.platform.tools.ToolClassSearch;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 扫描Controller上的注解，绑定Controller和controllerKey
 *
 * @author Leo
 */
public class ControllerPlugin implements IPlugin {

    protected final Logger log = Logger.getLogger(getClass());
    public static List<String>excludeList=new ArrayList<>();
    static {
        excludeList.add(".css");
        excludeList.add(".js");
        excludeList.add(".woff2");
        excludeList.add(".ttf");
        excludeList.add(".woff");
        excludeList.add(".txt");
    }
    private Routes routes;

    public ControllerPlugin(Routes routes) {
        this.routes = routes;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public boolean start() {
        // 查询所有继承BaseController的子类
        List<Class<?>> controllerClasses = ToolClassSearch.search(BaseController.class);
        List<Class<?>> controllerClasses1 = ToolClassSearch.search(ApiController.class);
        List<Class<?>> controllerClasses2 = ToolClassSearch.search(MsgControllerAdapter.class);
        controllerClasses.addAll(controllerClasses1);
        controllerClasses.addAll(controllerClasses2);
        // 循环处理自动注册映射
        for (Class controller : controllerClasses) {
            // 获取注解对象
            Controller controllerBind = (Controller) controller.getAnnotation(Controller.class);
            if (controllerBind == null) {
                log.warn(controller.getName() + "继承了BaseController，但是没有注解绑定映射路径，请检查是否已经手动绑定 ！！！");
                continue;
            }

            // 获取映射路径数组
            String[] controllerKeys = controllerBind.controllerKey();
            String key="";
            for (String controllerKey : controllerKeys) {
                controllerKey = controllerKey.trim();
                key=controllerKey;
                if (controllerKey.equals("")) {
                    log.error(controller.getName() + "注解错误，映射路径为空");
                    continue;
                }
                // 注册映射
                routes.add(controllerKey, controller);
                log.debug("Controller注册： controller = " + controller + ", controllerKey = " + controllerKey);
            }
            Method[] mts = controller.getMethods();
            for (Method method:mts){
                Exclude at = method.getAnnotation(Exclude.class);
                if (at!=null)
                excludeList.add(key+"/"+method.getName());
            }
        }
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
    public static boolean contains (String str){
        final boolean[] isPass = {false};
        excludeList.forEach((s)->{
            if(str.indexOf(s)>-1)
                isPass[0] = true;
        });
        return isPass[0];
    }
}
