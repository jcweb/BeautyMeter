package cn.yaman.activity;

import android.os.Handler;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivityNurseProcessBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.LogUtils;
import cn.lamb.base.util.ToastUtils;
import cn.yaman.Constants.HttpUrl;
import cn.yaman.entity.NurseRequestEntity;
import cn.yaman.entity.SchemeDetailEntity;
import cn.yaman.entity.UserEntity;
import cn.yaman.http.HttpResponse;
import cn.yaman.http.HttpUtils;
import cn.yaman.http.YamanHttpCallback;
import cn.yaman.utils.JsonUtils;
import cn.yaman.utils.PreferenceUtils;
import cn.yaman.utils.TimeUtils;
import cn.yaman.view.NurseProcessTitle;

public class NurseProcessActivity extends BaseActivity<ActivityNurseProcessBinding> {
    private List<SchemeDetailEntity> list;
    private String prepareIurl;
    private int curIndex;
    private int sum;
    private int maxTime;
    private int curTime;
    private Handler handler = new Handler();
    private SchemeDetailEntity curEntity;
    private List<NurseRequestEntity.DetailList> reqList = new ArrayList<>();
    int schemeId;
    NurseProcessTitle titlebar;
    int deviceId;

    @Override
    public int bindContentView() {
        return R.layout.activity_nurse_process;
    }

    @Override
    public void onProcessor() {
        initTitlebar();
        list = (ArrayList<SchemeDetailEntity>) getIntent().getSerializableExtra("schemelist");
        prepareIurl = getIntent().getStringExtra("prepareIurl");
        schemeId = getIntent().getIntExtra("schemeId", 0);
        deviceId = getIntent().getIntExtra("deviceId", 0);
//        getBinding().setUi(this);
        titlebar.getPageIndicatorView().initIndicator(list.size());
        sum = list.size();
        initData(list.get(0));
    }

    private void initTitlebar() {
        setStatusbarMode(true);
        titlebar = new NurseProcessTitle();
        setTitlebarStrategy(titlebar);
        titlebar.setElevation(R.dimen.dp_02);
        titlebar.setLeftIcon(v -> finish());
    }

    private void initData(SchemeDetailEntity entity) {
        handler.removeCallbacks(timeRun);
        curEntity = entity;
        titlebar.getPageIndicatorView().setSelectedPage(curIndex);
        curTime = Integer.valueOf(entity.getDuration());
        if (curIndex == sum - 1) {
            getBinding().nurseProcessVpNext.setVisibility(View.GONE);
        }

        Glide.with(this).asGif().load(entity.getGifUrl()).into(getBinding().nurseProcessVpImg);
        getBinding().nurseProcessVpTitle.setText(entity.getName());
        getBinding().nurseProcessVpPart.setText(getString(R.string.nurse_process_part) + entity.getPart());
        getBinding().nurseProcessVpContent.setText(entity.getScript());
        getBinding().nurseProcessVpTime.setText(TimeUtils.secondToStr(curTime));
        getBinding().nurseProcessVpTip.setText(entity.getRemark());
        handler.postDelayed(timeRun, 1000);
    }

    private Runnable timeRun = new Runnable() {
        @Override
        public void run() {
            curTime -= 1;
            if (curIndex < sum && curTime > 0) {
                getBinding().nurseProcessVpTime.setText(TimeUtils.secondToStr(curTime));
                handler.postDelayed(timeRun, 1000);
            } else if (curIndex == sum - 1) {
                if (curTime > 0) {
                    getBinding().nurseProcessVpTime.setText(TimeUtils.secondToStr(curTime));
                    handler.postDelayed(timeRun, 1000);
                } else {
                    addData();
                    commit();
                }
            } else {
                addData();
                curIndex += 1;
                initData(list.get(curIndex));

            }
        }
    };

    private void show() {
//    Glide.with(this).asGif().load(list.get(0).getGifUrl()).into(getBinding().ivNurseProcess);
    }

    public void pause(View view) {
        handler.removeCallbacks(timeRun);
        getBinding().nurseProcessVpPp.setVisibility(View.GONE);
        getBinding().nurseProcessVpPe.setVisibility(View.VISIBLE);
    }

    public void next(View view) {
        addData();
        curIndex += 1;
        initData(list.get(curIndex));
    }

    public void play(View view) {
        handler.postDelayed(timeRun, 1000);
        getBinding().nurseProcessVpPe.setVisibility(View.GONE);
        getBinding().nurseProcessVpPp.setVisibility(View.VISIBLE);
    }

    public void end(View view) {
        addData();
        commit();
    }

    private void addData() {
        NurseRequestEntity.DetailList item = new NurseRequestEntity().new DetailList();
        item.setSecond(Integer.valueOf(curEntity.getDuration()) - curTime);
        item.setOperationId(curEntity.getId() + "");
        reqList.add(item);
    }

    public void finish(View view) {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        handler.removeCallbacks(timeRun);
    }

    public void commit() {
        NurseRequestEntity entity = new NurseRequestEntity();
        UserEntity userEntity = PreferenceUtils.getInstance().getUserEntity();
        UserEntity.UserBean bean = userEntity.getUser();

        entity.setUserId(bean.getId());
        entity.setDeviceId(deviceId);
        entity.setSchemeId(schemeId);
        entity.setList(reqList);
        String json = new Gson().toJson(entity);

        LogUtils.e(json);
        HttpUtils.newRequester(true).post(HttpUrl.RECORD_ADD, json, new YamanHttpCallback(this) {
            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    finish();
                    List<SchemeDetailEntity> schemeList = JsonUtils.getParamList(response.getData(), SchemeDetailEntity.class);
                } else {
                    ToastUtils.toastShort(response.getResultMsg());
                }
            }
        });

    }
}
