package com.rabbit.magazine;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

public abstract class BaseActivity extends Activity {

	ArrayList<SoftReference<Bitmap>> bitmaps = new ArrayList<SoftReference<Bitmap>>();
	
	ArrayList<OnFullScreenListener> listeners=new ArrayList<OnFullScreenListener>();
	
	private ArrayList<OnVideoControllerListener> vControllers=new ArrayList<OnVideoControllerListener>();
	
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		recycle();
		super.onDestroy();
	}

	public void addBitMap(Bitmap bitmap) {
		bitmaps.add(new SoftReference<Bitmap>(bitmap));
	}
	
	
	public void addListener(OnFullScreenListener listener){
		listeners.add(listener);
	}
	
	public void addVideoController(OnVideoControllerListener listener){
		getvControllers().add(listener);
	}

	public void recycle() {
		for (Iterator iterator = bitmaps.iterator(); iterator.hasNext();) {
			SoftReference<Bitmap> reference = (SoftReference<Bitmap>) iterator.next();
			Bitmap bitmap2 = reference.get();
			if (bitmap2 != null&&!bitmap2.isRecycled()) {
				bitmap2.recycle();
				System.gc();
			}
		}
	}

	public void setvControllers(ArrayList<OnVideoControllerListener> vControllers) {
		this.vControllers = vControllers;
	}

	public ArrayList<OnVideoControllerListener> getvControllers() {
		return vControllers;
	}

}
