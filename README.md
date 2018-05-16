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
      	 compile 'com.github.shajinyang:animphotoview:1.0.1'
      }

#### 布局引入
    确保photoview在布局的最上层即可
    示例：
    <?xml version="1.0" encoding="utf-8"?>
    <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:background="#efefef"
        android:orientation="vertical"
        >
        <android.support.v7.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="70dp"
            app:title="微信图片浏览"
            android:fitsSystemWindows="true"
            android:background="@color/colorPrimary"
            app:titleTextColor="#ffffff"
            />
        <android.support.v7.widget.RecyclerView
            android:id="@+id/rv"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:src="@mipmap/ji"
            android:layout_below="@id/toolbar"
            />
        <com.sjy.photoview.AnimPhotoView
            android:id="@+id/pv"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            />
    </RelativeLayout>



#### 代码示例：recycleview的item点击

     AnimPhotoView pv;
     List<Object> imgspath=new ArrayList<>();
     ...

    @Override
    public void onClick(View v) {
        pv.fromView((ImageView)v)     //当前点击的view
                .whitBitmapWidth(100) //当前浏览图片的宽度
                .whitBitmapHeight(100) //当前浏览图片的高度
                .withImgs(imgspath) //一组图片集合
                    //设置加载图片回调，可以自行选用glide还是fresco或者原生方式
                .setImageLoader(new IPhotoLoader() {
                    @Override
                    public void display(Object path, ImageView targertView) {
                        Glide.with(mContext)
                                .load(path)
                                .into(targertView);
                    }
                })
                .setCurrentPostion(i)//当前图片索引
                              //设置滑动回调，需要改变绑定关系
                .setOnPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChange(int position) {
                        pv.onBindChanged((ImageView) rv.getChildAt(position),imgs.get(position).getWidth(),imgs.get(position).getHeight());
                    }
                })
                .start();
    }

    ...



















