package cn.yaman.http;


import android.text.TextUtils;

import java.util.HashMap;

import cn.lamb.http.HttpOption;
import cn.lamb.http.HttpRequester;
import cn.yaman.utils.PreferenceUtils;

/**
 * 网络请求工具类
 * @author timpkins
 */
public final class HttpUtils {

    public static HttpRequester newRequester(boolean isLogin) {
        HttpRequester requester = new HttpRequester();
        requester.setOption(createOption(isLogin));
        return requester;
    }

    public static HttpRequester newRequester() {
        HttpRequester requester = new HttpRequester();
        requester.setOption(createOption());
        return requester;
    }

    private static HttpOption createOption(boolean isLogin) {
        HttpOption option = new HttpOption();
        option.setMediaType(HttpOption.MEDIA_JSON);
        HashMap<String, String> bodys = new HashMap<>();
        option.setBodys(bodys);
        option.setDeserializer(new ResponseDeserializer());
        option.setDebug(false);

        /*临时处理*/
        HashMap<String, String> headers = new HashMap<>();
        String token = PreferenceUtils.getInstance().getUserEntity().getAccessToken();
        if(!TextUtils.isEmpty(token)){
            headers.put("Authorization", token);
        }
        option.setHeaders(headers);

        return option;
    }

    private static HttpOption createOption() {
        HttpOption option = new HttpOption();
        option.setMediaType(HttpOption.MEDIA_XWWW);
        HashMap<String, String> bodys = new HashMap<>();
        option.setBodys(bodys);

        HashMap<String, String> headers = new HashMap<>();
        String token = PreferenceUtils.getInstance().getUserEntity().getAccessToken();
        if(!TextUtils.isEmpty(token)){
            headers.put("Authorization", token);
        }
        option.setHeaders(headers);
        option.setDeserializer(new ResponseDeserializer());
        option.setDebug(false);
        return option;
    }
}
