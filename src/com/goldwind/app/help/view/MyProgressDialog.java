package com.goldwind.app.help.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldwind.app.help.R;


public class MyProgressDialog extends Dialog {
    private static MyProgressDialog progressDialog;
    private View contentView;

    private MyProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    private static MyProgressDialog createDialog(Context context) {
        MyProgressDialog dialog = new MyProgressDialog(context,
                R.style.ProgressDialog);
        dialog.contentView = View.inflate(context, R.layout.layout_dialog_loading, null);
        dialog.setContentView(dialog.contentView);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        // 不可以用“返回键”取消
        // dialog.setCancelable(false);
        return dialog;
    }

    private static MyProgressDialog createDialog(Context context, boolean cancelable) {
        MyProgressDialog dialog = new MyProgressDialog(context,
                R.style.ProgressDialog);
        dialog.setContentView(R.layout.layout_dialog_loading);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        // 不可以用“返回键”取消
        dialog.setCancelable(cancelable);
        return dialog;
    }

    /**
     * 打开进度条
     */
    public static void showDialog(Context context) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        progressDialog = createDialog(context);
        progressDialog.show();
    }

    /**
     * 打开进度条
     */
    public static void showDialog(Context context, String text) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        progressDialog = createDialog(context);
        ((TextView) progressDialog.findViewById(R.id.tv_loading_dialog)).setText(text);
        progressDialog.show();
    }

    /**
     * 打开进度条
     */
    public static void showDialog(Context context, boolean cancelable) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        progressDialog = createDialog(context, cancelable);
        progressDialog.show();
    }

    /**
     * 打开进度条
     */
    public static void showDialog(Context context, String text, boolean cancelable) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        progressDialog = createDialog(context, cancelable);
        ((TextView) progressDialog.findViewById(R.id.tv_loading_dialog)).setText(text);
        progressDialog.show();
    }

    public static void closeDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        ImageView imageView = (ImageView) findViewById(R.id.iv_loading_dialog);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView
                .getBackground();
        animationDrawable.start();
    }
}
