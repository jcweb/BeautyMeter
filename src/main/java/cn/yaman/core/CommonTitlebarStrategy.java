package cn.yaman.core;

import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.TitlebarCommonLayoutBinding;

import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import cn.lamb.core.ITitlebarStrategy;
import cn.yaman.YamanApplication;

/**
 * 通用标题栏，最大可能性使用所有页面
 * @author timpkins
 */
public final class CommonTitlebarStrategy extends ITitlebarStrategy<TitlebarCommonLayoutBinding> {

    @Override
    public int getTitleLayoutRes() {
        return R.layout.titlebar_common_layout;
    }

    public void setElevation(@DimenRes int res) {
        getTitlebarBinding().getRoot().setElevation(YamanApplication.getInstance().getResources().getDimension(res));
        getTitlebarBinding().getRoot().setBackgroundColor(Color.WHITE);
    }

    public void setLeftIcon(@NonNull OnClickListener listener) {
        ImageView ivLeft = getTitlebarBinding().ivTitleLeft;
        ivLeft.setVisibility(View.VISIBLE);
        ivLeft.setImageResource(R.mipmap.title_back_gold);
        ivLeft.setOnClickListener(listener);
    }

    public void setLeftIcon(@DrawableRes int res, @NonNull OnClickListener listener) {
        ImageView ivLeft = getTitlebarBinding().ivTitleLeft;
        ivLeft.setVisibility(View.VISIBLE);
        ivLeft.setImageResource(res);
        ivLeft.setOnClickListener(listener);
    }

    public void setTitleName(@NonNull String titleName) {
        TextView tvTitle = getTitlebarBinding().tvTitleMiddle;
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(titleName);
    }

    public void setTitleName(@StringRes int res) {
        TextView tvTitle = getTitlebarBinding().tvTitleMiddle;
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(res);
    }

    public void setTitleRight(@NonNull String right, @NonNull OnClickListener listener) {
        TextView tvRight = getTitlebarBinding().tvTitleRight;
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText(right);
        tvRight.setOnClickListener(listener);
    }

    public void setTitleRight(@StringRes int res, @NonNull OnClickListener listener) {
        TextView tvRight = getTitlebarBinding().tvTitleRight;
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText(res);
        tvRight.setOnClickListener(listener);
    }

    public ImageView getTitleLeft() {
        return getTitlebarBinding().ivTitleLeft;
    }

    public TextView getTitleMiddle() {
        return getTitlebarBinding().tvTitleMiddle;
    }

    public TextView getTitleRight() {
        return getTitlebarBinding().tvTitleRight;
    }

    public void setBackgroundColor(int color) {
        getTitlebarBinding().getRoot().setBackgroundResource(
                R.mipmap.ic_close);
    }
}
