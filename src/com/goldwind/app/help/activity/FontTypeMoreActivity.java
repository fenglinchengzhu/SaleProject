package com.goldwind.app.help.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.goldwind.app.help.BaseActivity;
import com.goldwind.app.help.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FontTypeMoreActivity extends BaseActivity {

    @Bind(R.id.iv_back)
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_more);
        ButterKnife.bind(this);
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
        ivBack.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: {
                finish();
                break;
            }
        }
    }
}
