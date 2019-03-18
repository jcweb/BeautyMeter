package cn.yaman.activity.center;

import android.text.Html;
import android.text.method.ScrollingMovementMethod;

import com.bumptech.glide.Glide;
import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivityAboutBinding;

import androidx.annotation.Nullable;
import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.ToastUtils;
import cn.lamb.http.HttpParams;
import cn.yaman.Constants.HttpUrl;
import cn.yaman.core.CommonTitlebarStrategy;
import cn.yaman.entity.AboutEntity;
import cn.yaman.http.HttpResponse;
import cn.yaman.http.HttpUtils;
import cn.yaman.http.YamanHttpCallback;
import cn.yaman.utils.JsonUtils;

public class AboutActivity extends BaseActivity<ActivityAboutBinding> {

    @Override
    public int bindContentView() {
        return R.layout.activity_about;
    }

    @Override
    public void onProcessor() {
        getBinding().setUi(this);
        setStatusbarMode(true);
        initTitlebar();
        getBinding().tvAboutContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        getHelpContent();
    }

    private void getHelpContent() {
        HttpParams params = new HttpParams();
        HttpUtils.newRequester().post(HttpUrl.ABOUT_US, params, new YamanHttpCallback(this) {
            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    AboutEntity aboutEntity = JsonUtils.getParam(response.getData(), AboutEntity.class);
                    getBinding().tvAboutContent.setText(Html.fromHtml(aboutEntity.getContent()));
                    Glide.with(AboutActivity.this).load(aboutEntity.getLogo()).into(getBinding().ivLogo);
                } else {
                    ToastUtils.toastShort(response.getResultMsg());
                }
            }
        });
    }

    private void initTitlebar() {
        CommonTitlebarStrategy titlebar = new CommonTitlebarStrategy();
        setTitlebarStrategy(titlebar);
        titlebar.setTitleName(getString(R.string.center_about_title));
        titlebar.setLeftIcon(v -> finish());
    }
}
