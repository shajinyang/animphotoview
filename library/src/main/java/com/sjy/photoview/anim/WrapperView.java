package com.sjy.photoview.anim;

import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by sjy on 2018/5/9.
 */

public class WrapperView {
    private ImageView imageView;
    private float mwidth;
    private float mheight;

    public float getMwidth() {
        return mwidth;
    }

    public void setMwidth(float mwidth) {
        this.mwidth = mwidth;
        ViewGroup.LayoutParams params= imageView.getLayoutParams();
        params.width= (int) mwidth;
        params.height= (int) mheight;
        imageView.setLayoutParams(params);
        imageView.requestLayout();
    }

    public float getMheight() {
        return mheight;
    }

    public void setMheight(float mheight) {
        this.mheight = mheight;
        ViewGroup.LayoutParams params= imageView.getLayoutParams();
        params.width= (int) mwidth;
        params.height= (int) mheight;
        imageView.setLayoutParams(params);
        imageView.requestLayout();
    }

    public WrapperView(ImageView imageView) {
        this.imageView = imageView;
    }
}
