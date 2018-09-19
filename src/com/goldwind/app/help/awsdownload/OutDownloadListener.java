package com.goldwind.app.help.awsdownload;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.goldwind.app.help.model.GetResourcesResult.ResourceItem;

public class OutDownloadListener implements TransferListener {

    private ResourceItem resourceItem;

    public OutDownloadListener(ResourceItem resourceItem) {
        super();
        this.resourceItem = resourceItem;
    }

    public ResourceItem getResourceItem() {
        return resourceItem;
    }

    @Override
    public void onError(int id, Exception e) {
    }

    @Override
    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
    }

    @Override
    public void onStateChanged(int id, TransferState state) {
    }

}
