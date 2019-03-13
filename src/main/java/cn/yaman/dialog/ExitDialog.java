package cn.yaman.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.tcl.smart.beauty.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * @author timpkins
 */
public class ExitDialog extends DialogFragment {
    private OnSelectedConfirm onSelectedConfirm;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_exit,container);
        TextView tvCancel = view.findViewById(R.id.tv_exit_cancel);
        tvCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        TextView tvConfirm = view.findViewById(R.id.tv_exit_confirm);
        tvConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onSelectedConfirm!=null){
                    onSelectedConfirm.OnConfirm();
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

        dialog.setContentView(R.layout.dialog_exit);
        dialog.setCanceledOnTouchOutside(false); // 外部点击不消失

        return dialog;
    }

    public void setOnSelectedConfirm(OnSelectedConfirm onSelectedConfirm) {
        this.onSelectedConfirm = onSelectedConfirm;
    }

    public interface OnSelectedConfirm{
        public void OnConfirm();
    }
}
