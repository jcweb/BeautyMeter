package cn.yaman.http;

import androidx.annotation.Nullable;
import cn.lamb.activity.BaseActivity;
import cn.lamb.http.HttpCallback;

/**
 * @author timpkins
 */
public class YamanHttpCallback implements HttpCallback {
    private BaseActivity activity;

    public YamanHttpCallback() {
    }

    public YamanHttpCallback(BaseActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onStart() {
        if (activity != null) {
            activity.showLoading();
        }
    }

    @Override
    public void onFailure(String url, String statusCode) {

    }

    @Override
    public void onSuccess(String url, @Nullable Object o) {

    }

    @Override
    public void onSuccess(String url, String result) {

    }

    @Override
    public void onError(String url, Exception e) {

    }

    @Override
    public void onFinish() {
        if (activity != null) {
            activity.dismissLoading();
        }
    }
}
