package com.lx.javawork.timemanager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class TimeManagerFragment2 extends Fragment {

    private LineChartView mLineChart;
    private TextView tvGrammer;
    private String today;
    String[] dateX = {"6-04", "6-05", "6-06", "6-07", "6-08", "6-09", "6-10"};//X轴的标注
    int[] studyTime = {1, 3, 5, 8, 2, 2, 0};//图表的数据点
    int[] month={31,31,28,31,30,31,30,31,31,30,31,30,31};//每个月的天数

    float[] dataY = {0, 50, 100, 200, 300, 400, 500};//Y轴的标注
    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_timemanager2_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvGrammer=view.findViewById(R.id.grammer);
        //设置x坐标的日期
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd");
        Date date = new Date(System.currentTimeMillis());
        today=simpleDateFormat.format(date);
        int M=Integer.parseInt(today.substring(0,2));
        int d=Integer.parseInt(today.substring(3));
        dateX[6]=M+"."+d;
        for(int i=5;i>=0;i--){
            String[] day;
            day=dateX[i+1].split("\\.");
            int intmonth=Integer.parseInt(day[0]);
            int intday=Integer.parseInt(day[1]);
            if(intday==1){
                if(intmonth==1){
                    intmonth=12;
                }else{
                    intmonth--;
                }
                intday=month[intmonth];
            }else{
                intday--;
            }
            dateX[i]=intmonth+"."+intday;
        }
        //设置Y坐标的值
        int timeSum=0;
        for(int i=0;i<7;i++){
            studyTime[i]=Integer.parseInt(new FileOperation().FileRead("records.txt",this.getContext(),dateX[i]));
            timeSum+=studyTime[i];
        }
        if(timeSum>=2100){
            tvGrammer.setText("近七天的学习时间长达\n"+timeSum/60+"小时"+timeSum%60+"分钟\n恭喜你完成平均每天坚持不看手机学习5小时的壮举！\n请继续保持！");
        }else if(timeSum>=1680){
            tvGrammer.setText("近七天的学习时间达到\n"+timeSum/60+"小时"+timeSum%60+"分钟\n你已经坚持了很久，值得表扬，但距离理想状态还有一定距离\n继续加油吧！");
        }else if(timeSum>=1260){
            tvGrammer.setText("近七天的学习时间达到\n"+timeSum/60+"小时"+timeSum%60+"分钟\n已经很不错了，相信自己，你还可以坚持更长的时间！");
        }else if(timeSum>=840){
            tvGrammer.setText("近七天的学习时间达到\n"+timeSum/60+"小时"+timeSum%60+"分钟\n表现还行吧，但是仍有很大的上升空间");
        }else if(timeSum>=420){
            tvGrammer.setText("近七天的学习时间达到\n"+timeSum/60+"小时"+timeSum%60+"分钟\n你平均每天能够坚持学习1个多小时，但是想要取得很大的进步，还需要进一步提高自制力");
        }else if(timeSum>0){
            tvGrammer.setText("近七天的学习时间达到\n"+timeSum/60+"小时"+timeSum%60+"分钟\n加油，每天多坚持一会儿，总有一天你也会成为学霸！");
        }else if(timeSum==0){
            tvGrammer.setText("你近七天没有学习时长的继续，快去学习吧！");
        }
        mLineChart = view.findViewById(R.id.lc);
        mLineChart.setInteractive(false);
        mLineChart.setZoomType(ZoomType.HORIZONTAL);
        mLineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        for (int i = 0; i < dateX.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(dateX[i]));
        }
        for (int i = 0; i < studyTime.length; i++) {
            mPointValues.add(new PointValue(i, studyTime[i]));
        }
        initLineChart();
    }

    private void initLineChart() {
        Line line = new Line(mPointValues).setColor(getResources().getColor(R.color.darkblue)); //折线的颜色
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.SQUARE）
        line.setCubic(true);//曲线是否平
        line.setFilled(true);//是否填充曲线的面积
        line.setFormatter(new SimpleLineChartValueFormatter(0));//设置显示小数点
		line.setHasLabels(true);
        line.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示 line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示
        line.setStrokeWidth(2);
        line.setPointRadius(5);
        lines.add(line);
        LineChartData data = new LineChartData();

        data.setValueLabelBackgroundColor(Color.TRANSPARENT);
        data.setValueLabelBackgroundAuto(true);
        data.setValueLabelBackgroundEnabled(true);
        data.setValueLabelTextSize(15);
        data.setValueLabelsTextColor(Color.WHITE); //此处设置坐标点旁边的文字颜色
        data.setLines(lines);


        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false); //X轴下面坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(getResources().getColor(R.color.darkblue));//蓝色


        axisX.setTextSize(12);//设置字体大小
        axisX.setMaxLabelChars(0); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisValues.length
        axisX.setValues(mAxisXValues); //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        axisX.setHasLines(false); //x 轴分割线



        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis(); //Y轴
        axisY.setName("");//y轴标注
        axisY.setTextSize(12);//设置字体大小
        axisY.setHasLines(false);
        axisY.setMaxLabelChars(6);//max label length, for example 60
        // 这样添加y轴坐标 就可以固定y轴的数据
        List<AxisValue> values = new ArrayList<>();
        for (int i = 0; i < dataY.length; i++) {
            AxisValue value = new AxisValue(dataY[i]);
            values.add(value);
        }
        axisY.setValues(values);
        axisY.setTextColor(getResources().getColor(R.color.darkblue));
        data.setAxisYLeft(axisY);//Y轴设置在左边

        //设置行为属性，支持缩放、滑动以及平移

        mLineChart.setInteractive(true);
        mLineChart.setZoomType(ZoomType.HORIZONTAL);
        mLineChart.setMaxZoom((float) 1);//最大放大比例
        mLineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        mLineChart.setLineChartData(data);
        mLineChart.setVisibility(View.VISIBLE);
        /**注：下面的7，10只是代表一个数字去类比而已
                  * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
                  */
        Viewport v = new Viewport(mLineChart.getMaximumViewport());
        v.left = 0;
        v.right = 7;
        v.bottom = 0;
        mLineChart.setCurrentViewport(v);
    }
}
