package cn.yaman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tcl.smart.beauty.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import cn.yaman.entity.DeviceEntity;

/**
 * @author timpkins
 */
public class DeviceListAdapter extends BaseRecyclerAdapter<DeviceEntity, ViewHolder> {
    public DeviceListAdapter(Context context, List<DeviceEntity> list) {
        super(context, list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        return new ViewHolder(layoutInflater.inflate(R.layout.device_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        DeviceEntity entity = list.get(position);
        ViewHolder itemHolder = (ViewHolder)viewHolder;
        super.onBindViewHolder(viewHolder, position);
        Glide.with(mContext).load(list.get(position).getSimageUrl()).into(itemHolder.iv);
        itemHolder.tvName.setText(entity.getName());

        itemHolder.tvAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onClick(position);
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView iv;
        private TextView tvName;
        private TextView tvAdd;
        public ViewHolder(@NonNull View view) {
            super(view);
            iv = view.findViewById(R.id.iv_device_img);
            tvName = view.findViewById(R.id.tv_device_name);
            tvAdd = view.findViewById(R.id.tv_device_add);
        }
    }

    private OnDeviceAddListener listener;

    public void setListener(OnDeviceAddListener listener) {
        this.listener = listener;
    }

    public interface OnDeviceAddListener{
        void onClick(int position);
    }
}
