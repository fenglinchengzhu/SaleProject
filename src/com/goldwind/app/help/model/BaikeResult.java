package com.goldwind.app.help.model;

import java.io.Serializable;
import java.util.List;

public class BaikeResult extends BaseResult {
    private static final long serialVersionUID = 1L;
    public Data data;

    public static class Data implements Serializable {
        private static final long serialVersionUID = 1L;
        public List<BaikeItem> resources;
        public String limit;
        public int start;
    }

    public static class BaikeItem implements Serializable {
        private static final long serialVersionUID = 1L;
        public int id;
        public String entry;
        public String pic;
        public String summary;
        public String created_time;
        public String catalog_id;
    }
}
