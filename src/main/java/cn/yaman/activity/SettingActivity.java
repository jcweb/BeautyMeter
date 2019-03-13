package cn.yaman.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivitySettingBinding;

import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.ToastUtils;
import cn.yaman.activity.center.ModifyPwActivity;
import cn.yaman.core.CommonTitlebarStrategy;
import cn.yaman.dialog.ExitDialog;
import cn.yaman.dialog.ExitDialog.OnSelectedConfirm;
import cn.yaman.dialog.PhoneModifyDialog;
import cn.yaman.dialog.PhoneModifyDialog.OnModifyListener;
import cn.yaman.utils.PreferenceUtils;

public class SettingActivity extends BaseActivity<ActivitySettingBinding> {

    @Override
    public int bindContentView() {
        return R.layout.activity_setting;
    }

    @Override
    public void onProcessor() {
        getBinding().setUi(this);
        setStatusbarMode(true);
        initTitlebar();
    }

    /*修改密码*/
    public void modifyPw(View view) {
        startActivity(ModifyPwActivity.class);
    }
    /*清理缓存*/

    /*更换手机号*/
    public void modifyPhone(View view){
        PhoneModifyDialog dialog = new PhoneModifyDialog();
        dialog.setOnModifyListener(new OnModifyListener() {
            @Override
            public void onFinish() {
                ToastUtils.toastShort("修改成功");
                PreferenceUtils.getInstance().loginOut();
                Intent intent = new Intent(SettingActivity.this, UserLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        dialog.show(getSupportFragmentManager(),"modifyDialog");
    }

    /*退出登录*/
    private void loginOut() {
        ExitDialog exitDialog = new ExitDialog();
        exitDialog.setOnSelectedConfirm(new OnSelectedConfirm() {
            @Override
            public void OnConfirm() {
                PreferenceUtils.getInstance().loginOut();
                Intent intent = new Intent(SettingActivity.this, UserLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        exitDialog.show(getSupportFragmentManager(), "exitDialog");
    }

    private void initTitlebar() {
        CommonTitlebarStrategy titlebar = new CommonTitlebarStrategy();
        setTitlebarStrategy(titlebar);
        titlebar.setTitleName("设置");
        titlebar.setTitleRight("退出登录", new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginOut();
            }
        });
        titlebar.setLeftIcon(v -> finish());
    }


}
