package cn.yaman.bluetooth;


import cn.yaman.bluetooth.device.BleCommandEditor;

/**
 */
public final class RougeCommandEditor extends BleCommandEditor {
    private static final String TAG = RougeCommandEditor.class.getSimpleName();

    private static RougeCommandEditor instance;

    private RougeCommandEditor() {
        super();
    }


    public synchronized static RougeCommandEditor getInstance() {
        if (null == instance) {
            instance = new RougeCommandEditor();
        }
        return instance;
    }

    /**
     * 请求设备状态信息
     */
    public void requestDeviceState() {
        byte[] bytes = {
                (byte) 0x55,
                (byte) 0xA1,
                (byte) 0x05,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00
        };
        sendBleMessage(makeCommand(bytes));
    }

}
