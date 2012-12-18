package com.rabbit.magazine.view;

import android.app.PendingIntent.OnFinished;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.kernel.Animation;
import com.rabbit.magazine.util.ImageUtil;

public class AnimationView2 extends ViewFlipper{
	
	private Animation animation;
	
	private Long delay;
	
	private int frameLength;
	
	private boolean isCycling=false;
	
	private String closesOnEnd;
	
	private Context context;
	
	private AlphaAnimation anim;
	
	public Animation getAnimation2(){
		return this.animation;
	}
	
	private Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what==1){
				startFlipping();
				startAnimation(anim);
			}
			if(msg.what==2){
				release();
			}
		};
	};
	
	public AnimationView2(Context context,Animation animation) {
		super(context);
		this.animation=animation;
		this.context=context;
		closesOnEnd=animation.getClosesOnEnd();
		String frame = animation.getFrame();
		int[] frames = FrameUtil.frame2int(frame);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(frames[2], frames[3]);
		params.setMargins(frames[0], frames[1],0,0);
		setLayoutParams(params);
		Float frameLength_=Float.parseFloat(animation.getFrameLength())*1000;
		frameLength=frameLength_.intValue();
		setFocusable(false);
		initial();
		setFlipInterval(frameLength);
		Float delary_=Float.parseFloat(animation.getDelay())*1000;
		delay=delary_.longValue();
		if("true".equals(animation.getCycling().toLowerCase())){
			isCycling=true;
		}
		anim=new AlphaAnimation(1.0f, 1.0f);
		anim.setDuration(0);
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(android.view.animation.Animation animation) {
			}
			@Override
			public void onAnimationRepeat(android.view.animation.Animation animation) {
			}
			@Override
			public void onAnimationEnd(android.view.animation.Animation anim) {
				ImageView img=(ImageView) getCurrentView();
				Integer index=(Integer) img.getTag();
				int count=AnimationView2.this.animation.getResources().length;
				if(index==count-1){
					if(!isCycling){
						stopFlipping();
						if("true".equals(closesOnEnd.toLowerCase())){
							Message msg=new Message();
							msg.what=2;
							handler.sendMessage(msg);
						}
					}
				}
			}
		});
		setOutAnimation(anim);
	}
	
	public void initial(){
		String[] resources=animation.getResources();
		for(int i=0;i<resources.length;i++){
			if(!resources[i].equals("")){
				//resource=resource.replace("\n", "");
				resources[i]="/"+resources[i].trim();
				ImageView img=new ImageView(context);
				String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, resources[i]);
				Bitmap bitmap=ImageUtil.loadImage(imgPath);
				img.setImageBitmap(bitmap);
				img.setTag(i);
				addView(img);
			}
		}
	}
	
	public void start(){
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					Thread.sleep(delay);
					Message msg=new Message();
					msg.what=1;
					handler.sendMessage(msg);
				} catch (InterruptedException e) {
					Log.e("AnimationView2", e.getMessage());
				}
			}
		}).start();
	}
	
	public void stop(){
		stopFlipping();
	}
	
	public void release(){
		int count=getChildCount();
		for(int j=0;j<count;j++){
			ImageView img=(ImageView) getChildAt(j);
			ImageUtil.recycle(img);
			img=null;
		}
		removeAllViews();
		stop();
	}
}
