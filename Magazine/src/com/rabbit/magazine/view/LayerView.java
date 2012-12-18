package com.rabbit.magazine.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.kernel.Hot;
import com.rabbit.magazine.kernel.Layer;
import com.rabbit.magazine.kernel.Picture;
import com.rabbit.magazine.util.ImageUtil;
import com.rabbit.magazine.util.StringUtil;

public class LayerView extends ImageView{

	private Layer layer;
	
	private boolean isLoad=false;
	
	private HotView hotView;
	
	private int frames[];
	
	public boolean isLoad() {
		return isLoad;
	}

	
	public void setLoad(boolean isLoad) {
		this.isLoad = isLoad;
	}

	public Layer getLayer() {
		return layer;
	}

	public LayerView(Context context, Layer layer) {
		super(context);
		this.layer=layer;
		int[] frames =FrameUtil.frame2int(layer.getFrame());
		// 自动转换坐标，需要测试
		frames=FrameUtil.autoAdjust(frames,context);
		this.frames=frames;
		LayoutParams params = new LayoutParams(frames[2], frames[3]);
		params.setMargins(frames[0], frames[1], 0, 0);
		setLayoutParams(params);
		setTag(layer.getName()+"#"+layer.getTag());
		String visiable=layer.getVisible();
		if(visiable!=null){
			if("true".equals(visiable.toLowerCase())){
				setVisibility(View.VISIBLE);
			}else{
				setVisibility(View.INVISIBLE);
			}
		}
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		isLoad=true;
		setVisibility(View.VISIBLE);
	}

	public void show(){
		ViewParent parent=getParent();
		if(parent instanceof FirstGroupView){
			FirstGroupView firstGroupView=(FirstGroupView) parent;
			showImg(firstGroupView);
		}else if(parent instanceof GroupView2){
			GroupView2 groupView=(GroupView2) parent;
			FrameLayout frameLayout=groupView.getFrameLayout();
			showImg(frameLayout);
		}
	}

	private void showImg(FrameLayout framelayout){
		if(isLoad){
			return;
		}
		int childCount=framelayout.getChildCount();
		String thisTag=getTag().toString();
		String thisName=thisTag.split("#")[0];
		LayerView layerView_=null;
		for(int i=0;i<childCount;i++){
			View view=framelayout.getChildAt(i);
			if(view instanceof LayerView){
				LayerView layerView=(LayerView)view;
				Layer layer=layerView.getLayer();
				String effect=layer.getEffect();
				String tag=layerView.getTag().toString();
				String name=tag.split("#")[0];
				if(thisTag.equals(tag)){
					Picture picture=layer.getPicture();
					String resource="";
					if(picture!=null){
						resource=picture.getResource();
					}
					String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, resource);
					Bitmap bitmap=ImageUtil.loadImage(imgPath);
					layerView.setImageBitmap(bitmap);
					HotView hotView=layerView.getHotView();
					if(hotView!=null){
						hotView.setVisibility(View.VISIBLE);
					}
					if(effect!=null){
						if("FADE".equals(effect)){
							Animation alpha = new AlphaAnimation(0.0f, 1.0f);
							alpha.setDuration(500);
							layerView.setAnimation(alpha);
							alpha.startNow();
						}else if("SLIDE_LEFT".equals(effect)){
							startTranslateAnimation2(layerView,-frames[2], 0, 0, 0);
						}else if("SLIDE_RIGHT".equals(effect)){
							startTranslateAnimation2(layerView,frames[2], 0, 0, 0);
						}else if("SLIDE_TOP".equals(effect)){
							startTranslateAnimation2(layerView,0, 0, frames[3], 0);
						}else if("SLIDE_BOTTOM".equals(effect)){
							startTranslateAnimation2(layerView,0, 0, -frames[3], 0);
						}else if("FLIP_LEFT".equals(effect)){
							startRotateAnimation2(layerView,0,-90);
						}else if("FLIP_TOP".equals(effect)){
							startRotateAnimation2(layerView,0, 270);
						}else if("FLIP_RIGHT".equals(effect)){
							startRotateAnimation2(layerView,0,90);
						}else if("FLIP_BOTTOM".equals(effect)){
							startRotateAnimation2(layerView,0,180);
						}
					}
				}else if(thisName.equals(name)){
					if("SLIDE_LEFT".equals(effect)){
						layerView_=layerView;
						startTranslateAnimation(layerView,0, -frames[2], 0, 0);
					}else if("SLIDE_RIGHT".equals(effect)){
						startTranslateAnimation(layerView,0, frames[2], 0, 0);
					}else if("SLIDE_TOP".equals(effect)){
						startTranslateAnimation(layerView,0, 0, 0, frames[3]);
					}else if("SLIDE_BOTTOM".equals(effect)){
						startTranslateAnimation(layerView,0, 0, 0, -frames[3]);
					}else if("FLIP_LEFT".equals(effect)){
						startRotateAnimation(layerView,0, -90);
					}else if("FLIP_TOP".equals(effect)){
						startRotateAnimation(layerView,0,270);
					}else if("FLIP_RIGHT".equals(effect)){
						startRotateAnimation(layerView,0,90);
					}else if("FLIP_BOTTOM".equals(effect)){
						startRotateAnimation(layerView,0, 180);
					}else{
						ImageUtil.recycle(layerView);
						HotView hotView=layerView.getHotView();
						if(hotView!=null){
							hotView.setVisibility(View.INVISIBLE);
						}
					}
				}
			}
		}
		for(int i=0;i<childCount;i++){
			View view=framelayout.getChildAt(i);
			if(view!=this&&view!=layerView_){
				view.bringToFront();
			}else{
				System.out.println();
			}
		}
	}

	//动画End之后释放图片
	private void startTranslateAnimation(LayerView layerView,int X,int toX,int Y,int toY){
		Animation translate=new TranslateAnimation(X, toX, Y, toY);
		translate.setDuration(500);
		LayerAnimationListener listener=new LayerAnimationListener(layerView);
		translate.setAnimationListener(listener);
		layerView.setAnimation(translate);
		translate.startNow();
	}
	
	//不释放
	private void startTranslateAnimation2(LayerView layerView,int X,int toX,int Y,int toY){
		Animation translate=new TranslateAnimation(X, toX, Y, toY);
		translate.setDuration(500);
		layerView.setAnimation(translate);
		translate.startNow();
	}
	
	private void startRotateAnimation(LayerView layerView,int from,int to){
		RotateAnimation rotate=new RotateAnimation(from, to,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotate.setDuration(500);
		LayerAnimationListener listener=new LayerAnimationListener(layerView);
		rotate.setAnimationListener(listener);
		layerView.setAnimation(rotate);
		rotate.startNow();
	}
	
	private void startRotateAnimation2(LayerView layerView,int from,int to){
		RotateAnimation rotate=new RotateAnimation(from, to,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotate.setDuration(500);
		layerView.setAnimation(rotate);
		rotate.startNow();
	}
	
	public HotView getHotView() {
		return hotView;
	}


	public void setHotView(HotView hotView) {
		this.hotView = hotView;
	}
}
