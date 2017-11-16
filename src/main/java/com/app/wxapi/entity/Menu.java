package com.app.wxapi.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ruieliu on 2017/6/29.
 */
public class Menu extends HashMap {
    List button = new ArrayList();
    List sublist = new ArrayList();

    public void getview(String name, String url) {
        HashMap hm = new HashMap();
        hm.put("type", "view");
        hm.put("name", name);
        hm.put("url", url);
        sublist.add(hm);
    }

    public void setsub(String sub) {
        this.sublist = new ArrayList();
        HashMap hm = new HashMap();
        hm.put("name", sub);
        hm.put("sub_button", sublist);
        button.add(hm);
    }

    public HashMap getButton() {
        HashMap hm = new HashMap();
        hm.put("button", button);
        return hm;
    }

    public void setview(String sub, String url) {
        HashMap hm = new HashMap();
        hm.put("name", sub);
        hm.put("type", "view");
        hm.put("url", url);
        button.add(hm);
    }
}
