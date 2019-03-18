package cn.yaman.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.FragmentMonthBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.lamb.fragment.BaseFragment;
import cn.yaman.entity.RecordMonthEntity;
import cn.yaman.utils.LineChartUtils;
import cn.yaman.utils.StarUtils;


public class MonthFragment extends BaseFragment<FragmentMonthBinding> {
    private String[] xAxis;
    DecimalFormat df = new DecimalFormat("0.0");

    @Override
    public int bindContentView() {
        return R.layout.fragment_month;
    }

    @Override
    public void onProcessor() {
//        initMain(new RecordMonthEntity());
    }

    public void initMain(RecordMonthEntity entity) {
//        // 更新图表
//        String[] xAxis = {"1", "7", "12", "18", "23", "29"};
        String[] yAxis = {"0分", "60分", "100分"};

        LineChartUtils.initChart(getBinding().lcMonth);
        List<Entry> list = setData(entity.getXyOutVoList());
        LineChartUtils.notifyDataSetChanged(getBinding().lcMonth, list, xAxis);
        getBinding().tvMonthDate.setText(df.format(entity.getTotalTime() / 3600f) + "小时   ");
        getBinding().tvMonthDuration.setText(df.format(entity.getAveTime() / 60.0f) + "分钟");
        getBinding().tvMonthScheme.setText(entity.getScheme());
        getBinding().tvMonthProgress.setText(entity.getAveCompleteness() + "%");
        showSore(true);
        setStar(entity.getEffact());
    }

    public void showSore(boolean flag) {
        if (flag) {
            getBinding().lcYlableMax.setVisibility(View.VISIBLE);
            getBinding().lcYlableMid.setVisibility(View.VISIBLE);
            getBinding().lcYlableMin.setVisibility(View.VISIBLE);
        } else {
            getBinding().lcYlableMax.setVisibility(View.INVISIBLE);
            getBinding().lcYlableMid.setVisibility(View.INVISIBLE);
            getBinding().lcYlableMin.setVisibility(View.INVISIBLE);
        }
    }

    private List<Entry> setData(List<RecordMonthEntity.XyOutVoList> list) {
        List<Entry> values = new ArrayList<>();
        xAxis = new String[list.size()];
        Collections.sort(list, new Comparator<RecordMonthEntity.XyOutVoList>() {

            @Override
            public int compare(RecordMonthEntity.XyOutVoList o1, RecordMonthEntity.XyOutVoList o2) {
                int i = Integer.valueOf(o1.getDay()) - Integer.valueOf(o2.getDay());
                return i;
            }
        });
        for (int i = 0; i < list.size(); i++) {
            xAxis[i] = list.get(i).getDay();
            values.add(new Entry(Float.valueOf(String.valueOf(i)), list.get(i).getAveTime() / 60));
        }

//        values.add(new Entry(1.0f, 20));
//        values.add(new Entry(2.0f, 30));
//        values.add(new Entry(3.0f, 50));
//        values.add(new Entry(4.0f, 60));
//        values.add(new Entry(5.0f, 100));

        return values;
    }

    private void setStar(int progress) {
        int num = StarUtils.getStarNum(progress);
        for (int i = 0; i < num; i++) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.star_item, null);
            getBinding().llContainerStar.addView(view);
        }
    }

    private void init() {
        ArrayList<Entry> yEntries = new ArrayList<>();
        yEntries.add(new Entry(10.0f, 0));
        yEntries.add(new Entry(13.0f, 1));
        yEntries.add(new Entry(18.0f, 2));
        yEntries.add(new Entry(8.0f, 3));
        yEntries.add(new Entry(12.0f, 4));
        LineDataSet lineDataSet = new LineDataSet(yEntries, "Y label");
        String[] xData = new String[]{"13:00", "14:00", "15:00", "16:00", "17:00"};
        LineData lineData = new LineData(lineDataSet);
        // 设置坐标轴
        setAxis();
        getBinding().lcMonth.setData(lineData);
    }

    public void setAxis() {
        Description description = new Description();
        description.setText("");
        getBinding().lcMonth.setDescription(description);
        getBinding().lcMonth.setDragEnabled(true);
        getBinding().lcMonth.setScaleEnabled(false);
        getBinding().lcMonth.setDoubleTapToZoomEnabled(false);

        // 把X轴放到下面
        XAxis xAxis = getBinding().lcMonth.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);//设置x轴上每个点对应的线
        xAxis.setDrawLabels(true);//绘制标签  指x轴上的对应数值
        setYAxis();
    }

    /**
     * 设置Y轴
     */
    private void setYAxis() {

        YAxis yAxis = getBinding().lcMonth.getAxisLeft();
        yAxis.setAxisMinimum(0);//纵坐标从0开始
        yAxis.setDrawZeroLine(false);
        yAxis.setDrawLabels(true);//设置左y轴上每个点对应的线
        yAxis.setDrawGridLines(false);
        yAxis.setDrawAxisLine(false);
// 不需要右面显示Y轴
        YAxis rightYaxis = getBinding().lcMonth.getAxisRight();
        rightYaxis.setEnabled(false);
        rightYaxis.setDrawZeroLine(false);
        rightYaxis.setDrawLabels(false);//设置右y轴上每个点对应的线
        rightYaxis.setDrawGridLines(false);
        rightYaxis.setDrawAxisLine(false);

    }
}