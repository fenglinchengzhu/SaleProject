package com.goldwind.app.help.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.goldwind.app.help.BaseFragmentActivity;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.adapter.CommonAdapter;
import com.goldwind.app.help.adapter.ViewHolder;
import com.goldwind.app.help.fragment.HistorySimpleCalculatorInputFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shihx on 2016/2/14.
 */
public class HistoryInputActivity extends BaseFragmentActivity {
    boolean isALL = true;

    @Bind(R.id.index_calculator_back_img)
    ImageView backImg;

    @Bind(R.id.index_calculator_switch_img)
    ImageView switch_img;

    @Bind(R.id.index_calculator_title_txt)
    TextView titleTxt;

    @Bind(R.id.index_calculator_down_img)
    ImageView downImg;

    @Bind(R.id.index_calculator_title_lay)
    LinearLayout titleLay;


    HistorySimpleCalculatorInputFragment mCalculatorInputAllFragment;
    //    CalculatorInputFragment mCalculatorInputPartFragment;
    String mKey;
    List<String> mList;
    Context mContext;
    CommonAdapter mCommonAdapter;
    private PopupWindow mPopupWindow;
    private Boolean isExtend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_layout);
        mContext = this;
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

    }

    @Override
    protected void initListener() {
        backImg.setOnClickListener(this);
        switch_img.setOnClickListener(this);
        titleTxt.setOnClickListener(this);
        downImg.setOnClickListener(this);
        titleLay.setOnClickListener(this);
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
        }
        titleTxt.setText(result);
    }

    private void setTabSelection(int tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        switch (tag) {
            case Constant.TAB.CAL_ALL:
                isALL = true;
                break;
            case Constant.TAB.CAL_PART:
                isALL = false;
                break;
        }
        fragmentTransaction.replace(R.id.calculator_input_content, getFragment(tag));
        fragmentTransaction.commit();
    }

    private Fragment getFragment(int tag) {
        Fragment fragment = null;
        switch (tag) {
            case Constant.TAB.CAL_ALL:
                if (mCalculatorInputAllFragment == null)
                    mCalculatorInputAllFragment = new HistorySimpleCalculatorInputFragment();
                mCalculatorInputAllFragment.setIsHistory(true);
                mCalculatorInputAllFragment.setKey(mKey);
                fragment = mCalculatorInputAllFragment;
                break;
            case Constant.TAB.CAL_PART:
//                if(mCalculatorInputPartFragment == null)
//                    mCalculatorInputPartFragment = new CalculatorInputFragment();
//                fragment = mCalculatorInputPartFragment;
                break;
        }
        return fragment;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            dismissPopWindow();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == backImg) {
            finish();
        } else if (v == switch_img) {
            setTabSelection(isALL ? Constant.TAB.CAL_PART : Constant.TAB.CAL_ALL);
        }
        if (v == titleLay) {
            //showPopuptWindow();
        }
        if (v == downImg || v == titleTxt) {
            //showPopuptWindow();
        }


    }

    private void showPopuptWindow() {
        if (mPopupWindow == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View popupWindow = layoutInflater.inflate(R.layout.pop_menu_list, null);
            ListView lisview = (ListView) popupWindow.findViewById(R.id.pop_menu_listview);
            mCommonAdapter = new CommonAdapter<String>(mContext, mList, R.layout.input_list_down_item) {
                @Override
                public void convert(ViewHolder helper, String item) {
                    helper.setText(R.id.input_list_single_item_txt, item);
                }
            };
            lisview.setAdapter(mCommonAdapter);

            lisview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int pos,
                                        long arg3) {
                    //Toast.makeText(mContext, "点击事件", 2000).show();
                    //点击跳转
                    dismissPopWindow();
                    titleTxt.setText(mList.get(pos));
                }
            });
            mPopupWindow = new PopupWindow(popupWindow, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setFocusable(true);
        } else {
        }
        mPopupWindow.showAsDropDown(findViewById(R.id.index_calculator_title_lay), 0, 15);
    }

    private void dismissPopWindow() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
