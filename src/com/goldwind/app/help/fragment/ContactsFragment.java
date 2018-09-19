package com.goldwind.app.help.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldwind.app.help.BaseFragment;
import com.goldwind.app.help.R;
import com.goldwind.app.help.adapter.ContactHomeAdapter;
import com.goldwind.app.help.model.ContactBean;
import com.goldwind.app.help.view.QuickAlphabeticBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 本地
 */
public class ContactsFragment extends BaseFragment {
    @Bind(R.id.acbuwa_list)
    ListView personList;
    @Bind(R.id.fast_scroller)
    QuickAlphabeticBar alpha;
    @Bind(R.id.rl_top_menu)
    RelativeLayout rlTopMenu;

    @Bind(R.id.iv_pic_anim)
    ImageView iv_pic_anim;
    @Bind(R.id.view_white)
    View view_white;

    private ContactHomeAdapter adapter;
    private List<ContactBean> list;
    private AsyncQueryHandler asyncQuery;

    private Map<Integer, ContactBean> contactIdMap = null;

    private Animation alphaAnimation;

    private Handler mHandler;

    private boolean isSnackBar = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        ButterKnife.bind(this, view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rlTopMenu.getLayoutParams();
            int h = Resources.getSystem().getDimensionPixelSize(Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
            layoutParams.topMargin = h;
            rlTopMenu.setBackgroundResource(R.color.c7);
            rlTopMenu.setLayoutParams(layoutParams);
            isSnackBar = true;
        }

        initAll();

        System.gc();
        System.gc();

        return view;
    }

    @Override
    public void onResume() {
        view_white.setVisibility(View.GONE);
        iv_pic_anim.setVisibility(View.GONE);
        System.gc();
        System.gc();
        super.onResume();
    }

    @Override
    public void onDestroy() {


        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.gc();
        System.gc();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    protected void initParam() {
        asyncQuery = new MyAsyncQueryHandler(this.getActivity().getContentResolver());

        mHandler = new Handler();
        alphaAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.alpha);
    }


    @Override
    protected void initViews() {

        TextView emptyView = new TextView(getActivity());
        emptyView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        emptyView.setText("暂无联系人");
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) personList.getParent()).addView(emptyView);
        personList.setEmptyView(emptyView);

    }

    @Override
    protected void initListener() {

        alphaAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                iv_pic_anim.setVisibility(View.VISIBLE);
                view_white.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv_pic_anim.setVisibility(View.GONE);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view_white.setVisibility(View.GONE);
                    }
                }, 400);
            }
        });

    }

    @Override
    protected void initData() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人的Uri
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.DATA1,
                "sort_key",
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY
        }; // 查询的列
        asyncQuery.startQuery(0, null, uri, projection, null, null,
                "sort_key COLLATE LOCALIZED asc"); // 按照sort_key升序查询
    }

    private void setAdapter(List<ContactBean> list) {
        adapter = new ContactHomeAdapter(this.getActivity(), list, alpha);
        personList.setAdapter(adapter);
        alpha.init(this.getActivity());
        alpha.setListView(personList);
        alpha.setHight(alpha.getHeight());
        alpha.setVisibility(View.VISIBLE);
        personList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactBean cb = (ContactBean) adapter.getItem(position);
                String toPhone = cb.getPhoneNum();
                Uri uri = Uri.parse("tel:" + toPhone);
                Intent it = new Intent(Intent.ACTION_CALL, uri);
                startActivity(it);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 数据库异步查询类AsyncQueryHandler
     *
     * @author administrator
     */
    private class MyAsyncQueryHandler extends AsyncQueryHandler {

        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        /**
         * 查询结束的回调函数
         */
        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {

                contactIdMap = new HashMap<Integer, ContactBean>();

                list = new ArrayList<ContactBean>();
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String name = cursor.getString(1);
                    String number = cursor.getString(2);
                    String sortKey = cursor.getString(3);
                    int contactId = cursor.getInt(4);
                    Long photoId = cursor.getLong(5);
                    String lookUpKey = cursor.getString(6);

                    if (contactIdMap.containsKey(contactId)) {

                    } else {

                        ContactBean cb = new ContactBean();
                        cb.setDisplayName(name);
//					if (number.startsWith("+86")) {// 去除多余的中国地区号码标志，对这个程序没有影响。
//						cb.setPhoneNum(number.substring(3));
//					} else {
                        cb.setPhoneNum(number);
//					}
                        cb.setSortKey(sortKey);
                        cb.setContactId(contactId);
                        cb.setPhotoId(photoId);
                        cb.setLookUpKey(lookUpKey);
                        list.add(cb);

                        contactIdMap.put(contactId, cb);

                    }
                }
                if (list.size() > 0) {
                    setAdapter(list);
                }
            }
        }

    }
}
