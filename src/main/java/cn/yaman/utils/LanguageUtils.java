package cn.yaman.utils;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.LogUtils;
import cn.lamb.core.impl.SharedPreferencesHelper;
import cn.yaman.Constants.Language;
import cn.yaman.Constants.Preferences;
import cn.yaman.YamanApplication;

/**
 * 语言切换
 * @author timpkins
 */
public final class LanguageUtils {

    public static void switchLanguage(BaseActivity activity) {
        SharedPreferencesHelper sharedPreferences = YamanApplication.getSharedPreferences();
        String currentLang = sharedPreferences.getData(Preferences.APP_LANGUAGE, Language.ZH);
        String language = Language.ZH.equals(currentLang) ? Language.EN : Language.ZH;
        LogUtils.e("需设置语言 = " + language);
        Locale myLocale = new Locale(language);
        Resources res = YamanApplication.getInstance().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        activity.recreate();//刷新界面
        sharedPreferences.putData(Preferences.APP_LANGUAGE, language);
    }
}
