package com.rabbit.magazine.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.kernel.PictureSet;
import com.rabbit.magazine.util.ImageUtil;

public class PictureSetView extends ImageView implements OnClickListener {
	
	private Context context;
	
	private PictureSet pictureSet;
	
	public PictureSetView(Context context, PictureSet pictureSet) {
		super(context);
		this.context=context;
		this.pictureSet=pictureSet;
		String frame = pictureSet.getFrame();
		int[] frames = FrameUtil.frame2int(frame);
		frames=FrameUtil.autoAdjust(frames,context);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(frames[2], frames[3]);
		params.setMargins(frames[0], frames[1],0,0);
		setLayoutParams(params);
		setOnClickListener(this);
		//setBackgroundColor(Color.RED);
		String envelope=pictureSet.getEnvelope();
		if(envelope!=null){
			String imgPath = AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, envelope);
			Bitmap bitmap = ImageUtil.loadImage(imgPath);
			setImageBitmap(bitmap);
		}
	}

	@Override
	public void onClick(View v) {
		PictureSetPopupWindow popupWindow=new PictureSetPopupWindow(context, pictureSet);
		popupWindow.showAtLocation((View)v.getParent(), Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
	}
}
