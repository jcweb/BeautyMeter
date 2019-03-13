package cn.yaman.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivitySchemeBinding;

import java.util.List;

import androidx.annotation.Nullable;
import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.LogUtils;
import cn.lamb.base.util.ToastUtils;
import cn.lamb.http.HttpParams;
import cn.yaman.Constants.HttpUrl;
import cn.yaman.core.CommonTitlebarStrategy;
import cn.yaman.entity.SchemeEntity;
import cn.yaman.http.HttpResponse;
import cn.yaman.http.HttpUtils;
import cn.yaman.http.YamanHttpCallback;
import cn.yaman.utils.JsonUtils;
import cn.yaman.utils.TimeUtils;

public class SchemeActivity extends BaseActivity<ActivitySchemeBinding> {
    int deviceId;

    @Override
    public int bindContentView() {
        return R.layout.activity_scheme;
    }

    @Override
    public void onProcessor() {
        initTitlebar();
        deviceId=getIntent().getIntExtra("deviceId", 0);
        getSchemeList();
    }

    private void initTitlebar() {
        setStatusbarMode(true);
        CommonTitlebarStrategy titlebar = new CommonTitlebarStrategy();
        setTitlebarStrategy(titlebar);
        titlebar.setTitleName(getString(R.string.title_scheme));
        titlebar.setElevation(R.dimen.dp_02);
        titlebar.setLeftIcon(v -> finish());
    }


    /*获取方案列表*/
    private void getSchemeList() {
        HttpParams params = new HttpParams();
        params.put("model", "美容仪XT-1B");
        HttpUtils.newRequester().post(HttpUrl.SCHEME_LIST, params, new YamanHttpCallback(this) {
            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    List<SchemeEntity> list = JsonUtils.getParamList(response.getData(), SchemeEntity.class);
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            autoAddItem(list.get(i));
                        }
                    }
                    LogUtils.d(response.getData());
                } else {
                    ToastUtils.toastShort(response.getResultMsg());
                }
            }
        });
    }

    /*动态加载护理方案列表*/
    private void autoAddItem(SchemeEntity entity) {
        View view = LayoutInflater.from(this).inflate(R.layout.scheme_list_item, null);
        ImageView iv = view.findViewById(R.id.iv_scheme_list);
        TextView tvName = view.findViewById(R.id.tv_scheme_list_name);
        TextView tvContent = view.findViewById(R.id.tv_scheme_list_content);
        TextView tvTime = view.findViewById(R.id.tv_scheme_list_time);
        Glide.with(this).load(entity.getImageUrl()).into(iv);
        tvName.setText(entity.getName());
        tvContent.setText(entity.getExplain());
        if (!TextUtils.isEmpty(entity.getDuration())) {
            int second = Integer.parseInt(entity.getDuration());
            tvTime.setText(TimeUtils.secondToStr(second));
        }
        getBinding().llContainerScheme.addView(view);

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SchemeActivity.this, SchemeDetailActivity.class);
                intent.putExtra("schemeEntity", entity);
                intent.putExtra("deviceId", deviceId);
                startActivity(intent);
            }
        });
    }
}
