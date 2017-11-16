//package test;
//
//import com.demo.wxapi.listener.RedisMsgPubSubListener;
//import com.demo.wxapi.utils.DateUtil;
//import org.junit.jupiter.api.Test;
//import redis.clients.jedis.Jedis;
//
//public class TestSubscribe {
//    @Test
//    public void testSubscribe() throws Exception{
//        Jedis jedis = new Jedis("localhost");
//        RedisMsgPubSubListener listener = new RedisMsgPubSubListener();
//        jedis.subscribe(listener, "redisChatTest");
//        //other code
//        System.out.println("end");
//    }
//    @Test
//    public void testPublish() throws Exception{
//        Jedis jedis = new Jedis("localhost");
//        jedis.publish("redisChatTest", "我是天才"+ DateUtil.getNowString(0));
//        jedis.publish("redisChatTest", "我牛逼");
//        jedis.publish("redisChatTest", "哈哈");
//    }
//
//}
