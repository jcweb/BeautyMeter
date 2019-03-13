package cn.yaman.bluetooth.device;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.yaman.bluetooth.BLETool;
import cn.yaman.bluetooth.common.BleConfigure;
import cn.yaman.util.LogUtil;
import cn.yaman.util.StringUtil;

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
    private Map<Integer, Byte[]> frameMap = new TreeMap<Integer, Byte[]>();
    protected byte[] preData;


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


    /**
     * 高8位低8位拼接
     *
     * @param high 高8位字节
     * @param low  低8位字节
     * @return
     */
    protected int getValueFromHLByte(byte high, byte low) {
        return ((high & 0xff) << 8) | (low & 0xff);
    }

    /**
     * 拆成高8位低8位
     * 下标0为高8位
     *
     * @param value 低8位字节
     * @return
     */
    protected byte[] getHLByteFromValue(int value) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((value & 0x00) << 8);
        bytes[1] = (byte) (value & 0xff);
        return bytes;
    }


    protected synchronized String getArrayString(byte[] data) {
        if (sb == null) {
            sb = new StringBuffer();
        }
        sb.setLength(0);
        int i = 0;
        while (i < data.length) {
            sb.append(Integer.toHexString(data[i] & 0xff)).append(" ");
            i++;
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    protected void sendToCommndEditor() {

    }

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
     * @author: Sergio Pan
     * @Time: 2018/6/26 15:43
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
            LogUtil.d(TAG, "---writeLlsAlertLevel----Receive--start--ConnectionState= false");
            return;
        }
        LogUtil.d(TAG, "---writeLlsAlertLevel----Receive--start--isEnd= " + isEnd);
        LogUtil.d(TAG, "---writeLlsAlertLevel----Receive--start--index= " + index);
        LogUtil.d(TAG, "---writeLlsAlertLevel----Receive--start--data= " + StringUtil.byteToHexStr(data));
        //过滤相同数据
//        if (isTheSameData(data)) {
//            return;
//        }
        LogUtil.d(TAG, "---writeLlsAlertLevel--dataHeaderReceive--data= " + data[0]);
        LogUtil.d(TAG, "---unpack--dataHeaderReceive--dataHeaderReceive= " + dataHeaderReceive);
        if (data[0] == dataHeaderReceive && !isEnd) {
            LogUtil.d(TAG, "---unpack--dataHeaderReceive--data= " + data[0]);
            isEnd = true;
            packageBuff.clear();
            mBytes = new byte[StringUtil.byteToInteger(data[2]) + 4];
            System.arraycopy(data, 0, mBytes, 0, data.length);
            index = data.length;

        } else {
//            System.arraycopy(data, 0, mBytes, index, data.length);
//            index = data.length + index;
        }
//
//        for (Byte b : data) {
//            packageBuff.add(b);
//        }
//        int buffLen = packageBuff.size();
//        byte[] mBytes = new byte[buffLen];
////        mBytes = packageBuff.toArray(mBytes);
//        for (int i = 0; i < buffLen; i++) {
//            mBytes[i] = packageBuff.get(i);
//        }
        //TODO 校验包完整性
        LogUtil.d(TAG, "--writeLlsAlertLevel----unpack---11--bytes = " + StringUtil.byteToHexStr(mBytes));
        if (index == (mBytes.length)) {
            LogUtil.d(TAG, "------unpack---Receive--bytes = " + StringUtil.byteToHexStr(mBytes));
            LogUtil.d(TAG, "------unpack---Receive--bytes = " + StringUtil.byteToInteger(mBytes[mBytes.length-1]));
            int result = StringUtil.CRC_XModem(mBytes);
            if (result > 0) {
                LogUtil.e(TAG, "---unpack-crc16 result = " + result);
            } else {
                LogUtil.d(TAG, "---writeLlsAlertLevel--crc16 index = " + index);
                LogUtil.d(TAG, "---writeLlsAlertLevel--crc16 mBytes = " + mBytes.length);
                LogUtil.d(TAG, "---writeLlsAlertLevel--crc16 result = " + result);
            }

            if (result == 0) {
                isEnd = false;
                index = 0;
                final int len = mBytes.length;
                byte[] bytes = new byte[len];
//                for (int i = 0; i < len; i++) {
//                    bytes[i] = mBytes[i];
//                }
                System.arraycopy(mBytes, 0, bytes, 0, len);
                //TODO 调实现者analyze分析数据方法
                LogUtil.d(TAG, "------writeLlsAlertLevel---Receive--End--bytes = " + StringUtil.byteToHexStr(bytes));
                analyze(bytes);
//                int ctrl = bytes[3] & 0xff;
//                if (0 == ctrl) {
//                    if(BleConfigure.isDecrypt){
//                        analyze(decrypt(bytes));
//                    }else{
//                        analyze(bytes);
//                    }
//
//                } else {
//                    switch (ctrl) {
//                        case 1:
//                            counts = 0;
//                            frameMap.clear();
//                            frameMap.put(0, toObjects(Arrays.copyOfRange(decrypt(bytes), 5, len - 2)));
//                            counts = (len - 2);
//                            LogUtil.d(TAG, "------writeLlsAlertLevel---Receive--1--counts = " + counts);
//                            break;
//                        case 2:
//                            frameMap.put((bytes[4] & 0xff), toObjects(Arrays.copyOfRange(decrypt(bytes), 5, len - 2)));
//                            counts += (len - 2);
//                            LogUtil.d(TAG, "------writeLlsAlertLevel---Receive--2--counts = " + counts);
//                            break;
//                        case 3:
//                            frameMap.put((bytes[4] & 0xff), toObjects(Arrays.copyOfRange(decrypt(bytes), 5, len - 2)));
//                            counts += (len - 2);
//                            LogUtil.d(TAG, "------writeLlsAlertLevel---Receive--3--counts = " + counts);
//                            if(BleConfigure.isDecrypt){
//                                analyze(decrypt(paserMap(frameMap, counts + 6)));
//                            }else{
//                                analyze(paserMap(frameMap, counts + 6));
//                            }
//                            break;
//                    }
//                }

            }
        }
    }

    int counts = 0;

    private byte[] toPrimitives(Byte[] oBytes) {
        byte[] bytes = new byte[oBytes.length];

        for (int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }

        return bytes;
    }

    private Byte[] toObjects(byte[] bytesPrim) {
        Byte[] bytes = new Byte[bytesPrim.length];

        int i = 0;
        for (byte b : bytesPrim) bytes[i++] = b; // Autoboxing

        return bytes;
    }

    public int getIntFromBytes(byte paramByte1, byte paramByte2) {
        return (paramByte1 & 0xFF) << 8 | paramByte2 & 0xFF;
    }

    private byte[] paserMap(Map<Integer, Byte[]> map, int len) {
        byte[] bytes = new byte[len];
        bytes[0] = 0x5a;
        bytes[1] = (byte) ((len - 5) >>> 8);
        bytes[2] = (byte) (len - 5);
        bytes[3] = 0x00;
        int index=4;
        for (Iterator<Integer> it = map.keySet().iterator(); it.hasNext(); ) {
            Byte[] data = map.get(it.next());
            int l=data.length;
            LogUtil.d(TAG, "------writeLlsAlertLevel---paserMap----data = " + StringUtil.byteToHexStr(toPrimitives(data)));
            System.arraycopy(toPrimitives(data),0,bytes,index,l);
            LogUtil.d(TAG, "------writeLlsAlertLevel---paserMap----bytes = " + StringUtil.byteToHexStr(bytes));
            index+=l;
        }
        return bytes;
    }



    /**
     * 解析type
     * Created by fWX581433 on 2018/6/26 11:38
     */
    protected int[] paserType(int type) {
        int[] ints = new int[2];
        String str = String.format("%08d", Integer.valueOf(Integer.toBinaryString(type)));
        LogUtil.d(TAG, "paserType--->type=" + str);
        int bit7 = Integer.valueOf(str.substring(0, 1));
        ints[0] = bit7;
        LogUtil.d(TAG, "paserType--->bit7=" + bit7);
        int dataType = Integer.parseInt(str.substring(1), 2);
        LogUtil.d(TAG, "paserType--->dataType=" + dataType);
        ints[1] = dataType;
        return ints;
    }

    /**
     * 解析Length
     * Created by fWX581433 on 2018/6/26 11:38
     */
    protected int paserLength(byte l1, byte l2) {
        int[] ints = new int[2];
        String s1 = String.format("%08d", Integer.valueOf(Integer.toBinaryString(StringUtil.byteToInteger(l1))));
        String s2 = String.format("%08d", Integer.valueOf(Integer.toBinaryString(StringUtil.byteToInteger(l2))));
        LogUtil.d(TAG, "paserLength--->" + s1 + s2);
        int bit7 = Integer.valueOf(s1.substring(0, 1));
        ints[0] = bit7;
        LogUtil.d(TAG, "paserLength--->bit7=" + bit7);
        int dataLen = Integer.parseInt(s1.substring(1) + s2.substring(1), 2);
        LogUtil.d(TAG, "paserLength--->dataLen=" + dataLen);
        return dataLen;
    }

    public void onRelease() {
        sb = null;
    }

    public void clearReceiveData() {
        index = 0;
        isEnd = false;
        packageBuff.clear();
        LogUtil.d(TAG, "---writeLlsAlertLevel----clearData");
    }
}
