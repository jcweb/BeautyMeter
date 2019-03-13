package cn.yaman.activity.center;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivityModifyPwBinding;

import androidx.annotation.Nullable;
import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.ToastUtils;
import cn.lamb.http.HttpParams;
import cn.yaman.Constants.HttpUrl;
import cn.yaman.core.CommonTitlebarStrategy;
import cn.yaman.entity.UserEntity.UserBean;
import cn.yaman.http.HttpResponse;
import cn.yaman.http.HttpUtils;
import cn.yaman.http.YamanHttpCallback;
import cn.yaman.utils.PreferenceUtils;

public class ModifyPwActivity extends BaseActivity<ActivityModifyPwBinding> {
    private TextView tvCode;
    private Handler handler;
    private UserBean userBean;
    private static final int MAX_TIME = 60;
    private int lastSecond = MAX_TIME;
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            tvCode.setText(getResources().getString(R.string.login_regetCode, lastSecond));
            if (lastSecond == 0) {
                tvCode.setEnabled(true);
                tvCode.setText(R.string.login_getCode);
                handler.removeCallbacks(run);
                lastSecond = MAX_TIME;
            } else {
                lastSecond--;
                handler.postDelayed(run, 1000L);
            }
        }
    };

    @Override
    public int bindContentView() {
        return R.layout.activity_modify_pw;
    }

    @Override
    public void onProcessor() {
        getBinding().setUi(this);
        setStatusbarMode(true);
        initTitlebar();
        tvCode = getBinding().tvGetVerify;
        userBean = PreferenceUtils.getInstance().getUserEntity().getUser();
        getBinding().tvPhoneInput.setText(userBean.getPhone());
        handler = new Handler();
    }

    private void initTitlebar() {
        CommonTitlebarStrategy titlebar = new CommonTitlebarStrategy();
        setTitlebarStrategy(titlebar);
        titlebar.setTitleName(getString(R.string.pw_modify_title));
        titlebar.setLeftIcon(v -> finish());
    }

    public void vcode(View view) {
        HttpParams params = new HttpParams();
        params.put("phone", userBean.getPhone());
        HttpUtils.newRequester().post(HttpUrl.SEND_CODE, params, new YamanHttpCallback(this) {

            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    handler.post(run);
                    tvCode.setEnabled(false);
                    ToastUtils.toastShort("验证码发送成功");
                } else {
                    ToastUtils.toastShort(response.getResultMsg());
                }
            }
        });

    }

    public void modifyPW(View view) {
        if (checkParameter()){
            HttpParams params = new HttpParams();
            params.put("phone", userBean.getPhone());
            params.put("id", userBean.getId());
            params.put("password", getBinding().etPwInput.getText().toString());
            params.put("captcha", getBinding().etVerifyInput.getText().toString());
            HttpUtils.newRequester().post(HttpUrl.MODIFY_PW, params, new YamanHttpCallback(this) {

                @Override
                public void onSuccess(String url, @Nullable Object o) {
                    super.onSuccess(url, o);
                    if (o == null) {
                        return;
                    }
                    HttpResponse response = (HttpResponse) o;
                    if (response.getResultCode() == 0) {
                        finish();
                    } else {
                        ToastUtils.toastShort(response.getResultMsg());
                    }
                }
            });
        }

    }

    private boolean checkParameter(){
        String verify = getBinding().etVerifyInput.getText().toString();
        String pw = getBinding().etPwInput.getText().toString();
        String pwRe = getBinding().etPwReinput.getText().toString();
        if(TextUtils.isEmpty(verify)){
            ToastUtils.toastShort("验证码不能为空");
            return false;
        }else if(TextUtils.isEmpty(pw)){
            ToastUtils.toastShort("密码不能为空");
            return false;
        }else if(TextUtils.isEmpty(pwRe)){
            ToastUtils.toastShort("密码不能为空");
            return false;
        }else if(!pw.equals(pwRe)){
            ToastUtils.toastShort("两次密码不一样");
            return false;
        }else {
            return true;
        }
    }
}
