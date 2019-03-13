package cn.yaman.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tcl.smart.beauty.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import cn.lamb.base.util.ToastUtils;
import cn.lamb.http.HttpParams;
import cn.yaman.Constants.HttpUrl;
import cn.yaman.http.HttpResponse;
import cn.yaman.http.HttpUtils;
import cn.yaman.http.YamanHttpCallback;
import cn.yaman.utils.PreferenceUtils;

/**
 * @author timpkins
 */
public class PhoneModifyDialog extends DialogFragment {
    private OnModifyListener onModifyListener;
    private EditText etPhone;
    private EditText etVerify;
    private TextView tvVerify;
    private Handler handler;
    private static final int MAX_TIME = 60;
    private int lastSecond = MAX_TIME;
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            tvVerify.setText(getResources().getString(R.string.login_regetCode, lastSecond));
            if (lastSecond == 0) {
                tvVerify.setEnabled(true);
                tvVerify.setText(R.string.login_getCode);
                handler.removeCallbacks(run);
                lastSecond = MAX_TIME;
            } else {
                lastSecond--;
                handler.postDelayed(run, 1000L);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        handler = new Handler();
        View view = inflater.inflate(R.layout.dialog_phone_modify,container);
        etPhone = view.findViewById(R.id.et_modify_phone);
        etVerify = view.findViewById(R.id.et_modify_verify_input);
        tvVerify = view.findViewById(R.id.tv_get_modify_verify);
        tvVerify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etPhone.getText().toString())){
                    ToastUtils.toastShort(getString(R.string.phone_modify_toast));
                    return;
                }else{
                    getVerify();
                }
            }
        });
        TextView tvCommit = view.findViewById(R.id.tv_phone_commit);
        ImageView tvExit = view.findViewById(R.id.iv_phone_modify_cancel);
        tvExit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvCommit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etPhone.getText().toString())){
                    ToastUtils.toastShort(getString(R.string.phone_modify_toast));
                    return;
                }else if(TextUtils.isEmpty(etVerify.getText().toString())){
                    ToastUtils.toastShort(getString(R.string.phone_verify_toast));
                    return;
                }else{
                    checkVerify();
                }
            }
        });
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Content前设定

        dialog.setContentView(R.layout.dialog_exit);
        dialog.setCanceledOnTouchOutside(false); // 外部点击不消失

        return dialog;
    }

    private void getVerify(){
            HttpParams params = new HttpParams();
            params.put("phone", etPhone.getText().toString());
            HttpUtils.newRequester().post(HttpUrl.SEND_CODE, params, new YamanHttpCallback() {

                @Override
                public void onSuccess(String url, @Nullable Object o) {
                    super.onSuccess(url, o);
                    if (o == null) {
                        return;
                    }
                    HttpResponse response = (HttpResponse) o;
                    if (response.getResultCode() == 0) {
                        handler.post(run);
                        tvVerify.setEnabled(false);
                        ToastUtils.toastShort("验证码发送成功");
                    } else {
                        ToastUtils.toastShort(response.getResultMsg());
                    }
                }
            });

    }

    public void checkVerify(){
        HttpParams params = new HttpParams();
        params.put("phone", etPhone.getText().toString());
        params.put("captcha",etVerify.getText().toString());
        HttpUtils.newRequester(true).post(HttpUrl.REGIST_CHECK, params, new YamanHttpCallback() {
            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    modifyPhone();
                } else {
                    ToastUtils.toastShort(response.getResultMsg());
                }
            }
        });
    }

    private void modifyPhone(){
        HttpParams params = new HttpParams();
        params.put("phone", etPhone.getText().toString());
        params.put("captcha",etVerify.getText().toString());
        params.put("id", PreferenceUtils.getInstance().getUserEntity().getUser().getId());
        HttpUtils.newRequester().post(HttpUrl.MODIFY_PHONE, params, new YamanHttpCallback() {

            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    if(onModifyListener!=null){
                        onModifyListener.onFinish();
                    }
                } else {
                    ToastUtils.toastShort(response.getResultMsg());
                }
            }
        });

    }

    public void setOnModifyListener(OnModifyListener onModifyListener) {
        this.onModifyListener = onModifyListener;
    }

    public interface OnModifyListener{
        void onFinish();
    }
}
