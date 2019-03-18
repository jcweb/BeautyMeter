package cn.yaman.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tcl.smart.beauty.R;

import java.util.ArrayList;
import java.util.List;

import cn.yaman.utils.ScreenUtils;


/**
 * Created by shichaohui on 2015/7/10 0010.
 * <p/>
 * 页码指示器类，获得此类实例后，可通过{@link PageIndicatorView#initIndicator(int)}方法初始化指示器
 * </P>
 */
public class PageIndicatorView extends LinearLayout {

    private Context mContext = null;
    private int dotSize = 30; // 指示器的大小（dp）
    private int margins = 4; // 指示器间距（dp）
    private List<ImageView> indicatorViews = null; // 存放指示器

    public PageIndicatorView(Context context) {
        this(context, null);
    }

    public PageIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;

        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);

        dotSize = ScreenUtils.dip2px(context, dotSize);
        margins = ScreenUtils.dip2px(context, margins);
    }

    /**
     * 初始化指示器，默认选中第一页
     *
     * @param count 指示器数量，即页数
     */
    public void initIndicator(int count) {

        if (indicatorViews == null) {
            indicatorViews = new ArrayList<>();
        } else {
            indicatorViews.clear();
            removeAllViews();
        }
        ImageView view;
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        params.setMargins(margins, margins, margins, margins);
        params.rightMargin = margins;
        params.gravity = Gravity.CENTER;
        for (int i = 0; i < count; i++) {
            view = new ImageView(mContext);
            view.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.point_unselect, null));
            view.setLayoutParams(params);
            addView(view);
            indicatorViews.add(view);
        }
        if (indicatorViews.size() > 0) {
            indicatorViews.get(0).setImageDrawable(mContext.getResources().getDrawable(R.mipmap.point_select, null));
            ;
        }
    }

    /**
     * 设置选中页
     *
     * @param selected 页下标，从0开始
     */
    public void setSelectedPage(int selected) {
        for (int i = 0; i < indicatorViews.size(); i++) {
            if (i == selected) {
                indicatorViews.get(i).setImageDrawable(mContext.getResources().getDrawable(R.mipmap.point_select, null));
            } else {
                indicatorViews.get(i).setImageDrawable(mContext.getResources().getDrawable(R.mipmap.point_unselect, null));
            }
        }
    }

}