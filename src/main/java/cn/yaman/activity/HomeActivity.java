package cn.yaman.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivityHomeBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener;
import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.ToastUtils;
import cn.lamb.http.HttpParams;
import cn.yaman.Constants.HttpUrl;
import cn.yaman.YamanApplication;
import cn.yaman.activity.center.AboutActivity;
import cn.yaman.activity.center.DeviceListActivity;
import cn.yaman.activity.center.DeviceSelectActivity;
import cn.yaman.activity.center.FeedbackActivity;
import cn.yaman.activity.center.HelpActivity;
import cn.yaman.activity.center.UserCenterActivity;
import cn.yaman.entity.RecordEntity;
import cn.yaman.entity.SelectDeviceEntity;
import cn.yaman.entity.UserEntity;
import cn.yaman.http.HttpResponse;
import cn.yaman.http.HttpUtils;
import cn.yaman.http.YamanHttpCallback;
import cn.yaman.utils.JsonUtils;
import cn.yaman.utils.PreferenceUtils;
import cn.yaman.utils.TimeUtils;

public class HomeActivity extends BaseActivity<ActivityHomeBinding> {
    private static final int TYPE_USER_CENTER = 100;
    private static final int TYPE_DEVICE_SELECT = 101;
    private static final int RECENTLY_THREE = 3;
    private boolean isOpen = false;
    private static final String DATE_FORMAT = "MM/dd hh:mm";
    private int deviceId = 0;
    //记录用户首次点击返回键的时间
    private long firstTime = 0;
    private SelectDeviceEntity deviceEntity;
    @Override
    public int bindContentView() {
        return R.layout.activity_home;
    }

    @Override
    public void onProcessor() {
        getBinding().setUi(this);
        getBinding().setHasrecord(false);
        initUserInfo();
        getRecentlyDevice();
    }

    private void initUserInfo() {
        UserEntity userEntity = PreferenceUtils.getInstance().getUserEntity();

        getBinding().tvCenterName.setText(userEntity.getUser().getName());
        getBinding().leftList.leftHead.tvUserNick.setText(userEntity.getUser().getName());
        ImageView icon = getBinding().leftList.leftHead.civUserImage;
        if (!TextUtils.isEmpty(userEntity.getUser().getIconUrl())) {
            Glide.with(this).load(userEntity.getUser().getIconUrl()).into(icon);
            Glide.with(this).load(userEntity.getUser().getIconUrl()).into(getBinding().ivHomeIcon);
        }
        getBinding().dlHome.setDrawerListener(listener);
    }

    /*绑定设备(用户无绑定设备时)*/
    public void bindDevice(View view) {
        startActivity(DeviceListActivity.class);
    }

    /*获取最近一次测量的设备信息*/
    private void getRecentlyDevice() {
        HttpParams params = new HttpParams();
        params.put("userId", PreferenceUtils.getInstance().getUserEntity().getUser().getId());
        HttpUtils.newRequester().post(HttpUrl.DEVICE_RECENTLY, params, new YamanHttpCallback(this) {
            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    deviceEntity = JsonUtils.getParam(response.getData(), SelectDeviceEntity.class);
                    if (deviceEntity != null && !TextUtils.isEmpty(deviceEntity.getSimageUrl())) {
                        getBinding().setHasrecord(true);
                        Glide.with(HomeActivity.this).load(deviceEntity.getSimageUrl()).into(getBinding().layoutHasDevice.ivDeviceSign);
                        getRecentlyRecord(deviceEntity.getId());
                    }
                } else {
                    ToastUtils.toastShort(response.getResultMsg());
                }
            }
        });
    }

    /*获取最近的3条护肤记录*/
    private void getRecentlyRecord(int deviceId) {
        HttpParams params = new HttpParams();
        this.deviceId = deviceId;
        params.put("deviceId", deviceId);
        params.put("userId", PreferenceUtils.getInstance().getUserEntity().getUser().getId());
        params.put("number", RECENTLY_THREE);
//        params.put("happenTimeLong", System.currentTimeMillis());
        HttpUtils.newRequester().post(HttpUrl.RECORD_LIST, params, new YamanHttpCallback(this) {
            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    List<RecordEntity> list = JsonUtils.getParamList(response.getData(), RecordEntity.class);
                    if (list != null && list.size() > 0) {
                        getBinding().llContainerHome.setVisibility(View.VISIBLE);
                        for (int i = 0; i < list.size(); i++) {
                            autoRecentlyRecord(list.get(i));
                        }
                    }
                    ToastUtils.toastShort(response.getResultMsg());
                }
            }
        });
    }

    /*加载最近的三条护肤记录*/
    private void autoRecentlyRecord(RecordEntity entity) {
        /*动态加载护肤记录*/
        View view = LayoutInflater.from(this).inflate(R.layout.record_list_item, null);

        View line = LayoutInflater.from(this).inflate(R.layout.include_horiz_divide_green, null);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
        line.setLayoutParams(params);
        getBinding().llContainerHome.addView(line);

        ImageView iv = view.findViewById(R.id.iv_record_item);
        TextView tvName = view.findViewById(R.id.tv_record_item_name);
        TextView tvDate = view.findViewById(R.id.tv_record_item_date);
        TextView tvTime = view.findViewById(R.id.tv_record_item_time);
        Glide.with(this).load(entity.getImageUrl()).into(iv);
        tvName.setText(entity.getSname());
        tvDate.setText(TimeUtils.dateToStr(DATE_FORMAT, entity.getHappenTime()));
        tvTime.setText(TimeUtils.secondToStr(entity.getSecond()));
        getBinding().llContainerHome.addView(view);

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RecordDetailActivity.class);
//                intent.putExtra("recordId", "1");
                intent.putExtra("recordId", entity.getId() + "");
                startActivity(intent);
            }
        });
    }

    public void record(View view) {
        startActivity(NurseRecordActivity.class);
    }

    /*用户信息*/
    public void userInfo(View view) {
        Intent intent = new Intent(this, UserCenterActivity.class);
        startActivityForResult(intent, TYPE_USER_CENTER);
    }

    /*我的设备*/
    public void device(View view) {
        Intent intent = new Intent(this, DeviceSelectActivity.class);
        startActivityForResult(intent, TYPE_DEVICE_SELECT);
    }

    /*设置*/
    public void setting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    /*意见反馈*/
    public void feedback(View view) {
        Intent intent = new Intent(this, FeedbackActivity.class);
        startActivity(intent);
    }

    /*关于我们*/
    public void about(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    /*使用帮助*/
    public void help(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra("title", getString(R.string.center_help_title));
        intent.putExtra("url", HttpUrl.USE_HELP);
        startActivity(intent);
    }

    /*模式选择*/
    public void selectScheme(View view) {
        Intent intent = new Intent(this, SchemeActivity.class);
        intent.putExtra("deviceEntity", deviceEntity);
        startActivity(intent);
    }

    /*点击头像控制侧滑*/
    public void controlDrawer(View view) {
        if (isOpen) {
            getBinding().dlHome.closeDrawers();
        } else {
            getBinding().dlHome.openDrawer(Gravity.LEFT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (isOpen) {
                getBinding().dlHome.closeDrawers();
            }
            if (requestCode == TYPE_USER_CENTER) {
                initUserInfo();
            } else if (requestCode == TYPE_DEVICE_SELECT) {
                getBinding().setHasrecord(true);
                 deviceEntity = (SelectDeviceEntity) data.getSerializableExtra("deviceEntity");
                Glide.with(HomeActivity.this).load(deviceEntity.getSimageUrl()).into(getBinding().layoutHasDevice.ivDeviceSign);
                getRecentlyRecord(deviceEntity.getId());
            }
        }
    }

    DrawerLayout.DrawerListener listener = new DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {
            drawerView.setClickable(true);
            isOpen = true;
        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {
            isOpen = false;
        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(getContext(), getString(R.string.dapp_exit_hint), Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                YamanApplication.getInstance().onTerminate();
            }
        }
        return false;
    }
}
