package com.goldwind.app.help;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.goldwind.app.help.model.UserInfoResult;
import com.goldwind.app.help.util.CommonUtil;
import com.goldwind.app.help.util.JsonUtil;

/**
 * User:YaoXin
 * Date:2015/7/17
 */
public class Constant {
    // true 测试开发环境  false 线上环境
    public static final boolean IS_TEST = true;
    // Log Tag
    public static final String LOG_TAG = "jfkt";
    public static final String BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/jinfeng";
    public static final String BASE_FILE_PATH = BASE_PATH + "/file/";
    public static final String BASE_CACHE_PATH = BASE_PATH + "/cache/";
    //    public static final String BASE_URL = "http://ec2-54-223-73-201.cn-north-1.compute.amazonaws.com.cn:9090/jinfengread";
//    public static final String BASE_URL = "http://115.28.204.43:9000/jinfengread";
    public static final String BASE_URL = "http://ec2-54-223-101-219.cn-north-1.compute.amazonaws.com.cn:9090/jinfengread";
    public static boolean onlyWifi = true;
    public static boolean onlyVideo = true;
    public static int newUpdate = 0;
    public static String BASE_DECODE_PATH;
    private static UserInfoResult.Data USER;

    // 保存用户的登录信息
    public static void saveUserInfo(Context context, UserInfoResult.Data user) {
        USER = user;
        String jsonData = JsonUtil.toJson(user);
        CommonUtil.spPutString(context.getApplicationContext(), "CurrentSaveUser", jsonData);
    }

    public static UserInfoResult.Data getCurrentUser(Context context) {
        if (USER != null) {
            return USER;
        }

        String jsonData = CommonUtil.spGetString(context.getApplicationContext(), "CurrentSaveUser");
        if (!TextUtils.isEmpty(jsonData)) {
            UserInfoResult.Data userInfoResult = JsonUtil.fromJson(jsonData, UserInfoResult.Data.class);
            USER = userInfoResult;
            return USER;
        } else {
            return null;
        }
    }

    public static int getPageSize() {
        return 20;
    }

    public static final class Api {
        public static final String API_BASE_TEST = "http://goldwind.yuefengd.com/";
        public static final String API_BASE = "http://54.223.50.249/";

        public static final String API_BASE_APK = "http://goldwind.yuefengd.com/gw/apk/";
        public static final String FEED_BACK = BASE_URL + "/staffInfo/feedBack";
        public static final String GET_RESOURCES = BASE_URL + "/staffInfo/getResources";
        public static final String USER_INFO = BASE_URL + "/staffInfo/userInfo";
        public static final String SET_APPLY_PERMISSION = BASE_URL + "/staffInfo/setApplyPermission";
        public static final String GET_APPLY_PERMISSION = BASE_URL + "/staffInfo/getApplyPermission";
        public static final String SEARCH_RESOURCES = BASE_URL + "/staffInfo/searchResources";
        //public static final String GET_APK_VERSION = BASE_URL + "/staffInfo/getApkVersion";
        public static final String GET_APK_VERSION = BASE_URL + "/staffInfo/getApkVersion";
        public static final String GET_CATEGORY_RESOURCES = BASE_URL + "/staffInfo/getCategoryResources";
        public static final String GET_DOC_RECOMMEND = BASE_URL + "/staffInfo/getRecommend";


        public static final String GET_RECOMMEND = (IS_TEST ? API_BASE_TEST : API_BASE) + "gw/app/baike/visited";//http://goldwind.yuefengd.com/gw/mng/baike/query?catalog_id=12";

        public static final String GET_TOP_IMAGE = (IS_TEST ? API_BASE_TEST : API_BASE) + "gw/app/pic/lists";
        public static final String GET_SEARCH = (IS_TEST ? API_BASE_TEST : API_BASE) + "gw/app/baike/query?entry=";
        //category
        public static final String GET_BAIKE_DETAIL = (IS_TEST ? API_BASE_TEST : API_BASE) + "gw/app/baike/baikeViews?baike_id=";
        public static final String GET_BAIKE_CATEGORY = (IS_TEST ? API_BASE_TEST : API_BASE) + "gw/app/catalog/lists?id=";
        public static final String GET_CATE_CONTENT = (IS_TEST ? API_BASE_TEST : API_BASE) + "gw/app/baike/query?catalog_id=";

        public static final String GET_NEWS_LIST = (IS_TEST ? API_BASE_TEST : API_BASE) + "gw/app/news/lists?";
        public static final String GEY_NEWS_DETAIL = (IS_TEST ? API_BASE_TEST : API_BASE) + "gw/app/news/newsViews?news_id=";
        public static final String GEY_UPDATE_URL = (IS_TEST ? API_BASE_TEST : API_BASE) + "gw/app/version/lastest?client=android";

        public static final String GOLD_DOMAIN = (IS_TEST ? API_BASE_TEST : API_BASE);

        //计算器类API
        public static final String GET_LOCATION_URL = (IS_TEST ? API_BASE_TEST : API_BASE) + "gw/app/location/lists?area=area";
        public static final String GET_LOCATION_DETAIL_URL = (IS_TEST ? API_BASE_TEST : API_BASE) + "gw/app/location/lists?area=";
        public static final String GET_JIZU_TYPE_URL = (IS_TEST ? API_BASE_TEST : API_BASE) + "gw/app/location/lists?area=jitype";
        public static final String GET_FENG_TYPE_URL = (IS_TEST ? API_BASE_TEST : API_BASE) + "gw/app/location/lists?area=fengtype";
        public static final String GET_CAL_OUT_PARAMS_URL = (IS_TEST ? API_BASE_TEST : API_BASE) + "gw/app/calculate/test";


    }

    public static final class Key {
        public static final String FIRST_START = "FIRST_START";
        public static final String CURRENT_USER = "CURRENT_USER";
        public static final String ONLY_WIFI = "ONLY_WIFI"; // false;true 仅用WIFI
        public static final String ONLY_VIDEO = "ONLY_VIDEO"; // false;true 仅手动下载视频
    }

    public static final class EXTRA {
        public static final String JIZU_TYPE = "JIZU_TYPE";
        public static final String SEARCH_NAME = "SEARCH_NAME";
        public static final String CALCULATOR_NAME = "CALCULATOR_NAME";
        public static final String CALCULATOR_20 = "CALCULATOR_20";
        public static final String CALCULATOR_25 = "CALCULATOR_25";
        public static final String CALCULATOR_15 = "CALCULATOR_15";
        public static final String CALCULATOR_30 = "CALCULATOR_30";
        public static final String CALCULATOR_TMP = "CALCULATOR_TMP";


    }

    public static final class TAB {
        public static final int CAL_ALL = 0x8001;
        public static final int CAL_PART = CAL_ALL + 1;
        public static final int EMPTY = CAL_PART + 1;
    }
}
