package com.goldwind.app.help.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldwind.app.help.BaseActivity;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.db.MyDB;
import com.goldwind.app.help.model.GetResourcesResult.ResourceItem;
import com.goldwind.app.help.util.ApiUtil;
import com.goldwind.app.help.view.SildingFinishLayout;
import com.goldwind.app.help.view.SildingFinishLayout.OnSildingFinishListener;

import java.util.ArrayList;

import me.xiaopan.sketch.SketchImageView;

/**
 * 删除资源
 */
public class DeleteActivity extends BaseActivity {

    private int staffidNum = 0;

    private SildingFinishLayout sildingFinishLayout;
    private RelativeLayout rl_top_menu;
    private ImageView iv_back;
    private TextView tv_title;
    private GridView gv_content;
    private Button bt_delete_right;
    private ArrayList<ResourceItem> fileList;
    private AllItemAdapter allItemAdapter;
    private ArrayList<ResourceItem> deleteList;

    private boolean isSnackBar = false;

    private PopupWindow dialogPopupWindow;

    private ReceiveBroadCast mReceiveBroadCast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detete);
        initAll();
        sildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {
            @Override
            public void onSildingFinish() {
                finish();
            }
        });

        sildingFinishLayout.setTouchView(gv_content);

        mReceiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("DeleteActivityBroadCast");
        registerReceiver(mReceiveBroadCast, filter);
    }

    @Override
    protected void onDestroy() {
        if (mReceiveBroadCast != null) {
            try {
                unregisterReceiver(mReceiveBroadCast);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    @Override
    protected void initParam() {
        staffidNum = MyDB.getInstance(getApplicationContext()).getStaffidNum();

        ResourceItem deleteResourceItem = (ResourceItem) getIntent().getSerializableExtra("deleteResourceItem");

        fileList = new ArrayList<ResourceItem>();
        allItemAdapter = new AllItemAdapter();
        deleteList = new ArrayList<ResourceItem>();

        deleteList.add(deleteResourceItem);
    }

    private void refreshData() {
        if (Constant.getCurrentUser(getApplicationContext()) == null) {
            return;
        }
        // 底部已经下载的资源
        fileList.clear();
        fileList.addAll(MyDB.getInstance(getApplicationContext()).getReadResourceList(Constant.getCurrentUser(getApplicationContext()).staffid, 0,
                Constant.getCurrentUser(getApplicationContext()) + ""));
    }

    @Override
    protected void initViews() {
        sildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout);
        rl_top_menu = (RelativeLayout) findViewById(R.id.rl_top_menu);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        gv_content = (GridView) findViewById(R.id.gv_content);
        bt_delete_right = (Button) findViewById(R.id.bt_delete_right);
    }

    @Override
    protected void initListener() {
        iv_back.setOnClickListener(this);
        bt_delete_right.setOnClickListener(this);
        gv_content.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cb_delete = (CheckBox) view.findViewById(R.id.cb_delete);
                if (cb_delete.isChecked()) {
                    cb_delete.setChecked(false);
                } else {
                    cb_delete.setChecked(true);
                }
            }
        });
    }

    private boolean isInDeleteList(ResourceItem resourceItem) {
        for (ResourceItem item : deleteList) {
            if (resourceItem.resourcesid == item.resourcesid) {
                return true;
            }
        }
        return false;
    }

    private ResourceItem findInDeleteList(ResourceItem resourceItem) {
        for (ResourceItem item : deleteList) {
            if (resourceItem.resourcesid == item.resourcesid) {
                return item;
            }
        }
        return null;
    }

    @Override
    protected void initData() {
        refreshData();
        tv_title.setText("已选中" + deleteList.size() + "项");
        gv_content.setAdapter(allItemAdapter);
    }

    private void showDialogPop() {
        if (dialogPopupWindow == null) {
            View view = View.inflate(this, R.layout.pop_delete_dialog, null);
            Button bt_dialog_left = (Button) view.findViewById(R.id.bt_delete_dialog_left);
            Button bt_dialog_right = (Button) view.findViewById(R.id.bt_delete_dialog_right);
            bt_dialog_left.setOnClickListener(this);
            bt_dialog_right.setOnClickListener(this);
            dialogPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            dialogPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            dialogPopupWindow.setFocusable(true);
            dialogPopupWindow.setAnimationStyle(R.style.popwin_anim_style);
        }
        dialogPopupWindow.showAtLocation(gv_content, Gravity.CENTER, 0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: {
                finish();
                break;
            }
            case R.id.bt_delete_dialog_left: {
                if (dialogPopupWindow != null) {
                    dialogPopupWindow.dismiss();
                }
                break;
            }
            case R.id.bt_delete_dialog_right: {
                if (dialogPopupWindow != null) {
                    dialogPopupWindow.dismiss();
                }
                if (deleteList == null || deleteList.size() == 0) {
                    if (getApplicationContext()
                            == null) {
                        return;
                    }
                    return;
                }

                refreshData();
                deleteList.clear();
                allItemAdapter.notifyDataSetChanged();
                tv_title.setText("已选中" + deleteList.size() + "项");
                break;
            }
            case R.id.bt_delete_right: {
                if (deleteList == null || deleteList.size() == 0) {
                    if (getApplicationContext()
                            == null) {
                        return;
                    }
                    return;
                }
                showDialogPop();
                break;
            }
            default:
                break;
        }
    }

    private class AllItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return fileList.size();
        }

        @Override
        public Object getItem(int position) {
            return fileList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(DeleteActivity.this, R.layout.layout_item_resource_delete, null);
                ViewHolder holder = new ViewHolder();
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.iv_type_tag = (ImageView) convertView.findViewById(R.id.iv_type_tag);
                holder.iv_pic = (SketchImageView) convertView.findViewById(R.id.iv_pic);
                holder.iv_video_tag = (ImageView) convertView.findViewById(R.id.iv_video_tag);
                holder.cb_delete = (CheckBox) convertView.findViewById(R.id.cb_delete);
                convertView.setTag(holder);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            ResourceItem resourceItem = fileList.get(position);

            holder.cb_delete.setOnCheckedChangeListener(new MyOnCheckedChangeListener(resourceItem));

            if (isInDeleteList(resourceItem)) {
                holder.cb_delete.setChecked(true);
            } else {
                holder.cb_delete.setChecked(false);
            }

            holder.iv_pic.setImageBitmap(null);

            if (resourceItem.type == 3) {
                ApiUtil.downloadPic(resourceItem.fileaddress.split(",")[0], holder.iv_pic);
            } else {
                ApiUtil.downloadFengMian(resourceItem.cover, holder.iv_pic);
            }

            holder.tv_name.setText(resourceItem.name);
            holder.tv_name.setTextColor(0xff4f4f4f);

            if (resourceItem.type == 2) {
                holder.iv_video_tag.setVisibility(View.VISIBLE);
                holder.iv_type_tag.setImageResource(R.drawable.img_video_tag_font);
            } else if (resourceItem.type == 1) {
                holder.iv_video_tag.setVisibility(View.GONE);
                holder.iv_type_tag.setImageResource(R.drawable.img_doc_tag);
            } else if (resourceItem.type == 3) {
                holder.iv_video_tag.setVisibility(View.GONE);
                holder.iv_type_tag.setImageResource(R.drawable.img_pic_tag);
            }
            return convertView;
        }

        class MyOnCheckedChangeListener implements OnCheckedChangeListener {
            private ResourceItem resourceItem;

            public MyOnCheckedChangeListener(ResourceItem resourceItem) {
                this.resourceItem = resourceItem;
            }

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!isInDeleteList(resourceItem)) {
                        deleteList.add(resourceItem);
                    }
                } else {
                    if (isInDeleteList(resourceItem)) {
                        deleteList.remove(findInDeleteList(resourceItem));
                    }
                }
                tv_title.setText("已选中" + deleteList.size() + "项");
            }
        }

        class ViewHolder {
            ImageView iv_video_tag;
            ImageView iv_type_tag;
            SketchImageView iv_pic;
            TextView tv_name;
            CheckBox cb_delete;
        }

    }

    public class ReceiveBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("data");
            if (TextUtils.equals(message, "RoleChange")) {
                if (getApplicationContext()
                        == null) {
                    return;
                }
            }
        }
    }
}
