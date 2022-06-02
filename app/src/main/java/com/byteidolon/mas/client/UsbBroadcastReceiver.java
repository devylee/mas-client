package com.byteidolon.mas.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * USB事件监听
 */
public class UsbBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = String.valueOf(UsbBroadcastReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Action Received: " + intent.getAction());
        String path;
        switch (intent.getAction()) {
            case Intent.ACTION_MEDIA_MOUNTED: // 插入U盘
                Toast.makeText(context, intent.getData().getPath() + " mounted", Toast.LENGTH_LONG).show();
                path = App.getInstance().scanMasAssets(intent.getData().getPath());
                if (null != path) {
                    App.getInstance().play(path);
                }
                break;
            case Intent.ACTION_MEDIA_UNMOUNTED: // 拔出U盘
                path = App.getInstance().getCurrentAssetsPath();
                if ((path != null) && path.startsWith(intent.getData().getPath())) {
                    App.getInstance().stop();
                    App.getInstance().scanMasAssets();
                }
                Toast.makeText(context, intent.getData().getPath() + " unmounted", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
