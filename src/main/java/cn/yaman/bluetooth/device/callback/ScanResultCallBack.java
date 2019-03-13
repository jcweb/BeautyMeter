package cn.yaman.bluetooth.device.callback;

import android.bluetooth.BluetoothDevice;

/**
 * 扫描结果回调
 */
public interface ScanResultCallBack {

    /**
     * 扫描结果回调方法
     *
     * @param device
     */
    void onDiscovered(BluetoothDevice device);

    /**
     * 开始扫描蓝牙
     */
    void onScanStart();

    /**
     * 扫描蓝牙结束
     */
    void onScanFinish();
}
