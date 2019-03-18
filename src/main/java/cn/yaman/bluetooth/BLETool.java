package cn.yaman.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;


import java.util.HashMap;
import java.util.Map;

import cn.yaman.bluetooth.callBack.BleDeviceStateCallBack;
import cn.yaman.bluetooth.common.BleConfigure;
import cn.yaman.bluetooth.device.callback.ConnectCallBack;
import cn.yaman.bluetooth.device.callback.ScanResultCallBack;

/**
 * 蓝牙管理器
 */
public class BLETool extends BleManager {
    private final static String TAG = BLETool.class.getSimpleName();

    private static BLETool instance;
    /***蓝牙适配器对象**/
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private String mDeviceAddress;
    private Map<String, ScanResultCallBack> scanResultCallbackMap = new HashMap<>();
    private Map<String, ConnectCallBack> connectCallBackMap = new HashMap<>();
    private static RougeMessageAnalyser analyser;
    private static RougeCommandEditor editor;
    //连接设备状态
    private boolean isBleConnected = false;
    private boolean isSend = false;

    public boolean getIsSend() {
        return isSend;
    }

    public void setIsSend(boolean flag) {
        isSend = flag;
    }

    private BLETool() {
    }

    public synchronized static BLETool getInstance() {
        if (instance == null) {
            instance = new BLETool();
        }
        return instance;
    }

    @Override
    protected boolean initBleService(Context ctx) {
        if (null == ctx) {
            return false;
        }
        mHandler = new Handler();
        BluetoothManager bluetoothManager =
                (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        }
        editor = RougeCommandEditor.getInstance();
        analyser = RougeMessageAnalyser.getInstance();
        if (null != mBluetoothLeService) {
            if (null != editor) {
                editor.registerBleMessageWriter(mBluetoothLeService);
                editor.setDataHeaderSend(BleConfigure.DATA_B_HEADER_SEND);
                mBluetoothLeService.setBleWriteDataResultCallBack(editor.getBleWriteDataResultCallBack());
            }
            mBluetoothLeService.setRougeMessageAnalyser(analyser);
            mBluetoothLeService.setScanResultCallBack(scanResultCallBack);
            mBluetoothLeService.setConnectCallback(connectCallBack);
        }
        if (null != analyser) {
            analyser.setDataHeaderReceive(BleConfigure.DATA_B_HEADER_RECEIVE);
            analyser.setBleDeviceStateCallBack(bleDeviceStateCallBack);
        }
        return true;
    }


    /**
     * 扫描蓝牙设备结果回调
     */
    private ScanResultCallBack scanResultCallBack = new ScanResultCallBack() {
        @Override
        public void onDiscovered(BluetoothDevice device) {
            for (ScanResultCallBack callBack : scanResultCallbackMap.values()) {
                callBack.onDiscovered(device);
            }
        }

        @Override
        public void onScanStart() {
            for (ScanResultCallBack callBack : scanResultCallbackMap.values()) {
                callBack.onScanStart();
            }
        }

        @Override
        public void onScanFinish() {
            for (ScanResultCallBack callBack : scanResultCallbackMap.values()) {
                callBack.onScanFinish();
            }
        }
    };

    /**
     * 连接蓝牙设备结果回调
     */
    private ConnectCallBack connectCallBack = new ConnectCallBack() {
        @Override
        public void onConnected() {
            for (ConnectCallBack callBack : connectCallBackMap.values()) {
                callBack.onConnected();
            }
        }

        @Override
        public void onConnecting() {
            for (ConnectCallBack callBack : connectCallBackMap.values()) {
                callBack.onConnecting();
            }
        }

        @Override
        public void onScaleWake() {
            for (ConnectCallBack callBack : connectCallBackMap.values()) {
                callBack.onScaleWake();
            }
        }

        @Override
        public void onScaleSleep() {
            for (ConnectCallBack callBack : connectCallBackMap.values()) {
                callBack.onScaleSleep();
            }
        }

        @Override
        public void onDisConnected() {
            resetBleState();
            for (ConnectCallBack callBack : connectCallBackMap.values()) {
                callBack.onDisConnected();
            }
        }
    };
private BleDeviceStateCallBack bleDeviceStateCallBack = new BleDeviceStateCallBack() {
    @Override
    public void onState(int state, int batter, int runTime) {
    }
};

    /**
     * 注册扫描结果回调
     */
    public void registerScanResultCallBack(String className, ScanResultCallBack callBack) {
        if (!TextUtils.isEmpty(className) && null != callBack) {
            scanResultCallbackMap.put(className, callBack);
        }
    }


    /**
     * 注销扫描结果回调
     */
    public void unregisterScanResultCallBack(String className) {
        if (!TextUtils.isEmpty(className)) {
            if (scanResultCallbackMap.containsKey(className)) {
                scanResultCallbackMap.remove(className);
            }
        }
    }

    /**
     * 注册连接结果回调
     */
    public void registerConnectCallBack(String className, ConnectCallBack callBack) {
        if (!TextUtils.isEmpty(className) && null != callBack) {
            connectCallBackMap.put(className, callBack);
        }
    }

    /**
     * 注销连接结果回调
     */
    public void unregisterConnectCallBack(String className) {
        if (!TextUtils.isEmpty(className)) {
            if (connectCallBackMap.containsKey(className)) {
                connectCallBackMap.remove(className);
            }
        }
    }

    /**
     * 连接设备
     */
    public void connectBluetooth(String deviceName, String deviceAddress) {
        mDeviceAddress = deviceAddress;
        //蓝牙连接
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);

        }
    }

    /**
     * 断开设备连接
     */
    public void disConnectBluetooth() {
        resetBleState();
        if (null != mBluetoothLeService) {
            mBluetoothLeService.disconnect();
        }
    }

    /**
     * 重置设备状态
     */
    private void resetBleState() {
        isBleConnected = false;
        isSend = false;
        if (null != editor) {
            editor.clearData();
        }
        if (null != analyser) {
            analyser.clearReceiveData();
        }
    }

    public boolean getConnectionState() {
        return isBleConnected;
    }

    public boolean getBleServiceState() {
        if (null != mBluetoothLeService) {
            return mBluetoothLeService.getConnectionState();
        }
        return false;
    }

    //请求设备状态信息
    public void requestDeviceState() {
        if (null != editor) {
            editor.requestDeviceState();
        }
    }

    public void release() {
        if (null != mBluetoothLeService) {
            mBluetoothLeService.release();
        }
        if (null != instance) {
            instance = null;
        }
    }

}