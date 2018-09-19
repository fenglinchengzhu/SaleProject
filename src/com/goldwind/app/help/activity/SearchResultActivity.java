package com.goldwind.app.help.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldwind.app.help.BaseActivity;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.adapter.ContentListAdapter;
import com.goldwind.app.help.adapter.SearchAdapter;
import com.goldwind.app.help.db.MyDB;
import com.goldwind.app.help.model.BaikeResult;
import com.goldwind.app.help.model.GetResourcesResult;
import com.goldwind.app.help.util.CommonUtil;
import com.goldwind.app.help.util.LoadDataFromServer;
import com.goldwind.app.help.view.MyProgressDialog;
import com.goldwind.app.help.view.XListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchResultActivity extends BaseActivity implements XListView.IXListViewListener {

    private static final String LOG_TAG = "SearchResultActivity";
    private static final int PAGE_SIZE = 10;
    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.btn_search)
    ImageButton btnSearch;


    @Bind(R.id.rl_search_reslut)
    RelativeLayout rlSearchReslut;

    @Bind(R.id.iv_back)
    ImageView ivback;


    @Bind(R.id.lv_search_result)
    ListView keyListView;
    @Bind(R.id.tv_index)
    Button tvIndex;


    @Bind(R.id.search_result_listview)
    XListView mListView;
    String TAG = "SearchResultActivity";
    TextView emptyView;
    private Context mContext;
    private List<BaikeResult> mList;
    private String mKeyWord;
    private ContentListAdapter myAdapter;
    private SearchAdapter searchAdapter;
    private MyDB myDB;
    // 推荐
    private int page = 0;
    private int lastStart = 0;
    private List<GetResourcesResult.BaikeItem> searchItemList = new ArrayList<GetResourcesResult.BaikeItem>();
    private ArrayList<String> searchKeyWords = new ArrayList<String>();

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        mContext = this;
        myDB = MyDB.getInstance(mContext);
        ButterKnife.bind(this);
        initAll();
    }

    @Override
    protected void initParam() {
        searchAdapter = new SearchAdapter(mContext, searchKeyWords);
        keyListView.setAdapter(searchAdapter);
        searchItemList = new ArrayList<GetResourcesResult.BaikeItem>();
        myAdapter = new ContentListAdapter(mContext, searchItemList);
        mListView.setAdapter(myAdapter);
    }

    @Override
    public void onLoadMore() {
        page++;
        searchResult();
    }

    @Override
    public void onRefresh() {
        page = 0;
        lastStart = 0;
        searchResult();
    }

    @Override
    public void onResume() {
        super.onResume();
        hideKeyListView();
        btnSearch.requestFocus();
        btnSearch.requestFocusFromTouch();

    }

    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(CommonUtil.date2Str(new Date(), "HH:mm"));
    }

    private void initEmptyView() {
        emptyView = new TextView(mContext);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        emptyView.setText("");
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) mListView.getParent()).addView(emptyView);
        mListView.setEmptyView(emptyView);
    }

    @Override
    protected void initViews() {
        mListView.setAdapter(myAdapter);
        initEmptyView();
        mListView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void initListener() {
        etSearch.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        ivback.setOnClickListener(this);
        mListView.setXListViewListener(this);
        keyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mKeyWord = (String) parent.getItemAtPosition(position);
                etSearch.setText(mKeyWord);
                searchResult();
                hideKeyListView();
                btnSearch.requestFocus();
                btnSearch.requestFocusFromTouch();
                /*keyListView.setVisibility(View.GONE);
                rlSearchReslut.setVisibility(View.GONE);*/
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GetResourcesResult.BaikeItem resourceItem = (GetResourcesResult.BaikeItem) parent.getItemAtPosition(position);
                Intent detailIntent = new Intent();
                detailIntent.setClass(mContext, BaikeContentDetailActivity.class);
                detailIntent.putExtra("item", resourceItem);
                startActivityForResult(detailIntent, 1001);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!TextUtils.isEmpty(s) && s.length() > 0) {

                } else {
                    searchKeyWords = myDB.getHistory(5 + "");
                    Log.d(LOG_TAG, "history " + searchKeyWords.toString());
                    searchAdapter.notifyDataSetChanged();
                    searchAdapter = new SearchAdapter(mContext, searchKeyWords);
                    keyListView.setAdapter(searchAdapter);
                    keyListView.setVisibility(View.VISIBLE);
                    rlSearchReslut.setVisibility(View.VISIBLE);
                }


            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {

            }
        });

        etSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    searchKeyWords = myDB.getHistory(5 + "");
                    Log.d(LOG_TAG, "history " + searchKeyWords.toString());
                    searchAdapter.notifyDataSetChanged();
                    searchAdapter = new SearchAdapter(mContext, searchKeyWords);
                    keyListView.setAdapter(searchAdapter);
                    searchAdapter.notifyDataSetChanged();
                    keyListView.setVisibility(View.VISIBLE);
                    rlSearchReslut.setVisibility(View.VISIBLE);
                }
            }
        });
        tvIndex.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        //String searchKey = getIntent().getExtras().getString(Constant.EXTRA.SEARCH_NAME);
        mList = new ArrayList<BaikeResult>();

    }

    @Override
    public void onClick(View v) {
        if (v == btnSearch) {
            mListView.setVisibility(View.GONE);
            mKeyWord = etSearch.getText().toString().trim();
            searchResult();
            btnSearch.requestFocus();
            btnSearch.requestFocusFromTouch();
//            hideSoftInputMethod();
        } else if (v == tvIndex) {
            Intent intent = new Intent(mContext, KnowledgeBaikeActivity.class);
            startActivity(intent);
        } else if (v == etSearch) {
            mListView.setVisibility(View.GONE);
//            showSoftInputMethod();
            emptyView.setText("");
            initEmptyView();
            searchKeyWords = myDB.getHistory(5 + "");
            Log.d(LOG_TAG, "history " + searchKeyWords);
            searchAdapter = new SearchAdapter(mContext, searchKeyWords);
            keyListView.setAdapter(searchAdapter);
            searchAdapter.notifyDataSetChanged();
            keyListView.setVisibility(View.VISIBLE);
            rlSearchReslut.setVisibility(View.VISIBLE);
        } else if (v == ivback) {
            hideSoftInputMethod();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "requestCode is:" + requestCode + "---resultCode is:" + resultCode);
        if (requestCode == 1001) {
            Log.i(TAG, "searchItemList.size() is:" + searchItemList.size());

            keyListView.setVisibility(View.VISIBLE);
            rlSearchReslut.setVisibility(View.VISIBLE);
            etSearch.requestFocus();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }


    private void searchResult() {
        hideKeyListView();//不要隐藏，返回时不可见
//        mListView.setVisibility(View.INVISIBLE);
        searchItemList.clear();
        if (TextUtils.isEmpty(mKeyWord)) {
            Toast.makeText(mContext, "搜索内容不能为空", Toast.LENGTH_LONG).show();
            return;
        } else {
            myDB.saveHistory(mKeyWord);
            MyProgressDialog.showDialog(mContext, "加载中");
            onLoad();
            String searchUrl = Constant.Api.GET_SEARCH + mKeyWord + "&number=" + (page + 1);
            LoadDataFromServer task = new LoadDataFromServer(mContext, searchUrl);
            task.getData(new LoadDataFromServer.DataCallBack() {
                @Override
                public void onDataCallBack(com.alibaba.fastjson.JSONObject data) {
                    com.alibaba.fastjson.JSONArray jsonArray = null;
                    int number = 0;
                    MyProgressDialog.closeDialog();
                    try {
                        Log.d(LOG_TAG, "result " + data.toString());
                        number = data.getInteger("num");
                        if (number == PAGE_SIZE) {
                            mListView.showFooterView();
                        } else {
                            mListView.hideFooterView();
                        }
                        jsonArray = data.getJSONArray("data");

                        if (jsonArray != null && number > 0) {
                            ArrayList<GetResourcesResult.BaikeItem> list = new ArrayList<GetResourcesResult.BaikeItem>();
                            for (int i = 0; i < jsonArray.size(); i++) {
                                GetResourcesResult.BaikeItem item2 = new GetResourcesResult.BaikeItem();
                                com.alibaba.fastjson.JSONObject jsonObject = jsonArray.getJSONObject(i);
                                item2.id = jsonObject.getInteger("id");
                                item2.entry = jsonObject.getString("entry");
                                item2.pic = jsonObject.getString("pic");
                                item2.summary = jsonObject.getString("summary");
                                item2.created_time = jsonObject.getString("created_time");
                                item2.catalog_id = jsonObject.getString("catalog_id");
                                list.add(item2);
                            }
                            if (list.size() == 0) {

                                Toast.makeText(mContext, "no result", Toast.LENGTH_LONG).show();
                            } else if (list.size() == 1) {
                                Intent detailIntent = new Intent();
                                detailIntent.setClass(mContext, BaikeContentDetailActivity.class);
                                detailIntent.putExtra("item", list.get(0));
                                startActivityForResult(detailIntent, 1001);
                            } else {
                                if (page == 0) {
                                    searchItemList.clear();
//                                    Toast.makeText(mContext, "刷新成功", Toast.LENGTH_LONG).show();
                                }
                                searchItemList.addAll(list);
                                myAdapter.notifyDataSetChanged();
                            }
                        } else {
                            emptyView.setText("暂无资源");
                            initEmptyView();
                        }
                    } catch (Exception e) {

                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    private void hideKeyListView() {
        keyListView.setVisibility(View.GONE);
        rlSearchReslut.setVisibility(View.GONE);
    }

    private void showSoftInputMethod() {
        InputMethodManager imm = (InputMethodManager) SearchResultActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void hideSoftInputMethod() {
        InputMethodManager imm = (InputMethodManager) SearchResultActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
    }
}
