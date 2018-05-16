package com.sjy.photoview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sjy.photoview.adapter.ViewPagerAdapter;
import com.sjy.photoview.anim.WrapperView;
import com.sjy.photoview.fragment.PvFragment;
import com.sjy.photoview.listener.IPhotoLoader;
import com.sjy.photoview.listener.OnPageChangeListener;
import com.sjy.photoview.listener.OnPhotoViewClickListener;
import com.sjy.photoview.view.HackyViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 仿微信朋友圈的图片浏览动画
 * Created by sjy on 2018/5/9.
 */

public class AnimPhotoView extends FrameLayout implements OnPhotoViewClickListener {
    //动画间隔
    private final int DURATION_TIME=300;
    //关闭动画间隔
    private final int DURATION_TIME_CLOSE=200;
    //是否正在动画
    private boolean isAnim=false;
    //是否关闭大图
    private boolean tag_open=false;
    //加载显示图片回调
    private IPhotoLoader iPhotoLoader;
    //切换回调
    private OnPageChangeListener onPageChangeListener;
    //当前位置
    private int currentPostion=0;
    //图片集合
    private List<Object> images;
    //背景
    private View bg;
    //目标viewpager
    private HackyViewPager targetView;
    //指示器view
    private TextView indicator;

    private List<Fragment> fragmentList=new ArrayList<>();

    private ViewPagerAdapter viewPagerAdapter;

    //用户传入的view
    private ImageView cusView;

    //切换绑定历史view记录
    private ImageView currentView;

    //原始图片信息
    private  float bitmapW=0;
    private  float bitmapH=0;

    //小图view的宽高
    private float origImgW=0;
    private float origImgH=0;
    //小图view的左边距
    private float origImgMarginLeft=0;
    //小图view的上边距
    private float origImgMarginTop=0;
    //图片放大后的中心点
    private float sCenterX=0;
    private float sCenterY=0;
    //大图view的中心点
    private float lCenterX=0;
    private float lCenterY=0;

    //targetView起始位移坐标
    private float transStartX=0;
    private float transStartY=0;

    //动画平移距离
    private float translationX=0;
    private float translationY=0;

    public AnimPhotoView(@NonNull Context context) {
        this(context,null);
    }

    public AnimPhotoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AnimPhotoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBg();
        initTargetView();
        initIndicator();
    }

    /**
     * 从目标view开始
     * @param fromImg
     * @return
     */
    public AnimPhotoView fromView(ImageView fromImg){
        this.cusView=fromImg;
        currentView=fromImg;
        return this;
    }

    /**
     * 初始图片宽度
     * @return
     */
    public AnimPhotoView whitBitmapWidth(float bitmapW){
        this.bitmapW=bitmapW;
        return this;
    }

    /**
     * 初始图片高度
     * @return
     */
    public AnimPhotoView whitBitmapHeight(float bitmapH){
        this.bitmapH=bitmapH;
        return this;
    }

    /**
     * 设置当前position
     * @param postion
     * @return
     */
    public AnimPhotoView setCurrentPostion(int postion){
        this.currentPostion=postion;
        return this;
    }

    /**
     * 设置图片集合
     * @param images
     * @return
     */
    public AnimPhotoView withImgs(List<Object> images){
        this.images=images;
        return this;
    }


    /**
     * 设置图片加载库
     * @return
     */
    public AnimPhotoView setImageLoader(IPhotoLoader iPhotoLoader){
        this.iPhotoLoader=iPhotoLoader;
        return this;
    }

    /**
     * 设置多图片页面改变回调
     * @param onPageChangeListener
     * @return
     */
    public AnimPhotoView setOnPageChange(OnPageChangeListener onPageChangeListener){
        this.onPageChangeListener=onPageChangeListener;
        return this;
    }

    /**
     * 显示展开
     */
    public void start(){
        transStartX=0;
        transStartY=0;
        initViewPager();
        calculateAttr();
        layoutAllView();
        updatePostion();
        updateIndicator();
        indicator.setVisibility(VISIBLE);
        animOpen();
        tag_open=true;
    }


    /**
     *关闭
     */
    public boolean close(){
        if(tag_open){
            animClose();
            tag_open=false;
            return true;
        }else {
            return false;
        }

    }

    /**
     * 绑定关系改变调用
     * @param fromImg
     * @param bitmapW
     * @param bitmapH
     */
    public void onBindChanged(ImageView fromImg,float bitmapW,float bitmapH){
        transStartX=fromImg.getX()-currentView.getX();
        transStartY=fromImg.getY()-currentView.getY();
        this.cusView=fromImg;
        this.bitmapW=bitmapW;
        this.bitmapH=bitmapH;
        //重新计算动画属性
        calculateAttr();
    }


    /**
     * 计算属性(平移 缩放)
     */
    private void calculateAttr(){
        if(cusView==null)return;

        //放大后，位移前的相对屏幕的坐标
        int[] sposition=new int[2];
        cusView.getLocationInWindow(sposition);

        //放大后，位移之后的相对屏幕的坐标
        int[] lposition=new int[2];
        this.getLocationInWindow(lposition);


        //计算动画开始时view的宽高及布局位置
        origImgW=cusView.getWidth();
        origImgH=origImgW/bitmapW*bitmapH;
        origImgMarginLeft=sposition[0]-lposition[0];//减去父布局的相对x轴偏移
        origImgMarginTop=sposition[1]-lposition[1];//减去父布局的相对y轴偏移

        //计算位移距离
        sCenterX= this.getWidth()/2+sposition[0];
        sCenterY= this.getHeight()/2+sposition[1]-(origImgH-cusView.getHeight())/2;
        lCenterX=this.getWidth()/2+lposition[0];
        lCenterY=this.getHeight()/2+lposition[1];
        translationX=lCenterX-sCenterX+transStartX;
        translationY=lCenterY-sCenterY+transStartY;


    }

    /**
     * 完成初始布局
     */
    private void layoutAllView(){
        LayoutParams params= (LayoutParams) targetView.getLayoutParams();
        params.width= (int) origImgW;
        params.height= (int) origImgH;
        params.setMargins((int)origImgMarginLeft,(int)(origImgMarginTop-(origImgH-cusView.getHeight())/2),0,0);
        targetView.setLayoutParams(params);
    }



    /**
     * 初始化背景
     */
    private void initBg(){
        ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bg=new View(getContext());
        bg.setBackgroundColor(Color.BLACK);
        bg.setAlpha(0f);
        bg.setLayoutParams(params);
        addView(bg);
    }


    /**
     * 初始化viewpager
     */
    private void initTargetView(){
        targetView=new HackyViewPager(getContext());
        targetView.setVisibility(INVISIBLE);
        targetView.setId(R.id.hack_viewer_pager_sjy);
        targetView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animClose();
            }
        });
        targetView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(onPageChangeListener!=null){
                    currentPostion=position;
                    onPageChangeListener.onPageChange(position);
                    updateIndicator();
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        addView(targetView);
    }

    /**
     * 初始化指示器
     */
    private void initIndicator(){
        LayoutParams params=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity=Gravity.BOTTOM;
        params.setMargins(0,0,0,25);
        indicator=new TextView(getContext());
        indicator.setText("1/1");
        indicator.setTextSize(18);
        indicator.setTextColor(Color.LTGRAY);
        indicator.setGravity(Gravity.CENTER);
        indicator.setVisibility(INVISIBLE);
        indicator.setLayoutParams(params);
        addView(indicator);
    }

    /**
     * 设置多图
     */
    private void initViewPager(){
        if(fragmentList==null){
            fragmentList=new ArrayList<>();
        }else {
            fragmentList.clear();
        }
        for (Object imgPath:images
                ) {
            fragmentList.add(new PvFragment(this,iPhotoLoader,imgPath));
        }
        if(viewPagerAdapter==null){
            viewPagerAdapter=new ViewPagerAdapter(((AppCompatActivity)getContext()).getSupportFragmentManager(),fragmentList);
            targetView.setAdapter(viewPagerAdapter);
        }else {
            viewPagerAdapter.setFragments(fragmentList);
        }
        targetView.setOffscreenPageLimit(fragmentList.size());

    }


    /**
     * 更新viewpager当前浏览位置
     */
    private void updatePostion(){
        if(targetView!=null){
            targetView.setCurrentItem(currentPostion);
        }
    }

    /**
     * 更新指示器
     */
    private void updateIndicator(){
        if(indicator!=null&&images!=null){
            indicator.setText((currentPostion+1)+"/"+images.size());
        }
    }

    /**
     * 展开动画
     */
    private void animOpen(){
        if(isAnim)return;
        isAnim=true;
        WrapperView wrapperView=new WrapperView(targetView);
        ObjectAnimator oa= ObjectAnimator.ofFloat(wrapperView,"mwidth",origImgW,this.getWidth());
        ObjectAnimator oa2= ObjectAnimator.ofFloat(wrapperView,"mheight",origImgH,this.getHeight());
        ObjectAnimator oa3= ObjectAnimator.ofFloat(targetView,"translationX",transStartX,translationX);
        ObjectAnimator oa4= ObjectAnimator.ofFloat(targetView,"translationY",transStartY,translationY);
        ObjectAnimator oa5= ObjectAnimator.ofFloat(bg,"alpha",0,1);
        AnimatorSet set=new AnimatorSet();
        set.play(oa).with(oa2).with(oa5).with(oa3).with(oa4);
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                targetView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnim=false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        set.setDuration(DURATION_TIME);
        set.start();
    }

    /**
     * 关闭动画
     */
    private void animClose(){
        if(isAnim)return;
        isAnim=true;
        WrapperView wrapperView=new WrapperView(targetView);
        ObjectAnimator oa= ObjectAnimator.ofFloat(wrapperView,"mwidth",this.getWidth(),origImgW);
        ObjectAnimator oa2= ObjectAnimator.ofFloat(wrapperView,"mheight",this.getHeight(),origImgH);
        ObjectAnimator oa3= ObjectAnimator.ofFloat(targetView,"translationX",translationX,transStartX);
        ObjectAnimator oa4= ObjectAnimator.ofFloat(targetView,"translationY",translationY,transStartY);
        ObjectAnimator oa5= ObjectAnimator.ofFloat(bg,"alpha",1,0);
        AnimatorSet set=new AnimatorSet();
        set.play(oa).with(oa2).with(oa5).with(oa3).with(oa4);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                indicator.setVisibility(INVISIBLE);
                currentView=cusView;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnim=false;
                targetView.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.setDuration(DURATION_TIME_CLOSE);
        set.setInterpolator(new AccelerateInterpolator());
        set.start();
    }

    @Override
    public void onClick() {
        close();
    }
}
