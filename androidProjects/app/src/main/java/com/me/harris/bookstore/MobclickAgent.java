package com.me.harris.bookstore;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Harris on 2018/4/8.
 */

public class MobclickAgent {

    public static final String TAG = MobclickAgent.class.getSimpleName();

    public static void onEvent(Context app, String tag) {
        Log.e(TAG, tag);
        if (app != null&&!TextUtils.isEmpty(tag)) {
            Toast.makeText(app, tag, Toast.LENGTH_SHORT).show();
        }
    }
}
