package cn.yaman.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;

import com.bumptech.glide.Glide;
import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivityStartNurseBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.lamb.activity.BaseActivity;
import cn.yaman.core.CommonTitlebarStrategy;
import cn.yaman.entity.SchemeDetailEntity;
import cn.yaman.entity.SchemeEntity;

public class StartNurseActivity extends BaseActivity<ActivityStartNurseBinding> {
    private static final int MAX_TIME = 3;
    private int lastSecond = MAX_TIME;
    private SchemeEntity schemeEntity;
    private List<SchemeDetailEntity> list;
    private Handler handler;
    private String prepareIurl;
    int deviceId;
    int schemeId;
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            if (lastSecond == 0) {
                Intent intent = new Intent(StartNurseActivity.this, NurseProcessActivity.class);
                intent.putExtra("schemelist", (Serializable) list);
                intent.putExtra("prepareIurl",prepareIurl);
                intent.putExtra("schemeId", schemeId);
                intent.putExtra("deviceId", deviceId);
                startActivity(intent);
                finish();
            } else if (lastSecond == 2) {
                lastSecond--;
                getBinding().ivCountDown.setImageResource(R.mipmap.ic_count_down_2);
                handler.postDelayed(run, 1000L);
            } else if (lastSecond == 1) {
                lastSecond--;
                getBinding().ivCountDown.setImageResource(R.mipmap.ic_count_down_1);
                handler.postDelayed(run, 1000L);
            } else {
                lastSecond--;
                handler.postDelayed(run, 1000L);
            }
        }
    };

    @Override
    public int bindContentView() {
        return R.layout.activity_start_nurse;
    }

    @Override
    public void onProcessor() {
        initTitlebar();
        list = new ArrayList<>();
        ArrayList<SchemeDetailEntity> schemelist = (ArrayList<SchemeDetailEntity>) getIntent().getSerializableExtra("schemelist");
        list.addAll(schemelist);
        schemeEntity = (SchemeEntity) getIntent().getSerializableExtra("schemeEntity");
        schemeId=getIntent().getIntExtra("schemeId",0);
        deviceId=getIntent().getIntExtra("deviceId", 0);
        initMain(schemeEntity);
        handler = new Handler();
    }

    private void initTitlebar() {
        setStatusbarMode(true);
        CommonTitlebarStrategy titlebar = new CommonTitlebarStrategy();
        setTitlebarStrategy(titlebar);
        titlebar.setTitleName(getString(R.string.start_prepare));
        titlebar.setElevation(R.dimen.dp_02);
        titlebar.setLeftIcon(v -> finish());
    }

    /*初始化页面*/
    private void initMain(SchemeEntity entity) {
        prepareIurl=(String)entity.getPrepareIurl();
        Glide.with(this).load(entity.getPrepareIurl()).into(getBinding().ivNurseImg);

        getBinding().tvExplain.setText(entity.getExplain());
    }

    public void start(View view) {
        getBinding().setClick(true);
        handler.post(run);
    }
}
