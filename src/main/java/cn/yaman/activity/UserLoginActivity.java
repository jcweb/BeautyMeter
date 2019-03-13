package cn.yaman.activity;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tcl.smart.beauty.BuildConfig;
import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivityUserLoginBinding;

import androidx.annotation.Nullable;
import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.LogUtils;
import cn.lamb.base.util.ToastUtils;
import cn.lamb.http.HttpParams;
import cn.lamb.util.ValidateData;
import cn.yaman.Constants.HttpUrl;
import cn.yaman.activity.center.HelpActivity;
import cn.yaman.core.CommonTitlebarStrategy;
import cn.yaman.entity.UserEntity;
import cn.yaman.http.HttpResponse;
import cn.yaman.http.HttpUtils;
import cn.yaman.http.YamanHttpCallback;
import cn.yaman.utils.JsonUtils;
import cn.yaman.utils.PreferenceUtils;

/**
 * 用户登录，注册和忘记密码
 * @author timpkins
 */
public class UserLoginActivity extends BaseActivity<ActivityUserLoginBinding> {
    public static final String KEY_TYPE = "ktype";
    public static final int TYPE_LOGIN = 0x01; // 登录
    public static final int TYPE_REGIST = 0x02; // 注册
    public static final int TYPE_FORGET = 0x03; // 忘记密码
    private int type = TYPE_LOGIN;
    private static final int MAX_TIME = 60;
    private int lastSecond = MAX_TIME;
    private static final int COUNTRY_CODE = 100;
    private TextView tvCode;
    private Handler handler;
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
        return R.layout.activity_user_login;
    }

    public void setCountryCode(View view) {
        Intent intent = new Intent(this, AreaSelectActivity.class);
        startActivityForResult(intent, COUNTRY_CODE);
    }

    @Override
    public void onProcessor() {
        setStatusbarMode(true);
        initTitlebar();

        type = getIntent().getIntExtra(KEY_TYPE, TYPE_LOGIN);
        getBinding().setType(type);
        getBinding().setUi(this);
        tvCode = getBinding().tvUserGetcode;
        handler = new Handler();

        getBinding().etUserPhone.setText(BuildConfig.USER_NAME);
        getBinding().etUserPasswd.setText(BuildConfig.USER_PASSWD);
    }

    private void initTitlebar() {
        CommonTitlebarStrategy titlebar = new CommonTitlebarStrategy();
        setTitlebarStrategy(titlebar);
        titlebar.setLeftIcon(v -> finish());
    }

    public void login(int type) {
        String phone = getBinding().etUserPhone.getText().toString().trim();
        String passwd = "";
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.toastShort(getString(R.string.login_phone_hint));
        } else if (!ValidateData.isPhone(phone)) {


            ToastUtils.toastShort("请输入有效的手机号码");
        }

        if (type == TYPE_LOGIN) {
            passwd = getBinding().etUserPasswd.getText().toString().trim();
        } else {
            passwd = getBinding().etUserCode.getText().toString().trim();
        }


        if (TextUtils.isEmpty(passwd)) {
            if (type == TYPE_LOGIN) {
                ToastUtils.toastShort(getString(R.string.login_passwd));
            } else {
                ToastUtils.toastShort(getString(R.string.login_code));
            }
        }
        if (type == TYPE_LOGIN) {
            if (!ValidateData.isPasswd(passwd)) {
                ToastUtils.toastShort("请输入至少6位的登录密码");
            } else {
                HttpParams params = new HttpParams();
                params.put("phone", phone);
                params.put("password", passwd);
//                params.put("password", ValidateData.md5(passwd));
                HttpUtils.newRequester(true).post(HttpUrl.USER_LOGIN, params, new YamanHttpCallback(this) {
                    @Override
                    public void onSuccess(String url, @Nullable Object o) {
                        super.onSuccess(url, o);
                        if (o == null) {
                            return;
                        }
                        HttpResponse response = (HttpResponse) o;
                        if (response.getResultCode() == 0) {
                            LogUtils.d(response.getData());
                            UserEntity userEntity = JsonUtils.getParam(response.getData(), UserEntity.class);
                            PreferenceUtils.getInstance().setUserEntity(userEntity);
//                            Intent intent = new Intent(UserLoginActivity.this,HomeActivity.class);
//                            intent.putExtra("userEntity",userEntity);
                            startActivity(HomeActivity.class);
                            finish();
                        } else {
                            ToastUtils.toastShort(response.getResultMsg());
                        }
                    }
                });
            }
        } else if (type == TYPE_REGIST || type == TYPE_FORGET) {
//            getBinding().etUserPasswd.setInputType(InputType.TYPE_CLASS_NUMBER);
            if (TextUtils.isEmpty(passwd) || passwd.length() < 4) {
                ToastUtils.toastShort(getString(R.string.center_verify_input));
            } else {
                HttpParams params = new HttpParams();
                params.put("phone", phone);
                params.put("captcha", passwd);
                HttpUtils.newRequester().post(HttpUrl.REGIST_CHECK, params, new YamanHttpCallback(this) {
                    @Override
                    public void onSuccess(String url, @Nullable Object o) {
                        super.onSuccess(url, o);
                        if (o == null) {
                            return;
                        }
                        HttpResponse response = (HttpResponse) o;
                        if (response.getResultCode() == 0 || response.getResultCode() == 1007) {//1007其实是session引起的错误的一种情况，临时处理，后续再改
//                            UserBean userBean = new UserBean();
//                            userBean.setPhone(phone);
//                            UserEntity userEntity = new UserEntity();
//                            userEntity.setUser(userBean);
                            PreferenceUtils.getInstance().getUserEntity().getUser().setPhone(phone);
                            PreferenceUtils.getInstance().setUserEntity(PreferenceUtils.getInstance().getUserEntity());
                            Intent intent = new Intent(UserLoginActivity.this, PwLoginActivity.class);
                            intent.putExtra(PwLoginActivity.HINT_TYPE, type);
                            startActivity(intent);
                            finish();
                        } else {
                            ToastUtils.toastShort(response.getResultMsg());
                        }
                    }
                });
            }
        }
    }

    public void protocol(View view) {
        Intent intent = new Intent(UserLoginActivity.this, HelpActivity.class);
        intent.putExtra("title", getString(R.string.login_protocol));
        intent.putExtra("url", HttpUrl.USER_PROTOCOL);
        startActivity(intent);
    }

    public void vcode(View view) {
        String phone = getBinding().etUserPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.toastShort("请输入手机号码");
        } else if (!ValidateData.isPhone(phone)) {
            ToastUtils.toastShort("请输入有效手机号码");
        } else {
            HttpParams params = new HttpParams();
            params.put("phone", phone);
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

    }

    public void regist(View view) {
        Intent intent = new Intent(this, UserLoginActivity.class);
        intent.putExtra(UserLoginActivity.KEY_TYPE, UserLoginActivity.TYPE_REGIST);
        startActivity(intent);
    }

    public void forget(View view) {
        Intent intent = new Intent(this, UserLoginActivity.class);
        intent.putExtra(UserLoginActivity.KEY_TYPE, UserLoginActivity.TYPE_FORGET);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COUNTRY_CODE && resultCode == RESULT_OK) {
            String code = data.getStringExtra("countryCode");
            if (!TextUtils.isEmpty(code)) {
                getBinding().tvCountryCode.setText(code);
            }
        }
    }
}
