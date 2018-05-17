![](sjylogo.png)
# 仿微信朋友圈图片浏览photoview


  这是第一版功能：支持多图浏览，可放大缩小
  后续第二版会增加微信的手势功能


![](gif22.gif)




### 如何使用

#### Android Studio
    第一步：
      在项目的gradle里配置
      allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
      }

      第二步：
      在module的gradle里配置
      dependencies {
         ...
      	 compile 'com.github.shajinyang:animphotoview:1.0.2'
      }



#### 代码示例：recycleview的item点击

     AnimPhotoView pv;
     ArrayList<GallBean> list=new ArrayList<>();
     ...

    @Override
    public void onClick(View v) {
        Gallery.getInstance()
                .with(BrowActivity.this)//必须传递activity
                .fromView(v)//动画开始的view，一般为imageview
                .currentPostion(position)//当前位置
                .loadImages(list)//图片集合
                //自定义加载器
                .setLoader(new IPhotoLoader() {
                    @Override
                    public void loadImg(GallBean gallBean, ImageView targertView) {
                        Glide.with(BrowActivity.this)
                                .load(gallBean.getImgPath())
                                .into(targertView);
                    }
                })
                //监听回调，一般在fragment多图片切换时回调，需要重新调用绑定关系方法
                .setonPageChangeListener(new OnPageChangeListener() {
                    @Override
                    public void onPageChange(int position) {
                        Gallery.getInstance()
                                .onBindChange(rv.getChildAt(position),position);
                    }
                })
                .start();
    }

    ...



















