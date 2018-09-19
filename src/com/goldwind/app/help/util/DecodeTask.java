package com.goldwind.app.help.util;

import android.os.AsyncTask;

import com.goldwind.app.help.Constant;
import com.goldwind.app.help.model.GetResourcesResult.ResourceItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class DecodeTask extends AsyncTask<Void, Integer, Boolean> {
    private File decodeFile;
    private File sourceFile;
    private DecodeListener decodeListener;
    private String userDir;

    public DecodeTask(ResourceItem mResourceItem, String userDir, DecodeListener decodeListener) {
        super();
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
        file = new File(Constant.BASE_DECODE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(Constant.BASE_DECODE_PATH + userDir + "/");
        if (!file.exists()) {
            file.mkdirs();
        }
        this.userDir = userDir;
        this.decodeListener = decodeListener;
        this.sourceFile = AppUtil.getFile(Constant.BASE_URL + "/" + mResourceItem.fileaddress);
        String decodeFilePath = Constant.BASE_DECODE_PATH + userDir + "/" + CommonUtil.md5(mResourceItem.name +
                mResourceItem.fileaddress)
                + AppUtil.getFileSuffix(mResourceItem.fileaddress);
        this.decodeFile = new File(decodeFilePath);
        System.gc();
        System.gc();
    }

    @Override
    protected void onPreExecute() {
        if (decodeListener != null) {
            decodeListener.onPreExecute();
        }

        File file = new File(Constant.BASE_DECODE_PATH + userDir + "/");
        if (!file.exists()) {
            file.mkdirs();
        }

        File[] fs = file.listFiles();
        if (fs != null && fs.length > 1) {
            File file1 = fs[0].lastModified() < fs[1].lastModified() ? fs[0] : fs[1];
            file1.delete();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            System.gc();
            System.gc();
            FileDesUtil fileDES = new FileDesUtil("zi85mc8F0m34S2F%*");
            if (decodeFile.exists()) {
                decodeFile.delete();
            }
            File file = new File(Constant.BASE_DECODE_PATH + userDir + "/");
            if (!file.exists()) {
                file.mkdirs();
            }
            fileDES.doDecryptFile(new FileInputStream(sourceFile), new FileOutputStream(decodeFile));
            System.gc();
            System.gc();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (decodeFile.exists()) {
                decodeFile.delete();
            }
            File file = new File(Constant.BASE_DECODE_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (decodeListener != null) {
            decodeListener.onPostExecute(result, decodeFile);
        }
    }

    public void setDecodeListener(DecodeListener decodeListener) {
        this.decodeListener = decodeListener;
    }

    public File getDecodeFile() {
        return decodeFile;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public interface DecodeListener {
        void onPreExecute();

        void onPostExecute(Boolean result, File decodeFile);
    }
}
