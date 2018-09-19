package com.goldwind.app.help.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.goldwind.app.help.util.SPUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CalculatorHistoryActivity extends BaseActivity {

    @Bind(R.id.calculator_history_back)
    ImageView back_img;

    @Bind(R.id.calculator_history_title)
    TextView titleTxt;

    @Bind(R.id.frag_my_history_listview)
    ListView mListView;

    List<String> mList;
    ArrayList<String> mTempList = new ArrayList<String>();
    Context mContext;

    CommonAdapter mCommonAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_history);
        mContext = this;
        ButterKnife.bind(this);
        initAll();
    }

    @Override
    protected void initParam() {

    }

    @Override
    protected void initViews() {
        titleTxt.setText(getString(R.string.str_my_cal_history));
    }

    @Override
    protected void initListener() {
        back_img.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mList = new ArrayList<String>();
//        HashMap<String,String> keyMap = SPUtils.getAll(this);
        SPUtils.setFileName(SPUtils.FILE_INPUT_NAME);
        Log.d("test", "   " + SPUtils.getAll(this).keySet());
        Set<String> keyS = SPUtils.getAll(this).keySet();

        mList.clear();

        mTempList.clear();
        ;
        Log.d("tiger", keyS.toString());
        for (String key : keyS) {

            mTempList.add(key);

        }

        Collections.sort(mTempList, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                String time1Str = lhs.substring(lhs.lastIndexOf(":") + 1);
                String time2Str = rhs.substring(rhs.lastIndexOf(":") + 1);
                long time1 = Long.parseLong(time1Str);
                long time2 = Long.parseLong(time2Str);

                if (time1 == time2) {
                    return 0;
                } else if (time1 > time2) {
                    return -1;
                } else {
                    return 1;
                }

            }
        });

        for (String key : mTempList) {
            String tmpKey = key.substring(0, key.lastIndexOf(":"));
            mList.add(tmpKey);
        }


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
                intent.setClass(mContext, HistoryInputActivity.class);
                intent.putExtra(Constant.EXTRA.CALCULATOR_NAME, Constant.EXTRA.CALCULATOR_20);
                intent.putExtra("key", mTempList.get(pos));
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
