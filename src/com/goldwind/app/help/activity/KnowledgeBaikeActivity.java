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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.goldwind.app.help.BaseFragmentActivity;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.model.CategoryItem;
import com.goldwind.app.help.util.LoadDataFromServer;
import com.goldwind.app.help.view.MyProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class KnowledgeBaikeActivity extends BaseFragmentActivity {


    public static final int knowledge = 0x1001;
    public static final int knowledge_detail = knowledge + 1;
    private static final String TAG = "KnowledgeBaikeActivity";
    @Bind(R.id.knowledge_baike_back)
    ImageView backImg;
    List<CategoryItem> dataList;
    ListAdapter mListAdapter;
    @Bind(R.id.frag_knowledge_search_btn)
    ImageButton knowledge_btn;
    @Bind(R.id.frag_knowledge_search_edit)
    EditText etSearch;
    @Bind(R.id.frag_knowledge_listview)
    ListView mListView;
    private boolean isSnackBar = false;
    private CategoryItem mCurrentItem;
    private int mIndex = 0;
    private boolean isCategory = true;
    private HashMap<Integer, List<CategoryItem>> mCategoryMap = new HashMap<Integer, List<CategoryItem>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge_baike);
        ButterKnife.bind(this);
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            super.setTranslucentStatus(true);
            isSnackBar = true;
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);// 通知栏所需颜色*/
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
        etSearch.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        dataList = new ArrayList<CategoryItem>();
        mListAdapter = new ListAdapter(this);
        mListView.setAdapter(mListAdapter);
        getCategoryItem(0);

        Log.i(TAG, "dataList.size() is:" + dataList.size());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                final CategoryItem item = (CategoryItem) parent.getItemAtPosition(i);
                mCurrentItem = item;
                if (item.isParent) {
                    pullData(item.id);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(KnowledgeBaikeActivity.this, KnowledgeBaikeDetailActivity.class);
                    intent.putExtra("category", item);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == backImg) {
            finish();
        } else if (v == etSearch) {
            Intent intent = new Intent(this, SearchResultActivity.class);
            startActivity(intent);
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

    private void getCategoryItem(int cateGoryId) {
        MyProgressDialog.showDialog(this, "加载中");

        String cate_url = Constant.Api.GET_BAIKE_CATEGORY + cateGoryId;
        LoadDataFromServer task = new LoadDataFromServer(this, cate_url);
        task.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(JSONObject data) {
                Log.d(TAG, "result " + data.toString());
                //int number = data.getInteger("num");
                MyProgressDialog.closeDialog();
                com.alibaba.fastjson.JSONArray jarry = data.getJSONArray("data");
                if (jarry != null && jarry.size() > 0) {
                    List<CategoryItem> temp = new ArrayList<CategoryItem>();
                    for (int i = 0; i < jarry.size(); i++) {
                        JSONObject jb = jarry.getJSONObject(i);
                        CategoryItem item = new CategoryItem();
                        item.id = jb.getInteger("id");
                        item.title = jb.getString("title");
                        item.isParent = jb.getBoolean("is_parent");
                        temp.add(item);
                    }
                    dataList = temp;
                    Log.d(TAG, "result " + temp.toString());
                    mCategoryMap.put(mIndex, temp);
                    mListAdapter.notifyDataSetChanged();

                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        popData();
    }

    private void popData() {
        if (mIndex > 0) {
            mIndex--;
            dataList = mCategoryMap.get(mIndex);
            mListAdapter.notifyDataSetChanged();
        } else {
            finish();
        }
        Log.d(TAG, "data map" + mCategoryMap.toString());
    }

    private void pullData(int cate_id) {
        mIndex++;
        getCategoryItem(cate_id);
        dataList = mCategoryMap.get(mIndex);
        mListAdapter.notifyDataSetChanged();
        Log.d(TAG, "data map" + mCategoryMap.toString());
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
                convertView = layoutInflater.inflate(R.layout.frag_knowledge_item_layout, null);

                viewHolder.icon = (ImageView) convertView.findViewById(R.id.frag_knowledge_item_icon);
                viewHolder.right = (ImageView) convertView.findViewById(R.id.frag_knowledge_item_right);
                viewHolder.content = (TextView) convertView.findViewById(R.id.frag_knowledge_item_content);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Log.d(TAG, "str is:" + dataList.get(position));
            viewHolder.content.setText(dataList.get(position).title);

            return convertView;
        }
    }


}
