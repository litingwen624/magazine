package com.rabbit.magazine.view;

import java.util.List;
import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.kernel.Animation;
import com.rabbit.magazine.kernel.Group;
import com.rabbit.magazine.kernel.Hot;
import com.rabbit.magazine.kernel.Layer;
import com.rabbit.magazine.kernel.Picture;
import com.rabbit.magazine.kernel.PictureSet;
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
import android.widget.ImageView.ScaleType;

public class HorizontalGroupView extends HorizontalScrollView  implements android.view.View.OnTouchListener{
	
	private Context context;
	
	private Group group;
	
	private FlipperPageView2 flipperPageView;
	
	private FrameLayout frameLayout;
	
	private PageView2 pageView;

	public FrameLayout getFrameLayout() {
		return frameLayout;
	}
	
	public HorizontalGroupView(Context context,Group group,FlipperPageView2 flipperPageView,PageView2 pageView) {
		super(context);
		this.context=context;
		this.group=group;
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
		flipperPageView.gethGroupViewList().add(this);
		setOnTouchListener(this);
		setHorizontalFadingEdgeEnabled(false);
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
				CustVideoView pictureSetView=new CustVideoView(context, ps);
				frameLayout.addView(pictureSetView);
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
				int[] frames=FrameUtil.frame2int(pic.getFrame());
				frames=FrameUtil.autoAdjust(frames,context);
				LayoutParams params=new LayoutParams(frames[2], frames[3]);
				params.setMargins(frames[0], frames[1], 0, 0);
				pictureView=new PictureView(context, group,pic);
				pictureView.setLayoutParams(params);
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
				HotView hotView=(HotView)childView;
				ImageUtil.recycle(hotView);
			}else if(childView instanceof LayerView){
				LayerView layerView=(LayerView)childView;
				ImageUtil.recycle(layerView);
			}else if(childView instanceof AnimationView2){
				AnimationView2 animationView=(AnimationView2)childView;
				animationView.release();
			}else if(childView instanceof CustVideoView){
				CustVideoView videoView=(CustVideoView)childView;
			}else if(childView instanceof HorizontalGroupView){
				HorizontalGroupView groupView=(HorizontalGroupView)childView;
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
				HorizontalGroupView groupView=(HorizontalGroupView)childView;
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
	public boolean onTouch(View view, MotionEvent event) {
		view.getParent().requestDisallowInterceptTouchEvent(true);
		return false;
	}
}
