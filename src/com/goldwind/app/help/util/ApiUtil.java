package com.goldwind.app.help.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.awsdownload.AwsJinFengDownloadService;
import com.goldwind.app.help.awsdownload.Util;
import com.goldwind.app.help.model.BaseResult;
import com.goldwind.app.help.view.MyTouchImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.xiaopan.sketch.SketchImageView;

/**
 * Created by Yao on 2015/10/24.
 */
public class ApiUtil {
    private static final AsyncHttpClient client = new AsyncHttpClient();
    public static String _did;
    public static String _dname;
    public static String _language;
    public static String _version;
    public static String _appversion;
    public static String _model;
    public static String _systemtype;
    public static String _from;
    public static String _network;
    public static String _devicetoken;
    public static String bucketName;
    private static boolean beginDownPic = false;

    static {
        client.setTimeout(1000 * 15);
    }

    /**
     * 请求演出接口
     */
    public static void request(MyHttpRequest<?> httpRequest) {

        httpRequest.onRequestStart();

        RequestParams params = null;

        if (httpRequest.getParamsMap() != null) {
            params = new RequestParams();

            Set<Map.Entry<String, Object>> entrySet = httpRequest.getParamsMap().entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                params.put(entry.getKey(), entry.getValue());
            }
        }
        client.post(httpRequest.getUrl(), params, httpRequest);

        // show log
        if (Constant.IS_TEST) {
            String logParams = genRequestStr(httpRequest.getParamsMap());
            if (TextUtils.isEmpty(logParams)) {
                LogUtil.i(httpRequest.getUrl());
            } else {
                LogUtil.i(httpRequest.getUrl() + "?" + logParams);
            }
        }
    }

    /**
     * 请求演出接口
     */
    public static void requestGet(MyHttpRequest<?> httpRequest) {

        httpRequest.onRequestStart();

        RequestParams params = null;

        if (httpRequest.getParamsMap() != null) {
            params = new RequestParams();

            Set<Map.Entry<String, Object>> entrySet = httpRequest.getParamsMap().entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                params.put(entry.getKey(), entry.getValue());
            }
        }
        client.get(httpRequest.getUrl(), params, httpRequest);
//		client.post(httpRequest.getUrl(), params, httpRequest);

        // show log
        if (Constant.IS_TEST) {
            String logParams = genRequestStr(httpRequest.getParamsMap());
            if (TextUtils.isEmpty(logParams)) {
                LogUtil.i(httpRequest.getUrl());
            } else {
                LogUtil.i(httpRequest.getUrl() + "?" + logParams);
            }
        }
    }

    // 意见反馈
    public static void request(MyHttpRequest<?> httpRequest, List<File> fileList) {

        httpRequest.onRequestStart();

        RequestParams params = null;
        params = new RequestParams();
        params.setForceMultipartEntityContentType(true);

        if (httpRequest.getParamsMap() != null) {
            Set<Map.Entry<String, Object>> entrySet = httpRequest.getParamsMap().entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                params.put(entry.getKey(), entry.getValue());
            }
        }

        try {
            if (fileList != null && fileList.size() > 0) {
                int i = 1;
                for (File file : fileList) {
                    params.put("files_" + i, file);
                    i++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        client.post(httpRequest.getUrl(), params, httpRequest);

        // show log
        if (Constant.IS_TEST) {
            String logParams = genRequestStr(httpRequest.getParamsMap());
            if (TextUtils.isEmpty(logParams)) {
                LogUtil.i(httpRequest.getUrl());
            } else {
                LogUtil.i(httpRequest.getUrl() + "?" + logParams);
            }
        }
    }

    public static String genRequestStr(Map<String, Object> paramsMap) {
        String result = "";
        if (paramsMap != null) {
            Set<Map.Entry<String, Object>> entrySet = paramsMap.entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                result += (entry.getKey() + "=" + entry.getValue() + "&");
            }
            if (!TextUtils.isEmpty(result)) {
                result = result.substring(0, result.length() - 1);
            }
        }
        return result;
    }

    public static Map<String, Object> genRequestMap(Map<String, Object> paramsMap) {
        paramsMap.put("_did", _did);
        paramsMap.put("_dname", _dname);
        paramsMap.put("_language", _language);
        paramsMap.put("_version", _version);
        paramsMap.put("_appversion", _appversion);
        paramsMap.put("_model", _model);
        paramsMap.put("_systemtype", _systemtype);
        paramsMap.put("_from", _from);
        paramsMap.put("_network", _network);
        paramsMap.put("_devicetoken", _devicetoken);

        String encode = Base64.encode(ApiUtil.genRequestStr(paramsMap).getBytes());
        String encrypt = XXTEA.encrypt(encode, "1%fg&kj)(M%#Zjasd&tr*");

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("_secdata", encrypt);
        return result;
    }

    public static String getBucketName(Context context) {
        if (TextUtils.isEmpty(bucketName)) {
            bucketName = CommonUtil.spGetString(context, "bucketName");
        }
        return bucketName;
    }

    public static void downloadFengMian(final String key, final SketchImageView imageView) {
        final File file = AwsJinFengDownloadService.getFengMianFile(key);
        final File fileTmp = new File(Constant.BASE_FILE_PATH + CommonUtil.md5(key) + ".tmp");
        if (file.exists()) {
            imageView.displayImage(file.getAbsolutePath());
        } else {
            if (fileTmp.exists()) {
                return;
            }
            final Handler handler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    if (msg.what == 111) {
                        String path = (String) msg.obj;
                        imageView.displayImage(path);
                    }
                }

                ;
            };
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        S3ObjectInputStream inputStream = Util.getS3Client(imageView.getContext().
                                getApplicationContext()).getObject(getBucketName(imageView.getContext()), key).getObjectContent();
                        byte[] b = new byte[2048];
                        int len;
                        OutputStream outputStream = new FileOutputStream(fileTmp);
                        while ((len = inputStream.read(b)) != -1) {
                            outputStream.write(b, 0, len);
                        }
                        outputStream.close();
                        inputStream.close();
                        if (fileTmp.exists()) {
                            fileTmp.renameTo(file);
                            Message message = new Message();
                            message.what = 111;
                            message.obj = file.getAbsolutePath();
                            handler.sendMessage(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            ThreadPool.getInstance().addTask(runnable);
        }
    }

    public static void downloadPic(final String key, final MyTouchImageView imageView) {
        final File file = new File(Constant.BASE_FILE_PATH + CommonUtil.md5(key));
        final File fileTmp = new File(Constant.BASE_FILE_PATH + CommonUtil.md5(key) + ".tmp");
        if (file.exists()) {
            imageView.setImageBitmap(BitMapUtil.getBitmapFromFile(file.getAbsolutePath(), 480, 800));
            imageView.init();
        } else {
            if (fileTmp.exists()) {
                return;
            }

            final Handler handler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    if (msg.what == 111) {
                        String path = (String) msg.obj;
                        imageView.setImageBitmap(BitMapUtil.getBitmapFromFile(path, 480, 800));
                        imageView.init();
                    }
                }

                ;
            };

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        FileDesUtil fileDES = new FileDesUtil("zi85mc8F0m34S2F%*");
                        S3ObjectInputStream inputStream = Util.getS3Client(imageView.getContext().
                                getApplicationContext()).getObject(getBucketName(imageView.getContext()), key).getObjectContent();
                        fileDES.doDecryptFile(inputStream, new FileOutputStream(fileTmp));
                        if (fileTmp.exists()) {
                            fileTmp.renameTo(file);
                            Message message = new Message();
                            message.what = 111;
                            message.obj = file.getAbsolutePath();
                            handler.sendMessage(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            ThreadPool.getInstance().addTask(runnable);
        }
    }

    public static void downloadPic(final String key, final ImageView imageView) {
        final File file = new File(Constant.BASE_FILE_PATH + CommonUtil.md5(key));
        final File fileTmp = new File(Constant.BASE_FILE_PATH + CommonUtil.md5(key) + ".tmp");
        if (file.exists()) {
            imageView.setImageBitmap(BitMapUtil.getBitmapFromFile(file.getAbsolutePath(), 480, 800));
        } else {
            if (fileTmp.exists()) {
                return;
            }

            final Handler handler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    if (msg.what == 111) {
                        String path = (String) msg.obj;
                        imageView.setImageBitmap(BitMapUtil.getBitmapFromFile(path, 480, 800));
                    }
                }

                ;
            };

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        FileDesUtil fileDES = new FileDesUtil("zi85mc8F0m34S2F%*");
                        S3ObjectInputStream inputStream = Util.getS3Client(imageView.getContext().
                                getApplicationContext()).getObject(getBucketName(imageView.getContext()), key).getObjectContent();
                        fileDES.doDecryptFile(inputStream, new FileOutputStream(fileTmp));
                        if (fileTmp.exists()) {
                            fileTmp.renameTo(file);
                            Message message = new Message();
                            message.what = 111;
                            message.obj = file.getAbsolutePath();
                            handler.sendMessage(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            ThreadPool.getInstance().addTask(runnable);
        }
    }

    public static void downloadPic(final String key, final SketchImageView imageView) {
        final File file = new File(Constant.BASE_FILE_PATH + CommonUtil.md5(key));
        final File fileTmp = new File(Constant.BASE_FILE_PATH + CommonUtil.md5(key) + ".tmp");
        if (file.exists()) {
            imageView.displayImage(file.getAbsolutePath());
        } else {
            if (beginDownPic) {
                return;
            }
            final Handler handler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    if (msg.what == 111) {
                        String path = (String) msg.obj;
                        imageView.displayImage(path);
                    }
                }

                ;
            };
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        FileDesUtil fileDES = new FileDesUtil("zi85mc8F0m34S2F%*");
                        S3ObjectInputStream inputStream = Util.getS3Client(imageView.getContext().
                                getApplicationContext()).getObject(getBucketName(imageView.getContext()), key).getObjectContent();
                        fileDES.doDecryptFile(inputStream, new FileOutputStream(fileTmp));
                        if (fileTmp.exists()) {
                            fileTmp.renameTo(file);
                            Message message = new Message();
                            message.what = 111;
                            message.obj = file.getAbsolutePath();
                            handler.sendMessage(message);
                            beginDownPic = false;
                        } else {
                            beginDownPic = false;
                        }
                    } catch (Exception e) {
                        beginDownPic = false;
                        if (fileTmp.exists()) {
                            fileTmp.delete();
                        }
                        e.printStackTrace();
                    }
                }
            };
            beginDownPic = true;
            new Thread(runnable).start();
//			ThreadPool.getInstance().addTask(runnable);
        }
    }

    public static abstract class MyHttpRequest<T extends BaseResult> extends TextHttpResponseHandler {
        private Context context;
        private String apiPath;
        private Map<String, Object> paramsMap;

        public MyHttpRequest(Context context, String apiPath, Map<String, Object> paramsMap) {
            this.context = context;
            this.apiPath = apiPath;
            this.paramsMap = paramsMap;
        }

        // 打开 loading
        protected void onRequestStart() {
        }

        // 各种异常情况 打开 loading error
        protected void onNetOrServerFailure() {
        }

        // --各种异常情况细分
        protected void onNetTimeout() {
            // Toast.makeText(context.getApplicationContext(),"网络请求超时",Toast.LENGTH_SHORT).show();
        }

        // --各种异常情况细分
        protected void onNetNotConnect() {
            // Toast.makeText(context.getApplicationContext(),"请检查网络连接",Toast.LENGTH_SHORT).show();
        }

        // --各种异常情况细分
        protected void onServerError() {
            // Toast.makeText(context.getApplicationContext(),"网络异常",Toast.LENGTH_SHORT).show();
        }

        // --各种异常情况细分
        protected void onJsonParseError() {
            // Toast.makeText(context.getApplicationContext(),"网络异常",Toast.LENGTH_SHORT).show();
        }

        // 数据成功返回 关闭loading
        protected void onNetAndServerSuccess() {
        }

        // --数据成功返回细分
        public abstract void handleResult(T result);

        // --数据成功返回细分
        protected void handleResult(String result) {
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            onNetOrServerFailure();

            if (statusCode == 0) {
                if (throwable instanceof SocketTimeoutException) {
                    onNetTimeout();
                } else {
                    onNetNotConnect();
                }
            } else {
                onServerError();
            }

            if (Constant.IS_TEST) {
                LogUtil.e("statusCode=" + statusCode);
                if (!TextUtils.isEmpty(responseString)) {
                    LogUtil.e(responseString);
                }
                if (throwable != null) {
                    LogUtil.ex(throwable);
                }
            }
        }

        @SuppressWarnings({"unchecked"})
        @Override
        public void onSuccess(int i, Header[] headers, String s) {
            BaseResult result = JsonUtil.fromJson(s, (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
            if (result == null) {
                onNetOrServerFailure();
                onJsonParseError();
            } else {
                onNetAndServerSuccess();
                handleResult((T) result);
                handleResult(s);
            }
        }

        public Context getContext() {
            return context;
        }

        public String getUrl() {
            if (apiPath == null) {
                return "";
            }
            return apiPath;
        }

        public Map<String, Object> getParamsMap() {
            return paramsMap;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBytes) {
            String result = getResponseString(responseBytes, getCharset());
            if (Constant.IS_TEST && !TextUtils.isEmpty(result)) {
                LogUtil.i(result);
                LogUtil.toSdCard(result);
            }
            onSuccess(statusCode, headers, result);
        }
    }
}
