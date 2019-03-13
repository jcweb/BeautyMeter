package cn.yaman.bluetooth.callBack;

public interface BleDeviceStateCallBack {
    void onState(int state,int batter,int runTime);
}
