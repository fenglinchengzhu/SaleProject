package com.goldwind.app.help.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldwind.app.help.BaseFragmentActivity;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.fragment.CalculatorEmptyFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 报错提示界面
 * Created by shihx on 2016/2/14.
 */
public class CalculatorEmptyActivity extends BaseFragmentActivity {


    @Bind(R.id.index_calculator_back_img)
    ImageView backImg;

    @Bind(R.id.index_calculator_switch_img)
    ImageView switch_img;

    @Bind(R.id.index_calculator_title_txt)
    TextView titleTxt;

    List<String> mList;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_template);
        ButterKnife.bind(this);
        initAll();
    }

    @Override
    protected void initParam() {

    }

    @Override
    protected void initData() {
        String calculatorType = getIntent().getExtras().getString(Constant.EXTRA.CALCULATOR_NAME);
        setTabSelection(Constant.TAB.CAL_ALL);
        mList = new ArrayList<String>();
        String[] reses = getResources().getStringArray(R.array.calculator_type_array);
        if (TextUtils.isEmpty(calculatorType)) {
            calculatorType = Constant.EXTRA.CALCULATOR_20;
        }
        handleTitle(reses, calculatorType);
        mList = Arrays.asList(reses);
    }

    private void handleTitle(String[] reses, String type) {
        String result = reses[0];
        if (type.equals(Constant.EXTRA.CALCULATOR_15)) {
            result = reses[2];
        } else if (type.equals(Constant.EXTRA.CALCULATOR_25)) {
            result = reses[1];
        } else if (type.equals(Constant.EXTRA.CALCULATOR_20)) {
            result = reses[0];
        } else if (type.equals(Constant.EXTRA.CALCULATOR_30)) {
            result = reses[3];
        } else if (type.equals(Constant.EXTRA.CALCULATOR_TMP)) {
            result = reses[4];
        }
        titleTxt.setText(result);
    }

    @Override
    protected void initViews() {
        switch_img.setVisibility(View.GONE);
    }

    @Override
    protected void initListener() {
        backImg.setOnClickListener(this);
        switch_img.setOnClickListener(this);
    }

    private void setTabSelection(int tag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.calculator_template_content, getFragment());
        ft.commit();
    }

    private Fragment getFragment() {
        Fragment fragment = new CalculatorEmptyFragment();
        return fragment;
    }

    @Override
    public void onClick(View v) {
        if (v == backImg) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
