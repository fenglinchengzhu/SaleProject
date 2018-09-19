package com.goldwind.app.help.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldwind.app.help.BaseFragmentActivity;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.model.CategoryItem;
import com.goldwind.app.help.model.NewsResult;
import com.goldwind.app.help.util.CommonUtil;
import com.goldwind.app.help.util.LoadDataFromServer;
import com.goldwind.app.help.view.MyProgressDialog;
import com.goldwind.app.help.view.XListView;

import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewsActivity extends BaseFragmentActivity implements XListView.IXListViewListener {


    private static final String TAG = "NewsActivity";
    public int curFragmentTag = -1;
    @Bind(R.id.iv_back)
    ImageView backImg;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.lv_news)
    XListView lvRecommend;
    private boolean isSnackBar = false;
    // 推荐
    private ArrayList<NewsResult.NewsItem> dataList = new ArrayList<NewsResult.NewsItem>();
    private ListAdapter myAdapter;
    private CategoryItem mItem;
    private int lastStart = 0;
    private int page = 1;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);
        mContext = this;
        initAll();
    }


    @Override
    protected void initParam() {
        myAdapter = new ListAdapter(this);
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
                NewsResult.NewsItem resourceItem = (NewsResult.NewsItem) parent.getItemAtPosition(position);
                Intent detailIntent = new Intent();
                detailIntent.setClass(NewsActivity.this, NewsContentDetailActivity.class);
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
        String url = Constant.Api.GET_NEWS_LIST + "&number=" + page;
        LoadDataFromServer task = new LoadDataFromServer(mContext, url);
        task.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(com.alibaba.fastjson.JSONObject data) {
                Log.i(TAG, "json data is:" + data);
                onLoad();
                MyProgressDialog.closeDialog();
                com.alibaba.fastjson.JSONArray jsonArray = null;
                int number = 0;
                try {
                    number = data.getInteger("num");
                    jsonArray = data.getJSONArray("data");

                    if (jsonArray != null && number > 0) {
                        lastStart = 0;
                        ArrayList<NewsResult.NewsItem> list = new ArrayList<NewsResult.NewsItem>();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            NewsResult.NewsItem item2 = new NewsResult.NewsItem();
                            com.alibaba.fastjson.JSONObject jsonObject = jsonArray.getJSONObject(i);
                            item2.id = jsonObject.getInteger("id");
                            item2.entry = jsonObject.getString("entry");
                            item2.type = jsonObject.getString("type");
                            item2.summary = jsonObject.getString("summary");
                            item2.visited_num = jsonObject.getString("visited_num");
                            item2.pic = jsonObject.containsKey("pic") ? jsonObject.getString("pic") : "";
                            item2.url = jsonObject.containsKey("url") ? jsonObject.getString("url") : "";
                            item2.created_time = jsonObject.containsKey("time") ? jsonObject.getString("time") : "";
                            list.add(item2);
                        }
                        if (page == 1) {
                            dataList.clear();
                        }
                        dataList.addAll(list);
                        if (dataList.size() > 0) {
                            lvRecommend.showFooterView();
                        } else {
                            lvRecommend.hideFooterView();
                        }

                        myAdapter.notifyDataSetChanged();
                    } else {
                        if (page != 1) {

                            lvRecommend.hideFooterView();
                        } else {
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

    private static class ViewHolder {
        ImageView icon;
        TextView content;
        ImageView right;
    }

    class ListAdapter extends BaseAdapter {

        LayoutInflater layoutInflater;

        public ListAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
        }


        @Override
        public int getCount() {
            return dataList == null ? 0 : dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList == null ? null : dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.news_item_layout, null);

                viewHolder.icon = (ImageView) convertView.findViewById(R.id.frag_knowledge_item_icon);
                viewHolder.right = (ImageView) convertView.findViewById(R.id.frag_knowledge_item_right);
                viewHolder.content = (TextView) convertView.findViewById(R.id.frag_knowledge_item_content);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Log.d(TAG, "str is:" + dataList.get(position));
            viewHolder.content.setText(dataList.get(position).entry);

            return convertView;
        }
    }
}
