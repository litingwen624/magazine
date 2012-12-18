package com.rabbit.magazine.view;

import java.util.List;
import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.kernel.Animation;
import com.rabbit.magazine.kernel.BasicView;
import com.rabbit.magazine.kernel.Group;
import com.rabbit.magazine.kernel.Hot;
import com.rabbit.magazine.kernel.Layer;
import com.rabbit.magazine.kernel.Picture;
import com.rabbit.magazine.kernel.PictureSet;
import com.rabbit.magazine.kernel.Rotater;
import com.rabbit.magazine.kernel.Shutter;
import com.rabbit.magazine.kernel.Slider;
import com.rabbit.magazine.kernel.Video;
import com.rabbit.magazine.util.ImageUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.ScrollView;

public class GroupView2 extends ScrollView implements android.view.View.OnTouchListener{

	private Context context;
	
	private Group group;
	
	private FlipperPageView2 flipperPageView;
	
	private FrameLayout frameLayout;
	
	private PageView2 pageView;
	
	private float downXValue;
	private float downYValue;
	private int distanceY=0;
	private String paged;
	private int UNIT=768;
	
	
	public FrameLayout getFrameLayout() {
		return frameLayout;
	}

	public GroupView2(Context context,Group group,FlipperPageView2 flipperPageView,PageView2 pageView) {
		super(context);
		this.context=context;
		this.group=group;
		this.paged=group.getPaged();
		this.pageView=pageView;
		this.flipperPageView=flipperPageView;
		LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		this.frameLayout=new FrameLayout(context);
		this.frameLayout.setLayoutParams(params);
		addView(this.frameLayout);
		int[] frames=FrameUtil.frame2int(group.getFrame());
		// 自动转换坐标，需要测试
		frames=FrameUtil.autoAdjust(frames,context);
		params=new LayoutParams(frames[2], frames[3]);
		params.setMargins(frames[0], frames[1], 0, 0);
		setLayoutParams(params);
		setFocusable(false);
		buildView(group);
		setOnTouchListener(this);
		UNIT=frames[3];
		setVerticalFadingEdgeEnabled(false);
	}

	/**
	 * 构建Group中的View，子Group不构建
	 * @param group
	 */
	private void buildView(Group group){
		//PictureView
		List<Picture> pics = group.getPictures();
		buildPictureView(pics, group);
		
		//LayerView
		List<Layer> layers=group.getLayers();
		buildLayerView(layers,group);
		
		//HotView
		List<Hot> hots=group.getHots();
		buildHotView(hots,group);
		
		//PictureSetView
		List<PictureSet> pictureSets=group.getPictureSets();
		buildPictureSetView(pictureSets);
		
		//AnimationView
		List<Animation> animations = group.getAnimations();
		buildAnimationView(animations);
		
		
		//VideoView
		List<Video> videos = group.getVideos();
		buildVideos(videos);
		
		// RotaterView
		List<Rotater> rotaters = group.getRotaters();
		buildRotaters(rotaters);
		
		List<Slider> sliders=group.getSliders();
		buildSliderView(sliders);
		
		List<Shutter> shutters=group.getShutters();
		buildShutterView(shutters);
		
	}
	
	private void buildShutterView(List<Shutter> shutters) {
		if (shutters != null && shutters.size() != 0) {
			for (Shutter shutter : shutters) {
				ShutterView shutterView = new ShutterView(context, shutter);
				frameLayout.addView(shutterView);
			}
		}
	}
	
	private void buildSliderView(List<Slider> sliders) {
		if (sliders != null && sliders.size() != 0) {
			for (Slider slider : sliders) {
				SliderView sliderView = new SliderView(context, slider);
				frameLayout.addView(sliderView);
				pageView.getGroupViewList().add(sliderView);
			}
		}
	}
	
	private void buildRotaters(List<Rotater> rotaters) {
		if (rotaters != null && rotaters.size() != 0) {
			for (Rotater rotater : rotaters) {
				RotaterView rotaterView = new RotaterView(context,group,rotater);
				frameLayout.addView(rotaterView);
			}
		}
	}
	private void buildAnimationView(List<Animation> animations) {
		if (animations != null && animations.size() != 0) {
			for (Animation a : animations) {
				AnimationView2 animationView = new AnimationView2(context, a);
				frameLayout.addView(animationView);
			}
		}
	}
	
	private void buildVideos(List<Video> videos) {
		if (videos != null && videos.size() != 0) {
			for(Video ps:videos){
				final CustVideoView videoview=new CustVideoView(context, ps);
				MediaController mediaController = new MediaController(context);
				mediaController.setEnabled(true);
				int[] frames = FrameUtil.frame2int(ps.getFrame());
				frames=FrameUtil.autoAdjust(frames, context);
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(frames[2], frames[3]);	
				params.setMargins(frames[0], frames[1], 0, 0);
				//mediaController.setPadding(frames[0], frames[1], 0, 0);
//				mediaController.setLayoutParams(params);
				videoview.setMediaController(mediaController);
				String preview = ps.getPreview();
				if(preview!=null&&!preview.equals("")){
					ImageView image=new ImageView(context);
					String imgPath = AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, preview);
					Bitmap bitmap = ImageUtil.loadImage(imgPath);
					image.setImageBitmap(bitmap);
					image.setScaleType(ScaleType.FIT_XY);
					image.setLayoutParams(params);
					image.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							videoview.start();
						}
						
					});
					frameLayout.addView(image);
				}
				
				
				frameLayout.addView(videoview);
			}
		}
	}

	/**
	 * 构建PictureSetView
	 * @param pictureSets
	 */
	private void buildPictureSetView(List<PictureSet> pictureSets) {
		if (pictureSets != null && pictureSets.size() != 0) {
			for(PictureSet ps:pictureSets){
				PictureSetView pictureSetView=new PictureSetView(context, ps);
				frameLayout.addView(pictureSetView);
			}
		}
	}

	/**
	 * 构建LayerView
	 * @param layers
	 */
	private void buildLayerView(List<Layer> layers,Group group) {
		if (layers != null && layers.size() != 0) {
			for(Layer layer:layers){
				LayerView layerView=new LayerView(context, layer);
				frameLayout.addView(layerView);
				Hot hot=layer.getHot();
				if(hot!=null){
					if(hot.getFrame()==null){
						hot.setFrame(layer.getFrame());
					}
					HotView hotView=new HotView(context, hot, group, flipperPageView);
					String visible=layer.getVisible();
					if(!"true".equals(visible.toLowerCase())){
						hotView.setVisibility(View.INVISIBLE);
					}
					frameLayout.addView(hotView);
					layerView.setHotView(hotView);
				}
			}
		}
	}

	/**
	 * 构建HotView
	 * @param hots
	 */
	private void buildHotView(List<Hot> hots,Group group) {
		if (hots != null && hots.size() != 0) {
			for(Hot hot:hots){
				HotView hotview=new HotView(context,hot,group,flipperPageView);
				frameLayout.addView(hotview);
			}
		}
	}

	/**
	 * 构建PictureView
	 * @param pics
	 * @param group
	 */
	private void buildPictureView(List<Picture> pics,Group group){
		if (pics != null && pics.size() != 0) {
			for (Picture pic : pics) {
				PictureView pictureView;
//				int[] frames=FrameUtil.frame2int(pic.getFrame());
//				LayoutParams params=new LayoutParams(frames[2], frames[3]);
//				params.setMargins(frames[0], frames[1], 0, 0);
				pictureView=new PictureView(context, group,pic);
//				pictureView.setLayoutParams(params);
				frameLayout.addView(pictureView);
			}
		}
	}
	
	/**
	 * 释放GroupView下控件的图片资源
	 */
	public void releaseGroupViewImg(){
		int childCount=frameLayout.getChildCount();
		for(int i=0;i<childCount;i++){
			View childView=frameLayout.getChildAt(i);
			if(childView instanceof PictureView){
				PictureView pictureView=(PictureView)childView;
				ImageUtil.recycle(pictureView);
			}else if(childView instanceof PictureSetView){
				PictureSetView pictureSetView=(PictureSetView)childView;
				ImageUtil.recycle(pictureSetView);
			}else if(childView instanceof HotView){
				/*HotView hotView=(HotView)childView;
				ImageUtil.recycle(hotView);*/
			}else if(childView instanceof LayerView){
				LayerView layerView=(LayerView)childView;
				ImageUtil.recycle(layerView);
			}else if(childView instanceof AnimationView2){
				AnimationView2 animationView=(AnimationView2)childView;
				animationView.release();
			}else if(childView instanceof CustVideoView){
				CustVideoView videoView=(CustVideoView)childView;
			}else if(childView instanceof GroupView2){
				GroupView2 groupView=(GroupView2)childView;
				groupView.releaseGroupViewImg();
			}else if(childView instanceof SliderView){
				SliderView sliderView=(SliderView)childView;
				sliderView.release();
			}else if(childView instanceof ShutterView){
				ShutterView shutterView=(ShutterView)childView;
				shutterView.release();
			}
		}
	}

	/**
	 *加载GroupView下控件的图片资源
	 */
	public void loadGroupViewImg(){
		int childCount=frameLayout.getChildCount();
		for(int i=0;i<childCount;i++){
			View childView=frameLayout.getChildAt(i);
			if(childView instanceof PictureView){
				PictureView pictureView=(PictureView)childView;
				String resource=pictureView.getPicture().getResource();
				if(resource!=null){
					String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, resource);
					Bitmap bitmap=ImageUtil.loadImage(imgPath);
					pictureView.setImageBitmap(bitmap);
					pictureView.setScaleType(ScaleType.FIT_XY);
				}
			}else if(childView instanceof GroupView2){
				GroupView2 groupView=(GroupView2)childView;
				groupView.loadGroupViewImg();
			}else if(childView instanceof PictureSetView){
				
			}else if(childView instanceof HotView){
				HotView view=(HotView) childView;
				view.setScaleType(ScaleType.FIT_XY);
			}else if(childView instanceof LayerView){
				LayerView layerView=(LayerView)childView;
				if(!layerView.isLoad()){
					Layer layer=layerView.getLayer();
					String visible=layer.getVisible();
					if("TRUE".equals(visible)){
						Picture picture=layer.getPicture();
						if(picture!=null){
							String resource=picture.getResource();
							if(resource!=null){
								String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, resource);
								Bitmap bitmap=ImageUtil.loadImage(imgPath);
								layerView.setImageBitmap(bitmap);
								layerView.setScaleType(ScaleType.FIT_XY);
							}
						}
					}
				}
			}else if (childView instanceof RotaterView) {
				RotaterView rotaterView = (RotaterView) childView;
				rotaterView.loadFirstImage();
			}else if(childView instanceof AnimationView2){
				AnimationView2 animationView=(AnimationView2)childView;
				animationView.initial();
				animationView.start();
			}else if(childView instanceof CustVideoView){
				CustVideoView video=(CustVideoView) childView;
				if (video.getVideo().getAutomatic().equalsIgnoreCase("true")) {
					video.start();
				}
			}else if(childView instanceof SliderView){
				SliderView sliderView=(SliderView)childView;
				sliderView.loadResource();
			}else if(childView instanceof ShutterView){
				ShutterView shutterView=(ShutterView)childView;
				shutterView.loadResource();
			}
		}
	}
	
	public Group getGroup() {
		return group;
	}

	@Override
	public boolean onTouch(final View v, MotionEvent event) {
		// 消除ScrollView嵌套的滑动事件冲突
		v.getParent().requestDisallowInterceptTouchEvent(true);
		int action=event.getAction();
		int curScrollY = v.getScrollY();
		int curScrollX = v.getScrollX();
		Float currentY = event.getY();
		Float currentX=event.getX();
		switch(action){
			case MotionEvent.ACTION_DOWN:
				downXValue =event.getX();
				downYValue = event.getY();
				break;
			case MotionEvent.ACTION_UP:
				// 垂直方向的时候可以左右翻页或者上下滚动
				String orientation = group.getOrientation();
				if(orientation.equals(BasicView.VERTICAL)){
						if ((currentX-downXValue) <-120){ 
							int curItem=flipperPageView.getCurrentItem();
							flipperPageView.setCurrentItem(curItem+1, true);
			            }    
						else if ((currentX-downXValue) >120){   
			            	int curItem=flipperPageView.getCurrentItem();
							flipperPageView.setCurrentItem(curItem-1, true);
			            }   
						else if ((downYValue - currentY) > 5) {// 向下滑动
							if(paged!=null&&"true".equals(paged.toLowerCase())){
								distanceY=distanceY+UNIT;
								int height=getFrameLayout().getHeight();
								if((distanceY)<height){
									v.post(new Runnable(){
										@Override
										public void run() {
											((GroupView2)v).smoothScrollTo(0, distanceY);
										}
									});
								}else{
									distanceY=distanceY-UNIT;
								}
							}
							
						}
						else if ((currentY - downYValue) > 5) {// 向上滑动
							if(paged!=null&&"true".equals(paged.toLowerCase())){
								distanceY=distanceY-UNIT;
								if(distanceY>=0){
									v.post(new Runnable(){
										@Override
										public void run() {
											((GroupView2)v).smoothScrollTo(0, distanceY);
										}
									});
								}else{
									distanceY=distanceY+UNIT;
								}
							}
						}
				}				
				break;
		}
		return false;
	}
	
}































