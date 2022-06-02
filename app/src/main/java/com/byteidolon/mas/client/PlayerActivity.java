package com.byteidolon.mas.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Player Activity
 */
public class PlayerActivity extends AppCompatActivity {
    private final String TAG = String.valueOf(PlayerActivity.class);

    private List<File> files = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        String path = getIntent().getStringExtra("path");
        Log.i(TAG, "Play: " + path);

        if (path != null) {
            // assets
            File assets = new File(path);
            assets.setReadable(true);

            try {
                for (File file : Objects.requireNonNull(assets.listFiles())) {
                    if (!file.isDirectory() && !file.getName().startsWith("._")) {
                        files.add(file);
                    }
                }
            } catch (NullPointerException e) {
                Log.e(TAG, "assets null", e);
            }

            if (files.size() > 0) {
                App.getInstance().setCurrentAssetsPath(path);
                // MediaView
                MediaView mediaView = findViewById(R.id.media_rotator);
                mediaView.setMedias(files);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.mas_assets_empty), Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "Stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroy");
        App.getInstance().stop();
    }
}