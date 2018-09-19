package com.goldwind.app.help.db;

import java.io.Serializable;

/**
 * Created by Yao on 2015/10/20.
 */
public class labelDto implements Serializable {
    private static final long serialVersionUID = -7357957182068635537L;
    public int labelid;
    public String labelname;
    public int version;
    public String createtime;
    public int isdelete;

    public int getLabelid() {
        return labelid;
    }

    public void setLabelid(int labelid) {
        this.labelid = labelid;
    }

    public String getLabelname() {
        return labelname;
    }

    public void setLabelname(String labelname) {
        this.labelname = labelname;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(int isdelete) {
        this.isdelete = isdelete;
    }
}
