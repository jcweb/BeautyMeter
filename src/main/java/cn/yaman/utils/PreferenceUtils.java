package cn.yaman.utils;

import android.content.Context;

import cn.yaman.BasePreference;
import cn.yaman.YamanApplication;
import cn.yaman.entity.UserEntity;
import cn.yaman.entity.UserEntity.UserBean;

/**
 * @author timpkins
 */
public class PreferenceUtils extends BasePreference {
    //用户名的key
    private String USER_NAME = "user_name";
    private static PreferenceUtils preferenceUtils;
    private UserEntity userEntity;

    public PreferenceUtils(Context context) {
        super(context);
        userEntity = new UserEntity();
    }

    public synchronized static PreferenceUtils getInstance() {
        if (null == preferenceUtils) {
            preferenceUtils = new PreferenceUtils(YamanApplication.getInstance());
        }
        return preferenceUtils;
    }

    public UserEntity getUserEntity() {
        UserEntity.UserBean userBean = new UserBean();
        userBean.setBirthday(getString("birthday"));
        userBean.setHeight(getString("height"));
        userBean.setName(getString("name"));
        userBean.setIconUrl(getString("iconUrl"));
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
        setString("birthday", userEntity.getUser().getBirthday());
        setString("name", userEntity.getUser().getName());
        setString("height", userEntity.getUser().getHeight());
        setString("iconUrl", userEntity.getUser().getIconUrl());
    }

    public void loginOut() {
        delete();
    }
}
