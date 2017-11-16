package com.app.wxapi.model;


import com.jfinal.plugin.activerecord.Model;

public class AppInfo extends Model<AppInfo> {
    public static final AppInfo dao = new AppInfo().dao();
}
