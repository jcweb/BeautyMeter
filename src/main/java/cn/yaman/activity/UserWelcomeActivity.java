package cn.yaman.activity;

import android.content.Intent;
import android.view.View;

import com.tcl.smart.beauty.R;

import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.ToastUtils;
import cn.yaman.util.PermissionUtils;
import cn.yaman.utils.LanguageUtils;

/**
 * 欢迎页面
 * @author timpkins
 */
public class UserWelcomeActivity extends BaseActivity {

    @Override
    public int bindContentView() {
        return R.layout.activity_beauty_welcome;
    }

    @Override
    public void onProcessor() {
        setStatusbarMode(true);
        PermissionUtils.checkPermissions(this);
    }

    public void translate(View view){
        ToastUtils.toastShort("translate");
        LanguageUtils.switchLanguage(this);
    }

    public void regist(View view){
        Intent intent = new Intent(this, UserLoginActivity.class);
        intent.putExtra(UserLoginActivity.KEY_TYPE, UserLoginActivity.TYPE_REGIST);
        startActivity(intent);
    }

    public void login(View view){
        Intent intent = new Intent(this, UserLoginActivity.class);
        intent.putExtra(UserLoginActivity.KEY_TYPE, UserLoginActivity.TYPE_LOGIN);
        startActivity(UserLoginActivity.class);
    }
}
