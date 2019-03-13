package cn.yaman.activity.center;

import android.content.Intent;
import android.view.View;

import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivityDeviceListBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.ToastUtils;
import cn.lamb.http.HttpParams;
import cn.yaman.Constants.HttpUrl;
import cn.yaman.adapter.DeviceListAdapter;
import cn.yaman.adapter.DeviceListAdapter.OnDeviceAddListener;
import cn.yaman.core.CommonTitlebarStrategy;
import cn.yaman.entity.DeviceEntity;
import cn.yaman.http.HttpResponse;
import cn.yaman.http.HttpUtils;
import cn.yaman.http.YamanHttpCallback;
import cn.yaman.utils.JsonUtils;

public class DeviceListActivity extends BaseActivity<ActivityDeviceListBinding> implements OnDeviceAddListener {
    private DeviceListAdapter adapter;
    private List<DeviceEntity> list;
    @Override
    public int bindContentView() {
        return R.layout.activity_device_list;
    }

    @Override
    public void onProcessor() {
        getBinding().setUi(this);
        setStatusbarMode(true);
        initTitlebar();
        list = new ArrayList<>();
        adapter = new DeviceListAdapter(this,list);
        getBinding().rvDeviceList.setLayoutManager(new LinearLayoutManager(this));
        getBinding().rvDeviceList.setAdapter(adapter);
        adapter.setListener(this);
        getDeviceList();
    }

    private void initTitlebar() {
        CommonTitlebarStrategy titlebar = new CommonTitlebarStrategy();
        setTitlebarStrategy(titlebar);
        titlebar.setTitleName(getString(R.string.device_list_title));
        titlebar.setElevation(R.dimen.dp_02);
        titlebar.setLeftIcon(v -> finish());
    }

    private void getDeviceList(){
        HttpParams params = new HttpParams();
        params.put("pageNum", "1");
        params.put("pageSize", "10");
        HttpUtils.newRequester().post(HttpUrl.DEVICE_LIST, params, new YamanHttpCallback(this) {
            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    List<DeviceEntity> deviceList  = JsonUtils.getParamList(response.getData(),DeviceEntity.class);
                    list.addAll(deviceList);
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtils.toastShort(response.getResultMsg());
                }
            }
        });
    }

    @Override
    public void onClick(int position) {
        Intent intent = new Intent(this,DeviceAddActivity.class);
        intent.putExtra("deviceEntity",list.get(position));
        startActivity(intent);
    }

    public void productDetail(View view){
        startActivity(DeviceDetailActivity.class);
    }
}
