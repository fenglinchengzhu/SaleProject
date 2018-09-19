package com.goldwind.app.help.util;

import android.annotation.SuppressLint;
import android.util.Log;

import com.goldwind.app.help.Constant;

import java.io.FileWriter;

/**
 * Log工具类
 */
public class LogUtil {

    private final static String TAG = Constant.LOG_TAG;

    private LogUtil() {
    }

    private static int getLevel() {
        if (Constant.IS_TEST) {
            return Log.VERBOSE;
        } else {
            return Log.ASSERT;
        }
    }

    /**
     * Get The Current Function Name
     */
    private static String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(LogUtil.class.getName())) {
                continue;
            }
            return "[" + Thread.currentThread().getName() + ": " + st.getFileName() + ":" + st.getLineNumber() + " " + st.getMethodName() + "]";
        }
        return null;
    }

    /**
     * The Log Level:i
     *
     * @param str
     */
    public static void i(Object str) {
        if (getLevel() <= Log.INFO) {
            Log.i(TAG, str.toString());
        }
    }

    /**
     * The Log Level:d
     *
     * @param str
     */
    public static void d(Object str) {
        if (getLevel() <= Log.DEBUG) {
            Log.d(TAG, str.toString());
        }
    }

    /**
     * The Log Level:V
     *
     * @param str
     */
    public static void v(Object str) {
        if (getLevel() <= Log.VERBOSE) {
            Log.v(TAG, str.toString());
        }
    }

    /**
     * The Log Level:w
     *
     * @param str
     */
    public static void w(Object str) {
        if (getLevel() <= Log.WARN) {
            Log.w(TAG, str.toString());
        }
    }

    /**
     * The Log Level:e
     *
     * @param str
     */
    public static void e(Object str) {
        if (getLevel() <= Log.ERROR) {
            Log.e(TAG, str.toString());
        }
    }

    /**
     * The Log Level:i
     *
     * @param str
     */
    public static void ii(Object str) {
        if (getLevel() <= Log.INFO) {
            String name = getFunctionName();
            if (name != null) {
                Log.i(TAG, name + " - " + str);
            } else {
                Log.i(TAG, str.toString());
            }
        }
    }

    /**
     * The Log Level:d
     *
     * @param str
     */
    public static void dd(Object str) {
        if (getLevel() <= Log.DEBUG) {
            String name = getFunctionName();
            if (name != null) {
                Log.d(TAG, name + " - " + str);
            } else {
                Log.d(TAG, str.toString());
            }
        }
    }

    /**
     * The Log Level:V
     *
     * @param str
     */
    public static void vv(Object str) {
        if (getLevel() <= Log.VERBOSE) {
            String name = getFunctionName();
            if (name != null) {
                Log.v(TAG, name + " - " + str);
            } else {
                Log.v(TAG, str.toString());
            }
        }
    }

    /**
     * The Log Level:w
     *
     * @param str
     */
    public static void ww(Object str) {
        if (getLevel() <= Log.WARN) {
            String name = getFunctionName();
            if (name != null) {
                Log.w(TAG, name + " - " + str);
            } else {
                Log.w(TAG, str.toString());
            }
        }
    }

    /**
     * The Log Level:e
     *
     * @param str
     */
    public static void ee(Object str) {
        if (getLevel() <= Log.ERROR) {
            String name = getFunctionName();
            if (name != null) {
                Log.e(TAG, name + " - " + str);
            } else {
                Log.e(TAG, str.toString());
            }
        }
    }

    /**
     * The Log Level:e
     */
    public static void ex(Throwable tr) {
        if (getLevel() <= Log.ERROR) {
            Log.e(TAG, tr.getMessage(), tr);
        }
    }

    /**
     * The Log Level:e
     *
     * @param msg
     * @param tr
     */
    public static void ex(String msg, Throwable tr) {
        if (getLevel() <= Log.ERROR) {
            Log.e(TAG, msg, tr);
        }
    }

    /**
     * 输出到Log
     *
     * @param str
     */
    public static void toLog(String tag, Object str) {
        if (getLevel() == Log.VERBOSE) {
            Log.i(tag, str.toString());
        }
    }

    public static void toConsole(String str) {
        if (getLevel() == Log.VERBOSE) {
            System.out.println(str);
        }
    }

    /**
     * 输出到SdCard
     *
     * @param str
     */
    @SuppressLint("SdCardPath")
    public static void toSdCard(String str) {
        if (getLevel() == Log.VERBOSE) {
            if (str != null) {
                try {
                    String path = "/sdcard/" + TAG + ".txt";
                    str = str.replaceAll(System.getProperty("line.separator"), "\r\n");
                    FileWriter fw = new FileWriter(path, false);
                    fw.write(str);
                    fw.flush();
                    fw.close();
                } catch (Exception e) {
                    ex(e);
                }
            }
        }
    }
}
