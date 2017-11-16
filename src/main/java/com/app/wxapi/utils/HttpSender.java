package com.demo.wxapi.utils;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;


/**
 * @param url        应用地址，类似于http://ip:port/msg/
 * @param account    账号
 * @param pswd       密码
 * @param mobile     手机号码，多个号码使用","分割
 * @param msg        短信内容
 * @param needstatus 是否需要状态报告，需要true，不需要false
 * @return 返回值定义参见HTTP协议文档
 * @throws Exception
 */
public class HttpSender {


    public static String batchSend(String url, String account, String pswd, String mobile, String msg,
                                   boolean needstatus, String extno) throws Exception {
        HttpClient client = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
        GetMethod method = new GetMethod();
        InputStream in = null;
        ByteArrayOutputStream baos = null;
        try {
            URI base = new URI(url, false);
            method.setURI(new URI(base, "HttpBatchSendSM", false));
            method.setQueryString(new NameValuePair[]{new NameValuePair("account", account),
                    new NameValuePair("pswd", pswd), new NameValuePair("mobile", mobile),
                    new NameValuePair("needstatus", String.valueOf(needstatus)), new NameValuePair("msg", msg),
                    new NameValuePair("extno", extno)});
            int result = client.executeMethod(method);
            if (result == HttpStatus.SC_OK) {
                in = method.getResponseBodyAsStream();
                baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = in.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }

                return URLDecoder.decode(baos.toString(), "UTF-8");
            } else {
                throw new Exception("HTTP ERROR Status: " + method.getStatusCode() + ":" + method.getStatusText());
            }
        } finally {
            method.releaseConnection();
            in.close();// 1
            baos.close();// 2
        }
    }

}

