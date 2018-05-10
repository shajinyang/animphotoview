package com.sjy.photoview.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.sjy.photoview.listener.IPhotoLoader;
import com.sjy.photoview.listener.OnPhotoViewClickListener;

/**
 * fragment带懒加载
 * Created by sjy on 2017/5/17.
 */

public class PvFragment extends Fragment {
    private OnPhotoViewClickListener onPhotoViewClickListener;
    private IPhotoLoader iPhotoLoader;
    private Object imgPath;
    public Context mContext;
    public Boolean isFirstLoad=true;//是否是第一次加载
    public Boolean isVisible=false;//是否可见
    public Boolean isPrepare=false;//是否全部准备完毕
    private PhotoView photoView;

    @SuppressLint("ValidFragment")
    public PvFragment(OnPhotoViewClickListener onPhotoViewClickListener,IPhotoLoader iPhotoLoader,Object imgPath) {
        this.onPhotoViewClickListener = onPhotoViewClickListener;
        this.iPhotoLoader=iPhotoLoader;
        this.imgPath=imgPath;
    }

    public PvFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        photoView=new PhotoView(getContext());
        ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        photoView.setLayoutParams(params);
        mContext=getActivity();
        isPrepare=true;
        init();
        loadData();
        return photoView;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            isVisible=true;
            loadData();
        }
    }

    /**
     * 图片复位
     */
    public void refreshDrawableState(){
        if(photoView!=null){
            if(1.0f!=photoView.getScale()){
                photoView.setScale(1.0f);
            }

        }
    }

    private void loadData(){
        if(isFirstLoad&& isVisible && isPrepare){
            initData();
            isFirstLoad=false;
        }
    }

    private  void init(){
        if(iPhotoLoader!=null){
            iPhotoLoader.display(imgPath,photoView);
        }
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshDrawableState();
                if(onPhotoViewClickListener!=null) {
                    onPhotoViewClickListener.onClick();
                }
            }
        });
    }




    private  void initData(){

    }

}
