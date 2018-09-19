package com.goldwind.app.help.util;

import android.content.Context;

import com.goldwind.app.help.Constant;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 应用工具类
 */
public class AppUtil {

    public static String getDeviceId(Context context) {
        return new DeviceUuidFactory(context).getDeviceUuid().toString().replaceAll("-", "");
    }

    public static String getDatePath(String s) throws MalformedURLException {
//		LogUtil.d("aaaaaaaaaaaa====="+s);
        URL url = new URL(s);
        String path = url.getFile();
        int index = path.lastIndexOf("/");
        path = path.substring(index - 18, index + 1);
        return path;
    }

    public static String getFileSuffix(String s) {
        int index = s.lastIndexOf('.');
        String result = s.substring(index, s.length());
        return result;
    }

    // url 转 本地文件
    public static File getFile(String s) {
        URL url = null;
        try {
            url = new URL(s);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url == null) {
            return null;
        }
        String path = url.getFile();
        String fileName = new File(path).getName();
        int index = path.lastIndexOf("/");
        String datePath = path.substring(index - 18, index + 1);
        return new File(Constant.BASE_FILE_PATH + datePath, fileName);
    }

}
