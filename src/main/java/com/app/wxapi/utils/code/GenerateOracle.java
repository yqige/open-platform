package com.app.wxapi.utils.code;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.platform.constant.ConstantInit;
import com.platform.tools.ToolDataBase;
import com.platform.tools.ToolSqlXml;
import com.platform.tools.ToolString;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 定制Oracle下的代码生成
 *
 * @author Leo
 */
public class GenerateOracle extends GenerateBase {

    private static Logger log = Logger.getLogger(GenerateOracle.class);

    /**
     * 循环生成文件
     *
     * @throws IOException
     */
    public static void generate(String[][] tabArr, Map<String, Boolean> map) throws IOException {
        tableArr = tabArr;
//		log.info("启动ConfigCore start ......");
//    	new ConfigCore();
//    	log.info("启动ConfigCore end ......");

        String db_type = PropKit.get(ConstantInit.db_type_key);
        log.info("db_type = " + db_type);
        if (!db_type.equals(ConstantInit.db_type_oracle)) {
            throw new RuntimeException("请设置init.properties配置文件db.type = oracle");
        }

        GenerateBase base = new GenerateOracle();
        for (int i = 0; i < tableArr.length; i++) {
            // 数据源名称
            String dataSource = tableArr[i][0];
            // 表名
            String tableName = tableArr[i][1];
            // 主键
            String pkName = tableArr[i][2];
            // 类名
            String className = tableArr[i][3];
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

            // 7.生成视图文件
            if (map.get("view")) {
                base.view(classNameSmall, tableName, colunmList);
            }
            if (map.get("list")) {
                base.list(classNameSmall, tableName, colunmList);
            }
            if (map.get("edit")) {
                base.edit(classNameSmall, tableName, colunmList);
            }
            if (map.get("add")) {
                base.add(classNameSmall, tableName, colunmList);
            }
        }

        System.exit(0);
    }

    @Override
    public List<TableColumnDto> getColunm(String tableName) {
        String dbUser = ToolDataBase.getDbInfo().getUserName();

        List<TableColumnDto> list = new ArrayList<TableColumnDto>();

        String tableDesc = Db.use(ConstantInit.db_dataSource_main).findFirst(ToolSqlXml.getSql("platform.oracle.getTableComments"), dbUser, tableName).getStr("COMMENTS");

        Map<String, String> columnJavaTypeMap = getJavaType(tableName);

        List<Record> listColumnComments = Db.use(ConstantInit.db_dataSource_main).find(ToolSqlXml.getSql("platform.oracle.getColumnComments"), dbUser, tableName, tableName);
        for (Record record : listColumnComments) {
            String column_name = record.getStr("COLUMN_NAME").toLowerCase();
            String column_type = record.getStr("DATA_TYPE").toLowerCase();
            BigDecimal column_length = record.getBigDecimal("DATA_LENGTH");
            String comments = record.getStr("COMMENTS");

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
            table.setColumn_length(column_length.toString());
            table.setColumn_desc(comments);

            table.setColumn_className(columnJavaTypeMap.get(column_name.toLowerCase()));

            list.add(table);
        }

        return list;
    }


}
