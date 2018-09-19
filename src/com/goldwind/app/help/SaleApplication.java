/*
 * Copyright (C) 2007-2015 FBReader.ORG Limited <contact@fbreader.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package com.goldwind.app.help;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.goldwind.app.help.db.MyDB;
import com.goldwind.app.help.util.ApiUtil;
import com.goldwind.app.help.util.CommonUtil;
import com.goldwind.app.help.util.DeviceUuidFactory;
import com.goldwind.app.help.util.NetUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;
import java.util.Locale;

import me.xiaopan.sketch.DefaultImageSizeCalculator;
import me.xiaopan.sketch.Sketch;

public class SaleApplication extends Application {


    /**
     * 初始化ImageLoader
     */
    private static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build())
                .diskCache(new UnlimitedDiskCache(new File(Constant.BASE_CACHE_PATH))).denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            ApiUtil._appversion = pi.versionName == null ? "" : pi.versionName;
            ApiUtil._version = pi.versionCode + "";
        } catch (Exception e) {
        }


        ApiUtil._did = (new DeviceUuidFactory(this)).getDeviceUuid().toString();
        ApiUtil._dname = new Build().MODEL != null ? new Build().MODEL : "";
        ApiUtil._language = Locale.getDefault().getLanguage();
        ApiUtil._model = new Build().MODEL != null ? new Build().MODEL : "";
        ApiUtil._devicetoken = ApiUtil._did;
        ApiUtil._network = NetUtil.getNetworkState(this) + "";
        ApiUtil._from = "jinfeng";
        ApiUtil._systemtype = "android";

        Constant.BASE_DECODE_PATH = getFilesDir().getAbsolutePath() + "/";

        File file = new File(Constant.BASE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(Constant.BASE_CACHE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(Constant.BASE_FILE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(Constant.BASE_DECODE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }

        Sketch.with(this).getConfiguration().setImageSizeCalculator(new DefaultImageSizeCalculator() {
            @Override
            public int calculateInSampleSize(int outWidth, int outHeight, int targetWidth, int targetHeight) {
                // 如果目标尺寸都小于等于0，那就别计算了没意义
                if (targetWidth <= 0 && targetHeight <= 0) {
                    return 1;
                }

                // 如果目标尺寸都大于等于原始尺寸，也别计算了没意义
                if (targetWidth >= outWidth && targetHeight >= outHeight) {
                    return 1;
                }

                // 首先根据缩放后只要有任何一边小于等于目标即可的规则计算一遍inSampleSize
                int inSampleSize = 1;
                do {
                    inSampleSize *= 2;
                }
                while ((outWidth / inSampleSize) > targetWidth && (outHeight / inSampleSize) > targetHeight);

                return inSampleSize / 2;
            }
        });

        initImageLoader(this);
        MyDB.getInstance(this);

        String s1 = CommonUtil.spGetString(this, Constant.Key.ONLY_VIDEO);
        if (TextUtils.equals("false", s1)) {
            Constant.onlyVideo = false;
        } else {
            Constant.onlyVideo = true;
        }

        String s2 = CommonUtil.spGetString(this, Constant.Key.ONLY_WIFI);
        if (TextUtils.equals("false", s2)) {
            Constant.onlyWifi = false;
        } else {
            Constant.onlyWifi = true;
        }
    }
}
