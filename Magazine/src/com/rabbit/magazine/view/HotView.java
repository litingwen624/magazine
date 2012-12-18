package com.rabbit.magazine.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.MagazineLoaderActivity;
import com.rabbit.magazine.WebViewActivity;
import com.rabbit.magazine.kernel.Group;
import com.rabbit.magazine.kernel.Hot;
import com.rabbit.magazine.kernel.Layer;
import com.rabbit.magazine.util.ImageUtil;

public class HotView extends ImageView implements OnClickListener {

	public static final String PAGE = "page";
	public static final String LAYER = "layer";
	public static final String AUDIO = "audio";
	public static final String LINK = "link";
	public static final String RESETSCROLL = "reset scroll";
	public static final String ZOOM="zoom";
	private Hot hot;
	private Group group;
	private FlipperPageView2 flipperPageView;
	
	private LayoutParams params;
	
	public void setParams(LayoutParams params) {
		this.params = params;
	}

	private LayoutParams pictureViewParams;

	private Context context;

	public HotView(Context context, Hot hot, Group group,
			FlipperPageView2 flipperPageView) {
		super(context);
		this.context = (MagazineLoaderActivity) context;
		this.hot = hot;
		this.group = group;
		this.flipperPageView = flipperPageView;
		// setBackgroundColor(Color.BLUE);
		// setAlpha(0.5f);
		String frame = hot.getFrame();
		int[] frames = FrameUtil.frame2int(frame);
		frames = FrameUtil.autoAdjust(frames, context);
		params = new LayoutParams(frames[2], frames[3]);
		params.setMargins(frames[0], frames[1], 0, 0);
		setLayoutParams(params);
		setOnClickListener(this);
		String pic = hot.getPicture();
		if (pic != null) {
			String imgPath = AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, pic);
			Bitmap bitmap = ImageUtil.loadImage(imgPath);
			setImageBitmap(bitmap);
		}
	}

	@Override
	public void onClick(View v) {
		String action = hot.getAction();
		String argument = hot.getArgument();
		if (action.equals(PAGE)) {
			int pageNumber;
			if (argument.contains("+")) {
				argument = argument.replace("+", "");
				pageNumber = Integer.parseInt(argument)
						+ flipperPageView.getCurrentItem() + 1;
			} else if (argument.contains("-")) {
				argument = argument.replace("-", "");
				pageNumber = flipperPageView.getCurrentItem()
						- Integer.parseInt(argument) + 1;
			} else {
				pageNumber = Integer.parseInt(argument);
			}
			flipperPageView.gotoPage(pageNumber - 1);
		} else if (action.equals(LAYER)) {
			String tag = argument;
			View viewParent = (View) getParent();
			if (tag.endsWith("#hide")) {
				if (viewParent instanceof FirstGroupView) {
					FirstGroupView firstGroupView = (FirstGroupView) viewParent;
					LayerView layerView = (LayerView) firstGroupView
							.findViewWithTag(hot.getTag());
					firstGroupView.removeView(this);
					ImageUtil.recycle(layerView);
				} else if (viewParent instanceof GroupView2) {
					GroupView2 groupView = (GroupView2) viewParent;
					LayerView layerView = (LayerView) groupView
							.getFrameLayout().findViewWithTag(hot.getTag());
					groupView.getFrameLayout().removeView(this);
					ImageUtil.recycle(layerView);
				}
				return;
			}
			int index = argument.indexOf("#");
			if (index > 0) {
				String[] split = argument.split("#");
				tag = split[0] + "#" + split[1];
			}
			if (viewParent instanceof FirstGroupView) {
				FirstGroupView firstGroupView = (FirstGroupView) viewParent;
				int count = firstGroupView.getChildCount();
				for (int i = 0; i < count; i++) {
					View view = firstGroupView.getChildAt(i);
					if (view instanceof LayerView) {
						if (tag.equals(view.getTag().toString())) {
							LayerView layerView = (LayerView) view;
							Layer layer = layerView.getLayer();
							Hot hot = layer.getHot();
							if (hot != null) {
								String arg = hot.getArgument();
								if (arg.endsWith("#hide")) {
									hot.setFrame(layer.getFrame());
									hot.setTag(layerView.getTag().toString());
									HotView hotView = new HotView(context, hot,
											group, flipperPageView);
									firstGroupView.addView(hotView);
								}
							}
							layerView.show();
						}
					}
				}
			} else if (viewParent instanceof GroupView2) {
				GroupView2 groupView = (GroupView2) viewParent;
				int count = groupView.getFrameLayout().getChildCount();
				for (int i = 0; i < count; i++) {
					View view = groupView.getFrameLayout().getChildAt(i);
					if (view instanceof LayerView) {
						if (tag.equals(view.getTag().toString())) {
							LayerView layerView = (LayerView) view;
							Layer layer = layerView.getLayer();
							Hot hot = layer.getHot();
							if (hot != null) {
								String arg = hot.getArgument();
								if (arg.endsWith("#hide")) {
									hot.setFrame(layer.getFrame());
									hot.setTag(layerView.getTag().toString());
									HotView hotView = new HotView(context, hot,
											group, flipperPageView);
									groupView.getFrameLayout().addView(hotView);
								}
							}
							layerView.show();
						}
					}
				}
			}
		} else if (action.equals(LINK)) {
			Intent intent=new Intent(context,WebViewActivity.class);
			intent.putExtra("url", argument);
			context.startActivity(intent);
			/*Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse(argument);
			intent.setData(content_url);
			context.startActivity(intent);*/
		} else if (action.equals(RESETSCROLL)) {
			View parent = (View) getParent();
			if(parent.getClass()==FrameLayout.class){
				GroupView2 groupView=(GroupView2) parent.getParent();
				groupView.smoothScrollTo(0, 0);
			}else if(parent.getClass()==FirstGroupView.class){
				PageView2 pageView=(PageView2) parent.getParent().getParent();
				pageView.loadResource(0, pageView.getFrameLayout().getChildCount());
				pageView.smoothScrollTo(0, 0);
			}
			/*View viewParent = (View) getParent();
			ViewParent framelayout = viewParent.getParent();
			ViewParent page = framelayout.getParent();
			if (page != null && page instanceof PageView2) {
				PageView2 pageview2 = (PageView2) page;
				pageview2.loadResource(0, pageview2.getFrameLayout()
						.getChildCount());
				pageview2.smoothScrollTo(0, 0);
			}
			Log.d("HotView", "回到页首");*/
		}else if(action.equals(ZOOM)){
			PictureView pictureView=(PictureView) getTag();
			int width=context.getResources().getDisplayMetrics().widthPixels;
			int height=context.getResources().getDisplayMetrics().heightPixels;
			LayoutParams params=(LayoutParams) pictureView.getLayoutParams();
			if(params.width==width&&params.height==height){
				pictureView.setLayoutParams(pictureViewParams);
				setLayoutParams(this.params);
				
				String zoomInIcon=pictureView.getPicture().getZoomedImage();
				String imgPath = AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, zoomInIcon);
				Bitmap bitmap = ImageUtil.loadImage(imgPath);
				setImageBitmap(bitmap);
			}else{
				pictureViewParams=params;
				
				params=new LayoutParams(width,height);
				params.setMargins(0, 0, 0, 0);
				pictureView.setLayoutParams(params);
				
				params=new LayoutParams(getWidth(),getHeight());
				params.setMargins(width-getWidth()-2, height-getHeight()-2, 0, 0);
				setLayoutParams(params);
				
				String zoomOutIcon=pictureView.getPicture().getZoomIcon();
				String imgPath = AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, zoomOutIcon);
				Bitmap bitmap = ImageUtil.loadImage(imgPath);
				setImageBitmap(bitmap);
				
				bringToFront();
			}
		}
	}
}
