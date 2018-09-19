package com.goldwind.app.help.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldwind.app.help.BaseFragmentActivity;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.adapter.ContentListAdapter;
import com.goldwind.app.help.model.CategoryItem;
import com.goldwind.app.help.model.GetResourcesResult;
import com.goldwind.app.help.util.CommonUtil;
import com.goldwind.app.help.util.LoadDataFromServer;
import com.goldwind.app.help.view.MyProgressDialog;
import com.goldwind.app.help.view.XListView;

import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class KnowledgeBaikeDetailActivity extends BaseFragmentActivity implements XListView.IXListViewListener {


    private static final String TAG = "KnowledgeBaikeDetailActivity";
    private static final int PAGE_SIZE = 10;
    public int curFragmentTag = -1;
    @Bind(R.id.knowledge_baike_back)
    ImageView backImg;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.lv_content)
    XListView lvRecommend;
    private boolean isSnackBar = false;
    // 推荐
    private ArrayList<GetResourcesResult.BaikeItem> resourceItemList;
    private ContentListAdapter myAdapter;
    private CategoryItem mItem;
    private int lastStart = 0;
    private int page = 1;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_baike);
        ButterKnife.bind(this);
        mContext = this;
        initAll();
    }


    @Override
    protected void initParam() {
        resourceItemList = new ArrayList<GetResourcesResult.BaikeItem>();
        myAdapter = new ContentListAdapter(this, resourceItemList);
        mItem = (CategoryItem) getIntent().getSerializableExtra("category");
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initListener() {
        lvRecommend.setXListViewListener(this);
        backImg.setOnClickListener(this);
        lvRecommend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GetResourcesResult.BaikeItem resourceItem = (GetResourcesResult.BaikeItem) parent.getItemAtPosition(position);
                Intent detailIntent = new Intent();
                detailIntent.setClass(KnowledgeBaikeDetailActivity.this, BaikeContentDetailActivity.class);
                detailIntent.putExtra("item", resourceItem);
                startActivity(detailIntent);

            }
        });
        lvRecommend.setXListViewListener(this);
    }

    @Override
    protected void initData() {
        lvRecommend.setAdapter(myAdapter);
        onRefresh();
    }

    @Override
    public void onLoadMore() {
        page++;
        requestGetRecommend();
    }

    @Override
    public void onRefresh() {
        page = 1;
        lastStart = 0;
        requestGetRecommend();
    }

    @Override
    public void onClick(View v) {
        if (v == backImg) {
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void requestGetRecommend() {
        MyProgressDialog.showDialog(mContext, "加载中");
        String url = Constant.Api.GET_CATE_CONTENT + mItem.id + "&number=" + page;
        LoadDataFromServer task = new LoadDataFromServer(mContext, url);
        task.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(com.alibaba.fastjson.JSONObject data) {
                onLoad();
                MyProgressDialog.closeDialog();
                com.alibaba.fastjson.JSONArray jsonArray = null;
                int number = 0;
                try {
                    number = data.getInteger("num");
                    jsonArray = data.getJSONArray("data");

                    if (jsonArray != null && number > 0) {
                        lastStart = 0;
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

                        if (page == 1) {
                            if (list.size() < PAGE_SIZE) {
                                lvRecommend.hideFooterView();
                            }
                            resourceItemList.clear();
                            resourceItemList.addAll(list);
                        } else if (page != 1) {

                            resourceItemList.addAll(list);
                            if (resourceItemList.size() > 0) {
                                lvRecommend.showFooterView();
                            } else {
                                lvRecommend.hideFooterView();
                            }
                        }
                        myAdapter.notifyDataSetChanged();
                    } else {
                        if (page != 1) {

                            lvRecommend.hideFooterView();
                        } else {
                            resourceItemList.clear();

                        }
                        myAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    private void onLoad() {
        lvRecommend.stopRefresh();
        lvRecommend.stopLoadMore();
        lvRecommend.setRefreshTime(CommonUtil.date2Str(new Date(), "HH:mm"));
    }

}
