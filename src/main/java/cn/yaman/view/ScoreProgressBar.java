package cn.yaman.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcl.smart.beauty.R;


/**
 * @className: ScoreProgressBar
 * @description: 进度圈，带动画
 */
public class ScoreProgressBar extends RelativeLayout {

    private final static String TAG = "ScoreProgressBar";
    private TextView tvProgress;
    private int progress;
    private ValueAnimator valueAnimator;
    private int centerX;   //view宽的中心点(可以暂时理解为圆心)
    private int centerY;   //view高的中心点(可以暂时理解为圆心)
    private int w;
    private int h;

    private float mRingWidth; //圆环的宽度
    private Paint mPaint;
    //    private int color[] = new int[3];   //渐变颜色
    private int bgRingColor;
    private int ringColor;

    private RectF mRectF; //圆环的矩形区域
    private int angle;
    private int angleEnd;
    private boolean isStop = true;

    public ScoreProgressBar(Context context) {
        super(context);
        initView(context);
    }

    public ScoreProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ScoreProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);
    }

    private void initView(Context context) {

        //抗锯齿画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //防止边缘锯齿
        mPaint.setAntiAlias(true);
        //需要重写onDraw就得调用此
        this.setWillNotDraw(false);
        bgRingColor = Color.parseColor("#55000000");
        ringColor = Color.parseColor("#ffff0000");
        mRingWidth = 40;
        //圆环渐变的颜色
//        color[0] = Color.parseColor("#FFD300");
//        color[1] = Color.parseColor("#FF0084");
//        color[2] = Color.parseColor("#16FF00");

        LayoutInflater lif = LayoutInflater.from(context);
        View mView = lif.inflate(R.layout.view_score_progressbar, null);
        tvProgress = mView.findViewById(R.id.tv_score_progress);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mView.setLayoutParams(params);

        addView(mView);

    }

    /**
     * @methodName: setCenterView
     * @description: 自定义中间的View
     * @author: Sergio Pan
     * @Time: 2018/8/6 11:13
     */
    public void setCenterView(View centerView, TextView tvCenter) {
        removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        centerView.setLayoutParams(params);
        addView(centerView);
        tvProgress = tvCenter;
    }


    /**
     * @methodName: setRingColor
     * @description: 设置圆环颜色
     * @author: Sergio Pan
     * @Time: 2018/8/6 11:46
     */
    public void setRingColor(int ringColor) {
        this.ringColor = ringColor;
    }

    /**
     * @methodName: setBgRingColor
     * @description: 设置背景圆环颜色
     * @author: Sergio Pan
     * @Time: 2018/8/6 11:46
     */
    public void setBgRingColor(int bgRingColor) {
        this.bgRingColor = bgRingColor;
    }

    /**
     * @methodName: setRingWidth
     * @description: 设置圆环宽度
     * @author: Sergio Pan
     * @Time: 2018/8/6 11:47
     */
    public void setRingWidth(int ringWidth) {
        this.mRingWidth = ringWidth;
        requestLayout();
    }

    /**
     * @methodName: setProgress
     * @description: 设置进度
     * @author: Sergio Pan
     * @Time: 2018/8/6 11:47
     */
    public void setProgress(int progress) {
        if (progress < 0 || progress > 100) {
            tvProgress.setText(getContext().getString(R.string.no_data));
            return;
        }
        this.progress = progress;
        if (tvProgress != null) {
            tvProgress.setText(progress + "");
        }
        angleEnd = progress * 360 / 100;
//        ULog.i(TAG, "-----setProgress-----angleEnd = " + angleEnd);
        isStop = false;

        new Thread(run).start();

    }

    public void stop() {
        isStop = true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //view的宽和高,相对于父布局(用于确定圆心)
        w = getMeasuredWidth();
        h = getMeasuredHeight();
        centerX = w / 2;
        centerY = h / 2;
        //画矩形
        mRectF = new RectF(mRingWidth / 2, mRingWidth / 2, w - mRingWidth / 2, h - mRingWidth / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画默认圆环
        drawNormalRing(canvas);
        //画彩色圆环
        drawColorRing(canvas);

    }

    /**
     * 画默认圆环
     * @param canvas
     */
    private void drawNormalRing(Canvas canvas) {
        Paint ringNormalPaint = new Paint(mPaint);
        ringNormalPaint.setStyle(Paint.Style.STROKE);
        ringNormalPaint.setStrokeWidth(mRingWidth);
        ringNormalPaint.setColor(bgRingColor);//圆环默认颜色为灰色
        canvas.drawArc(mRectF, 0, 360, false, ringNormalPaint);
    }

    /**
     * 画彩色圆环
     * @param canvas
     */
    private void drawColorRing(Canvas canvas) {
        Paint ringColorPaint = new Paint(mPaint);
        ringColorPaint.setStyle(Paint.Style.STROKE);
        ringColorPaint.setStrokeWidth(mRingWidth);
        ringColorPaint.setColor(ringColor);
//        ringColorPaint.setShader(new SweepGradient(centerX, centerY, color, null));
        //逆时针旋转90度
//        canvas.rotate(-90, w, h);
        canvas.drawArc(mRectF, -90, angle, false, ringColorPaint);
//        ringColorPaint.setShader(null);
    }


    private Runnable run = new Runnable() {
        @Override
        public void run() {

            while (!isStop) {
                angle++;
//                ULog.d(TAG, "-----run-----angle = " + angle + ", angleEnd = " + angleEnd);
                if (angle > angleEnd) {
                    isStop = true;
                    return;
                }
                int delay = 15;
                if (angle > angleEnd / 2) {
                    delay = 20;
                } else if (angle > angleEnd * 2 / 3) {
                    delay = 25;
                } else if (angle > angleEnd * 3 / 4) {
                    delay = 35;
                }
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                postInvalidate();
            }

        }
    };

}
