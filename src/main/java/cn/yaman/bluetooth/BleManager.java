package cn.yaman.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;


import java.io.InputStream;
import java.util.Arrays;

import cn.yaman.BasePreference;
import cn.yaman.YamanApplication;
import cn.yaman.bluetooth.common.Fields;
import cn.yaman.bluetooth.common.FileNames;
import cn.yaman.bluetooth.device.BleScanner;
import cn.yaman.bluetooth.device.BluetoothLeService;
import cn.yaman.util.LogUtil;


/**
 * @description 描 述：蓝牙管理器
 */
public abstract class BleManager {

    protected static final String TAG = BleManager.class.getSimpleName();


    protected static BleManager instance;
    protected BluetoothLeService mBluetoothLeService;
    protected Context mContext;
    protected BluetoothDevice curDevice;
    public byte[] otaByte;
    private byte[] otaVersionByte;
    public long otaByteLen;
    public boolean mBlnFileRdy = false;

    private String newBtVersion = "";
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
//        readOTAFileA("Rouge_101.bin");
        isInit = initBleService(ctx);
        return isInit;
    }

    protected abstract boolean initBleService(Context ctx);

    public void readOTAFileA(String fileName) {
        try {
            InputStream fis = YamanApplication.getInstance().getApplicationContext().getAssets().open(fileName);
            int i = fis.available();
            otaByte = new byte[i];
            fis.read(otaByte, 0, i);
            otaByteLen = otaByte.length;
            mBlnFileRdy = true;
        } catch (Exception e) {
            e.printStackTrace();
            mBlnFileRdy = false;
        }
        otaVersionByte = Arrays.copyOfRange(otaByte, 43, 47);
        StringBuffer sb = new StringBuffer();

        for (byte b : otaVersionByte) {
            sb.append((b & 0xFF) + ".");
        }
        sb.deleteCharAt(sb.length() - 1);
        newBtVersion = sb.toString();

    }

    public byte[] getOtaData() {
        return otaByte;
    }

    public byte[] getOtaVersionByte() {
        return otaVersionByte;
    }

    public String getNewCurBleVersion() {
        return newBtVersion;
    }

    //    //搜索设备
    public void scanLeDevice(final boolean enable) {
        LogUtil.d(TAG, "scanLeDevice----------->" + enable);
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

    //保存设备ID
    public void saveDeviceID(String mac, String id) {
        //存储绑定的设备的name
        BasePreference spf = new BasePreference(YamanApplication.getInstance().getApplicationContext());
        spf.open(FileNames.STRING_FILE);
        spf.setString(mac, id);
        //
    }

    public String getDeviceID(String mac) {
        //存储绑定的设备的name
        BasePreference spf = new BasePreference(YamanApplication.getInstance().getApplicationContext());
        spf.open(FileNames.STRING_FILE);
        return spf.getString(mac);
        //
    }

    //保存设备ID
    public void savePhoneMac(String mac) {
        //存储绑定的设备的name
        BasePreference spf = new BasePreference(YamanApplication.getInstance().getApplicationContext());
        spf.open(FileNames.STRING_FILE);
        spf.setString("PhoneMac", mac);
        //
    }

    public String getPhoneMac() {
        //存储绑定的设备的name
        BasePreference spf = new BasePreference(YamanApplication.getInstance().getApplicationContext());
        spf.open(FileNames.STRING_FILE);
        return spf.getString("PhoneMac");
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
