package com.byteidolon.mas.client;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Log {
    private static final String NEW_LINE = System.getProperty("line.separator") ;
    private final static File mLogFile;
    private final static SimpleDateFormat dateFormat;

    static {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        mLogFile = new File(ContextHelper.getContext()
                .getExternalFilesDir(null).getAbsolutePath(), "logs.txt" );
        if ( !mLogFile.exists() ) {
            try {
                mLogFile.createNewFile();
            } catch ( final Exception e ) {
                e.printStackTrace();
            }
            logDeviceInfo();
        }
        logVersionInfo();
    }

    private static synchronized void appendLog(String text, @Nullable Throwable tr) {
        try {
            final FileWriter fileOut = new FileWriter( mLogFile, true );
            fileOut.append( dateFormat.format(new Date()) + " - " + text + NEW_LINE );
            if (!Objects.isNull(tr)) {
                tr.printStackTrace(new PrintWriter(fileOut));
            }
            fileOut.close();
        } catch ( final Exception e ) {
            e.printStackTrace();
        }
    }

    private static void logDeviceInfo() {
        appendLog("Model : " + android.os.Build.MODEL, null);
        appendLog("Brand : " + android.os.Build.BRAND, null);
        appendLog("Product : " + android.os.Build.PRODUCT, null);
        appendLog("Device : " + android.os.Build.DEVICE, null);
        appendLog("Codename : " + android.os.Build.VERSION.CODENAME, null);
        appendLog("Release : " + android.os.Build.VERSION.RELEASE, null);
    }
    private static void logVersionInfo() {
        appendLog("Mode : " + BuildConfig.BUILD_TYPE, null);
        appendLog("Version : " + BuildConfig.VERSION_NAME, null);
    }

    public static int d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            appendLog("DEBUG " + tag + " : " + msg, null);
        }
        return android.util.Log.d(tag, msg);
    }
    public static int d(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            appendLog("DEBUG " + tag + " : " + msg, tr);
        }
        return android.util.Log.d(tag, msg, tr);
    }
    public static int i(String tag, String msg) {
        if (isLoggable(tag, android.util.Log.INFO)) {
            appendLog("INFO " + tag + " : " + msg, null);
        }
        return android.util.Log.i(tag, msg);
    }
    public static int i(String tag, String msg, Throwable tr) {
        if (isLoggable(tag, android.util.Log.INFO)) {
            appendLog("INFO " + tag + " : " + msg, tr);
        }
        return android.util.Log.i(tag, msg, tr);
    }
    public static int e(String tag, String msg) {
        if (isLoggable(tag, android.util.Log.ERROR)) {
            appendLog("ERROR " + tag + " : " + msg, null);
        }
        return android.util.Log.e(tag, msg);
    }
    public static int e(String tag, String msg, Throwable tr) {
        if (isLoggable(tag, android.util.Log.ERROR)) {
            appendLog("ERROR " + tag + " : " + msg, tr);
        }
        return android.util.Log.e(tag, msg, tr);
    }
    public static int w(String tag, String msg) {
        if (isLoggable(tag, android.util.Log.WARN)) {
            appendLog("WARN " + tag + " : " + msg, null);
        }
        return android.util.Log.w(tag, msg);
    }
    public static int w(String tag, Throwable tr) {
        if (isLoggable(tag, android.util.Log.WARN)) {
            appendLog("WARN " + tag + " : " + tr.getMessage(), tr);
        }
        return android.util.Log.w(tag, tr);
    }
    public static int w(String tag, String msg, Throwable tr) {
        if (isLoggable(tag, android.util.Log.WARN)) {
            appendLog("WARN " + tag + " : " + msg, tr);
        }
        return android.util.Log.w(tag, msg, tr);
    }
    public static int v(String tag, String msg) {
        if (isLoggable(tag, android.util.Log.VERBOSE)) {
            appendLog("VERBOSE " + tag + " : " + msg, null);
        }
        return android.util.Log.v(tag, msg);
    }
    public static int v(String tag, String msg, Throwable tr) {
        if (isLoggable(tag, android.util.Log.VERBOSE)) {
            appendLog("VERBOSE " + tag + " : " + msg, tr);
        }
        return android.util.Log.e(tag, msg, tr);
    }
    public static boolean isLoggable(String s, int i) {
        return android.util.Log.isLoggable(s, i);
    }
}
