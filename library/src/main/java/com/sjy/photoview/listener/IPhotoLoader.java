package com.sjy.photoview.listener;

import android.view.View;
import android.widget.ImageView;

import com.sjy.photoview.bean.GallBean;

/**
 * Created by sjy on 2018/5/10.
 */

public interface IPhotoLoader {
    void loadImg(GallBean gallBean, ImageView targertView );
}
