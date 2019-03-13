package cn.yaman.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.tcl.smart.beauty.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * 身高选择器
 */
@SuppressWarnings("unused")
public class HeightPicker extends WheelPicker<Integer> {

    private int mStartHeight, mEndHeight;
    private int mSelectedHeight;
    private OnHeightSelectedListener mOnHeightSelectedListener;

    public HeightPicker(Context context) {
        this(context, null);
    }

    public HeightPicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeightPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        setItemMaximumWidthText("0000");
        updateHeight();
        setSelectedHeight(mSelectedHeight, false);
        setOnWheelChangeListener(new OnWheelChangeListener<Integer>() {
            @Override
            public void onWheelSelected(Integer item, int position) {
            	mSelectedHeight = item;
                if (mOnHeightSelectedListener != null) {
                    mOnHeightSelectedListener.onHeightSelected(item);
                }
            }
        });
    }

    private void initAttrs(Context context, @Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        mSelectedHeight = 165;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HeightPicker);
        mStartHeight = a.getInteger(R.styleable.HeightPicker_startHeight, 50);
        mEndHeight = a.getInteger(R.styleable.HeightPicker_endHeight, 300);
        a.recycle();

    }

    private void updateHeight() {
        List<Integer> list = new ArrayList<>();
        for (int i = mStartHeight; i <= mEndHeight; i++) {
            list.add(i);
        }
        setDataList(list);
    }

    public void setStartHeight(int startHeight) {
        mStartHeight = startHeight;
        updateHeight();
        if (mStartHeight > mSelectedHeight) {
            setSelectedHeight(mStartHeight, false);
        } else {
            setSelectedHeight(mSelectedHeight, false);
        }
    }

    public void setEndHeight(int endHeight) {
        mEndHeight = endHeight;
        updateHeight();
        if (mSelectedHeight > endHeight) {
            setSelectedHeight(mEndHeight, false);
        } else {
            setSelectedHeight(mSelectedHeight, false);
        }
    }

    public void setHeight(int startHeight, int endHeight) {
        setStartHeight(startHeight);
        setEndHeight(endHeight);
    }

    public void setSelectedHeight(int selectedHeight) {
        setSelectedHeight(selectedHeight, true);
    }

    public void setSelectedHeight(int selectedHeight, boolean smoothScroll) {
        setCurrentPosition(selectedHeight - mStartHeight, smoothScroll);
    }

    public int getSelectedHeight() {
    	return mSelectedHeight;
    }



    public interface OnHeightSelectedListener {
        void onHeightSelected(int height);
    }

}
