package com.app.wxapi.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    private static final String[] PATTERNS = {"yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd",
            "yyyy-M-d",
            "yyyyMMdd",
            "yyyyMM",
            "yyyy年M月d日",
            "yyyy.M.d",
            "yyyy.MM.dd",
            "yyyy年M月",
            "MM-dd",
            "HH:mm",
            "yyMMdd"};
    public static SimpleDateFormat df = new SimpleDateFormat();

    /**
     * Timestamp转字符串
     *
     * @param time
     * @param pattern 0-"yyyy-MM-dd HH:mm:ss"
     *                1-"yyyy-MM-dd HH:mm"
     *                2-"yyyy-MM-dd"
     *                3-"yyyy-M-d"
     *                4-"yyyyMMdd"
     *                5-"yyyyMM"
     *                6-"yyyy年M月d日"
     *                7-"yyyy.M.d"
     *                8-"yyyy.MM.dd"
     *                9-"yyyy年M月"
     *                10-"MM-dd"
     *                11-"HH:mm"
     * @return
     */
    public static String DateToString(Timestamp time, int pattern) {
        return DateToString(TimestampToDate(time), pattern);
    }

    /**
     * 字符串转Date
     *
     * @param time
     * @param pattern 0-"yyyy-MM-dd HH:mm:ss"
     *                1-"yyyy-MM-dd HH:mm"
     *                2-"yyyy-MM-dd"
     *                3-"yyyy-M-d"
     *                4-"yyyyMMdd"
     *                5-"yyyyMM"
     *                6-"yyyy年M月d日"
     *                7-"yyyy.M.d"
     *                8-"yyyy.MM.dd"
     *                9-"yyyy年M月"
     *                10-"MM-dd"
     *                11-"HH:mm"
     * @return
     */
    public static Date StringToDate(String time, int pattern) throws ParseException {
        if (time == null)
            return null;
        df.applyPattern(PATTERNS[pattern]);
        return df.parse(time);
    }

    /**
     * 字符串转Long
     *
     * @param time
     * @param pattern 0-"yyyy-MM-dd HH:mm:ss"
     *                1-"yyyy-MM-dd HH:mm"
     *                2-"yyyy-MM-dd"
     *                3-"yyyy-M-d"
     *                4-"yyyyMMdd"
     *                5-"yyyyMM"
     *                6-"yyyy年M月d日"
     *                7-"yyyy.M.d"
     *                8-"yyyy.MM.dd"
     *                9-"yyyy年M月"
     *                10-"MM-dd"
     *                11-"HH:mm"
     * @return
     */
    public static Long StringToLong(String time, int pattern) throws ParseException {
        return StringToDate(time, pattern).getTime();
    }

    /**
     * Timestamp转Date
     *
     * @param time
     * @return
     */
    public static Date TimestampToDate(Timestamp time) {
        if (time == null)
            return null;
        return new Date(time.getTime());
    }

    /**
     * Date转字符串
     *
     * @param date
     * @param pattern 0-"yyyy-MM-dd HH:mm:ss"
     *                1-"yyyy-MM-dd HH:mm"
     *                2-"yyyy-MM-dd"
     *                3-"yyyy-M-d"
     *                4-"yyyyMMdd"
     *                5-"yyyyMM"
     *                6-"yyyy年M月d日"
     *                7-"yyyy.M.d"
     *                8-"yyyy.MM.dd"
     *                9-"yyyy年M月"
     *                10-"MM-dd"
     *                11-"HH:mm"
     *                12-"yyMMdd"
     * @return
     */
    public static String DateToString(Date date, int pattern) {
        if (date == null)
            return "";
        df.applyPattern(PATTERNS[pattern]);
        return df.format(date);
    }

    /**
     * 传入Timestamp如果是当天,返回pattern1格式字符串，否则返回pattern2格式
     *
     * @param time
     * @param pattern1
     * @param pattern2 0-"yyyy-MM-dd HH:mm:ss"
     *                 1-"yyyy-MM-dd HH:mm"
     *                 2-"yyyy-MM-dd"
     *                 3-"yyyy-M-d"
     *                 4-"yyyyMMdd"
     *                 5-"yyyyMM"
     *                 6-"yyyy年M月d日"
     *                 7-"yyyy.M.d"
     *                 8-"yyyy.MM.dd"
     *                 9-"yyyy年M月"
     *                 10-"MM-dd"
     *                 11-"HH:mm"
     * @return
     */
    public static String getTimeOrDate(Timestamp time, int pattern1, int pattern2) {
        if (time == null)
            return "";
        String result = null;
        if (time.getTime() > getNowZeroLong())
            result = DateToString(time, pattern1);
        else
            result = DateToString(time, pattern2);
        return result;
    }

    /**
     * 获得当前时间毫秒数
     *
     * @return
     */
    public static Long getNowLong() {
        return System.currentTimeMillis();
    }

    /**
     * 获得当前时间字符串
     *
     * @param pattern 0-"yyyy-MM-dd HH:mm:ss"
     *                1-"yyyy-MM-dd HH:mm"
     *                2-"yyyy-MM-dd"
     *                3-"yyyy-M-d"
     *                4-"yyyyMMdd"
     *                5-"yyyyMM"
     *                6-"yyyy年M月d日"
     *                7-"yyyy.M.d"
     *                8-"yyyy.MM.dd"
     *                9-"yyyy年M月"
     *                10-"MM-dd"
     *                11-"HH:mm"
     * @return
     */
    public static String getNowString(int pattern) {
        return DateToString(new Date(), pattern);
    }

    /**
     * 获得当天0点0分0秒的毫秒数
     *
     * @return
     */
    public static Long getNowZeroLong() {
        return getDateZeroLong(getNowLong());
    }

    /**
     * 根据传入Long,获得那天0点0分0秒的毫秒数
     *
     * @return
     */
    public static Long getDateZeroLong(Long l) {
        return l / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
    }

    /**
     * 根据传入date,获得那天0点0分0秒的毫秒数
     *
     * @return
     */
    public static Long getDateZeroLong(Date date) {
        return date.getTime() / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
    }

    /**
     * 根据时间获得days天以前或以后的时间Date，days 为负：days天以前，days 为正：days天以后
     *
     * @param day
     * @param days
     * @return
     */
    public static Date getDateBeforeOrAfter(Date day, int days) {
        Calendar now = Calendar.getInstance();
        now.setTime(day);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + days);
        return now.getTime();
    }

    /**
     * 获得days天以前或以后的时间Date，days 为负：days天以前，days 为正：days天以后
     *
     * @param days
     * @return
     */
    public static Date getNowDateBeforeOrAfter(int days) {
        return getDateBeforeOrAfter(new Date(), days);
    }

    /**
     * 获得days天以前的时间Date
     *
     * @param days
     * @return
     */
    public static Date getNowDateBefore(int days) {
        return getNowDateBeforeOrAfter(-Math.abs(days));
    }

    public static void main(String[] args) throws Exception {

        System.out.println(isBetween(DateUtil.StringToDate("2017-09-23 00:00:00",0), "2017-09-13 00:00:00", "2017-09-17 23:59:59"));
    }

    public static Date formate(Date date, int i) {
        if (date == null)
            return null;
        df.applyPattern(PATTERNS[i]);
        try {
            return StringToDate(df.format(date), i);
        } catch (ParseException e) {
            return null;
        }
    }

    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    public static Boolean isBetweenHM(Date date, String strDateBegin,
                                      String strDateEnd) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(date);
        // 截取当前时间时分秒
        int strDateH = Integer.parseInt(strDate.substring(11, 13));
        int strDateM = Integer.parseInt(strDate.substring(14, 16));
        int strDateS = Integer.parseInt(strDate.substring(17, 19));
        // 截取开始时间时分秒
        int strDateBeginH = Integer.parseInt(strDateBegin.substring(0, 2));
        int strDateBeginM = Integer.parseInt(strDateBegin.substring(3, 5));
        int strDateBeginS = Integer.parseInt(strDateBegin.substring(6, 8));
        // 截取结束时间时分秒
        int strDateEndH = Integer.parseInt(strDateEnd.substring(0, 2));
        int strDateEndM = Integer.parseInt(strDateEnd.substring(3, 5));
        int strDateEndS = Integer.parseInt(strDateEnd.substring(6, 8));
        if ((strDateH >= strDateBeginH && strDateH <= strDateEndH)) {
            // 当前时间小时数在开始时间和结束时间小时数之间
            if (strDateH > strDateBeginH && strDateH < strDateEndH) {
                return true;
                // 当前时间小时数等于开始时间小时数，分钟数在开始和结束之间
            } else if (strDateH == strDateBeginH && strDateM >= strDateBeginM
                    && strDateM <= strDateEndM) {
                return true;
                // 当前时间小时数等于开始时间小时数，分钟数等于开始时间分钟数，秒数在开始和结束之间
            } else if (strDateH == strDateBeginH && strDateM == strDateBeginM
                    && strDateS >= strDateBeginS && strDateS <= strDateEndS) {
                return true;
            }
            // 当前时间小时数大等于开始时间小时数，等于结束时间小时数，分钟数小等于结束时间分钟数
            else if (strDateH >= strDateBeginH && strDateH == strDateEndH
                    && strDateM <= strDateEndM) {
                return true;
                // 当前时间小时数大等于开始时间小时数，等于结束时间小时数，分钟数等于结束时间分钟数，秒数小等于结束时间秒数
            } else if (strDateH >= strDateBeginH && strDateH == strDateEndH
                    && strDateM == strDateEndM && strDateS <= strDateEndS) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    public static Boolean isBetween(Date date, String strDateBegin,
                                    String strDateEnd) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d1 = sf.parse(strDateBegin);
            Date d2 = sf.parse(strDateEnd);
            if(date.before(d2)&&date.after(d1)){
                return true;
            }else return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

    }
}
