package com.goldwind.app.help.model;

import java.io.Serializable;

public class GetApkVersionResult extends BaseResult {
    private static final long serialVersionUID = 1L;
    public Data data;

    public static class Data implements Serializable {
        private static final long serialVersionUID = 1L;
        public String address;
        public int id;
    }
}
