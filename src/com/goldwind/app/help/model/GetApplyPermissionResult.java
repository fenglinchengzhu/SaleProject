package com.goldwind.app.help.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Yao on 2015/11/2.
 */
public class GetApplyPermissionResult extends BaseResult implements Serializable {
    private static final long serialVersionUID = 1L;
    public Data data;

    public static class Data implements Serializable {
        private static final long serialVersionUID = 1L;
        public List<DataBean> data;
        public List<StartBean> startApply;
        public String role;
    }

    public static class DataBean implements Serializable {
        private static final long serialVersionUID = 1L;
        public long createtime;
        public int permissionid;
        public String reason;
        public int status;
        public int beforepermission;
        public int staffid;
        public int applypermission;
    }

    public static class StartBean implements Serializable {
        private static final long serialVersionUID = 1L;
        public long createtime;
        public int permissionid;
        public String reason;
        public int status;
        public int beforepermission;
        public int staffid;
        public int applypermission;
    }
}
