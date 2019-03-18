package cn.yaman.bluetooth.common;

/**
 * 设备参数配置
 */
public final class BleConfigure {
    /**
     * 蓝牙名称
     */
    public final static String DEVICE_NAME="XESS";
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

    public final static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public final static String CLIENT_CHARACTERISTIC_CONFIG_1 = "00002901-0000-1000-8000-00805f9b34fb";
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

}
