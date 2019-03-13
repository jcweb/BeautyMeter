package cn.yaman.view;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.NurseProcessTitleLayoutBinding;

import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import cn.lamb.core.ITitlebarStrategy;
import cn.yaman.YamanApplication;

public class NurseProcessTitle extends ITitlebarStrategy<NurseProcessTitleLayoutBinding> {
    @Override
    public int getTitleLayoutRes() {
        return R.layout.nurse_process_title_layout;
    }

    public void setElevation(@DimenRes int res) {
        getTitlebarBinding().getRoot().setElevation(YamanApplication.getInstance().getResources().getDimension(res));
        getTitlebarBinding().getRoot().setBackgroundColor(Color.WHITE);
    }

    public void setLeftIcon(@NonNull View.OnClickListener listener) {
        ImageView ivLeft = getTitlebarBinding().nurseProcessTitleLeft;
        ivLeft.setVisibility(View.VISIBLE);
        ivLeft.setImageResource(R.mipmap.title_back_gold);
        ivLeft.setOnClickListener(listener);
    }

    public void setLeftIcon(@DrawableRes int res, @NonNull View.OnClickListener listener) {
        ImageView ivLeft = getTitlebarBinding().nurseProcessTitleLeft;
        ivLeft.setVisibility(View.VISIBLE);
        ivLeft.setImageResource(res);
        ivLeft.setOnClickListener(listener);
    }

    public ImageView getTitleLeft() {
        return getTitlebarBinding().nurseProcessTitleLeft;
    }

    public PageIndicatorView getPageIndicatorView() {
        return getTitlebarBinding().nurseProcessTitleIndicator;
    }

    public void setBackgroundColor(int color) {
        getTitlebarBinding().getRoot().setBackgroundResource(
                R.mipmap.ic_close);
    }
}
