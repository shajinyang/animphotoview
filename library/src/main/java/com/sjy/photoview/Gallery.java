package com.sjy.photoview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.sjy.photoview.activity.PhotoViewActivity;
import com.sjy.photoview.bean.GallBean;
import com.sjy.photoview.listener.IPhotoLoader;
import com.sjy.photoview.listener.OnCalDataChanged;
import com.sjy.photoview.listener.OnPageChangeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjy on 2018/5/17.
 */

public class Gallery {

    //当前activity
    private Activity mActivity;
    //用户传入的view
    private View cusView;
    //图片集合
    private ArrayList<GallBean> imgs;
    //当前位置
    private int currentPosition;
    //图片加载
    private IPhotoLoader iPhotoLoader;
    //切换回调
    private OnPageChangeListener onPageChangeListener;
    //数据改变回调
    private OnCalDataChanged onCalDataChanged;
    //覆盖view的宽高
    private float overLayViewWidth=0;
    private float overLayViewHeight=0;
    //覆盖view的左边距
    private float overLayViewMarginLeft=0;
    //覆盖view的上边距
    private float overLayViewMarginTop=0;


    private Gallery(){

    }

    private static class GalleryHolder{
        private static final Gallery gallery=new Gallery();
    }


    public static Gallery getInstance(){
        return GalleryHolder.gallery;
    }

    public Gallery with(Activity mActivity){
        this.mActivity=mActivity;
        return this;
    }

    public Gallery fromView(View cusView){
        this.cusView=cusView;
        return this;
    }

    public Gallery loadImages(ArrayList<GallBean> imgs){
        this.imgs=imgs;
        return this;
    }

    public Gallery currentPostion(int currentPosition){
        this.currentPosition=currentPosition;
        return this;
    }

    public Gallery setLoader(IPhotoLoader iPhotoLoader){
        this.iPhotoLoader=iPhotoLoader;
        return this;
    }

    public Gallery setonPageChangeListener(OnPageChangeListener onPageChangeListener){
        this.onPageChangeListener=onPageChangeListener;
        return this;
    }

    public void start(){
        calculatPostion();
        PhotoViewActivity.setListener(iPhotoLoader);
        PhotoViewActivity.setOnPageChangeListener(onPageChangeListener);
        Intent intent=new Intent(mActivity, PhotoViewActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("imgs",imgs);
        bundle.putFloat("overLayViewWidth",overLayViewWidth);
        bundle.putFloat("overLayViewHeight",overLayViewHeight);
        bundle.putFloat("overLayViewMarginLeft",overLayViewMarginLeft);
        bundle.putFloat("overLayViewMarginTop",overLayViewMarginTop);
        bundle.putSerializable("cusViewScaleType",((ImageView)cusView).getScaleType());
        bundle.putInt("position",currentPosition);
        intent.putExtras(bundle);
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(0,0);
    }

    /**
     * 绑定关系改变时调用，fragment滑动时
     * @param cusView
     * @param currentPosition
     */
    public void onBindChange(View cusView,int currentPosition){
        this.cusView=cusView;
        this.currentPosition=currentPosition;
        calculatPostion();
        onCalDataChanged.onCalChange(overLayViewWidth,overLayViewHeight,overLayViewMarginLeft,overLayViewMarginTop);
    }

    public void setOnCalDataChanged(OnCalDataChanged onCalDataChanged){
        this.onCalDataChanged=onCalDataChanged;
    }

    /**
     * 计算overLay大小和位置
     */
    private void calculatPostion(){
        if(cusView==null)return;
        if(imgs==null||imgs.size()==0)return;
        //用户传入的view的绝对坐标
        int[] sposition=new int[2];
        cusView.getLocationInWindow(sposition);

        //计算覆盖物view的宽高及布局位置
        overLayViewWidth=cusView.getWidth();
        overLayViewHeight=cusView.getHeight();
        overLayViewMarginLeft=sposition[0];
        overLayViewMarginTop=sposition[1];

    }





}
