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
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
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
import com.sjy.photoview.listener.OnScaleChangeListener;
import com.sjy.photoview.view.HackyViewPager;

import java.util.ArrayList;
import java.util.List;


/**
 * 带动画的activity
 * Created by sjy on 2018/5/17.
 */

public class PhotoViewActivity extends AppCompatActivity implements OnCalDataChanged {
    private static final int DURATION_TIME=350;
    private static final int DURATION_TIME_CLOSE=250;
    //是否正在动画
    private boolean isAnim=false;
    private FrameLayout relativeLayout;
    private View bg;
    //图片加载
    private static IPhotoLoader iPhotoLoader;
    //切换回调
    private static OnPageChangeListener onPageChangeListener;

    private ImageView.ScaleType cusViewScaleType;
    private  float overLayViewWidth;
    private  float overLayViewHeight;
    private  float overLayViewMarginLeft;
    private  float overLayViewMarginTop;
    private int position=0;

    private float overLayViewWidthClose=0;
    private float overLayViewHeightClose=0;
    private float overLayViewMarginLeftClose=0;
    private float overLayViewMarginTopClose=0;


    //是否打开动画切换效果
    private boolean animStyle=true;

    //动画平移距离
    private float translationX=0;
    private float translationY=0;

    //关闭动画平移距离
    private float translationCloseX=0;
    private float translationCloseY=0;


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
        relativeLayout= (FrameLayout) findViewById(R.id.module_lib_bg);
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
            animStyle=getIntent().getExtras().getBoolean("anim");
            position=getIntent().getExtras().getInt("position");
            imgs= (ArrayList<GallBean>) getIntent().getExtras().getSerializable("imgs");
            cusViewScaleType= (ImageView.ScaleType) getIntent().getExtras().getSerializable("cusViewScaleType");
            indicator.setText((position+1)+" / "+imgs.size());

            relativeLayout.post(new Runnable() {
                @Override
                public void run() {
                    if(animStyle) {
                        initOverLayView();
                        hackyViewPager.post(new Runnable() {
                            @Override
                            public void run() {
                                calculateDistance();
                                animOpen();
                            }
                        });
                    }else {
                        initOverLayViewWithOutAnim();
                    }

                }
            });

        }
    }

    /**
     * 计算
     */
    private void calculateDistance(){
        //获取当前的图像尺寸
        int bitmapW=imgs.get(position).getBitmapWidth();
        int bitmapH=imgs.get(position).getBitmapHeight();

        //计算覆盖物view关闭时的的宽高及布局位置
        if(bitmapH*1.0f/bitmapW>relativeLayout.getHeight()*1.0f/relativeLayout.getWidth()){
            overLayViewHeightClose=relativeLayout.getHeight();
            overLayViewWidthClose=overLayViewHeightClose/bitmapH*bitmapW;
        }else {
            overLayViewWidthClose=relativeLayout.getWidth();
            overLayViewHeightClose=overLayViewWidthClose/bitmapW*bitmapH;
        }
        overLayViewMarginLeftClose=(relativeLayout.getWidth()-overLayViewWidthClose)/2;
        overLayViewMarginTopClose=(relativeLayout.getHeight()-overLayViewHeightClose)/2;

        //展开动画移动距离
        translationX=-overLayViewMarginLeft;
        translationY=-overLayViewMarginTop;

        //关闭动画时的移动距离
        translationCloseX=overLayViewMarginLeft-overLayViewMarginLeftClose;
        translationCloseY=overLayViewMarginTop-overLayViewMarginTopClose;


    }

    /**
     * 初始化覆盖物
     */
    private void  initOverLayView(){
        fragments=new ArrayList<>();
        hackyViewPager=new HackyViewPager(this);
        hackyViewPager.setId(R.id.hack_viewer_pager_sjy);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) overLayViewWidth,(int) overLayViewHeight);
        params.setMargins((int)overLayViewMarginLeft,(int) overLayViewMarginTop, 0, 0);
        hackyViewPager.setLayoutParams(params);
        relativeLayout.addView(hackyViewPager,relativeLayout.getChildCount()-1);
        for (GallBean bean:imgs
                ) {
            PvFragment pvFragment=new PvFragment(new OnPhotoViewClickListener() {
                @Override
                public void onClick() {
                    ((PvFragment)fragments.get(position)).scaleChange(cusViewScaleType);
                    setHackyViewPagerPosition();
                    animClose();
                }
            }, iPhotoLoader, bean);
            fragments.add(pvFragment);
        }
        hackyViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),fragments));
        hackyViewPager.setOffscreenPageLimit(3);
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

    /**
     * 初始化覆盖物(不带动画效果)
     */
    private void  initOverLayViewWithOutAnim(){
        bg.setAlpha(1f);
        fragments=new ArrayList<>();
        hackyViewPager=new HackyViewPager(this);
        hackyViewPager.setId(R.id.hack_viewer_pager_sjy);
        hackyViewPager.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        relativeLayout.addView(hackyViewPager,relativeLayout.getChildCount()-1);
        for (GallBean bean:imgs
                ) {
            PvFragment pvFragment=new PvFragment(new OnPhotoViewClickListener() {
                @Override
                public void onClick() {
                    finish();
                }
            }, iPhotoLoader, bean);
            fragments.add(pvFragment);
        }
        hackyViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),fragments));
        hackyViewPager.setOffscreenPageLimit(3);
        hackyViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int p) {
                position=p;
                indicator.setText((position+1)+" / "+imgs.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        hackyViewPager.setCurrentItem(position);
    }



    @Override
    public void onBackPressed() {
        if(animStyle) {
            ((PvFragment)fragments.get(position)).scaleChange(cusViewScaleType);
            setHackyViewPagerPosition();
            animClose();
        }else {
            finish();
        }
    }


    /**
     * 展开动画
     */
    private void animOpen(){
        if(isAnim)return;
        isAnim=true;
        WrapperView wrapperView=new WrapperView(hackyViewPager);
        ObjectAnimator oa= ObjectAnimator.ofFloat(wrapperView,"mwidth",overLayViewWidth,relativeLayout.getWidth());
        ObjectAnimator oa2= ObjectAnimator.ofFloat(wrapperView,"mheight",overLayViewHeight,relativeLayout.getHeight());
        ObjectAnimator oa3= ObjectAnimator.ofFloat(hackyViewPager,"translationX",0,translationX);
        ObjectAnimator oa4= ObjectAnimator.ofFloat(hackyViewPager,"translationY",0,translationY);
        ObjectAnimator oa5= ObjectAnimator.ofFloat(bg,"alpha",0,1);
        AnimatorSet set=new AnimatorSet();
        set.play(oa).with(oa2).with(oa5).with(oa3).with(oa4);
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                indicator.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((PvFragment)fragments.get(position)).scaleChange(ImageView.ScaleType.FIT_CENTER);
                    }
                },DURATION_TIME/2);

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
     * 关闭动画时，重新改变viewpager位置
     * 主要是为了修正 切换ScaleType时  闪动的问题
     */
    private void setHackyViewPagerPosition(){
        FrameLayout.LayoutParams params= (FrameLayout.LayoutParams) hackyViewPager.getLayoutParams();
        params.width= (int) overLayViewWidthClose;
        params.height= (int) overLayViewHeightClose;
        params.setMargins((int)overLayViewMarginLeftClose,(int) overLayViewMarginTopClose, 0, 0);
        hackyViewPager.setLayoutParams(params);
    }


    /**
     * 关闭动画
     */
    private void animClose(){
        if(isAnim)return;
        isAnim=true;
        WrapperView wrapperView=new WrapperView(hackyViewPager);
        ObjectAnimator oa= ObjectAnimator.ofFloat(wrapperView,"mwidth",overLayViewWidthClose,overLayViewWidth);
        ObjectAnimator oa2= ObjectAnimator.ofFloat(wrapperView,"mheight",overLayViewHeightClose,overLayViewHeight);
        ObjectAnimator oa3= ObjectAnimator.ofFloat(hackyViewPager,"translationX",0,translationCloseX);
        ObjectAnimator oa4= ObjectAnimator.ofFloat(hackyViewPager,"translationY",0,translationCloseY);
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
