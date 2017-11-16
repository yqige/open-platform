package com.app.wxapi.service;

import com.app.wxapi.model.Favorites;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.platform.mvc.base.BaseService;
import weixin.popular.util.JsonUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ruieliu on 2017/6/27.
 */
public class FavoritesService extends BaseService {
    /**
     * 缓存
     */
    public static final FavoritesService service = Enhancer.enhance(FavoritesService.class);

    public List<Favorites> list(String para) {
        Kv cond =Kv.by("createUserid= ",para);
        List<Record> list = Db.find(Db.getSqlPara("favorites.list",Kv.by("cond",cond).set("orderBy","createDate desc")));
        List<Favorites> list1 = new ArrayList<>();
        list.forEach(record -> {
            list1.add(JsonUtil.parseObject(record.toJson(), Favorites.class));
        });
        return list1;
    }

    public void saveOrUpdate(String localId,String code,String openid) {
        Kv cond =Kv.by("createUserid= ",openid).set("barcode= ",code);
        Kv where= (Kv) cond.clone();
        Record r = Db.findFirst(Db.getSqlPara("favorites.list",Kv.by("cond",cond)));
        if (r == null) {
            Favorites f = new Favorites();
            f.setBarcode(code);
            f.setPic(localId);
            f.setCreateDate(new Date());
            f.setCreateUserid(openid);
            f.save();
        } else {
            cond.set("pic=",localId).set("modifyDate=",new Date());
            SqlPara ss = Db.getSqlPara("favorites.update", Kv.by("cond", cond).set("where",where));
            Db.update(ss.getSql(),ss.getPara());
        }

    }
}
