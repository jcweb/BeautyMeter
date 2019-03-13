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
import cn.yaman.entity.SelectDeviceEntity;

/**
 * @author timpkins
 */
public class SelectDeviceAdapter extends BaseRecyclerAdapter<SelectDeviceEntity, ViewHolder> {
    public SelectDeviceAdapter(Context context, List<SelectDeviceEntity> list) {
        super(context, list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        return new ViewHolder(layoutInflater.inflate(R.layout.device_select_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        SelectDeviceEntity entity = list.get(position);
        ViewHolder itemHolder = (ViewHolder) viewHolder;
        super.onBindViewHolder(viewHolder, position);
        Glide.with(mContext).load(list.get(position).getSimageUrl()).into(itemHolder.iv);
        itemHolder.tvName.setText(entity.getName());
        itemHolder.tvType.setText(entity.getModel());
        itemHolder.parent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position, false);
                }
            }
        });
        itemHolder.tvManage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position, true);
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv;
        private TextView tvName;
        private TextView tvType;
        private TextView tvManage;
        private View parent;

        public ViewHolder(@NonNull View view) {
            super(view);
            parent = view.findViewById(R.id.ll_device);
            iv = view.findViewById(R.id.iv_device_select_img);
            tvName = view.findViewById(R.id.tv_device_select_name);
            tvType = view.findViewById(R.id.tv_device_type);
            tvManage = view.findViewById(R.id.tv_device_manage);
        }
    }

    private OnDeviceAddListener listener;

    public void setListener(OnDeviceAddListener listener) {
        this.listener = listener;
    }

    public interface OnDeviceAddListener {
        void onClick(int position, boolean clickBtn);
    }
}
