package test;

import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.druid.DruidPlugin;
import com.platform.mvc.base.BaseService;

public class Test extends BaseService{
    public static Test test = Enhancer.enhance(Test.class);
    public void startDb(){
        DruidPlugin druidPlugin_ = new DruidPlugin("jdbc:mysql://192.168.199.235:3306/supplychain_test?characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull", "root", "abhwllm603" );
        log.info("configPlugin 配置ActiveRecord插件");
        ActiveRecordPlugin arp_ = new ActiveRecordPlugin("bb", druidPlugin_);
        arp_.setTransactionLevel(4);//事务隔离级别
        boolean devMode = true;
        arp_.setDevMode(devMode); // 设置开发模式
        arp_.setShowSql(devMode); // 是否显示SQL
        arp_.setContainerFactory(new CaseInsensitiveContainerFactory(true));// 大小写不敏感
    }
    @Before({Tx.class})
    public void tt(){
        String sql="update address set name='ma_jian_bo_4' where id=1";
        Db.update(sql);
        Db.use("bb").update(sql);
        throw new RuntimeException("就是要报错");
    }
    public static class log{
        public static void info(String s){
            System.out.println(s);
        }
    }
}
