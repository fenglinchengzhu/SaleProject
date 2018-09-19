package com.goldwind.app.help.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldwind.app.help.BaseFragmentActivity;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.fragment.ComplexCalculatorOutFragment;
import com.goldwind.app.help.fragment.SimpleCalculatorOutFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 具体的计算器输出量
 * Created by shihx on 2016/2/14.
 */
public class HistoryOutPutActivity extends BaseFragmentActivity {

    boolean isALL = true;

    @Bind(R.id.index_calculator_back_img)
    ImageView backImg;

    @Bind(R.id.index_calculator_switch_img)
    ImageView switch_img;
    @Bind(R.id.index_calculator_title_txt)
    TextView titleTxt;

    SimpleCalculatorOutFragment mTemplateAllFragment;
    ComplexCalculatorOutFragment mTemplatePartFragment;


    String mKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_template);
        ButterKnife.bind(this);
        try {
            mKey = getIntent().getStringExtra("key");
        } catch (Exception e) {
            e.printStackTrace();
        }
        initAll();

    }

    @Override
    protected void initParam() {

    }

    @Override
    protected void initViews() {
        titleTxt.setText(getString(R.string.str_main_calculator));
    }

    @Override
    protected void initListener() {
        backImg.setOnClickListener(this);
        switch_img.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        setTabSelection(Constant.TAB.CAL_ALL);
    }

    private void setTabSelection(int tag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (tag) {
            case Constant.TAB.CAL_ALL:
                isALL = true;
                break;
            case Constant.TAB.CAL_PART:
                isALL = false;
                break;
        }
        ft.replace(R.id.calculator_template_content, getFragment(tag));
        ft.commit();
    }

    private Fragment getFragment(int tag) {
        Fragment fragment = null;
        switch (tag) {
            case Constant.TAB.CAL_ALL:
                if (mTemplateAllFragment == null)
                    mTemplateAllFragment = new SimpleCalculatorOutFragment();
                mTemplateAllFragment.setKey(mKey);
                fragment = mTemplateAllFragment;
                break;
            case Constant.TAB.CAL_PART:
                if (mTemplatePartFragment == null)
                    mTemplatePartFragment = new ComplexCalculatorOutFragment();
                mTemplatePartFragment.setKey(mKey);
                fragment = mTemplatePartFragment;
                break;
        }
        return fragment;
    }

    @Override
    public void onClick(View v) {
        if (v == backImg) {
            finish();
        } else if (v == switch_img) {
            setTabSelection(isALL ? Constant.TAB.CAL_PART : Constant.TAB.CAL_ALL);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
