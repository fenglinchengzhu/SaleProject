package com.goldwind.app.help.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.goldwind.app.help.BaseActivity;
import com.goldwind.app.help.R;
import com.goldwind.app.help.view.ClearEditText;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * on 2016/2/12.
 * 具体的一个百科页面
 */
public class SpecificBaikePageActivity extends BaseActivity {

    @Bind(R.id.home_header_home_btn)
    ImageView home_btn;
    @Bind(R.id.home_header_inner_lay)
    RelativeLayout inner_lay;
    @Bind(R.id.home_inner_search_btn)
    Button inner_search_btn;
    @Bind(R.id.home_inner_search_edit)
    ClearEditText searchEdit;
    @Bind(R.id.home_header_search_btn)
    ImageView searchBtn;//搜索按钮
    @Bind(R.id.specific_baike_page_webview)
    WebView mWebView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_baike_page);
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
        inner_search_btn.setOnClickListener(this);
        home_btn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        String webUrl = "www.baidu.com";
        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.loadUrl(webUrl);
    }

    @Override
    public void onClick(View v) {
        if (inner_search_btn == v) {
            //loadData();//need keyword
            startActivity(new Intent(mContext, SearchResultActivity.class));
        } else if (home_btn == v) {//跳转到主页
            startActivity(new Intent(mContext, HomeActivity.class));
            finish();
        } else if (v == searchBtn) {//搜索
            int result = inner_lay.getVisibility();
            if (result == View.GONE) {
                inner_lay.setVisibility(View.VISIBLE);
            } else {
                inner_lay.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
