package com.goldwind.app.help.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.model.GetResourcesResult;
import com.goldwind.app.help.util.LoadUserAvatar;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import me.xiaopan.sketch.SketchImageView;

public class ContentListAdapter extends BaseAdapter {
    private List<GetResourcesResult.BaikeItem> resourceItemList;
    private LoadUserAvatar avatarLoader;
    private Context mContext;

    public ContentListAdapter(Context context, List<GetResourcesResult.BaikeItem> datas) {
        resourceItemList = datas;
        mContext = context;
        avatarLoader = new LoadUserAvatar(mContext, Constant.BASE_FILE_PATH);

    }


    @Override
    public int getCount() {
        if (resourceItemList == null) {
            return 0;
        }
        return resourceItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return resourceItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.layout_item_baike_list, null);
            holder = new ViewHolder();
            holder.iv_pic = (SketchImageView) convertView.findViewById(R.id.iv_pic);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_description = (TextView) convertView.findViewById(R.id.tv_description);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        GetResourcesResult.BaikeItem resourceItem = resourceItemList.get(position);
        holder.iv_pic.setImageBitmap(null);
        ImageLoader.getInstance().displayImage(Constant.Api.GOLD_DOMAIN + resourceItem.pic, holder.iv_pic);
        holder.tv_name.setText(resourceItem.entry);
        holder.tv_time.setText(resourceItem.created_time);
        holder.tv_description.setText(resourceItem.summary);
        resourceItem = null;
        return convertView;
    }

    private void showPic(ImageView iamgeView, String avatar) {
        final String url_avatar = avatar;
        iamgeView.setTag(url_avatar);
        if (url_avatar != null && !url_avatar.equals("")) {
            Bitmap bitmap = avatarLoader.loadImage(iamgeView, url_avatar,
                    new LoadUserAvatar.ImageDownloadedCallBack() {

                        public void onImageDownloaded(ImageView imageView, Bitmap bitmap) {
                            if (imageView.getTag() == url_avatar) {
                                imageView.setImageBitmap(bitmap);

                            }
                        }

                    });
            if (bitmap != null)
                iamgeView.setImageBitmap(bitmap);

        }
    }

    private class ViewHolder {
        SketchImageView iv_pic;
        TextView tv_description;
        TextView tv_name;
        TextView tv_time;
    }
}