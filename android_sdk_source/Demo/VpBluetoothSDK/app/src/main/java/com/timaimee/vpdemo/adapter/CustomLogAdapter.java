package com.timaimee.vpdemo.adapter;

import android.util.Log;

import com.orhanobut.logger.LogAdapter;

/**
 * Created by timaimee on 2016/7/19.
 */
public class CustomLogAdapter implements LogAdapter {
    @Override public void d(String tag, String message) {
        Log.d(tag, message);
    }

    @Override public void e(String tag, String message) {
        Log.e(tag, message);
    }

    @Override public void w(String tag, String message) {
        Log.w(tag, message);
    }

    @Override public void i(String tag, String message) {
        Log.i(tag, message);
    }

    @Override public void v(String tag, String message) {
        Log.v(tag, message);
    }

    @Override public void wtf(String tag, String message) {
        Log.wtf(tag, message);
    }
}
