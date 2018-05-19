package com.sjy.animphotoview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sjy.animphotoview.base.BaseViewHolder;
import com.sjy.animphotoview.base.CommonAdapter;
import com.sjy.photoview.Gallery;
import com.sjy.photoview.bean.GallBean;
import com.sjy.photoview.listener.IPhotoLoader;
import com.sjy.photoview.listener.OnPageChangeListener;

import java.util.ArrayList;

/**
 * Created by sjy on 2018/5/17.
 */

public class BrowActivity extends AppCompatActivity {
    RecyclerView rv;
    ArrayList<GallBean> list;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_browser22);
        rv=findViewById(R.id.rv);

        list=new ArrayList<>();
        GallBean gallBean=new GallBean();
        gallBean.setBitmapWidth(1080);
        gallBean.setBitmapHeight(1440);
        gallBean.setImgPath(R.mipmap.ji);
        gallBean.setThumbImgPath(R.mipmap.test22);
        GallBean gallBean2=new GallBean();
        gallBean2.setBitmapWidth(500);
        gallBean2.setBitmapHeight(312);
        gallBean2.setImgPath(R.mipmap.test22);
        gallBean2.setThumbImgPath(R.mipmap.test22);
        GallBean gallBean3=new GallBean();
        gallBean3.setBitmapWidth(500);
        gallBean3.setBitmapHeight(312);
        gallBean3.setImgPath(R.mipmap.test22);
        gallBean3.setThumbImgPath(R.mipmap.test22);
        GallBean gallBean4=new GallBean();
        gallBean4.setBitmapWidth(500);
        gallBean4.setBitmapHeight(312);
        gallBean4.setImgPath(R.mipmap.test22);
        gallBean4.setThumbImgPath(R.mipmap.test22);
        list.add(gallBean2);
        list.add(gallBean);
        list.add(gallBean3);
        list.add(gallBean4);

        rv.setLayoutManager(new GridLayoutManager(this,3));
        rv.setAdapter(new CommonAdapter<GallBean>(this,R.layout.item_img,list) {
            @Override
            public void convert(BaseViewHolder holder, GallBean gallBean, final int position) {
                Glide.with(mContext)
                        .load(gallBean.getImgPath())
                        .into((ImageView) holder.getView(R.id.s_img));
                holder.setOnClickListener(R.id.s_img, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Gallery.getInstance()
                                .with(BrowActivity.this)
                                .fromView(v)
                                .currentPostion(position)
                                .loadImages(list)
                                .setLoader(new IPhotoLoader() {
                                    @Override
                                    public void loadImg(GallBean gallBean, final ImageView targertView) {
//                                        Glide.with(mContext).asBitmap().load(gallBean.getImgPath()).into(new SimpleTarget<Bitmap>() {
//                                            @Override
//                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                                                    targertView.setImageBitmap(resource);
//                                            }
//                                        });
                                        Glide.with(BrowActivity.this)
                                                .load(gallBean.getImgPath())
                                                .into(targertView);
                                    }
                                })
                                .setonPageChangeListener(new OnPageChangeListener() {
                                    @Override
                                    public void onPageChange(int position) {
                                        Gallery.getInstance()
                                                .onBindChange(rv.getChildAt(position),position);
                                    }
                                })
                                .start();
                    }
                });
            }
        });
    }

}
