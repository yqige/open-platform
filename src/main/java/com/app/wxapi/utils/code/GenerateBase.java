package com.app.wxapi.utils.code;

import com.jfinal.plugin.activerecord.DbKit;
import com.platform.tools.ToolRandoms;
import org.beetl.core.BeetlKit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.*;

/**
 * 简易辅助开发代码生成器
 * <p>
 * 描述：根据表，生成对应的.sql.xml文件、Model类、Service类、validator类、Controller类，
 * 不包含业务处理逻辑，考虑到开发的业务个性化，通用的生成意义不是太大，只做辅助开发
 *
 * @author Leo
 */
public abstract class GenerateBase {

    /**
     * 二维数组说明：
     * <p>
     * 数据源（默认可以是null）、
     * 表名、
     * 主键名（默认可以是null）、
     * 类名（不包含后缀.java）
     */
    public static String[][] tableArr = {
    };

    /**
     * 生成的包和类所在的源码根目录，比如src或者是weiXin
     */
    public static String srcFolder = Config.srcFolder;

    /**
     * 生成的文件存放的包，公共基础包
     * 描述：比如
     * platform所在的包就是com.platform
     * weixin所在的包就是com.weixin
     */
    public static String packageBase = Config.packageBase;

    /**
     * controller基础路径，例如
     *
     * @Controller(controllerKey = "/jf/platform/authImg") 中的platform
     * @Controller(controllerKey = "/jf/wx/authImg") 中的wx
     * <p>
     * render基础路径，例如
     * /platform/user/add.jsp 中的 platform
     * /weiXin/user/list.jsp 中的 weiXin
     */
    public static String basePath = Config.basePath;

    /**
     * 获取表的所有字段信息
     *
     * @param tableName
     * @return
     */
    public abstract List<TableColumnDto> getColunm(String tableName);

    /**
     * 获取表所有数据类型
     *
     * @param tableName
     * @return
     */
    public Map<String, String> getJavaType(String tableName) {
        //  获取字段数
        Map<String, String> columnJavaTypeMap = new HashMap<String, String>();

        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            conn = DbKit.getConfig().getConnection();
            st = conn.createStatement();
            String sql = "select * from " + tableName + " where 1 != 1 ";
            rs = st.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();

            int columns = rsmd.getColumnCount();
            for (int i = 1; i <= columns; i++) {
                //获取字段名
                String columnName = rsmd.getColumnName(i).toLowerCase();
                String columnClassName = rsmd.getColumnClassName(i);
                if (columnClassName.equals("[B")) {
                    columnClassName = "byte[]";
                }
                columnJavaTypeMap.put(columnName, columnClassName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbKit.getConfig().close(rs, st, conn);
        }

        return columnJavaTypeMap;
    }

    /**
     * 获取所有数据类型
     *
     * @param tableName
     * @return
     */
    public Set<String> getJataTypeList(String tableName) {
        Map<String, String> map = getJavaType(tableName);
        Set<String> keys = map.keySet();
        Set<String> typeSet = new HashSet<String>();
        for (String key : keys) {
            String type = map.get(key);
            if (type.equals("byte[]") || type.startsWith("java.lang.")) {
                continue;
            }
            typeSet.add(map.get(key));
        }
        return typeSet;
    }

    /**
     * 生成Model
     *
     * @param className
     * @param classNameSmall
     * @param dataSource
     * @param tableName
     * @param pkName
     */
    public void model(String className, String classNameSmall, String dataSource, String tableName, String pkName, List<TableColumnDto> colunmList) {
        Map<String, Object> paraMap = new HashMap<String, Object>();
        String packages = packageBase + "." + className.toLowerCase();
        paraMap.put("package", packages);
        paraMap.put("className", className);
        paraMap.put("dataSource", dataSource);
        paraMap.put("tableName", tableName);
        paraMap.put("pkName", pkName);
        paraMap.put("namespace", basePath + "." + classNameSmall);

        paraMap.put("colunmList", colunmList);
        paraMap.put("dataTypes", getJataTypeList(tableName));

        String filePath = System.getProperty("user.dir") + "/" + srcFolder + "/" + packages.replace(".", "/") + "/" + className + ".java";
        System.out.println(filePath);
        createFileByTemplete("model.html", paraMap, filePath);
    }

    /**
     * 生成DTO
     *
     * @param className
     * @param classNameSmall
     * @param dataSource
     * @param tableName
     * @param colunmList
     */
    public void dto(String className, String classNameSmall, String dataSource, String tableName, List<TableColumnDto> colunmList) {
        Map<String, Object> paraMap = new HashMap<String, Object>();
        String packages = packageBase + "." + className.toLowerCase();
        paraMap.put("package", packages);
        paraMap.put("className", className);
        paraMap.put("dataSource", dataSource);
        paraMap.put("tableName", tableName);

        paraMap.put("colunmList", colunmList);
        paraMap.put("dataTypes", getJataTypeList(tableName));

        String filePath = System.getProperty("user.dir") + "/" + srcFolder + "/" + packages.replace(".", "/") + "/" + className + "Dto.java";
        createFileByTemplete("dto.html", paraMap, filePath);
    }

    /**
     * 生成.sql.xml
     *
     * @param classNameSmall
     * @param tableName
     */
    public void sql(String classNameSmall, String tableName, List<TableColumnDto> columnList) {
        Map<String, Object> paraMap = new HashMap<String, Object>();
        String packages = packageBase + "." + classNameSmall.toLowerCase();
        paraMap.put("namespace", basePath + "." + classNameSmall);
        paraMap.put("tableName", tableName);
        paraMap.put("columnList", columnList);
        paraMap.put("table_desc", columnList.get(0).getTable_desc());
        StringBuffer sb = new StringBuffer("");
        for (TableColumnDto col : columnList) {
            sb.append("\t\t\t<% if(!isEmpty(" + col.getColumn_name() + ")){ %>\n\t\t\t" +
                    "	and o." + col.getColumn_name() + " like #'%$" + col.getColumn_name() + "$%'#\n\t\t\t" +
                    "<% } %><!--" + col.getColumn_desc() + " -->\n\n");
        }
        paraMap.put("whereParam", sb.toString());
        String filePath = System.getProperty("user.dir") + "/" + srcFolder + "/" + packages.replace(".", "/") + "/" + classNameSmall + ".sql.xml";
        createFileByTemplete("sql.html", paraMap, filePath);
    }

    /**
     * 生成模块功能SQL文件,用于插入菜单
     */
    public void operatorSQL(String classNameSmall, String tableName, List<TableColumnDto> columnList) {
        Map<String, Object> paraMap = new HashMap<String, Object>();
        String packages = packageBase + "." + classNameSmall.toLowerCase();
        paraMap.put("namespace", basePath + "." + classNameSmall);
        paraMap.put("tableName", tableName);
//		paraMap.put("moduleName", moduleName);
//		paraMap.put("moduleIds", moduleIds);
        paraMap.put("table_desc", columnList.get(0).getTable_desc());

        Map<String, String> opMap = new HashMap<String, String>();
        opMap.put("url", "/bs/" + basePath + "/" + classNameSmall + "/list");
        opMap.put("ids", ToolRandoms.getUuid(true));
        opMap.put("name", "列表");
        paraMap.put("list", opMap);

        opMap = new HashMap<String, String>();
        opMap.put("url", "/bs/" + basePath + "/" + classNameSmall + "/add");
        opMap.put("ids", ToolRandoms.getUuid(true));
        opMap.put("name", "新增");
        paraMap.put("add", opMap);

        opMap = new HashMap<String, String>();
        opMap.put("url", "/bs/" + basePath + "/" + classNameSmall + "/save");
        opMap.put("ids", ToolRandoms.getUuid(true));
        opMap.put("name", "保存");
        paraMap.put("save", opMap);

        opMap = new HashMap<String, String>();
        opMap.put("url", "/bs/" + basePath + "/" + classNameSmall + "/edit");
        opMap.put("ids", ToolRandoms.getUuid(true));
        opMap.put("name", "编辑");
        paraMap.put("edit", opMap);

        opMap = new HashMap<String, String>();
        opMap.put("url", "/bs/" + basePath + "/" + classNameSmall + "/update");
        opMap.put("ids", ToolRandoms.getUuid(true));
        opMap.put("name", "修改");
        paraMap.put("update", opMap);

        opMap = new HashMap<String, String>();
        opMap.put("url", "/bs/" + basePath + "/" + classNameSmall + "/view");
        opMap.put("ids", ToolRandoms.getUuid(true));
        opMap.put("name", "查看");
        paraMap.put("view", opMap);

        opMap = new HashMap<String, String>();
        opMap.put("url", "/bs/" + basePath + "/" + classNameSmall + "/delete");
        opMap.put("ids", ToolRandoms.getUuid(true));
        opMap.put("name", "删除");
        paraMap.put("delete", opMap);

        String filePath = System.getProperty("user.dir") + "/" + srcFolder + "/" + packages.replace(".", "/") + "/" + classNameSmall + "-operator.sql";
        createFileByTemplete("operator.sql.html", paraMap, filePath);

    }

    /**
     * 生成Controller
     *
     * @param className
     * @param classNameSmall
     */
    public void controller(String className, String classNameSmall, String tableName, List<TableColumnDto> colunmList) {
        Map<String, Object> paraMap = new HashMap<String, Object>();
        String packages = packageBase + "." + classNameSmall.toLowerCase();
        paraMap.put("package", packages);
        paraMap.put("className", className);
        paraMap.put("classNameSmall", classNameSmall);
        paraMap.put("basePath", basePath);
        paraMap.put("tableName", tableName);
        paraMap.put("colunmList", colunmList);
        String filePath = System.getProperty("user.dir") + "/" + srcFolder + "/" + packages.replace(".", "/") + "/" + className + "Controller.java";
        createFileByTemplete("controller.html", paraMap, filePath);
    }

    /**
     * 生成validator
     *
     * @param className
     * @param classNameSmall
     */
    public void validator(String className, String classNameSmall, List<TableColumnDto> colunmList) {
        Map<String, Object> paraMap = new HashMap<String, Object>();
        String packages = packageBase + "." + classNameSmall.toLowerCase();
        paraMap.put("package", packages);
        paraMap.put("className", className);
        paraMap.put("classNameSmall", classNameSmall);
        paraMap.put("basePath", basePath);
        paraMap.put("colunmList", colunmList);
        String filePath = System.getProperty("user.dir") + "/" + srcFolder + "/" + packages.replace(".", "/") + "/" + className + "Validator.java";
        createFileByTemplete("validator.html", paraMap, filePath);
    }

    /**
     * 生成Service
     *
     * @param className
     * @param classNameSmall
     */
    public void service(String className, String classNameSmall, List<TableColumnDto> colunmList) {
        Map<String, Object> paraMap = new HashMap<String, Object>();
        String packages = packageBase + "." + classNameSmall.toLowerCase();
        paraMap.put("package", packages);
        paraMap.put("className", className);
        paraMap.put("classNameSmall", classNameSmall);
        paraMap.put("namespace", srcFolder + "." + classNameSmall);
        paraMap.put("colunmList", colunmList);
        String filePath = System.getProperty("user.dir") + "/" + srcFolder + "/" + packages.replace(".", "/") + "/" + className + "Service.java";
        createFileByTemplete("service.html", paraMap, filePath);
    }

    /**
     * 生成add.html
     *
     * @param classNameSmall
     * @param tableName
     */
    public void add(String classNameSmall, String tableName, List<TableColumnDto> colunmList) {
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("classNameSmall", classNameSmall);
        paraMap.put("colunmList", colunmList);
        paraMap.put("basePath", basePath);
        paraMap.put("tableName", tableName);
        String filePath = System.getProperty("user.dir") + "/src/main/webapp/WEB-INF/view/bs/" + basePath + "/" + classNameSmall + "/add.html";
        createFileByTemplete("add.html", paraMap, filePath);
    }

    /**
     * 生成edit.html
     *
     * @param classNameSmall
     * @param tableName
     */
    public void edit(String classNameSmall, String tableName, List<TableColumnDto> colunmList) {
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("classNameSmall", classNameSmall);
        paraMap.put("colunmList", colunmList);
        paraMap.put("basePath", basePath);
        paraMap.put("tableName", tableName);
        String filePath = System.getProperty("user.dir") + "/src/main/webapp/WEB-INF/view/bs/" + basePath + "/" + classNameSmall + "/edit.html";
        createFileByTemplete("edit.html", paraMap, filePath);
    }

    /**
     * 生成list.html
     *
     * @param classNameSmall
     * @param tableName
     */
    public void list(String classNameSmall, String tableName, List<TableColumnDto> colunmList) {
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("classNameSmall", classNameSmall);
        paraMap.put("colunmList", colunmList);
        paraMap.put("basePath", basePath);
        paraMap.put("tableName", tableName);
        paraMap.put("authUrlAdd", "<% if(authUrl('/bs/" + basePath + "/" + classNameSmall + "/add')){ %>");
        paraMap.put("authUrlEdit", "<% if(authUrl('/bs/" + basePath + "/" + classNameSmall + "/edit')){ %>");
        paraMap.put("authUrlDelete", "<% if(authUrl('/bs/" + basePath + "/" + classNameSmall + "/delete')){ %>");
        paraMap.put("end", "<%}%>");
        paraMap.put("pageStart", "<% if(isEmpty(splitPage.list) == false){ %>\n" +
                "\t        <% for(o in splitPage.list ){%>");
        paraMap.put("pageInclude", "<% include(\"/common/splitPage-bs.html\", {\"formId\" : \"splitPage\", \"splitPageDiv\" :" +
                "\"splitPageDiv\"}){} %>");
        String filePath = System.getProperty("user.dir") + "/src/main/webapp/WEB-INF/view/bs/" + basePath + "/" + classNameSmall + "/list.html";
        createFileByTemplete("list.html", paraMap, filePath);
    }


    /**
     * 生成view.html
     *
     * @param classNameSmall
     * @param tableName
     */
    public void view(String classNameSmall, String tableName, List<TableColumnDto> colunmList) {
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("classNameSmall", classNameSmall);
        paraMap.put("colunmList", colunmList);
        paraMap.put("basePath", basePath);
        paraMap.put("tableName", tableName);
        String filePath = System.getProperty("user.dir") + "/src/main/webapp/WEB-INF/view/bs/" + basePath + "/" + classNameSmall + "/view.html";
        createFileByTemplete("view.html", paraMap, filePath);
    }

    /**
     * 根据具体模板生成文件
     *
     * @param templateFileName
     * @param paraMap
     * @param filePath
     */
    public void createFileByTemplete(String templateFileName, Map<String, Object> paraMap, String filePath) {
        try {
            Class<?> classes = Class.forName("com.demo.wxapi.utils.code.GenerateBase");

            InputStream controllerInputStream = classes.getResourceAsStream("template/" + templateFileName);
            int count = 0;
            while (count == 0) {
                count = controllerInputStream.available();
            }

            byte[] bytes = new byte[count];
            int readCount = 0; // 已经成功读取的字节的个数
            while (readCount < count) {
                readCount += controllerInputStream.read(bytes, readCount, count - readCount);
            }

            String template = new String(bytes);

            String javaSrc = BeetlKit.render(template, paraMap);

            File file = new File(filePath);

            File path = new File(file.getParent());
            if (!path.exists()) {
                path.mkdirs();
            }

            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            output.write(javaSrc);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

