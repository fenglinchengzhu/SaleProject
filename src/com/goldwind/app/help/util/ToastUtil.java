package com.goldwind.app.help.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    private static String oldMsg;
    private static Toast toast;

    public static void showToastShort(Context context, String s) {
        if (s != null) {
            showToast(context, s, Toast.LENGTH_SHORT);
        }
    }

    public static void showToastLong(Context context, String s) {
        if (s != null) {
            showToast(context, s, Toast.LENGTH_LONG);
        }
    }

    public static void showToastShort(Context context, int resId) {
        showToastShort(context, context.getString(resId));
    }

    public static void showToastLong(Context context, int resId) {
        showToastLong(context, context.getString(resId));
    }

    private static void showToast(Context context, String s, int duration) {
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), s, duration);
            toast.show();
        } else {
            if (s.equals(oldMsg)) {
                toast.show();
            } else {
                oldMsg = s;
                toast.setText(s);
                toast.show();
            }
        }
    }
}
