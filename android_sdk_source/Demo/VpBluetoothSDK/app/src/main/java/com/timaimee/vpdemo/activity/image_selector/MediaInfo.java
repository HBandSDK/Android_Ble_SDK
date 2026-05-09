package com.timaimee.vpdemo.activity.image_selector;

import android.net.Uri;

public class MediaInfo {
    public Uri uri;
    public String path;
    public String displayName;
    public long size;
    public int width;
    public int height;
    public String mimeType;
    public boolean isVideo;

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    @Override
    public String toString() {
        return "MediaInfo{" +
                "uri=" + uri +
                ", path='" + path + '\'' +
                ", displayName='" + displayName + '\'' +
                ", size=" + size +
                ", width=" + width +
                ", height=" + height +
                ", mimeType='" + mimeType + '\'' +
                ", isVideo=" + isVideo +
                '}';
    }
}
