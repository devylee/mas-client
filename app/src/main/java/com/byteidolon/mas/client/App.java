package com.byteidolon.mas.client;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * APP单例
 */
public class App extends Application {
    private final String TAG = String.valueOf(App.class);
    private static App instance = null;
    private StorageManager storageManager = null;
    private Method getVolumePath = null;
    private OnVolumeScanListener volumeScanListener = null;

    public String getCurrentAssetsPath() {
        return currentAssetsPath;
    }

    public void setCurrentAssetsPath(@Nullable String assetsPath) {
        this.currentAssetsPath = assetsPath;
    }

    private String currentAssetsPath = null;

    public List<MasAssets> getMasAssets() {
        return masAssets;
    }

    private final List<MasAssets> masAssets = new ArrayList<>();

    private WeakReference<Activity> currentActivityWeakRef;
    private Object activityUpdateLock = new Object();
    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        synchronized (activityUpdateLock){
            if (currentActivityWeakRef != null) {
                currentActivity = currentActivityWeakRef.get();
            }
        }
        return currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
        synchronized (activityUpdateLock){
            currentActivityWeakRef = new WeakReference<>(activity);
        }
    }

    public static final String PATH_ASSETS = "/MasAssets";

    public static App getInstance() { return instance; }

    public void setVolumeScanListener(OnVolumeScanListener volumeScanListener) {
        this.volumeScanListener = volumeScanListener;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ContextHelper.setContext(getApplicationContext());

        storageManager = (StorageManager)getApplicationContext().getSystemService(Context.STORAGE_SERVICE);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                setCurrentActivity(activity);
                //Log.d(TAG, "Current Activity: " + activity.getComponentName().toString());
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });

        // 启动事件监听
//        BootBroadcastReceiver bootBroadcastReceiver = new BootBroadcastReceiver();
//        IntentFilter bootIntentFilter = new IntentFilter();
//        bootIntentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
//
//        registerReceiver(bootBroadcastReceiver, bootIntentFilter);

        // USB事件监听
        UsbBroadcastReceiver usbBroadcastReceiver = new UsbBroadcastReceiver();
        IntentFilter usbIntentFilter = new IntentFilter();
        usbIntentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        usbIntentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        usbIntentFilter.addDataScheme("file");

        registerReceiver(usbBroadcastReceiver, usbIntentFilter);

        setVolumeScanListener(() -> {
            if (masAssets.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.missing_mas_assets), Toast.LENGTH_LONG).show();
            } else {
                play();
            }
        });

        try {
            Class volumeClass = Class.forName(StorageVolume.class.getName());
            getVolumePath = volumeClass.getDeclaredMethod("getPath");
            getVolumePath.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        instance = this;

        Log.d(TAG, "App Created");
    }

    /**
     * 从所有卷中扫描MasAssets
     */
    public void scanMasAssets() {
        masAssets.clear();

        for (StorageVolume volume : storageManager.getStorageVolumes()) {
            scanMasAssets(volume);
        }

        if (null != volumeScanListener) {
            volumeScanListener.onScanCompleted();
        }
    }

    /**
     * 扫描指定卷
     * @param volume
     * @return
     */
    @Nullable public String scanMasAssets(StorageVolume volume) {
        String path = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                path = volume.getDirectory().getAbsolutePath();
            } else {
                path = getVolumePath.invoke(volume).toString();
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }

        if (null != path) {
            File assets = new File(path + PATH_ASSETS);
//            Log.d(TAG, volume.getDescription(getApplicationContext()));
//            Log.d(TAG, assets.getAbsolutePath());
//            Log.d(TAG, assets.canRead() + " " + volume.isPrimary() + " " + volume.isEmulated() + " " + volume.isRemovable());
            if (assets.exists() && assets.isDirectory() && assets.canRead()) {
                if (masAssets.stream().noneMatch(a -> a.getPath().equals(assets.getAbsolutePath()))) {
                    masAssets.add(new MasAssets()
                            .setDescription(volume.getDescription(getApplicationContext()))
                            .setEmulated(volume.isEmulated())
                            .setPrimary(volume.isPrimary())
                            .setRemovable(volume.isRemovable())
                            .setPath(assets.getAbsolutePath()));
                    return assets.getAbsolutePath();
                }
            } else {
                File finalAssets = new File(path + "/Android/data/" + getPackageName() + "/files" + PATH_ASSETS);
//                Log.d(TAG, finalAssets.getAbsolutePath());
//                Log.d(TAG, String.valueOf(finalAssets.canRead()));
                if (finalAssets.exists() && finalAssets.isDirectory() && finalAssets.canRead()) {
                    if (masAssets.stream().noneMatch(a -> a.getPath().equals(finalAssets.getAbsolutePath()))) {
                        masAssets.add(new MasAssets()
                                .setDescription(volume.getDescription(getApplicationContext()))
                                .setEmulated(volume.isEmulated())
                                .setPrimary(volume.isPrimary())
                                .setRemovable(volume.isRemovable())
                                .setPath(finalAssets.getAbsolutePath()));
                        return finalAssets.getAbsolutePath();
                    }
                }
            }
        }

        return null;
    }

    /**
     * 扫描MasAssets
     * @param volumePath
     * @return
     */
    @Nullable public String scanMasAssets(String volumePath) {
        StorageVolume volume = storageManager.getStorageVolume(new File(volumePath));
        return scanMasAssets(volume);
    }

    /**
     * 播放内容
     * @param path
     * @return
     */
    public void play(@NonNull String path) {
        if (path.equals(currentAssetsPath)) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.mas_assets_duplicated), Toast.LENGTH_LONG).show();
            return;
        } else if (!Objects.isNull(currentAssetsPath)) {
            stop();
        }

        Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("path", path);
        startActivity(intent);
    }

    /**
     * 播放内容
     */
    public void play() {
        if (null != currentAssetsPath) {
            play(currentAssetsPath);
        } else if (!masAssets.isEmpty()) {
            MasAssets top = masAssets.stream().noneMatch(a -> a.isRemovable())
                    ? masAssets.stream().filter(a -> a.isPrimary()).findFirst().orElse(null)
                    : masAssets.stream().filter(a -> a.isRemovable()).findFirst().orElse(null);
            if (Objects.isNull(top)) {
                top = masAssets.stream().findFirst().orElse(masAssets.get(0));
            }

            play(top.getPath());
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (getCurrentActivity().getComponentName().getClassName()
                .equals(PlayerActivity.class.getName())) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        setCurrentAssetsPath(null);
    }

    public interface OnVolumeScanListener {
        void onScanCompleted();
    }
}
