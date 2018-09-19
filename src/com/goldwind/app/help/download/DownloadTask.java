package com.goldwind.app.help.download;
//package com.goldwind.app.help.download;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.RandomAccessFile;
//import java.net.MalformedURLException;
//import java.net.URL;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//
//import com.goldwind.app.help.Constant;
//import com.goldwind.app.help.model.GetResourcesResult.ResourceItem;
//import com.goldwind.app.help.util.AppUtil;
//
//import android.accounts.NetworkErrorException;
//import android.content.Context;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.AsyncTask;
//
///**
// * 每一个下载任务
// */
//public class DownloadTask extends AsyncTask<Void, Integer, Long> {
//	public final static int TIME_OUT = 30000;
//	private final static int BUFFER_SIZE = 1024 * 8;
//	private static final String TEMP_SUFFIX = ".download";
//
//	private String name;
//	private String id;
//	private URL URL;
//	private File file;
//	private File tempFile;
//	private String url;
//	private RandomAccessFile outputStream;
//	private DownloadTaskListener listener;
//	private Context context;
//
//	private long downloadSize;
//	private long previousFileSize;
//	private long totalSize;
//	private long downloadPercent;
//	private long networkSpeed;
//	private long previousTime;
//	private long totalTime;
//	private Throwable error = null;
//	private boolean interrupt = false;
//	
//	private ResourceItem resourceItem;
//	private boolean isForce = false;
//	public void setForce(boolean isForce) {
//		this.isForce = isForce;
//	}
//
//	public DownloadTask(Context context, ResourceItem resourceItem, DownloadTaskListener listener) throws MalformedURLException {
//		super();
//		this.context = context;
//		this.resourceItem = resourceItem;
//		this.url = Constant.BASE_URL + "/" + resourceItem.fileaddress;
//		this.name = resourceItem.name;
//		this.id = resourceItem.resourcesid + "";
//		this.URL = new URL(url);
//		this.listener = listener;
//		
//		String path = AppUtil.getDatePath(this.url);
//		path = Constant.BASE_FILE_PATH + path;
//		File file = new File(path);
//		if (!file.exists()) {
//			file.mkdirs();
//		}
//		
//		String fileName = new File(URL.getFile()).getName();
//		this.file = new File(path, fileName);
//		this.tempFile = new File(path, fileName + TEMP_SUFFIX);
//	}
//
//	@Override
//	protected void onPreExecute() {
//		previousTime = System.currentTimeMillis();
//		if (listener != null)
//			listener.preDownload(this);
//	}
//
//	@Override
//	protected Long doInBackground(Void... params) {
//		long result = -1;
//		try {
//			result = download();
//		} catch (NetworkErrorException e) {
//			error = e;
//		} catch (FileAlreadyExistException e) {
//			error = e;
//		} catch (NoMemoryException e) {
//			error = e;
//		} catch (IOException e) {
//			error = e;
//		}  catch (NoWifiException e) {
//			error = e;
//		}
//		return result;
//	}
//
//	@Override
//	protected void onPostExecute(Long result) {
//		if (error != null) {
//			if (listener != null) {
//				listener.errorDownload(this, error);
//			}
//			return;
//		}
//		if (result == -1) {
//			listener.errorDownload(this, new IOException("unkown excetion"));
//			return;
//		}
//		if (interrupt) {
//			listener.errorDownload(this, new IOException("download interrupt"));
//			return;
//		}
//		// 下载完成
//		tempFile.renameTo(file);
//		if (listener != null) {
//			listener.finishDownload(this);
//		}
//	}
//
//	@Override
//	protected void onProgressUpdate(Integer... progress) {
//		totalTime = System.currentTimeMillis() - previousTime;
//		downloadSize = progress[0];
//		downloadPercent = (downloadSize + previousFileSize) * 100 / totalSize;
//		networkSpeed = downloadSize / totalTime;
//		if (listener != null)
//			listener.updateProcess(this);
//	}
//
//	@Override
//	public void onCancelled() {
//		interrupt = true;
//		super.onCancelled();
//	}
//
//	private HttpGet httpGet;
//	private HttpClient httpClient;
//	private HttpResponse response;
//
//	private long download() throws NetworkErrorException, IOException, FileAlreadyExistException, NoMemoryException, IllegalStateException, NoWifiException {
//		if (!isNetworkAvailable(context)) {
//			throw new NetworkErrorException("网络不可用");
//		}
//		
//		if (Constant.onlyWifi) {
//			if (!isWifi(context) && !isForce) {
//				throw new NoWifiException("不是Wifi环境");
//			}
//		}
//
//		httpClient = new DefaultHttpClient();
//		httpGet = new HttpGet(url);
//		httpGet.setHeader("Accept-Encoding", "identity");
//		response = httpClient.execute(httpGet);
//		totalSize = response.getEntity().getContentLength();
//
//		if (totalSize == -1) {
//			throw new IOException("getContentLength return -1");
//		}
//
//		if (file.exists() && totalSize == file.length()) {
//			throw new FileAlreadyExistException("文件已存在");
//		} else if (tempFile.exists()) {
//			httpGet.addHeader("Range", "bytes=" + tempFile.length() + "-");
//			previousFileSize = tempFile.length();
//			response = httpClient.execute(httpGet);
//		}
//
//		// 第一次更新进度条
//		publishProgress(0);
//
//		if (totalSize - tempFile.length() > StorageUtils.getAvailableStorage()) {
//			throw new NoMemoryException("SD Card 空间不足");
//		}
//
//		outputStream = new ProgressReportingRandomAccessFile(tempFile, "rw");
//		int bytesCopied = copy(response.getEntity().getContent(), outputStream);
//
//		if ((previousFileSize + bytesCopied) != totalSize) {
//			throw new IOException("download incomplete");
//		}
//
//		return bytesCopied;
//	}
//
//	private int copy(InputStream input, RandomAccessFile out) throws IOException, NetworkErrorException, NoWifiException {
//		if (input == null || out == null) {
//			throw new IOException("InputStream or RandomAccessFile is null");
//		}
//
//		byte[] buffer = new byte[BUFFER_SIZE];
//		int count = 0, n = 0;
//		long errorBlockTimePreviousTime = -1, expireTime = 0;
//
//		out.seek(out.length());
//
//		while (!interrupt) {
//			n = input.read(buffer, 0, BUFFER_SIZE);
//			if (n == -1) {
//				break;
//			}
//			out.write(buffer, 0, n);
//			count += n;
//
//			if (!isNetworkAvailable(context)) {
//				throw new NetworkErrorException("网络不可用");
//			}
//			
//			if (Constant.onlyWifi) {
//				if (!isWifi(context) && !isForce) {
//					throw new NoWifiException("不是Wifi环境");
//				}
//			}
//
//			if (networkSpeed == 0) {
//				if (errorBlockTimePreviousTime > 0) {
//					expireTime = System.currentTimeMillis() - errorBlockTimePreviousTime;
//					if (expireTime > TIME_OUT) {
//						throw new NetworkErrorException("下载网络连接超时");
//					}
//				} else {
//					errorBlockTimePreviousTime = System.currentTimeMillis();
//				}
//			} else {
//				expireTime = 0;
//				errorBlockTimePreviousTime = -1;
//			}
//			
//			try {
//				Thread.sleep(30);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//		out.close();
//		input.close();
//		httpGet = null;
//		response = null;
//		httpClient = null;
//		return count;
//	}
//
//	private final class ProgressReportingRandomAccessFile extends RandomAccessFile {
//		private int progress = 0;
//
//		public ProgressReportingRandomAccessFile(File file, String mode) throws FileNotFoundException {
//			super(file, mode);
//		}
//
//		@Override
//		public void write(byte[] buffer, int offset, int count) throws IOException {
//			super.write(buffer, offset, count);
//			progress += count;
//			publishProgress(progress);
//		}
//	}
//
//	public static boolean isNetworkAvailable(Context context) {
//		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//		if (connectivity == null) {
//			return false;
//		} else {
//			NetworkInfo[] info = connectivity.getAllNetworkInfo();
//			if (info != null) {
//				for (int i = 0; i < info.length; i++) {
//					if (info[i].getState() == NetworkInfo.State.CONNECTED || info[i].getState() == NetworkInfo.State.CONNECTING) {
//						return true;
//					}
//				}
//			}
//		}
//		return false;
//	}
//	
//	public static boolean isWifi(Context mContext) {
//		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
//				.getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
//		if (activeNetInfo != null
//				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//			return true;
//		}
//		return false;
//	}
//
//	public String getUrl() {
//		return url;
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public String getId() {
//		return id;
//	}
//
//	public File getFile() {
//		return file;
//	}
//
//	public boolean isInterrupt() {
//		return interrupt;
//	}
//
//	public long getDownloadPercent() {
//		return downloadPercent;
//	}
//
//	public long getDownloadSize() {
//		return downloadSize + previousFileSize;
//	}
//
//	public long getTotalSize() {
//		return totalSize;
//	}
//
//	public long getDownloadSpeed() {
//		return this.networkSpeed;
//	}
//
//	public long getTotalTime() {
//		return this.totalTime;
//	}
//
//	public DownloadTaskListener getListener() {
//		return this.listener;
//	}
//
//	public ResourceItem getResourceItem() {
//		return resourceItem;
//	}
//}
