package com.bttendance.view;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bttendance.R;
import com.bttendance.helper.ViewHelper;

/**
 * Bttendance Dialog
 *
 * @author The Finest Artist
 */
public class BTDialog {

    public interface OnDialogListener {
        void onConfirmed(String edit);

        void onCanceled();
    }

    public static void hide(MaterialDialog dialog) {
        if (dialog != null)
            dialog.hide();
    }

    public static MaterialDialog alert(Context context, String title, String message, final OnDialogListener listener) {
        if (context == null)
            return null;

        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .titleColorRes(R.color.bttendance_black)
                .contentColorRes(R.color.bttendance_silver)
                .positiveColorRes(R.color.bttendance_navy)
                .negativeColorRes(R.color.bttendance_silver)
                .title(title)
                .content(message)
                .positiveText(R.string.confrim)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.Callback() {
                    @Override
                    public void onNegative(MaterialDialog materialDialog) {
                        if (listener != null)
                            listener.onCanceled();
                    }

                    @Override
                    public void onPositive(MaterialDialog materialDialog) {
                        if (listener != null)
                            listener.onConfirmed(null);
                    }
                }).dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (listener != null)
                            listener.onCanceled();
                    }
                }).build();

        themeDialog(context, dialog);
        dialog.show();
        return dialog;
    }

    public static MaterialDialog ok(Context context, String title, String message, final OnDialogListener listener) {
        if (context == null)
            return null;


        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .titleColorRes(R.color.bttendance_black)
                .contentColorRes(R.color.bttendance_silver)
                .positiveColorRes(R.color.bttendance_navy)
                .title(title)
                .content(message)
                .positiveText(R.string.ok)
                .callback(new MaterialDialog.SimpleCallback() {
                    @Override
                    public void onPositive(MaterialDialog materialDialog) {
                        if (listener != null)
                            listener.onCanceled();
                    }
                }).dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (listener != null)
                            listener.onCanceled();
                    }
                }).build();

        themeDialog(context, dialog);
        dialog.show();
        return dialog;
    }

    public static MaterialDialog edit(Context context, String title, String message, final OnDialogListener listener) {
        if (context == null)
            return null;

        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .titleColorRes(R.color.bttendance_black)
                .contentColorRes(R.color.bttendance_silver)
                .positiveColorRes(R.color.bttendance_navy)
                .negativeColorRes(R.color.bttendance_silver)
                .title(title)
                .content(message)
                .positiveText(R.string.confrim)
                .negativeText(R.string.cancel)
                .build();

        themeDialog(context, dialog);
        dialog.show();
        return dialog;
    }

    public static MaterialDialog context(Context context, String title, String[] options, final OnDialogListener listener) {
        if (context == null)
            return null;

        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .titleColorRes(R.color.bttendance_black)
                .contentColorRes(R.color.bttendance_silver)
                .title(title)
                .items(options)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (listener != null)
                            listener.onConfirmed(text.toString());
                    }
                }).dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (listener != null)
                            listener.onCanceled();
                    }
                })
                .build();

        themeDialog(context, dialog);
        dialog.show();
        return dialog;
    }

    public static MaterialDialog progress(Context context, String message) {
        if (context == null)
            return null;

        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .titleColorRes(R.color.bttendance_silver)
                .title(message)
                .customView(R.layout.dialog_progress)
                .build();

        themeDialog(context, dialog);
        dialog.show();
        return dialog;
    }

    private static void themeDialog(Context context, MaterialDialog dialog) {
        if (dialog == null)
            return;

        if (dialog.getCustomView() == null) {
            if (dialog.getTitleFrame() != null
                    && dialog.getTitleFrame().getParent() != null
                    && dialog.getTitleFrame().getParent().getParent() != null
                    && dialog.getTitleFrame().getParent().getParent() instanceof LinearLayout) {
                LinearLayout topLayout = (LinearLayout) dialog.getTitleFrame().getParent().getParent();
                topLayout.setBackgroundResource(R.drawable.bt_white_bg);

                TextView buttonDefaultNegative = (TextView) topLayout.findViewById(R.id.buttonDefaultNegative);
                TextView buttonDefaultPositive = (TextView) topLayout.findViewById(R.id.buttonDefaultPositive);
                if (buttonDefaultPositive != null)
                    ViewHelper.setBackground(context, buttonDefaultPositive, R.drawable.dialog_btn_selector);
                if (buttonDefaultNegative != null)
                    ViewHelper.setBackground(context, buttonDefaultNegative, R.drawable.dialog_btn_selector);
            }
        } else {
            if (dialog.getTitleFrame() != null
                    && dialog.getTitleFrame().getParent() != null
                    && dialog.getTitleFrame().getParent().getParent() != null
                    && dialog.getTitleFrame().getParent().getParent().getParent() != null
                    && dialog.getTitleFrame().getParent().getParent().getParent().getParent() != null
                    && dialog.getTitleFrame().getParent().getParent().getParent().getParent() instanceof LinearLayout) {
                LinearLayout topLayout = (LinearLayout) dialog.getTitleFrame().getParent().getParent().getParent().getParent();
                topLayout.setBackgroundResource(R.drawable.bt_white_bg);

                TextView buttonDefaultNegative = (TextView) topLayout.findViewById(R.id.buttonDefaultNegative);
                TextView buttonDefaultPositive = (TextView) topLayout.findViewById(R.id.buttonDefaultPositive);
                if (buttonDefaultPositive != null)
                    ViewHelper.setBackground(context, buttonDefaultPositive, R.drawable.dialog_btn_selector);
                if (buttonDefaultNegative != null)
                    ViewHelper.setBackground(context, buttonDefaultNegative, R.drawable.dialog_btn_selector);
            }
        }
    }

}//end of class
