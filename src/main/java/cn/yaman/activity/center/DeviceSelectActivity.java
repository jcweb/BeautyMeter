package cn.yaman.activity.center;

import android.content.Intent;
import android.view.View;

import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivityDeviceSelectBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.ToastUtils;
import cn.lamb.http.HttpParams;
import cn.yaman.Constants.HttpUrl;
import cn.yaman.adapter.SelectDeviceAdapter;
import cn.yaman.adapter.SelectDeviceAdapter.OnDeviceAddListener;
import cn.yaman.core.CommonTitlebarStrategy;
import cn.yaman.entity.SelectDeviceEntity;
import cn.yaman.http.HttpResponse;
import cn.yaman.http.HttpUtils;
import cn.yaman.http.YamanHttpCallback;
import cn.yaman.utils.JsonUtils;
import cn.yaman.utils.PreferenceUtils;

public class DeviceSelectActivity extends BaseActivity<ActivityDeviceSelectBinding> implements OnDeviceAddListener {
    private SelectDeviceAdapter adapter;
    private List<SelectDeviceEntity> list;

    @Override
    public int bindContentView() {
        return R.layout.activity_device_select;
    }

    @Override
    public void onProcessor() {
        setStatusbarMode(true);
        initTitlebar();
        getBinding().rvDeviceSelect.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new SelectDeviceAdapter(this, list);
        adapter.setListener(this);
        getBinding().rvDeviceSelect.setAdapter(adapter);
        getDeviceList();
    }

    private void getDeviceList() {
        HttpParams params = new HttpParams();
        params.put("pageNum", "1");
        params.put("pageSize", "10");
        params.put("userId", PreferenceUtils.getInstance().getUserEntity().getUser().getId());
        HttpUtils.newRequester().post(HttpUrl.DEVICE_BIND_LIST, params, new YamanHttpCallback(this) {
            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    List<SelectDeviceEntity> deviceList = JsonUtils.getParamList(response.getData(), SelectDeviceEntity.class);
                    list.addAll(deviceList);
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtils.toastShort(response.getResultMsg());
                }
            }
        });
    }

    private void initTitlebar() {
        CommonTitlebarStrategy titlebar = new CommonTitlebarStrategy();
        setTitlebarStrategy(titlebar);
        titlebar.setTitleName(getString(R.string.my_equip_title));
        titlebar.setElevation(R.dimen.dp_02);
        titlebar.setLeftIcon(v -> finish());
    }

    /*添加新设备*/
    public void addDevice(View view) {
        startActivity(DeviceListActivity.class);
    }

    @Override
    public void onClick(int position, boolean clickBtn) {
        Intent intent = new Intent();
        intent.putExtra("deviceEntity", list.get(position));
        if (clickBtn) {
            intent.setClass(this, DeviceDetailActivity.class);
            startActivity(intent);
        } else {
            setResult(RESULT_OK, intent);
            finish();
        }

    }
}
