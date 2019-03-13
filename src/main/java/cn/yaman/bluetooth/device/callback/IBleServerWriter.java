package cn.yaman.bluetooth.device.callback;

/**
 * Ble发送接口
 */
public interface IBleServerWriter {

    boolean writeLlsAlertLevel(String type,byte[] bb);
}
