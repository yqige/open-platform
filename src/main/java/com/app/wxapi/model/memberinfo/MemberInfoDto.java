package com.app.wxapi.model.memberinfo;

import com.jfinal.plugin.activerecord.Record;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 会员信息 DTO
 */
public class MemberInfoDto extends Record implements Serializable {

    //TODO 可通过IDE工具生成该序列化版本号
    private static final long serialVersionUID = 1L;

    private static Logger log = Logger.getLogger(MemberInfo.class);

    private Integer id;
    private String memmernum;
    private String memmername;
    private Integer sex;
    private String phone;
    private String memmbertype;
    private Integer status;
    private BigDecimal members_point;
    private BigDecimal money;
    private String remark;
    private String referee;
    private String wxnum;
    private String openid;
    private String qq;
    private String adress;
    private String idcard;
    private Timestamp birthday;
    private String email;
    private String career;
    private String receiveid;
    private String tenant_id;
    private Timestamp create_date;
    private String create_by;
    private Timestamp update_date;
    private String update_by;
    private Timestamp from_date;
    private Timestamp end_date;
    private String mempic;

    public MemberInfoDto(String phone, String tenant_id, String receiveid) {
        setPhone(phone);
        setTenant_id(tenant_id);
        setReceiveid(receiveid);
    }

    public MemberInfoDto() {

    }

    public void setId(Integer id) {
        set("id", id);
        this.id = id;
    }

    public Integer getId() {
        return getLong("id").intValue();
    }

    public void setMemmernum(String memmernum) {
        this.memmernum = memmernum;
        set("memmernum", memmernum);
    }

    public String getMemmernum() {
        return memmernum;
    }

    public void setMemmername(String memmername) {
        this.memmername = memmername;
    }

    public String getMemmername() {
        return memmername;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getSex() {
        return sex;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        set("phone", phone);
    }

    public String getPhone() {
        return phone;
    }

    public void setMemmbertype(String memmbertype) {
        this.memmbertype = memmbertype;
        set("memmbertype", memmbertype);
    }

    public String getMemmbertype() {
        return memmbertype;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setMembers_point(BigDecimal members_point) {
        this.members_point = members_point;
    }

    public BigDecimal getMembers_point() {
        return members_point;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
        set("money", money);
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }

    public void setReferee(String referee) {
        this.referee = referee;
    }

    public String getReferee() {
        return referee;
    }

    public void setWxnum(String wxnum) {
        this.wxnum = wxnum;
    }

    public String getWxnum() {
        return wxnum;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getQq() {
        return qq;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getAdress() {
        return adress;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setBirthday(Timestamp birthday) {
        this.birthday = birthday;
    }

    public Timestamp getBirthday() {
        return birthday;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getCareer() {
        return career;
    }

    public void setReceiveid(String receiveid) {
        this.receiveid = receiveid;
        set("receiveid", receiveid);
    }

    public String getReceiveid() {
        return receiveid;
    }

    public void setTenant_id(String tenant_id) {
        this.tenant_id = tenant_id;
        set("tenant_id", tenant_id);
    }

    public String getTenant_id() {
        return tenant_id;
    }

    public void setCreate_date(Timestamp create_date) {
        this.create_date = create_date;
        set("create_date", create_date);
    }

    public Timestamp getCreate_date() {
        return create_date;
    }

    public void setCreate_by(String create_by) {
        this.create_by = create_by;
    }

    public String getCreate_by() {
        return create_by;
    }

    public void setUpdate_date(Timestamp update_date) {
        this.update_date = update_date;
    }

    public Timestamp getUpdate_date() {
        return update_date;
    }

    public void setUpdate_by(String update_by) {
        this.update_by = update_by;
    }

    public String getUpdate_by() {
        return update_by;
    }

    public void setFrom_date(Timestamp from_date) {
        this.from_date = from_date;
    }

    public Timestamp getFrom_date() {
        return from_date;
    }

    public void setEnd_date(Timestamp end_date) {
        this.end_date = end_date;
    }

    public Timestamp getEnd_date() {
        return end_date;
    }

    public void setMempic(String mempic) {
        this.mempic = mempic;
    }

    public String getMempic() {
        return mempic;
    }

}
