package cn.yaman.bluetooth.device.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


import java.util.HashMap;
import java.util.Map;

import cn.yaman.bluetooth.device.callback.BleSatusCallBack;

/**
 * 蓝牙开关状态监听广播
 */

public final class BleStatusReceiver extends BroadcastReceiver {

    private static final String TAG = "BleStatusReceiver";

    private Map<Integer, BleSatusCallBack> callbackHashMap = new HashMap<>();
    private boolean isListeneredBleState = false;
    private static BleStatusReceiver instance;
    private static int index = 0;

    private BleStatusReceiver() {
    }

    public static synchronized BleStatusReceiver getInstance() {
        if (instance == null) {
            instance = new BleStatusReceiver();
        }
        return instance;
    }

    /**
     * 注册监听蓝牙状态广播
     */
    public void registerBleStatusReceiver(Context context) {
        if (!isListeneredBleState && context != null) {
            isListeneredBleState = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            context.registerReceiver(instance, filter);//, "android.permission.BIND_APPWIDGET", new Handler(Looper.myLooper()));
        }
    }

    /**
     * 注销监听蓝牙状态广播
     */
    public void unregisterBleStatusReceiver(Context context) {
        if (isListeneredBleState && context != null) {
            context.unregisterReceiver(instance);
            isListeneredBleState = false;
            callbackHashMap.clear();
            instance = null;
        }

    }

    public void putBleSatusCallback(BleSatusCallBack callBack) {
        index++;
        callbackHashMap.put(index, callBack);
    }

    public void removeBleSatusCallback(BleSatusCallBack callBack) {
        callbackHashMap.remove(callBack);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);

            switch (blueState) {
                case BluetoothAdapter.STATE_TURNING_ON:
                    for (BleSatusCallBack callBack : callbackHashMap.values()) {
                        callBack.onBleTurningOn();
                    }
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    for (BleSatusCallBack callBack : callbackHashMap.values()) {
                        callBack.onBleTurningOff();
                    }
                    break;
                case BluetoothAdapter.STATE_ON:
                    for (BleSatusCallBack callBack : callbackHashMap.values()) {
                        callBack.onBleStateOn();
                    }
                    break;
                case BluetoothAdapter.STATE_OFF:
                    for (BleSatusCallBack callBack : callbackHashMap.values()) {
                        callBack.onBleStateOff();
                    }
                    break;
                    default:
                        break;
            }

        }else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            //蓝牙连接被切断
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            for (BleSatusCallBack callBack : callbackHashMap.values()) {
                callBack.onDisConnected(device);
            }
        }

    }
}
