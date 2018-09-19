package com.goldwind.app.help.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldwind.app.help.BaseFragment;
import com.goldwind.app.help.R;

import butterknife.ButterKnife;

/**
 * 复杂计算结果呈现
 */

public class CalculatorEmptyFragment extends BaseFragment {

    private static String TAG = "CalculatorEmptyFragment";

    Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.caculator_empty_layout, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;//super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
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

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        /*if(v == computeBtn){
            Intent intent = new Intent();
            intent.setClass(getActivity(),CalculatorOutPutActivity.class);
            startActivity(intent);
        }*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
