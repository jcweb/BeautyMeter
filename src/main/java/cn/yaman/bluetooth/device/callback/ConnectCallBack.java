package cn.yaman.bluetooth.device.callback;

/**
 * 连接设备回调
 */
public interface ConnectCallBack {

    public void onConnected();
    public void onConnecting();
    public void onScaleWake();

    public void onScaleSleep();

    public void onDisConnected();

}
