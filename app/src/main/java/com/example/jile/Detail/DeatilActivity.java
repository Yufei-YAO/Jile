package com.example.jile.Detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.jile.Bean.Account;
import com.example.jile.Bean.Bill;
import com.example.jile.Bean.FirstClass;
import com.example.jile.Bean.Mem;
import com.example.jile.Bean.SecondClass;
import com.example.jile.Bean.Store;
import com.example.jile.Constant.Constants;
import com.example.jile.New.NewBIllActivity;
import com.example.jile.R;
import com.example.jile.Util.BillMiddle;
import com.example.jile.Util.DateUtil;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.picker.widget.TimePickerView;
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder;
import com.xuexiang.xui.widget.picker.widget.configure.PickerOptions;
import com.xuexiang.xui.widget.picker.widget.listener.OnTimeSelectChangeListener;
import com.xuexiang.xui.widget.picker.widget.listener.OnTimeSelectListener;
import com.xuexiang.xui.widget.textview.autofit.AutoFitTextView;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 流水界面
 * 初始化时展示过去一周内的流水情况，即startDate为六天前，endDate为今天，searchType为Constant.SEARCH_TYPE_DAY
 * */
public class DeatilActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Button btnStartDateSelector,btnEndDateSelector,btnSearchTypeSelector,btnBack;
    private TextView tvBalance,tvIncome,tvCost;
    private Object searchType;
    private Date startDate,endDate;
    private List<String> firstClassItems = new LinkedList<>(Arrays.asList("时间","一级分类","二级分类","账户","成员","商家"));
    private List<List<String>> secondClassItems = new LinkedList<>(Arrays.asList(new LinkedList<>(Arrays.asList("日","周","月","年")),Arrays.asList("一级分类"),
                                                                    Arrays.asList("二级分类"),Arrays.asList("账户"),Arrays.asList("成员"),Arrays.asList("商家")));
    private List<List<Bill>> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deatil);
        recyclerView = findViewById(R.id.recycler_view);
        try {
            init();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DeatilActivity.this,"its a test",Toast.LENGTH_SHORT).show();
            }
        });
        btnBack.setOnClickListener((l)->finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            update();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void getComponent(){
        btnEndDateSelector = findViewById(R.id.btnDateEnd);
        btnStartDateSelector = findViewById(R.id.btnDateStart);
        btnSearchTypeSelector = findViewById(R.id.btnSearchType);
        btnBack = findViewById(R.id.btnBack);
        tvBalance = findViewById(R.id.tvBalance);
        tvIncome = findViewById(R.id.tvIncome);
        tvCost = findViewById(R.id.tvCost);
    }

    /**
     * 初始化activity
     * */
    private void init() throws ParseException {
        getComponent();
        btnSearchTypeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionsPickerView<String> optionsPickerView = new OptionsPickerBuilder(DeatilActivity.this, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3 ,View v) {
                        btnSearchTypeSelector.setText(secondClassItems.get(options1).get(option2));
                        if(options1==0){
                            searchType = secondClassItems.get(0).get(option2);
                        }else{
                            switch (options1){
                                case 1:
                                    searchType = new FirstClass();
                                    break;
                                case 2:
                                    searchType = new SecondClass();
                                    break;
                                case 3:
                                    searchType = new Account();
                                    break;
                                case 4:
                                    searchType = new Mem();
                                    break;
                                case 5:
                                    searchType = new Store();
                                    break;
                            }
                        }
                        try {
                            update();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }).setTitleText("分类选择")
                        .setDividerColor(Color.BLACK)
                        .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                        .setContentTextSize(20)
                        .setSelectOptions(0,0)
                        .build();
                optionsPickerView.setPicker(firstClassItems,secondClassItems);
                optionsPickerView.show();
            }
        });
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        endDate = calendar.getTime();
        btnEndDateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView mDatePicker = new TimePickerBuilder(DeatilActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelected(Date date, View v) {
                        endDate = date;
                        btnEndDateSelector.setText(Constants.DATE_FORMAT_YEAR_MONTH_DAY.format(date));
                        try {
                            update();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                })
                        .setType(true,true,true,false,false,false)
                        .setDate(calendar)
                        .setTitleText("结束日期选择")
                        .build();
                mDatePicker.show();
            }
        });
        calendar.add(Calendar.DATE,-6);
        startDate = calendar.getTime();
        btnStartDateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView mDatePicker = new TimePickerBuilder(DeatilActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelected(Date date, View v) {
                        startDate = date;
                        btnStartDateSelector.setText(Constants.DATE_FORMAT_YEAR_MONTH_DAY.format(date));
                        try {
                            update();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                })
                        .setType(true,true,true,false,false,false)
                        .setDate(calendar)
                        .setTitleText("起始日期选择")
                        .build();
                mDatePicker.show();
            }
        });
        searchType = Constants.SEARCH_TYPE_DAY;
        btnStartDateSelector.setText(Constants.DATE_FORMAT_YEAR_MONTH_DAY.format(calendar.getTime()));
        btnEndDateSelector.setText(Constants.DATE_FORMAT_YEAR_MONTH_DAY.format(new Date(System.currentTimeMillis())));
        btnSearchTypeSelector.setText(firstClassItems.get(0)+"-"+secondClassItems.get(0).get(0));
        update();
    }

    private void update() throws ParseException {
        if(searchType instanceof String){
            data = BillMiddle.getBill((String)searchType,startDate,endDate,this);
        }else if(searchType instanceof FirstClass){
            data = BillMiddle.getBill((FirstClass) searchType,startDate,endDate,this);
        }else if(searchType instanceof SecondClass){
            data = BillMiddle.getBill((SecondClass)searchType,startDate,endDate,this);
        }else if(searchType instanceof Account){
            data = BillMiddle.getBill((Account)searchType,startDate,endDate,this);
        }else if(searchType instanceof Mem){
            data = BillMiddle.getBill((Mem)searchType,startDate,endDate,this);
        }else{
            data = BillMiddle.getBill((Store)searchType,startDate,endDate,this);
        }
        BigDecimal cost,income;
        cost = getTotalCost(data);
        income = getTotalIncome(data);
        tvBalance.setText(income.add(cost).toString());
        tvIncome.setText(income.toString());
        tvCost.setText(cost.toString());
        List<LineElement> lle = new LinkedList<>();
        for(List<Bill> lb:data){
            List<View> lv = new LinkedList<>();
            for(Bill b:lb){
                lv.add(BillToViewAdapter(b));
            }
            LineElement le = new LineElement(getLineElementTitle(lb),
                    getLineElementBalance(lb).toPlainString(),getLineElementIncome(lb).toPlainString()
                    ,getLineElementCost(lb).toPlainString(),lv);
            lle.add(le);
        }
        WidgetUtils.initRecyclerView(recyclerView);
        recyclerView.setAdapter(new ExpandableListAdapter(recyclerView,lle));
    }


    /**
     * 将Bill转化成基于adapter_bill的View
     * @param bill 需要转化的bill
     * */
    private View BillToViewAdapter(Bill bill){
        LayoutInflater mInflater = LayoutInflater.from(this);
        View view = mInflater.inflate(R.layout.adapter_bill,null);
        if(bill==null){
            return null;
        }else{
            ImageView im = view.findViewById(R.id.ivIcon);
            TextView tvTime = view.findViewById(R.id.tvTime);
            TextView tvDay = view.findViewById(R.id.tvDay);
            TextView tvSecondClass = view.findViewById(R.id.tvSecondClass);
            TextView tvAccount = view.findViewById(R.id.tvAccount);
            AutoFitTextView tvMoney = view.findViewById(R.id.tvMoney);
            Button btnModify = view.findViewById(R.id.btnModify);
            if(!bill.getType().equals(Constants.TRANSFER)){
                im.setImageResource(bill.getIconId());

            }else{
                im.setImageResource(R.drawable.icon_transfer);
            }
            try {
                tvTime.setText(DateUtil.getShortDate(bill.getDate().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvDay.setText(DateUtil.getWeek(bill.getDate().toString()));
            if(!bill.getType().equals(Constants.TRANSFER)){
                tvSecondClass.setText(bill.getSecond());
            }else{
                tvSecondClass.setText("从 "+bill.getFirst()+" 转入 "+bill.getSecond());
            }
            tvMoney.setText(bill.getNum().toString());
            tvAccount.setText(bill.getAccountname());
            btnModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("uuid",bill.getUuid());
                    Intent intent = new Intent(DeatilActivity.this, NewBIllActivity.class).putExtras(bundle);
                    startActivity(intent);
                }
            });
            return view;
        }
    }

    /**
     * 统计符合要求的所有支出
     * @param llb 所有符合要求的bill
    * */
    private BigDecimal getTotalCost(List<List<Bill>> llb){
        BigDecimal cost = new BigDecimal("0");
        for(List<Bill> lb:llb){
            cost = cost.add(getLineElementCost(lb));
        }
        return cost;
    }

    /**
     * 统计符合要求的所有收入
     * @param llb 所有符合要求的bill
     * */
    private BigDecimal getTotalIncome(List<List<Bill>> llb){
        BigDecimal income = new BigDecimal("0");
        for(List<Bill> lb:llb){
            income = income.add(getLineElementIncome(lb));
        }
        return income;
    }

    /**
     * 统计单行收入
     * @param lb 所有符合要求的bill
     * */
    private BigDecimal getLineElementIncome(List<Bill> lb){
        BigDecimal income = new BigDecimal("0");
        for(Bill b:lb){
            if(b.getType().equals(Constants.INCOME)){
                income = income.add(b.getNum());
            }
        }
        return income;
    }

    /**
     * 统计单行支出
     * @param lb 所有符合要求的bill
     * */
    private BigDecimal getLineElementCost(List<Bill> lb){
        BigDecimal cost = new BigDecimal("0");
        for(Bill b:lb){
            if(b.getType().equals(Constants.COST)){
                cost = cost.add(b.getNum());
            }
        }
        return cost;
    }

    /**
     * 统计单行结余
     * @param lb 所有符合要求的bill
     * */
    private BigDecimal getLineElementBalance(List<Bill> lb){
        return getLineElementIncome(lb).add(getLineElementCost(lb));
    }

    /**
     * 获得单行title
     * @param lb 所有符合要求的bill
     * */
    private String getLineElementTitle(List<Bill> lb){
        return "it's a title";
    }
}
