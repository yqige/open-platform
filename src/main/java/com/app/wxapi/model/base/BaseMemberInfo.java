package com.app.wxapi.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseMemberInfo<M extends BaseMemberInfo<M>> extends Model<M> implements IBean {

    public M setId(java.lang.Integer id) {
        set("id", id);
        return (M) this;
    }

    public java.lang.Integer getId() {
        return get("id");
    }

    public M setMemmernum(java.lang.String memmernum) {
        set("memmernum", memmernum);
        return (M) this;
    }

    public java.lang.String getMemmernum() {
        return get("memmernum");
    }

    public M setMemmbertype(java.lang.String memmbertype) {
        set("memmbertype", memmbertype);
        return (M) this;
    }

    public java.lang.String getMemmbertype() {
        return get("memmbertype");
    }

    public M setMemmername(java.lang.String memmername) {
        set("memmername", memmername);
        return (M) this;
    }

    public java.lang.String getMemmername() {
        return get("memmername");
    }

    public M setPhone(java.lang.String phone) {
        set("phone", phone);
        return (M) this;
    }

    public java.lang.String getPhone() {
        return get("phone");
    }

    public M setReferee(java.lang.String referee) {
        set("referee", referee);
        return (M) this;
    }

    public java.lang.String getReferee() {
        return get("referee");
    }

    public M setSex(java.lang.Integer sex) {
        set("sex", sex);
        return (M) this;
    }

    public java.lang.Integer getSex() {
        return get("sex");
    }

    public M setWxnum(java.lang.String wxnum) {
        set("wxnum", wxnum);
        return (M) this;
    }

    public java.lang.String getWxnum() {
        return get("wxnum");
    }

    public M setOpenid(java.lang.String openid) {
        set("openid", openid);
        return (M) this;
    }

    public java.lang.String getOpenid() {
        return get("openid");
    }

    public M setQq(java.lang.String qq) {
        set("qq", qq);
        return (M) this;
    }

    public java.lang.String getQq() {
        return get("qq");
    }

    public M setAdress(java.lang.String adress) {
        set("adress", adress);
        return (M) this;
    }

    public java.lang.String getAdress() {
        return get("adress");
    }

    public M setIdcard(java.lang.String idcard) {
        set("idcard", idcard);
        return (M) this;
    }

    public java.lang.String getIdcard() {
        return get("idcard");
    }

    public M setBirthday(java.util.Date birthday) {
        set("birthday", birthday);
        return (M) this;
    }

    public java.util.Date getBirthday() {
        return get("birthday");
    }

    public M setEmail(java.lang.String email) {
        set("email", email);
        return (M) this;
    }

    public java.lang.String getEmail() {
        return get("email");
    }

    public M setCareer(java.lang.String career) {
        set("career", career);
        return (M) this;
    }

    public java.lang.String getCareer() {
        return get("career");
    }

    public M setRemark(java.lang.String remark) {
        set("remark", remark);
        return (M) this;
    }

    public java.lang.String getRemark() {
        return get("remark");
    }

    public M setReceiveid(java.lang.String receiveid) {
        set("receiveid", receiveid);
        return (M) this;
    }

    public java.lang.String getReceiveid() {
        return get("receiveid");
    }

    public M setShopid(java.lang.String shopid) {
        set("shopid", shopid);
        return (M) this;
    }

    public java.lang.String getShopid() {
        return get("shopid");
    }

    public M setMembersPoint(java.lang.Integer membersPoint) {
        set("members_point", membersPoint);
        return (M) this;
    }

    public java.lang.Integer getMembersPoint() {
        return get("members_point");
    }

    public M setMoney(java.math.BigDecimal money) {
        set("money", money);
        return (M) this;
    }

    public java.math.BigDecimal getMoney() {
        return get("money");
    }

    public M setCreateDate(java.util.Date createDate) {
        set("create_date", createDate);
        return (M) this;
    }

    public java.util.Date getCreateDate() {
        return get("create_date");
    }

    public M setCreateBy(java.lang.String createBy) {
        set("create_by", createBy);
        return (M) this;
    }

    public java.lang.String getCreateBy() {
        return get("create_by");
    }

    public M setUpdateDate(java.util.Date updateDate) {
        set("update_date", updateDate);
        return (M) this;
    }

    public java.util.Date getUpdateDate() {
        return get("update_date");
    }

    public M setUpdateBy(java.lang.String updateBy) {
        set("update_by", updateBy);
        return (M) this;
    }

    public java.lang.String getUpdateBy() {
        return get("update_by");
    }

}