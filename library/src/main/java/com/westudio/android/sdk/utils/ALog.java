package com.westudio.android.sdk.utils;

import android.util.Log;

public class ALog {

    private static String debugTag = "MyApplication";
    private static DebugLevel debugLevel = DebugLevel.VERBOSE;

    private ALog() {
    }

    public static String getDebugTag() {
        return debugTag;
    }

    public static void setDebugTag(final String debugTag) {
        ALog.debugTag = debugTag;
    }

    public static DebugLevel getDebugLevel() {
        return debugLevel;
    }

    public static void setDebugLevel(final DebugLevel debugLevel) {
        if (debugLevel == null) {
            throw new IllegalArgumentException("debug-level can't be null");
        }

        ALog.debugLevel = debugLevel;
    }

    public static void v(final String message) {
        ALog.v(debugTag, message);
    }

    public static void v(final String tag, final String message) {
        if (!isDebuggable(DebugLevel.VERBOSE)) {
            return;
        }
        Log.v(tag, message);
    }

    public static void d(final String message) {
        ALog.d(debugTag, message);
    }

    public static void d(final String message, final Throwable t) {
        ALog.d(debugTag, message, t);
    }

    public static void d(final String tag, final String message) {
        ALog.d(tag, message, null);
    }

    public static void d(final String tag, final String message, final Throwable t) {
        if (!isDebuggable(DebugLevel.DEBUG)) {
            return;
        }

        if (t == null) {
            Log.d(tag, message);
        } else {
            Log.d(tag, message, t);
        }
    }

    public static void i(final String message) {
        ALog.i(debugTag, message);
    }

    public static void i(final String message, final Throwable t) {
        ALog.i(debugTag, message, t);
    }

    public static void i(final String tag, final String message) {
        ALog.i(tag, message, null);
    }

    public static void i(final String tag, final String message, final Throwable t) {
        if (!isDebuggable(DebugLevel.INFO)) {
            return;
        }

        if (t == null) {
            Log.i(tag, message);
        } else {
            Log.i(tag, message, t);
        }
    }

    public static void w(final String message) {
        ALog.w(debugTag, message);
    }

    public static void w(final Throwable t) {
        ALog.w(debugTag, "", t);
    }

    public static void w(final String tag, final String message) {
        ALog.w(tag, message, null);
    }

    public static void w(final String tag, final String message, final Throwable t) {
        if (!isDebuggable(DebugLevel.WARNING)) {
            return;
        }

        if (t == null) {
            Log.w(tag, message);
        } else {
            Log.w(tag, message, t);
        }
    }

    public static void e(final String message) {
        ALog.e(debugTag, message);
    }

    public static void e(final Throwable t) {
        ALog.e(debugTag, "", t);
    }

    public static void e(final String tag, final String message) {
        ALog.e(tag, message, null);
    }

    public static void e(final String tag, final String message, final Throwable t) {
        if (!isDebuggable(DebugLevel.ERROR)) {
            return;
        }

        if (t == null) {
            Log.e(tag, message);
        } else {
            Log.e(tag, message, t);
        }
    }

    public static boolean isDebuggable(DebugLevel level) {
        return debugLevel.isDebuggable(level);
    }

    public static enum DebugLevel implements Comparable<DebugLevel> {
        NONE, ERROR, WARNING, INFO, DEBUG, VERBOSE;

        public static DebugLevel ALL = DebugLevel.VERBOSE;

        public boolean isDebuggable(final DebugLevel debugLevel) {
            return this.compareTo(debugLevel) >= 0;
        }
    }
}
