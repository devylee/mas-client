package com.byteidolon.mas.client;

import android.app.Activity;
import android.content.Context;

import java.lang.ref.WeakReference;

public class ContextHelper {
    private static WeakReference<Context> contextWeakRef;
    private static final Object lock = new Object();

    public static Context getContext() {
        Context context = null;
        synchronized (lock){
            if (contextWeakRef != null) {
                context = contextWeakRef.get();
            }
        }
        return context;
    }

    public static void setContext(Context context) {
        synchronized (lock){
            contextWeakRef = new WeakReference<>(context);
        }
    }
}
