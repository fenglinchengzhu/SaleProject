package com.goldwind.app.help.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Yao on 2015/11/2.
 */
public class GetResourcesResult extends BaseResult {
    private static final long serialVersionUID = 1L;
    public Data data;

    public static class Data implements Serializable {
        private static final long serialVersionUID = 1L;
        public List<CategoryItem> category;
        public List<LabelItem> label;
        public List<ResourceItem> resource;
        public Version version;
        public String staffRole;
        public String groupids;

        public String accessKeyID;
        public String secretKey;
        public String bucketName;
    }

    public static class CategoryItem implements Serializable {
        private static final long serialVersionUID = 1L;
        public int id;
        public int parentId;
        public long createTime;
        public String name;
        public boolean isdelete;
        public int number;
        public String version;

        // 附加
        public List<CategoryItem> subList;
    }

    public static class ResourceItem implements Serializable {
        private static final long serialVersionUID = 1L;
        public String cover;
        public String size;
        public int isrecommend;
        public String labelid;
        public int status;
        public String description;
        public int isdelete;
        public String fileaddress;
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

    public static class BaikeItem implements Serializable {
        private static final long serialVersionUID = 1L;
        public int id;
        public String entry;
        public String pic;
        public String summary;
        public String created_time;
        public String catalog_id;
        public String url;
        public String type;

        @Override
        public String toString() {
            return "BaikeItem{" +
                    "id=" + id +
                    ", entry='" + entry + '\'' +
                    ", pic='" + pic + '\'' +
                    ", summary='" + summary + '\'' +
                    ", created_time='" + created_time + '\'' +
                    ", catalog_id='" + catalog_id + '\'' +
                    '}';
        }
    }

    public static class LabelItem implements Serializable {
        private static final long serialVersionUID = 1L;
        public int labelid;
        public String labelname;
        public long createTime;
        public boolean isdelete;
        public String version;
    }

    public static class Version implements Serializable {
        private static final long serialVersionUID = 1L;
        public String categoryVersion;
        public String resourceVersion;
        public String labelVersion;
    }
}
