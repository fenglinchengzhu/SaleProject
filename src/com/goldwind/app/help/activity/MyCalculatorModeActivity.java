package com.goldwind.app.help.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.goldwind.app.help.BaseActivity;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.adapter.CommonAdapter;
import com.goldwind.app.help.adapter.ViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 我的模板
 */
public class MyCalculatorModeActivity extends BaseActivity {

    @Bind(R.id.knowledge_baike_back)
    ImageView back_img;

    @Bind(R.id.knowledge_baike_title)
    TextView titleTxt;

    @Bind(R.id.frag_my_mode_listview)
    ListView mListView;

    List<String> mList;
    CommonAdapter mCommonAdapter;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_calculator_mode);
        ButterKnife.bind(this);
        mContext = this;
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            super.setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.c1);// 通知栏所需颜色*/
        initAll();
    }

    @Override
    protected void initParam() {

    }

    @Override
    protected void initViews() {
        titleTxt.setText(getString(R.string.str_my_cal_mode));
    }

    @Override
    protected void initListener() {
        back_img.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mList = new ArrayList<String>();
        String[] resesHistory = getResources().getStringArray(R.array.calculator_template_array);
        mList = Arrays.asList(resesHistory);

        mCommonAdapter = new CommonAdapter<String>(mContext, mList, R.layout.mode_list_single_item) {
            @Override
            public void convert(ViewHolder helper, String item) {
                TextView textView = (TextView) helper.getView(R.id.mode_list_single_item_txt);
                textView.setText(item);
                textView.setTextColor(getResources().getColor(R.color.c1));
            }
        };
        mListView.setAdapter(mCommonAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int pos,
                                    long arg3) {
                //Toast.makeText(mContext, "点击事件", 2000).show();
                //点击跳转
                //titleTxt.setText(mList.get(pos));
                Intent intent = new Intent();
                intent.setClass(mContext, CalculatorInputActivity.class);
                intent.putExtra(Constant.EXTRA.CALCULATOR_NAME, Constant.EXTRA.CALCULATOR_20);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == back_img) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
