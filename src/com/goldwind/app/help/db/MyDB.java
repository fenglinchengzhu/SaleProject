package com.goldwind.app.help.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.goldwind.app.help.Constant;
import com.goldwind.app.help.download.StorageUtils;
import com.goldwind.app.help.model.GetResourcesResult;
import com.goldwind.app.help.model.GetResourcesResult.ResourceItem;
import com.goldwind.app.help.util.AppUtil;
import com.goldwind.app.help.util.CommonUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MyDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "JinFeng.db"; // 数据库名称
    private static final int version = 1; // 数据库版本
    private static MyDB instance;
    private SQLiteDatabase mDatabase;
    private AtomicInteger mOpenCounter = new AtomicInteger();

    private MyDB(Context context) {
        super(context, DB_NAME, null, version);
    }

    public static synchronized MyDB getInstance(Context context) {
        if (instance == null) {
            // 一个数据库连接
            instance = new MyDB(context);
        }
        return instance;
    }

    private synchronized SQLiteDatabase getSafeDatabase() {
        if (mOpenCounter.incrementAndGet() == 1) {
            mDatabase = instance.getWritableDatabase();
        }
        return mDatabase;
    }

    private synchronized void closeSafeDatabase() {
        if (mOpenCounter.decrementAndGet() == 0) {
            // 没有被占用关闭
            mDatabase.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 标签表
        String sql1 = "create table label(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT, " // 主键
                + "labelid  INTEGER not null, " // 标签ID
                + "labelname VARCHAR(200) not null, " // 标签名称
                + "version VARCHAR(30) not null," // 版本号
                + "createTime VARCHAR(30) not null,"// 创建时间
                + "isdelete INTEGER not null" // 是否删除
                + ")";
        // 分类表
        String sql2 = "create table category(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT, " // 主键
                + "id  INTEGER not null, " // 分类ID
                + "name VARCHAR(200) not null, " // 分类名称
                + "parentId INTEGER not null," // 分类父ID
                + "version VARCHAR(30) not null," // 版本号
                + "number INTEGER not null," // 阅读数量
                + "createTime VARCHAR(30) not null,"// 创建时间
                + "isdelete INTEGER not null" // 是否删除
                + ")";
        // 资源表
        String sql3 = "create table resource(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT, " // 主键
                + "resourcesid  INTEGER not null, " // 资源ID
                + "name VARCHAR(200) not null, " // 资源名称
                + "type INTEGER not null," // 资源类型
                + "fileaddress TEXT not null," // 资源地址
                + "size VARCHAR(30) not null,"
                + "version VARCHAR(30) not null," // 版本号
                + "status INTEGER not null," // 资源权限
                + "createTime VARCHAR(30) not null,"// 创建时间
                + "classid TEXT not null," // 分类ID
                + "labelid TEXT," // 标签IDs
                + "cover TEXT," // 封面
                + "description TEXT," // 描述
                + "isrecommend INTEGER not null," // 是否推荐
                + "isdelete INTEGER not null," // 是否删除
                + "groupid TEXT," // 组ids
                + "staffid INTEGER," // 员工id
                + "readTime INTEGER," // 阅读时间
                + "downloadState INTEGER not null" // 下载状态 0：没有下载 1：下载完毕
                + ")";

        // baike
        String sql4 = "create table baike(" + "id INTEGER PRIMARY KEY AUTOINCREMENT, " // 主键
                + "entry TEXT not null, "
                + "pic TEXT , "
                + "summary TEXT , "
                + "created_time TEXT, "
                + "catalog_id TEXT"// 创建时间
                + ")";

        // 分类表
        String sql5 = "create table history(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT, " // 主键
                + "name VARCHAR(200) not null, " // 分类名称
                + "createTime VARCHAR(30) not null"// 创建时间
                + ")";

        //保存输出入输出量表
        String sql6 = "create table output(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT, " // 主键
                + "calcu_id  VARCHAR(30) not null, " // 存入时候的ID 唯一性
                + "calcu_name VARCHAR(200) not null, " // 存入名称
                + "outA VARCHAR(30) not null," // 机型
                + "outB VARCHAR(30) ," // 装机容量
                + "outC VARCHAR(30) ," // 总投资
                + "outD VARCHAR(30) ," // 建设投资
                + "outE VARCHAR(30) ," // 建设期利息
                + "outF VARCHAR(30) ," // 流动资金
                + "outG VARCHAR(30) ," // 总收入
                + "outH VARCHAR(30) ," // 销售税金及附加
                + "outI VARCHAR(30) ," // 总成本
                + "outJ VARCHAR(30) ," // 年均利润总额
                + "outK VARCHAR(30) ," // 年均所得税
                + "outL VARCHAR(30) ," // 内部收益率
                + "outM VARCHAR(30) ," // 全部投资内部收益率
                + "outN VARCHAR(30) ," // 全部投资内部收益率所得税前
                + "outO VARCHAR(30) ," // 全部投资内部收益率所得税后
                + "outP VARCHAR(30) ," // 资本金内部收益率
                + "outQ VARCHAR(30) ," // 基准收益率
                + "outR VARCHAR(30) ," // 财务净现值
                + "outS VARCHAR(30) ," // 全部投资财务净现值
                + "outT VARCHAR(30) ," // 全部投资财务净现值所得税前
                + "outU VARCHAR(30) ," // 全部投资财务净现值所得税后
                + "outV VARCHAR(30) ," // 资本金财务净现值 (10%)
                + "outW VARCHAR(30) ," // 投资回收期

                + "outX VARCHAR(30) ," // 总投资收益率
                + "outY VARCHAR(30) ," // 总投资利税率
                + "outZ VARCHAR(30) ," // 资本金净利润率

                + "outA1 VARCHAR(30) ," // 生产能力利用率盈亏平衡点
                + "outB1 VARCHAR(30) ," // 贷款偿还期
                + "outC1 VARCHAR(30) ," // 资产负债率
                + "outD1 VARCHAR(30) ," // 度电成本
                + "outE1 VARCHAR(30) ," // 单位千瓦造价（动态）

                + "outF1 VARCHAR(30) ," // 年发电量
                + "outG1 VARCHAR(30) ," // 单位千瓦售价
                + "outH1 VARCHAR(30) ," // 标准运行小时数

                + ")";


        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
        db.execSQL(sql4);
        db.execSQL(sql5);
//		db.execSQL(sql6);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // 转化
    private GetResourcesResult.ResourceItem createResourceItem(Cursor cursor) {
        GetResourcesResult.ResourceItem item = new GetResourcesResult.ResourceItem();
        item.cover = cursor.getString(cursor.getColumnIndex("cover"));
        item.isrecommend = cursor.getInt(cursor.getColumnIndex("isrecommend"));
        item.labelid = cursor.getString(cursor.getColumnIndex("labelid"));
        item.status = cursor.getInt(cursor.getColumnIndex("status"));
        item.description = cursor.getString(cursor.getColumnIndex("description"));
        item.isdelete = cursor.getInt(cursor.getColumnIndex("isdelete"));
        item.fileaddress = cursor.getString(cursor.getColumnIndex("fileaddress"));
        item.type = cursor.getInt(cursor.getColumnIndex("type"));
        item.classid = cursor.getString(cursor.getColumnIndex("classid"));
        item.resourcesid = cursor.getInt(cursor.getColumnIndex("resourcesid"));
        String createTime = cursor.getString(cursor.getColumnIndex("createTime"));
        item.createTime = Long.valueOf(createTime);
        item.version = cursor.getString(cursor.getColumnIndex("version"));
        item.name = cursor.getString(cursor.getColumnIndex("name"));
        item.downloadState = cursor.getInt(cursor.getColumnIndex("downloadState"));
        item.staffid = cursor.getInt(cursor.getColumnIndex("staffid"));
        item.groupid = cursor.getString(cursor.getColumnIndex("groupid"));
        item.readTime = cursor.getLong(cursor.getColumnIndex("readTime"));
        item.size = cursor.getString(cursor.getColumnIndex("size"));
        return item;
    }

    // 转化
    private GetResourcesResult.CategoryItem createCategory(Cursor cursor) {
        GetResourcesResult.CategoryItem item = new GetResourcesResult.CategoryItem();
        if (cursor.getInt(cursor.getColumnIndex("isdelete")) == 1) {
            item.isdelete = true;
        } else {
            item.isdelete = false;
        }
        item.id = cursor.getInt(cursor.getColumnIndex("id"));
        item.parentId = cursor.getInt(cursor.getColumnIndex("parentId"));
        item.number = cursor.getInt(cursor.getColumnIndex("number"));
        String createTime = cursor.getString(cursor.getColumnIndex("createTime"));
        item.createTime = Long.valueOf(createTime);
        item.version = cursor.getString(cursor.getColumnIndex("version"));
        item.name = cursor.getString(cursor.getColumnIndex("name"));
        return item;
    }


    private GetResourcesResult.BaikeItem createBaike(Cursor cursor) {
        GetResourcesResult.BaikeItem item = new GetResourcesResult.BaikeItem();

        item.id = cursor.getInt(cursor.getColumnIndex("id"));
        item.entry = cursor.getString(cursor.getColumnIndex("entry"));
        item.pic = cursor.getString(cursor.getColumnIndex("pic"));

        item.summary = cursor.getString(cursor.getColumnIndex("summary"));
        item.created_time = cursor.getString(cursor.getColumnIndex("created_time"));
        item.catalog_id = cursor.getString(cursor.getColumnIndex("catalog_id"));
        return item;
    }

    /**
     * 获取子分类
     */
    public ArrayList<String> getHistory(String limit) {
        mDatabase = getReadableDatabase();
        ArrayList<String> result = new ArrayList<String>();
        Cursor cursor = mDatabase.rawQuery("select * from history " + " order by createTime desc" + " limit " + limit, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            result.add(name);
        }
        cursor.close();
        mDatabase.close();
        return result;
    }

    /**
     * 获取子分类
     */
    public void saveHistory(String name) {
        mDatabase = getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put("createTime", System.currentTimeMillis());
        if (isExist(mDatabase, "history", "name", name)) {
            mDatabase.update("history", values, "name = ?", new String[]{name});
        } else {
            values.put("name", name);
            mDatabase.insert("history", null, values);
        }
        closeSafeDatabase();
    }

    /**
     * 获取子分类
     */
    public ArrayList<GetResourcesResult.CategoryItem> getCategoryItemList(String parentId) {
        mDatabase = getReadableDatabase();
        ArrayList<GetResourcesResult.CategoryItem> result = new ArrayList<GetResourcesResult.CategoryItem>();
        Cursor cursor = mDatabase.rawQuery("select * from category where parentId = ? and isdelete <> 1 ", new String[]{parentId});
        while (cursor.moveToNext()) {
            result.add(createCategory(cursor));
        }
        cursor.close();
        mDatabase.close();
        return result;
    }

    /**
     * 获取子分类
     */
    public ArrayList<GetResourcesResult.CategoryItem> getPCategoryItemList(String parentId) {
        mDatabase = getReadableDatabase();
        ArrayList<GetResourcesResult.CategoryItem> result = new ArrayList<GetResourcesResult.CategoryItem>();
        Cursor cursor = mDatabase.rawQuery("select * from category where parentId = ? and isdelete <> 1 and name = ?", new String[]{parentId, "营销中心"});
        while (cursor.moveToNext()) {
            result.add(createCategory(cursor));
        }
        cursor.close();
        mDatabase.close();
        return result;
    }

    /**
     * 推荐的资源
     */
    public int getStaffidNum() {
        mDatabase = getReadableDatabase();
        Cursor cursor = mDatabase.rawQuery("select staffid from resource group by staffid", null);
        int i = 0;
        while (cursor.moveToNext()) {
            i++;
        }
        cursor.close();
        mDatabase.close();
        return i;
    }

    /**
     * 推荐的资源
     */
    public ArrayList<GetResourcesResult.ResourceItem> getTuiJianResourceList(String staffid) {
        mDatabase = getReadableDatabase();
        ArrayList<GetResourcesResult.ResourceItem> result = new ArrayList<GetResourcesResult.ResourceItem>();
        Cursor cursor = mDatabase.rawQuery("select * from resource where staffid = ? and isrecommend = 1 and isdelete <> 1 order by version desc",
                new String[]{staffid});
        while (cursor.moveToNext()) {
            result.add(createResourceItem(cursor));
        }
        cursor.close();
        mDatabase.close();
        return result;
    }

    /**
     * 搜索用户的资源
     */
    public ArrayList<GetResourcesResult.ResourceItem> searchResourceList(String staffid, String key) {
        mDatabase = getReadableDatabase();
        ArrayList<GetResourcesResult.ResourceItem> result = new ArrayList<GetResourcesResult.ResourceItem>();
        Cursor cursor = mDatabase.rawQuery("select * from resource where staffid = ? and isdelete <> 1 and name like '%" + key + "%' order by version desc",
                new String[]{staffid});
        while (cursor.moveToNext()) {
            result.add(createResourceItem(cursor));
        }
        cursor.close();
        mDatabase.close();
        return result;
    }

    /**
     * 某一分类的资源
     */
    public ArrayList<GetResourcesResult.ResourceItem> getResourceListByClass(String staffid, String classid) {
        mDatabase = getReadableDatabase();
        ArrayList<GetResourcesResult.ResourceItem> result = new ArrayList<GetResourcesResult.ResourceItem>();
        Cursor cursor = mDatabase.rawQuery("select * from resource where staffid = ? and isdelete <> 1 order by version desc", new String[]{staffid});
        while (cursor.moveToNext()) {
            String classids = cursor.getString(cursor.getColumnIndex("classid"));
            if (isInClassid(classid, classids)) {
                result.add(createResourceItem(cursor));
            }
        }
        cursor.close();
        mDatabase.close();
        return result;
    }

    private boolean isInClassid(String classid, String classids) {
        if (TextUtils.isEmpty(classids)) {
            return false;
        }
        String[] classidArr = classids.split(",");
        if (classidArr == null || classidArr.length == 0) {
            return false;
        }
        for (String string : classidArr) {
            if (TextUtils.equals(string, classid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取已经下载的资源
     */
    public synchronized ArrayList<GetResourcesResult.ResourceItem> getReadResourceList(String staffid, int type, String status) {
        mDatabase = getReadableDatabase();
        ArrayList<GetResourcesResult.ResourceItem> result = new ArrayList<GetResourcesResult.ResourceItem>();
        Cursor cursor = null;
        if (type != 0) {
            cursor = mDatabase.rawQuery(
                    "select * from resource where staffid = ? and isdelete <> 1 and type = ? and status <= ? and downloadState = 1 order by readTime desc",
                    new String[]{staffid, type + "", status});
        } else {
            cursor = mDatabase.rawQuery("select * from resource where staffid = ? and isdelete <> 1 and status <= ? and downloadState = 1 order by readTime desc",
                    new String[]{staffid, status});
        }
        while (cursor.moveToNext()) {
            result.add(createResourceItem(cursor));
        }
        cursor.close();
        mDatabase.close();
        return result;
    }

    /**
     * 获取用户的资源
     */
    public ArrayList<GetResourcesResult.ResourceItem> getResourceList(String staffid) {
        mDatabase = getReadableDatabase();
        ArrayList<GetResourcesResult.ResourceItem> result = new ArrayList<GetResourcesResult.ResourceItem>();
        Cursor cursor = mDatabase.rawQuery("select * from resource where staffid = ? and isdelete <> 1 order by version desc", new String[]{staffid});
        while (cursor.moveToNext()) {
            result.add(createResourceItem(cursor));
        }
        cursor.close();
        mDatabase.close();
        return result;
    }

    /**
     * 获取用户的资源(已下载,需要删除的)
     */
    public ArrayList<GetResourcesResult.ResourceItem> getResourceListHasDown(String staffid, String status) {
        mDatabase = getReadableDatabase();
        ArrayList<GetResourcesResult.ResourceItem> result = new ArrayList<GetResourcesResult.ResourceItem>();
        Cursor cursor = mDatabase.rawQuery("select * from resource where staffid = ? and downloadState = 1 and (isdelete = 1 or status > " + status
                + ") order by version desc", new String[]{staffid});
        while (cursor.moveToNext()) {
            result.add(createResourceItem(cursor));
        }
        cursor.close();
        mDatabase.close();
        return result;
    }

    /**
     * 获取单个资源
     */
    public GetResourcesResult.ResourceItem getResourceById(String staffid, String resourcesid) {
        mDatabase = getReadableDatabase();
        Cursor cursor = mDatabase
                .rawQuery("select * from resource where staffid = ? and resourcesid = ? order by version desc", new String[]{staffid, resourcesid});
        GetResourcesResult.ResourceItem result = null;
        if (cursor.moveToFirst()) {
            result = createResourceItem(cursor);
        }
        cursor.close();
        mDatabase.close();
        return result;
    }

    /**
     * 获取下载队列
     */
    public ArrayList<GetResourcesResult.ResourceItem> getResourceDownloadList(String staffid, boolean onlyVideo, String status) {
        mDatabase = getReadableDatabase();
        ArrayList<GetResourcesResult.ResourceItem> result = new ArrayList<GetResourcesResult.ResourceItem>();
        Cursor cursor = null;
        if (onlyVideo) {
            cursor = mDatabase.rawQuery(
                    "select * from resource where downloadState <> 1 and isdelete <> 1 and staffid = ? and type <> 2 and status <= ? order by version desc",
                    new String[]{staffid, status});
        } else {
            cursor = mDatabase.rawQuery("select * from resource where downloadState <> 1 and isdelete <> 1 and staffid = ? and status <= ? order by version desc",
                    new String[]{staffid, status});
        }
        while (cursor.moveToNext()) {
            result.add(createResourceItem(cursor));
        }
        cursor.close();
        mDatabase.close();
        return result;
    }

    /**
     * 获取资源数量
     */
    public int getResourceCount(String staffid) {
        mDatabase = getReadableDatabase();
        Cursor cursor = mDatabase.rawQuery("select count(*) from resource where staffid = ? and isdelete <> 1", new String[]{staffid});
        cursor.moveToFirst();
        int i = cursor.getInt(0);
        cursor.close();
        mDatabase.close();
        return i;
    }

	/* ----------------------写操作---------------------- */

    public void deleteResources(Context context, ArrayList<ResourceItem> list, String staffid, int staffidNum) {
        mDatabase = getSafeDatabase();
        for (ResourceItem resourceItem : list) {
            mDatabase.delete("resource", "resourcesid = ? and staffid = ?", new String[]{resourceItem.resourcesid + "", staffid});
            CommonUtil.spPutInt(context, staffid + "_D_" + resourceItem.resourcesid, 1);
            if (staffidNum == 1 && resourceItem.type != 3) {
                try {
                    String path = AppUtil.getDatePath(Constant.BASE_URL + "/" + resourceItem.fileaddress);
                    path = Constant.BASE_FILE_PATH + path;
                    String fileName = new File(new URL(Constant.BASE_URL + "/" + resourceItem.fileaddress).getFile()).getName();
                    File file = new File(path, fileName);
                    StorageUtils.delete(file);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        closeSafeDatabase();
    }

    /**
     * 检查当前用户的文件下载状态
     */
    public void checkDownloadState(String staffid, String status) {
        ArrayList<GetResourcesResult.ResourceItem> list = getResourceList(staffid);
        mDatabase = getSafeDatabase();
        if (mDatabase.isOpen()) {
            for (ResourceItem resourceItem : list) {
                if (resourceItem.type == 3) {
                    continue;
                }
                try {
                    String url = Constant.BASE_URL + "/" + resourceItem.fileaddress;
                    String path = AppUtil.getDatePath(url);
                    path = Constant.BASE_FILE_PATH + path;
                    File file = new File(path);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    String fileName = new File(new URL(url).getFile()).getName();
                    ContentValues contentValues = new ContentValues();
                    if (new File(path, fileName).exists()) {
                        contentValues.put("downloadState", 1);
                    } else {
                        contentValues.put("downloadState", 0);
                    }
                    mDatabase.update("resource", contentValues, "resourcesid = ? and staffid = ?", new String[]{resourceItem.resourcesid + "", staffid});
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        closeSafeDatabase();
    }

    // 已下载需要删除的
    public void checkFileDelete(String staffid, String status) {
        ArrayList<GetResourcesResult.ResourceItem> list1 = getResourceListHasDown(staffid, status);
        mDatabase = getSafeDatabase();
        for (ResourceItem resourceItem : list1) {
            try {
                String url = Constant.BASE_URL + "/" + resourceItem.fileaddress;
                String path = AppUtil.getDatePath(url);
                path = Constant.BASE_FILE_PATH + path;
                File file = new File(path);
                if (file.exists()) {
                    String fileName = new File(new URL(url).getFile()).getName();
                    File file2 = new File(path, fileName);
                    if (file2.exists() && file2.delete()) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("downloadState", 0);
                        mDatabase.update("resource", contentValues, "resourcesid = ? and staffid = ?", new String[]{resourceItem.resourcesid + "", staffid});
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        closeSafeDatabase();
    }

    /**
     * 更新下载状态
     */
    public void updateResourceDownloadState(String staffid, int resourcesid, int value) {
        try {
            mDatabase = getSafeDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("downloadState", value);
            if (mDatabase.isOpen()) {
                mDatabase.update("resource", contentValues, "resourcesid = ? and staffid = ?", new String[]{resourcesid + "", staffid});
            }
            closeSafeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新阅读时间
     */
    public void updateResourceReadTime(String staffid, int resourcesid, long readTime) {
        try {
            mDatabase = getSafeDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("readTime", readTime);
            if (mDatabase.isOpen()) {
                mDatabase.update("resource", contentValues, "resourcesid = ? and staffid = ?", new String[]{resourcesid + "", staffid});
            }
            closeSafeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isDelete(String groupids1, String groupids2) {
        String[] groupids1s = groupids1.split(",");
        String[] groupids2s = groupids2.split(",");
        for (String string : groupids1s) {
            if (isInGroups(string, groupids2s)) {
                return false;
            }
        }
        return true;
    }

    private boolean isInGroups(String groupid, String[] groupids2s) {
        for (String string : groupids2s) {
            if (TextUtils.equals(string, groupid)) {
                return true;
            }
        }
        return false;
    }

    public void insert(Context context, ResourceItem item, String staffid) {
        if (TextUtils.isEmpty(staffid)) {
            return;
        }
        mDatabase = getSafeDatabase();
        item.staffid = Integer.valueOf(staffid);
        ContentValues contentValues = new ContentValues();
        contentValues.put("cover", item.cover);
        contentValues.put("isrecommend", item.isrecommend);
        contentValues.put("labelid", item.labelid);
        contentValues.put("status", item.status);
        contentValues.put("description", item.description);
        contentValues.put("fileaddress", item.fileaddress);
        contentValues.put("type", item.type);
        contentValues.put("classid", item.classid);
        contentValues.put("createTime", item.createTime + "");
        contentValues.put("version", item.version);
        contentValues.put("name", item.name == null ? "" : item.name);
        contentValues.put("groupid", item.groupid);
        contentValues.put("staffid", item.staffid);
        contentValues.put("isdelete", item.isdelete);
        contentValues.put("size", item.size);

        if (item.type == 3) {
            contentValues.put("downloadState", 1);
        } else {
            try {
                String url = Constant.BASE_URL + "/" + item.fileaddress;
                String path = AppUtil.getDatePath(url);
                path = Constant.BASE_FILE_PATH + path;
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String fileName = new File(new URL(url).getFile()).getName();
                if (new File(path, fileName).exists()) {
                    contentValues.put("downloadState", 1);
                } else {
                    contentValues.put("downloadState", 0);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                contentValues.put("downloadState", 0);
            }
        }

        // 是否存在
        if (isExist(mDatabase, "resource", "resourcesid", item.resourcesid, "staffid", item.staffid)) {
            mDatabase.update("resource", contentValues, "resourcesid = ? and staffid = ?", new String[]{item.resourcesid + "", item.staffid + ""});
        } else {
            contentValues.put("resourcesid", item.resourcesid);
            mDatabase.insert("resource", null, contentValues);
            CommonUtil.spPutInt(context, staffid + "_D_" + item.resourcesid, 0);
        }

        closeSafeDatabase();
    }

    /**
     * 更新资源
     */
    public void update(Context context, GetResourcesResult result, boolean isDelete, String staffid) {
        mDatabase = getSafeDatabase();
        if (isDelete) {
            mDatabase.delete("resource", "staffid = ?", new String[]{staffid});
        }
        // 分类
        for (GetResourcesResult.CategoryItem item : result.data.category) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("parentId", item.parentId);
            contentValues.put("createTime", item.createTime + "");
            contentValues.put("name", item.name == null ? "" : item.name);
            if (item.isdelete) {
                contentValues.put("isdelete", 1);
            } else {
                contentValues.put("isdelete", 0);
            }
            contentValues.put("number", item.number);
            contentValues.put("version", item.version);
            if (isExist(mDatabase, "category", "id", item.id)) {
                mDatabase.update("category", contentValues, "id = ?", new String[]{item.id + ""});
            } else {
                contentValues.put("id", item.id);
                mDatabase.insert("category", null, contentValues);
            }
        }
        // 标签
        for (GetResourcesResult.LabelItem item : result.data.label) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("createTime", item.createTime + "");
            contentValues.put("labelname", item.labelname == null ? "" : item.labelname);
            if (item.isdelete) {
                contentValues.put("isdelete", 1);
            } else {
                contentValues.put("isdelete", 0);
            }
            contentValues.put("version", item.version);
            if (isExist(mDatabase, "label", "labelid", item.labelid)) {
                mDatabase.update("label", contentValues, "labelid = ?", new String[]{item.labelid + ""});
            } else {
                contentValues.put("labelid", item.labelid);
                mDatabase.insert("label", null, contentValues);
            }
        }
        // 资源
        for (GetResourcesResult.ResourceItem item : result.data.resource) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("cover", item.cover);
            contentValues.put("isrecommend", item.isrecommend);
            contentValues.put("labelid", item.labelid);
            contentValues.put("status", item.status);
            contentValues.put("description", item.description);
            contentValues.put("fileaddress", item.fileaddress);
            contentValues.put("type", item.type);
            contentValues.put("classid", item.classid);
            contentValues.put("createTime", item.createTime + "");
            contentValues.put("version", item.version);
            contentValues.put("name", item.name == null ? "" : item.name);
            contentValues.put("groupid", item.groupid);
            contentValues.put("staffid", item.staffid);
            contentValues.put("size", item.size);
            if (item.type == 3) {
                contentValues.put("downloadState", 1);
            } else {
                contentValues.put("downloadState", 0);
            }
            if (isDelete(item.groupid, result.data.groupids)) {
                contentValues.put("isdelete", 1);
            } else {
                contentValues.put("isdelete", item.isdelete);
            }
            // 是否存在
            if (isExist(mDatabase, "resource", "resourcesid", item.resourcesid, "staffid", item.staffid)) {
                mDatabase.update("resource", contentValues, "resourcesid = ? and staffid = ?", new String[]{item.resourcesid + "", item.staffid + ""});
            } else {
                if (CommonUtil.spGetInt(context, item.staffid + "_D_" + item.resourcesid) == 0) {
                    contentValues.put("resourcesid", item.resourcesid);
                    mDatabase.insert("resource", null, contentValues);
                }
            }
        }
        closeSafeDatabase();
    }

    public int getNewUpdateNum(List<ResourceItem> resourceList) {
        mDatabase = getReadableDatabase();
        int i = 0;
        for (ResourceItem item : resourceList) {
            if (item.isdelete == 1) {
                continue;
            }

            if (isExist(mDatabase, "resource", "resourcesid", item.resourcesid, "staffid", item.staffid)) {
                i++;
            }
        }
        mDatabase.close();
        return i;
    }

    // 查询是否存在标签、分类
    private boolean isExist(SQLiteDatabase db, String tableName, String colName, int id) {
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select count(*) from " + tableName + " where " + colName + " = ?", new String[]{id + ""});
            cursor.moveToFirst();
            int i = cursor.getInt(0);
            cursor.close();
            if (i > 0)
                return true;
        }
        return false;
    }

    // 查询是否存在资源
    private boolean isExist(SQLiteDatabase db, String tableName, String colName, int id, String colName1, int id1) {
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select count(*) from " + tableName + " where " + colName + " = ? and " + colName1 + " = ?", new String[]{id + "",
                    id1 + ""});
            cursor.moveToFirst();
            int i = cursor.getInt(0);
            cursor.close();
            if (i > 0)
                return true;
        }
        return false;
    }


    private boolean isExist(SQLiteDatabase db, String tableName, String colName, String value) {
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select count(*) from " + tableName + " where " + colName + " = ?", new String[]{value});
            cursor.moveToFirst();
            int i = cursor.getInt(0);
            cursor.close();
            if (i > 0)
                return true;
        }
        return false;
    }


    public void saveCacheBaike(ArrayList<GetResourcesResult.BaikeItem> baikeItemList) {
        mDatabase = getReadableDatabase();
        mDatabase.delete("baike", null, null);
        for (GetResourcesResult.BaikeItem baike : baikeItemList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", baike.id);
            contentValues.put("entry", baike.entry + "");
            contentValues.put("pic", baike.pic);
            contentValues.put("summary", baike.summary);
            contentValues.put("created_time", baike.created_time);
            contentValues.put("catalog_id", baike.catalog_id);
            if (mDatabase.isOpen()) {
                if (isExist(mDatabase, "baike", "id", baike.id)) {
                    mDatabase.update("baike", contentValues, "id = ?", new String[]{baike.id + ""});
                } else {
                    contentValues.put("id", baike.id);
                    mDatabase.insert("baike", null, contentValues);
                }
            }
        }
        closeSafeDatabase();
    }

    public ArrayList<GetResourcesResult.BaikeItem> getCacheBaike(int count) {
        mDatabase = getReadableDatabase();
        ArrayList<GetResourcesResult.BaikeItem> result = new ArrayList<GetResourcesResult.BaikeItem>();
        if (mDatabase.isOpen()) {
            Cursor cursor = mDatabase.rawQuery("select * from baike limit " + count, null);
            while (cursor.moveToNext()) {
                result.add(createBaike(cursor));
            }
            cursor.close();
            mDatabase.close();
        }
        return result;
    }
}
