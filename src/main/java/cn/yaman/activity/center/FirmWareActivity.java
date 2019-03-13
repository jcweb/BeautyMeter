package cn.yaman.activity.center;

import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivityFirmWareBinding;

import cn.lamb.activity.BaseActivity;
import cn.yaman.core.CommonTitlebarStrategy;

public class FirmWareActivity extends BaseActivity<ActivityFirmWareBinding> {

    @Override
    public int bindContentView() {
        return R.layout.activity_firm_ware;
    }

    @Override
    public void onProcessor() {
        getBinding().setUi(this);
        setStatusbarMode(true);
        initTitlebar();
    }

    private void initTitlebar() {
        CommonTitlebarStrategy titlebar = new CommonTitlebarStrategy();
        setTitlebarStrategy(titlebar);
        titlebar.setTitleName(getString(R.string.center_version_content));
        titlebar.setLeftIcon(v -> finish());
    }
}
