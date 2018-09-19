package com.goldwind.app.help.fragment;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldwind.app.help.BaseFragment;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.activity.DeleteActivity;
import com.goldwind.app.help.activity.NewUpdateActivity;
import com.goldwind.app.help.activity.SearchActivity;
import com.goldwind.app.help.db.MyDB;
import com.goldwind.app.help.model.GetResourcesResult.ResourceItem;
import com.goldwind.app.help.util.ApiUtil;
import com.goldwind.app.help.util.CommonUtil;
import com.goldwind.app.help.util.LogUtil;
import com.goldwind.app.help.view.MyGridViewDown;
import com.goldwind.app.help.view.MyGridViewUP;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.xiaopan.sketch.SketchImageView;

/**
 * 本地
 */
public class LocalFragment extends BaseFragment {
    @Bind(R.id.iv_back_img)
    ImageView backImg;
    @Bind(R.id.tv_title)
    TextView tv_title;
    @Bind(R.id.iv_search)
    ImageView ivSearch;
    @Bind(R.id.gv_top)
    GridView gvTop;
    //	@Bind(R.id.gv_all)
    MyGridViewDown gvAll;
    //	@Bind(R.id.gv_all_1)
    MyGridViewUP gvAll_1;
    @Bind(R.id.iv_class)
    ImageView ivClass;
    @Bind(R.id.iv_class_1)
    ImageView ivClass_1;
    @Bind(R.id.ll_bottom)
    LinearLayout llBottom;
    @Bind(R.id.ll_bottom_1)
    LinearLayout llBottom_1;
    PopupWindow popupWindow;
    @Bind(R.id.rl_top_menu)
    RelativeLayout rlTopMenu;

    @Bind(R.id.iv_pic_anim)
    ImageView iv_pic_anim;
    @Bind(R.id.view_white)
    View view_white;

    // 分类名
    private String className = "全部";
    private ArrayList<ResourceItem> fileList;
    private ArrayList<ResourceItem> tuiJianList;
    private AllItemAdapter allItemAdapter;
    private TopItemAdapter topItemAdapter;

    private PopupWindow deleteDialogPopupWindow;
    private ResourceItem deleteResourceItem;

    private Animation alphaAnimation;

    private Handler mHandler;

    private boolean isSnackBar = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local, container, false);
        ButterKnife.bind(this, view);
        gvAll_1 = (MyGridViewUP) view.findViewById(R.id.gv_all_1);
        gvAll = (MyGridViewDown) view.findViewById(R.id.gv_all);


        initAll();


        return view;
    }

    @Override
    public void onResume() {
        refreshData();
        view_white.setVisibility(View.GONE);
        iv_pic_anim.setVisibility(View.GONE);
        allItemAdapter.notifyDataSetChanged();
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
        if (requestCode == 1111) {
            topItemAdapter.notifyDataSetChanged();
        } else if (requestCode == 1122) {
        }
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
        fileList = new ArrayList<ResourceItem>();
        tuiJianList = new ArrayList<ResourceItem>();
        mHandler = new Handler();
        alphaAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.alpha);
    }

    private void refreshData() {
        LogUtil.d("refreshData");

        if (Constant.getCurrentUser(getActivity().getApplicationContext()) == null) {
            return;
        }
        if (getActivity() == null) {
            return;
        }
        if (getActivity().getApplicationContext() == null) {
            return;
        }
        if (TextUtils.isEmpty(Constant.getCurrentUser(getActivity().getApplicationContext()).role)) {
            return;
        }

        // 底部已经下载的资源
        fileList.clear();
        fileList.addAll(MyDB.getInstance(getActivity().getApplicationContext()).getReadResourceList(
                Constant.getCurrentUser(getActivity().getApplicationContext()).staffid, getType(),
                Constant.getCurrentUser(getActivity().getApplicationContext()).role + ""));

        // 顶部的两个推荐的资源
        tuiJianList.clear();
        tuiJianList.addAll(MyDB.getInstance(getActivity().getApplicationContext()).getTuiJianResourceList(
                Constant.getCurrentUser(getActivity().getApplicationContext()).staffid));
    }

    public void refreshDataAndNotify() {
        try {
            refreshData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (allItemAdapter != null) {
            allItemAdapter.notifyDataSetChanged();
        }
        if (topItemAdapter != null) {
            topItemAdapter.notifyDataSetChanged();
        }
    }

    private int getType() {
        if (TextUtils.equals("文档", className)) {
            return 1;
        } else if (TextUtils.equals("视频", className)) {
            return 2;
        } else if (TextUtils.equals("图片", className)) {
            return 3;
        } else {
            return 0;
        }
    }

    @Override
    protected void initViews() {
        gvAll.setLlBottom(llBottom_1);
        gvAll_1.setLlBottom(llBottom_1);
        gvAll_1.setMyGridViewDown(gvAll);

        TextView emptyView = new TextView(getActivity());
        emptyView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        emptyView.setText("暂无资源");
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) gvAll.getParent()).addView(emptyView);
        gvAll.setEmptyView(emptyView);

        TextView emptyView_1 = new TextView(getActivity());
        emptyView_1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        emptyView_1.setText("暂无资源");
        emptyView_1.setGravity(Gravity.CENTER);
        emptyView_1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        emptyView_1.setVisibility(View.GONE);
        ((ViewGroup) gvAll_1.getParent()).addView(emptyView_1);
        gvAll_1.setEmptyView(emptyView_1);
    }

    @Override
    protected void initListener() {
        ivSearch.setOnClickListener(this);
        ivClass.setOnClickListener(this);
        ivClass_1.setOnClickListener(this);
        llBottom_1.setOnClickListener(this);
        backImg.setOnClickListener(this);
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

//		tv_title.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				getActivity().startActivity(new Intent(getActivity(), ShowDownLoadActivity.class));
//			}
//		});

        gvAll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!gvAll.isClick()) {
                    return;
                }

                final View v = view.findViewById(R.id.rl_item);
                PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.1f, 1f);
                PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.1f, 1f);
                ObjectAnimator.ofPropertyValuesHolder(v, pvhX, pvhY).setDuration(1000).start();

                ResourceItem resourceItem = (ResourceItem) parent.getItemAtPosition(position);
//				openResource(resourceItem);
            }
        });

        gvAll_1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!gvAll_1.isClick()) {
                    return;
                }


                final View v = view.findViewById(R.id.rl_item);
                PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.1f, 1f);
                PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.1f, 1f);
                ObjectAnimator.ofPropertyValuesHolder(v, pvhX, pvhY).setDuration(1000).start();
                ResourceItem resourceItem = (ResourceItem) parent.getItemAtPosition(position);
//				openResource(resourceItem);
            }
        });

        gvAll.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!gvAll.isClick()) {
                    return true;
                }

                final View v = view.findViewById(R.id.rl_item);
                deleteResourceItem = (ResourceItem) parent.getItemAtPosition(position);
                PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.1f, 1f);
                PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.1f, 1f);
                ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(v, pvhX, pvhY);
                objectAnimator.addListener(new AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Intent intent = null;
                        try {
                            intent = new Intent(getActivity(), DeleteActivity.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (intent != null) {
                            intent.putExtra("deleteResourceItem", deleteResourceItem);
                            startActivityForResult(intent, 1122);
                            getActivity().overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_right_out);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                });

                objectAnimator.setDuration(500).start();
                return true;
            }
        });
        gvAll_1.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!gvAll_1.isClick()) {
                    return true;
                }


                final View v = view.findViewById(R.id.rl_item);
                deleteResourceItem = (ResourceItem) parent.getItemAtPosition(position);
                PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.1f, 1f);
                PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.1f, 1f);
                ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(v, pvhX, pvhY);
                objectAnimator.addListener(new AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Intent intent = null;
                        try {
                            intent = new Intent(getActivity(), DeleteActivity.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (intent != null) {
                            intent.putExtra("deleteResourceItem", deleteResourceItem);
                            startActivityForResult(intent, 1122);
                            getActivity().overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_right_out);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                });

                objectAnimator.setDuration(500).start();
                return true;
            }
        });

        gvTop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    startActivityForResult(new Intent(getActivity(), NewUpdateActivity.class), 1111);
                } else {
                    final ResourceItem resourceItem = (ResourceItem) parent.getItemAtPosition(position);

                    if (resourceItem.type != 3) {
                        ImageView iv_pic = (ImageView) view.findViewById(R.id.iv_pic);
                        iv_pic.setDrawingCacheEnabled(true);
                        Bitmap obmp = Bitmap.createBitmap(iv_pic.getDrawingCache());
                        iv_pic.setDrawingCacheEnabled(false);
                        iv_pic_anim.setImageBitmap(obmp);

                        int[] location = new int[2];
                        iv_pic.getLocationOnScreen(location);
                        int aaa = CommonUtil.dip2px(getActivity().getApplicationContext(), 8);
                        int bbb = CommonUtil.dip2px(getActivity().getApplicationContext(), 10);

                        TranslateAnimation translateAnimation = new TranslateAnimation(
                                TranslateAnimation.ABSOLUTE, location[0] - aaa,
                                TranslateAnimation.ABSOLUTE, aaa,
                                TranslateAnimation.ABSOLUTE, bbb,
                                TranslateAnimation.ABSOLUTE, bbb);
                        translateAnimation.setDuration(500);

                        iv_pic_anim.startAnimation(translateAnimation);
                        view_white.startAnimation(alphaAnimation);
                        iv_pic_anim.setVisibility(View.VISIBLE);
                        view_white.setVisibility(View.VISIBLE);

                        mHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                openDetailPage(resourceItem);
                            }
                        }, 450);

                    } else {
                        openDetailPage(resourceItem);
                    }
                }
            }
        });
    }

    private void openDetailPage(ResourceItem resourceItem) {
//		// pdf
//		if (resourceItem.type == 1 && resourceItem.fileaddress.endsWith("pdf")) {
//			Intent intent = new Intent(getActivity(), PDFDetailActivity.class);
//			intent.putExtra("resourceItem", resourceItem);
//			intent.putExtra("anim", true);
//			startActivity(intent);
//			// 视频
//		} else if (resourceItem.type == 2) {
//			Intent intent = new Intent(getActivity(), VideoDetailActivity.class);
//			intent.putExtra("resourceItem", resourceItem);
//			intent.putExtra("anim", true);
//			startActivity(intent);
//			// 电子书
//		} else if (resourceItem.type == 1 && resourceItem.fileaddress.endsWith("epub")) {
//			Intent intent = new Intent(getActivity(), ResourceDetailActivity.class);
//			intent.putExtra("resourceItem", resourceItem);
//			intent.putExtra("anim", true);
//			startActivity(intent);
//		} else if (resourceItem.type == 3) {
//			Intent intent = null;
//			if (resourceItem.status > Integer.valueOf(Constant.getCurrentUser(getActivity().getApplicationContext()).role)) {
//				intent = new Intent(getActivity(), PicsDetailUnRoleActivity.class);
//			} else {
//				intent = new Intent(getActivity(), PicsDetailActivity.class);
//			}
//			intent.putExtra("resourceItem", resourceItem);
//			startActivity(intent);
//		}
    }

    @Override
    protected void initData() {
        refreshData();

        topItemAdapter = new TopItemAdapter();
        gvTop.setAdapter(topItemAdapter);
        allItemAdapter = new AllItemAdapter();


        gvAll.setAdapter(allItemAdapter);
        gvAll_1.setAdapter(allItemAdapter);


        if (allItemAdapter.getCount() > 0) {
            gvAll_1.setSelection(0);
            gvAll_1.scrollTo(0, 0);
            gvAll_1.smoothScrollToPosition(0);
            LogUtil.d("ccccccccccccccccccc");
        }


//		if (allItemAdapter.getCount() > 0) {
//			gvAll.setSelection(0);
//			gvAll_1.setSelection(0);
//		}
    }

    private void showDeleteDialogPop() {
        if (deleteDialogPopupWindow == null) {
            View view = View.inflate(getActivity(), R.layout.pop_delete_dialog, null);
            Button bt_dialog_left = (Button) view.findViewById(R.id.bt_delete_dialog_left);
            Button bt_dialog_right = (Button) view.findViewById(R.id.bt_delete_dialog_right);
            bt_dialog_left.setOnClickListener(this);
            bt_dialog_right.setOnClickListener(this);
            deleteDialogPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            deleteDialogPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            deleteDialogPopupWindow.setFocusable(true);
            deleteDialogPopupWindow.setAnimationStyle(R.style.popwin_anim_style);
        }
        deleteDialogPopupWindow.showAtLocation(llBottom, Gravity.BOTTOM, 0, 0);
    }

//	private void openVideoResource(File file, ResourceItem resourceItem) {
//		Intent intent = new Intent(getActivity(), VideoViewSubtitle.class);
//		intent.putExtra("path", file.getAbsolutePath());
//		intent.putExtra("showName", resourceItem.name);
//		startActivity(intent);
//		MyDB.getInstance(getActivity().getApplicationContext()).updateResourceReadTime(Constant.getCurrentUser(getActivity().getApplicationContext()).staffid,
//				resourceItem.resourcesid, System.currentTimeMillis());
//	}
//
//	private void openPdfResource(File file, ResourceItem resourceItem) {
//		Uri uri = Uri.fromFile(file);
//		Intent intent = new Intent(getActivity(), MuPDFActivity.class);
//		intent.setAction(Intent.ACTION_VIEW);
//		intent.setData(uri);
//		startActivity(intent);
//		MyDB.getInstance(getActivity().getApplicationContext()).updateResourceReadTime(Constant.getCurrentUser(getActivity().getApplicationContext()).staffid,
//				resourceItem.resourcesid, System.currentTimeMillis());
//	}
//
//	private void openEpubResource(File file, ResourceItem resourceItem) {
//		Book book = new Book(resourceItem.resourcesid + Integer.valueOf(Constant.getCurrentUser(getActivity().getApplicationContext()).staffid),
//				file.getAbsolutePath(), resourceItem.name, "auto", "zh");
//		FBReader.openBookActivity(getActivity(), book, null);
//		MyDB.getInstance(getActivity().getApplicationContext()).updateResourceReadTime(Constant.getCurrentUser(getActivity().getApplicationContext()).staffid,
//				resourceItem.resourcesid, System.currentTimeMillis());
//	}
//
//	private void openResource(final ResourceItem resourceItem) {
//		if (resourceItem.type == 1 && resourceItem.fileaddress.endsWith("pdf")) {
//			DecodeTask decodeTask = new DecodeTask(resourceItem, Constant.getCurrentUser(getActivity().getApplicationContext()).staffid, new DecodeListener() {
//
//				@Override
//				public void onPreExecute() {
//					MyProgressDialog.showDialog(getActivity(), "正在解密", false);
//				}
//
//				@Override
//				public void onPostExecute(Boolean result, File decodeFile) {
//					MyProgressDialog.closeDialog();
//					if (result) {
//						openPdfResource(decodeFile, resourceItem);
//					} else {
//						if (getActivity() == null || getActivity().getApplicationContext() == null) {
//							return;
//						}
//						new SnackBar.Builder(getActivity(),isSnackBar).withMessage("解密失败").withDuration(SnackBar.SHORT_SNACK).show();
//					}
//				}
//			});
//			if (!decodeTask.getSourceFile().exists()) {
//				if (getActivity() == null || getActivity().getApplicationContext() == null) {
//					return;
//				}
//				new SnackBar.Builder(getActivity(),isSnackBar).withMessage("该资源没有下载完成").withDuration(SnackBar.SHORT_SNACK).show();
//				return;
//			}
//			if (decodeTask.getDecodeFile().exists()) {
//				openPdfResource(decodeTask.getDecodeFile(), resourceItem);
//				return;
//			}
//			decodeTask.execute();
//		} else if (resourceItem.type == 1 && resourceItem.fileaddress.endsWith("epub")) {
//			DecodeTask decodeTask = new DecodeTask(resourceItem, Constant.getCurrentUser(getActivity().getApplicationContext()).staffid, new DecodeListener() {
//				@Override
//				public void onPreExecute() {
//					MyProgressDialog.showDialog(getActivity(), "正在解密", false);
//				}
//
//				@Override
//				public void onPostExecute(Boolean result, File decodeFile) {
//					MyProgressDialog.closeDialog();
//					if (result) {
//						openEpubResource(decodeFile, resourceItem);
//					} else {
//						if (getActivity() == null || getActivity().getApplicationContext() == null) {
//							return;
//						}
//						new SnackBar.Builder(getActivity(),isSnackBar).withMessage("解密失败").withDuration(SnackBar.SHORT_SNACK).show();
//					}
//				}
//			});
//			if (!decodeTask.getSourceFile().exists()) {
//				if (getActivity() == null || getActivity().getApplicationContext() == null) {
//					return;
//				}
//				new SnackBar.Builder(getActivity(),isSnackBar).withMessage("该资源没有下载完成").withDuration(SnackBar.SHORT_SNACK).show();
//				return;
//			}
//			if (decodeTask.getDecodeFile().exists()) {
//				openEpubResource(decodeTask.getDecodeFile(), resourceItem);
//				return;
//			}
//			decodeTask.execute();
//		} else if (resourceItem.type == 2) {
//			DecodeTask decodeTask = new DecodeTask(resourceItem, Constant.getCurrentUser(getActivity().getApplicationContext()).staffid, new DecodeListener() {
//
//				@Override
//				public void onPreExecute() {
//					MyProgressDialog.showDialog(getActivity(), "正在解密", false);
//				}
//
//				@Override
//				public void onPostExecute(Boolean result, File decodeFile) {
//					MyProgressDialog.closeDialog();
//					if (result) {
//						openVideoResource(decodeFile, resourceItem);
//					} else {
//						if (getActivity() == null || getActivity().getApplicationContext() == null) {
//							return;
//						}
//						new SnackBar.Builder(getActivity(),isSnackBar).withMessage("解密失败").withDuration(SnackBar.SHORT_SNACK).show();
//					}
//				}
//			});
//			if (!decodeTask.getSourceFile().exists()) {
//				if (getActivity() == null || getActivity().getApplicationContext() == null) {
//					return;
//				}
//				new SnackBar.Builder(getActivity(),isSnackBar).withMessage("该资源没有下载完成").withDuration(SnackBar.SHORT_SNACK).show();
//				return;
//			}
//			if (decodeTask.getDecodeFile().exists()) {
//				openVideoResource(decodeTask.getDecodeFile(), resourceItem);
//				return;
//			}
//			decodeTask.execute();
//		} else if (resourceItem.type == 3) {
////
//
//		}
//	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.bt_delete_dialog_left:
                if (deleteDialogPopupWindow != null) {
                    deleteDialogPopupWindow.dismiss();
                }
                break;
            case R.id.bt_delete_dialog_right:
                if (deleteResourceItem != null) {
                    MyDB.getInstance(getActivity().getApplicationContext()).updateResourceReadTime(
                            Constant.getCurrentUser(getActivity().getApplicationContext()).staffid, deleteResourceItem.resourcesid, 0L);
                    deleteResourceItem = null;
                    refreshData();
                    allItemAdapter.notifyDataSetChanged();
                }
                if (deleteDialogPopupWindow != null) {
                    deleteDialogPopupWindow.dismiss();
                }
                break;
            case R.id.iv_class:
            case R.id.iv_class_1:
                View view = View.inflate(getActivity(), R.layout.layout_resource_class, null);
                if (popupWindow == null) {
                    popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    popupWindow.setFocusable(true);
                }

                final LinearLayout ll_resource_class = (LinearLayout) popupWindow.getContentView().findViewById(R.id.ll_resource_class);
                for (int i = 0; i < ll_resource_class.getChildCount(); i++) {
                    TextView textView = (TextView) ll_resource_class.getChildAt(i);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            className = ((TextView) v).getText().toString();
                            for (int i = 0; i < ll_resource_class.getChildCount(); i++) {
                                TextView textView = (TextView) ll_resource_class.getChildAt(i);
                                if (TextUtils.equals(textView.getText(), className)) {
                                    textView.setTextColor(0xffBF6319);
                                } else {
                                    textView.setTextColor(0xffffffff);
                                }
                            }
                            popupWindow.dismiss();
                            refreshData();
                            allItemAdapter.notifyDataSetChanged();
                        }
                    });
                    if (TextUtils.equals(textView.getText(), className)) {
                        textView.setTextColor(0xffBF6319);
                    } else {
                        textView.setTextColor(0xffffffff);
                    }
                }
                popupWindow.showAsDropDown(v, -100, 10);
                break;

            case R.id.iv_back_img:
                getActivity().finish();
                ;
                break;
        }
    }

    private class TopItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (tuiJianList.size() < 2) {
                return 1 + tuiJianList.size();
            }
            return 3;
        }

        @Override
        public Object getItem(int position) {
            if (position > 0) {
                return tuiJianList.get(position - 1);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            System.gc();
            System.gc();
            View view = View.inflate(getActivity(), R.layout.layout_item_resource_1, null);
            if (position == 0) {
                TextView tv_num = (TextView) view.findViewById(R.id.tv_num);
                tv_num.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(Constant.getCurrentUser(getActivity().getApplicationContext()).staffid)) {
                    int num = Constant.newUpdate;
                    CommonUtil.spPutInt(getActivity().getApplicationContext(),
                            Constant.getCurrentUser(getActivity().getApplicationContext()).staffname + "_newUpdate",
                            Constant.newUpdate);
                    if (num != 0) {
                        tv_num.setText(num + "");
                    } else {
                        tv_num.setVisibility(View.INVISIBLE);
                    }
                } else {
                    tv_num.setVisibility(View.INVISIBLE);
                }
                view.findViewById(R.id.iv_type_tag).setVisibility(View.GONE);
                ((ImageView) view.findViewById(R.id.iv_video_tag)).setVisibility(View.GONE);
                view.findViewById(R.id.fl_pic).setBackgroundDrawable(null);
                ((SketchImageView) view.findViewById(R.id.iv_pic)).setImageResource(R.drawable.ss1);
                ((TextView) view.findViewById(R.id.tv_name)).setText("最新更新");
                ((TextView) view.findViewById(R.id.tv_name)).setGravity(Gravity.CENTER_HORIZONTAL);
            }
            if (position == 1 || position == 2) {
                ResourceItem resourceItem = tuiJianList.get(position - 1);
                if (resourceItem.type == 3) {
                    ApiUtil.downloadPic(resourceItem.fileaddress.split(",")[0], (SketchImageView) view.findViewById(R.id.iv_pic));
                } else {
                    ApiUtil.downloadFengMian(resourceItem.cover, (SketchImageView) view.findViewById(R.id.iv_pic));
                }
                ((TextView) view.findViewById(R.id.tv_name)).setText(resourceItem.name);
                ((TextView) view.findViewById(R.id.tv_name)).setGravity(Gravity.CENTER_HORIZONTAL);
                if (resourceItem.type == 2) {
                    ((ImageView) view.findViewById(R.id.iv_video_tag)).setVisibility(View.VISIBLE);
                    ((ImageView) view.findViewById(R.id.iv_type_tag)).setImageResource(R.drawable.img_video_tag_font);
                } else if (resourceItem.type == 1) {
                    ((ImageView) view.findViewById(R.id.iv_video_tag)).setVisibility(View.GONE);
                    ((ImageView) view.findViewById(R.id.iv_type_tag)).setImageResource(R.drawable.img_doc_tag);
                } else if (resourceItem.type == 3) {
                    ((ImageView) view.findViewById(R.id.iv_video_tag)).setVisibility(View.GONE);
                    ((ImageView) view.findViewById(R.id.iv_type_tag)).setImageResource(R.drawable.img_pic_tag);
                }
            }
            return view;
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
            ResourceItem resourceItem = fileList.get(position);

            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.layout_item_resource, null);
                ViewHolder holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();

            holder.iv_pic.setImageBitmap(null);
            holder.iv_pic.setBackgroundDrawable(null);

            if (resourceItem.type == 3 && TextUtils.isEmpty(resourceItem.cover)) {
                ApiUtil.downloadPic(resourceItem.fileaddress.split(",")[0], holder.iv_pic);
            } else if (resourceItem.type != 3 && !TextUtils.isEmpty(resourceItem.cover)) {
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
            if (position == getCount() - 1) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.tv_name.getLayoutParams();
                layoutParams.bottomMargin = CommonUtil.dip2px(getActivity(), 60f);
                holder.tv_name.setLayoutParams(layoutParams);
                gvAll_1.setLastView(convertView);
            } else {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.tv_name.getLayoutParams();
                layoutParams.bottomMargin = CommonUtil.dip2px(getActivity(), 0f);
                holder.tv_name.setLayoutParams(layoutParams);
            }

            return convertView;
        }

        private class ViewHolder {
            private SketchImageView iv_pic;
            private ImageView iv_video_tag;
            private ImageView iv_type_tag;
            private TextView tv_name;

            public ViewHolder(View view) {
                iv_video_tag = (ImageView) view.findViewById(R.id.iv_video_tag);
                iv_pic = (SketchImageView) view.findViewById(R.id.iv_pic);
                iv_type_tag = (ImageView) view.findViewById(R.id.iv_type_tag);
                tv_name = (TextView) view.findViewById(R.id.tv_name);
            }
        }
    }
}
