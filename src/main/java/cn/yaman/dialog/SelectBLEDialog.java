package cn.yaman.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcl.smart.beauty.R;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.yaman.adapter.SelectDeviceListAdapter;
import cn.yaman.callBack.OnBLESelectItemClickListener;

/**
 * 搜索蓝牙设备列表弹出窗口
 */
public class SelectBLEDialog {
    private static final String TAG = SelectBLEDialog.class.getSimpleName();
    private static SelectBLEDialog instance;
    private Activity mActivity;
    private SelectDeviceListAdapter mDeviceListAdapter;
    private OnBLESelectItemClickListener clickListener;
    private static Dialog mDialog;
    private RecyclerView rvDeviceList;

    private SelectBLEDialog() {
    }

    public synchronized static SelectBLEDialog getInstance() {
        if (instance == null) {
            instance = new SelectBLEDialog();
        }
        return instance;
    }

    public synchronized void showDialogSelectBLE(Activity activity, List<BluetoothDevice> list, OnBLESelectItemClickListener listener) {
        if (null == mDialog) {
            initDialog(activity, list, listener);
        } else {
            if (list.size() >= 1) {
                mDeviceListAdapter.addDevice(list.get(list.size() - 1));
            }
        }
        mDeviceListAdapter.notifyDataSetChanged();

        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void initDialog(Activity activity, List<BluetoothDevice> mDeviceList, OnBLESelectItemClickListener listener) {
        this.mActivity = activity;
        clickListener = listener;
        if (null == mDialog) {
            mDialog = new Dialog(activity, 0);
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View contentView = inflater.inflate(R.layout.dialog_window_ble_list, null, false);
            initView(contentView);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(contentView);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (null != clickListener) {
                        clickListener.onBLESelectItemNoScanClick();
                    }
                }
            });
            mDeviceListAdapter.setDeviceList(mDeviceList);
        }
    }

    /**
     * 初始化VIEW
     * Created by fWX581433 on 2018/6/29 16:42
     */
    private void initView(View view) {
        if (null == view) {
            return;
        }
        rvDeviceList = (RecyclerView) view.findViewById(R.id.rv_device_list);
        rvDeviceList.setLayoutManager(new LinearLayoutManager(mActivity));
        mDeviceListAdapter = new SelectDeviceListAdapter(mActivity);
        mDeviceListAdapter.setClickListener(itemClickListener);
        rvDeviceList.setAdapter(mDeviceListAdapter);
    }


    private OnBLESelectItemClickListener itemClickListener = new OnBLESelectItemClickListener() {
        @Override
        public void onBLESelectItemClick(BluetoothDevice device) {
            if (null != clickListener) {
                clickListener.onBLESelectItemClick(device);
                dialogDismiss();
            }
        }

        @Override
        public void onBLESelectItemScanClick() {
        }

        @Override
        public void onBLESelectItemNoScanClick() {
        }
    };

    public void dialogDismiss() {
        if (null != mDialog) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void release() {
        dialogDismiss();
        mActivity = null;
    }
}
