package cn.yaman.bluetooth;


import cn.yaman.bluetooth.common.BleConfigure;
import cn.yaman.bluetooth.device.BleCommandEditor;
import cn.yaman.util.LogUtil;

/**
 * Created by fWX581433 on 2018/7/13 15:03
 */
public final class RougeCommandEditor extends BleCommandEditor {
    private static final String TAG = RougeCommandEditor.class.getSimpleName();

    private static RougeCommandEditor instance;

    private RougeCommandEditor() {
        super();
    }

    private byte[] otaVersionByte;

    public synchronized static RougeCommandEditor getInstance() {
        if (null == instance) {
            instance = new RougeCommandEditor();
        }
        return instance;
    }

    /**
     * 设置OTA固件版本信息
     */
    public void setOtaVersion(byte[] data) {
        if (null != data) {
            otaVersionByte = data;
        }
    }

    /**
     * 请求设备状态信息
     */
    public void requestDeviceState() {
        LogUtil.d(TAG, "----requestDeviceState---cmd.");
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

    public void test() {
        byte[] bytes = {
                (byte) 0x55,
                (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x08
        };
        sendBleMessage(makeCommand(bytes));

    }

    /**
     * 固件升级
     * 1.5.9.1	查询单板是否允许升级
     */
    public void requestFirmwareVeneer() {
        //TLV:1.package_version 2.info_component_size  3.ota_work_mode
        byte[] version = null;
        if (null != otaVersionByte) {
            version = otaVersionByte;
        } else {
            version = new byte[4];
        }
        int len = version.length;
        byte[] arrayOfByte3 = new byte[11 + len];
        arrayOfByte3[0] = (byte) 0x09;
        arrayOfByte3[1] = (byte) 0x01;

        //TLV:1.package_version
        arrayOfByte3[2] = (byte) 0x01;
        arrayOfByte3[3] = (byte) len;
        System.arraycopy(version, 0, arrayOfByte3, 4, len);
        //TLV:1.info_component_size
        arrayOfByte3[len + 4] = (byte) 0x02;
        arrayOfByte3[len + 5] = (byte) 0x02;
        arrayOfByte3[len + 6] = (byte) 0x01;
        arrayOfByte3[len + 7] = (byte) 0x00;

        //TLV:1.ota_work_mode
        arrayOfByte3[len + 8] = (byte) 0x03;
        arrayOfByte3[len + 9] = (byte) 0x01;
        arrayOfByte3[len + 10] = (byte) 0x00;

        sendBleMessage_NO(makeCommand(arrayOfByte3));
    }

    /**
     * 固件升级
     * 2.5.9.2	升级参数协商
     */
    public void requestFirmwareUpgradeParams() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x09;
        bytes[1] = (byte) 0x02;
        sendBleMessage_NO(makeCommand(bytes));
    }

    /**
     * 固件升级
     * 3.5.9.9	APP升级状态通知 发送app准备就绪状态，请求设备上报
     */
    public void requestFirmwareReport() {
        //TLV:1.oat_app_status
        byte[] bytes = new byte[5];
        bytes[0] = (byte) 0x09;
        bytes[1] = (byte) 0x09;

        //TLV:1.oat_app_status
        bytes[2] = (byte) 0x01;
        bytes[3] = (byte) 0x01;
        bytes[4] = (byte) 0x01;

        sendBleMessage_NO(makeCommand(bytes));
    }

    /**
     * 固件升级
     * 4.5.9.4	升级包文件内容传输 发固件包
     */
    public void sendFirmwarePackage(byte[] pkg, int bitmap) {
        if (null == pkg) {
            return;
        }
        byte[] data = new byte[pkg.length + 3];
        data[0] = (byte) 0x09;
        data[1] = (byte) 0x04;
        data[2] = (byte) bitmap;
        System.arraycopy(pkg, 0, data, 3, pkg.length);
        sendBleMessage_NO(makeCommand(data));
    }

    /**
     * 固件升级
     * 5.5.9.6	升级包校验结果上报 接收完数据包，开始升级指令
     */
    public void sendStartFirmwareUpgrade() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x09;
        bytes[1] = (byte) 0x06;
        sendBleMessage_NO(makeCommand(bytes));
    }

    /**
     * 固件升级
     * 7.5.9.8	取消升级指令
     */
    public void cancelFirmwareUpgrade() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) 0x09;
        bytes[1] = (byte) 0x08;
        sendBleMessage_NO(makeCommand(bytes));
    }

    /**
     * 请求光谱模组ID
     */
    public void requestspectrumID() {
        LogUtil.d(TAG, "----requestspectrumID---cmd.");
        byte[] bytes = {
                (byte) 0x0A,
                (byte) 0x10,
        };

        sendBleMessage(makeCommand(bytes));
    }

    /**
     * 响应启动测量动作
     * Created by fWX581433 on 2018/8/7 11:18
     */
    public void responseMeasureAction() {
        LogUtil.d(TAG, "----responseMeasureAction---cmd.");
        byte[] bytes = {
                (byte) 0x01,
                (byte) 0x20,
                (byte) 0x01,
                (byte) 0x01,
                (byte) 0x02,
        };
        sendBleMessage(makeCommand(bytes));
    }

    /**
     * 测量启动指令：APP→设备：
     */
    public void requestMeasure() {
        LogUtil.d(TAG, "----requestMeasure---cmd.");
        byte[] bytes = {
                (byte) 0x01,
                (byte) 0x16,
                (byte) 0x01,
                (byte) 0x01,
                (byte) 0x0A
        };
        sendBleMessage(makeCommand(bytes));
    }

    /**
     * 校准启动指令：APP→设备：
     */
    public void requestCalibMeasure() {
        LogUtil.d(TAG, "----requestCalibMeasure---cmd.");
        byte[] bytes = {
                (byte) 0x01,
                (byte) 0x16,
                (byte) 0x01,
                (byte) 0x01,
                (byte) BleConfigure.totalCalibMeasure
        };
        sendBleMessage(makeCommand(bytes));
    }

    public void sendGetCalibrateData() {
        LogUtil.d(TAG, "----sendGetCalibrateData---cmd.");
        byte[] bytes = {
                (byte) 0x0A,
                (byte) 0x13,
        };
        sendBleMessage(makeCommand(bytes));
    }

    public void sendSynBleMode() {
        LogUtil.d(TAG, "----sendSynBleMode---cmd.");
        byte[] bytes = {
                (byte) 0x01,
                (byte) 0x24
        };
        sendBleMessage(makeCommand(bytes));
    }

    /**
     * 设置测量模式为仅高光谱
     */
    public void sendModeSpectrum() {
        LogUtil.d(TAG, "----sendModeSpectrum---cmd.");
        byte[] bytes = {
                (byte) 0x01,
                (byte) 0x1C,
                (byte) 0x01,
                (byte) 0x01,
                (byte) 0xA1,
        };
        sendBleMessage(makeCommand(bytes));
    }

    /**
     * 设置测量模式为水油加高光谱
     */
    public void sendModeOilWater() {
        LogUtil.d(TAG, "----sendModeOilWater---cmd.");
        byte[] bytes = {
                (byte) 0x01,
                (byte) 0x1C,
                (byte) 0x01,
                (byte) 0x01,
                (byte) 0xB1
        };
        sendBleMessage(makeCommand(bytes));
    }

    /**
     * 设置测量模式为校准
     */
    public void sendModeCalibrate() {
        LogUtil.d(TAG, "----sendModeCalibrate---cmd.");
        byte[] bytes = {
                (byte) 0x01,
                (byte) 0x1C,
                (byte) 0x01,
                (byte) 0x01,
                (byte) 0xC1
        };
        sendBleMessage(makeCommand(bytes));
    }

    /**
     * 获取设备版本信息
     */
    public void sendBleVersion() {
        LogUtil.d(TAG, "----sendBleVersion---cmd.");
        byte[] bytes = {
                (byte) 0x01,
                (byte) 0x07,
                (byte) 0x01,
                (byte) 0x00,
                (byte) 0x02,
                (byte) 0x00,
                (byte) 0x03,
                (byte) 0x00,
                (byte) 0x05,
                (byte) 0x00,
                (byte) 0x07,
                (byte) 0x00,
                (byte) 0x08,
                (byte) 0x00,
                (byte) 0x09,
                (byte) 0x00,
                (byte) 0x0A,
                (byte) 0x00,
                (byte) 0x0B,
                (byte) 0x00,
        };

        sendBleMessage(makeCommand(bytes));
    }

    /**
     * 设置高光谱配置参数
     */
    public void sendSpectrumParam(byte[] exposureTime, byte[] currentVal) {
        if (null == exposureTime || null == currentVal) {
            return;
        }
        LogUtil.d(TAG, "----sendSpectrumParam---cmd.");
        int eLen = exposureTime.length;
        int cLen = currentVal.length;

        byte[] bytes = new byte[4 + eLen + cLen];
        bytes[0] = (byte) 0x0A;
        bytes[1] = (byte) 0x0E;
        bytes[2] = (byte) (((eLen + cLen) / 128) | 0x80);
        bytes[3] = (byte) ((eLen + cLen) % 128);
        System.arraycopy(exposureTime, 0, bytes, 4, eLen);
        System.arraycopy(currentVal, 0, bytes, 4 + eLen, cLen);
        sendBleMessage(bytes);
    }

    /**
     * 获取高光谱配置版本信息
     * Created by fWX581433 on 2018/8/27 10:04
     */
    public void sendSpectrumVersion() {
        byte[] bytes = {
                (byte) 0x0A,
                (byte) 0x12
        };
        sendBleMessage(makeCommand(bytes));
    }

    public void sendCalibrateResult(boolean flag) {
        byte[] bytes = {
                (byte) 0x0A,
                (byte) 0x0F,
                (byte) 0x01,
                (byte) 0x01,
                (byte) 0x00
        };
        if (flag) {
            bytes[4] = (byte) 0x01;
        }
        sendBleMessage(makeCommand(bytes));
    }

    public void sendMeasureResult(boolean flag) {
        byte[] bytes = {
                (byte) 0x01,
                (byte) 0x23,
                (byte) 0x00,
        };
        if (flag) {
            bytes[2] = (byte) 0x01;
        }
        sendBleMessage(makeCommand(bytes));
    }

    /**
     * Created by fWX581433 on 2018/8/27 10:52
     */
    public void sendSpectrumConfigData(byte[] version, byte[] data) {
        if (null == version || null == data) {
            return;
        }
        int vLen = version.length;
        int len = data.length;
        int tLen = vLen + len + 6;
        if (len > 127) {
            tLen = tLen + 1;
        }
        byte[] bytes = new byte[tLen];
        bytes[0] = (byte) 0x0A;
        bytes[1] = (byte) 0x11;
        bytes[2] = (byte) 0x01;
        bytes[3] = (byte) vLen;
        System.arraycopy(version, 0, bytes, 4, vLen);
        bytes[vLen + 4] = (byte) 0x02;
        if (len > 127) {
            bytes[vLen + 5] = (byte) ((len / 128) | 0x80);
            bytes[vLen + 6] = (byte) (len % 128);
            System.arraycopy(data, 0, bytes, vLen + 7, len);
        } else {
            bytes[vLen + 5] = (byte) len;
            System.arraycopy(data, 0, bytes, vLen + 6, len);
        }
        sendBleMessage(makeCommand(bytes));
    }

    public void sendDoMeasrue() {
        byte[] bytes = {
                (byte) 0x01,
                (byte) 0x21,
                (byte) 0x01,
                (byte) 0x01,
                (byte) 0x01
        };
        sendBleMessage(makeCommand(bytes));
    }

    //5.10.1	获取单板侧的文件信息
    public void requestLogFiles() {
        byte[] bytes = {
                (byte) 0x0A,
                (byte) 0x01,
                (byte) 0x01,
                (byte) 0x03,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x01
        };
        sendBleMessage(makeCommand(bytes));
    }

    //5.10.2	文件传输协商参数
    public void requestDownloadLogParams() {
        byte[] bytes = {
                (byte) 0x0A,
                (byte) 0x02,
                (byte) 0x02,
                (byte) 0x05,
                (byte) 0x31,
                (byte) 0x2E,
                (byte) 0x30,
                (byte) 0x2E,
                (byte) 0x30
        };
        sendBleMessage(makeCommand(bytes));
    }

    //5.10.3	查询单个文件信息
    public void requestLogFileInfo() {
        byte[] bytes = {
                (byte) 0x0A,
                (byte) 0x03,
                (byte) 0x01,
                (byte) 0x03,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x01
        };
        sendBleMessage(makeCommand(bytes));
    }

    //5.10.4	向单板侧申请文件数据
    public void requestLogFileData() {
        byte[] bytes = {
                (byte) 0x0A,
                (byte) 0x04,
                (byte) 0x01,
                (byte) 0x03,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x01
        };
        sendBleMessage(makeCommand(bytes));
    }

}
