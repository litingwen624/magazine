package com.rabbit.magazine.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import com.rabbit.magazine.kernel.Group;
import com.rabbit.magazine.kernel.Picture;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class PictureView extends ImageView implements OnClickListener{

	private boolean isLoad=false;
	
	private Context context;
	
	private LayoutParams params;
	
	public boolean isLoad() {
		return isLoad;
	}

	public void setLoad(boolean isLoad) {
		this.isLoad = isLoad;
	}

	private Picture picture;
	public Picture getPicture() {
		return picture;
	}

	private Group group;
	public Group getGroup() {
		return group;
	}
	

	public PictureView(Context context,Group group,Picture picture){
		super(context);
		this.picture = picture;
		this.group=group;
		this.context=context;
		String frame=picture.getFrame();
		if(frame==null){
			frame=group.getFrame();
		}
		int[] frames=FrameUtil.frame2int(frame);
		frames=FrameUtil.autoAdjust(frames,context);
		LayoutParams params=new LayoutParams(frames[2],frames[3]);
		params.setMargins(frames[0], frames[1], 0, 0);
		this.params=params;
		setLayoutParams(params);
		String zoomable=picture.getZoomable();
		if(zoomable!=null&&"true".equals(zoomable.toLowerCase())){
			setOnClickListener(this);
		}
		setTag(true);
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		isLoad=true;
	}

	@Override
	public void onClick(View v) {
		if((Boolean)getTag()){
			LayoutParams params=new LayoutParams(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels);
			params.setMargins(0, 0, 0, 0);
			setLayoutParams(params);
			bringToFront();
			setTag(false);
		}else{
			setLayoutParams(this.params);
			setTag(true);
		}
	}
}
