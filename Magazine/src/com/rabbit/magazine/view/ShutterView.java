package com.rabbit.magazine.view;

import java.util.List;
import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.R;
import com.rabbit.magazine.kernel.Picture;
import com.rabbit.magazine.kernel.Shutter;
import com.rabbit.magazine.util.ImageUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

public class ShutterView extends FrameLayout {
	
	private ViewFlipper viewFlipper;
	
	private LinearLayout linear;
	
	private Context context;
	
	private Shutter shutter;

	public ShutterView(Context context,Shutter shutter) {
		super(context);
		this.context=context;
		this.shutter=shutter;
		String frame = shutter.getFrame();
		int[] frames = FrameUtil.frame2int(frame);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(frames[2], frames[3]);
		params.setMargins(frames[0], frames[1],0,0);
		setLayoutParams(params);
		viewFlipper=new ViewFlipper(context);
		addView(viewFlipper);
		linear=new LinearLayout(context);
		linear.setGravity(Gravity.LEFT|Gravity.BOTTOM);
		linear.setOrientation(LinearLayout.HORIZONTAL);
		List<Picture> pictures=shutter.getPictures();
		for(int i=0;i<pictures.size();i++){
			ImageView img=new ImageView(context);
			String resource=pictures.get(i).getResource();
			img.setTag(resource);
			img.setId(i);
			viewFlipper.addView(img);
			Button btn=new Button(context);
			btn.setText(String.valueOf(i+1));
			btn.setTag(i);
			android.widget.LinearLayout.LayoutParams params_=new android.widget.LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			if(i==0){
				String selected=shutter.getSelected();
				if(selected!=null&&!selected.contains("undefined")){
					String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, selected);
					Bitmap bm=ImageUtil.loadImage(imgPath);
					params_=new android.widget.LinearLayout.LayoutParams(bm.getWidth(), bm.getHeight());
					BitmapDrawable bd= new BitmapDrawable(context.getResources(), bm);  
					btn.setBackgroundDrawable(bd);
				}else{
					btn.setBackgroundColor(Color.RED);
				}
			}else{
				String unselected=shutter.getUnselected();
				if(unselected!=null&&!unselected.contains("undefined")){
					String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, unselected);
					Bitmap bm=ImageUtil.loadImage(imgPath);
					params_=new android.widget.LinearLayout.LayoutParams(bm.getWidth(), bm.getHeight());
					BitmapDrawable bd= new BitmapDrawable(context.getResources(), bm);  
					btn.setBackgroundDrawable(bd);
				}else{
					btn.setBackgroundColor(Color.WHITE);
				}
			}
			params_.setMargins(0, 0, 10, 0);
			btn.setLayoutParams(params_);
			linear.addView(btn);
		}
		addView(linear);
		setFocusable(false);
		viewFlipper.setFlipInterval(shutter.getInterval()*1000);
	}
	
	public void loadResource(){
		int count=viewFlipper.getChildCount();
		for(int i=0;i<count;i++){
			View view=viewFlipper.getChildAt(i);
			if(view instanceof ImageView){
				ImageView img=(ImageView)view;
				String resource=img.getTag().toString();
				String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, resource);
				Bitmap bitmap=ImageUtil.loadImage(imgPath);
				img.setImageBitmap(bitmap);
			}
			if(view instanceof LinearLayout){
				
			}
		}
		Animation alpha_in=AnimationUtils.loadAnimation(context, R.anim.alpha_in);
		alpha_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				ImageView img=(ImageView) viewFlipper.getCurrentView();
				int id=img.getId();
				int count=linear.getChildCount();
				for(int i=0;i<count;i++){
					Button btn=(Button) linear.getChildAt(i);
					if(i!=id){
						String unselected=shutter.getUnselected();
						if(unselected!=null&&!unselected.contains("undefined")){
							String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, unselected);
							Bitmap bm=ImageUtil.loadImage(imgPath);
							BitmapDrawable bd= new BitmapDrawable(context.getResources(), bm);  
							btn.setBackgroundDrawable(bd);
						}else{
							btn.setBackgroundColor(Color.WHITE);
						}
					}else{
						String selected=shutter.getSelected();
						if(selected!=null&&!selected.contains("undefined")){
							String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, selected);
							Bitmap bm=ImageUtil.loadImage(imgPath);
							BitmapDrawable bd= new BitmapDrawable(context.getResources(), bm);  
							btn.setBackgroundDrawable(bd);
						}else{
							btn.setBackgroundColor(Color.RED);
						}
					}
				}
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				
			}
		});
		viewFlipper.setInAnimation(alpha_in);
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha_out));
		viewFlipper.setAutoStart(true);
		viewFlipper.startFlipping();
	}

	public ViewFlipper getViewFlipper() {
		return viewFlipper;
	}
	
	public void release(){
		int count=viewFlipper.getChildCount();
		for(int i=0;i<count;i++){
			View view=viewFlipper.getChildAt(i);
			if(view instanceof ImageView){
				ImageUtil.recycle((ImageView)view);
			}
			if(view instanceof LinearLayout){
				
			}
		}
	}
}
