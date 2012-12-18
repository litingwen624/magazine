package com.rabbit.magazine.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;
import android.widget.ImageView.ScaleType;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.MagazineActivity;
import com.rabbit.magazine.OnFullScreenListener;
import com.rabbit.magazine.OnVideoControllerListener;
import com.rabbit.magazine.kernel.Video;
import com.rabbit.magazine.util.ImageUtil;

public class CustVideoView extends VideoView implements OnClickListener,OnFullScreenListener,OnVideoControllerListener {

	private boolean autoClose=false;

	private Video video;

	private Activity act;

	private String fullscreen;
	
	private boolean load=true;
	
	private Context context;
	
	/**
	 * 基于Vidoe的上层视图
	 */
	private List<View> floatLayer=new ArrayList<View>();

	
	public CustVideoView(final Context context, Video video) {
		super(context);
		this.context=context;
		this.setVideo(video);
		String frame = video.getFrame();
		String closesOnEnd = video.getClosesOnEnd();
		if (closesOnEnd!=null&&closesOnEnd.equalsIgnoreCase("true")) {
			autoClose = true;
		}
		fullscreen = video.getFullscreen();
		FrameLayout.LayoutParams params;
		int[] frames = FrameUtil.frame2int(frame);
		frames=FrameUtil.autoAdjust(frames, context);
		// 全屏模式
		if(fullscreen.equalsIgnoreCase("true")){
			act = (Activity) context;
			Display display = act.getWindowManager().getDefaultDisplay();
			int width = display.getWidth();
			int height=display.getHeight();
			params = new FrameLayout.LayoutParams(width, height);
			params.topMargin=0;
			params.leftMargin=0;
			setMeasuredDimension(width, height);
		}else {
			params = new FrameLayout.LayoutParams(frames[2], frames[3]);	
			params.setMargins(frames[0], frames[1], 0, 0);
		}
		setLayoutParams(params);
		String path = AppConfigUtil.getAppResource(AppConfigUtil.MAGAZINE_ID) + video.getResource();
		setVideoPath(path);
		setVisibility(View.VISIBLE);
		setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				MagazineActivity act=(MagazineActivity) context;
				act.getvControllers().remove(this);
				if (autoClose) {
					setVisibility(View.INVISIBLE);
					showLayers();
				}
			}
		});
		setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		start();
	}
	
	
	@Override
	public void start() {
		setVisibility(View.VISIBLE);
		fadeLayers();
		String fullscreen = getVideo().getFullscreen();
		if("TRUE".equals(fullscreen)){
			MagazineActivity act=(MagazineActivity) context;
			act.addListener(this);
			stopRunningVideo(true);
		}else{
			stopRunningVideo(false);
		}
		MagazineActivity act=(MagazineActivity) context;
		act.addVideoController(this);
		super.start();
	}
	
	private void stopRunningVideo(boolean fade) {
		MagazineActivity act=(MagazineActivity) context;
		ArrayList<OnVideoControllerListener> startlistener = act.getvControllers();
		if(startlistener!=null&&startlistener.size()>0){
			for (Iterator iterator = startlistener.iterator(); iterator.hasNext();) {
				OnVideoControllerListener listener = (OnVideoControllerListener) iterator.next();
				listener.stopVideo(fade);
			}
		}
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 全屏模式
		if(fullscreen.equalsIgnoreCase("true")){
			Display display = act.getWindowManager().getDefaultDisplay();
			int width = display.getWidth();
			int height=display.getHeight();
			setMeasuredDimension(width, height);
		}
		else  {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		// 全屏模式
		String fullscreen = video.getFullscreen();
		if(fullscreen.equalsIgnoreCase("true")){
			Display display = act.getWindowManager().getDefaultDisplay();
			int width = display.getWidth();
			int height=display.getHeight();
			super.onLayout(changed, left, top, width, height);
		}else {
			super.onLayout(changed, left, top, right, bottom);
		}
	}

	public void setVideo(Video video) {
		this.video = video;
	}

	public Video getVideo() {
		return video;
	}

	public void setUnload(boolean unload) {
		this.load = unload;
	}

	public boolean isUnload() {
		return load;
	}

	public void setFloatLayer(List<View> floatLayer) {
		this.floatLayer = floatLayer;
	}

	public List<View> getFloatLayer() {
		return floatLayer;
	}

	public void fadeLayers() {
		for (Iterator iterator = floatLayer.iterator(); iterator.hasNext();) {
			View view = (View) iterator.next();
			view.setVisibility(View.INVISIBLE);
		}
	}
	
	protected void showLayers() {
		for (Iterator iterator = floatLayer.iterator(); iterator.hasNext();) {
			View view = (View) iterator.next();
			view.setVisibility(View.VISIBLE);
		}
	}


	@Override
	public void close() {
		boolean playing = super.isPlaying();
		if(playing){
			super.stopPlayback();
		}
		setVisibility(View.INVISIBLE);
		showLayers();
		// 从全屏退出之后,显示出之前的视频
		MagazineActivity act=(MagazineActivity) context;
		ArrayList<OnVideoControllerListener> startlistener = act.getvControllers();
		if(startlistener!=null&&startlistener.size()>0){
			for (Iterator iterator = startlistener.iterator(); iterator.hasNext();) {
				CustVideoView listener = (CustVideoView) iterator.next();
				listener.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void stopVideo(boolean fade) {
		boolean playing = super.isPlaying();
		if(playing){
			super.stopPlayback();
		}
		if(fade){
			setVisibility(View.INVISIBLE);
		}
	}

}
