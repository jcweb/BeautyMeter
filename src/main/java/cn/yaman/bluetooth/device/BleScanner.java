package cn.yaman.bluetooth.device;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;


import java.util.List;

import cn.yaman.bluetooth.common.BleConfigure;
import cn.yaman.bluetooth.device.callback.BleSatusCallBack;
import cn.yaman.bluetooth.device.callback.ScanResultCallBack;
import cn.yaman.bluetooth.device.receiver.BleStatusReceiver;

/**
 * 蓝牙扫描器，单例
 */
public class BleScanner implements BleSatusCallBack {
    private final static String TAG = BleScanner.class.getSimpleName();
    protected static BleScanner instance;
    protected BluetoothAdapter mBluetoothAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback = null;
    private ScanResultCallBack scanResultCallBack = null;
    private Handler handler;
    private final static int DelayedTime = 4000;
    private static boolean isScan = false;
    private boolean isEnableScan = false;
    private void BleScanner() {
    }

    public synchronized static BleScanner getInstance() {

        if (instance == null) {
            instance = new BleScanner();
        }
        return instance;
    }

    /**
     * 注册搜索结果回调
     */
    public void setScanResultCallBack(ScanResultCallBack callBack) {
        if (null != callBack) {
            scanResultCallBack = callBack;
        }
    }

    /**
     * 初始化搜索回调
     */
    private void initLeScanCallback() {
        if (null == handler) {
            handler = new Handler(Looper.getMainLooper());
            BleStatusReceiver.getInstance().putBleSatusCallback(BleScanner.this);
        }
        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                if (null != device.getName() && (device.getName().equalsIgnoreCase("APP BLE")|device.getName().startsWith(BleConfigure.DEVICE_NAME))) {
                    List<ParcelUuid> list = ScanRecordUtil.parseFromBytes(scanRecord).getServiceUuids();
                    if (null != list && list.size() > 0) {
                        for (ParcelUuid uuid : list) {
                            //TODO
                        }
                    }
                    if (null != scanResultCallBack) {
                        scanResultCallBack.onDiscovered(device);
                    }
                }

            }
        };
    }

    //搜索设备
    public void scanLeDevice(boolean enable) {
        isEnableScan = enable;
        if (isScan) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            isScan = false;
        }
        initLeScanCallback();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (null == mBluetoothAdapter) {
            return;
        }
        if (enable) {
            if (null != scanResultCallBack) {
                scanResultCallBack.onScanStart();
            }
            if (!isScan) {
                if (!mBluetoothAdapter.isEnabled()) {
                    if (null != handler) {
                        handler.postDelayed(runnableTimeOut, 2000);
                    }
                    mBluetoothAdapter.enable();
                } else {
                    startScan();
                }

            }
        } else {
            if (null != handler) {
                handler.removeCallbacks(runnable);
            }
            if (null != scanResultCallBack) {
                scanResultCallBack.onScanFinish();
            }
        }
    }

    private void startScan() {
        isScan = true;
        if (null != handler) {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, DelayedTime);
            handler.removeCallbacks(runnableScan);
            handler.post(runnableScan);
        }
    }

    private Runnable runnableScan = new Runnable() {
        @Override
        public void run() {
            Boolean b = mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    };
    /**
     * 关闭扫描
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (null != handler) {
                handler.removeCallbacks(runnable);
            }
            scanLeDevice(false);
        }
    };

    private Runnable runnableTimeOut = new Runnable() {
        @Override
        public void run() {
            if (null != handler) {
                handler.removeCallbacks(runnableTimeOut);
                if (null != scanResultCallBack) {
                    scanResultCallBack.onScanFinish();
                }
            }
        }
    };
    private Runnable reSacn = new Runnable() {
        @Override
        public void run() {
            scanLeDevice(true);
        }
    };

    public void removeTimeOut() {
        if (null != handler) {
            handler.removeCallbacks(runnableTimeOut);
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        if (null != scanResultCallBack) {
            scanResultCallBack = null;
        }
        scanLeDevice(false);
        if (null != handler) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
        mBluetoothAdapter = null;
        if (null != mLeScanCallback) {
            mLeScanCallback = null;
        }
        if (null != instance) {
            instance = null;
        }
    }

    @Override
    public void onBleStateOn() {
        if (isEnableScan && !isScan) {
            scanLeDevice(true);
        }
    }

    @Override
    public void onBleStateOff() {

    }

    @Override
    public void onBleTurningOn() {
        removeTimeOut();
    }

    @Override
    public void onBleTurningOff() {

    }

    @Override
    public void onDisConnected(BluetoothDevice device) {

    }

}
