package cn.yaman.bluetooth.device;


import android.os.Handler;


import cn.yaman.bluetooth.BLETool;
import cn.yaman.bluetooth.device.callback.BleWriteDataResultCallBack;
import cn.yaman.bluetooth.device.callback.IBleServerWriter;
import cn.yaman.utils.StringUtil;


import java.util.ArrayList;
import java.util.List;

/**
 * 蓝牙指令编辑者，抽象层，具体由开发者实现，用于蓝牙数据协议，编辑蓝牙指令
 */
public class BleCommandEditor {

    protected final static String TAG = "BleCommandEditor";
    protected IBleServerWriter mBleMessageWriter;
    protected StringBuffer sb;
    protected byte dataHeaderSend;
    private List<byte[]> pList = new ArrayList<>();
    private Handler handler = new Handler();
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
        if (!BLETool.getInstance().getIsSend()) {
            BLETool.getInstance().setIsSend(true);
            paserData(data);
        }
    }

    private synchronized void paserData(byte[] bb) {
        pList.add(bb);
        sendData();
    }

    private synchronized void sendData() {
        if (null != pList && pList.size() != 0) {
            if (null != handler) {
                handler.removeCallbacks(sendRun);
                handler.removeCallbacks(timeOut);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (mBleMessageWriter != null) {
                        if (pList.size() > 0) {
                            BLETool.getInstance().setIsSend(true);
                            mBleMessageWriter.writeLlsAlertLevel("paserData", pList.get(0));
                        }
                    }
                }
            }).start();
        }
    }

    private Runnable timeOut = new Runnable() {
        @Override
        public void run() {
            if (null != handler) {
                handler.removeCallbacks(timeOut);
            }
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
            if (null != handler) {
                handler.removeCallbacks(timeOut);
                handler.removeCallbacks(sendRun);
            }
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

        return data;
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
        commandOfBytes[cLen - 1] = StringUtil.xor(paramArrayOfByte);
        return commandOfBytes;
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
