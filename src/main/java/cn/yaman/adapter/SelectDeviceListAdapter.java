package cn.yaman.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.tcl.smart.beauty.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import cn.yaman.callBack.OnBLESelectItemClickListener;

public class SelectDeviceListAdapter extends RecyclerView.Adapter<SelectDeviceListAdapter.DeviceListHolder> {
    private final Context mContext;
    private List<BluetoothDevice> mDeviceList;
    private OnBLESelectItemClickListener clickListener;

    public SelectDeviceListAdapter(Context context) {
        this.mContext = context;
        mDeviceList = new ArrayList<BluetoothDevice>();
    }

    public List getmDeviceList() {
        return mDeviceList;
    }

    public void clearDeviceList() {
        mDeviceList.clear();
    }

    public void setClickListener(OnBLESelectItemClickListener listener) {
        if (null != listener) {
            clickListener = listener;
        }

    }

    public void setDeviceList(List<BluetoothDevice> list) {
        if (list.size() > 0) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                if (!mDeviceList.contains(list.get(i))) {
                    mDeviceList.add(list.get(i));
                }
            }
        }
    }

    public void addDevice(BluetoothDevice device) {

        if (!mDeviceList.contains(device)) {
            mDeviceList.add(device);
        }
    }

    @Override
    public DeviceListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DeviceListHolder(parent, LayoutInflater.from(mContext));
    }

    @Override
    public void onBindViewHolder(DeviceListHolder holder, int position) {
        holder.bind(mDeviceList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }

    class DeviceListHolder extends RecyclerView.ViewHolder {
        private TextView mDeviceName;
        public DeviceListHolder(ViewGroup parent, LayoutInflater inflater) {
            super(inflater.inflate(R.layout.item_device_list, parent, false));
            mDeviceName = (TextView) itemView.findViewById(R.id.device_name);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void bind(final BluetoothDevice device) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null == device) {
                        return;
                    }
                    if (null != clickListener) {
                        clickListener.onBLESelectItemClick(device);
                    }
                }
            });
            String name = device.getName();
            mDeviceName.setText(TextUtils.isEmpty(name) ? device.getAddress() : name);
        }
    }
}