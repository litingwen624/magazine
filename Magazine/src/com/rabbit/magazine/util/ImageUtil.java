package com.rabbit.magazine.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.rabbit.magazine.kernel.Group;
import com.rabbit.magazine.kernel.Picture;
import com.rabbit.magazine.view.FirstGroupView;
import com.rabbit.magazine.view.FrameUtil;
import com.rabbit.magazine.view.GroupView2;
import com.rabbit.magazine.view.HorizontalGroupView;
import com.rabbit.magazine.view.LayerView;
import com.rabbit.magazine.view.PageView2;
import com.rabbit.magazine.view.PictureView;
import com.rabbit.magazine.view.PopupWindowPictureView;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class ImageUtil {

	/**
	 * 回收资源
	 * 
	 * @param img
	 */
	public static void recycle(ImageView img) {
		BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
		img.setImageBitmap(null);
		if (img instanceof PictureView) {
			((PictureView) img).setLoad(false);
		} else if (img instanceof LayerView) {
			((LayerView) img).setLoad(false);
		} else if (img instanceof PopupWindowPictureView) {
			((PopupWindowPictureView) img).setLoad(false);
		}
		if (drawable != null) {
			Bitmap bmp = drawable.getBitmap();
			drawable = null;
			if (bmp != null && !bmp.isRecycled()) {
				bmp.recycle();
				bmp = null;
				System.gc();
			}
		}
	}

	/**
	 * 加载图片资源
	 * 
	 * @param imgPath
	 * @return
	 */
	public static Bitmap loadImage(String imgPath) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;//图形的参数应该由两个字节来表示，应该是一种16位的位图
		opt.inPurgeable = true;//设为True的话表示使用BitmapFactory创建的Bitmap,inPurgeable为false时表示创建的Bitmap的Pixel内存空间不能被回收
		opt.inInputShareable = true;//是否深拷贝
		// 获取资源图片
		imgPath=imgPath.replace("\n", "");
		imgPath=imgPath.trim();
		InputStream is=null;
		try {
			is = new FileInputStream(imgPath);
		} catch (FileNotFoundException e) {
			Log.e("ImageUtil", "文件："+imgPath+"  找不到");
			return null;
		}
		Bitmap bitmap=BitmapFactory.decodeStream(is, null, opt);//直接调用 JNI >> nativeDecodeAsset（） 来完成decode，无需再使用java层的createBitmap，从而节省了java层的空间.
		try {
			if(is!=null){
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Bitmap bitmap=BitmapFactory.decodeFile(imgPath,opt);
		return bitmap;
	}

	/**
	 * 释放该页的图片资源
	 * 
	 * @param pageView
	 */
	public static void releasePageViewImg(PageView2 pageView) {
		FrameLayout frameLayout = pageView.getFrameLayout();
		int childCount = frameLayout.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = frameLayout.getChildAt(i);
			if (childView instanceof FirstGroupView) {
				((FirstGroupView) childView).releaseGroupViewImg();
			} else if (childView instanceof GroupView2) {
				((GroupView2) childView).releaseGroupViewImg();
			}else if(childView instanceof HorizontalGroupView){
				((HorizontalGroupView) childView).releaseGroupViewImg();
			}
		}
	}

	/**
	 * 加载该页的图片资源
	 * 
	 * @param sv
	 */
	public static void loadPageViewImg(PageView2 pageView) {
		FrameLayout frameLayout = pageView.getFrameLayout();
		int childCount = frameLayout.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = frameLayout.getChildAt(i);
			if (childView instanceof FirstGroupView) {
				FirstGroupView firstGroupView = (FirstGroupView) childView;
				firstGroupView.loadGroupViewImg();
			} else if (childView instanceof GroupView2) {
				GroupView2 groupView = (GroupView2) childView;
				Group group = groupView.getGroup();
				int[] frames = FrameUtil.frame2int(group.getFrame());
//				if (frames[1] < 768 * 2) {// 每页只加载坐标小于768*2的图片，如果第一张图片高度高于768*2，那么只加载这一张图片以及上层的图片
				groupView.loadGroupViewImg();
//				}
			}else if (childView instanceof HorizontalGroupView) {
				HorizontalGroupView groupView = (HorizontalGroupView) childView;
				Group group = groupView.getGroup();
				int[] frames = FrameUtil.frame2int(group.getFrame());
//				if (frames[1] < 768 * 2) {// 每页只加载坐标小于768*2的图片，如果第一张图片高度高于768*2，那么只加载这一张图片以及上层的图片
				groupView.loadGroupViewImg();
//				}
			}
		}
	}
	
	/**
	 * 生成缩略图
	 * @param imgPath
	 * @param height
	 * @return
	 */
	public static Bitmap createThumbnail(String imgPath,int height){
		InputStream is=null;
		Bitmap bitmap=null;
		try {
			is = new FileInputStream(imgPath);
			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inJustDecodeBounds=true;
			BitmapFactory.decodeStream(is, null, options);
			//Bitmap bitmap=BitmapFactory.decodeFile(imgPath,options);
			options.inJustDecodeBounds=false;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			int be=(int)(options.outHeight/(float)height);
			if(be<=0){
				be=1;
			}
			options.inSampleSize=be;
			is.close();
			is = new FileInputStream(imgPath);
			bitmap=BitmapFactory.decodeStream(is, null, options);
			is.close();
			//bitmap=BitmapFactory.decodeFile(imgPath, options);
		}catch (FileNotFoundException e) {
			Log.e("ImageUtil", "文件："+imgPath+"  找不到");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("ImageUtil", e.getMessage());
			e.printStackTrace();
		}
		return bitmap;
	}

	public static void writeBitmap(Bitmap drawingCache, File bitmap) {
		try {
			bitmap.createNewFile();
			FileOutputStream ot = new FileOutputStream(bitmap);
			drawingCache.compress(CompressFormat.PNG, 50, ot);
			ot.flush();
			ot.close();
		} catch (Exception e) {
			
		}
	}
}
