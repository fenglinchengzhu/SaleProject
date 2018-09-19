package com.goldwind.app.help.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.goldwind.app.help.BaseFragment;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.activity.KnowledgeBaikeDetailActivity;
import com.goldwind.app.help.activity.SearchResultActivity;
import com.goldwind.app.help.model.CategoryItem;
import com.goldwind.app.help.util.IChangeFragment;
import com.goldwind.app.help.util.LoadDataFromServer;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class KnowledgeFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    String TAG = "KnowledgeFragment";
    List<CategoryItem> dataList;
    ListAdapter mListAdapter;
    @Bind(R.id.frag_knowledge_search_btn)
    Button knowledge_btn;
    @Bind(R.id.frag_knowledge_listview)
    ListView mListView;
    private View rootView;
    private IChangeFragment mChangeFragment;

    public KnowledgeFragment(IChangeFragment iChangeFragment) {
        // Required empty public constructor
        mChangeFragment = iChangeFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_knowledge, null);
        ButterKnife.bind(this, rootView);
        return rootView;//inflater.inflate(R.layout.fragment_knowledge, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
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
        mListView.setOnItemClickListener(this);
        knowledge_btn.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        dataList = new ArrayList<CategoryItem>();
        getCategoryItem(0);
        Log.i(TAG, "dataList.size() is:" + dataList.size());
        mListAdapter = new ListAdapter(getActivity(), dataList);
        mListView.setAdapter(mListAdapter);

    }

    @Override
    public void onClick(View v) {
        if (v == knowledge_btn) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), SearchResultActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Object obj = (Object) parent.getAdapter().getItem(position);
        //Toast.makeText(getActivity(),obj.toString(),1).show();
        //mChangeFragment.changeFragment(KnowledgeBaikeActivity.knowledge_detail);
        Intent intent = new Intent();
        intent.setClass(getActivity(), KnowledgeBaikeDetailActivity.class);
        startActivity(intent);
    }

    private void getCategoryItem(int cateGoryId) {
        dataList.clear();
        String cate_url = Constant.Api.GET_BAIKE_CATEGORY + cateGoryId;
        LoadDataFromServer task = new LoadDataFromServer(this.getActivity(), cate_url);
        task.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(JSONObject data) {
                com.alibaba.fastjson.JSONArray jarry = data.getJSONArray("data");
                if (jarry != null && jarry.size() > 0) {

                }
            }
        });
    }

    private static class ViewHolder {
        ImageView icon;
        TextView content;
        ImageView right;
    }

    class ListAdapter extends BaseAdapter {

        List<CategoryItem> list;
        LayoutInflater layoutInflater;

        public ListAdapter(Context context, List<CategoryItem> list) {
            layoutInflater = LayoutInflater.from(context);
            this.list = list;
        }

        public void setList(List list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list == null ? null : list.get(position);
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
            Log.d(TAG, "str is:" + list.get(position));
            viewHolder.content.setText(list.get(position).title);

            return convertView;
        }
    }
}
