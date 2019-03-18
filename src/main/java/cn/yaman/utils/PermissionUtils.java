package cn.yaman.utils;

import android.Manifest.permission;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;

public class PermissionUtils {

    //权限
    private static String[] PERMISSIONS_STORAGE = {
            permission.READ_EXTERNAL_STORAGE,
            permission.WRITE_EXTERNAL_STORAGE,
            permission.ACCESS_FINE_LOCATION,
            permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            permission.READ_PHONE_STATE,
            permission.ACCESS_COARSE_LOCATION,
            permission.SYSTEM_ALERT_WINDOW,
            permission.CAMERA
    };
    private static List<String> mPermissionList = new ArrayList<>();

    public static void checkPermissions(Activity activity) {
        mPermissionList.clear();//清空没有通过的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int len = PERMISSIONS_STORAGE.length;
            for (int i = 0; i < len; i++) {
                if (ActivityCompat.checkSelfPermission(activity, PERMISSIONS_STORAGE[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(PERMISSIONS_STORAGE[i]);//添加还未授予的权限
                }
                //申请权限
                if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
                    ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, 100);
                }
            }
        }
    }

}
