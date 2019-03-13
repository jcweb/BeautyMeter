package cn.yaman.activity.center;

import android.content.Intent;
import android.view.View;

import com.bumptech.glide.Glide;
import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivityDeviceDetailBinding;

import androidx.annotation.Nullable;
import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.ToastUtils;
import cn.lamb.http.HttpParams;
import cn.yaman.Constants.HttpUrl;
import cn.yaman.entity.SelectDeviceEntity;
import cn.yaman.entity.VersionEntity;
import cn.yaman.http.HttpResponse;
import cn.yaman.http.HttpUtils;
import cn.yaman.http.YamanHttpCallback;
import cn.yaman.utils.AppUtils;
import cn.yaman.utils.JsonUtils;
import cn.yaman.utils.PreferenceUtils;

public class DeviceDetailActivity extends BaseActivity<ActivityDeviceDetailBinding> {
    private VersionEntity versionEntity;
    private SelectDeviceEntity entity;

    @Override
    public int bindContentView() {
        return R.layout.activity_device_detail;
    }

    @Override
    public void onProcessor() {
        getBinding().setUi(this);
        entity = (SelectDeviceEntity) getIntent().getSerializableExtra("deviceEntity");
        if (entity != null) {
            initMainUI();
        }
        getVersion();
    }

    private void initMainUI() {
        getBinding().tvDeviceDetailMac.setText(entity.getMacAdress());
        getBinding().tvDeviceDetailRemark.setText(entity.getName());
        getBinding().tvDeviceDetailModel.setText(entity.getModel());
        Glide.with(this).load(entity.getSimageUrl()).into(getBinding().ivDeviceLogo);
    }


    public void version(View view) {
        Intent intent = new Intent(DeviceDetailActivity.this, FirmWareActivity.class);
        intent.putExtra("version", versionEntity);
        startActivity(intent);
    }

    public void unBinding(View view) {
        HttpParams params = new HttpParams();
        params.put("deviceId", entity.getId());
        params.put("userId", PreferenceUtils.getInstance().getUserEntity().getUser().getId());
        HttpUtils.newRequester().post(HttpUrl.DEVICE_UNBIND, params, new YamanHttpCallback(this) {
            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    ToastUtils.toastShort(response.getResultMsg());
                }
            }
        });
    }

    /*获取版本信息*/
    private void getVersion() {
        HttpParams params = new HttpParams();
        params.put("versionCode", AppUtils.getVersionCode(this));
        HttpUtils.newRequester().post(HttpUrl.APP_UPDATE, params, new YamanHttpCallback(this) {
            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    versionEntity = JsonUtils.getParam(response.getData(), VersionEntity.class);
                    getBinding().tvFirmwareVersion.setVisibility(View.VISIBLE);
                    getBinding().tvFirmwareVersion.setText(versionEntity.getVersionName());
                } else {
                    ToastUtils.toastShort(response.getResultMsg());
                }
            }
        });
    }
}
