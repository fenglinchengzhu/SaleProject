package com.goldwind.app.help.util;

import android.os.AsyncTask;

import com.goldwind.app.help.Constant;
import com.goldwind.app.help.model.GetResourcesResult.ResourceItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class DecodeBufferTask extends AsyncTask<Void, Integer, byte[]> {
    private File sourceFile;
    private DecodeListener decodeListener;

    public DecodeBufferTask(ResourceItem mResourceItem, DecodeListener decodeListener) {
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
        this.decodeListener = decodeListener;
        this.sourceFile = AppUtil.getFile(Constant.BASE_URL + "/" + mResourceItem.fileaddress);
        System.gc();
    }

    @Override
    protected void onPreExecute() {
        if (decodeListener != null) {
            decodeListener.onPreExecute();
        }
    }

    @Override
    protected byte[] doInBackground(Void... params) {
        try {
            System.gc();
            FileDesUtil fileDES = new FileDesUtil("zi85mc8F0m34S2F%*");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            fileDES.doDecryptFile(new FileInputStream(sourceFile), baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(byte[] result) {
        if (decodeListener != null) {
            decodeListener.onPostExecute(result);
        }
    }

    public void setDecodeListener(DecodeListener decodeListener) {
        this.decodeListener = decodeListener;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public interface DecodeListener {
        void onPreExecute();

        void onPostExecute(byte[] result);
    }
}
