package cn.yaman.activity.center;

import android.content.Intent;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.bumptech.glide.Glide;
import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivityUserCenterBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.Nullable;
import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.LogUtils;
import cn.lamb.base.util.ToastUtils;
import cn.lamb.http.HttpParams;
import cn.yaman.Constants.HttpUrl;
import cn.yaman.core.CommonTitlebarStrategy;
import cn.yaman.dialog.DateDailog;
import cn.yaman.dialog.DateDailog.OnDateChooseListener;
import cn.yaman.dialog.HeightDialog;
import cn.yaman.dialog.HeightDialog.OnHeightSelectedListener;
import cn.yaman.dialog.NameEditDialog;
import cn.yaman.dialog.NameEditDialog.OnNameSetListener;
import cn.yaman.dialog.SexDialog;
import cn.yaman.dialog.SexDialog.OnSexSelectedListener;
import cn.yaman.entity.UserEntity;
import cn.yaman.entity.UserEntity.UserBean;
import cn.yaman.http.HttpResponse;
import cn.yaman.http.HttpUtils;
import cn.yaman.http.YamanHttpCallback;
import cn.yaman.utils.PathUtils;
import cn.yaman.utils.PreferenceUtils;

public class UserCenterActivity extends BaseActivity<ActivityUserCenterBinding> {
    private String strDate = "";//日期1999-01-08
    private String strHeight = "";//身高
    private int sex = -1;//性别
    private static String DATE_FORMAT = "yyyy-MM-dd";
    private UserEntity userEntity;
    private static final int REQUEST_ALBUM = 0x02;

    @Override
    public int bindContentView() {
        return R.layout.activity_user_center;
    }

    @Override
    public void onProcessor() {
        getBinding().setUi(this);
        setStatusbarMode(true);
        initTitlebar();
        initUserInfo();
    }

    private void initUserInfo() {
        userEntity = new UserEntity();
        userEntity = PreferenceUtils.getInstance().getUserEntity();
        UserEntity.UserBean userBean = userEntity.getUser();
        getBinding().tvCenterNick.setText(userBean.getName());
        String str = userBean.getSex() == 0 ? getString(R.string.dialog_sex_female) : getString(R.string.dialog_sex_male);
        getBinding().tvCenterSex.setText(str);
        getBinding().tvCenterHeight.setText(userBean.getHeight());
        getBinding().tvCenterBirth.setText(userBean.getBirthday());
        if (!TextUtils.isEmpty(userEntity.getUser().getIconUrl())) {
            Glide.with(this).load(userEntity.getUser().getIconUrl()).into(getBinding().ivCenterImage);
        }
    }

    /*出生日期设置*/
    public void birthdayDialog(View view) {

        DateDailog dateDailog = new DateDailog();
        dateDailog.setOnDateChooseListener(new OnDateChooseListener() {
            @Override
            public void onDateChoose(int year, int month, int day) {
//                getBinding().tvCenterBirth.setText(year + "年" + month + "月" + day + "日");
//                strDate = year + "-" + month + "-" + day;
                getBinding().tvCenterBirth.setText(year + "-" + month + "-" + day);
            }
        });
        dateDailog.show(getSupportFragmentManager(), "DateDailog");
    }

    /*昵称设置*/
    public void nickDialog(View view) {
        NameEditDialog dialog = new NameEditDialog();
        dialog.setOnNameSetListener(new OnNameSetListener() {
            @Override
            public void onNameSet(String name) {
                getBinding().tvCenterNick.setText(name);
            }
        });
        dialog.show(getSupportFragmentManager(), "nameDialog");
    }

    /*设置性别*/
    public void sexDialog(View view) {
        SexDialog dialog = new SexDialog();
        dialog.setListener(new OnSexSelectedListener() {
            @Override
            public void onSelected(int type) {
                sex = type;
                String strSex = sex == 0 ? getString(R.string.dialog_sex_female) : getString(R.string.dialog_sex_male);
                getBinding().tvCenterSex.setText(strSex);
            }
        });
        dialog.show(getSupportFragmentManager(), "sexDialog");
    }

    /*设置身高*/
    public void heightDialog(View view) {
        HeightDialog heightDialog = new HeightDialog();
        heightDialog.setOnHeightSelectedListener(new OnHeightSelectedListener() {
            @Override
            public void onSelected(int height) {
                strHeight = height + "";
                getBinding().tvCenterHeight.setText(height + "cm");
            }
        });
        heightDialog.show(getSupportFragmentManager(), "heightDialog");
    }

    /*完成*/
    public void commit() {
        String strNick = getBinding().tvCenterNick.getText().toString();
        String strHeight = getBinding().tvCenterHeight.getText().toString();
        String strSex = getBinding().tvCenterSex.getText().toString();
        strDate = getBinding().tvCenterBirth.getText().toString();
        long time = 0L;
        if (TextUtils.isEmpty(strNick)) {
            ToastUtils.toastShort(getString(R.string.toast_user_nick));
        } else if (TextUtils.isEmpty(strDate)) {
            ToastUtils.toastShort(getString(R.string.toast_user_birthday));
        } else if (TextUtils.isEmpty(strHeight)) {
            ToastUtils.toastShort(getString(R.string.toast_user_height));
        } else if (TextUtils.isEmpty(strSex)) {
            ToastUtils.toastShort(getString(R.string.toast_user_sex));
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
            try {
                Date date = formatter.parse(strDate);
                time = date.getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
            HttpParams params = new HttpParams();
            params.put("name", strNick);
            params.put("sex", sex);
            params.put("birthdayLong", time);
            params.put("height", strHeight);
            params.put("id", userEntity.getUser().getId());
            HttpUtils.newRequester().post(HttpUrl.USER_MODIFY, params, new YamanHttpCallback(this) {
                @Override
                public void onSuccess(String url, @Nullable Object o) {
                    super.onSuccess(url, o);
                    if (o == null) {
                        return;
                    }
                    HttpResponse response = (HttpResponse) o;
                    if (response.getResultCode() == 0) {
                        UserBean userBean = userEntity.getUser();
                        userBean.setHeight(strHeight);
                        userBean.setName(strNick);
                        userBean.setBirthday(strDate);
                        userBean.setId(userEntity.getUser().getId());
                        userEntity.setUser(userBean);
                        PreferenceUtils.getInstance().setUserEntity(userEntity);
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        ToastUtils.toastShort(response.getResultMsg());
                    }
                }
            });
        }
    }

    /*设置头像*/
    public void selectIcon(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_ALBUM);
    }

    /*上传头像*/
    public void uploadIcon(String imageUrl) {
        HttpUtils.newRequester().uploadFile(HttpUrl.RECORD_LIST, imageUrl, new YamanHttpCallback() {
            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                LogUtils.e(response.getData());
                if (response.getResultCode() == 0) {
                    ToastUtils.toastShort(response.getResultMsg());
                    Glide.with(UserCenterActivity.this).load(imageUrl).into(getBinding().ivCenterImage);
                }
            }
        });
    }

    private void initTitlebar() {
        CommonTitlebarStrategy titlebar = new CommonTitlebarStrategy();
        setTitlebarStrategy(titlebar);
        titlebar.setTitleName(getString(R.string.center_user_title));
        titlebar.setTitleRight("完成", new OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });
        titlebar.setLeftIcon(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ALBUM && resultCode == RESULT_OK) {
            String url = PathUtils.getPath(UserCenterActivity.this, data.getData(), Images.Media.EXTERNAL_CONTENT_URI);
            uploadIcon(url);
        }
    }
}
