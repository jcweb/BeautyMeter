package cn.yaman.activity;

import android.content.Intent;
import android.view.View;

import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivitySchemeDetailBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.ToastUtils;
import cn.lamb.http.HttpParams;
import cn.yaman.Constants.HttpUrl;
import cn.yaman.adapter.SchemeListAdapter;
import cn.yaman.core.CommonTitlebarStrategy;
import cn.yaman.entity.SchemeDetailEntity;
import cn.yaman.entity.SchemeEntity;
import cn.yaman.http.HttpResponse;
import cn.yaman.http.HttpUtils;
import cn.yaman.http.YamanHttpCallback;
import cn.yaman.utils.JsonUtils;

public class SchemeDetailActivity extends BaseActivity<ActivitySchemeDetailBinding> {
    private int schemeId;
    private SchemeListAdapter adapter;
    private List<SchemeDetailEntity> list;
    private  SchemeEntity entity;
    int deviceId;
    @Override
    public int bindContentView() {
        return R.layout.activity_scheme_detail;
    }

    @Override
    public void onProcessor() {
        entity = (SchemeEntity)getIntent().getSerializableExtra("schemeEntity");
        deviceId=getIntent().getIntExtra("deviceId", 0);
        if(entity!=null){
            String title = entity.getName();
            schemeId = entity.getId();
            initTitlebar(title);
        }
        getBinding().rvSchemeDetail.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new SchemeListAdapter(this,list);
        getBinding().rvSchemeDetail.setAdapter(adapter);
        getSchemeList();
    }

    private void initTitlebar(String title) {
        setStatusbarMode(true);
        CommonTitlebarStrategy titlebar = new CommonTitlebarStrategy();
        setTitlebarStrategy(titlebar);
        titlebar.setTitleName(title);
        titlebar.setElevation(R.dimen.dp_02);
        titlebar.setLeftIcon(v -> finish());
    }

    /*获取方案列表*/
    private void getSchemeList(){
        HttpParams params = new HttpParams();
        params.put("schemeId", schemeId);
        HttpUtils.newRequester().post(HttpUrl.SCHEME_DETAIL_LIST, params, new YamanHttpCallback(this) {
            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    List<SchemeDetailEntity> schemeList = JsonUtils.getParamList(response.getData(), SchemeDetailEntity.class);
                    list.addAll(schemeList);
                    adapter.notifyDataSetChanged();
                    getBinding().tvStartScheme.setVisibility(View.VISIBLE);
                } else {
                    ToastUtils.toastShort(response.getResultMsg());
                }
            }
        });
    }

    public void startNurse(View view){
        Intent intent = new Intent(this,StartNurseActivity.class);
        intent.putExtra("schemelist",(Serializable) list);
        intent.putExtra("schemeEntity",entity);
        intent.putExtra("schemeId", schemeId);
        intent.putExtra("deviceId", deviceId);
        startActivity(intent);
    }
    /*动态加载护理方案列表*/
//    private void  autoAddItem(SchemeDetailEntity entity){
//        View view = LayoutInflater.from(this).inflate(R.layout.scheme_detail_item, null);
//        ImageView iv = view.findViewById(R.id.iv_scheme_detail);
//        TextView tvName = view.findViewById(R.id.tv_scheme_detail_name);
//        TextView tvContent = view.findViewById(R.id.tv_scheme_detail_content);
//        TextView tvTime = view.findViewById(R.id.tv_scheme_detail_time);
//        Glide.with(this).load(entity.getImageUrl()).into(iv);
//        tvName.setText(entity.getName());
//        tvContent.setText(entity.getFunction());
//        tvTime.setText(entity.getDuration());
//        getBinding().llContainerSchenmeDetail.addView(view);
//
//    }
}
