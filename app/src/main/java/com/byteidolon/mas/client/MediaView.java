package com.byteidolon.mas.client;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * MediaView
 */
public class MediaView extends RelativeLayout {
    private final String TAG = String.valueOf(MediaView.class);
    private ViewPager pager;
    private MediaAdapter adapter;

    public MediaView(Context context) {
        super(context);
        initView();
    }

    public MediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MediaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        //Log.d(TAG, "MediaView init");
        pager = new ViewPager(getContext());
        adapter = new MediaAdapter(getContext(), pager);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(1);
        addView(pager, new LayoutParams(-1, -1));
    }

    /**
     * 文件列表
     * @param files
     */
    public void setMedias(List<File> files) {
        //Log.d(TAG, "set medias");
        Collections.sort(files, Comparator.comparing(File::getName)); // 按文件名做了个排序
        adapter.setMedias(files);
    }

    @Override
    protected void onAttachedToWindow() {
        Log.d(TAG, "mediaview attached");
        super.onAttachedToWindow();
        adapter.play(); // 自动播放
    }
}
