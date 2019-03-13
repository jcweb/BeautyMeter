package cn.yaman;

import android.content.res.Configuration;
import android.util.DisplayMetrics;

import java.util.Locale;

import cn.lamb.BaseApplication;
import cn.yaman.Constants.Language;
import cn.yaman.Constants.Preferences;
import cn.yaman.bluetooth.BLETool;

/**
 * @author timpkins
 */
public class YamanApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        setAppLanguage();
        BLETool.getInstance().init(getApplicationContext());
    }

    private void setAppLanguage(){
        String currentLanguage = getSharedPreferences().getData(Preferences.APP_LANGUAGE, Language.ZH);
        Locale locale = new Locale(currentLanguage);
        Configuration configuration = getResources().getConfiguration();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        configuration.locale = locale;
        getResources().updateConfiguration(configuration, dm);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        BLETool.getInstance().disConnectBluetooth();
    }
}
