package cn.yaman.util;

import java.text.SimpleDateFormat;

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
            return minute + "\'";
        } else if (minute > 0 && second > 0) {
            return minute + "\'" + second + "\"";
        } else {
            return "";
        }
    }

    public static String dateToStr(long second) {
        return format.format(second);
    }
}
