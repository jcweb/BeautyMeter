package cn.yaman.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivityRecordDetailBinding;

import androidx.annotation.Nullable;
import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.LogUtils;
import cn.lamb.base.util.ToastUtils;
import cn.lamb.http.HttpParams;
import cn.yaman.Constants.HttpUrl;
import cn.yaman.entity.RecordDetailEntity;
import cn.yaman.entity.RecordDetailEntity.DetailListBean;
import cn.yaman.http.HttpResponse;
import cn.yaman.http.HttpUtils;
import cn.yaman.http.YamanHttpCallback;
import cn.yaman.utils.JsonUtils;
import cn.yaman.utils.ScreenUtils;
import cn.yaman.utils.TimeUtils;

public class RecordDetailActivity extends BaseActivity<ActivityRecordDetailBinding> {
    @Override
    public int bindContentView() {
        return R.layout.activity_record_detail;
    }

    @Override
    public void onProcessor() {
        String id = getIntent().getStringExtra("recordId");
        getRecordDetail(id);
    }

    private void getRecordDetail(String id) {
        HttpParams params = new HttpParams();
        params.put("recordId", id);
        HttpUtils.newRequester().post(HttpUrl.RECORD_DETAIL, params, new YamanHttpCallback(this) {
            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    RecordDetailEntity entity = JsonUtils.getParam(response.getData(), RecordDetailEntity.class);

                    getBinding().tvRecordTitle.setText(TimeUtils.dateToStr(entity.getHappenTime()));
                    getBinding().tvNurseDuration.setText(TimeUtils.secondToStr(entity.getSecond()));
                    getBinding().tvRecordDescribe.setText(getString(R.string.nurse_time) + "  " + TimeUtils.secondToStr(entity.getSecond()));
                    getBinding().tvNurseModel.setText(entity.getSname());
                    int progress = entity.getNtime() / 3600;
                    getBinding().cpSkinAge.setProgress(progress, true);
                    Glide.with(RecordDetailActivity.this).load(entity.getImageUrl()).into(getBinding().ivNurseModel);

                    String[] index = getResources().getStringArray(R.array.record_effect_index);
                    addEffectItem(index[0], entity.getAtp());
                    addEffectItem(index[1], entity.getCollagen());
                    addEffectItem(index[2], entity.getElastin());
                    addEffectItem(index[3], entity.getMicrocirculation());
                    addEffectItem(index[4], entity.getMoisture());
                    addEffectItem(index[5], entity.getBurning());
                    addEffectItem(index[6], entity.getRepair());
                    addEffectItem(index[7], entity.getVitality());
                    addEffectItem(index[8], entity.getBurning());

                    if (entity.getDetailList() != null && entity.getDetailList().size() > 0) {
                        for (int i = 0; i < entity.getDetailList().size(); i++) {
                            addDetailItem(entity.getDetailList().get(i));
                            if (i < entity.getDetailList().size()-1) {
                                addLine();
                            }
                        }
                    }
                    LogUtils.d(response.getData());
                } else {
                    ToastUtils.toastShort(response.getResultMsg());
                }
            }
        });
    }

    private void initMain() {

    }

    /*加载护理功效UI*/
    private void addEffectItem(String index, int progress) {
        View view = LayoutInflater.from(this).inflate(R.layout.nurse_effect_item, null);
        LayoutParams param = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, 1.0f);
        view.setLayoutParams(param);
        ProgressBar pb = view.findViewById(R.id.pb_name);
        TextView tvName = view.findViewById(R.id.tv_progress_name);
        pb.setProgress(progress);
        tvName.setText(index);
        getBinding().llContainerEffect.addView(view);
    }

    /*加载护理细节UI*/
    private void addDetailItem(DetailListBean entity) {
        View view = LayoutInflater.from(this).inflate(R.layout.nurse_detail_item, null);
        ImageView ivDetail = view.findViewById(R.id.iv_nurse_detail);
        TextView tvName = view.findViewById(R.id.tv_detail_name);
        TextView tvTime = view.findViewById(R.id.tv_detail_time);
        tvName.setText(entity.getName());
        Glide.with(this).load(entity.getImageUrl()).into(ivDetail);
        tvTime.setText(TimeUtils.secondToStr(entity.getSecond()));
        getBinding().llContainerDetail.addView(view);
    }

    /*添加竖线*/
    private void addLine() {
        View line = LayoutInflater.from(this).inflate(R.layout.include_horiz_divide_green, null);
        LayoutParams param = new LinearLayout.LayoutParams(
                ScreenUtils.dip2px(this, 1),
                ScreenUtils.dip2px(this, 20));
        ((LinearLayout.LayoutParams) param).leftMargin = ScreenUtils.dip2px(this, 40);
        line.setLayoutParams(param);
        getBinding().llContainerDetail.addView(line);
    }

    public void finish(View view) {
        finish();
    }

}
