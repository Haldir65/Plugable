package com.me.harris.bookstore;

import android.app.Application;
import android.content.Context;

/**
 * Created by Harris on 2018/4/8.
 */

public class App extends Application {

    private static Context app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = getApplicationContext();
    }

    public static Context getApp() {
        return app;
    }
}
