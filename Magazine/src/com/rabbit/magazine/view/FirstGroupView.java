package com.rabbit.magazine.view;

import java.util.List;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.MagazineActivity;
import com.rabbit.magazine.kernel.Animation;
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
import com.rabbit.magazine.util.StringUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView.ScaleType;
import android.widget.MediaController;

/**
 * 最低层的GroupView
 * 
 * @author cyqin
 * 
 */
public class FirstGroupView extends FrameLayout {

	private Context context;

	private FlipperPageView2 flipperPageView;

	private Group group;
	
	private PageView2 pageView;

	public FirstGroupView(Context context, Group group, FlipperPageView2 flipperPageView,PageView2 pageView) {
		super(context);
		this.context = context;
		this.flipperPageView = flipperPageView;
		this.pageView=pageView;
		this.group = group;
//		int[] frames=FrameUtil.frame2int(group.getFrame());
//		// 自动转换坐标，需要测试
//		frames=FrameUtil.autoAdjust(frames,context);
//		LayoutParams params = new LayoutParams(frames[2], frames[3]);
//		params.setMargins(frames[0], frames[1], 0, 0);
//		setLayoutParams(params);
		// PictureView
		List<Picture> pics = group.getPictures();
		buildPictureView(pics, group);

		// LayerView
		List<Layer> layers = group.getLayers();
		buildLayerView(layers,group);

		// HotView
		List<Hot> hots = group.getHots();
		buildHotView(hots, group);

		// PictureSetView
		List<PictureSet> pictureSets = group.getPictureSets();
		buildPictureSetView(pictureSets);
		
		// AnimationView
		List<Animation> animations = group.getAnimations();
		buildAnimationView(animations);
		
		// VideoView
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
				addView(shutterView);
			}
		}
	}

	private void buildSliderView(List<Slider> sliders) {
		if (sliders != null && sliders.size() != 0) {
			for (Slider slider : sliders) {
				SliderView sliderView = new SliderView(context, slider);
				addView(sliderView);
				pageView.getGroupViewList().add(sliderView);
			}
		}
	}

	private void buildRotaters(List<Rotater> rotaters) {
		if (rotaters != null && rotaters.size() != 0) {
			for (Rotater rotater : rotaters) {
				RotaterView rotaterView = new RotaterView(context,group,rotater);
				addView(rotaterView);
			}
		}
	}

	private void buildAnimationView(List<Animation> animations) {
		if (animations != null && animations.size() != 0) {
			for (Animation a : animations) {
				AnimationView2 animationView = new AnimationView2(context, a);
				addView(animationView);
			}
		}
	}

	private void buildVideos(List<Video> videos) {
		if (videos != null && videos.size() != 0) {
			for (Video ps : videos) {
				final CustVideoView videoview = new CustVideoView(context, ps);
				final MediaController mediaController = new MediaController(context);
				mediaController.setEnabled(true);
				int[] frames = FrameUtil.frame2int(ps.getFrame());
				frames=FrameUtil.autoAdjust(frames, context);
				mediaController.setAnchorView(videoview);
				//mediaController.setPadding(0, 0,frames[2], frames[3]);
				videoview.setMediaController(mediaController);
				String preview = ps.getPreview();
				addView(videoview);
				if(StringUtil.isNotNull(preview)){
					FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(frames[2], frames[3]);	
					params.setMargins(frames[0], frames[1], 0, 0);
					ImageView image=new ImageView(context);
					String imgPath = AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, preview);
					Bitmap bitmap = ImageUtil.loadImage(imgPath);
					image.setImageBitmap(bitmap);
					image.setScaleType(ScaleType.FIT_XY);
					image.setLayoutParams(params);
					OnClickListener click = new OnClickListener(){

						@Override
						public void onClick(View v) {
							videoview.start();
						}
						
					};
					image.setOnClickListener(click);
					addView(image);
					videoview.getFloatLayer().add(image);
					String playIcon = ps.getPlayIcon();
					if(StringUtil.isNotNull(playIcon)){
						ImageView iconImg=new ImageView(context);
						
						String iconpath = AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, playIcon);
						Bitmap bitmap1 = ImageUtil.loadImage(iconpath);
						int width = bitmap1.getWidth();
						int height = bitmap1.getHeight();
						FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(width, height);	
						params1.setMargins(frames[0]+frames[2]/2, frames[1]+frames[3]/2, 0, 0);
						iconImg.setImageBitmap(bitmap1);
						iconImg.setScaleType(ScaleType.CENTER);
						iconImg.setOnClickListener(click);
						addView(iconImg,params1);
						videoview.getFloatLayer().add(iconImg);
					}
				}
				
				videoview.getParent().requestDisallowInterceptTouchEvent(true);
			}
		}
	}

	/**
	 * 构建PictureSetView
	 * 
	 * @param pictureSets
	 */
	private void buildPictureSetView(List<PictureSet> pictureSets) {
		if (pictureSets != null && pictureSets.size() != 0) {
			for (PictureSet ps : pictureSets) {
				PictureSetView pictureSetView = new PictureSetView(context, ps);
				addView(pictureSetView);
			}
		}
	}

	/**
	 * 构建LayerView
	 * 
	 * @param layers
	 */
	private void buildLayerView(List<Layer> layers, Group group) {
		if (layers != null && layers.size() != 0) {
			for (Layer layer : layers) {
				LayerView layerView = new LayerView(context, layer);
				addView(layerView);
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
					addView(hotView);
					layerView.setHotView(hotView);
				}
			}
		}
	}

	/**
	 * 构建HotView
	 * 
	 * @param hots
	 */
	private void buildHotView(List<Hot> hots, Group group) {
		if (hots != null && hots.size() != 0) {
			for (Hot hot : hots) {
				HotView hotview = new HotView(context, hot, group, flipperPageView);
				addView(hotview);
			}
		}
	}

	/**
	 * 构建PictureView
	 * 
	 * @param pics
	 * @param group
	 */
	private void buildPictureView(List<Picture> pics, Group group) {
		if (pics != null && pics.size() != 0) {
			for (Picture pic : pics) {
				PictureView pictureView = new PictureView(context, group, pic);
				addView(pictureView);
				String zoomedImg=pic.getZoomedImage();
				String zoomeIcon=pic.getZoomIcon();
				if(zoomedImg!=null&&zoomeIcon!=null){
					pictureView.setOnClickListener(null);
					Hot zoomHot=new Hot();
					zoomHot.setFrame(pic.getFrame());
					int left=((LayoutParams) pictureView.getLayoutParams()).leftMargin;
					int top=((LayoutParams) pictureView.getLayoutParams()).topMargin;
					int width=((LayoutParams) pictureView.getLayoutParams()).width;
					int height=((LayoutParams) pictureView.getLayoutParams()).height;
					zoomHot.setFrame((left+width-30)+","+(top+height+30)+","+30+","+30);
					zoomHot.setPicture(zoomedImg);
					zoomHot.setAction("zoom");
					HotView zoomInhotview = new HotView(context, zoomHot, group, flipperPageView);
					zoomInhotview.setTag(pictureView);
					LayoutParams params=new LayoutParams(30, 30);
					params.setMargins(left+width-30-2, top+height-30-2, 0, 0);
					zoomInhotview.setLayoutParams(params);
					zoomInhotview.setParams(params);
					zoomInhotview.setScaleType(ScaleType.FIT_XY);
					addView(zoomInhotview);
				}
			}
		}
	}

	/**
	 * 释放GroupView下控件的图片资源
	 */
	public void releaseGroupViewImg() {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = getChildAt(i);
			if (childView instanceof PictureView) {
				PictureView pictureView = (PictureView) childView;
				ImageUtil.recycle(pictureView);
			} else if (childView instanceof PictureSetView) {

			} else if (childView instanceof HotView) {
				/*HotView hotView=(HotView)childView;
				ImageUtil.recycle(hotView);*/
			} else if (childView instanceof LayerView) {
				LayerView layerView = (LayerView) childView;
				ImageUtil.recycle(layerView);
			} else if (childView instanceof AnimationView2) {
				AnimationView2 animationView=(AnimationView2)childView;
				animationView.release();
			} else if (childView instanceof CustVideoView) {

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
	 * 加载GroupView下控件的图片资源
	 */
	public void loadGroupViewImg() {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = getChildAt(i);
			if (childView instanceof PictureView) {
				PictureView pictureView = (PictureView) childView;
				Picture picture = pictureView.getPicture();
				if (picture != null) {
					String frame = picture.getFrame();
					if (frame != null) {
						int[] frames = FrameUtil.frame2int(frame);
						if (frames[1] < 768 * 2) {// 每页只加载坐标小于768*2的图片，如果第一张图片高度高于768*2，那么只加载这一张图片以及上层的图片
							String resource = picture.getResource();
							if (resource != null) {
								String imgPath = AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, resource);
								Bitmap bitmap = ImageUtil.loadImage(imgPath);
								pictureView.setScaleType(ScaleType.FIT_XY);
								pictureView.setImageBitmap(bitmap);
							}
						}
					} else {
						String frame2 = group.getFrame();
						if (frame2 != null) {
							int[] frames = FrameUtil.frame2int(frame2);
							if (frames[1] < 768 * 2) {// 每页只加载坐标小于768*2的图片，如果第一张图片高度高于768*2，那么只加载这一张图片以及上层的图片
								String resource = picture.getResource();
								if (resource != null) {
									String imgPath = AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID,
											resource);
									Bitmap bitmap = ImageUtil.loadImage(imgPath);
									pictureView.setImageBitmap(bitmap);
									pictureView.setScaleType(ScaleType.FIT_XY);
								}
							}
						}
					}
				}
			} else if (childView instanceof PictureSetView) {

			} else if (childView instanceof HotView) {
				HotView view=(HotView) childView;
				view.setScaleType(ScaleType.FIT_XY);
			} else if (childView instanceof LayerView) {
				LayerView layerView = (LayerView) childView;
				Layer layer = layerView.getLayer();
				layerView.setScaleType(ScaleType.FIT_XY);
				String visible = layer.getVisible();
				if(!layerView.isLoad()){
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
			} else if (childView instanceof AnimationView2) {
				AnimationView2 animationView=(AnimationView2)childView;
				animationView.initial();
				animationView.start();
			} else if (childView instanceof CustVideoView) {
//					CustVideoView video = (CustVideoView) childView;
//					// 自动播放
//					if (video.getVideo().getAutomatic().equalsIgnoreCase("true")) {
//						video.start();
//				}
			} else if (childView instanceof RotaterView) {
					RotaterView rotaterView = (RotaterView) childView;
					rotaterView.loadFirstImage();
//				}
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
}
