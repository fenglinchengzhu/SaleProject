package com.goldwind.app.help.service;
//package com.goldwind.app.help.service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.goldwind.app.help.BaseActivity;
//import com.goldwind.app.help.R;
//import com.goldwind.app.help.download.DownloadTask;
//import com.goldwind.app.help.download.DownloadTaskListener;
//import com.goldwind.app.help.download.StorageUtils;
//import com.goldwind.app.help.service.JinFengDownloadService.TaskQueue;
//import com.goldwind.app.help.util.LogUtil;
//
//public class ShowDownLoadActivity extends BaseActivity {
//
//	private JinFengDownloadService mService;
//	private DownloadTaskListener mDownloadTaskListener;
//
//	// （其他）
//	private TaskQueue mTaskQueue;
//	// （下载）
//	private List<DownloadTask> mDownloadingTasks;
//	// （暂停）
//	private List<DownloadTask> mPausingTasks;
//	
//	private ArrayList<TaskInfoBean> dataList;
//	
//	private ListView lv_download;
//	
//	private MyAdapter myAdapter;
//	
//	private ServiceConnection connection;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_show_download);
//		initAll();
//		LogUtil.w("ShowDownLoadActivity onCreate");
//	}
//	
//	@Override
//	protected void onDestroy() {
//		LogUtil.w("ShowDownLoadActivity onDestroy");
//		unbindService(connection);
//		mService.setmOutDownloadTaskListener(null);
//		mDownloadTaskListener = null;
//		mTaskQueue = null;
//		mDownloadingTasks = null;
//		mPausingTasks = null;
//		dataList = null;
//		connection = null;
//		mService = null;
//		super.onDestroy();
//	}
//
//	@Override
//	protected void initParam() {
//		binderService();
//		mDownloadTaskListener = new DownloadTaskListener() {
//
//			@Override
//			public void updateProcess(DownloadTask task) {
//				for (TaskInfoBean taskInfoBean : dataList) {
//					if (TextUtils.equals(task.getUrl(), taskInfoBean.url)) {
//						taskInfoBean.downloadPercent = task.getDownloadPercent() + "%";
//						taskInfoBean.totalSize = StorageUtils.size(task.getTotalSize());
//						taskInfoBean.networkSpeed = StorageUtils.size(task.getDownloadSpeed() * 1000L) + "/秒";
//						break;
//					}
//				}
//				myAdapter.notifyDataSetChanged();
//			}
//
//			@Override
//			public void preDownload(DownloadTask task) {
//				for (TaskInfoBean taskInfoBean : dataList) {
//					if (TextUtils.equals(task.getId(), taskInfoBean.id)) {
//						taskInfoBean.state = "下载中";
//						taskInfoBean.downloadPercent = "0%";
//						taskInfoBean.totalSize = "未知";
//						taskInfoBean.networkSpeed = "未知";
//						break;
//					}
//				}
//				myAdapter.notifyDataSetChanged();
//			}
//
//			@Override
//			public void finishDownload(DownloadTask task) {
//				TaskInfoBean taskInfoBean1 = null;
//				for (TaskInfoBean taskInfoBean : dataList) {
//					if (TextUtils.equals(task.getId(), taskInfoBean.id)) {
//						taskInfoBean1 = taskInfoBean;
//						break;
//					}
//				}
//				if (taskInfoBean1 != null) {
//					dataList.remove(taskInfoBean1);
//				}
//				myAdapter.notifyDataSetChanged();
//			}
//
//			@Override
//			public void errorDownload(DownloadTask task, Throwable error) {
//				TaskInfoBean taskInfoBean1 = null;
//				for (TaskInfoBean taskInfoBean : dataList) {
//					if (TextUtils.equals(task.getId(), taskInfoBean.id)) {
//						taskInfoBean1 = taskInfoBean;
//						break;
//					}
//				}
//				if (taskInfoBean1 != null) {
//					dataList.remove(taskInfoBean1);
//				}
//				myAdapter.notifyDataSetChanged();
//			}
//		};
//	}
//
//	@Override
//	protected void initViews() {
//		lv_download = (ListView) findViewById(R.id.lv_download);
//	}
//
//	@Override
//	protected void initListener() {
//	}
//
//	@Override
//	protected void initData() {
//
//	}
//
//	private void binderService() {
//		Intent intent = new Intent(this, JinFengDownloadService.class);
//		connection = new ServiceConnection() {
//			@Override
//			public void onServiceConnected(ComponentName componentName, IBinder binder) {
//				LogUtil.w("===>onServiceConnected");
//				mService = ((JinFengDownloadService.LocalBinder) binder).getService();
//				mTaskQueue = mService.getmTaskQueue();
//				mDownloadingTasks = mService.getmDownloadingTasks();
//				mPausingTasks = mService.getmPausingTasks();
//				dataList = new ArrayList<TaskInfoBean>();
//				fillDataList();
//				myAdapter = new MyAdapter();
//				lv_download.setAdapter(myAdapter);
//				mService.setmOutDownloadTaskListener(mDownloadTaskListener);
//			}
//
//			@Override
//			public void onServiceDisconnected(ComponentName componentName) {
//				LogUtil.w("===>onServiceDisconnected");
//			}
//		};
//		bindService(intent, connection, Context.BIND_AUTO_CREATE);
//	}
//	
//	private void fillDataList(){
//		LogUtil.w("===>fillDataList");
//		dataList.clear();
//		for (DownloadTask task : mDownloadingTasks) {
//			TaskInfoBean taskInfoBean = new TaskInfoBean();
//			taskInfoBean.state = "下载中";
//			taskInfoBean.id = task.getId();
//			taskInfoBean.name = task.getName();
//			taskInfoBean.path = task.getFile().getAbsolutePath();
//			taskInfoBean.url = task.getUrl();
//			taskInfoBean.downloadPercent = task.getDownloadPercent() + "%";
//			taskInfoBean.totalSize = StorageUtils.size(task.getTotalSize());
//			taskInfoBean.networkSpeed = StorageUtils.size(task.getDownloadSpeed() * 1000L) + "/秒";
//			dataList.add(taskInfoBean);
//		}
//		for (int i = 0; i < mTaskQueue.size(); i++) {
//			DownloadTask task = mTaskQueue.get(i);
//			TaskInfoBean taskInfoBean = new TaskInfoBean();
//			taskInfoBean.state = "等待";
//			taskInfoBean.id = task.getId();
//			taskInfoBean.name = task.getName();
//			taskInfoBean.url = task.getUrl();
//			taskInfoBean.path = task.getFile().getAbsolutePath();
//			taskInfoBean.downloadPercent = "0%";
//			taskInfoBean.totalSize = "未知";
//			taskInfoBean.networkSpeed = "未知";
//			dataList.add(taskInfoBean);
//		}
//		for (DownloadTask task : mPausingTasks) {
//			TaskInfoBean taskInfoBean = new TaskInfoBean();
//			taskInfoBean.state = "暂停";
//			taskInfoBean.id = task.getId();
//			taskInfoBean.name = task.getName();
//			taskInfoBean.path = task.getFile().getAbsolutePath();
//			taskInfoBean.url = task.getUrl();
//			taskInfoBean.downloadPercent = task.getDownloadPercent() + "%";
//			taskInfoBean.totalSize = StorageUtils.size(task.getTotalSize());
//			taskInfoBean.networkSpeed = StorageUtils.size(task.getDownloadSpeed() * 1000L) + "/秒";
//			dataList.add(taskInfoBean);
//		}
//	}
//
//	@Override
//	public void onClick(View v) {
//		
//	}
//	
//	private class MyAdapter extends BaseAdapter{
//
//		@Override
//		public int getCount() {
//			return dataList.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return null;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return 0;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			ViewHolder holder;
//			if (convertView == null) {
//				convertView = View.inflate(ShowDownLoadActivity.this, R.layout.layout_download_task, null);
//				holder = new ViewHolder();
//				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
//				holder.tv_path = (TextView) convertView.findViewById(R.id.tv_path);
//				holder.tv_info = (TextView) convertView.findViewById(R.id.tv_info);
//				convertView.setTag(holder);
//			}
//			holder = (ViewHolder) convertView.getTag();
//			TaskInfoBean taskInfoBean = dataList.get(position);
//			holder.tv_name.setText(taskInfoBean.name);
//			holder.tv_path.setText(taskInfoBean.path);
//			String info = taskInfoBean.state + "   大小：" + taskInfoBean.totalSize + "   速度：" + taskInfoBean.networkSpeed
//					+ "   完成：" + taskInfoBean.downloadPercent;
//			holder.tv_info.setText(info);
//			return convertView;
//		}
//		
//		private class ViewHolder {
//			TextView tv_name;
//			TextView tv_path;
//			TextView tv_info;
//		}
//	}
//}
