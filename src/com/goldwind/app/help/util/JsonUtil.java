package com.goldwind.app.help.util;

import android.text.TextUtils;

import com.goldwind.app.help.model.PageMenu;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Json解析
 */
public class JsonUtil {

    List<PageMenu> list = new ArrayList<PageMenu>();

    public static <T> T fromJson(String str, Class<T> clazz) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        T object = null;
        try {
            Gson gson = new Gson();
            object = gson.fromJson(str, clazz);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return object;
    }

    public static <T> T fromJson(String str, Type typeOfT) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        T object = null;
        try {
            Gson gson = new Gson();
            object = gson.fromJson(str, typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return object;
    }

    public static String toJson(Object o) {
        return new Gson().toJson(o);
    }

    public List<PageMenu> getListMenu(String json, boolean isSecond) {
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = new JSONObject(array.get(i).toString());
                PageMenu pageMenu = new PageMenu();
                if (jsonObject.has("title")) {
                    pageMenu.title = isSecond ? "  " + jsonObject.getString("title") : jsonObject.getString("title");
                }
                if (jsonObject.has("id")) {
                    pageMenu.id = jsonObject.getString("id");
                }

                list.add(pageMenu);
                if (jsonObject.has("data")) {
                    getListMenu(jsonObject.getString("data"), true);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


}
