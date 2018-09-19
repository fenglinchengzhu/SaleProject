package com.goldwind.app.help.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.goldwind.app.help.BaseFragment;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.activity.CalculatorEmptyActivity;
import com.goldwind.app.help.activity.CalculatorHistoryActivity;
import com.goldwind.app.help.activity.CalculatorInputActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewCalculatorFragment extends BaseFragment {

    Context mContext;

    @Bind(R.id.icon_calculator_20_img)
    ImageView calculator_20_img;

    @Bind(R.id.icon_calculator_25_img)
    ImageView calculator_25_img;

    @Bind(R.id.icon_calculator_15_img)
    ImageView calculator_15_img;

    @Bind(R.id.icon_calculator_30_img)
    ImageView calculator_30_img;

    @Bind(R.id.icon_calculator_myhistory_img)
    ImageView calculator_myhistory_img;

    @Bind(R.id.icon_calculator_mytemp_img)
    ImageView calculator_mytemp_img;

    @Bind(R.id.rl_top_menu)
    RelativeLayout rlTopMenu;

    @Bind(R.id.top_menu_back)
    ImageView topMenuBack;

    public NewCalculatorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_calculator, container, false);
        // Inflate the layout for this fragment
        ButterKnife.bind(this, rootView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rlTopMenu.getLayoutParams();
            int h = Resources.getSystem().getDimensionPixelSize(Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
            layoutParams.topMargin = h;
//			rlTopMenu.setBackgroundResource(R.color.c6);
            rlTopMenu.setLayoutParams(layoutParams);
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initAll();
    }


    @Override
    protected void initViews() {
        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.calculator_20_icon, calculator_20_img);
        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.calculator_25_icon, calculator_25_img);
        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.calculator_15_icon, calculator_15_img);
        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.calculator_30_icon, calculator_30_img);
        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.calculator_myhistory_icon, calculator_myhistory_img);
        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.calculator_mytmp_icon, calculator_mytemp_img);
    }

    @Override
    protected void initParam() {

    }

    @Override
    protected void initListener() {
        calculator_20_img.setOnClickListener(this);
        calculator_15_img.setOnClickListener(this);
        calculator_25_img.setOnClickListener(this);
        calculator_30_img.setOnClickListener(this);
        calculator_myhistory_img.setOnClickListener(this);
        calculator_mytemp_img.setOnClickListener(this);

        topMenuBack.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        if (v == calculator_20_img) {
            Intent intent = new Intent();
            intent.putExtra(Constant.EXTRA.CALCULATOR_NAME, Constant.EXTRA.CALCULATOR_20);
            intent.setClass(mContext, CalculatorInputActivity.class);
            startActivity(intent);
        }

        if (v == topMenuBack) {
            getActivity().onBackPressed();
        }
        if (v == calculator_myhistory_img) {
            Intent intent = new Intent();
            intent.setClass(mContext, CalculatorHistoryActivity.class);
            startActivity(intent);
        }

        if (v == calculator_mytemp_img) {
            Intent intent = new Intent();
            intent.putExtra(Constant.EXTRA.CALCULATOR_NAME, Constant.EXTRA.CALCULATOR_TMP);
            intent.setClass(mContext, CalculatorEmptyActivity.class);
            startActivity(intent);
        }

        if (v == calculator_15_img) {
            Intent intent = new Intent();
            intent.putExtra(Constant.EXTRA.CALCULATOR_NAME, Constant.EXTRA.CALCULATOR_15);
            intent.setClass(mContext, CalculatorEmptyActivity.class);
            startActivity(intent);
        }

        if (v == calculator_25_img) {
            Intent intent = new Intent();
            intent.putExtra(Constant.EXTRA.CALCULATOR_NAME, Constant.EXTRA.CALCULATOR_25);
            intent.setClass(mContext, CalculatorEmptyActivity.class);
            startActivity(intent);
        }

        if (v == calculator_30_img) {
            Intent intent = new Intent();
            intent.putExtra(Constant.EXTRA.CALCULATOR_NAME, Constant.EXTRA.CALCULATOR_30);
            intent.setClass(mContext, CalculatorEmptyActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        /*img15Bitmap.recycle();
        img25Bitmap.recycle();
        img20Bitmap.recycle();
        img30Bitmap.recycle();
        imgHisBitmap.recycle();
        imgModBitmap.recycle();*/
    }
}
