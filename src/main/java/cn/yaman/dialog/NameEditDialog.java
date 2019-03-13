package cn.yaman.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.tcl.smart.beauty.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import cn.lamb.base.util.ToastUtils;

/**
 * 编辑昵称的弹出框
 * @author timpkins
 */
public class NameEditDialog extends DialogFragment {
    private TextView tvCancel,tvSave;
    private EditText etName;
    private OnNameSetListener onNameSetListener;
    private boolean mIsShowAnimation = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_name_edit, container);

        tvCancel = view.findViewById(R.id.tv_edit_cancel);
        tvSave = view.findViewById(R.id.tv_edit_save);
        etName = view.findViewById(R.id.et_name_edit);

        tvCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onNameSetListener!=null){
                    if(TextUtils.isEmpty(etName.getText().toString())){
                        ToastUtils.toastShort("输入信息不能为空");
                    }else {
                        onNameSetListener.onNameSet(etName.getText().toString());
                        dismiss();
                    }
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

        dialog.setContentView(R.layout.dialog_name_edit);
        dialog.setCanceledOnTouchOutside(true); // 外部点击取消

        Window window = dialog.getWindow();
        if (window != null) {
            if (mIsShowAnimation) {
                window.getAttributes().windowAnimations = R.style.DatePickerDialogAnim;
            }
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.BOTTOM; // 紧贴底部
            lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
            lp.dimAmount = 0.35f;
            window.setAttributes(lp);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        return dialog;
    }

    public void setOnNameSetListener(OnNameSetListener onNameSetListener) {
        this.onNameSetListener = onNameSetListener;
    }

    public interface OnNameSetListener{
        void onNameSet(String name);
    };
}
