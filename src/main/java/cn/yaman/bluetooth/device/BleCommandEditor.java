package cn.yaman.bluetooth.device;


import android.os.Handler;


import cn.yaman.bluetooth.BLETool;
import cn.yaman.bluetooth.common.BleConfigure;
import cn.yaman.bluetooth.device.callback.BleWriteDataResultCallBack;
import cn.yaman.bluetooth.device.callback.IBleServerWriter;
import cn.yaman.util.LogUtil;
import cn.yaman.util.StringUtil;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 蓝牙指令编辑者，抽象层，具体由开发者实现，用于蓝牙数据协议，编辑蓝牙指令
 */
public class BleCommandEditor {

    protected final static String TAG = "BleCommandEditor";
    protected IBleServerWriter mBleMessageWriter;
    protected StringBuffer sb;
    protected byte dataHeaderSend;
    private boolean isPaserPackage = false;
    private List<byte[]> pList = new ArrayList<>();
    //    private boolean isSend = false;
    private Handler handler = new Handler();
    private Object object = new Object();
    private List<byte[]> cmdList = new ArrayList<>();

    protected BleCommandEditor() {

    }

    public void setDataHeaderSend(byte dataHeaderSend) {
        this.dataHeaderSend = dataHeaderSend;
    }

    public void registerBleMessageWriter(IBleServerWriter bleMessageWriter) {
        this.mBleMessageWriter = bleMessageWriter;
    }

    public void sendBleMessage(final byte[] data) {
        LogUtil.d(TAG, "------sendBleMessage----加密前的原始指令---data = " + StringUtil.byteToHexStr(data));
//        pList.add(encrypt(data));
//        sendData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (BleConfigure.isDecrypt) {
                    frameData(encrypt(data));
                } else {
                    frameData(data);
                }
            }
        }).start();

    }

    //
    public void sendBleMessage_NO(final byte[] data) {
        LogUtil.d(TAG, "------sendBleMessage---不加密的原始指令---data = " + StringUtil.byteToHexStr(data));
//        pList.add(data);
//        sendData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                    frameData(data);
            }
        }).start();
    }

    private synchronized void frameData(byte[] encryptData) {
        LogUtil.i(TAG, "makeCommand------00---->" + StringUtil.byteToHexStr(encryptData));
        int cLen = encryptData.length;
        if (cLen > 254) {
            byte[] b1 = new byte[254];
            b1[0] = dataHeaderSend;
            b1[1] = getHLByteFromValue(254 - 5)[0];
            b1[2] = getHLByteFromValue(254 - 5)[1];
            b1[3] = 0x01;
            b1[4] = 0x00;
            encryptData = Arrays.copyOfRange(encryptData, 4, encryptData.length - 2);
            LogUtil.i(TAG, "makeCommand-----010----->" + StringUtil.byteToHexStr(encryptData));
            cLen = encryptData.length;
            System.arraycopy(encryptData, 0, b1, 5, 247);
            byte[] c1 = Arrays.copyOfRange(b1, 0, 252);
            int t = StringUtil.CRC_XModem(c1);
            b1[252] = (byte) (t >>> 8);
            b1[253] = (byte) t;
            cmdList.add(b1);
            LogUtil.i(TAG, "makeCommand-----111----->" + StringUtil.byteToHexStr(b1));
            byte[] b2 = new byte[254];
            b2[0] = dataHeaderSend;
            b2[1] = getHLByteFromValue(254 - 5)[0];
            b2[2] = getHLByteFromValue(254 - 5)[1];
            b2[3] = 0x02;
            b2[4] = 0x01;
            System.arraycopy(encryptData, 247, b2, 5, 247);
            byte[] c2 = Arrays.copyOfRange(b2, 0, 252);
            int t1 = StringUtil.CRC_XModem(c2);
            b2[252] = (byte) (t1 >>> 8);
            b2[253] = (byte) t1;
            cmdList.add(b2);
            LogUtil.i(TAG, "makeCommand-----222----->" + StringUtil.byteToHexStr(b2));
            byte[] b3 = new byte[cLen - 247*2 + 7];
            b3[0] = dataHeaderSend;
            b3[1] = getHLByteFromValue(cLen - 247*2 + 2)[0];
            b3[2] = getHLByteFromValue(cLen - 247*2 + 2)[1];
            b3[3] = 0x03;
            b3[4] = 0x02;
            System.arraycopy(encryptData, 247*2, b3, 5, cLen - 247*2);
            byte[] c3 = Arrays.copyOfRange(b3, 0, cLen - 247*2 + 7 - 2);
            int t3 = StringUtil.CRC_XModem(c3);
            b3[cLen - 247*2 + 7 - 2] = (byte) (t3 >>> 8);
            b3[cLen - 247*2 + 7 - 1] = (byte) t3;
            cmdList.add(b3);
            LogUtil.i(TAG, "makeCommand----333------>" + StringUtil.byteToHexStr(b3));
//        encryptData = new byte[b1.length+b2.length];
//        System.arraycopy(b1,0,encryptData,0,b1.length);
//        System.arraycopy(b2,0,encryptData,b1.length,b2.length);
//        LogUtil.i(TAG,"makeCommand------1---->"+StringUtil.byteToHexStr(encryptData));
        } else {
            cmdList.add(encryptData);
        }
        if(!BLETool.getInstance().getIsSend()){
            BLETool.getInstance().setIsSend(true);
            paserData(cmdList.get(0));
        }

    }

    private synchronized void paserData(byte[] bb) {
        LogUtil.d(TAG, "----writeLlsAlertLevel-- 分片前的原始指令-- status=" + ", bb = " + StringUtil.byteToHexStr(bb));
        int len = bb.length;
        int length = BleConfigure.supportedMTU;
        if (len > length) {
            isPaserPackage = true;
            int tl = 0;
            if (len % length == 0) {
                tl = (len / length);
            } else {
                tl = (len / length) + 1;
            }

//            pList.clear();
            for (int i = 0; i < tl; i++) {
                int offset = length * i;
                int l = len - offset;
                if (l > 0 && l <= length) {
                    length = l;
                }
                final byte[] b = new byte[length];
                System.arraycopy(bb, offset, b, 0, length);
                pList.add(b);
            }
            sendData();
        } else {
            isPaserPackage = false;
            pList.add(bb);
            sendData();
        }

    }

    private synchronized void sendData() {
        if (null != pList && pList.size() != 0) {
            if (null != handler) {
                handler.removeCallbacks(sendRun);
                handler.removeCallbacks(timeOut);
            }
//            handler.postDelayed(timeOut, 1000);
            LogUtil.d(TAG, "writeLlsAlertLevel----sendData--->start--->b=" + StringUtil.byteToHexStr(pList.get(0)));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (mBleMessageWriter != null) {
                        if (pList.size() > 0) {
                            BLETool.getInstance().setIsSend(true);
                            LogUtil.d(TAG, "writeLlsAlertLevel----sendData--->run--->b=" + StringUtil.byteToHexStr(pList.get(0)));
                            mBleMessageWriter.writeLlsAlertLevel("paserData", pList.get(0));
                        }
                    }
                }
            }).start();
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

    private Runnable timeOut = new Runnable() {
        @Override
        public void run() {
            if (null != handler) {
                handler.removeCallbacks(timeOut);
            }
            LogUtil.d(TAG, "writeLlsAlertLevel----sendData--->timeOut");
//            sendData();
        }
    };
    private Runnable sendRun = new Runnable() {
        @Override
        public void run() {
            sendData();
        }
    };

    public BleWriteDataResultCallBack getBleWriteDataResultCallBack() {
        return bleWriteDataResultCallBack;
    }

    private BleWriteDataResultCallBack bleWriteDataResultCallBack = new BleWriteDataResultCallBack() {
        @Override
        public void onResult(boolean result) {
            LogUtil.d(TAG, "writeLlsAlertLevel----BleWriteDataResultCallBack--->result=" + result);
            if (null != handler) {
                handler.removeCallbacks(timeOut);
                handler.removeCallbacks(sendRun);
            }
//            if (result) {
            if (pList.size() != 0) {
                pList.remove(0);
            }

            if (cmdList.size() > 0 && 0 == pList.size()) {
                cmdList.remove(0);
                if (cmdList.size() != 0) {
                    paserData(cmdList.get(0));
                    return;
                } else {
                    BLETool.getInstance().setIsSend(false);
                }
            }
//            }
            handler.post(sendRun);
        }
    };

    /**
     * 加密处理，默认为不加密，如数据需要加密，需重写此方法
     *
     * @param data
     * @return
     */
    protected byte[] encrypt(byte[] data) {
        //
        byte[] encrypt_type = new byte[]{0x7C, 0x01, 0x01};

        return encrypt_type;
    }

    public byte xor(byte[] bytes) {

        byte x = bytes[1];
        int len = bytes.length;
        for (int i = 2; i < len; i++) {
            x ^= bytes[i];
        }

        return x;
    }

    /**
     * 组包
     */
    public byte[] makeCommand(byte[] paramArrayOfByte) {
        int len = paramArrayOfByte.length;
        int cLen = len + 1;
        byte[] commandOfBytes = new byte[cLen];
        System.arraycopy(paramArrayOfByte, 0, commandOfBytes, 0, len);
        commandOfBytes[cLen - 1] = StringUtil.CRC_XModem(paramArrayOfByte);
        return commandOfBytes;
    }

    protected byte[] hexStrToBytes(String paramString) {
        byte[] arrayOfByte;
        if ((paramString == null) || (paramString.equals(""))) {
            arrayOfByte = null;
            return arrayOfByte;
        }
        int i = paramString.length();
        int j = i / 2;
        if (j * 2 < i) {
            j++;
        }
        for (char[] arrayOfChar = ("0" + paramString).toCharArray(); ; arrayOfChar = paramString.toCharArray()) {
            arrayOfByte = new byte[j];
            for (int k = 0; k < j; k++) {
                int m = k * 2;
                arrayOfByte[k] = ((byte) (charToByte(arrayOfChar[m]) << 4 | charToByte(arrayOfChar[(m + 1)])));
            }
            break;
        }
        return arrayOfByte;
    }

    public byte charToByte(char paramChar) {
        byte b = (byte) "0123456789abcdef".indexOf(paramChar);
        if (b == -1) {
            b = (byte) "0123456789ABCDEF".indexOf(paramChar);
        }
        return b;
    }

    public String numToHex16(int paramInt) {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Integer.valueOf(paramInt);
        String str = String.format("%04x", arrayOfObject);
        if (str.length() > 4) {
            str = str.substring(-4 + str.length());
        }
        return str;
    }


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

    public void release() {

        mBleMessageWriter = null;
        sb = null;
    }

    public void clearData() {
        pList.clear();
        cmdList.clear();
    }

}
