package cn.yaman.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author timpkins
 */
public class TimeUtils {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");

    /*把second转换成12'10"格式*/
    public static String secondToStr(int num) {
        int minute = num / 60;
        int second = num % 60;
        if (minute == 0 && second > 0) {
            return second + "\"";
        } else if (minute > 0 && second == 0) {
            return minute + "\'" + "00" + "\"";
        } else if (minute > 0 && second > 0) {
            return minute + "\'" + second + "\"";
        } else {
            return "";
        }
    }

    public static String dateToStr(long second) {
        return format.format(second);
    }

    public static long strToDate(String str) {
        long time = 0L;
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(str);
            time = date.getTime();
            return time;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String dateToStr(String formatStr,long second) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(second);
    }
}
