package com.byteidolon.mas.client;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;

/*
 * Main Activity
 */
public class MainActivity extends AppCompatActivity {
    private final String TAG = String.valueOf(MainActivity.class);
    private final int REQUEST_STORAGE_PERMISSIONS = 79000001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "MainActivity Created");

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (Objects.isNull(fragment)) {
            fragment = MainFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

        checkPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity Resume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "MainActivity Stop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "MainActivity Pause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MainActivity Destroy");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSIONS) {
            App.getInstance().scanMasAssets();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int REQUEST_OVERLAY_PERMISSIONS = 120;
        if (requestCode == REQUEST_OVERLAY_PERMISSIONS) {
            checkPermissions();
        }
    }

    /**
     * 所需权限
     */
    private void checkPermissions() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED
            }, REQUEST_STORAGE_PERMISSIONS);
        } else {
            App.getInstance().scanMasAssets();
        }
    }
}