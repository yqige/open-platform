package com.app.wxapi.utils.code;

/**
 * 代码自动生成配置 Created by leo on 2015/12/23.
 */
public interface Config {
    /**
     * 生成的包和类所在的源码根目录，比如src,
     */
    public static final String srcFolder = "src";

    /**
     * 生成的文件存放的包，公共基础包 描述：比如 platform所在的包就是com.platform
     */
    public static final String packageBase = "com.app.wxapi.model";

    /**
     * controller基础路径，例如
     *
     * @Controller(controllerKey = "/bs/platform/authImg") 中的platform
     * @Controller(controllerKey = "/jf/wx/authImg") 中的wx
     * <p>
     * render基础路径，例如 /platform/user/add.jsp 中的
     * platform
     */
    public static final String basePath = "wxapi";

}
