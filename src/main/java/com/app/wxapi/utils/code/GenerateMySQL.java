package com.app.wxapi.utils.code;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.platform.constant.ConstantInit;
import com.platform.dto.DataBase;
import com.platform.tools.ToolDataBase;
import com.platform.tools.ToolString;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 定制MySQL下的代码生成
 *
 * @author Leo
 */
public class GenerateMySQL extends GenerateBase {

    private static Logger log = Logger.getLogger(GenerateMySQL.class);

    /**
     * <pre>
     * 循环生成文件
     * 二维数组说明：
     *
     * 数据源（默认可以是null）、
     * 表名、
     * 主键名（默认可以是null）、
     * 类名（不包含后缀.java）
     * </pre>
     *
     * @throws IOException
     */
    public static void generate(String[][] tabArr, Map<String, Boolean> map) throws IOException {
        tableArr = tabArr;
        String db_type = PropKit.get(ConstantInit.db_type_key);
        log.info("db_type = " + db_type);
        if (!db_type.equals(ConstantInit.db_type_mysql)) {
            throw new RuntimeException("请设置init.properties配置文件db.type = mysql");
        }

        log.info("configPlugin 配置Druid数据库连接池连接属性");
        DataBase db = ToolDataBase.getDbInfo();
        String username = db.getUserName();
        String password = db.getPassWord();
        String jdbcUrl = db.getJdbcUrl();
        String dbName = ToolDataBase.getDbInfo().getDbName();
        jdbcUrl = jdbcUrl.replace(dbName, "information_schema");
        DruidPlugin druidPluginIS = new DruidPlugin(jdbcUrl, username, password, "com.mysql.jdbc.Driver");
        druidPluginIS.start();

        log.info("configPlugin 配置ActiveRecord插件");
        ActiveRecordPlugin arpIS = new ActiveRecordPlugin("information_schema", druidPluginIS);
        arpIS.setDevMode(true); // 设置开发模式
        arpIS.setShowSql(true); // 是否显示SQL
        arpIS.setContainerFactory(new CaseInsensitiveContainerFactory(true));// 大小写不敏感
        arpIS.setDialect(new MysqlDialect());
        arpIS.start();

        GenerateBase base = new GenerateMySQL();
        for (String[] element : tableArr) {
            // 数据源名称
            String dataSource = element[0];
            // 表名
            String tableName = element[1];
            // 主键
            String pkName = element[2];
            // 类名
            String className = element[3];
            // 类名首字母小写
            String classNameSmall = ToolString.toLowerCaseFirstOne(className);

            List<TableColumnDto> colunmList = base.getColunm(tableName);

            // 1.生成sql文件
            if (map.get("sql")) {
                base.sql(classNameSmall, tableName, colunmList);
            }

            // 2.生成model
            if (map.get("model")) {
                base.model(className, classNameSmall, dataSource, tableName, pkName, colunmList);
            }
            // 3.生成validator
            if (map.get("validator")) {
                base.validator(className, classNameSmall, colunmList);
            }
            // 4.生成controller
            if (map.get("controller")) {
                base.controller(className, classNameSmall, tableName, colunmList);
            }
            // 5.生成service
            if (map.get("service")) {
                base.service(className, classNameSmall, colunmList);
            }
            // 6.生成DTO
            if (map.get("dto")) {
                base.dto(className, classNameSmall, dataSource, tableName, colunmList);
            }

            // 7.生成operator SQL
            if (map.get("operatorSQL")) {
                base.operatorSQL(classNameSmall, tableName, colunmList);
            }

            // 8.生成视图文件
//			if (map.get("view")) {
//				base.view(classNameSmall, tableName, colunmList);
//			}
//			if (map.get("list")) {
//				base.list(classNameSmall, tableName, colunmList);
//			}
//			if (map.get("edit")) {
//				base.edit(classNameSmall, tableName, colunmList);
//			}
//			if (map.get("add")) {
//				base.add(classNameSmall, tableName, colunmList);
//			}
        }
    }

    @Override
    public List<TableColumnDto> getColunm(String tableName) {
        List<TableColumnDto> list = new ArrayList<TableColumnDto>();

        String dbName = ToolDataBase.getDbInfo().getDbName();

        String tableDesc = Db.use("information_schema")
                .findFirst("select * from tables where table_schema = ? and table_name = ?", dbName, tableName).getStr("table_COMMENT");
        List<Record> listColumn = Db.use("information_schema").find("select * from columns where table_schema = ? and table_name = ?",
                dbName, tableName);

        Map<String, String> columnJavaTypeMap = getJavaType(tableName);

        for (Record record : listColumn) {
            String column_name = record.getStr("column_name");
            String column_type = record.getStr("column_type");
            log.info(column_name);
            log.info(record.get("CHARACTER_MAXIMUM_LENGTH"));
            String character_maximum_length = record.get("CHARACTER_MAXIMUM_LENGTH") == null ? "10"
                    : record.get("CHARACTER_MAXIMUM_LENGTH").toString();
            String column_comment = record.getStr("COLUMN_COMMENT");

            // 需要跳过的字段
            if ("xxx".equals(column_name) || "yyy".equals(column_name) || "zzz".equals(column_name)) {
                continue;
            }

            TableColumnDto table = new TableColumnDto();
            table.setTable_name(tableName);
            table.setTable_desc(tableDesc);

            table.setColumn_name(column_name);
            table.setColumn_name_upperCaseFirstOne(ToolString.toUpperCaseFirstOne(column_name));
            table.setColumn_type(column_type);
            table.setColumn_length(character_maximum_length);
            table.setColumn_desc(column_comment);

            table.setColumn_className(columnJavaTypeMap.get(column_name.toLowerCase()));

            list.add(table);
        }

        return list;
    }

}
