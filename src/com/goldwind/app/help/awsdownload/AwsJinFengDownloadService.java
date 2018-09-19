package com.goldwind.app.help.awsdownload;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.db.MyDB;
import com.goldwind.app.help.model.GetResourcesResult;
import com.goldwind.app.help.model.GetResourcesResult.ResourceItem;
import com.goldwind.app.help.util.ApiUtil;
import com.goldwind.app.help.util.AppUtil;
import com.goldwind.app.help.util.Base64;
import com.goldwind.app.help.util.CommonUtil;
import com.goldwind.app.help.util.JsonUtil;
import com.goldwind.app.help.util.LogUtil;
import com.goldwind.app.help.util.ToastUtil;
import com.goldwind.app.help.util.XXTEA;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AwsJinFengDownloadService extends Service {

    private static final int MAX_DOWNLOAD_THREAD_COUNT = 1;
    private static final int RUN_TASK_WHAT = 0x2000;
    private static final int RUN_REQUEST_WHAT = 0x3000;
    private static final int REQUEST_AGAIN_WHAT = 0x4000;
    private static final String TEMP_SUFFIX = ".download";
    public static String bucketName;
    private TransferUtility transferUtility;
    private List<ResourceItem> mDownloadingList;
    private HashMap<Integer, TransferObserver> mTransferObserverMap;
    private TaskQueueList mTaskQueueList;

    private Handler mHandler;
    private Boolean isRunning = false;
    private DownloadThread mDownloadThread;

    private ReceiveBroadCast mReceiveBroadCast;
    private OutDownloadListener currentDownloadListener;
    private IBinder mBinder = new LocalBinder();

    public static File getDownloadFile(ResourceItem resourceItem) {
        File resultFile = null;
        try {
            String url = Constant.BASE_URL + "/" + resourceItem.fileaddress;
            String path = Constant.BASE_FILE_PATH + AppUtil.getDatePath(url);
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = new File(new URL(url).getFile()).getName();
            resultFile = new File(path, fileName);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return resultFile;
    }

    public static File getFengMianFile(String cover) {
        File resultFile = null;
        try {
            String url = Constant.BASE_URL + "/" + cover;
            String path = Constant.BASE_FILE_PATH + AppUtil.getDatePath(url);
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = new File(new URL(url).getFile()).getName();
            resultFile = new File(path, fileName);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return resultFile;
    }

    public static File getDownloadTempFile(ResourceItem resourceItem) {
        File resultFile = null;
        try {
            String url = Constant.BASE_URL + "/" + resourceItem.fileaddress;
            String path = Constant.BASE_FILE_PATH + AppUtil.getDatePath(url);
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = new File(new URL(url).getFile()).getName();
            resultFile = new File(path, fileName + TEMP_SUFFIX);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return resultFile;
    }

    public static boolean isInList(ResourceItem item, List<ResourceItem> list) {
        if (list != null && list.size() > 0) {
            for (ResourceItem resourceItem : list) {
                if (resourceItem.resourcesid == item.resourcesid) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case RUN_TASK_WHAT: {
                        ResourceItem resourceItem = (ResourceItem) msg.obj;
                        beginDownload(resourceItem, false);
                        break;
                    }
                    case RUN_REQUEST_WHAT: {
                        requestGetResources();
                        break;
                    }
                    case REQUEST_AGAIN_WHAT: {
                        requestGetResources();
                        break;
                    }
                    default:
                        break;
                }
            }
        };
        mReceiveBroadCast = new ReceiveBroadCast();
        mDownloadThread = new DownloadThread();
        IntentFilter filter = new IntentFilter();
        filter.addAction("AwsJinFengDownloadServiceBroadcast");
        registerReceiver(mReceiveBroadCast, filter);
        mDownloadingList = new ArrayList<ResourceItem>();
        mTransferObserverMap = new HashMap<Integer, TransferObserver>();
        mTaskQueueList = new TaskQueueList();
        transferUtility = Util.getTransferUtility(this);
        mHandler.sendEmptyMessageDelayed(RUN_REQUEST_WHAT, 1000);
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        unregisterReceiver(mReceiveBroadCast);
        super.onDestroy();
    }

    // 请求服务器资源开始下载
    private void requestGetResources() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("staffid", Constant.getCurrentUser(getApplicationContext()).staffid);
        map.put("userName", Constant.getCurrentUser(getApplicationContext()).staffname);
        map.put("passWord", Constant.getCurrentUser(getApplicationContext()).password);
        String version = CommonUtil.spGetString(getApplicationContext(), Constant.getCurrentUser(getApplicationContext()).staffname + "_version");
        if (TextUtils.isEmpty(version)) {
            map.put("categoryVersion", "0");
            map.put("resourceVersion", "0");
            map.put("labelVersion", "0");
        } else {
            GetResourcesResult.Version versionBean = JsonUtil.fromJson(version, GetResourcesResult.Version.class);
            map.put("categoryVersion", versionBean.categoryVersion);
            map.put("resourceVersion", versionBean.resourceVersion);
            map.put("labelVersion", versionBean.labelVersion);
        }
        ApiUtil.request(new ApiUtil.MyHttpRequest<GetResourcesResult>(getApplicationContext(), Constant.Api.GET_RESOURCES, ApiUtil.genRequestMap(map)) {
            @Override
            protected void onRequestStart() {
            }

            @Override
            protected void onNetOrServerFailure() {
            }

            @Override
            protected void onNetAndServerSuccess() {
            }

            @Override
            public void handleResult(GetResourcesResult result) {
                switch (result.result) {
                    case 10002: {
                        CommonUtil.spPutString(getApplicationContext(), "accessKeyID", result.data.accessKeyID);
                        CommonUtil.spPutString(getApplicationContext(), "secretKey", result.data.secretKey);
                        CommonUtil.spPutString(getApplicationContext(), "bucketName", result.data.bucketName);

                        ToastUtil.showToastShort(getApplicationContext(), result.msg);
                        Intent intent = new Intent();
                        intent.putExtra("data", "nostaff");
                        intent.setAction("HomeActivityBroadCast");
                        sendBroadcast(intent);
                    }
                    case 200: {
                        if (result.data == null) {
                            return;
                        }

                        CommonUtil.spPutString(getApplicationContext(), "accessKeyID", result.data.accessKeyID);
                        CommonUtil.spPutString(getApplicationContext(), "secretKey", result.data.secretKey);
                        CommonUtil.spPutString(getApplicationContext(), "bucketName", result.data.bucketName);

                        if (TextUtils.isEmpty(bucketName)) {
                            bucketName = result.data.bucketName;
                            ApiUtil.bucketName = bucketName;
                        }

                        if (result.data.resource != null) {
                            int staffidInt = Integer.valueOf(Constant.getCurrentUser(getApplicationContext()).staffid);
                            for (ResourceItem item : result.data.resource) {
                                item.staffid = staffidInt;
                            }
                        }

                        // 权限更改
                        boolean isRoleChange = false;
                        if (!TextUtils.equals(Constant.getCurrentUser(getApplicationContext()).role, result.data.staffRole)) {
                            isRoleChange = true;

                            if (Integer.valueOf(result.data.staffRole) > Integer.valueOf(Constant.getCurrentUser(getApplicationContext()).role)) {
                                Constant.getCurrentUser(getApplicationContext()).role = result.data.staffRole;
                                CommonUtil.spPutString(getApplicationContext(), Constant.getCurrentUser(getApplicationContext()).staffname,
                                        JsonUtil.toJson(Constant.getCurrentUser(getApplicationContext())));

                                ArrayList<String> broadCastList = new ArrayList<String>();
                                broadCastList.add("HomeActivityBroadCast");
                                broadCastList.add("DeleteActivityBroadCast");
                                broadCastList.add("FenLeiActivityBroadCast");
                                broadCastList.add("FenLeiNullActivityBroadCast");
                                broadCastList.add("PDFDetailActivityBroadCast");
                                broadCastList.add("PicsDetailActivityBroadCast");
                                broadCastList.add("ResourceDetailActivityBroadCast");
                                broadCastList.add("VideoDetailActivityBroadCast");
                                broadCastList.add("AboutMeActivityBroadCast");
                                broadCastList.add("NewUpdateActivityBroadCast");
                                broadCastList.add("AdviceActivityBroadCast");
                                broadCastList.add("PicsReaderActivityBroadCast");
                                broadCastList.add("SearchActivityBroadCast");
                                broadCastList.add("ReadRoleActivityBroadCast");
                                broadCastList.add("PicsDetailUnRoleActivityBroadCast");

                                for (String string : broadCastList) {
                                    Intent intent = new Intent();
                                    intent.putExtra("data", "RoleChange");
                                    intent.setAction(string);
                                    sendBroadcast(intent);
                                }
                            } else {
                                Constant.getCurrentUser(getApplicationContext()).role = result.data.staffRole;
                                CommonUtil.spPutString(getApplicationContext(), Constant.getCurrentUser(getApplicationContext()).staffname,
                                        JsonUtil.toJson(Constant.getCurrentUser(getApplicationContext())));
                            }
                        }

                        // 更新资源
                        String secdata = (String) this.getParamsMap().get("_secdata");
                        String decry = XXTEA.decrypt(secdata, "1%fg&kj)(M%#Zjasd&tr*");
                        String resourceVersion = Uri.parse(this.getUrl() + "?" + new String(Base64.decode(decry))).getQueryParameter("resourceVersion");

                        if ((result.data.resource != null && result.data.resource.size() > 0) || isRoleChange || TextUtils.equals(resourceVersion, "0")) {
                            if (result.data.resource != null && result.data.resource.size() > 0) {
                                Constant.newUpdate = MyDB.getInstance(getApplicationContext()).getNewUpdateNum(result.data.resource);
                                if (Constant.newUpdate > 0) {
                                    CommonUtil.spPutInt(getApplicationContext(), Constant.getCurrentUser(getApplicationContext()).staffname + "_newUpdate",
                                            Constant.newUpdate);
                                }
                            }
                        }

                        if (TextUtils.equals(resourceVersion, "0")) {
                            // 清空原有的
                            MyDB.getInstance(getApplicationContext()).update(getApplicationContext(), result, true, Constant.getCurrentUser(getApplicationContext()).staffid);
                        } else {
                            MyDB.getInstance(getApplicationContext()).update(getApplicationContext(), result, false, Constant.getCurrentUser(getApplicationContext()).staffid);
                        }

                        if ((result.data.resource != null && result.data.resource.size() > 0) || isRoleChange || TextUtils.equals(resourceVersion, "0")) {
                            MyDB.getInstance(getApplicationContext()).checkDownloadState(Constant.getCurrentUser(getApplicationContext()).staffid,
                                    Constant.getCurrentUser(getApplicationContext()).role);
                            MyDB.getInstance(getApplicationContext()).checkFileDelete(Constant.getCurrentUser(getApplicationContext()).staffid,
                                    Constant.getCurrentUser(getApplicationContext()).role);
                            // 通知刷新页面
                            Intent intent = new Intent();
                            intent.putExtra("data", "yes");
                            intent.setAction("HomeActivityBroadCast");
                            sendBroadcast(intent);
                        }

                        boolean isRequestAgain = false;

                        // 组更改下一次重新清空请求
                        String groupids = CommonUtil.spGetString(getApplicationContext(), Constant.getCurrentUser(getApplicationContext()).staffname + "_groupids");
                        if (TextUtils.isEmpty(groupids) || TextUtils.equals(groupids, result.data.groupids)) {
                            CommonUtil.spPutString(getApplicationContext(), Constant.getCurrentUser(getApplicationContext()).staffname + "_version",
                                    JsonUtil.toJson(result.data.version));
                        } else {
                            GetResourcesResult.Version version = new GetResourcesResult.Version();
                            version.categoryVersion = "0";
                            version.labelVersion = "0";
                            version.resourceVersion = "0";
                            CommonUtil.spPutString(getApplicationContext(), Constant.getCurrentUser(getApplicationContext()).staffname + "_version",
                                    JsonUtil.toJson(version));
                            isRequestAgain = true;
                        }

                        CommonUtil.spPutString(getApplicationContext(), Constant.getCurrentUser(getApplicationContext()).staffname + "_groupids",
                                result.data.groupids);

                        // 开始下载，获取需要下载的资源
                        ArrayList<ResourceItem> resourceDownloadList = MyDB.getInstance(getApplicationContext())
                                .getResourceDownloadList(Constant.getCurrentUser(getApplicationContext()).staffid, Constant.onlyVideo,
                                        Constant.getCurrentUser(getApplicationContext()).role);

                        if (resourceDownloadList.size() > 0) {
                            if (Constant.onlyWifi) {
                                // 不是wifi
                                if (!isWifi(getApplicationContext())) {
                                    return;
                                }
                            }
                        } else {
                            return;
                        }

                        for (ResourceItem resourceItem : resourceDownloadList) {
                            if (resourceItem.type == 3) {
                                continue;
                            }
                            if (resourceItem.type == 2 && Constant.onlyVideo) {
                                continue;
                            }
                            mTaskQueueList.offer(resourceItem);
                        }

                        if (!isRunning) {
                            isRunning = true;
                            mDownloadThread.start();
                        }

                        if (isRequestAgain) {
                            mHandler.sendEmptyMessageDelayed(REQUEST_AGAIN_WHAT, 800);
                        }

                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        });
    }

    public void beginDownloadFromDetail(ResourceItem item) {
        if (isInList(item, mDownloadingList)) {
            return;
        }
        if (isInList(item, mTaskQueueList.getList())) {
            mTaskQueueList.remove(item);
        }
        beginDownload(item, true);
    }

    public void pauseDownloadFromDetail(ResourceItem item) {
        if (!isInList(item, mDownloadingList)) {
            return;
        }
        TransferObserver observer = mTransferObserverMap.get(Integer.valueOf(item.resourcesid));
        if (observer != null) {
            transferUtility.pause(observer.getId());
        }
    }

    public void resumeDownloadFromDetail(ResourceItem item) {
        if (!isInList(item, mDownloadingList)) {
            return;
        }
        TransferObserver observer = mTransferObserverMap.get(Integer.valueOf(item.resourcesid));
        if (observer != null) {
            transferUtility.resume(observer.getId());
        }
    }

    public HashMap<Integer, TransferObserver> getmTransferObserverMap() {
        return mTransferObserverMap;
    }

    public TaskQueueList getmTaskQueueList() {
        return mTaskQueueList;
    }

    private void beginDownload(ResourceItem resourceItem, boolean isForce) {
        if (getDownloadFile(resourceItem).exists()) {
            return;
        }
        if (!isForce && Constant.onlyWifi && !isWifi(getApplicationContext())) {
            ArrayList<ResourceItem> removeList = new ArrayList<ResourceItem>();
            for (int i = 0; i < mDownloadingList.size(); i++) {
                removeList.add(mDownloadingList.get(i));
            }
            for (ResourceItem item : removeList) {
                TransferObserver observer = mTransferObserverMap.get(Integer.valueOf(item.resourcesid));
                transferUtility.pause(observer.getId());
                transferUtility.deleteTransferRecord(observer.getId());
                mTransferObserverMap.remove(Integer.valueOf(resourceItem.resourcesid));
                mDownloadingList.remove(item);
            }
            removeList.clear();
        } else {
            mDownloadingList.add(resourceItem);
            if (TextUtils.isEmpty(bucketName)) {
                bucketName = CommonUtil.spGetString(this, "bucketName");
                ApiUtil.bucketName = bucketName;
            }
            TransferObserver observer = transferUtility.download(bucketName, resourceItem.fileaddress, getDownloadTempFile(resourceItem));
            observer.setTransferListener(new DownloadListener(resourceItem));
            mTransferObserverMap.put(resourceItem.resourcesid, observer);
        }
    }

    public void setCurrentDownloadListener(OutDownloadListener currentDownloadListener) {
        this.currentDownloadListener = currentDownloadListener;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class DownloadListener implements TransferListener {
        private ResourceItem resourceItem;

        public DownloadListener(ResourceItem resourceItem) {
            super();
            this.resourceItem = resourceItem;
        }

        public boolean isEqual() {
            if (currentDownloadListener.getResourceItem().resourcesid == this.resourceItem.resourcesid) {
                return true;
            }
            return false;
        }

        @Override
        public void onError(int id, Exception e) {
            getDownloadTempFile(resourceItem).delete();
            mDownloadingList.remove(resourceItem);
            TransferObserver observer = mTransferObserverMap.get(Integer.valueOf(resourceItem.resourcesid));
            transferUtility.deleteTransferRecord(observer.getId());
            mTransferObserverMap.remove(Integer.valueOf(resourceItem.resourcesid));
//			LogUtil.e(e);

            if (currentDownloadListener != null && isEqual()) {
                currentDownloadListener.onError(id, e);
            }
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            if (currentDownloadListener != null && isEqual()) {
                currentDownloadListener.onProgressChanged(id, bytesCurrent, bytesTotal);
            }
        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            LogUtil.e(state.toString());
            if (state == TransferState.COMPLETED) {
                getDownloadTempFile(resourceItem).renameTo(getDownloadFile(resourceItem));
                mDownloadingList.remove(resourceItem);
                TransferObserver observer = mTransferObserverMap.get(Integer.valueOf(resourceItem.resourcesid));
                transferUtility.deleteTransferRecord(observer.getId());
                mTransferObserverMap.remove(Integer.valueOf(resourceItem.resourcesid));

                MyDB.getInstance(getApplicationContext()).updateResourceDownloadState(resourceItem.staffid + "", resourceItem.resourcesid, 1);

                LogUtil.d(resourceItem.name + " 下载完成");

                Intent intent = new Intent();
                intent.putExtra("data", "yes");
                intent.setAction("HomeActivityBroadCast");
                sendBroadcast(intent);
            }
            if (currentDownloadListener != null && isEqual()) {
                currentDownloadListener.onStateChanged(id, state);
            }
        }
    }

    private class DownloadThread extends Thread {
        @Override
        public void run() {
            while (isRunning) {
                ResourceItem item = mTaskQueueList.poll();
                if (item != null) {
                    Message message = new Message();
                    message.what = RUN_TASK_WHAT;
                    message.obj = item;
                    mHandler.sendMessage(message);
                }
            }
        }
    }

    public class TaskQueueList {
        private LinkedList<ResourceItem> list;

        public TaskQueueList() {
            this.list = new LinkedList<ResourceItem>();
        }

        public LinkedList<ResourceItem> getList() {
            return list;
        }

        // 入队列
        public void offer(ResourceItem item) {
            if (!isInList(item, this.list) && !isInList(item, mDownloadingList)) {
                this.list.offer(item);
            }
        }

        // 出队列
        public ResourceItem poll() {
            ResourceItem item = null;
            while (mDownloadingList.size() >= MAX_DOWNLOAD_THREAD_COUNT || (item = list.poll()) == null) {
                try {
                    Thread.sleep(2000); // sleep
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return item;
        }

        public void remove(ResourceItem item) {
            ResourceItem removeItem = null;
            for (ResourceItem resourceItem : list) {
                if (item.resourcesid == resourceItem.resourcesid) {
                    removeItem = resourceItem;
                    break;
                }
            }
            if (removeItem != null) {
                list.remove(removeItem);
            }
        }
    }

    public class ReceiveBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("data");
            if (TextUtils.equals(message, "request")) {
                mHandler.sendEmptyMessageDelayed(RUN_REQUEST_WHAT, 1000);
            } else if (TextUtils.equals(message, "onlyVideo")) {
                ArrayList<ResourceItem> removeList = new ArrayList<ResourceItem>();
                for (int i = 0; i < mTaskQueueList.list.size(); i++) {
                    ResourceItem item = mTaskQueueList.list.get(i);
                    if (item.type == 2) {
                        removeList.add(item);
                    }
                }
                for (ResourceItem item : removeList) {
                    mTaskQueueList.list.remove(item);
                }
                removeList.clear();
                for (int i = 0; i < mDownloadingList.size(); i++) {
                    ResourceItem item = mDownloadingList.get(i);
                    if (item.type == 2) {
                        removeList.add(item);
                    }
                }
                for (ResourceItem item : removeList) {
                    TransferObserver observer = mTransferObserverMap.get(Integer.valueOf(item.resourcesid));
                    transferUtility.pause(observer.getId());
                    transferUtility.deleteTransferRecord(observer.getId());
                    mTransferObserverMap.remove(Integer.valueOf(item.resourcesid));
                    mDownloadingList.remove(item);
                }
                removeList.clear();
            }
        }

    }

    public class LocalBinder extends Binder {
        // 返回本地服务
        public AwsJinFengDownloadService getService() {
            return AwsJinFengDownloadService.this;
        }
    }
}
