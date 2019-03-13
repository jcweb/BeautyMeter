package cn.yaman.activity.center;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivityDeviceAddBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.ToastUtils;
import cn.lamb.http.HttpParams;
import cn.yaman.Constants;
import cn.yaman.bluetooth.BLETool;
import cn.yaman.bluetooth.device.callback.ConnectCallBack;
import cn.yaman.bluetooth.device.callback.ScanResultCallBack;
import cn.yaman.callBack.OnBLESelectItemClickListener;
import cn.yaman.core.CommonTitlebarStrategy;
import cn.yaman.dialog.HintDialog;
import cn.yaman.dialog.SelectBLEDialog;
import cn.yaman.entity.DeviceEntity;
import cn.yaman.http.HttpResponse;
import cn.yaman.http.HttpUtils;
import cn.yaman.http.YamanHttpCallback;
import cn.yaman.utils.PreferenceUtils;

public class DeviceAddActivity extends BaseActivity<ActivityDeviceAddBinding> {
    //    private DeviceEntity entity;
    private final static String TAG = DeviceAddActivity.class.getSimpleName();
    private List<BluetoothDevice> list;
    private DeviceEntity entity;
    private String BtAddr, BtName;

    @Override
    public int bindContentView() {
        return R.layout.activity_device_add;
    }

    @Override
    public void onProcessor() {
        setStatusbarMode(true);
        initTitlebar();
        getBinding().setUi(this);
        entity = (DeviceEntity) getIntent().getSerializableExtra("deviceEntity");
        initMainUI(entity);
        list = new ArrayList<>();
        BLETool.getInstance().registerConnectCallBack(TAG, connectCallBack);
    }

    private void initMainUI(DeviceEntity entity) {
        if (entity != null) {
            Glide.with(this).load(entity.getDimageUrl()).into(getBinding().ivDeviceImg);
        }
    }

    private void initTitlebar() {
        CommonTitlebarStrategy titlebar = new CommonTitlebarStrategy();
        setTitlebarStrategy(titlebar);
        titlebar.setLeftIcon(R.mipmap.ic_close, v -> finish());
    }

    public void searchDevice(View view) {
        if (!BLETool.getInstance().getPhoneBTState()) {
            //蓝牙未打开
            new HintDialog(DeviceAddActivity.this, R.mipmap.ic_nurse_start,
                    "请确认蓝牙已经打开", "取消", "去设置", new HintDialog.OnCommitListener() {
                @Override
                public void onClick() {
                    Intent settintIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                    DeviceAddActivity.this.startActivity(settintIntent);
                }
            });
        } else {
            startSearch();
        }
    }

    private void startSearch() {
        getBinding().tvDeviceAddTitle.setText("正在搜索设备");
        getBinding().tvDeviceBuy.setVisibility(View.GONE);
        getBinding().tvDeviceMatch.setVisibility(View.GONE);
        showLoading();
        BLETool.getInstance().registerScanResultCallBack(TAG, scanResultCallBack);
        BLETool.getInstance().scanLeDevice(true);
        list.clear();
    }

    private void showUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DeviceAddActivity.this, "搜索不到设备，请重试！", Toast.LENGTH_SHORT).show();
                hideloading();
                getBinding().tvDeviceAddTitle.setText(R.string.text_device_close);
                getBinding().tvDeviceBuy.setVisibility(View.VISIBLE);
                getBinding().tvDeviceMatch.setVisibility(View.VISIBLE);
            }
        });
    }

    private void connectDevice() {
        HttpParams params = new HttpParams();
        params.put("userId", PreferenceUtils.getInstance().getUserEntity().getUser().getId());
        params.put("macAdress", BtAddr);
        params.put("serialnum", BtAddr.replaceAll("\\:", ""));
        params.put("name", BtName);
        params.put("model", entity.getModel());
        params.put("type", entity.getType());
        params.put("color", "红色");
        HttpUtils.newRequester().post(Constants.HttpUrl.DEVICE_ADD, params, new YamanHttpCallback(this) {
            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getBinding().tvDeviceAddTitle.setText("绑定成功");
                            getBinding().tvDeviceBuy.setVisibility(View.GONE);
                            getBinding().tvDeviceMatch.setVisibility(View.GONE);
                            getBinding().ivDeviceCompleteImg.setVisibility(View.VISIBLE);
                            getBinding().tvDeviceComplete.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    ToastUtils.toastShort(response.getResultMsg());
                }
            }
        });


    }

    public void bindDevice(View view) {
        finish();
    }

    private void hideloading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissLoading();
            }
        });
    }

    private ScanResultCallBack scanResultCallBack = new ScanResultCallBack() {
        @Override
        public void onDiscovered(BluetoothDevice device) {
            if (list.size() < 1) {
                hideloading();
            }
            list.add(device);
            SelectBLEDialog.getInstance().showDialogSelectBLE(DeviceAddActivity.this, list, onBLESelectItemClickListener);
        }

        @Override
        public void onScanStart() {

        }

        @Override
        public void onScanFinish() {
            if (list.size() < 1) {
                showUI();
            }
        }
    };

    private ConnectCallBack connectCallBack = new ConnectCallBack() {
        @Override
        public void onConnected() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectDevice();
//                    Toast.makeText(DeviceAddActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onConnecting() {

        }

        @Override
        public void onScaleWake() {

        }

        @Override
        public void onScaleSleep() {

        }

        @Override
        public void onDisConnected() {

        }
    };

    private OnBLESelectItemClickListener onBLESelectItemClickListener = new OnBLESelectItemClickListener() {
        @Override
        public void onBLESelectItemClick(BluetoothDevice device) {
            BtName = device.getName();
            BtAddr = device.getAddress();
            BLETool.getInstance().connectBluetooth(BtName, BtAddr);
        }

        @Override
        public void onBLESelectItemScanClick() {

        }

        @Override
        public void onBLESelectItemNoScanClick() {

        }
    };
}
