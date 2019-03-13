package cn.yaman.activity;

import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivityUserPasswdBinding;

import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.LogUtils;
import cn.yaman.core.CommonTitlebarStrategy;

/**
 * 用户登录，注册和忘记密码
 * @author timpkins
 */
public class UserPasswdActivity extends BaseActivity<ActivityUserPasswdBinding> {
    public static final String KEY_TYPE = "ktype";
    public static final int TYPE_REGIST = 0x01; // 注册
    public static final int TYPE_FORGET = 0x02; // 忘记密码
    private int type = TYPE_REGIST;

    @Override
    public int bindContentView() {
        return R.layout.activity_user_passwd;
    }

    @Override
    public void onProcessor() {
        setStatusbarMode(true);
        initTitlebar();

        type = getIntent().getIntExtra(KEY_TYPE, TYPE_REGIST);
//        getBinding().setType(type);
        getBinding().setType(TYPE_REGIST);
        LogUtils.e("当前类型 = " + type);
    }

    private void initTitlebar() {
        CommonTitlebarStrategy titlebar = new CommonTitlebarStrategy();
        setTitlebarStrategy(titlebar);
        titlebar.setLeftIcon(v -> finish());
    }
}
