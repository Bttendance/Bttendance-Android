
package com.utopia.bttendance.helper;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

// It pops up keyboard to input texts and drops a cursor to a view(final View view, such as Edittext)
public class KeyboardHelper {

    public static void show(final Context context, final View view) {
        view.requestFocus();
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager) context
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(view, 0);
            }
        }, 200);
    }

    public static void showByUser(Context context, View view) {
        InputMethodManager keyboard = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void hide(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideByUser(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}
