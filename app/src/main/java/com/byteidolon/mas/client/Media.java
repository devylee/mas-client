package com.byteidolon.mas.client;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.gifdecoder.GifHeaderParser;
import com.bumptech.glide.gifdecoder.StandardGifDecoder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifBitmapProvider;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

/**
 * 多媒体文件
 */
public class Media extends RelativeLayout {
    private final String TAG = String.valueOf(Media.class);

    private File file = null;
    private boolean isImage = false;
    private boolean isGif = false;
    private GifDrawable gif = null;
    private boolean isVideo = false;
    private boolean isPlaying = false;
    private ImageView image = null;
    private VideoView video = null;
    private MediaPlayListener listener = null;
    private final int MESSAGE_PLAY_DONE = 7001;
    private int duration = 3500;

    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            handler.removeMessages(MESSAGE_PLAY_DONE);
            onPlayDone();
        }
    };

    public Media(Context context) {
        super(context);
    }

    public Media(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Media(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMedia(File file) {
        this.file = file;
        String fileType = "";
        try {
            fileType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, fileType);
        if (fileType.startsWith("image/")) {
            isImage = true;
            if (fileType.equals("image/gif")) {
                isGif = true;
            }
            image = new ImageView(getContext());
            image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            image.setBackgroundColor(Color.WHITE);
            if (isGif) {
                Glide.with(getContext()).load(file.getAbsolutePath())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.e(TAG, "image load failed");
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                gif = (GifDrawable) resource;
                                gif.stop();
                                gif.setLoopCount(1);
                                ByteBuffer buffer = gif.getBuffer();
                                GifBitmapProvider provider = new GifBitmapProvider(Glide.get(getContext()).getBitmapPool());
                                GifDecoder decoder = new StandardGifDecoder(provider);
                                decoder.setData(new GifHeaderParser().setData(buffer).parseHeader(), buffer);
                                int _duration = 1000;
                                for (int i = 0; i < gif.getFrameCount(); i ++) {
                                    _duration += decoder.getDelay(i);
                                }
                                if (duration < _duration) {
                                    duration = _duration;
                                }
                                return false;
                            }
                        }).into(image);
            } else {
                Glide.with(getContext()).load(file.getAbsolutePath())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(image);
            }
            addView(image, new RelativeLayout.LayoutParams(-1, -1));
        } else if (fileType.startsWith("video/")) {
            isVideo = true;
        }
        //Log.d(TAG, file.getAbsolutePath());
    }

    /**
     * 播放
     */
    public void play() {
        if (!isPlaying()) {
            if (isVideo) {
                video = new VideoView(getContext());
                //video.setVideoPath(file.getAbsolutePath()); // MediaPlayer java.io.FileNotFoundException: No content provider
                video.setVideoURI(Uri.fromFile(file));
                video.setOnCompletionListener(mp -> {
                    Log.d(TAG, "media completed");
                    onPlayDone();
                });
                video.setOnErrorListener((mp, what, extra) -> {
                    Log.d(TAG,"media error " + what + " " + extra);
                    mp.stop();
                    return false;
                });
                video.setOnPreparedListener(mp -> {
                    Log.d(TAG,"media prepared");
                    mp.setLooping(false);
                    mp.start();
                });
                addView(video, new RelativeLayout.LayoutParams(-1, -1));
            } else if (isImage) {
                if (isGif && (null != gif)) {
                    try {
                        gif.startFromFirstFrame();
                    } catch (Exception ex) {
                        gif.start();
                    }

                }
                waiting();
            }
        }
    }

    /**
     * 停止
     */
    public void stop() {
        isPlaying = false;
        handler.removeMessages(MESSAGE_PLAY_DONE);
        if (isVideo && (null != video)) {
            video.stopPlayback();
            video.setOnCompletionListener(null);
            video.setOnPreparedListener(null);
            video.setOnErrorListener(null);
            video.suspend();
            video = null;
        } else if (isGif && (null != gif)) {
            gif.stop();
        }
    }

    /**
     * 播放状态
     * @return
     */
    public boolean isPlaying() {
        return (isVideo && (null != video)) ? video.isPlaying() : isPlaying;
    }

    private void waiting() {
        if (!handler.hasMessages(MESSAGE_PLAY_DONE)) {
            isPlaying = true;
            handler.sendEmptyMessageDelayed(MESSAGE_PLAY_DONE, duration);
        }
    }

    private void onPlayDone() {
        isPlaying = false;
        if (null != listener) {
            listener.onPlayDone(this);
        }
    }

//    @Override
//    protected void onDetachedFromWindow() {
//        Log.d(TAG, "detached");
//        super.onDetachedFromWindow();
//        stop();
//    }
//
//    @Override
//    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
//        Log.d(TAG, "visibility " + visibility);
//        super.onVisibilityChanged(changedView, visibility);
//        stop();
//    }

    /**
     * 媒体类型
     * @return
     */
    public Type getType() {
        return isVideo ? Type.Video : isImage ? Type.Image : Type.Unknown;
    }

    /**
     * 注册事件监听器
     * @param listener
     */
    public void addMediaPlayListener(MediaPlayListener listener) {
        this.listener = listener;
    }

    /**
     * 多媒体文件类型
     */
    public enum Type {
        Image,
        Video,
        Unknown
    }

    /**
     * 媒体播放监听器
     */
    public interface MediaPlayListener {
        void onPlayDone(Media media);
    }
}
