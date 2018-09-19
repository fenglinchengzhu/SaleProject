package com.goldwind.app.help.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.goldwind.app.help.BaseFragmentActivity;
import com.goldwind.app.help.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DocumentActivity extends BaseFragmentActivity {


    private static final int BENDI_INDEX = 1;
    private static final int ZAIXIAN_INDEX = 2;
    @Bind(R.id.index_calculator_back_img)
    ImageView backImg;
    @Bind(R.id.index_calculator_title_lay)
    View titleLay;
    @Bind(R.id.rl_doc_bd)
    RelativeLayout rl_doc_bd;
    @Bind(R.id.rl_doc_zx)
    RelativeLayout rl_doc_zx;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_layout);
        mContext = this.getApplicationContext();
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
        backImg.setOnClickListener(this);
        rl_doc_bd.setOnClickListener(this);
        rl_doc_zx.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(mContext, DocumentContentActivity.class);

        if (v == rl_doc_bd) {
            intent.putExtra("tagIndex", BENDI_INDEX);
            startActivity(intent);
        } else if (v == rl_doc_zx) {
            intent.putExtra("tagIndex", ZAIXIAN_INDEX);
            startActivity(intent);
        } else if (v == backImg) {
            finish();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
