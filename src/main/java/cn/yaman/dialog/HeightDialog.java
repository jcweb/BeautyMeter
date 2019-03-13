package cn.yaman.dialog;

import android.app.Dialog;
import android.graphics.Color;
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
import cn.yaman.view.HeightPicker;

/**
 * @author timpkins
 */
public class HeightDialog extends DialogFragment {
    private OnHeightSelectedListener onHeightSelectedListener;
    private HeightPicker heightPicker;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_height,container);
        heightPicker = view.findViewById(R.id.hp_height_set);
        heightPicker.setCurtainColor(Color.WHITE);
        TextView tvCancel = view.findViewById(R.id.btn_dialog_height_cancel);
        TextView tvSave = view.findViewById(R.id.btn_dialog_height_decide);
        tvCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onHeightSelectedListener!=null){
                    onHeightSelectedListener.onSelected(heightPicker.getSelectedHeight());
                    dismiss();
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

        dialog.setContentView(R.layout.dialog_date);
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

    public void setOnHeightSelectedListener(OnHeightSelectedListener onHeightSelectedListener) {
        this.onHeightSelectedListener = onHeightSelectedListener;
    }

    public interface OnHeightSelectedListener{
        public void onSelected(int height);
    }
}
