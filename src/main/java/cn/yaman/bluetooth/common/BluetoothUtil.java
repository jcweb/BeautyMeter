package cn.yaman.bluetooth.common;

import android.os.Build;


/**
 * ble工具类
 * @author fenghui
 *
 */
public class BluetoothUtil {
	
	static String manufacturer = Build.MANUFACTURER.toUpperCase();
	static String model = Build.MODEL.toUpperCase();
	static int sdk = Build.VERSION.SDK_INT;
	
	
	//初始化监听消息返回结果
	public static final int START_DEVICE_CODE_SUCCESS = 0;
	public static final int START_DEVICE_CODE_UNSUPPORTBLE = -1;
	public static final int START_DEVICE_CODE_UNSUPPORTBT = -2;
	public static final int START_DEVICE_CODE_ERROR = -3;
	
	public static enum ConnectType {
		GATT_AUTO_FALSE,
		GATT_AUTO_TRUE,
		GATT_BYMAC,
		SCAN_BLE_LOLLIPOP,
		SCAN_BLE_LOLLIPOP_GATT_AUTO_TRUE
	};
	
	
	//设备类型
	public final static int DEVICETYPE_TIZHICHENG = 0;

	//
	public final static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
	public final static String CLIENT_CHARACTERISTIC_CONFIG_1 = "00002901-0000-1000-8000-00805f9b34fb";



	
	/**
	 * 获取扫描超时时间
	 * @return
	 */
	public static int getScanTimeout() {
		int timeout = 3000;
		
		
		//android 7.0以上版本，不需频繁扫描
		if(sdk > 23) {
			 timeout = 5000;
		}
		
		return timeout;
	}

	public static int getConnectTimeout() {
		return 20000;
	}
	
	/**
	 * 获取无消息接收超时时间
	 * @return
	 */
	public static int getNoMessageTimeout() {
		return 1500;
	}
	
	/**
	 * 根据手机型号获取连接方式
	 * @return
	 */
	public static ConnectType getConnectType() {
		
		if(sdk >= 21) {
			if(manufacturer.indexOf("HUAWEI") > -1 && model.indexOf("CHE-TL00") > -1) {		//荣耀4x
				//return ConnectType.SCAN_BLE_LOLLIPOP_GATT_AUTO_TRUE;
			}
			return ConnectType.SCAN_BLE_LOLLIPOP;
		}

		if(manufacturer.indexOf("HUAWEI") > -1 && model.indexOf("CHM-TL00H") > -1) {		//荣耀4x
			//return ConnectType.GATT_AUTO_TRUE;
		}
		
		return ConnectType.GATT_AUTO_FALSE;
	}
}