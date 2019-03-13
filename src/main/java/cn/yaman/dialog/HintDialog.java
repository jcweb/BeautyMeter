package cn.yaman.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tcl.smart.beauty.R;

import java.lang.ref.WeakReference;

import cn.yaman.activity.center.UserCenterActivity;

public class HintDialog {
    private OnCommitListener onCommitListener;
    private WeakReference<Context> weakContext;

    private HintDialog() {
    }

    ;

    public HintDialog(Context context, int imgId, String hintContent, String cancleContent, String commitContent, OnCommitListener listener) {
        onCommitListener = listener;
        weakContext = new WeakReference<>(context);
        LayoutInflater inflater = (LayoutInflater) weakContext.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_hint, null, false);
        Dialog dialog = new Dialog(weakContext.get());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        ImageView img = view.findViewById(R.id.hint_dialog_img);
        TextView contentTv = view.findViewById(R.id.hint_dialog_content);
        TextView cancleTv = view.findViewById(R.id.hint_dialog_cancle);
        TextView commitTv = view.findViewById(R.id.hint_dialog_commit);
        img.setBackground(weakContext.get().getDrawable(imgId));
        contentTv.setText(hintContent);
        cancleTv.setText(cancleContent);
        commitTv.setText(commitContent);
        cancleTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        commitTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (null != onCommitListener) {
                    onCommitListener.onClick();
                }
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.getAttributes().windowAnimations = R.style.DatePickerDialogAnim;
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.CENTER; // 紧贴底部
            lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
            lp.dimAmount = 0.35f;
            window.setAttributes(lp);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
    }

    public interface OnCommitListener {
        void onClick();
    }
}
