package com.goldwind.app.help.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Yao on 2015/11/2.
 */
public class SearchResult extends BaseResult {
    private static final long serialVersionUID = 1L;
    public Data data;

    public static class Data implements Serializable {
        private static final long serialVersionUID = 1L;
        public List<ResourceItem> resources;
        public String limit;
        public int start;
    }

    public static class ResourceItem implements Serializable {
        private static final long serialVersionUID = 1L;

        public String cover;
        public int isrecommend;
        public String labelid;
        public int status;
        public String description;
        public int isdelete;
        public String fileaddress;
        public String size;
        public int type;
        public String classid;
        public int resourcesid;
        public long createTime;
        public String name;
        public String version;
        public int downloadState;
        public int staffid;
        public String groupid;
        public long readTime;
    }
}
