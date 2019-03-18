package cn.yaman.utils;


import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

/**
 * 图表工具
 * Created by yangle on 2016/11/29.
 */
public class LineChartUtils {

    public static int dayValue = 0;
    public static int weekValue = 1;
    public static int monthValue = 2;
    static  int yi=-1;
    /**
     * 初始化图表
     *
     * @param chart 原始图表
     * @return 初始化后的图表
     */
    public static LineChart initChart(LineChart chart) {
        yi=-1;
        // 不显示数据描述
        chart.getDescription().setEnabled(false);
        // 没有数据的时候，显示“暂无数据”
        chart.setNoDataText("暂无数据");
        // 不显示表格颜色
        chart.setDrawGridBackground(false);
        // 不可以缩放
        chart.setScaleEnabled(false);
        // 不显示y轴右边的值
        chart.getAxisRight().setEnabled(false);
        // 不显示图例
        Legend legend = chart.getLegend();
        legend.setEnabled(false);
        // 向左偏移15dp，抵消y轴向右偏移的30dp
        chart.setExtraLeftOffset(-15);
        chart.setExtraBottomOffset(25);
        XAxis xAxis = chart.getXAxis();
        // 不显示x轴
//        xAxis.setDrawAxisLine(false);
        // 设置x轴数据的位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(12);
        xAxis.setYOffset(5);
//        xAxis.setAxisMinimum(0);
//        xAxis.setGridColor(Color.parseColor("#30FFFFFF"));
        // 设置x轴数据偏移量
//        xAxis.setYOffset(-5);
        YAxis yAxis = chart.getAxisLeft();
        // 不显示y轴
//        yAxis.setDrawAxisLine(false);
        // 设置y轴数据的位置
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        // 不从y轴发出横向直线
        yAxis.setDrawGridLines(false);
        yAxis.setTextColor(Color.WHITE);
        yAxis.setTextSize(12);
        // 设置y轴数据偏移量
        yAxis.setXOffset(15);
//        yAxis.setYOffset(0);
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(100.0f);
        yAxis.setDrawLabels(false);
        yAxis.setLabelCount(3,true);
        //Matrix matrix = new Matrix();
        // x轴缩放1.5倍
        //matrix.postScale(1.5f, 1f);
        // 在图表动画显示之前进行缩放
        //chart.getViewPortHandler().refresh(matrix, chart, false);
        // x轴执行动画
        //chart.animateX(2000);
        chart.invalidate();
        return chart;
    }

    /**
     * 设置图表数据
     *
     * @param chart  图表
     * @param values 数据
     */
    public static void setChartData(LineChart chart, List<Entry> values) {
        LineDataSet lineDataSet;
        XAxis xAxis = chart.getXAxis();
        xAxis.setLabelCount(values.size(),true);
        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            lineDataSet = (LineDataSet) chart.getData().getDataSetByIndex(0);
            lineDataSet.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            lineDataSet = new LineDataSet(values, "");
            // 设置曲线颜色
            lineDataSet.setColor(Color.parseColor("#FFFFFF"));
            // 设置平滑曲线
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            // 不显示坐标点的小圆点
            lineDataSet.setDrawCircles(false);
            // 不显示坐标点的数据
            lineDataSet.setDrawValues(false);
            // 不显示定位线
            lineDataSet.setHighlightEnabled(false);

            LineData data = new LineData(lineDataSet);
            chart.setData(data);
            chart.invalidate();
        }
    }

    /**
     * 更新图表
     *
     * @param chart     图表
     * @param values    数据
     * @param xValuesProcess X轴标签
     */
    public static void notifyDataSetChanged(LineChart chart, List<Entry> values, String[] xValuesProcess) {
        yi=-1;
        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xValuesProcess[(int) value];
            }
        });
        chart.invalidate();
        setChartData(chart, values);
    }

//    /**
//     * x轴数据处理
//     *
//     * @return x轴数据
//     */
//    private static String[] xValuesProcess() {
//        String[] week = {"1", "7", "13", "19", "25", "31"};
//        return week;
//    }

}