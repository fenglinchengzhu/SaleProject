package com.goldwind.app.help.service;
//package com.goldwind.app.help.service;
//
//import java.net.MalformedURLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Queue;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import android.annotation.SuppressLint;
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Binder;
//import android.os.Build;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.text.TextUtils;
//
//import com.goldwind.app.help.Constant;
//import com.goldwind.app.help.activity.HomeActivity;
//import com.goldwind.app.help.db.MyDB;
//import com.goldwind.app.help.download.DownloadTask;
//import com.goldwind.app.help.download.DownloadTaskListener;
//import com.goldwind.app.help.model.GetResourcesResult;
//import com.goldwind.app.help.model.GetResourcesResult.ResourceItem;
//import com.goldwind.app.help.util.ApiUtil;
//import com.goldwind.app.help.util.Base64;
//import com.goldwind.app.help.util.CommonUtil;
//import com.goldwind.app.help.util.JsonUtil;
//import com.goldwind.app.help.util.LogUtil;
//import com.goldwind.app.help.util.ToastUtil;
//import com.goldwind.app.help.util.XXTEA;
//
///**
// * 资源更新服务
// */
//public class JinFengDownloadService extends Service {
//	private Timer timer;
//	private GetResourcesTask getResourcesTask;
//	private Handler mHandler;
//	private static final int TIME_MESSAGE_WHAT = 0x1000;
//	private static final int RUN_TASK_WHAT = 0x2000;
//
//	// 最多3个线程下载
//	private static final int MAX_DOWNLOAD_THREAD_COUNT = 1;
//
//	// （其他）
//	private TaskQueue mTaskQueue;
//	// （下载）
//	private List<DownloadTask> mDownloadingTasks;
//	// （暂停）
//	private List<DownloadTask> mPausingTasks;
//
//	// 是否启动
//	private Boolean isRunning = false;
//	private Boolean isRequest = true;
//	private DownloadThread downloadThread;
//	private DownloadTaskListener mDownloadTaskListener;
//	private DownloadTaskListener mOutDownloadTaskListener;
//	private IBinder binder = new LocalBinder();
//
//	private String staffid;
//
//	private ReceiveBroadCast receiveBroadCast;
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		LogUtil.w("JinFengDownloadService onBind");
//		return binder;
//	}
//
//	@Override
//	public boolean onUnbind(Intent intent) {
//		LogUtil.w("JinFengDownloadService onUnbind");
//		return super.onUnbind(intent);
//	}
//
//	@SuppressLint("HandlerLeak")
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		LogUtil.w("JinFengDownloadService onCreate");
//		mTaskQueue = new TaskQueue();
//		mDownloadingTasks = new ArrayList<DownloadTask>();
//		mPausingTasks = new ArrayList<DownloadTask>();
//		mHandler = new Handler() {
//			@Override
//			public void handleMessage(Message msg) {
//				if (msg.what == TIME_MESSAGE_WHAT) {
//					if (isRequest) {
//						requestGetResources();
//					}
//				} else if (msg.what == RUN_TASK_WHAT) {
//					DownloadTask task = (DownloadTask) msg.obj;
//					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//						task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//					} else {
//						task.execute();
//					}
//				}
//			}
//		};
//
//		mDownloadTaskListener = new DownloadTaskListener() {
//			@Override
//			public void updateProcess(DownloadTask task) {
//				if (mOutDownloadTaskListener != null) {
//					mOutDownloadTaskListener.updateProcess(task);
//				}
//			}
//
//			@Override
//			public void preDownload(DownloadTask task) {
//				LogUtil.w("preDownload ===>" + task.getName());
//				if (mOutDownloadTaskListener != null) {
//					mOutDownloadTaskListener.preDownload(task);
//				}
//			}
//
//			@Override
//			public void finishDownload(DownloadTask task) {
//				completeTask(task);
//				// 更新下载状态
//				ResourceItem resourceItem = task.getResourceItem();
//				MyDB.getInstance(getApplicationContext()).updateResourceDownloadState(resourceItem.staffid + "", resourceItem.resourcesid, 1);
//
//				Intent intent = new Intent();
//				intent.putExtra("data", "yes");
//				intent.setAction(HomeActivity.HomeActivity_FLAG);
//				sendBroadcast(intent);
//
//				LogUtil.w("finishDownload ===>" + task.getName());
//				if (mOutDownloadTaskListener != null) {
//					mOutDownloadTaskListener.finishDownload(task);
//				}
//			}
//
//			@Override
//			public void errorDownload(DownloadTask task, Throwable error) {
//				completeTask(task);
//				if (error != null) {
//					LogUtil.w("errorDownload ===>" + task.getName() + " ===>" + error.getMessage());
//				}
//				if (mOutDownloadTaskListener != null) {
//					mOutDownloadTaskListener.errorDownload(task, error);
//				}
//			}
//		};
//
//		timer = new Timer();
//		getResourcesTask = new GetResourcesTask();
//		// timer.scheduleAtFixedRate(getResourcesTask, 10000, 20000);
//		timer.schedule(getResourcesTask, 500);
//		downloadThread = new DownloadThread();
//
//		receiveBroadCast = new ReceiveBroadCast();
//		IntentFilter filter = new IntentFilter();
//		filter.addAction("JinFengDownloadServiceBroadcast"); // 只有持有相同的action的接受者才能接收此广播
//		registerReceiver(receiveBroadCast, filter);
//	}
//
//	@Override
//	public void onDestroy() {
//		LogUtil.w("JinFengDownloadService onDestroy");
//		unregisterReceiver(receiveBroadCast);
//		// 停止请求
//		isRequest = false;
//		closeDownload();
//		getResourcesTask.cancel();
//		timer.cancel();
//		getResourcesTask = null;
//		timer = null;
//		super.onDestroy();
//	}
//
//	// 启动
//	public void startDownload() {
//		isRunning = true;
//		downloadThread.start();
//	}
//
//	// 停止
//	public void closeDownload() {
//		// 停止下载
//		isRunning = false;
//		pauseAllTask();
//	}
//
//	// 全部暂停
//	public synchronized void pauseAllTask() {
//		// 把其他全部放入暂停中
//		for (int i = 0; i < mTaskQueue.size(); i++) {
//			DownloadTask task = mTaskQueue.get(i);
//			mTaskQueue.remove(task);
//			mPausingTasks.add(task);
//		}
//		// 把下载全部放入暂停中
//		for (int i = 0; i < mDownloadingTasks.size(); i++) {
//			pauseDownloadingTask(mDownloadingTasks.get(i));
//		}
//	}
//
//	// 暂停
//	public synchronized void pauseDownloadingTask(DownloadTask task) {
//		try {
//			task.onCancelled();
//			mDownloadingTasks.remove(task);
//			mPausingTasks.add(newDownloadTask(task.getResourceItem()));
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//	}
//
//	// 暂停
//	public synchronized void pauseDownloadingTask(ResourceItem item) {
//		DownloadTask task = isInDownload(item);
//		if (task == null) {
//			return;
//		}
//		try {
//			task.onCancelled();
//			mDownloadingTasks.remove(task);
//			mPausingTasks.add(newDownloadTask(task.getResourceItem()));
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//	}
//
//	// 继续
//	public synchronized void continueTask(String url) {
//		for (int i = 0; i < mPausingTasks.size(); i++) {
//			DownloadTask task = mPausingTasks.get(i);
//			if (task != null && task.getUrl().equals(url)) {
//				mPausingTasks.remove(task);
//				mTaskQueue.offer(task);
//				break;
//			}
//		}
//	}
//
//	// 完成
//	public synchronized void completeTask(DownloadTask task) {
//		if (mDownloadingTasks.contains(task)) {
//			mDownloadingTasks.remove(task);
//		}
//	}
//
//	public void beginTask(ResourceItem resourceItem) {
//		if (isInDownload(resourceItem) != null) {
//			return;
//		}
//		try {
//			DownloadTask task = newDownloadTask(resourceItem);
//			task.setForce(true);
//			mTaskQueue.removeItem(resourceItem);
//			removeItemFromPause(resourceItem);
//			mDownloadingTasks.add(task);
//			Message message = new Message();
//			message.what = RUN_TASK_WHAT;
//			message.obj = task;
//			mHandler.sendMessage(message);
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//	}
//
//	// 实例化 DownloadTask
//	private DownloadTask newDownloadTask(ResourceItem resourceItem) throws MalformedURLException {
//		return new DownloadTask(getApplicationContext(), resourceItem, mDownloadTaskListener);
//	}
//
//	// 请求服务器资源
//	private void requestGetResources() {
//		Map<String, Object> map = new HashMap<String, Object>();
//		staffid = Constant.getCurrentUser(getApplicationContext()).staffid;
//		map.put("staffid", staffid);
//		map.put("userName", Constant.getCurrentUser(getApplicationContext()).staffname);
//		map.put("passWord", Constant.getCurrentUser(getApplicationContext()).password);
//
//		String version = CommonUtil.spGetString(getApplicationContext(), Constant.getCurrentUser(getApplicationContext()).staffname + "_version");
//
//		if (TextUtils.isEmpty(version)) {
//			map.put("categoryVersion", "0");
//			map.put("resourceVersion", "0");
//			map.put("labelVersion", "0");
//		} else {
//			GetResourcesResult.Version versionBean = JsonUtil.fromJson(version, GetResourcesResult.Version.class);
//			map.put("categoryVersion", versionBean.categoryVersion);
//			map.put("resourceVersion", versionBean.resourceVersion);
//			map.put("labelVersion", versionBean.labelVersion);
//		}
//
//		ApiUtil.request(new ApiUtil.MyHttpRequest<GetResourcesResult>(getApplicationContext(), Constant.Api.GET_RESOURCES, ApiUtil.genRequestMap(map)) {
//
//			@Override
//			protected void onRequestStart() {
//
//			}
//
//			@Override
//			protected void onNetOrServerFailure() {
//
//			}
//
//			@Override
//			protected void onNetAndServerSuccess() {
//
//			}
//
//			@Override
//			public void handleResult(GetResourcesResult result) {
//				if (!isRequest || TextUtils.isEmpty(Constant.getCurrentUser(getApplicationContext()).staffid)) {
//					return;
//				}
//				switch (result.result) {
//				case 10002: {
//					ToastUtil.showToastShort(getApplicationContext(), result.msg);
//					Intent intent = new Intent();
//					intent.putExtra("data", "nostaff");
//					intent.setAction(HomeActivity.HomeActivity_FLAG);
//					sendBroadcast(intent);
//				}
//				case 200: {
//					if (result.data == null) {
//						return;
//					}
//					if (result.data.resource != null) {
//						for (ResourceItem item : result.data.resource) {
//							item.staffid = Integer.valueOf(staffid);
//						}
//					}
//
//					boolean isRoleChange = false;
//					if (Constant.getCurrentUser(getApplicationContext()).role != result.data.staffRole) {
//						MyDB.getInstance(getApplicationContext()).checkFileDelete(Constant.getCurrentUser(getApplicationContext()).staffid, Constant.getCurrentUser(getApplicationContext()).role);
//						MyDB.getInstance(getApplicationContext()).checkDownloadState(Constant.getCurrentUser(getApplicationContext()).staffid, Constant.getCurrentUser(getApplicationContext()).role);
//						Constant.getCurrentUser(getApplicationContext()).role = result.data.staffRole;
//						isRoleChange = true;
//						CommonUtil.spPutString(getApplicationContext(), Constant.getCurrentUser(getApplicationContext()).staffname, JsonUtil.toJson(Constant.getCurrentUser(getApplicationContext())));
//					} else {
//						isRoleChange = false;
//						Constant.getCurrentUser(getApplicationContext()).role = result.data.staffRole;
//						CommonUtil.spPutString(getApplicationContext(), Constant.getCurrentUser(getApplicationContext()).staffname, JsonUtil.toJson(Constant.getCurrentUser(getApplicationContext())));
//					}
//
//					String secdata = (String) this.getParamsMap().get("_secdata");
//					String decry = XXTEA.decrypt(secdata, "1%fg&kj)(M%#Zjasd&tr*");
//					String resourceVersion = Uri.parse(this.getUrl() + "?" + new String(Base64.decode(decry))).getQueryParameter("resourceVersion");
//					LogUtil.d("resourceVersion=" + resourceVersion);
//					if (TextUtils.equals(resourceVersion, "0")) {
//						MyDB.getInstance(getApplicationContext()).update(result, true, staffid);
//					} else {
//						MyDB.getInstance(getApplicationContext()).update(result, false, staffid);
//					}
//					if ((result.data.resource != null && result.data.resource.size() > 0) || isRoleChange || TextUtils.equals(resourceVersion, "0")) {
//						MyDB.getInstance(getApplicationContext()).checkDownloadState(Constant.getCurrentUser(getApplicationContext()).staffid, Constant.getCurrentUser(getApplicationContext()).role);
//						MyDB.getInstance(getApplicationContext()).checkFileDelete(Constant.getCurrentUser(getApplicationContext()).staffid, Constant.getCurrentUser(getApplicationContext()).role);
//
//						if (result.data.resource.size() > 0) {
//							Constant.newUpdate = result.data.resource.size();
//							CommonUtil.spPutInt(getApplicationContext(), 
//									Constant.getCurrentUser(getApplicationContext()).staffname+"_newUpdate", 
//									Constant.newUpdate);
//						}
//
//						Intent intent = new Intent();
//						intent.putExtra("data", "yes");
//						intent.setAction(HomeActivity.HomeActivity_FLAG);
//						sendBroadcast(intent);
//					}
//
//					String groupids = CommonUtil.spGetString(getApplicationContext(), Constant.getCurrentUser(getApplicationContext()).staffname + "_groupids");
//					if (TextUtils.isEmpty(groupids) || TextUtils.equals(groupids, result.data.groupids)) {
//						CommonUtil.spPutString(getApplicationContext(), Constant.getCurrentUser(getApplicationContext()).staffname + "_version", JsonUtil.toJson(result.data.version));
//					} else {
//						GetResourcesResult.Version version = new GetResourcesResult.Version();
//						version.categoryVersion = "0";
//						version.labelVersion = "0";
//						version.resourceVersion = "0";
//						CommonUtil.spPutString(getApplicationContext(), Constant.getCurrentUser(getApplicationContext()).staffname + "_version", JsonUtil.toJson(version));
//					}
//
//					CommonUtil.spPutString(getApplicationContext(), Constant.getCurrentUser(getApplicationContext()).staffname + "_groupids", result.data.groupids);
//
//					ArrayList<ResourceItem> resourceDownloadList = MyDB.getInstance(getApplicationContext()).getResourceDownloadList(staffid,
//							Constant.onlyVideo, Constant.getCurrentUser(getApplicationContext()).role);
//					if (resourceDownloadList.size() > 0) {
//						if (Constant.onlyWifi) {
//							if (!isWifi(getApplicationContext())) {
//								// ToastUtil.showToastShort(getApplicationContext(),
//								// "请使用Wifi环境更新资源");
//								return;
//							}
//						}
//					}
//					for (ResourceItem resourceItem : resourceDownloadList) {
//						if (resourceItem.type != 3) {
//							if (isInDownload(resourceItem) != null || isInQueue(resourceItem) != null || isInPause(resourceItem) != null) {
//								continue;
//							}
//
//							try {
//								mTaskQueue.offer(newDownloadTask(resourceItem));
//							} catch (MalformedURLException e) {
//								e.printStackTrace();
//							}
//						}
//					}
//					if (!isRunning) {
//						startDownload();
//					}
//					break;
//				}
//				default: {
//					// ToastUtil.showToastShort(getApplicationContext(),
//					// result.msg);
//					break;
//				}
//				}
//			}
//		});
//	}
//
//	private class GetResourcesTask extends TimerTask {
//		@Override
//		public void run() {
//			mHandler.sendEmptyMessage(TIME_MESSAGE_WHAT);
//		}
//	}
//
//	private class DownloadThread extends Thread {
//		@Override
//		public void run() {
//			while (isRunning) {
//				LogUtil.d("mTaskQueue.poll()");
//				DownloadTask task = mTaskQueue.poll();
//				if (task != null) {
//					if (task.getResourceItem().type == 2 && Constant.onlyVideo) {
//
//					} else {
//						mDownloadingTasks.add(task);
//						Message message = new Message();
//						message.what = RUN_TASK_WHAT;
//						message.obj = task;
//						mHandler.sendMessage(message);
//					}
//				}
//			}
//		}
//	}
//
//	public class TaskQueue {
//		private Queue<DownloadTask> taskQueue;
//
//		public TaskQueue() {
//			taskQueue = new LinkedList<DownloadTask>();
//		}
//
//		// 加入队列
//		public void offer(DownloadTask task) {
//			taskQueue.offer(task);
//		}
//
//		// 出队列
//		public DownloadTask poll() {
//			DownloadTask task = null;
//			// 没有多余的下载线程或队列没有任务
//			while (mDownloadingTasks.size() >= MAX_DOWNLOAD_THREAD_COUNT || (task = taskQueue.poll()) == null) {
//				try {
//					Thread.sleep(2000); // sleep
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//			return task;
//		}
//
//		// 获取指定位置的任务
//		public DownloadTask get(int position) {
//			if (position >= size()) {
//				return null;
//			}
//			return ((LinkedList<DownloadTask>) taskQueue).get(position);
//		}
//
//		public Queue<DownloadTask> getTaskQueue() {
//			return taskQueue;
//		}
//
//		public int size() {
//			return taskQueue.size();
//		}
//
//		// 删除任务
//		public boolean remove(DownloadTask task) {
//			return taskQueue.remove(task);
//		}
//
//		public void removeItem(ResourceItem resourceItem) {
//			DownloadTask task = null;
//			for (DownloadTask downloadTask : ((LinkedList<DownloadTask>) taskQueue)) {
//				if (resourceItem.staffid == downloadTask.getResourceItem().staffid && resourceItem.resourcesid == downloadTask.getResourceItem().resourcesid) {
//					task = downloadTask;
//					break;
//				}
//			}
//			if (task != null) {
//				taskQueue.remove(task);
//			}
//		}
//	}
//
//	private void removeItemFromPause(ResourceItem resourceItem) {
//		DownloadTask task = null;
//		for (DownloadTask downloadTask : mPausingTasks) {
//			if (resourceItem.staffid == downloadTask.getResourceItem().staffid && resourceItem.resourcesid == downloadTask.getResourceItem().resourcesid) {
//				task = downloadTask;
//				break;
//			}
//		}
//		if (task != null) {
//			mPausingTasks.remove(task);
//		}
//	}
//
//	public class LocalBinder extends Binder {
//		// 返回本地服务
//		public JinFengDownloadService getService() {
//			return JinFengDownloadService.this;
//		}
//	}
//
//	public DownloadTask isInDownload(ResourceItem resourceItem) {
//		for (DownloadTask downloadTask : mDownloadingTasks) {
//			if (resourceItem.staffid == downloadTask.getResourceItem().staffid && resourceItem.resourcesid == downloadTask.getResourceItem().resourcesid) {
//				return downloadTask;
//			}
//		}
//		return null;
//	}
//
//	public DownloadTask isInQueue(ResourceItem resourceItem) {
//		for (DownloadTask downloadTask : mTaskQueue.getTaskQueue()) {
//			if (resourceItem.staffid == downloadTask.getResourceItem().staffid && resourceItem.resourcesid == downloadTask.getResourceItem().resourcesid) {
//				return downloadTask;
//			}
//		}
//		return null;
//	}
//
//	public DownloadTask isInPause(ResourceItem resourceItem) {
//		for (DownloadTask downloadTask : mPausingTasks) {
//			if (resourceItem.staffid == downloadTask.getResourceItem().staffid && resourceItem.resourcesid == downloadTask.getResourceItem().resourcesid) {
//				return downloadTask;
//			}
//		}
//		return null;
//	}
//
//	public TaskQueue getmTaskQueue() {
//		return mTaskQueue;
//	}
//
//	public List<DownloadTask> getmDownloadingTasks() {
//		return mDownloadingTasks;
//	}
//
//	public List<DownloadTask> getmPausingTasks() {
//		return mPausingTasks;
//	}
//
//	public void setmOutDownloadTaskListener(DownloadTaskListener mOutDownloadTaskListener) {
//		this.mOutDownloadTaskListener = mOutDownloadTaskListener;
//	}
//
//	public static boolean isWifi(Context mContext) {
//		ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
//		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//			return true;
//		}
//		return false;
//	}
//
//	public class ReceiveBroadCast extends BroadcastReceiver {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String message = intent.getStringExtra("data");
//			if (TextUtils.equals(message, "yes")) {
//				DownloadTask mDownloadTask = null;
//				for (DownloadTask downloadTask : mDownloadingTasks) {
//					if (downloadTask.getResourceItem().type == 2) {
//						mDownloadTask = downloadTask;
//					}
//				}
//				if (mDownloadTask != null) {
//					mDownloadTask.onCancelled();
//					mDownloadingTasks.remove(mDownloadTask);
//					return;
//				}
//			} else if (TextUtils.equals(message, "request")) {
//				new Timer().schedule(new GetResourcesTask(), 500);
//			}
//		}
//	}
//}
