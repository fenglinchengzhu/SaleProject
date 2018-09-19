package com.goldwind.app.help.fragment;

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

import com.goldwind.app.help.BaseFragment;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.activity.SearchResultActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class KnowledgeDetailFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    List<String> dataList;
    ListAdapter mListAdapter;
    @Bind(R.id.frag_knowledge_detail_search_btn)
    Button knowledge_detail_btn;
    @Bind(R.id.frag_knowledge_detail_listview)
    ListView mDetailListView;
    private String TAG = getClass().getName();
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_knowledge_detail, null);
        ButterKnife.bind(this, rootView);
        return rootView;//inflater.inflate(R.layout.fragment_knowledge_detail, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initAll();
    }

    @Override
    public void initAll() {
        super.initAll();
    }

    @Override
    protected void initParam() {

    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initListener() {
        knowledge_detail_btn.setOnClickListener(this);
        mDetailListView.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        dataList = new ArrayList<String>();
        String[] reses = getResources().getStringArray(R.array.knowledge_baike_detail_array);
        dataList = Arrays.asList(reses);
        Log.i(TAG, "dataList.size() is:" + dataList.size());
        mListAdapter = new ListAdapter(getActivity(), dataList);
        mDetailListView.setAdapter(mListAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v == knowledge_detail_btn) {
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
        Object obj = parent.getItemAtPosition(position);
        if (obj != null) {
            Intent intent = new Intent();
            intent.putExtra(Constant.EXTRA.JIZU_TYPE, (String) obj);
            intent.setClass(getActivity(), SearchResultActivity.class);
            startActivity(intent);
        }
    }

    private static class ViewHolder {
        ImageView icon;
        TextView content;
        ImageView right;
    }

    class ListAdapter extends BaseAdapter {

        List<String> list;
        LayoutInflater layoutInflater;

        public ListAdapter(Context context, List list) {
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
            viewHolder.content.setText(list.get(position));

            return convertView;
        }
    }
}
