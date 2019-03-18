package cn.yaman.bluetooth.device;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.yaman.bluetooth.BLETool;
import cn.yaman.utils.StringUtil;

/**
 * 蓝牙返回信息解析者，用于蓝牙数据协议，解析蓝牙数据
 * 1.解密
 * 2.解析
 * 3.区分业务
 * 4.回调接口，返回结果
 */
public abstract class BleMessageAnalyser {

    private StringBuffer sb;
    private static final String TAG = "BleMessageAnalyser";
    //数据解析后的各种回调
    protected byte dataHeaderReceive;
    protected List<Byte> packageBuff = new ArrayList<>();


    public void setDataHeaderReceive(byte dataHeaderReceive) {
        this.dataHeaderReceive = dataHeaderReceive;
    }

    /**
     * 接收数据
     *
     * @param data
     */
    public void onReceiveBleMessage(byte[] data) {
        //解压
        unpack(data);
    }

    /**
     * 解密
     *
     * @param data
     * @return
     */
    protected byte[] decrypt(byte[] data) {
        return data;
    }

    /**
     * 解析数据
     *
     * @param data
     */
    protected abstract void analyze(byte[] data);


    private SimpleDateFormat sdf;

    protected String getMeasureTimeStr(long measureTime) {
        if (sdf == null) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        Date curDate = new Date(measureTime);
        return sdf.format(curDate);
    }

    /**
     * @methodName: makePackage
     * @description: 组包
     */
    private boolean isEnd = false;
    int index = 0;
    byte[] mBytes = null;

    protected synchronized void unpack(byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }
        if (!BLETool.getInstance().getBleServiceState()) {
            index = 0;
            isEnd = true;
            packageBuff.clear();
            return;
        }
        if (data[0] == dataHeaderReceive && !isEnd) {
            isEnd = true;
            packageBuff.clear();
            mBytes = new byte[StringUtil.byteToInteger(data[2]) + 4];
            System.arraycopy(data, 0, mBytes, 0, data.length);
            index = data.length;

        } else {
//            System.arraycopy(data, 0, mBytes, index, data.length);
//            index = data.length + index;
        }

        //TODO 校验包完整性
        if (index == (mBytes.length)) {
            int result = StringUtil.xor(mBytes);
            if (result == 0) {
                isEnd = false;
                index = 0;
                final int len = mBytes.length;
                byte[] bytes = new byte[len];
                System.arraycopy(mBytes, 0, bytes, 0, len);
                analyze(bytes);

            }
        }
    }

    public void onRelease() {
        sb = null;
    }

    public void clearReceiveData() {
        index = 0;
        isEnd = false;
        packageBuff.clear();
    }
}
