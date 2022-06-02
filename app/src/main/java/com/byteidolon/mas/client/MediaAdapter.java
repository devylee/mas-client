package com.byteidolon.mas.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MediaAdapter extends PagerAdapter {
    private final String TAG = String.valueOf(MediaAdapter.class);

    private final List<Media> items = new ArrayList<>();
    private final Context context;
    private final ViewPager pager;
    private int position = 0;

    private int getPosition() {
        return position;
    }
    private void setPosition(int position) {
        this.position = position;
    }

    @SuppressLint("ClickableViewAccessibility")
    public MediaAdapter(Context context, ViewPager pager) {
        this.context = context;
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.d(TAG, "page scrolled " + position);
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "page selected " + position);
                Media previous = items.get(getPosition() % items.size());
                if (null != previous) {
                    previous.stop();
                }
                setPosition(position);
                Media media = items.get(position % items.size());
                if (null != media) {
                    media.play();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                Log.d(TAG, "page state " + state);
//                switch (state) {
//                    case ViewPager.SCROLL_STATE_IDLE:
//                    case ViewPager.SCROLL_STATE_SETTLING:
//                        play();
//                        break;
//                    case ViewPager.SCROLL_STATE_DRAGGING:
//                        break;
//                }
            }
        });
        pager.setOnTouchListener((v, event) -> {
            Log.d(TAG, "touch " + event.getAction());
            Media media = getCurrentMedia();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    media.stop();
                    break;
                case MotionEvent.ACTION_UP:
                    media.play();
                    break;
            }
            return false;
        });
        this.pager = pager;
    }

    /**
     * 设置播放文件列表
     * @param files
     */
    public void setMedias(List<File> files) {
        items.clear();
        for (File file : Objects.requireNonNull(files)) {
            //Log.d(TAG, file.getAbsolutePath());
            Media media = new Media(context);
            media.setMedia(file);
            if (media.getType().equals(Media.Type.Image)
                    || media.getType().equals(Media.Type.Video)) {
                media.addMediaPlayListener(m -> next());
                items.add(media);
            }
        }
    }

    /**
     * 播放
     */
    public void play() {
        Media media = getCurrentMedia();
        if (media != null) {
            media.play();
        }
    }

    /**
     * 当前播放的文件
     * @return
     */
    private Media getCurrentMedia() {
        return items.get(pager.getCurrentItem() % items.size());
    }

    /**
     * 下一个
     */
    private void next() {
        if (items.size() > 1) {
            pager.setCurrentItem((pager.getCurrentItem() < Integer.MAX_VALUE) ? (pager.getCurrentItem() + 1) : 0);
        }
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(items.get(position % items.size()));
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = items.size() > 0 ? items.get(position % items.size()) : null;
//        ViewGroup parent = (ViewGroup) view.getParent();
//        if (null != parent) {
//            parent.removeView(view);
//        }
        if (null != view) {
            container.addView(view);
        }
        return view;
    }
}
