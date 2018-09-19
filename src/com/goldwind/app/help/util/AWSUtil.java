package com.goldwind.app.help.util;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

public class AWSUtil {
    private static AmazonS3Client sS3Client;

    public static AmazonS3Client getS3Client() {
        if (sS3Client == null) {
            sS3Client = new AmazonS3Client(new BasicAWSCredentials("AKIAOKZOY5SBT7QTTXSQ", "OmbqnQwecyy5iOvWs8zzJGMk8qXOiNKpf0AgRNpU"));
            sS3Client.setRegion(Region.getRegion(Regions.CN_NORTH_1));
        }
        return sS3Client;
    }
}
