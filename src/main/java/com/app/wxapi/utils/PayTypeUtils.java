package com.app.wxapi.utils;

public class PayTypeUtils {
    public static String toStrValue(String value){
        switch (value){
            case "alipay_bar":return "支付宝";
            case "alipay_qr":return "支付宝";
            case "bal":return "储值支付";
            case "cash":return "现金";
            case "otherpay":return "其他";
            case "pos":return "POS";
            case "scpay":return "商场收银";
            case "scsy":return "商场收银";
            case "wx_qr":return "微信";
            case "wx_scan":return "微信";
            default: return "未知";
        }
    }
}
