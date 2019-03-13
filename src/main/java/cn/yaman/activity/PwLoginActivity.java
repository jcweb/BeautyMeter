package cn.yaman.activity;

import android.view.View;

import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivityPwLoginBinding;

import androidx.annotation.Nullable;
import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.ToastUtils;
import cn.lamb.http.HttpParams;
import cn.lamb.util.ValidateData;
import cn.yaman.Constants.HttpUrl;
import cn.yaman.core.CommonTitlebarStrategy;
import cn.yaman.entity.UserEntity;
import cn.yaman.http.HttpResponse;
import cn.yaman.http.HttpUtils;
import cn.yaman.http.YamanHttpCallback;
import cn.yaman.utils.JsonUtils;
import cn.yaman.utils.PreferenceUtils;

public class PwLoginActivity extends BaseActivity<ActivityPwLoginBinding> {
    public static final String HINT_TYPE = "hinttype";
    public static int SET_PW = 0x02;
    private int type = SET_PW;
    private String url= HttpUrl.USER_REGIST;

    @Override
    public int bindContentView() {
        return R.layout.activity_pw_login;
    }

    @Override
    public void onProcessor() {
        setStatusbarMode(true);
        initTitlebar();
        type = getIntent().getIntExtra(HINT_TYPE, SET_PW);
        url = type==SET_PW?HttpUrl.USER_REGIST:HttpUrl.FORGET_PW;
        getBinding().setType(type);
        getBinding().setUi(this);
    }

    private void initTitlebar() {
        CommonTitlebarStrategy titlebar = new CommonTitlebarStrategy();
        setTitlebarStrategy(titlebar);
        titlebar.setLeftIcon(v -> finish());
    }

    public void regist(View view) {
        String passwd = getBinding().etLoginPasswd.getText().toString();
        if (!ValidateData.isPasswd(passwd)) {
            ToastUtils.toastShort(getString(R.string.center_verify_input));
        } else {
            HttpParams params = new HttpParams();
            params.put("phone", PreferenceUtils.getInstance().getUserEntity().getUser().getPhone());
            params.put("password", passwd);
            HttpUtils.newRequester().post(url, params, new YamanHttpCallback(this) {
                @Override
                public void onSuccess(String url, @Nullable Object o) {
                    super.onSuccess(url, o);
                    if (o == null) {
                        return;
                    }
                    HttpResponse response = (HttpResponse) o;
                    if (response.getResultCode() == 0) {
                        login();
                    } else {
                        ToastUtils.toastShort(response.getResultMsg());
                    }
                }
            });
        }
    }

    private void login() {
        HttpParams params = new HttpParams();
        params.put("phone", PreferenceUtils.getInstance().getUserEntity().getUser().getPhone());
        params.put("password", getBinding().etLoginPasswd.getText().toString());
        HttpUtils.newRequester(true).post(HttpUrl.USER_LOGIN, params, new YamanHttpCallback(this) {
            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    UserEntity userEntity = JsonUtils.getParam(response.getData(), UserEntity.class);
                    PreferenceUtils.getInstance().setUserEntity(userEntity);
                    startActivity(HomeActivity.class);
                    finish();
                } else {
                    ToastUtils.toastShort(response.getResultMsg());
                }
            }
        });
    }
}
