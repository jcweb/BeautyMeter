package cn.yaman.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import cn.yaman.YamanApplication;
import cn.yaman.bluetooth.callBack.BleDeviceStateCallBack;
import cn.yaman.bluetooth.common.BleConfigure;
import cn.yaman.bluetooth.device.callback.ConnectCallBack;
import cn.yaman.bluetooth.device.callback.ScanResultCallBack;
import cn.yaman.util.LogUtil;
import cn.yaman.util.StringUtil;

/**
 * 蓝牙管理器
 */
public class BLETool extends BleManager {
    private final static String TAG = BLETool.class.getSimpleName();

    private static BLETool instance;
    /***蓝牙适配器对象**/
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private String mDeviceName;
    private String mTempDeviceName;
    private String mDeviceAddress;
    private int mDeviceBattery;
    private Map<String, ScanResultCallBack> scanResultCallbackMap = new HashMap<>();
    private Map<String, ConnectCallBack> connectCallBackMap = new HashMap<>();
    private static RougeMessageAnalyser analyser;
    private static RougeCommandEditor editor;
    //连接设备状态
    private boolean isBleConnected = false;
    //设备当前电量
    private int curBleBattery = 0;
    //设备充电状态
    private boolean bleChargeState = false;
    //高光谱ID
    private String spectrumID = "";
    private byte[] spectrumVersion = null;
    private byte[] spectrumConfigData = null;
    private Context calibrateModelContext = null;
    private boolean isSend = false;

    public boolean getIsSend() {
        return isSend;
    }

    public void setIsSend(boolean flag) {
        isSend = flag;
    }

    private boolean isSendAccess = false;

    private boolean blePowerLowHint = false;
    private boolean isStartMeasure = false;
    private boolean isGetVersion = false;
    private boolean isToCalbirate=false;

    public void setGetVersion(boolean flag) {
        isGetVersion = true;
    }
    private boolean isReqPower=false;

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
                editor.setOtaVersion(getOtaVersionByte());
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
//            analyser.setBleDeviceVersionCallBack(bleDeviceVersionCallBack);
        }
//        FalconCalibUtil.getInstance().registerFalconCalibListener(TAG,falconCalibListener);
        return true;
    }

    public void initUpgade(String fileName) {
        readOTAFileA(fileName);
    }

    /**
     * 扫描蓝牙设备结果回调
     */
    private ScanResultCallBack scanResultCallBack = new ScanResultCallBack() {
        @Override
        public void onDiscovered(BluetoothDevice device) {
            LogUtil.d(TAG, "ScanResultCallBack--onDiscovered->");
            LogUtil.d(TAG, "ScanResultCallBack--onDiscovered->name=" + device.getName());
            LogUtil.d(TAG, "ScanResultCallBack--onDiscovered->mac=" + device.getAddress());
            for (ScanResultCallBack callBack : scanResultCallbackMap.values()) {
                callBack.onDiscovered(device);
            }
        }

        @Override
        public void onScanStart() {
            LogUtil.d(TAG, "ScanResultCallBack--onScanStart->");
            for (ScanResultCallBack callBack : scanResultCallbackMap.values()) {
                callBack.onScanStart();
            }
        }

        @Override
        public void onScanFinish() {
            LogUtil.d(TAG, "ScanResultCallBack--onScanFinish->");
            for (ScanResultCallBack callBack : scanResultCallbackMap.values()) {
                callBack.onScanFinish();
            }
        }
    };

    /**
     * 连接蓝牙设备结果回调
     * Created by fWX581433 on 2018/7/14 9:36
     */
    private ConnectCallBack connectCallBack = new ConnectCallBack() {
        @Override
        public void onConnected() {
            LogUtil.d(TAG, "ScanResultCallBack--onConnected->");
            for (ConnectCallBack callBack : connectCallBackMap.values()) {
                callBack.onConnected();
            }
        }

        @Override
        public void onConnecting() {
            LogUtil.d(TAG, "ScanResultCallBack--onConnecting->");
            for (ConnectCallBack callBack : connectCallBackMap.values()) {
                callBack.onConnecting();
            }
        }

        @Override
        public void onScaleWake() {
            LogUtil.d(TAG, "ScanResultCallBack--onScaleWake->");
            for (ConnectCallBack callBack : connectCallBackMap.values()) {
                callBack.onScaleWake();
            }
        }

        @Override
        public void onScaleSleep() {
            LogUtil.d(TAG, "ScanResultCallBack--onScaleSleep->");
            for (ConnectCallBack callBack : connectCallBackMap.values()) {
                callBack.onScaleSleep();
            }
        }

        @Override
        public void onDisConnected() {
            LogUtil.d(TAG, "ConnectCallBack--onDisConnected--123---->" + isDeviceBound());
            resetBleState();
            for (ConnectCallBack callBack : connectCallBackMap.values()) {
                LogUtil.d(TAG, "ConnectCallBack---onDisConnected--->" + connectCallBackMap.keySet().iterator().next());
                callBack.onDisConnected();
            }
        }
    };
private BleDeviceStateCallBack bleDeviceStateCallBack = new BleDeviceStateCallBack() {
    @Override
    public void onState(int state, int batter, int runTime) {
        LogUtil.i(TAG,runTime+"");
    }
};

    public void sendMeasureAction(){
        if (null != editor) {
            editor.responseMeasureAction();
        }
    }


    private Runnable AccessLegalityTimer = new Runnable() {
        @Override
        public void run() {
            LogUtil.d(TAG, "AccessLegalityTimer--000->isSendAccess=" + isSendAccess);
            mHandler.removeCallbacks(AccessLegalityTimer);
            if (!isSendAccess) {
                LogUtil.d(TAG, "AccessLegalityTimer--111->isSendAccess=" + isSendAccess);
                isSendAccess = true;
            }
        }
    };

    /**
     * 注册扫描结果回调
     * Created by fWX581433 on 2018/7/13 14:51
     */
    public void registerScanResultCallBack(String className, ScanResultCallBack callBack) {
        if (!TextUtils.isEmpty(className) && null != callBack) {
            scanResultCallbackMap.put(className, callBack);
        }
    }


    /**
     * 注销扫描结果回调
     * Created by fWX581433 on 2018/7/13 14:51
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
     * Created by fWX581433 on 2018/7/3 15:58
     */
    public void registerConnectCallBack(String className, ConnectCallBack callBack) {
        if (!TextUtils.isEmpty(className) && null != callBack) {
            connectCallBackMap.put(className, callBack);
        }
    }

    /**
     * 注销连接结果回调
     * Created by fWX581433 on 2018/7/3 16:35
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
        LogUtil.d(TAG, "----connectBluetooth() ----");
        mDeviceName = deviceName;
        mDeviceAddress = deviceAddress;
        //蓝牙连接
        if (mBluetoothLeService != null) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            LogUtil.d(TAG, "Connect request result=" + result);
//                }
//            }).start();

        }
    }

    public void startLegalityTimer() {
        if (null != mHandler) {
            LogUtil.d(TAG, "startLegalityTimer----->");
            mHandler.removeCallbacks(AccessLegalityTimer);
            mHandler.postDelayed(AccessLegalityTimer, 6 * 1000);
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
        LogUtil.d(TAG, "--resetBleState()---");
        isStartMeasure = false;
        BleConfigure.IS_CALIB_HINT = false;
        BleConfigure.IS_UPLOADING = false;
        isReqPower=false;
        mHandler.removeCallbacks(AccessLegalityTimer);
        isBleConnected = false;
        isSendAccess = false;
        curBleBattery = 0;
        mDeviceName = "";
        mTempDeviceName = "";
        isSend = false;
        blePowerLowHint = false;
        bleChargeState = false;
        if (null != editor) {
            editor.clearData();
        }
        if (null != analyser) {
            analyser.clearReceiveData();
        }
    }

    public boolean getConnectionState() {
//        if (null != mBluetoothLeService) {
//            return mBluetoothLeService.getConnectionState();
//        }
        return isBleConnected;
    }

    public boolean getBleServiceState() {
        if (null != mBluetoothLeService) {
            return mBluetoothLeService.getConnectionState();
        }
        return false;
    }

    /**
     * 发送不加密指令
     * Created by fWX581433 on 2018/8/3 11:26
     */
    public void sendData(byte[] data) {
        if (null != mHandler) {
            mHandler.removeCallbacks(AccessLegalityTimer);
        }
        if (null != editor) {
            editor.sendBleMessage_NO(data);
        }
    }

    //请求设备状态信息
    public void requestDeviceState() {
        LogUtil.d(TAG, "--requestDeviceState()---");
        isReqPower=true;
        if (null != editor) {
            editor.requestDeviceState();
        }
    }


    public void setCurDeviceName(String name) {
        if (!TextUtils.isEmpty(name)) {
            mDeviceName = name;
        } else {
            mDeviceName = "";
        }

    }

    public String getCurDeviceName() {
        return mDeviceName;
    }

    public int getCurDeviceBattery() {
        return mDeviceBattery;
    }

    /**
     * 获取当前充电状态
     *
     * @return
     */
    public boolean getBleChargeState() {
        return bleChargeState;
    }



    public void sendRebootDevice() {

        byte[] upgraderC = {0x5A, 0x00, 0x06, 0x00, 0x01, 0x1D, 0x01, 0x01, 0x01, (byte) 0xDE, (byte) 0xCA};
        if (null != editor) {
            editor.sendBleMessage(upgraderC);
        }
    }

    public void sendCanclUpgrader() {
        byte[] upgraderC = {0x5A, 0x00, 0x05, 0x00, 0x09, 0x08, 0x01, 0x00, (byte) 0x70, (byte) 0xFE};
        if (null != editor) {
            editor.sendBleMessage(upgraderC);
        }
    }

    public void sendSpectrumVersion() {
        if (null != editor) {
            editor.sendSpectrumVersion();
        }
    }

    public void sendSpectrumConfigData() {
        if (null != editor) {
            editor.sendSpectrumConfigData(spectrumVersion, spectrumConfigData);
        }
    }

    public void sendModelDebug() {
        if (null != editor) {
            editor.sendBleMessage_NO(StringUtil.hexStringToBytes("5a000600011c0101d16303"));
        }
    }

    public void sendModeCalibrate(Context context) {
        LogUtil.d(TAG, "sendModeCalibrate() ---->");
        calibrateModelContext = context;
        BleConfigure.BLE_Model = 0xC1;
        if (null != editor) {
            editor.sendModeCalibrate();
        }
    }

    /**
     * 请求光谱模组ID
     * Created by fWX581433 on 2018/7/30 15:52
     */
    public void requestspectrumID() {
        if (null != editor) {
            editor.requestspectrumID();
        }
    }

    /**
     * 获取高光谱ID
     * Created by fWX581433 on 2018/8/7 15:11
     */
    public String getSpectrumID() {
        return spectrumID;
    }

    /**
     * 获取蓝牙信号强度
     * Created by fWX581433 on 2018/8/8 15:17
     */
    public void getRssiVal() {
        if (null != mBluetoothLeService) {
            mBluetoothLeService.getRssiVal();
        }
    }

    /**
     * 获取当前手机蓝牙mac地址
     *
     * @return
     */
    public String getTelBlueToothMac() {

        return getBtAddressByReflection();
    }

    public String getBtAddressByReflection() {
        String bluetoothAddress = Settings.Secure.getString(YamanApplication.getInstance().getApplicationContext().getContentResolver(), "bluetooth_address");
        LogUtil.i(TAG, "bt---mac--->=" + bluetoothAddress);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Field field = null;
        try {
            field = BluetoothAdapter.class.getDeclaredField("mService");
            field.setAccessible(true);
            Object bluetoothManagerService = field.get(bluetoothAdapter);
            if (bluetoothManagerService == null) {
                return null;
            }
            Method method = bluetoothManagerService.getClass().getMethod("getAddress");
            if (method != null) {
                Object obj = method.invoke(bluetoothManagerService);
                if (obj != null) {
                    LogUtil.i(TAG, "bt---mac-1-->=" + obj.toString());
                    return obj.toString();
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    ///

    /**
     * 可维可测读取事件
     * Created by fWX581433 on 2018/7/17 20:09
     */
    private void maintenanceEvent(int infoID) {
        switch (infoID) {
            //发生Crash
            case 0x01:
                break;
            //发生严重错误
            case 0x02:
                break;
            //Log空间不足
            case 0x03:
                break;
            //用户行为空间不足
            case 0x07:
                break;
            default:
                break;
        }
    }

    /**
     * 测量数据异常
     * Created by fWX581433 on 2018/7/17 20:14
     */
    private void measureErrorEvent(int infoID) {
        switch (infoID) {
            //表皮水油数据异常
            case 0x10:

                break;
            //高光谱模组测试数据异常
            case 0x20:
                break;
            //高光谱模组温度异常
            case 0x30:
                break;
            //APP下发测量次数不在1~10范围内
            case 0x40:
                break;
            default:
                break;
        }
    }

    /**
     * 设备异常事件
     * Created by fWX581433 on 2018/7/17 20:16
     */
    private void deviceErrorEvent(int infoID) {
        switch (infoID) {
            //flash写操作错误
            case 0x10:
                break;
            //flash读操作异常
            case 0x20:
                break;
            default:
                break;
        }
    }



    public void release() {
        LogUtil.d(TAG, "release---->");
        if (null != mBluetoothLeService) {
            mBluetoothLeService.release();
        }
        if (null != instance) {
            instance = null;
        }
        mDeviceName = "";
        mDeviceBattery = 0;
    }


}