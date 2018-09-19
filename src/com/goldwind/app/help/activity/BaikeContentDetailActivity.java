package com.goldwind.app.help.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.goldwind.app.help.BaseActivity;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.adapter.CommonAdapter;
import com.goldwind.app.help.adapter.ViewHolder;
import com.goldwind.app.help.model.GetResourcesResult;
import com.goldwind.app.help.model.PageMenu;
import com.goldwind.app.help.util.JsonUtil;
import com.goldwind.app.help.view.SlideMenu;

import java.util.List;

public class BaikeContentDetailActivity extends BaseActivity {
    private static final String TAG = "BaikeContentDetailActivity";
    String detailUrl;
    /**
     * 初始化滑动菜单
     */
    /*private void initSlidingMenu(String jstr) {
        // 设置滑动菜单视图界面
        setBehindContentView(R.layout.menu_frame);
        *//*getSupportFragmentManager().beginTransaction()
                .replace(R.id.menu_frame, SlidingMenuFragment.newInstance("list")).commit();*//*

        // 设置滑动菜单的属性值
        getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        getSlidingMenu().setShadowWidthRes(R.dimen.shadow_width);
        getSlidingMenu().setShadowDrawable(R.drawable.shadow);
        getSlidingMenu().setBehindOffsetRes(R.dimen.slidingmenu_offset);
        getSlidingMenu().setFadeDegree(0.35f);

    }*/
    List<PageMenu> mList;
    //    private PopupWindow mPopupWindow;
    CommonAdapter mCommonAdapter;
    ListView lisview;
    private ImageView backImage;
    private TextView mTitleView;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private GetResourcesResult.BaikeItem mItem;
    private Context mContext;
    private String username;
    private Handler mHandler = new Handler();
    private SlideMenu mSlideMenu;
    private Boolean isExtend = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.baike_content_detail);
        mContext = this;
        backImage = (ImageView) findViewById(R.id.iv_back);
        mTitleView = (TextView) findViewById(R.id.name);
        mWebView = (WebView) findViewById(R.id.webview);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_load_more);
        backImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView != null && mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    setResult(1002);
                    finish();
                }
            }
        });
        initWebView();
        iniView();
        initAll();
    }

    private void iniView() {
        mSlideMenu = (SlideMenu) findViewById(R.id.baike_slide_menu);
        String title = "";
        if (getIntent() != null) {

            mItem = (GetResourcesResult.BaikeItem) getIntent().getSerializableExtra("item");
            boolean isTop = getIntent().getBooleanExtra("isTop", false);
            Log.i(TAG, "mItem.type is:" + mItem.type);
            Log.i(TAG, "mItem.toString() is:" + mItem.toString());
            if (isTop) {
                if ("outlink".equals(mItem.type)) {
                    detailUrl = mItem.url;
                } else if ("baike".equals(mItem.type) || "news".equals(mItem.type)) {
                    detailUrl = Constant.Api.GOLD_DOMAIN + mItem.url;
                }
            } else {
                if ("outlink".equals(mItem.type)) {
                    detailUrl = mItem.url;
                } else if ("original".equals(mItem.type)) {
                    detailUrl = Constant.Api.GET_BAIKE_DETAIL + mItem.id;
                } else {
                    detailUrl = Constant.Api.GET_BAIKE_DETAIL + mItem.id;
                }
            }
            Log.d(TAG, "detail " + detailUrl);
            title = mItem.entry;
        }
//        mWebView.loadUrl(detailUrl);
        mTitleView.setText(title);

        mWebView.setWebChromeClient(new WebChromeClient() {
                                        @Override
                                        public void onProgressChanged(WebView view, int newProgress) {
                                            mProgressBar.setProgress(newProgress);
                                        }


                                    }


        );
        if (TextUtils.isEmpty(detailUrl)) {
            Toast.makeText(mContext, "地址不合法", Toast.LENGTH_LONG).show();
        }
//        detailUrl = "http://www.jd.com";
        mWebView.loadUrl(detailUrl);
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setVerticalScrollbarOverlay(true); //指定的垂直滚动条有叠加样式


//        mWebView.getSettings().setJavaScriptEnabled(true);
        //mWebView.loadUrl("http://goldwind.yuefengd.com/gw/mng/baike/baikeViews?baike_id=54");
        mWebView.addJavascriptInterface(this, "WebViewApi");
        mWebView.setWebViewClient(new WebViewLoadClient());


        WebSettings settings = mWebView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);//显示放大缩小 controler
        settings.setSupportZoom(true);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            setResult(1002);
            finish();
        }
    }

    /**
     * This is not called on the UI thread. Post a runnable to invoke
     * loadUrl on the UI thread.
     */
    @JavascriptInterface//加入这个注解
    public void RenderDirectory(final String json_str) {
        mHandler.post(new Runnable() {
            public void run() {
                //这里得到json_str 解析后传给
                initSlidingMenu(json_str);
                Log.d(TAG, "json_str" + json_str);
                Log.d(TAG, "js调用了java函数 RenderDirectory");
            }
        });

    }

    @JavascriptInterface//加入这个注解
    public void ClearIsDirectoryOpen() {

    }

    //跳转至对应项
    public void GoToId(final String id) {
        mHandler.post(new Runnable() {
            public void run() {
                //id 就是跳转的
                Log.d(TAG, "java调用了js函数 gotoid id is:" + id);
                mWebView.loadUrl("javascript:gotoid('" + id + "')");
            }
        });

    }

    @JavascriptInterface//加入这个注解
    public void OpenDirectory(final String id) {
        mHandler.post(new Runnable() {
            public void run() {
                //id 就是跳转的
                Log.d(TAG, "js调用了java函数 OpenDirectory id is:" + id);
                mSlideMenu.openMenu();
                //mWebView.loadUrl("javascript:gotoid(id)");
                //打开侧边栏
                /*mPopupWindow.showAsDropDown(
                        findViewById(R.id.top_bar), 0, 0);
                backgroundAlpha(0.6f);*/
            }
        });

    }

    @JavascriptInterface//加入这个注解
    public void CloseDirectory() {
        mHandler.post(new Runnable() {
            public void run() {
                Log.d(TAG, "js调用了java函数 CloseDirectory");
                //关闭侧边栏
                mSlideMenu.closeMenu();
                mWebView.loadUrl("javascript:ClearIsDirectoryOpen()");
            }
        });

    }

    private void initSlidingMenu(String json) {
//        Log.i(TAG,"json is:"+json);
//        Toast.makeText(BaikeContentDetailActivity.this, json, Toast.LENGTH_SHORT).show();
//        PageMenu pageMenu = JsonUtil.fromJson(json, PageMenu.class);
//        Log.i(TAG,"pageMenu.title is:"+pageMenu.title);
        mList = new JsonUtil().getListMenu(json, false);
        initPopuptWindow();
        /*mPopupWindow.showAsDropDown(findViewById(R.id.top_bar), 0, 0);
        backgroundAlpha(0.6f);*/
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    /**
     * 切换Fragment，也是切换视图的内容
     */
    public void goToPosition(String position) {
        OpenDirectory(position);
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

    }

    private int getDisplayWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        return screenWidth;
    }

    private void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
//        View popupWindow = layoutInflater.inflate(R.layout.pop_menu_list, null);
        lisview = (ListView) findViewById(R.id.baike_detail_listview);

        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.list_single_item);
        adapter.add("栏目1");
        adapter.add("栏目2");
        adapter.add("栏目3");
        lisview.setAdapter(adapter);*/
        mCommonAdapter = new CommonAdapter<PageMenu>(mContext, mList, R.layout.pop_list_single_item) {
            @Override
            public void convert(ViewHolder helper, PageMenu item) {
                helper.setText(R.id.list_single_item_txt, item.title);
            }
        };
        lisview.setAdapter(mCommonAdapter);

        lisview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int pos,
                                    long arg3) {
                //Toast.makeText(mContext, "点击事件", 2000).show();
                final PageMenu pageMenu = (PageMenu) mCommonAdapter.getItem(pos);
                //点击跳转
                mSlideMenu.closeMenu();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GoToId(pageMenu.id);
                    }
                }, 300);
            }
        });
        /*mPopupWindow = new PopupWindow(popupWindow, getDisplayWidth()/2,
                LayoutParams.MATCH_PARENT);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.white_bg));
        mPopupWindow.setAnimationStyle(R.style.menu_popwindow_anim_style);

        mPopupWindow.setOnDismissListener(new poponDismissListener());*/
    }

    class WebViewLoadClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;//super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

        }

        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
//            back.setEnabled(webView.canGoBack());
//            forward.setEnabled(webView.canGoForward());
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mWebView.getTitle() != null) {
                mTitleView.setText(mWebView.getTitle());
            }

        }
    }

    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            //Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
        }

    }

    /*
     * 获取PopupWindow实例
     */
    /*private void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }*/
}
