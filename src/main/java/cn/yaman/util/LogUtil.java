package cn.yaman.util;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @FileName: LogUtil
 * @Description: 日志工具类
 * @author: fujingqin
 * @Time: 2018/6/5 10:20
 */
public class LogUtil {

    /**
     * 日志文件总开关 true 为打开，false 为关闭
     */
    private static Boolean MYLOG_SWITCH = true; // 日志文件总开关

    /**
     * 日志写入文件开关 true 为打开，false 为关闭
     */
    private static Boolean MYLOG_WRITE_TO_FILE = true;// 日志写入文件开关
    private static char MYLOG_TYPE = 'v';// 输入日志类型，w代表只输出告警信息等，v代表输出所有信息
    private static String MYLOG_PATH_SDCARD_DIR = "com.huawei.spectrum";// 日志文件在sdcard中的路径
    private static int SDCARD_LOG_FILE_SAVE_DAYS = 0;// sd卡中日志文件的最多保存天数
    private static String MYLOGFILEName = "spectrum_log.csv";// 本类输出的日志文件名称
    public static SimpleDateFormat myLogSdf = new SimpleDateFormat(
            "HH:mm:ss:SSS");// 日志的输出格式
    private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式

    /**
     * 日志输出所有信息
     */
    public static char LOG_LEVEL_V='v';
    /**
     * 日志只输出Warm信息
     */
    public static char LOG_LEVEL_W='w';
    /**
     * 日志只输出Error信息
     */
    public static char LOG_LEVEL_E='e';
    /**
     * 日志只输出Info信息
     */
    public static char LOG_LEVEL_I='i';

    public static void w(String tag, Object msg) { // 警告信息
        log(tag, msg.toString(), 'w');
    }

    public static void e(String tag, Object msg) { // 错误信息
        log(tag, msg.toString(), 'e');
    }

    public static void d(String tag, Object msg) {// 调试信息
        log(tag, msg.toString(), 'd');
    }

    public static void i(String tag, Object msg) {//
        log(tag, msg.toString(), 'i');
    }

    public static void v(String tag, Object msg) {
        log(tag, msg.toString(), 'v');
    }

    public static void w(String tag, String text) {
        log(tag, text, 'w');
    }

    public static void e(String tag, String text) {
        log(tag, text, 'e');
    }

    public static void d(String tag, String text) {
        log(tag, text, 'd');
    }

    public static void i(String tag, String text) {
        log(tag, text, 'i');
    }

    public static void v(String tag, String text) {
        log(tag, text, 'v');
    }

    /**
     * @MethodName: setPrintLog
     * @Description: 设置是否打印Log, 默认打印
     * @author: fujingqin
     * @Time: 2018/6/6 10:13
     */
    public static void setPrintLog(boolean flag) {
        MYLOG_SWITCH = flag;
    }

    /**
     * @MethodName: setSaveLog
     * @Description: 设置是否保存Log, 默认保存
     * @author: fujingqin
     * @Time: 2018/6/6 10:15
     */
    public static void setSaveLog(boolean flag) {
        MYLOG_WRITE_TO_FILE = flag;
    }

    /**
     * @MethodName: setLogLevel
     * @Description: 设置打印Log级别:v代表输出所有信息,w代表只输出Warm信息,d代表只输出Debug信息,e代表只输出Error信息,i代表只输出Info信息
     * @author: fujingqin
     * @Time: 2018/6/6 10:18
     */
    public static void setLogLevel(char level) {
        MYLOG_TYPE = level;
    }

    /**
     * 根据tag, msg和等级，输出日志
     *
     * @param tag
     * @param msg
     * @param level
     * @return void
     * @since v 1.0
     */
    private static void log(String tag, String msg, char level) {
        if (MYLOG_SWITCH) {
            if ('e' == level && ('e' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) { // 输出错误信息
                Log.e(tag, msg);
            } else if ('w' == level && ('w' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
                Log.w(tag, msg);
            } else if ('d' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
                Log.d(tag, msg);
            } else if ('i' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
                Log.i(tag, msg);
            } else {
                Log.v(tag, msg);
            }
            if (MYLOG_WRITE_TO_FILE)
                writeLogtoFile(String.valueOf(level), tag, msg);
        }
    }

    private static String getLogPath() {
        String path = "";
        // 获取扩展SD卡设备状态
        String sDStateString = android.os.Environment.getExternalStorageState();

        // 拥有可读可写权限
        if (sDStateString.equals(android.os.Environment.MEDIA_MOUNTED)) {
            // 获取扩展存储设备的文件目录
            File SDFile = android.os.Environment.getExternalStorageDirectory();
            path = SDFile.getAbsolutePath() + File.separator
                    + MYLOG_PATH_SDCARD_DIR;
        }
        return path;

    }

    /**
     * 打开日志文件并写入日志信息
     *
     * @return
     **/
    private static void writeLogtoFile(String mylogtype, String tag, String text) {// 新建或打开日志文件
        Date nowtime = new Date();
        String needWriteFiel = logfile.format(nowtime);
//        String needWriteMessage = myLogSdf.format(nowtime) + "    " + mylogtype
//                + "    " + tag + "    " + text;
        String needWriteMessage = myLogSdf.format(nowtime) +","+ text;

        // 取得日志存放目录
        String path = getLogPath();
        if (path != null && !"".equals(path)) {
            try {
                // 创建目录
                File dir = new File(path);
                if (!dir.exists())
                    dir.mkdir();
                // 打开文件
                File file = new File(path + File.separator + needWriteFiel
                        + MYLOGFILEName);
                FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
                BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                bufWriter
                        .write(new String(needWriteMessage.getBytes(), "UTF-8"));
                bufWriter.newLine();
                bufWriter.close();
                filerWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除制定的日志文件
     */
    public static void delFile() {// 删除日志文件
        // 取得日志存放目录
        String path = getLogPath();
        if (path != null && !"".equals(path)) {
            String needDelFiel = logfile.format(getDateBefore());
            File file = new File(path, needDelFiel + MYLOGFILEName);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 得到现在时间前的几天日期，用来得到需要删除的日志文件日期
     */
    private static Date getDateBefore() {
        Date nowtime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(nowtime);
        now.set(Calendar.DATE, now.get(Calendar.DATE)
                - SDCARD_LOG_FILE_SAVE_DAYS);
        return now.getTime();
    }
}