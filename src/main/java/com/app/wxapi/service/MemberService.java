package com.app.wxapi.service;

import com.app.wxapi.model.MemberCards;
import com.app.wxapi.model.memberinfo.MemberInfo;
import com.app.wxapi.utils.DateUtil;
import com.app.wxapi.utils.StringUtils;
import com.app.wxapi.utils.WeiXinUtils;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.platform.mvc.base.BaseService;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by ruieliu on 2017/6/30.
 */
public class MemberService extends BaseService {
    static Log logger = Log.getLog("log_wx");
    static ArrayBlockingQueue queue=new ArrayBlockingQueue(100);
    /**
     * 缓存
     */
    public static final MemberService service = Enhancer.enhance(MemberService.class);
//    public static Cache c = Redis.use("numCachingFilter_wx");
    public static DecimalFormat df=new DecimalFormat("00000000");

    /**
     * todo
     * @param openid
     * @return
     */
    public Record find(String openid) {
        return Db.findFirst(Db.getSqlPara("member.find",openid));
    }

    public void update(MemberInfo memberInfo,String appId) {
        Kv cond =Kv.by("id<> ",memberInfo.getId())
                .set("phone= ",memberInfo.getPhone())
                .set("app_id= ",appId);
        List<MemberInfo> l = memberInfo.find(Db.getSqlPara("member.list",Kv.by("cond",cond)));
        if(l.size()>0)throw new RuntimeException("手机号已使用");
        MemberInfo mi = memberInfo.findById(memberInfo.getId());
        mi.setMemmername(memberInfo.getMemmername() == null ? "" : memberInfo.getMemmername());
        mi.setAdress(memberInfo.getAdress());
        mi.setPhone(memberInfo.getPhone());
        mi.setIdcard(memberInfo.getIdcard());
        mi.setBirthday(memberInfo.getBirthday());
        mi.setSex(memberInfo.getSex());
        mi.setQq(memberInfo.getQq());
        mi.setEmail(memberInfo.getEmail());
        mi.setCareer(memberInfo.getCareer());
        mi.setUpdate_by(memberInfo.getOpenid() + "");
        mi.setUpdate_date(new Timestamp(System.currentTimeMillis()));
        mi.update();
    }

    public int save(String phone, String tenant_id, String receiveid, String openid,String status, String appId) {
        logger.info("开始新增会员");
        Record record = new Record();
        record.set("memmername", " ");
        record.set("app_id", appId);
        record.set("phone", phone);
        record.set("tenant_id", tenant_id);
        record.set("receiveid", receiveid);
        record.set("memmernum", getNumForMember());
        record.set("memmbertype", WeiXinUtils.getMemberType(appId));
        record.set("openid", openid);
        record.set("money", new BigDecimal("0.00"));
        record.set("create_date", new Timestamp(System.currentTimeMillis()));
        record.set("birthday", Timestamp.valueOf("1900-01-01 00:00:00"));
        record.set("status", status);
        Db.save("crm_member_info", record);
        try {
            MemberCards mc = MemberCards.dao.findById(PropKit.getInt("memmber_id"));
            mc.setUsedQuantity(mc.getUsedQuantity() == null ? 1 : mc.getUsedQuantity() + 1);
            mc.update();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return record.getLong("id").intValue();
    }

    @Before(Tx.class)
    public void update(String id, String openid, String appId) {
        String[] params = id.split(",");//第一个为时间戳
        if(id.startsWith("@")){//目前山东专用
            Boolean b = DateUtil.isBetween(new Date(), "2017-09-13 00:00:00", "2017-09-17 23:59:59");
            if(b){
                updateNo(openid,id.substring(1).split(",")[0],appId);
            }else updateNo(openid,"543",appId);
        }else {
            String phone=params[1];
            if(phone.equals("99999999999"))
                phone=null;
            Kv cond =Kv.by("phone= ",phone).set("app_id= ",appId);
            Kv cond1 =Kv.by("openid= ",openid).set("app_id= ",appId);
            Record r = Db.findFirst(Db.getSqlPara("member.list",Kv.by("cond",cond)));
            Record r1 = Db.findFirst(Db.getSqlPara("member.list",Kv.by("cond",cond1)));
            if(r1==null){
                if(r==null){
                    logger.info("1------------");
                    logger.info("phone:"+phone);
                    int i = save(phone, params[2], params[3], openid,"0",appId);
                    logger.info("id:"+i);
                if (params.length==5&&StringUtils.isNotBlank(params[4])) {//携带了订单号
                    linkOrder(i,params[4]);
                }
                }else {
                    logger.info("2------------");
                    r.set("openid",openid);
                    Db.update("crm_member_info",r);
                }
            }else {
                if (r == null) {
                    int i=0;
                    logger.info("3------------");
                    if (phone!=null){
                        r1.set("phone", phone);
                        i=1;
                    }

                    if (r1.getStr("tenant_id") == null){
                        r1.set("tenant_id", params[2]);
                        i=1;
                    }
                    if (r1.getStr("receiveid") == null){
                        r1.set("receiveid", params[3]);
                        i=1;
                    }
                    if(i==1)
                        Db.update("crm_member_info", r1);
                } else {
                    if (r.getInt("id") == r1.getInt("id")){
                        logger.info("3-1-----------");
                        if (r.getStr("tenant_id") == null)
                            r.set("tenant_id", params[2]);
                        if (r.getStr("receiveid") == null)
                            r.set("receiveid", params[3]);
                        Db.update("crm_member_info", r);
                    }
                    else {
                        if (StringUtils.isBlank(r1.getStr("phone"))) {
                            logger.info("4------------");
                            r1.set("status", "1");
                            r1.set("openid", null);
                            Db.update("crm_member_info", r1);
                            if (r.getStr("tenant_id") == null)
                                r.set("tenant_id", params[2]);
                            if (r.getStr("receiveid") == null)
                                r.set("receiveid", params[3]);
                            r.set("openid", openid);
                            Db.update("crm_member_info", r);

                        } else {
                            throw new RuntimeException("phone_error");
                        }
                    }

                }
            }}

    }

    /**
     * 不是通过扫码关注成为的会员
     *
     * @param openId
     */
    public void updateNo(String openId,String tenant_id,String appId) {
        logger.info("不是通过扫码关注成为的会员:"+openId);
        Record r = find(openId);
        if (r == null)//记得还原
            save(null, tenant_id, null, openId,"0",appId);
    }

    static String cacheName = "numCachingFilter_wx";
    public synchronized String getNumForMember() {
        try {
            StringBuffer fl = new StringBuffer("Q").append(DateUtil.DateToString(new Date(), 12));
            Object crm_num;
            try {
                crm_num = queue.remove();
            }catch (NoSuchElementException e){
                addQueue();
                crm_num = queue.remove();
            }
            logger.info("crm_num:"+crm_num);
            return fl.append(crm_num).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Q"+System.currentTimeMillis();
        }
    }
    public void linkOrder(int member_id,String order_id){
        logger.info("开始关联订单");
        HashMap hashMap = new HashMap();
        hashMap.put("member_id",String.valueOf(member_id));
        hashMap.put("order_id",order_id);
        new Thread(() -> {
            String jsonResult = HttpUtils.get(PropKit.get("linkOrder","http://www.baidu.com")+"/member/linkOrder", hashMap);
            logger.info(jsonResult);
        }).start();
    }
    @Before(Tx.class)
    public void addQueue(){
        Record record = Db.findById("crm_member_num", 3);
        String crm_num = record.getStr("crm_num");
        if(Long.valueOf(crm_num)>=Long.valueOf("99999999"))
            crm_num=df.format(1);
        Long temp=Long.valueOf(crm_num);
        record.set("crm_num", df.format(temp + 100l));
        Db.update("crm_member_num", record);
        for(Long l=temp;l<temp + 100l;l++)
            queue.add(df.format(l));
    }
}
