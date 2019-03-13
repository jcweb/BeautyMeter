package cn.yaman.activity;

import android.text.method.ScrollingMovementMethod;
import android.view.View;

import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivityUserProtocolBinding;

import cn.lamb.activity.BaseActivity;
import cn.yaman.core.CommonTitlebarStrategy;

/**
 * 用户登录，注册和忘记密码
 * @author timpkins
 */
public class UserProtocolActivity extends BaseActivity<ActivityUserProtocolBinding> {

    @Override
    public int bindContentView() {
        return R.layout.activity_user_protocol;
    }

    @Override
    public void onProcessor() {
        setStatusbarMode(true);
        initTitlebar();

        getBinding().tvAgreementContent.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void initTitlebar() {
        CommonTitlebarStrategy titlebar = new CommonTitlebarStrategy();
        setTitlebarStrategy(titlebar);
        titlebar.setTitleName(R.string.login_protocol);
        titlebar.setElevation(R.dimen.dp_02);
        titlebar.setLeftIcon(v -> finish());
    }

    public void login(View view){
        startActivity(UserPasswdActivity.class);
    }
}
