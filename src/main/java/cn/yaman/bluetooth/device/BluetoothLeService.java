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


import cn.yaman.YamanApplication;
import cn.yaman.bluetooth.BLETool;
import cn.yaman.bluetooth.RougeMessageAnalyser;
import cn.yaman.bluetooth.common.BleConfigure;
import cn.yaman.bluetooth.device.callback.BleSatusCallBack;
import cn.yaman.bluetooth.device.callback.BleWriteDataResultCallBack;
import cn.yaman.bluetooth.device.callback.ConnectCallBack;
import cn.yaman.bluetooth.device.callback.IBleServerWriter;
import cn.yaman.bluetooth.device.callback.ScanResultCallBack;
import cn.yaman.bluetooth.device.receiver.BleStatusReceiver;

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
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                recon = false;
                phoneBtState = true;
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                BLETool.getInstance().scanLeDevice(false);
                if (null != connectCallback) {
                    connectCallback.onConnected();
                }

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                curDevice = null;
                int time = (int) (System.currentTimeMillis() - curStartConTime) / 1000;

                if (133 == status && !recon && isAutoConnect && curConMax > 0 && time < 20) {
                    disConnectDevice(gatt, 0);
                    recon = true;
                    mConnectionState = STATE_DISCONNECTED;
                    if (null != handler) {
                        handler.sendEmptyMessageDelayed(2, 500);
                    }
                } else {
                    curConMax = 4;
                    disConnectDevice(gatt, 1);
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                btService = gatt.getService(UUID.fromString(UUID_SERVICE.toString()));
                if (null == btService) {
                    disConnectDevice(gatt, 1);
                    return;
                }
                List<BluetoothGattCharacteristic> characteristics = btService.getCharacteristics();
                for (BluetoothGattCharacteristic characteristic : characteristics) {
                    setCharacteristicNotification(gatt, characteristic, true);
                }
            } else {
                disConnectDevice(gatt, 1);
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                isMTU = true;
                supportedMTU = mtu;//local var to record MTU size
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (rougeMessageAnalyser != null) {
                    rougeMessageAnalyser.onReceiveBleMessage(characteristic.getValue());
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {//写入成功
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
                disConnectDevice(gatt, 1);
            } else if (status == BluetoothGatt.GATT_WRITE_NOT_PERMITTED) {
                disConnectDevice(gatt, 1);
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
            if (status != 0) {
                //状态码出错，先断开连接然后再重新连接
                gatt.disconnect();
            } else {
//                if (null != connectCallback) {
//                    connectCallback.onConnected();
//                }
                //这里通知前端，设备连接成功
                UUID uuid = gattDescriptor.getCharacteristic().getUuid();


                setMutiNotify(gatt, uuid, 1);
                BLETool.getInstance().requestDeviceState();
            }

        }
    };


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
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        }
        getBTState();
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
            return true;
        }
        searchDeviceList.clear();
        initialize(YamanApplication.getInstance().getApplicationContext());
        if (mBluetoothAdapter == null || address == null) {
            return false;
        }
        bleAddress = address;
        mConnectionState = STATE_CONNECTING;
        try {
            curDevice = mBluetoothAdapter.getRemoteDevice(address);
        } catch (Exception e) {
            return false;
        }
        if (null == curDevice) {
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
        curConMax = 4;
        curStartConTime = 0;
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        close();
        if (null != connectCallback) {
            connectCallback.onDisConnected();
            handler.removeMessages(1);
            handler.sendEmptyMessageDelayed(1, 2500);
        }
        mConnectionState = STATE_DISCONNECTED;
    }

    /**
     * 未连接上，而断开资源
     *
     * @param gatt
     */
    public void disConnectDevice(BluetoothGatt gatt, int type) {
        isMTU = false;
        close();
        if (null != gatt) {
            gatt.disconnect();
            gatt.close();
            gatt = null;
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
        }
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        isMTU = false;

        if (null != mBluetoothGatt) {
            if (mBluetoothGatt.connect()) {
                mBluetoothGatt.disconnect();
            }
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param btService BluetoothGattService to act on.
     * @param enabled   If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattService btService,
                                              boolean enabled) {
        if (null == mBluetoothAdapter || null == mBluetoothGatt || null == btService) {
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

        if (null == mBluetoothAdapter || null == mBluetoothGatt || null == btService) {
            return;
        }
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(BleConfigure.CLIENT_CHARACTERISTIC_CONFIG));
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
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getService(UUID.fromString(UUID_SERVICE.toString()));
    }

    public BluetoothDevice getCurDevice() {
        return curDevice;
    }

    @Override
    public synchronized boolean writeLlsAlertLevel(String type, byte[] bb) {
        if (btService == null) {
            //TODO 需重新扫描连接
            disconnect();
            return false;
        }
        BluetoothGattCharacteristic alertLevel = btService.getCharacteristic(UUID_WRITE);

        if (alertLevel == null) {
            return false;
        }

        if (mBluetoothGatt == null) {
            return false;
        }

        isWriteOK = false;
        int storedLevel = alertLevel.getWriteType();
        alertLevel.setValue(bb);
        alertLevel.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        isWriteOK = mBluetoothGatt.writeCharacteristic(alertLevel);
        return isWriteOK;
    }


    @Override
    public void onDiscovered(BluetoothDevice device) {
        if (STATE_DISCONNECTED == mConnectionState) {
            if (BLETool.getInstance().isDeviceBound()) {
                String mac = BLETool.getInstance().getBoundDeviceMac();
                if (!TextUtils.isEmpty(mac) && device.getAddress().equalsIgnoreCase(mac)) {
                    connect(mac);
                }
            } else {
                scanResult(device);
            }
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
        if (null != scanResultCallback) {
            scanResultCallback.onScanStart();
        }
    }

    @Override
    public void onScanFinish() {
        if (null != scanResultCallback) {
            scanResultCallback.onScanFinish();
        }

        if (null != connectCallback && mConnectionState == STATE_DISCONNECTED) {
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
                    getBTState();
                    if (null != connectCallback && mConnectionState != STATE_CONNECTED && BLETool.getInstance().isDeviceBound()) {
                        if (phoneBtState) {
                            BLETool.getInstance().scanLeDevice(true);
                        }
                    }
                    break;
                case 2:
                    recon = false;
                    curConMax -= 1;
                    handler.removeMessages(2);
                    connect(bleAddress);
                    break;
            }

        }
    };

    @Override
    public void onBleStateOn() {
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
    }

    @Override
    public void onBleTurningOn() {
    }

    @Override
    public void onBleTurningOff() {
        phoneBtState = false;
    }

    @Override
    public void onDisConnected(BluetoothDevice device) {
    }

    /**
     * 解绑设备
     */
    public void unbindDevice() {
        disconnect();
    }

    public void release() {
        isAutoConnect = false;
        if (null != handler) {
            handler.removeMessages(1);
            handler.removeMessages(2);
        }
        disconnect();
    }

    /**
     * 获取蓝牙连接状态:true为已连接，false为未连接
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
     */
    public boolean getRssiVal() {
        if (mBluetoothGatt == null)
            return false;
        return mBluetoothGatt.readRemoteRssi();
    }
}
