package com.sjy.animphotoview;

import android.os.Bundle;
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
        gallBean.setThumbImgPath(R.mipmap.ji);
        GallBean gallBean2=new GallBean();
        gallBean2.setBitmapWidth(1080);
        gallBean2.setBitmapHeight(1440);
        gallBean2.setImgPath(R.mipmap.ji);
        gallBean2.setThumbImgPath(R.mipmap.ji);
        list.add(gallBean2);
        list.add(gallBean);

        rv.setLayoutManager(new GridLayoutManager(this,4));
        rv.setAdapter(new CommonAdapter<GallBean>(this,R.layout.item_img,list) {
            @Override
            public void convert(BaseViewHolder holder, GallBean gallBean, final int position) {
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
                                    public void loadImg(GallBean gallBean, ImageView targertView) {
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
