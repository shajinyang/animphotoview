package com.sjy.animphotoview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sjy.animphotoview.base.BaseViewHolder;
import com.sjy.animphotoview.base.CommonAdapter;
import com.sjy.photoview.AnimPhotoView;
import com.sjy.photoview.listener.IPhotoLoader;
import com.sjy.photoview.listener.OnPageChangeListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView rv;
    AnimPhotoView pv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv=findViewById(R.id.rv);
        pv=findViewById(R.id.pv);
        initRv();
        WindowUtil.transStateBar(this);
    }

    private void initRv(){
        final List<ImgBean> imgs=new ArrayList<>();
        imgs.add(new ImgBean(R.mipmap.ji,1080,1440) );
        imgs.add(new ImgBean(R.mipmap.test22,500,312));
        imgs.add(new ImgBean(R.mipmap.ji,1080,1440));
        imgs.add(new ImgBean(R.mipmap.test22,500,312));
        imgs.add(new ImgBean(R.mipmap.ji,1080,1440));

        final List<Object> imgspath=new ArrayList<>();
        imgspath.add(R.mipmap.ji);
        imgspath.add(R.mipmap.test22);
        imgspath.add(R.mipmap.ji);
        imgspath.add(R.mipmap.test22);
        imgspath.add(R.mipmap.ji);
        List<String> list=new ArrayList<>();
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        rv.setLayoutManager(new GridLayoutManager(this,3));
        rv.setAdapter(new CommonAdapter<ImgBean>(this,R.layout.item_img,imgs) {
            @Override
            public void convert(BaseViewHolder holder, final ImgBean imgBean, final int i) {
                Glide.with(mContext)
                        .load(imgBean.getPath())
                        .into((ImageView) holder.getView(R.id.s_img));
                holder.setOnClickListener(R.id.s_img, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pv.fromView((ImageView)v)
                                .whitBitmapWidth(imgBean.getWidth())
                                .whitBitmapHeight(imgBean.getHeight())
                                .withImgs(imgspath)
                                .setImageLoader(new IPhotoLoader() {
                                    @Override
                                    public void display(Object path, ImageView targertView) {
                                        Glide.with(mContext)
                                                .load(path)
                                                .into(targertView);
                                    }
                                })
                                .setCurrentPostion(i)
                                .setOnPageChange(new OnPageChangeListener() {
                                    @Override
                                    public void onPageChange(int position) {
                                        pv.onBindChanged((ImageView) rv.getChildAt(position),imgs.get(position).getWidth(),imgs.get(position).getHeight());
                                    }
                                })
                                .start();
                    }
                });
            }

        });

    }

    @Override
    public void onBackPressed() {
        if(!pv.close()){
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
        }
    }

    private class ImgBean{
        private Object path;
        private int width;
        private int height;

        public ImgBean(Object path, int width, int height) {
            this.path = path;
            this.width = width;
            this.height = height;
        }

        public Object getPath() {
            return path;
        }

        public void setPath(Object path) {
            this.path = path;
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
    }
}
