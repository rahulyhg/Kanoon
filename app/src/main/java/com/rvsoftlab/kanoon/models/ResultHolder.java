package com.rvsoftlab.kanoon.models;

import android.support.annotation.Nullable;

import com.otaliastudios.cameraview.Size;

import java.io.File;

public class ResultHolder {
    private static byte[] image;
    private static File video;
    private static com.otaliastudios.cameraview.Size nativeCaptureSize;
    private static long timeToCallback;

    public static void setImage(@Nullable byte[] image) {
        ResultHolder.image = image;
    }

    @Nullable
    public static byte[] getImage() {
        return image;
    }

    public static void setVideo(@Nullable File video) {
        ResultHolder.video = video;
    }

    @Nullable
    public static File getVideo() {
        return video;
    }

    public static Size getNativeCaptureSize() {
        return nativeCaptureSize;
    }

    public static void setNativeCaptureSize(Size nativeCaptureSize) {
        ResultHolder.nativeCaptureSize = nativeCaptureSize;
    }

    public static void setTimeToCallback(long timeToCallback) {
        ResultHolder.timeToCallback = timeToCallback;
    }

    public static long getTimeToCallback() {
        return timeToCallback;
    }

    public static void dispose() {
        setImage(null);
        setNativeCaptureSize(null);
        setTimeToCallback(0);
    }
}
