package com.app.common;

import com.app.wxapi.config.plugin.ControllerPlugin;
import com.app.wxapi.controller.WxMsgController;
import com.app.wxapi.directive.NumDirective;
import com.app.wxapi.handle.GlobalHandle;
import com.app.wxapi.interceptor.MenuInterceptor;
import com.app.wxapi.model._MappingKit;
import com.app.wxapi.service.MemberService;
import com.app.wxapi.utils.PayTypeUtils;
import com.app.wxapi.utils.WeiXinUtils;
import com.jfinal.config.*;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;

/**
 * 本 demo 仅表达最为粗浅的 jfinal 用法，更为有价值的实用的企业级用法
 * 详见 JFinal 俱乐部: http://jfinal.com/club
 * <p>
 * API引导式配置
 */
public class WeixinConfig extends JFinalConfig {
    static Log logger = Log.getLog("log_wx");

    /**
     * 配置常量
     */
    public void configConstant(Constants me) {
        // 加载少量必要配置，随后可用PropKit.get(...)获取值
        PropKit.use("a_little_config.properties");
        me.setDevMode(PropKit.getBoolean("devMode", false));
        me.setViewType(ViewType.JFINAL_TEMPLATE);
    }

    /**
     * 配置路由
     */
    public void configRoute(Routes me) {
        new ControllerPlugin(me).start();
    }

    public void configEngine(Engine me) {
        me.addSharedFunction("/common/_layout.html");
        me.addSharedFunction("/common/_paginate.html");
        me.addSharedFunction("/common/_orders_a.html");
        me.addSharedStaticMethod(PayTypeUtils.class);
        me.setDatePattern("yyyy-MM-dd HH:mm:ss");
        me.addDirective("num", new NumDirective());
        logger.info("当前模板路径："+me.getBaseTemplatePath());
    }

    public static DruidPlugin createDruidPlugin() {
        return new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());
    }

    /**
     * 配置插件
     */
    public void configPlugin(Plugins me) {
        // 配置C3p0数据库连接池插件
        DruidPlugin druidPlugin = createDruidPlugin();
        druidPlugin.setFilters("wall");
        me.add(druidPlugin);
        // 配置ActiveRecord插件
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
//        arp.setTransactionLevel(4);
        arp.setShowSql(true);
        arp.setBaseSqlTemplatePath(PathKit.getRootClassPath()+"/sql");
        arp.addSqlTemplate("all.sql");
        arp.setDevMode(PropKit.getBoolean("devMode"));
        // 所有映射在 MappingKit 中自动化搞定
        _MappingKit.mapping(arp);
        me.add(arp);

        logger.info("结束加载数据库配置，开始EhCache加载");
        me.add(new EhCachePlugin());
    }

    /**
     * 配置全局拦截器
     */
    public void configInterceptor(Interceptors me) {
        me.addGlobalActionInterceptor(new MenuInterceptor());
    }

    /**
     * 配置处理器
     */
    public void configHandler(Handlers me) {
        me.add(new GlobalHandle());
    }

    public void afterJFinalStart() {
        logger.info("开始加载公众号信息");
        WeiXinUtils.loadAppInfo();
        logger.info("开始初始化会员编号");
        MemberService.service.addQueue();
        logger.info("✅✅✅✅✅✅✅启动完成");
    }
}
