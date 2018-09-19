package com.goldwind.app.help.model;

import java.io.Serializable;

/**
 * Created by Yao on 2015/11/2.
 */
public class UserInfoResult extends BaseResult implements Serializable {
    private static final long serialVersionUID = 1L;
    public Data data;

    public static class Data implements Serializable {
        private static final long serialVersionUID = 1L;
        public String pernr;
        public String zhrotext;
        public String zhrptext;
        public String role;
        public String staffid;
        public String orgeh;
        public String staffname;
        public String version;
        public String password;
        public long time;

        public String accessKeyID;
        public String secretKey;
        public String bucketName;
    }
}
