package cn.yaman.bluetooth.common;

/**
 * 设备参数配置
 */
public final class BleConfigure {
    /**
     * 设备名称开头
     */
    public final static String BLE_NAME = "Honor Ai3-";
    public final static String BLE_NAME_1 = "honor AI31-";
    public final static String BLE_NAME_2 = "CI31-";
    public final static String BLE_NAME_3 = "HUAWEI CI31-";
    public final static String BLE_NAME_4 = "AI31-";
    public final static String BLE_NAME_5 = "HONOR AI-";
    public final static String BLE_NAME_6 = "HONOR AI31-";
    /**
     * 蓝牙服务UUID
     */
    public final static String UUID_SERVICE = "0000ffb0-0000-1000-8000-00805f9b34fb";
    /**
     * 蓝牙读服务UUID
     */
    public final static String UUID_READ = "0000ffb1-0000-1000-8000-00805f9b34fb";
    /**
     * 蓝牙写服务UUID
     */
    public final static String UUID_WRITE = "0000ffb2-0000-1000-8000-00805f9b34fb";
    /**
     * 蓝牙接受协议数据头
     */
    public final static byte DATA_B_HEADER_RECEIVE = (byte) 0x55;
    public final static byte DATA_C_HEADER_RECEIVE = (byte) 0x56;
    /**
     * 蓝牙发送协议数据头
     */
    public final static byte DATA_B_HEADER_SEND = (byte) 0x55;
    public final static byte DATA_C_HEADER_SEND = (byte) 0x56;
    /**
     * 蓝牙发送协议数据头
     */
    public final static byte DATA_CTRL_SEND = (byte) 0x00;
    /**
     * 蓝牙指令响应成功ACK
     */
    public final static int ACK_OK = 100000;
    /**
     * 蓝牙设备低电量警报值
     */
    public final static int BLE_LOW_POWER=10;

    public static int supportedMTU=20;

    public static boolean isDecrypt=false;

    public static int totalMeasure=10;

    public static int totalCalibMeasure=20;

    public static int calibCount=2;
    /**
     * 是否在校准模式
     */
    public static int BLE_Model = 0xB1;

    public static int autoMeasureDelay=10;

    public static int autoMeasureCount=0;

    public static boolean IS_CALIB_HINT=false;

    /*标记用户正在上传*/
    public static boolean IS_UPLOADING = false;

    /*标记用户正在通过用户校准测量*/
    public static boolean  IS_CALIB_DO_MEASURE=false;
}
