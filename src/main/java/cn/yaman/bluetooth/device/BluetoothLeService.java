/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.yaman.bluetooth.device;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;


import cn.yaman.YamanApplication;
import cn.yaman.bluetooth.BLETool;
import cn.yaman.bluetooth.RougeMessageAnalyser;
import cn.yaman.bluetooth.common.BleConfigure;
import cn.yaman.bluetooth.common.BluetoothUtil;
import cn.yaman.bluetooth.device.callback.BleSatusCallBack;
import cn.yaman.bluetooth.device.callback.BleWriteDataResultCallBack;
import cn.yaman.bluetooth.device.callback.ConnectCallBack;
import cn.yaman.bluetooth.device.callback.IBleServerWriter;
import cn.yaman.bluetooth.device.callback.ScanResultCallBack;
import cn.yaman.bluetooth.device.receiver.BleStatusReceiver;
import cn.yaman.util.LogUtil;
import cn.yaman.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService implements IBleServerWriter, ScanResultCallBack, BleSatusCallBack {
    private final static String TAG = "BluetoothLeService";
    private String bleAddress;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattService btService = null;
    private int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public final static UUID UUID_WRITE =
            UUID.fromString(BleConfigure.UUID_WRITE);
    public final static UUID UUID_READ =
            UUID.fromString(BleConfigure.UUID_READ);
    public final static UUID UUID_SERVICE =
            UUID.fromString(BleConfigure.UUID_SERVICE);
    /////
    private ConnectCallBack connectCallback;
    private ScanResultCallBack scanResultCallback;
    private BluetoothDevice curDevice;
    private BleStatusReceiver mReceiver;
    private RougeMessageAnalyser rougeMessageAnalyser;
    private static boolean isAutoConnect = true;
    private static BluetoothLeService instance;
    private List<Byte> buffer = new ArrayList<>();
    public static boolean isWriteOK = false;
    private BleWriteDataResultCallBack bleWriteDataResultCallBack;
    private boolean isMTU = false;
    int supportedMTU = 263;
    private boolean recon = false;
    private boolean phoneBtState = false;
    private int curConMax = 4;
    private long curStartConTime = 0;
    private static List<BluetoothDevice> searchDeviceList;

    public void setBleWriteDataResultCallBack(BleWriteDataResultCallBack callBack) {
        bleWriteDataResultCallBack = callBack;
    }

    //
//    public synchronized static BluetoothLeService getInstance() {
//        if (instance == null) {
//            instance = new BluetoothLeService();
//        }
//        return instance;
//    }
    public BluetoothLeService() {
        init();
    }

    private void init() {
        searchDeviceList = new ArrayList<>();
        BleScanner.getInstance().setScanResultCallBack(this);
        mReceiver = BleStatusReceiver.getInstance();
        mReceiver.registerBleStatusReceiver(YamanApplication.getInstance().getApplicationContext());
        mReceiver.putBleSatusCallback(this);
    }

    public void setConnectCallback(ConnectCallBack callBack) {
        this.connectCallback = callBack;
    }

    public void setScanResultCallBack(ScanResultCallBack callBack) {
        scanResultCallback = callBack;
    }

    public void setRougeMessageAnalyser(RougeMessageAnalyser messageAnalyser) {
        rougeMessageAnalyser = messageAnalyser;
    }

    //////
    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            LogUtil.d(TAG, "  onConnectionStateChange:--status=" + status);
            LogUtil.d(TAG, "  onConnectionStateChange:--newState=" + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                recon = false;
                phoneBtState = true;
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                BLETool.getInstance().scanLeDevice(false);
                LogUtil.d(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                LogUtil.d(TAG, "Attempting to start service discovery:" +
                        gatt.discoverServices());
                if (null != connectCallback) {
                    connectCallback.onConnected();
                }

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                LogUtil.d(TAG, "---onConnectionStateChange-->disconnect()---> ");
                curDevice = null;
//
                int time = (int) (System.currentTimeMillis() - curStartConTime) / 1000;

                if (133 == status && !recon && isAutoConnect && curConMax > 0 && time < 20) {
                    disConnectDevice(gatt, 0);
                    LogUtil.d(TAG, "Disconnected from GATT server.133");
                    recon = true;
                    mConnectionState = STATE_DISCONNECTED;
                    if (null != handler) {
                        handler.sendEmptyMessageDelayed(2, 500);
                    }
                } else {
                    curConMax = 4;
                    disConnectDevice(gatt, 1);
                    LogUtil.d(TAG, "Disconnected from GATT server.");
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            LogUtil.d(TAG, " onServicesDiscovered-->");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                btService = gatt.getService(UUID.fromString(UUID_SERVICE.toString()));
                if (null == btService) {
                    LogUtil.d(TAG, "---onServicesDiscovered-->disconnect()---> ");
                    disConnectDevice(gatt, 1);
                    return;
                }
                List<BluetoothGattCharacteristic> characteristics = btService.getCharacteristics();
                for (BluetoothGattCharacteristic characteristic : characteristics) {
                    LogUtil.d(TAG, "-----onServicesDiscovered-------characteristic uuid:" + characteristic.getUuid().toString());
                    setCharacteristicNotification(gatt, characteristic, true);
                }
//                BLETool.getInstance().startLegalityTimer();
            } else {
                disConnectDevice(gatt, 1);
//                gatt.disconnect();
                //gatt.close();
                LogUtil.d(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            LogUtil.d(TAG, "onReadRemoteRssi received: rssi=" + rssi);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                isMTU = true;
                supportedMTU = mtu;//local var to record MTU size
                LogUtil.d(TAG, "onMtuChanged received: mtu=" + mtu);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            LogUtil.d(TAG, "---读回调操作-----");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (rougeMessageAnalyser != null) {
                    rougeMessageAnalyser.onReceiveBleMessage(characteristic.getValue());
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            LogUtil.d(TAG, "---写回调操作---");
            if (status == BluetoothGatt.GATT_SUCCESS) {//写入成功
                LogUtil.d(TAG, "写入成功-->isWriteOK=" + isWriteOK);
                if (isWriteOK) {
                    if (null != bleWriteDataResultCallBack) {
                        bleWriteDataResultCallBack.onResult(true);
                    }
                } else {
                    if (null != bleWriteDataResultCallBack) {
                        bleWriteDataResultCallBack.onResult(false);
                    }
                }
            } else if (status == BluetoothGatt.GATT_FAILURE) {
                LogUtil.d(TAG, "---onCharacteristicWrite-写入失败->disconnect()---> ");
                disConnectDevice(gatt, 1);
                LogUtil.d(TAG, "写入失败");
            } else if (status == BluetoothGatt.GATT_WRITE_NOT_PERMITTED) {
                LogUtil.d(TAG, "---onCharacteristicWrite-没权限->disconnect()---> ");
                disConnectDevice(gatt, 1);
                LogUtil.d(TAG, "没权限");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            if (rougeMessageAnalyser != null) {
                rougeMessageAnalyser.onReceiveBleMessage(characteristic.getValue());
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor gattDescriptor, int status) {
            LogUtil.w(TAG, "--------onDescriptorWrite---123-----status = " + status);
            if (status != 0) {
                LogUtil.d(TAG, "-----onDescriptorWrite-----status not 0, do disconnect.");
                //状态码出错，先断开连接然后再重新连接
                gatt.disconnect();
            } else {
//                if (null != connectCallback) {
//                    connectCallback.onConnected();
//                }
                //这里通知前端，设备连接成功
                UUID uuid = gattDescriptor.getCharacteristic().getUuid();
                LogUtil.d(TAG, "-----onDescriptorWrite------UUID = " + uuid.toString());


                setMutiNotify(gatt, uuid, 1);

//                setCharacteristicNotification(btService, true);
                //byte[] bytes = {(byte) 0x5a, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x01, (byte) 0x1c, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0xa8, (byte) 0x7e};
                // byte[] bytes = {0x5A, 0x00, 0x05, 0x00, 0x01, 0x08, 0x01, 0x0, (byte) 0Xf5, 0x3d};
//                try {
//                    Thread.sleep(600);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                writeLlsAlertLevel(SampleGattAttributes.BLUETOOTH_MODEL_OILWATER_SPECTRUM);
//                SecureAccess.getInstance().sendAccessLegality();
                BLETool.getInstance().requestDeviceState();
            }

        }
    };
    // }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        LogUtil.d(TAG, "mBluetoothGatt.readCharacteristic(characteristic);");
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        boolean b = mBluetoothGatt.readCharacteristic(characteristic);
        LogUtil.d(TAG, "读取状态：" + b);
    }

    //    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
//        mBluetoothGatt.requestMtu(200);
        LogUtil.d(TAG, "  writeCharacteristic ");
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        boolean b = mBluetoothGatt.writeCharacteristic(characteristic);
        LogUtil.d(TAG, "写数据：" + b);
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize(Context context) {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager =
                    (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                LogUtil.d(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            LogUtil.d(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        getBTState();
        //initCallback();
        LogUtil.d(TAG, "initialize() ");
        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public synchronized boolean connect(final String address) {
        if (STATE_CONNECTING == mConnectionState) {
            LogUtil.d(TAG, "connect---STATE_CONNECTING--return-");
            return true;
        }
        searchDeviceList.clear();
//        BLETool.getInstance().scanLeDevice(false);
        initialize(YamanApplication.getInstance().getApplicationContext());
        LogUtil.d(TAG, "Connect: " + address);
//        SecureAccess.getInstance().startConnectTimer();
        if (mBluetoothAdapter == null || address == null) {
            LogUtil.d(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        bleAddress = address;
        mConnectionState = STATE_CONNECTING;
        try {
            curDevice = mBluetoothAdapter.getRemoteDevice(address);
        } catch (Exception e) {
            LogUtil.d(TAG, "address error .  Unable to connect.");
            return false;
        }
        // Previously connected device.  Try to reconnect.
//        if (mBluetoothDeviceAddress != null
//                && mBluetoothGatt != null) {
//            if (address.equals(mBluetoothDeviceAddress)) {
//                LogUtil.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
//                if (mBluetoothGatt.connect()) {
//                    mConnectionState = STATE_CONNECTING;
//                    if (null != connectCallback) {
//                        connectCallback.onConnecting();
//                    }
//                    return true;
//                } else {
//                    return false;
//                }
//            } else {
//                mBluetoothGatt.disconnect();
//                mBluetoothGatt.close();
//            }
//        }
        if (null == curDevice) {
            LogUtil.d(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        handler.post(new Runnable() {
            @Override
            public void run() {
                mBluetoothGatt = curDevice.connectGatt(YamanApplication.getInstance().getApplicationContext(), false, mGattCallback);
            }
        });
        curStartConTime = System.currentTimeMillis();
        LogUtil.d(TAG, "Trying to create a new connection.");
        if (null != connectCallback) {
            connectCallback.onConnecting();
        }
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        isMTU = false;
        recon = false;
        BleConfigure.supportedMTU = 20;
        curConMax = 4;
        curStartConTime = 0;
        LogUtil.d(TAG, "disconnect()----start-->");
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "disconnect()----BluetoothAdapter not initialized");
            return;
        }
//        if (mConnectionState == STATE_CONNECTED) {
//        mBluetoothGatt.disconnect();
//        mBluetoothGatt.close();
//        mConnectionState = STATE_DISCONNECTED;
        close();
        if (null != connectCallback) {
            connectCallback.onDisConnected();
            handler.removeMessages(1);
            handler.sendEmptyMessageDelayed(1, 2500);
        }
        mConnectionState = STATE_DISCONNECTED;
        LogUtil.d(TAG, "disconnect()----mBluetoothGatt.disconnect()");
//        }
    }

    /**
     * 未连接上，而断开资源
     *
     * @param gatt
     */
    public void disConnectDevice(BluetoothGatt gatt, int type) {
        isMTU = false;
        BleConfigure.supportedMTU = 20;
        LogUtil.d(TAG, "---disConnectDevice(BluetoothGatt gatt)-->disconnect()--->mConnectionState= " + mConnectionState);
//        if (gatt == null) {
//            return;
//        }
//        gatt.disconnect();
//        gatt.close();
        close();
        if (null != gatt) {
            gatt.disconnect();
            gatt.close();
            gatt = null;
            LogUtil.d(TAG, "-----disConnectDevice(BluetoothGatt gatt)----111---gatt.close();");
        }
        if (type > 0) {
            if (null != connectCallback && STATE_DISCONNECTED != mConnectionState) {
                connectCallback.onDisConnected();
            }
            if (null != handler) {
                handler.removeMessages(1);
                handler.sendEmptyMessageDelayed(1, 3000);
            }
            mConnectionState = STATE_DISCONNECTED;
            LogUtil.d(TAG, "-----disConnectDevice(BluetoothGatt gatt)----111----mBluetoothGatt.disconnect()");
        }
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        isMTU = false;
        LogUtil.d(TAG, "   mBluetoothGatt.close()---->mConnectionState=" + mConnectionState);

//        handler.post(new Runnable() {
//            @Override
//            public void run() {
        if (null != mBluetoothGatt) {
            if (mBluetoothGatt.connect()) {
                mBluetoothGatt.disconnect();
            }
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
//            }
//        });
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param btService BluetoothGattService to act on.
     * @param enabled   If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattService btService,
                                              boolean enabled) {
        LogUtil.d(TAG, "setCharacteristicNotification:" + enabled);
        if (null == mBluetoothAdapter || null == mBluetoothGatt || null == btService) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        BluetoothGattCharacteristic characteristic = btService.getCharacteristic(UUID_READ);
        if (characteristic != null) {

            mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
            if (UUID_READ.equals(characteristic.getUuid())) {
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                        UUID.fromString(BleConfigure.UUID_READ));
                for (BluetoothGattDescriptor dp : characteristic.getDescriptors()) {
                    dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(dp);
                }
            }
        }
    }

    public void setCharacteristicNotification(BluetoothGatt gatt,
                                              BluetoothGattCharacteristic characteristic, boolean enabled) {
        gatt.setCharacteristicNotification(characteristic, enabled);

        LogUtil.d(TAG, "setCharacteristicNotification:" + enabled);
        if (null == mBluetoothAdapter || null == mBluetoothGatt || null == btService) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(BluetoothUtil.CLIENT_CHARACTERISTIC_CONFIG));
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }
    }

    protected void setMutiNotify(BluetoothGatt gatt, UUID uuid, int channelNumber) {
        if (uuid.toString().equals(BleConfigure.UUID_SERVICE) && null != btService) {

            BluetoothGattCharacteristic characteristic = btService.getCharacteristic(UUID_READ);
            if (characteristic != null) {
                setCharacteristicNotification(gatt, characteristic, true);
                LogUtil.d(TAG, "-----setMutiNotify---characteristic ");
            }
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public BluetoothGattService getSupportedGattServices() {
        LogUtil.d(TAG, "  getSupportedGattServices()");
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getService(UUID.fromString(UUID_SERVICE.toString()));
    }

    public BluetoothDevice getCurDevice() {
        return curDevice;
    }

    @Override
    public synchronized boolean writeLlsAlertLevel(String type, byte[] bb) {
//        LogUtil.e(TAG, "type--------------->" + type);
//        synchronized (obj) {
        //BluetoothGattService linkLossService = mBluetoothGatt.getService(UUID.fromString(BluetoothUtil.getDeviceServiceUUID(currDeviceType)));
//        if (!mBleDevice.isConnected()) {
//            LogUtil.e(TAG, "device not connected, return from writeLlsAlertLevel.");
//            return;
//        }
        if (btService == null) {
            //TODO 需重新扫描连接
            LogUtil.d(TAG, "---writeLlsAlertLevel78-->disconnect()---> ");
            disconnect();
            LogUtil.e(TAG, "link loss Alert service not found!");
            return false;
        }
        BluetoothGattCharacteristic alertLevel = btService.getCharacteristic(UUID_WRITE);

        if (alertLevel == null) {
            LogUtil.e(TAG, "link loss Alert Level charateristic not found!");
            return false;
        }

        if (mBluetoothGatt == null) {
            LogUtil.e(TAG, "mBluetoothGatt is null");
            return false;
        }

//        boolean status = false;
        isWriteOK = false;
        int storedLevel = alertLevel.getWriteType();
        LogUtil.d(TAG, "storedLevel() - storedLevel=" + storedLevel);
        alertLevel.setValue(bb);
        alertLevel.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        isWriteOK = mBluetoothGatt.writeCharacteristic(alertLevel);
        LogUtil.d(TAG, "----writeLlsAlertLevel-- - status=" + isWriteOK + ", bb = " + StringUtil.byteToHexStr(bb));
        if (!isWriteOK) {
            LogUtil.e(TAG, "----writeLlsAlertLevel-- error- status=" + isWriteOK + ", bb = " + StringUtil.byteToHexStr(bb));
        }
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            status = mBluetoothGatt.writeCharacteristic(alertLevel);
//            LogUtil.d(TAG, "-------writeLlsAlertLevel---- again - status=" + status);
//        }
        return isWriteOK;
//        }
    }


    @Override
    public void onDiscovered(BluetoothDevice device) {
        LogUtil.d(TAG, "-------onDiscovered---- device -name =" + device.getName());
        LogUtil.d(TAG, "-------onDiscovered---- device -mac =" + device.getAddress());
        LogUtil.d(TAG, "-------onDiscovered---- isDeviceBound - =" + BLETool.getInstance().isDeviceBound());
        if (STATE_DISCONNECTED == mConnectionState) {
            if (BLETool.getInstance().isDeviceBound()) {
                String mac = BLETool.getInstance().getBoundDeviceMac();
                if (!TextUtils.isEmpty(mac) && device.getAddress().equalsIgnoreCase(mac)) {
//                    BLETool.getInstance().scanLeDevice(false);
//                    try {
//                        Thread.sleep(200);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    connect(mac);
                }
            } else {
                scanResult(device);
            }
//            connect(device.getAddress());
        }
    }

    private synchronized void scanResult(BluetoothDevice device) {
        boolean flag = false;
        if (searchDeviceList.size() < 1) {
            searchDeviceList.add(device);
        } else {
            for (BluetoothDevice d : searchDeviceList) {
                if (d.getAddress().equals(device.getAddress())) {
                    flag = true;
                }
            }
        }
        if (!flag && null != scanResultCallback) {
            scanResultCallback.onDiscovered(searchDeviceList.get(searchDeviceList.size() - 1));
        }
    }

    @Override
    public void onScanStart() {
        searchDeviceList.clear();
        getBTState();
        LogUtil.d(TAG, "onScanStart--1--->phoneBtState=" + phoneBtState);
        if (null != scanResultCallback) {
            LogUtil.d(TAG, "onScanStart---2-->");
            scanResultCallback.onScanStart();
        }
    }

    @Override
    public void onScanFinish() {
        LogUtil.d(TAG, "onScanFinish---mConnectionState-->" + mConnectionState);
        if (null != scanResultCallback) {
            scanResultCallback.onScanFinish();
        }

        if (null != connectCallback && mConnectionState == STATE_DISCONNECTED) {
//            connectCallback.onDisConnected();
            handler.sendEmptyMessageDelayed(1, 3000);
        }
    }

    public boolean getBTState() {
        BluetoothManager bm = (BluetoothManager) YamanApplication.getInstance().getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bm.getAdapter();
        if (null != mBluetoothAdapter && mBluetoothAdapter.isEnabled()) {
            phoneBtState = true;
        } else {
            phoneBtState = false;
        }
        return phoneBtState;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    handler.removeMessages(1);
                    LogUtil.d(TAG, "onScanFinish---scanLeDevice--phoneBtState>" + phoneBtState);
                    getBTState();
                    if (null != connectCallback && mConnectionState != STATE_CONNECTED && BLETool.getInstance().isDeviceBound()) {
                        if (phoneBtState) {
                            BLETool.getInstance().scanLeDevice(true);
                        }
                        //connectCallback.onConnecting();
                    }
                    break;
                case 2:
                    recon = false;
                    curConMax -= 1;
                    handler.removeMessages(2);
                    LogUtil.d(TAG, "-------removeMessages(2)---->");
                    connect(bleAddress);
                    break;
            }

        }
    };

    @Override
    public void onBleStateOn() {
        LogUtil.d(TAG, "-------onBleStateOn---->");
        phoneBtState = true;
        close();
        mConnectionState = STATE_DISCONNECTED;
        if (BLETool.getInstance().isDeviceBound()) {
            BLETool.getInstance().scanLeDevice(true);
        }
    }

    @Override
    public void onBleStateOff() {
        if (null != handler) {
            handler.removeMessages(1);
            handler.removeMessages(2);
        }
        curConMax = 4;
        phoneBtState = false;
        if (null != connectCallback) {
            connectCallback.onDisConnected();
        }
        BLETool.getInstance().scanLeDevice(false);
        mBluetoothAdapter = null;
        mConnectionState = STATE_DISCONNECTED;
        LogUtil.d(TAG, "-------onBleStateOff---->");
    }

    @Override
    public void onBleTurningOn() {
        LogUtil.d(TAG, "-------onBleTurningOn---->");
    }

    @Override
    public void onBleTurningOff() {
        phoneBtState = false;
        LogUtil.d(TAG, "-------onBleTurningOff---->");
    }

    @Override
    public void onDisConnected(BluetoothDevice device) {
//        LogUtil.d(TAG, "---onReceive----onDisConnected---->");
//        if(STATE_CONNECTED==mConnectionState&&null!=device&&null!=CurDevice&&device.getAddress().equalsIgnoreCase(CurDevice.getAddress())){
//            if (null != connectCallback) {
//                mConnectionState = STATE_DISCONNECTED;
//                connectCallback.onDisConnected();
//            }
//        }
    }

    /**
     * 解绑设备
     */
    public void unbindDevice() {
        LogUtil.d(TAG, "---unbindDevice-->disconnect()---> ");
        disconnect();
    }

    public void release() {
        isAutoConnect = false;
        if (null != handler) {
            handler.removeMessages(1);
            handler.removeMessages(2);
        }
        LogUtil.d(TAG, "---release-->disconnect()---> ");
        disconnect();
//        close();
    }

    /**
     * 获取蓝牙连接状态:true为已连接，false为未连接
     * Created by fWX581433 on 2018/7/6 16:50
     */
    public boolean getConnectionState() {
        boolean state = false;
        if (STATE_CONNECTED == mConnectionState) {
            state = true;
        }
        return state;
    }

    /**
     * 获取蓝牙信号强度
     * Created by fWX581433 on 2018/8/8 15:17
     */
    public boolean getRssiVal() {
        if (mBluetoothGatt == null)
            return false;
        return mBluetoothGatt.readRemoteRssi();
    }
}
