package com.example.jile.Graph;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.jile.Constant.Constants;
import com.example.jile.Detail.DeatilActivity;
import com.example.jile.MainView.MainActivity;
import com.example.jile.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.utils.ColorUtils;
import com.xuexiang.xui.widget.alpha.XUIAlphaTextView;
import com.xuexiang.xui.widget.layout.ExpandableLayout;
import com.xuexiang.xui.widget.picker.widget.TimePickerView;
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder;
import com.xuexiang.xui.widget.picker.widget.listener.OnTimeSelectListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.List;

import static com.example.jile.Constant.Constants.COST;
import static com.example.jile.Constant.Constants.INCOME;

public class GraphPieActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    private PieChart mPieChart;
    private List<PieEntry> mPieData ;

    private String searchType ,billtype;

    private ExpandableLayout expandableLayout1;
    private XUIAlphaTextView expand_button;
    private Date startDate,endDate;

    private GraphPieActivity.OnClick onClick = new GraphPieActivity.OnClick();

    private Button btnback,btnCostGraphByKind,btnCostGraphByAccount,btnIncomeGraphByKind,
            btnIncomeGraphByAccount,btnMonthIncome,btnMonthCost,btnBillDetail,
            btnSetStartDate,btnSetEndDate,btnSetBarChart,btnSetPieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphpie);
        getComponents();
        initChartStyle();
        expandableLayout1.setOnExpansionChangedListener((expansion, state) -> Log.d("expandableLayout1", "State: " + state));
        setListener(onClick);
        btnBillDetail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                update();
                return true;
            }
        });
        initPieChart(mPieChart);
    }


    private void getComponents() {
        mPieChart = findViewById(R.id.pie_chart);
        btnback = findViewById(R.id.btnBack);
        expandableLayout1 = findViewById(R.id.expandable_layout_1);
        expand_button = findViewById(R.id.expand_button);
        btnCostGraphByKind=findViewById(R.id.btnCostGraphByKind);
        btnCostGraphByAccount=findViewById(R.id.btnCostGraphByAccount);
        btnIncomeGraphByKind=findViewById(R.id.btnIncomeGraphByKind);
        btnIncomeGraphByAccount=findViewById(R.id.btnIncomeGraphByAccount);
        btnMonthIncome=findViewById(R.id.btnMonthIncome);
        btnMonthCost=findViewById(R.id.btnMonthCost);
        btnSetStartDate=findViewById(R.id.btnSetStartDate);
        btnSetEndDate=findViewById(R.id.btnSetEndDate);
        btnSetBarChart=findViewById(R.id.btnSetBarChart);
        btnSetPieChart=findViewById(R.id.btnSetPieChart);
        btnBillDetail=findViewById(R.id.btnBillDetail);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        PieEntry pe = (PieEntry)e;
        searchType = pe.getLabel();
        btnBillDetail.setText(pe.getLabel()+" "+pe.getValue()+" >");
    }

    @Override
    public void onNothingSelected() {
        btnBillDetail.setText("");
    }

    private class OnClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(System.currentTimeMillis()));
            switch (v.getId()){
                case R.id.btnBack:
                    finish();
                    break;
                case R.id.expand_button:
                    if (expandableLayout1.isExpanded()) {
                        expandableLayout1.collapse();
                    } else {
                        expandableLayout1.expand();
                    }
                    break;
                case R.id.btnCostGraphByKind:
                    searchType = "FirstClass";
                    billtype = COST;
                    update();
                    break;
                case R.id.btnCostGraphByAccount:
                    searchType = "Account";
                    billtype = INCOME;
                    update();
                    break;
                case R.id.btnIncomeGraphByKind:
                    searchType = "FirstClass";
                    billtype = INCOME;
                    update();
                    break;
                case R.id.btnMonthIncome:
                    searchType = "month";
                    billtype = INCOME;
                    update();
                    break;
                case R.id.btnMonthCost:
                    searchType = "month";
                    billtype = COST;
                    update();
                    break;
                case R.id.btnBillDetail:
                    intent = new Intent(GraphPieActivity.this, DeatilActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btnSetEndDate:
                    TimePickerView mDateEndPicker = new TimePickerBuilder(GraphPieActivity.this, new OnTimeSelectListener() {
                        @Override
                        public void onTimeSelected(Date date, View v) {
                            endDate = date;
                            btnSetEndDate.setText(Constants.DATE_FORMAT_YEAR_MONTH_DAY.format(date));
                            update();
                        }
                    })
                            .setType(true,true,true,false,false,false)
                            .setDate(calendar)
                            .setTitleText("结束日期选择")
                            .build();
                    mDateEndPicker.show();
                    break;
                case R.id.btnSetStartDate:
                    TimePickerView mDateStartPicker = new TimePickerBuilder(GraphPieActivity.this, new OnTimeSelectListener() {
                        @Override
                        public void onTimeSelected(Date date, View v) {
                            endDate = date;
                            btnSetStartDate.setText(Constants.DATE_FORMAT_YEAR_MONTH_DAY.format(date));
                            update();
                        }
                    })
                            .setType(true,true,true,false,false,false)
                            .setDate(calendar)
                            .setTitleText("结束日期选择")
                            .build();
                    mDateStartPicker.show();
                    break;
                case R.id.btnSetBarChart:
                    intent = new Intent(GraphPieActivity.this, GraphBarActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

    private void update() {
        mPieData=getpiebill(searchType,billtype,startDate,endDate);
        mPieData.add(new PieEntry(30,"test"));
        mPieChart.setCenterText(new SpannableString("总计\n"+getTotal(mPieData).toString()));
        initPieChart(mPieChart);
    }

    private void initPieChart(PieChart pieChart){
        PieDataSet dataSet = new PieDataSet(mPieData,"");
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int i=0;i<=mPieData.size();i++){
            colors.add(ColorUtils.getRandomColor());
        }
        dataSet.setColors(colors);

        PieData pieData = new PieData(dataSet);
        pieData.setDrawValues(true);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.WHITE);

        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void setListener(GraphPieActivity.OnClick onClick){
        btnback.setOnClickListener(onClick);
        expand_button.setOnClickListener(onClick);
        btnCostGraphByKind.setOnClickListener(onClick);
        btnCostGraphByAccount.setOnClickListener(onClick);
        btnIncomeGraphByKind.setOnClickListener(onClick);
        btnIncomeGraphByAccount.setOnClickListener(onClick);
        btnMonthIncome.setOnClickListener(onClick);
        btnMonthCost.setOnClickListener(onClick);
        btnSetStartDate.setOnClickListener(onClick);
        btnSetEndDate.setOnClickListener(onClick);
        btnSetBarChart.setOnClickListener(onClick);
        btnSetPieChart.setOnClickListener(onClick);
    }



    protected void initChartStyle() {
        //使用百分百显示
        mPieChart.setUsePercentValues(true);
        mPieChart.getDescription().setEnabled(false);
        mPieChart.setExtraOffsets(5, 10, 5, 5);

        //设置拖拽的阻尼，0为立即停止
        mPieChart.setDragDecelerationFrictionCoef(0.95f);

        //设置初始值
        searchType="FirstClass";
        billtype=COST;
        Calendar calendar = Calendar.getInstance();
        endDate = new Date(System.currentTimeMillis());
        calendar.setTime(endDate);
        calendar.add(Calendar.MONTH,-1);
        startDate=calendar.getTime();
        mPieData=getpiebill(searchType,billtype,startDate,endDate);

        //设置图标中心文字
        mPieChart.setCenterText(new SpannableString("总计/n"+getTotal(mPieData).toString()));
        mPieChart.setDrawCenterText(true);
        //设置图标中心空白，空心
        mPieChart.setDrawHoleEnabled(true);
        //设置空心圆的弧度百分比，最大100
        mPieChart.setHoleRadius(50f);
        mPieChart.setHoleColor(Color.WHITE);
        //设置透明弧的样式
        mPieChart.setTransparentCircleColor(Color.WHITE);
        mPieChart.setTransparentCircleAlpha(110);
        mPieChart.setTransparentCircleRadius(61f);

        //设置可以旋转
        mPieChart.setRotationAngle(0);
        mPieChart.setRotationEnabled(true);
        mPieChart.setHighlightPerTapEnabled(true);
        mPieChart.setOnChartValueSelectedListener(this);
    }
    /**
     * 搜索时间区间内符合搜索要求的Bill并根据分类返回一个List
     * new PieEntry(30f,"一月")
     * 第一个为float的金额，第二个为string  "类型"
     * 排序按百分比从大到小
     * @param searchType 搜索类型，包括SEARCHTYPE_MONTH = "month";
     *                                FirstClass,
     *                                SecondClass,
     *                                Account
     * @param startDate Date类型的时间，包括这一天
     * @param endDate Date类型的时间，包括这一天
     * */

    private List<PieEntry> getpiebill(String searchType, String billtype, Date startDate, Date endDate){
        List<PieEntry> strings = new ArrayList<>();
        strings.add(new PieEntry(1000f,"男生"));
        strings.add(new PieEntry(70f,"女生"));
        return strings;
    }
    /**
     * 统计符合要求的所有或收入
     * */
    private Float getTotal(List<PieEntry> bill){
        float sum=0;
        List<PieEntry> strings = new ArrayList<>();
        strings=bill;
        if(strings.isEmpty()){
            for(PieEntry i:bill){
                sum+=i.getValue();
            }
        }
        return sum;
    }

}