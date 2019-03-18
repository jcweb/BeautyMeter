package cn.yaman.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;


import cn.yaman.BasePreference;
import cn.yaman.YamanApplication;
import cn.yaman.bluetooth.common.Fields;
import cn.yaman.bluetooth.common.FileNames;
import cn.yaman.bluetooth.device.BleScanner;
import cn.yaman.bluetooth.device.BluetoothLeService;


/**
 * @description 描 述：蓝牙管理器
 */
public abstract class BleManager {

    protected static final String TAG = BleManager.class.getSimpleName();


    protected static BleManager instance;
    protected BluetoothLeService mBluetoothLeService;
    protected Context mContext;
    protected BluetoothDevice curDevice;
    private static BleScanner mBleScanner;
    public static boolean isInit = false;

    /**
     * @methodName: BleManager
     * @description: 初始化蓝牙模块，
     */
    public boolean init(Context ctx) {
        instance = this;
        mContext = ctx;
        mBleScanner = BleScanner.getInstance();
        mBluetoothLeService = new BluetoothLeService();
        isInit = initBleService(ctx);
        return isInit;
    }

    protected abstract boolean initBleService(Context ctx);

    //    //搜索设备
    public void scanLeDevice(final boolean enable) {
        mBleScanner.scanLeDevice(enable);
    }

    /**
     * @MethodName: isDeviceBound
     * @Description: 是否已绑定设备
     */
    public boolean isDeviceBound() {
        //读取绑定的设备的mac
        BasePreference spf = new BasePreference(YamanApplication.getInstance().getApplicationContext());
        spf.open(FileNames.STRING_FILE);
        String mac = spf.getString(Fields.DEVICE_MAC);
        return mac != null && !mac.isEmpty();
    }

    public String getBoundDeviceMac() {
        //读取绑定的设备的mac
        BasePreference spf = new BasePreference(YamanApplication.getInstance().getApplicationContext());
        spf.open(FileNames.STRING_FILE);
        return spf.getString(Fields.DEVICE_MAC);
    }

    public String getBoundDeviceName() {
        //读取绑定的设备的name
        BasePreference spf = new BasePreference(YamanApplication.getInstance().getApplicationContext());
        spf.open(FileNames.STRING_FILE);
        return spf.getString(Fields.DEVICE_NAME);
    }

    /**
     * @MethodName: isDeviceBound
     * @Description: 绑定设备
     */
    public void bindDevice(BluetoothDevice device) {
        //存储绑定的设备的mac
        BasePreference spf = new BasePreference(YamanApplication.getInstance().getApplicationContext());
        spf.open(FileNames.STRING_FILE);
        spf.setString(Fields.DEVICE_MAC, device.getAddress());
        bindDeviceName(device.getName());
        //
    }

    /**
     * 5. 解绑设备
     */
    public void unbindDevice() {
        if (mBluetoothLeService != null) {
            mBluetoothLeService.unbindDevice();
        }
        //存储绑定的设备的mac为null，即解绑
        BasePreference spf = new BasePreference(YamanApplication.getInstance().getApplicationContext());
        spf.open(FileNames.STRING_FILE);
        spf.setString(Fields.DEVICE_MAC, null);
        bindDeviceName("");
    }

    public void bindDeviceName(String name) {
        //存储绑定的设备的name
        BasePreference spf = new BasePreference(YamanApplication.getInstance().getApplicationContext());
        spf.open(FileNames.STRING_FILE);
        spf.setString(Fields.DEVICE_NAME, name);
        //
    }

    /**
     * 获取当前连接的设备
     */
    public BluetoothDevice getCurDevice() {
        if (null != mBluetoothLeService) {
            return mBluetoothLeService.getCurDevice();
        }
        return null;
    }
    /**
     * 获取当前手机蓝牙状态
     */
    public boolean getPhoneBTState() {
        if (null != mBluetoothLeService) {
            return mBluetoothLeService.getBTState();
        }
        return false;
    }
}
