package com.goldwind.app.help.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.goldwind.app.help.R;

import java.util.List;

public class SearchAdapter extends BaseAdapter {

    private List<String> searchKeyWords;
    private Context mContext;

    public SearchAdapter(Context context, List<String> datas) {
        mContext = context;
        searchKeyWords = datas;
    }

    @Override
    public int getCount() {
        if (searchKeyWords == null) {
            return 0;
        }
        return searchKeyWords.size();
    }

    @Override
    public Object getItem(int position) {
        return searchKeyWords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.layout_item_baike_search_list, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        holder.tv_name.setText(searchKeyWords.get(position));

        return convertView;
    }

    private class ViewHolder {
        TextView tv_name;
    }
}