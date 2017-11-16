package com.app.wxapi.model.memberinfo;

import com.platform.annotation.Table;
import com.platform.mvc.base.BaseModel;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 会员信息
 * 模型类，通过数据库表初始化
 */
@SuppressWarnings("unused")
@Table(tableName = "crm_member_info")
public class MemberInfo extends BaseModel<MemberInfo> {

    private static final long serialVersionUID = 1L;

    private static Logger log = Logger.getLogger(MemberInfo.class);

    public static final MemberInfo dao = new MemberInfo();

    /**
     * 主键
     * 类型：int(11)  长度：10
     */
    public static final String column_id = "id";

    /**
     * 会员卡号
     * 类型：varchar(60)  长度：60
     */
    public static final String column_memmernum = "memmernum";

    /**
     * 会员姓名
     * 类型：varchar(60)  长度：60
     */
    public static final String column_memmername = "memmername";

    /**
     * 性别
     * 类型：int(1)  长度：10
     */
    public static final String column_sex = "sex";

    /**
     * 手机号
     * 类型：varchar(13)  长度：13
     */
    public static final String column_phone = "phone";

    /**
     * 会员卡类型
     * 类型：varchar(60)  长度：60
     */
    public static final String column_memmbertype = "memmbertype";

    /**
     * 会员卡状态
     * 类型：int(1)  长度：10
     */
    public static final String column_status = "status";

    /**
     * 会员积分
     * 类型：decimal(11,2)  长度：10
     */
    public static final String column_members_point = "members_point";

    /**
     * 储值余额
     * 类型：decimal(11,2)  长度：10
     */
    public static final String column_money = "money";

    /**
     * 备注
     * 类型：longtext  长度：4294967295
     */
    public static final String column_remark = "remark";

    /**
     * 推荐人
     * 类型：varchar(60)  长度：60
     */
    public static final String column_referee = "referee";

    /**
     * 微信号
     * 类型：varchar(255)  长度：255
     */
    public static final String column_wxnum = "wxnum";

    /**
     * 微信openid
     * 类型：varchar(60)  长度：60
     */
    public static final String column_openid = "openid";

    /**
     * QQ
     * 类型：varchar(60)  长度：60
     */
    public static final String column_qq = "qq";

    /**
     * 联系地址
     * 类型：varchar(255)  长度：255
     */
    public static final String column_adress = "adress";

    /**
     * 身份证
     * 类型：varchar(60)  长度：60
     */
    public static final String column_idcard = "idcard";

    /**
     * 生日
     * 类型：datetime  长度：10
     */
    public static final String column_birthday = "birthday";

    /**
     * 邮箱
     * 类型：varchar(60)  长度：60
     */
    public static final String column_email = "email";

    /**
     * 职业
     * 类型：varchar(60)  长度：60
     */
    public static final String column_career = "career";

    /**
     * 拓展人
     * 类型：varchar(60)  长度：60
     */
    public static final String column_receiveid = "receiveid";

    /**
     * 开卡门店
     * 类型：varchar(60)  长度：60
     */
    public static final String column_tenant_id = "tenant_id";

    /**
     * 创建时间
     * 类型：datetime  长度：10
     */
    public static final String column_create_date = "create_date";

    /**
     * 创建人
     * 类型：varchar(60)  长度：60
     */
    public static final String column_create_by = "create_by";

    /**
     * 更新时间
     * 类型：datetime  长度：10
     */
    public static final String column_update_date = "update_date";

    /**
     * 更新人
     * 类型：varchar(60)  长度：60
     */
    public static final String column_update_by = "update_by";

    /**
     * 起始日期
     * 类型：datetime  长度：10
     */
    public static final String column_from_date = "from_date";

    /**
     * 终止日期
     * 类型：datetime  长度：10
     */
    public static final String column_end_date = "end_date";

    /**
     * 会员图像
     * 类型：varchar(60)  长度：60
     */
    public static final String column_mempic = "mempic";

    /**
     * sqlId : wxapi.memberInfo.splitPageFrom
     * 描述：分页from
     */
    public static final String sqlId_splitPage_from = "wxapi.memberInfo.splitPageFrom";

    public static final String sqlId_splitPage_select1 = "wxapi.memberInfo.splitPageSelect1";

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

    public MemberInfo(String phone, String tenant_id, String receiveid) {
        setPhone(phone);
        setTenant_id(tenant_id);
        setReceiveid(receiveid);
    }

    /**
     * @see MemberInfo#column_id
     */
    public void setId(Integer id) {
        set(column_id, id);
    }

    /**
     * @see MemberInfo#column_id
     */
    public Integer getId() {
        return get(column_id);
    }

    /**
     * @see MemberInfo#column_memmernum
     */
    public void setMemmernum(String memmernum) {
        set(column_memmernum, memmernum);
    }

    /**
     * @see MemberInfo#column_memmernum
     */
    public String getMemmernum() {
        return get(column_memmernum);
    }

    /**
     * @see MemberInfo#column_memmername
     */
    public void setMemmername(String memmername) {
        set(column_memmername, memmername);
    }

    /**
     * @see MemberInfo#column_memmername
     */
    public String getMemmername() {
        return get(column_memmername);
    }

    /**
     * @see MemberInfo#column_sex
     */
    public void setSex(Integer sex) {
        set(column_sex, sex);
    }

    /**
     * @see MemberInfo#column_sex
     */
    public Integer getSex() {
        return get(column_sex);
    }

    /**
     * @see MemberInfo#column_phone
     */
    public void setPhone(String phone) {
        set(column_phone, phone);
    }

    /**
     * @see MemberInfo#column_phone
     */
    public String getPhone() {
        return get(column_phone);
    }

    /**
     * @see MemberInfo#column_memmbertype
     */
    public void setMemmbertype(String memmbertype) {
        set(column_memmbertype, memmbertype);
    }

    /**
     * @see MemberInfo#column_memmbertype
     */
    public String getMemmbertype() {
        return get(column_memmbertype);
    }

    /**
     * @see MemberInfo#column_status
     */
    public void setStatus(Integer status) {
        set(column_status, status);
    }

    /**
     * @see MemberInfo#column_status
     */
    public Integer getStatus() {
        return get(column_status);
    }

    /**
     * @see MemberInfo#column_members_point
     */
    public void setMembers_point(BigDecimal members_point) {
        set(column_members_point, members_point);
    }

    /**
     * @see MemberInfo#column_members_point
     */
    public BigDecimal getMembers_point() {
        return get(column_members_point);
    }

    /**
     * @see MemberInfo#column_money
     */
    public void setMoney(BigDecimal money) {
        set(column_money, money);
    }

    /**
     * @see MemberInfo#column_money
     */
    public BigDecimal getMoney() {
        return get(column_money);
    }

    /**
     * @see MemberInfo#column_remark
     */
    public void setRemark(String remark) {
        set(column_remark, remark);
    }

    /**
     * @see MemberInfo#column_remark
     */
    public String getRemark() {
        return get(column_remark);
    }

    /**
     * @see MemberInfo#column_referee
     */
    public void setReferee(String referee) {
        set(column_referee, referee);
    }

    /**
     * @see MemberInfo#column_referee
     */
    public String getReferee() {
        return get(column_referee);
    }

    /**
     * @see MemberInfo#column_wxnum
     */
    public void setWxnum(String wxnum) {
        set(column_wxnum, wxnum);
    }

    /**
     * @see MemberInfo#column_wxnum
     */
    public String getWxnum() {
        return get(column_wxnum);
    }

    /**
     * @see MemberInfo#column_openid
     */
    public void setOpenid(String openid) {
        set(column_openid, openid);
    }

    /**
     * @see MemberInfo#column_openid
     */
    public String getOpenid() {
        return get(column_openid);
    }

    /**
     * @see MemberInfo#column_qq
     */
    public void setQq(String qq) {
        set(column_qq, qq);
    }

    /**
     * @see MemberInfo#column_qq
     */
    public String getQq() {
        return get(column_qq);
    }

    /**
     * @see MemberInfo#column_adress
     */
    public void setAdress(String adress) {
        set(column_adress, adress);
    }

    /**
     * @see MemberInfo#column_adress
     */
    public String getAdress() {
        return get(column_adress);
    }

    /**
     * @see MemberInfo#column_idcard
     */
    public void setIdcard(String idcard) {
        set(column_idcard, idcard);
    }

    /**
     * @see MemberInfo#column_idcard
     */
    public String getIdcard() {
        return get(column_idcard);
    }

    /**
     * @see MemberInfo#column_birthday
     */
    public void setBirthday(Timestamp birthday) {
        set(column_birthday, birthday);
    }

    /**
     * @see MemberInfo#column_birthday
     */
    public Timestamp getBirthday() {
        return get(column_birthday);
    }

    /**
     * @see MemberInfo#column_email
     */
    public void setEmail(String email) {
        set(column_email, email);
    }

    /**
     * @see MemberInfo#column_email
     */
    public String getEmail() {
        return get(column_email);
    }

    /**
     * @see MemberInfo#column_career
     */
    public void setCareer(String career) {
        set(column_career, career);
    }

    /**
     * @see MemberInfo#column_career
     */
    public String getCareer() {
        return get(column_career);
    }

    /**
     * @see MemberInfo#column_receiveid
     */
    public void setReceiveid(String receiveid) {
        set(column_receiveid, receiveid);
    }

    /**
     * @see MemberInfo#column_receiveid
     */
    public String getReceiveid() {
        return get(column_receiveid);
    }

    /**
     * @see MemberInfo#column_tenant_id
     */
    public void setTenant_id(String tenant_id) {
        set(column_tenant_id, tenant_id);
    }

    /**
     * @see MemberInfo#column_tenant_id
     */
    public String getTenant_id() {
        return get(column_tenant_id);
    }

    /**
     * @see MemberInfo#column_create_date
     */
    public void setCreate_date(Timestamp create_date) {
        set(column_create_date, create_date);
    }

    /**
     * @see MemberInfo#column_create_date
     */
    public Timestamp getCreate_date() {
        return get(column_create_date);
    }

    /**
     * @see MemberInfo#column_create_by
     */
    public void setCreate_by(String create_by) {
        set(column_create_by, create_by);
    }

    /**
     * @see MemberInfo#column_create_by
     */
    public String getCreate_by() {
        return get(column_create_by);
    }

    /**
     * @see MemberInfo#column_update_date
     */
    public void setUpdate_date(Timestamp update_date) {
        set(column_update_date, update_date);
    }

    /**
     * @see MemberInfo#column_update_date
     */
    public Timestamp getUpdate_date() {
        return get(column_update_date);
    }

    /**
     * @see MemberInfo#column_update_by
     */
    public void setUpdate_by(String update_by) {
        set(column_update_by, update_by);
    }

    /**
     * @see MemberInfo#column_update_by
     */
    public String getUpdate_by() {
        return get(column_update_by);
    }

    /**
     * @see MemberInfo#column_from_date
     */
    public void setFrom_date(Timestamp from_date) {
        set(column_from_date, from_date);
    }

    /**
     * @see MemberInfo#column_from_date
     */
    public Timestamp getFrom_date() {
        return get(column_from_date);
    }

    /**
     * @see MemberInfo#column_end_date
     */
    public void setEnd_date(Timestamp end_date) {
        set(column_end_date, end_date);
    }

    /**
     * @see MemberInfo#column_end_date
     */
    public Timestamp getEnd_date() {
        return get(column_end_date);
    }

    /**
     * @see MemberInfo#column_mempic
     */
    public void setMempic(String mempic) {
        set(column_mempic, mempic);
    }

    /**
     * @see MemberInfo#column_mempic
     */
    public String getMempic() {
        return get(column_mempic);
    }

    public MemberInfo() {
    }
}
