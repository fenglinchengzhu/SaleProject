package com.goldwind.app.help.model;

import java.io.Serializable;

/**
 * Created by tiger on 16-2-22.
 */
public class CategoryItem implements Serializable {
    public int id;
    public String title;
    public boolean isParent;

    public CategoryItem() {
    }

    @Override
    public String toString() {
        return "CategoryItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", isParent=" + isParent +
                '}';
    }
}
