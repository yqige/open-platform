package test;

import com.jfinal.kit.HttpKit;

import java.util.HashMap;

public class HttpUtil {
    public static void test(){
        HashMap hashMap = new HashMap();
//        hashMap.put("If‐Modified‐Since","Wed, 01 Nov 2017 03:54:21 GMT");
        hashMap.put("loginId","sdsdsd17admin");
        hashMap.put("loginPwd","111111");
        String in = HttpKit.post("http://demowms.abh58.net/toLogin",hashMap,"返回的是什么");
        System.out.println(in);
    }

    public static void main(String[] args) {
        HttpUtil.test();
    }
}
