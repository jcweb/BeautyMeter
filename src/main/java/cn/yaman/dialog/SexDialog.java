package cn.yaman.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.tcl.smart.beauty.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * @author timpkins
 */
public class SexDialog extends DialogFragment {
    private int sexType;//0表示女，1表示男
    private TextView tvMale,tvFemale,tvCommit;
    private OnSexSelectedListener listener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_sex, container);
        tvMale = view.findViewById(R.id.tv_sex_male);
        tvFemale = view.findViewById(R.id.tv_sex_female);
        tvCommit = view.findViewById(R.id.tv_sex_cancel);

        tvCommit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvFemale.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onSelected(0);
                    dismiss();
                }
            }
        });

        tvMale.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onSelected(1);
                    dismiss();
                }
            }
        });
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.DatePickerBottomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Content前设定

        dialog.setContentView(R.layout.dialog_sex);
        dialog.setCanceledOnTouchOutside(true); // 外部点击取消

        Window window = dialog.getWindow();
        if (window != null) {
            window.getAttributes().windowAnimations = R.style.DatePickerDialogAnim;
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.BOTTOM; // 紧贴底部
            lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
            lp.dimAmount = 0.35f;
            window.setAttributes(lp);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        return dialog;
    }

    public void setListener(OnSexSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnSexSelectedListener{
        void onSelected(int type);
    }
}
