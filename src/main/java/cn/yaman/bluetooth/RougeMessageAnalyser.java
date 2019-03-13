package cn.yaman.bluetooth;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.yaman.bluetooth.callBack.BleDeviceStateCallBack;
import cn.yaman.bluetooth.device.BleMessageAnalyser;
import cn.yaman.util.LogUtil;
import cn.yaman.util.StringUtil;

/**
 * 胭脂蓝牙数据解析器
 * Created by Sergio Pan on 2017/12/8.
 */

public final class RougeMessageAnalyser extends BleMessageAnalyser {

    private static final String TAG = "RougeMessageAnalyser";

    private static RougeMessageAnalyser instance = new RougeMessageAnalyser();

    /////////数据解析后的各种回调///////////
    BleDeviceStateCallBack bleDeviceStateCallBack;

    private RougeMessageAnalyser() {
        super();
    }

    public synchronized static RougeMessageAnalyser getInstance() {
        if (null == instance) {
            instance = new RougeMessageAnalyser();
        }
        return instance;
    }

    public void setBleDeviceStateCallBack(BleDeviceStateCallBack callBack) {
        bleDeviceStateCallBack = callBack;
    }

    @Override
    protected void analyze(byte[] data) {
        LogUtil.i(TAG, StringUtil.byteToHexStr(data));
        if (null == data) {
            return;
        }
        int cmdType = data[1];
        byte[] bytes = Arrays.copyOfRange(data, 2, data.length - 1);
        switch (cmdType) {
            case 0xA1:
                devcieState(bytes);
                break;
        }
    }

    private void devcieState(byte[] bytes) {
        if (null != bleDeviceStateCallBack) {
            bleDeviceStateCallBack.onState(StringUtil.byteToInteger(bytes[0]), StringUtil.byteToInteger(bytes[1]), StringUtil.TwoByteToInteger(bytes[3], bytes[2]));
        }


    }
}
