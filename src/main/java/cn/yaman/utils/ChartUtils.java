package cn.yaman.utils;

import android.graphics.Color;
import android.graphics.Matrix;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.tcl.smart.beauty.R;

import java.util.List;

/**
 * @author timpkins
 */
public class ChartUtils {
    public static int monthValue = 2;

    /**
     * 初始化图表
     * @param chart 原始图表
     * @return 初始化后的图表
     */
    public static LineChart initChart(LineChart chart) {
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

        XAxis xAxis = chart.getXAxis();
        // 不显示x轴
        xAxis.setDrawAxisLine(false);
        // 设置x轴数据的位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(R.color.color_black_666);
        xAxis.setTextSize(14);
        xAxis.setGridColor(R.color.chart_text);
        // 设置x轴数据偏移量
        xAxis.setAxisLineColor(R.color.chart_text);
        xAxis.setYOffset(-12);
        YAxis yAxis = chart.getAxisLeft();
        // 不显示y轴
        yAxis.setDrawAxisLine(false);
        xAxis.setLabelCount(6, false);
        // 设置y轴数据的位置
//        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        // 不从y轴发出横向直线
        yAxis.setDrawGridLines(false);
        yAxis.setTextColor(R.color.color_black_666);
        yAxis.setTextSize(14);
        // 设置y轴数据偏移量
        yAxis.setXOffset(20);
        yAxis.setYOffset(-3);
        yAxis.setAxisMinimum(0);
        yAxis.setGridColor(R.color.chart_text);
        yAxis.setDrawZeroLine(true);

        chart.setScaleMinima(0.9f, 0.9f);
        chart.getViewPortHandler().refresh(new Matrix(), chart, true);
        chart.invalidate();
        return chart;
    }

    /**
     * 设置图表数据
     * @param chart 图表
     * @param values 数据
     */
    public static void setChartData(LineChart chart, List<Entry> values) {
        LineDataSet lineDataSet;

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
     * @param chart 图表
     * @param values 数据
     */
    public static void notifyDataSetChanged(LineChart chart, List<Entry> values,
                                            String[] xAxis) {
        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xAxis[(int) value % xAxis.length];
            }
        });

        chart.invalidate();
        setChartData(chart, values);

        chart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return ((int) (value)) + "分";
            }
        });
    }

    /**
     * x轴数据处理
     * @param valueType 数据类型
     * @return x轴数据
     */
    private static String[] xValuesProcess(int valueType) {
        if (valueType == monthValue) { // 本月
            String[] monthValues = new String[7];
            long currentTime = System.currentTimeMillis();
            for (int i = 6; i >= 0; i--) {
                currentTime -= (4 * 24 * 60 * 60 * 1000);
            }
            return monthValues;
        }
        return new String[]{};
    }
}
