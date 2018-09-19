package com.goldwind.app.help.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldwind.app.help.BaseFragment;
import com.goldwind.app.help.BaseFragmentActivity;
import com.goldwind.app.help.R;
import com.goldwind.app.help.fragment.LocalFragment;
import com.goldwind.app.help.fragment.ResourceFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DocumentContentActivity extends BaseFragmentActivity {


    private static final int BENDI_INDEX = 1;
    private static final int ZAIXIAN_INDEX = 2;
    @Bind(R.id.iv_back_img)
    ImageView backImg;
    @Bind(R.id.iv_title)
    TextView titleLay;
    Context mContext;
    String[] Titles = new String[]{"本地文库", "在线文库"};
    ResourceFragment mResourceFragment;
    LocalFragment mLocalFragment;

    BaseFragment mReplaceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_detail_layout);
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

    }

    @Override
    protected void initData() {
        mResourceFragment = new ResourceFragment();
        mLocalFragment = new LocalFragment();
        int index = getIntent().getIntExtra("tagIndex", ZAIXIAN_INDEX);
        titleLay.setText(Titles[index - 1]);
        setTabSelection(index);
    }

    @Override
    public void onClick(View v) {
        if (v == backImg) {
            finish();
        }

    }

    private void setTabSelection(int tag) {
        if (tag == BENDI_INDEX) {
            mReplaceFragment = mLocalFragment;
        } else if (tag == ZAIXIAN_INDEX) {
            mReplaceFragment = mResourceFragment;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.replace(R.id.activity_doc_content, mReplaceFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
