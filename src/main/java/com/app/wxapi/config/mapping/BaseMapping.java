package com.app.wxapi.config.mapping;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.platform.annotation.Table;
import com.platform.mvc.base.BaseModel;
import com.platform.mvc.base.BaseModelCache;
import com.platform.tools.ToolClassSearch;
import org.apache.log4j.Logger;

import java.util.List;

public class BaseMapping {

    private static Logger log = Logger.getLogger(BaseMapping.class);

    protected ActiveRecordPlugin arp;
    protected String configName;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void scan() {
        // 查询所有继承BaseModel、BaseModelCache的子类
        List<Class<?>> modelClasses = ToolClassSearch.search(BaseModel.class);
        modelClasses.addAll(ToolClassSearch.search(BaseModelCache.class));

        // 循环处理自动注册映射
        for (Class model : modelClasses) {
            // 剔除BaseModelCache
            if (model.getName().endsWith("BaseModelCache")) {
                log.info("剔除BaseModelCache");
                continue;
            }

            // 获取注解对象
            Table tableBind = (Table) model.getAnnotation(Table.class);
            if (tableBind == null) {
                log.warn(model.getName() + "继承了BaseModel，但是没有注解绑定表名，请检查是否已经手动绑定 ！！！");
                continue;
            }

            // 获取映射属性
            String dataSourceName = tableBind.dataSourceName().trim();
            String tableName = tableBind.tableName().trim();
            String pkName = tableBind.pkName().trim();
            if (dataSourceName.equals("") || tableName.equals("") || pkName.equals("")) {
                log.error(model.getName() + "注解错误，数据源、表名、主键名为空 ！！！");
                break;
            }

            // 映射注册
            if (configName == null) {
                log.error(model.getName() + "ActiveRecordPlugin configName不能为null ！！！");
                break;
            }
            if (arp == null) {
                log.error(model.getName() + "ActiveRecordPlugin不能为null ！！！");
                break;
            }
            if (dataSourceName.equals(configName)) {
                arp.addMapping(tableName, pkName, model);
                log.info("Model注册： model = " + model + ", tableName = " + tableName + ", pkName: " + pkName);
            }
        }
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public void setArp(ActiveRecordPlugin arp) {
        this.arp = arp;
    }

}
