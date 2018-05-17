package com.sjy.photoview.bean;

import java.io.Serializable;

/**
 * Created by sjy on 2018/5/17.
 */

public class GallBean implements Serializable {
    private int bitmapWidth;
    private int bitmapHeight;
    private Object thumbImgPath;
    private Object imgPath;

    public int getBitmapWidth() {
        return bitmapWidth;
    }

    public void setBitmapWidth(int bitmapWidth) {
        this.bitmapWidth = bitmapWidth;
    }

    public int getBitmapHeight() {
        return bitmapHeight;
    }

    public void setBitmapHeight(int bitmapHeight) {
        this.bitmapHeight = bitmapHeight;
    }

    public Object getThumbImgPath() {
        return thumbImgPath;
    }

    public void setThumbImgPath(Object thumbImgPath) {
        this.thumbImgPath = thumbImgPath;
    }

    public Object getImgPath() {
        return imgPath;
    }

    public void setImgPath(Object imgPath) {
        this.imgPath = imgPath;
    }
}
