package cn.yaman.bluetooth.device.callback;

import android.bluetooth.BluetoothDevice;

/**
 * 蓝牙状态改变监听器
 */
public interface BleSatusCallBack {

    /**
     * 蓝牙打开时的回调方法
     */
    void onBleStateOn();

    /**
     *蓝牙关闭时的回调方法
     */
    void onBleStateOff();

    /**
     *蓝牙正在打开时的回调
     */
    void onBleTurningOn();

    /**
     *蓝牙正在关闭时的回调
     */
    void onBleTurningOff();

    /**
     * 蓝牙设备断开连接
     */
    void onDisConnected(BluetoothDevice device);
}
