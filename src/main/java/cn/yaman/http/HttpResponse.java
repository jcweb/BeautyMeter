package cn.yaman.http;

import androidx.annotation.NonNull;

/**
 * @author timpkins
 */
@SuppressWarnings("WeakerAccess")
public class HttpResponse {
    private int resultCode; // 状态码，为 0 则表示正常
    private String resultMsg; // 错误描述
    private String data; // 请求结果

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @NonNull
    @Override
    public String toString() {
        return "HttpResponse{ code=" + resultCode + ", message=\"" + resultMsg + "\", data=\"" + data + "\"}";
    }
}
