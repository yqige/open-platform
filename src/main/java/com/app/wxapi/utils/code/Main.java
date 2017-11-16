package com.app.wxapi.utils.code;

import com.app.wxapi.config.run.ConfigCore;
import com.jfinal.kit.PropKit;
import com.platform.constant.ConstantInit;
import com.platform.tools.ToolDirFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * 自动生成模板，一般项目初始化执行，后续开发中慎用，防止文件覆盖 Created by leo on 2015/12/23.
 */
public class Main {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Main.class);

    /**
     * <pre>
     * 需要自动生成模板的表
     *
     * 二维数组说明：
     *
     * 数据源（默认可以是null）、
     * 表名、
     * 主键名（默认可以是null）、
     * 类名（不包含后缀.java）
     * </pre>
     */
    public static String[][] tableArr = {
            {null, "crm_member_info", null, "WalletRecord"}
    };

    public static void main(String[] args) throws IOException {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        // 以下是类文件
        map.put("model", false);// 模型类,true 生成,false 不生成
        map.put("service", false);// 业务类
        map.put("controller", false);// 控制类
        map.put("validator", false);// 验证类
        map.put("dto", true);// 验证类
        map.put("sql", false);// sql.xml
        map.put("operatorSQL", false);// Operator sql
        // 以下是页面
//		map.put("add", false);// 增加页面
//		map.put("edit", false);// 编辑页面
//		map.put("list", false);// 列表页面
//		map.put("view", false);// 查看页面

        String cl = ToolDirFile.getClassesPath();
        System.out.println(cl);
        log.info("启动ConfigCore start ......");
        new ConfigCore();
        log.info("启动ConfigCore end ......");
        String db_type = PropKit.get(ConstantInit.db_type_key);
        if (db_type.equals("mysql")) {
            GenerateMySQL.generate(tableArr, map);
        } else if (db_type.equals("oracle")) {
            GenerateOracle.generate(tableArr, map);
        }
        log.info("自动生成代码完成");
        log.info("文件输出目录：" + System.getProperty("user.dir"));
        System.exit(0);
    }

}
