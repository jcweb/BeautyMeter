package cn.yaman.viewmodel;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import cn.lamb.base.util.LogUtils;
import cn.lamb.viewmodel.BaseViewModel;
import cn.yaman.Constants.HttpUrl;
import cn.yaman.UserModel;
import cn.yaman.http.HttpUtils;
import cn.yaman.http.YamanHttpCallback;

/**
 * @author timpkins
 */
public class UserViewModel extends BaseViewModel {
    private MutableLiveData<UserModel> user = new MutableLiveData<>();

    public UserViewModel() {

    }

    public MutableLiveData<UserModel> getUser() {
        return user;
    }

    public void setUser(String phone, String passwd) {
        user.setValue(new UserModel(phone, passwd));
//        String jsonParams = "{\"phone\":\"13923430920\",\"password\":\"123456\"}";
        String jsonParams = "{\"password\":\"123456\"}";
//        String jsonParams = "password=123456";
        HttpUtils.newRequester().post(HttpUrl.USER_LOGIN, jsonParams, new YamanHttpCallback(){
            @Override
            public void onStart() {
                super.onStart();
                LogUtils.e("onStart");
            }

            @Override
            public void onFailure(String url, String statusCode) {
                super.onFailure(url, statusCode);
                LogUtils.e("onFailure");
            }

            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                LogUtils.e("onSuccess");
            }

            @Override
            public void onSuccess(String url, String result) {
                super.onSuccess(url, result);
                LogUtils.e("onSuccess");
            }

            @Override
            public void onError(String url, Exception e) {
                super.onError(url, e);
                LogUtils.e("onError");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                LogUtils.e("onFinish");
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
