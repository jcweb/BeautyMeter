package cn.yaman;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author timpkins
 */
public class BasePreference {
    private Context context;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String FILE_NAME = "userinfo";

    public BasePreference(Context context) {
        this.context = context;
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public void setString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return sp.getString(key, null);
    }

    public void setBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public void setInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    public int getInt(String key) {
        return sp.getInt(key, 0);
    }

    public void delete(){
        sp.edit().clear();
    }

    public void open(String name) {
        this.open(name, 0);
    }

    public void open(String name, int version) {
        String fileName = name + "_" + version;
        this.sp = this.context.getSharedPreferences(fileName, 0);
    }
}
