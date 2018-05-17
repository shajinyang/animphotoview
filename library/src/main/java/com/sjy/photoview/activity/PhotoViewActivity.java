package com.sjy.photoview.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sjy.photoview.Gallery;
import com.sjy.photoview.R;
import com.sjy.photoview.adapter.ViewPagerAdapter;
import com.sjy.photoview.anim.WrapperView;
import com.sjy.photoview.bean.GallBean;
import com.sjy.photoview.fragment.PvFragment;
import com.sjy.photoview.listener.IPhotoLoader;
import com.sjy.photoview.listener.OnCalDataChanged;
import com.sjy.photoview.listener.OnPageChangeListener;
import com.sjy.photoview.listener.OnPhotoViewClickListener;
import com.sjy.photoview.view.HackyViewPager;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by sjy on 2018/5/17.
 */

public class PhotoViewActivity extends AppCompatActivity implements OnCalDataChanged {
    private static final int DURATION_TIME=500;
    private static final int DURATION_TIME_CLOSE=200;
    //是否正在动画
    private boolean isAnim=false;
    private RelativeLayout relativeLayout;
    private View bg;
    //图片加载
    private static IPhotoLoader iPhotoLoader;
    //切换回调
    private static OnPageChangeListener onPageChangeListener;

    private  float overLayViewWidth;
    private  float overLayViewHeight;
    private  float overLayViewMarginLeft;
    private  float overLayViewMarginTop;
    private int position=0;

    //targetView起始位移坐标
    private float transStartX=0;
    private float transStartY=0;

    //targetView动画开始时起始位移坐标中心点
    private float startCenterX;
    private float startCenterY;


    //targetView首次加载坐标中心点
    private float currentCenterX;
    private float currentCenterY;



    //动画平移距离
    private float translationX=0;
    private float translationY=0;

    //动画缩放
    private float scaleX=0f;
    private float scaleY=0f;

    private ArrayList<GallBean> imgs;
    private List<Fragment> fragments;
    private HackyViewPager hackyViewPager;
    private TextView indicator;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_module_lib_pv_browers);
        relativeLayout= (RelativeLayout) findViewById(R.id.module_lib_bg);
        bg=findViewById(R.id.bg_color);
        indicator= (TextView) findViewById(R.id.indicate);
        getIntentData();
        initListener();


    }

    private void initListener(){
        Gallery.getInstance()
                .setOnCalDataChanged(this);
    }

    public static void setListener(IPhotoLoader iPhotoLoader){
        PhotoViewActivity.iPhotoLoader=iPhotoLoader;
    }

    public static void setOnPageChangeListener(OnPageChangeListener onPageChangeListener){
        PhotoViewActivity.onPageChangeListener=onPageChangeListener;
    }



    private void getIntentData() {
        if(getIntent().getExtras()!=null){
            overLayViewWidth=getIntent().getExtras().getFloat("overLayViewWidth");
            overLayViewHeight=getIntent().getExtras().getFloat("overLayViewHeight");
            overLayViewMarginLeft=getIntent().getExtras().getFloat("overLayViewMarginLeft");
            overLayViewMarginTop=getIntent().getExtras().getFloat("overLayViewMarginTop");
            position=getIntent().getExtras().getInt("position");
            imgs= (ArrayList<GallBean>) getIntent().getExtras().getSerializable("imgs");
            indicator.setText((position+1)+" / "+imgs.size());


            //计算首次加载
            currentCenterX=(overLayViewWidth+2*overLayViewMarginLeft)/2;
            currentCenterY=(overLayViewHeight+2*overLayViewMarginTop)/2;


            relativeLayout.post(new Runnable() {
                @Override
                public void run() {
                    initOverLayView();
                    hackyViewPager.post(new Runnable() {
                        @Override
                        public void run() {
                            calculateDistance();
                            animOpen();
                        }
                    });
                }
            });

        }
    }

    /**
     * 计算
     */
    private void calculateDistance(){
        //计算缩放
        scaleX=relativeLayout.getWidth()*1.0f/overLayViewWidth;
        scaleY=relativeLayout.getHeight()*1.0f/overLayViewHeight;

        //计算初始中心点
        startCenterX=(overLayViewWidth+2*overLayViewMarginLeft)/2;
        startCenterY=(overLayViewHeight+2*overLayViewMarginTop)/2;

        //计算目标返回的view与当前view的距离偏差
        transStartX=startCenterX-currentCenterX;
        transStartY=startCenterY-currentCenterY;

        //计算位移距离
        translationX=relativeLayout.getWidth()/2-startCenterX+transStartX;
        translationY=relativeLayout.getHeight()/2-startCenterY+transStartY;

    }

    /**
     * 初始化覆盖物
     */
    private void  initOverLayView(){
        fragments=new ArrayList<>();
        hackyViewPager=new HackyViewPager(this);
        hackyViewPager.setId(R.id.hack_viewer_pager_sjy);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) overLayViewWidth,(int) overLayViewHeight);
        params.setMargins((int)overLayViewMarginLeft,(int) overLayViewMarginTop, 0, 0);//左上右下
        hackyViewPager.setLayoutParams(params);
        relativeLayout.addView(hackyViewPager,relativeLayout.getChildCount()-1);
        for (GallBean bean:imgs
                ) {
            fragments.add(new PvFragment(new OnPhotoViewClickListener() {
                @Override
                public void onClick() {
                    animClose();
                }
            }, iPhotoLoader, bean));
        }
        hackyViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),fragments));
        hackyViewPager.setOffscreenPageLimit(fragments.size());
        hackyViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int p) {
                position=p;
                indicator.setText((position+1)+" / "+imgs.size());
                onPageChangeListener.onPageChange(p);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        hackyViewPager.setCurrentItem(position);
    }



    @Override
    public void onBackPressed() {
        animClose();
    }


    /**
     * 展开动画
     */
    private void animOpen(){
        if(isAnim)return;
        isAnim=true;
        ObjectAnimator oa= ObjectAnimator.ofFloat(hackyViewPager,"scaleX",1,scaleX);
        ObjectAnimator oa2= ObjectAnimator.ofFloat(hackyViewPager,"scaleY",1,scaleY);
        ObjectAnimator oa3= ObjectAnimator.ofFloat(hackyViewPager,"translationX",transStartX,translationX);
        ObjectAnimator oa4= ObjectAnimator.ofFloat(hackyViewPager,"translationY",transStartY,translationY);
        ObjectAnimator oa5= ObjectAnimator.ofFloat(bg,"alpha",0,1);
        AnimatorSet set=new AnimatorSet();
        set.play(oa).with(oa2).with(oa3).with(oa4).with(oa5);
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
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
        ObjectAnimator oa= ObjectAnimator.ofFloat(hackyViewPager,"scaleX",scaleX,1);
        ObjectAnimator oa2= ObjectAnimator.ofFloat(hackyViewPager,"scaleY",scaleY,1);
        ObjectAnimator oa3= ObjectAnimator.ofFloat(hackyViewPager,"translationX",translationX,transStartX);
        ObjectAnimator oa4= ObjectAnimator.ofFloat(hackyViewPager,"translationY",translationY,transStartY);
        ObjectAnimator oa5= ObjectAnimator.ofFloat(bg,"alpha",1,0);
        AnimatorSet set=new AnimatorSet();
        set.play(oa).with(oa2).with(oa5).with(oa3).with(oa4);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                indicator.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnim=false;
                finish();
                overridePendingTransition(0,0);
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
    public void onCalChange(float overLayViewWidth, float overLayViewHeight, float overLayViewMarginLeft, float overLayViewMarginTop) {
        this.overLayViewHeight=overLayViewHeight;
        this.overLayViewWidth=overLayViewWidth;
        this.overLayViewMarginLeft=overLayViewMarginLeft;
        this.overLayViewMarginTop=overLayViewMarginTop;
        calculateDistance();
    }
}
