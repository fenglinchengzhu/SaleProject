package com.goldwind.app.help.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 图片异步加载类
 *
 * @author Leslie.Fang
 */
public class LoadDataFromServer {

    Context context;
    private String TAG = LoadDataFromServer.class.getSimpleName();
    private String url;
    private Map<String, String> map = null;
    private List<String> members = new ArrayList<String>();
    // 是否包含数组，默认是不包含
    private boolean has_Array = false;

    public LoadDataFromServer(Context context, String url,
                              Map<String, String> map) {
        this.url = url;
        this.map = map;
        has_Array = false;
        this.context = context;
    }

    public LoadDataFromServer(Context context, String url) {
        this.url = url;
        this.context = context;
    }

    //
    public LoadDataFromServer(Context context, String url,
                              Map<String, String> map, List<String> members) {
        this.url = url;
        this.map = map;
        this.members = members;
        has_Array = true;
    }

    @SuppressLint("HandlerLeak")
    public void postData(final DataCallBack dataCallBack) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 111 && dataCallBack != null) {
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    if (jsonObject != null) {

                        dataCallBack.onDataCallBack(jsonObject);

                    } else {
                        Toast.makeText(context, "获取数据为失败...", Toast.LENGTH_LONG)
                                .show();
                        Log.e(TAG, "获取数据失败");
                    }
                }
            }
        };

        new Thread() {

            @SuppressWarnings("rawtypes")
            public void run() {
                HttpClient client = new DefaultHttpClient();

                MultipartEntity entity = new MultipartEntity();
                Set keys = null;
                if (map != null) {
                    keys = map.keySet();
                }
                if (keys != null) {
                    Iterator iterator = keys.iterator();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        String value = (String) map.get(key);
                        if (key.equals("file")) {
                            File file = new File(value);
                            entity.addPart(key, new FileBody(file));
                        } else {

                            try {
                                entity.addPart(key, new StringBody(value,
                                        Charset.forName("UTF-8")));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
                // 如果包含数组，要把包含的数组放进去，项目目前只有members这个数组，所有固定键值，为了更灵活
                // 可以将传入自定义的键名......
                if (has_Array) {
                    for (int i = 0; i < members.size(); i++) {

                        try {
                            entity.addPart(
                                    "members[]",
                                    new StringBody(members.get(i), Charset
                                            .forName("UTF-8")));
                        } catch (UnsupportedEncodingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                client.getParams().setParameter(
                        CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
                // 请求超时
                client.getParams().setParameter(
                        CoreConnectionPNames.SO_TIMEOUT, 30000);
                HttpPost post = new HttpPost(url);
                post.setEntity(entity);
                StringBuilder builder = new StringBuilder();
                try {
                    HttpResponse response = client.execute(post);

                    if (response.getStatusLine().getStatusCode() == 200) {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(response.getEntity()
                                        .getContent(), Charset.forName("UTF-8")));
                        for (String s = reader.readLine(); s != null; s = reader
                                .readLine()) {
                            builder.append(s);
                        }
                        String builder_BOM = jsonTokener(builder.toString());
                        /*System.out.println("返回数据是------->>>>>>>>"
                                + builder.toString());*/
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject = JSONObject.parseObject(builder_BOM);
                            Message msg = new Message();
                            msg.what = 111;
                            msg.obj = jsonObject;
                            handler.sendMessage(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "获取数据错误");
                        }

                    }

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    Log.e(TAG, "服务器错误");

                } catch (ConnectTimeoutException e) {
                    e.printStackTrace();
                    Log.e(TAG, "连接超时");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }

    @SuppressLint("HandlerLeak")
    public void getData(final DataCallBack dataCallBack) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 111 && dataCallBack != null) {
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    if (jsonObject != null) {
                        dataCallBack.onDataCallBack(jsonObject);
                    } else {
                        Toast.makeText(context, "获取数据失败...", Toast.LENGTH_LONG)
                                .show();
                        Log.e(TAG, "获取数据失败");
                    }
                }
            }
        };

        new Thread() {

            @SuppressWarnings("rawtypes")
            public void run() {
                HttpParams httpparams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpparams,
                        10 * 1000);
                HttpClient client = new DefaultHttpClient(httpparams);
                HttpGet get = new HttpGet(url);
                Log.i("shihx", "url is:" + url);
                try {
                    HttpResponse response = client.execute(get);
                    int statusCode = response.getStatusLine().getStatusCode();
                    HttpEntity entity = response.getEntity();
                    String result = EntityUtils.toString(entity, "utf-8");
                    if (statusCode == 200) {
                        String builder_BOM = jsonTokener(result);
                        //Log.i("shihx","返回数据是------->>>>>>>>" + result);
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject = JSONObject.parseObject(builder_BOM);
                            Message msg = new Message();
                            msg.what = 111;
                            msg.obj = jsonObject;
                            handler.sendMessage(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "获取数据错误");
                        }

                    }

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    Log.e(TAG, "服务器错误");

                } catch (ConnectTimeoutException e) {
                    e.printStackTrace();
                    Log.e(TAG, "连接超时");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }

    private String jsonTokener(String in) {
        // consume an optional byte order mark (BOM) if it exists
        if (in != null && in.startsWith("\ufeff")) {
            in = in.substring(1);
        }
        return in;
    }

    /**
     * 网路访问调接口
     */
    public interface DataCallBack {
        void onDataCallBack(JSONObject data);
    }

}
