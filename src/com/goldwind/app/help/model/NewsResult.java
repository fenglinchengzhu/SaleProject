package com.goldwind.app.help.model;

import java.io.Serializable;
import java.util.List;

public class NewsResult extends BaseResult {
    private static final long serialVersionUID = 1L;
    public Data data;

    public static class Data implements Serializable {
        private static final long serialVersionUID = 1L;
        public List<NewsItem> resources;
        public String limit;
        public int start;
    }

    public static class NewsItem implements Serializable {
        private static final long serialVersionUID = 1L;
        public int id;
        public String entry;
        public String pic;
        public String summary;
        public String created_time;
        public String url;
        public String type;
        public String visited_num;
    }
}
