package cn.yaman.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tcl.smart.beauty.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import cn.yaman.entity.SchemeDetailEntity;

/**
 * @author timpkins
 */
public class SchemeListAdapter extends BaseRecyclerAdapter<SchemeDetailEntity, ViewHolder> {
    public SchemeListAdapter(Context context, List<SchemeDetailEntity> list) {
        super(context, list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        return new ViewHolder(layoutInflater.inflate(R.layout.scheme_detail_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        SchemeDetailEntity entity = list.get(position);
        ViewHolder itemHolder = (ViewHolder) viewHolder;
        super.onBindViewHolder(viewHolder, position);
        Glide.with(mContext).load(list.get(position).getImageUrl()).into(itemHolder.iv);
        itemHolder.tvName.setText(entity.getName());
        itemHolder.tvContent.setText(entity.getFunction());
        if (!TextUtils.isEmpty(entity.getDuration())) {
            int second = Integer.parseInt(entity.getDuration());

            itemHolder.tvTime.setText(second / 60 + mContext.getString(R.string.minute_once));
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv;
        private TextView tvName, tvContent, tvTime;

        public ViewHolder(@NonNull View view) {
            super(view);
            iv = view.findViewById(R.id.iv_scheme_detail);
            tvName = view.findViewById(R.id.tv_scheme_detail_name);
            tvContent = view.findViewById(R.id.tv_scheme_detail_content);
            tvTime = view.findViewById(R.id.tv_scheme_detail_time);

        }
    }
}
