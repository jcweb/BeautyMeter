package cn.yaman.callBack;

import android.bluetooth.BluetoothDevice;

/**
 * 蓝牙设备列表点击事件回调
 */
public interface OnBLESelectItemClickListener {
    void onBLESelectItemClick(BluetoothDevice device);
    void onBLESelectItemScanClick();
    void onBLESelectItemNoScanClick();
}
