package com.rabbit.magazine.view;

import java.util.ArrayList;
import java.util.List;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.MagazineLoaderActivity;
import com.rabbit.magazine.MagazinePageAdapter;
import com.rabbit.magazine.R;
import com.rabbit.magazine.kernel.Animation;
import com.rabbit.magazine.kernel.BasicView;
import com.rabbit.magazine.kernel.Group;
import com.rabbit.magazine.kernel.Hot;
import com.rabbit.magazine.kernel.Layer;
import com.rabbit.magazine.kernel.Page;
import com.rabbit.magazine.kernel.Picture;
import com.rabbit.magazine.kernel.PictureSet;
import com.rabbit.magazine.kernel.Rotater;
import com.rabbit.magazine.kernel.Shutter;
import com.rabbit.magazine.kernel.Slider;
import com.rabbit.magazine.kernel.Video;
import com.rabbit.magazine.util.ImageUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class PageView2 extends ScrollView {
	
	private Context context;
	
	private FlipperPageView2 flipperPageView;
	
	private Group firstGroup;
	
	private int UNIT=768;//768-实际滑动的距离
	
	private List<View> groupViewList=new ArrayList<View>();
	
	public List<View> getGroupViewList(){
		return groupViewList;
	}
	
	/**
	 * 整个FrameLayout
	 */
	private FrameLayout frameLayout;
	
	private View bigGroupView;
	
	public FrameLayout getFrameLayout() {
		return frameLayout;
	}

	public PageView2(Context context,Page page,int pageIndex,FlipperPageView2 flipperPageView){
		super(context);
		this.context=context;
		this.flipperPageView=flipperPageView;
		int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
		int heightPixels = context.getResources().getDisplayMetrics().heightPixels;
		UNIT=heightPixels;
		LayoutParams params=new LayoutParams(widthPixels, heightPixels);
		setLayoutParams(params);
		setFocusableInTouchMode(true);
		setFrameLayout(new FrameLayout(context));
		params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		getFrameLayout().setLayoutParams(params);
		addView(getFrameLayout());
		List<Group> groups = page.getGroups();
		if (groups != null && groups.size() > 0) {
			firstGroup=groups.get(0);
			intialFirstGroupView(firstGroup);
			buildGroupView(firstGroup);
			//上下滑动翻页
			setOnTouchListener(new OnTouchListener() {
				float downYValue = 0;
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					for (View groupView : groupViewList) {
						// 消除ScrollView嵌套的滑动事件冲突
						groupView.getParent().requestDisallowInterceptTouchEvent(false);
					}
					int action = event.getAction();
					switch (action) {
					case MotionEvent.ACTION_DOWN: {
						downYValue = event.getY();
						return true;
					}
					case MotionEvent.ACTION_UP: {
						int curScrollY = v.getScrollY();
						int childCount = getFrameLayout().getChildCount();
						float currentY = event.getY();
						if ((downYValue - currentY) > 100) {// 向下滑动
							int Y=curScrollY+UNIT;
							int height=getFrameLayout().getHeight();
							if((Y+UNIT)<=height){
								loadResource(Y,childCount);
								((PageView2)v).smoothScrollTo(0, Y);
							}else{
								//Toast.makeText(PageView2.this.context, "已到最底端", Toast.LENGTH_SHORT).show();
							}
						}
						if ((currentY - downYValue) > 100) {// 向上滑动
							int Y=curScrollY-UNIT;
							if(Y>=0){
								loadResource(Y,childCount);
								((PageView2)v).smoothScrollTo(0, Y);
							}else{
								//Toast.makeText(PageView2.this.context, "已到最顶端", Toast.LENGTH_SHORT).show();
							}
						}
						// 点击事件
						if(currentY==downYValue){
							//Toast.makeText(PageView2.this.context, "浮动层",Toast.LENGTH_SHORT).show();
							//showOperation(new Float(curScrollY).intValue());
						}
						break;
					}
					}
					return false;
				}
			});
			groupViewList.clear();//清除
			setVerticalFadingEdgeEnabled(false);
			setHorizontalFadingEdgeEnabled(false);
		}
	}
	
	protected void showOperation(int positionY) {
		final MagazineLoaderActivity activity = (MagazineLoaderActivity)context;
		int height = activity.getWindowManager().getDefaultDisplay().getHeight();
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		final ViewGroup floatLayer = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.topmenu, null);
		floatLayer.setVisibility(View.VISIBLE);
		floatLayer.setBackgroundColor(Color.BLACK);
		floatLayer.setAlpha((float) 0.5);
		ImageButton backhomeBtn = (ImageButton) floatLayer.findViewById(R.id.backHomeBtn);
		backhomeBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.finish();
			}
		});
		ImageButton categoryBtn = (ImageButton) floatLayer.findViewById(R.id.catetoryBtn);
		categoryBtn.setOnClickListener(new View.OnClickListener() {

			private Gallery gallery;

			@Override
			public void onClick(View v) {
//				gallery=(Gallery) floatLayer.findViewById(R.id.gallery1);
//				gallery=new Gallery(activity);
//				gallery.setAdapter(new MagazinePageAdapter(activity, activity.getMagazine()));
//				gallery.setVisibility(View.VISIBLE);
				GalleryPageView pageView=new GalleryPageView(activity);
				floatLayer.addView(pageView);
			}
		});
		int position=(positionY/height)*height;
		LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(0, position, 0, 0);
		params.height=height;
		params.width=width;
		params.width=activity.getWindowManager().getDefaultDisplay().getWidth();
		floatLayer.setClickable(true);
		floatLayer.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				if(event.getAction()==MotionEvent.ACTION_UP){
					int childCount = floatLayer.getChildCount();
					for (int i = 0; i < childCount; i++) {
						View child= floatLayer.getChildAt(i);
						if(child instanceof GalleryPageView){
							GalleryPageView pager=(GalleryPageView) child;
							pager.recycle();
						}
					}
					floatLayer.setVisibility(View.GONE);
				}
				return false;
			}
		});
		getFrameLayout().addView(floatLayer,params);
	}

	/**
	 * 上下滑动时资源的回收和加载,加载上中下3屏的图片资源，其余释放
	 * @param Y
	 * @param childCount
	 */
	protected void loadResource(int Y,int childCount){
		int minY=Y-UNIT;
		int maxY=Y+UNIT*2;
		for (int i = 0; i < childCount; i++) {
			View childView=getFrameLayout().getChildAt(i);
			if(childView instanceof FirstGroupView){
				FirstGroupView firstGroupView=(FirstGroupView)childView;
				int childCount2=firstGroupView.getChildCount();
				for(int j=0;j<childCount2;j++){
					View view=firstGroupView.getChildAt(j);
					if(view instanceof PictureView){
						PictureView pictureView=(PictureView)view;
						LayoutParams params=(LayoutParams)pictureView.getLayoutParams();
						if(minY<=params.topMargin&&params.topMargin<maxY){//加载FirstGroupView资源
							if(!pictureView.isLoad()){
								Picture picture=pictureView.getPicture();
								if(picture!=null){
									String resource=picture.getResource();
									String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, resource);
									Bitmap bitmap=ImageUtil.loadImage(imgPath);
									pictureView.setImageBitmap(bitmap);
									pictureView.setScaleType(ScaleType.FIT_XY);
								}
							}
						}else{//释放FirstGroupView资源,如果只有一张底图,将不释放资源
							Group group=pictureView.getGroup();
							List<Picture> pictures=group.getPictures();
							if(pictures.size()>=2){
								ImageUtil.recycle(pictureView);
							}
						}
					}
				}
			}else if(childView instanceof GroupView2){
				GroupView2 groupView=(GroupView2)childView;
				LayoutParams params = (LayoutParams)groupView.getLayoutParams();
				if(minY<=params.topMargin&&params.topMargin<maxY){//加载GroupView资源
					FrameLayout frameLayout=groupView.getFrameLayout();
					int childCount2=frameLayout.getChildCount();
					for(int j=0;j<childCount2;j++){
						View view=frameLayout.getChildAt(j);
						if(view instanceof PictureView){
							PictureView pictureView=(PictureView)view;
							if(!pictureView.isLoad()){
								Picture picture=pictureView.getPicture();
								if(picture!=null){
									String resource=picture.getResource();
									if(resource!=null){
										String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, resource);
										Bitmap bitmap=ImageUtil.loadImage(imgPath);
										pictureView.setImageBitmap(bitmap);
										pictureView.setScaleType(ScaleType.FIT_XY);
									}
								}
							}
						}
					}
				}else{//释放GroupView资源
					FrameLayout frameLayout=groupView.getFrameLayout();
					int childCount2=frameLayout.getChildCount();
					for(int j=0;j<childCount2;j++){
						View view=frameLayout.getChildAt(j);
						if(view instanceof PictureView){
							PictureView pictureView=(PictureView)view;
							ImageUtil.recycle(pictureView);
						}
					}
				}
			}else if(childView instanceof HorizontalGroupView){
				HorizontalGroupView hGroupView=(HorizontalGroupView)childView;
				LayoutParams hparams = (LayoutParams)hGroupView.getLayoutParams();
				if(minY<=hparams.topMargin&&hparams.topMargin<maxY){//加载GroupView资源
					FrameLayout frameLayout=hGroupView.getFrameLayout();
					int childCount2=frameLayout.getChildCount();
					for(int j=0;j<childCount2;j++){
						View view=frameLayout.getChildAt(j);
						if(view instanceof PictureView){
							PictureView pictureView=(PictureView)view;
							if(!pictureView.isLoad()){
								Picture picture=pictureView.getPicture();
								if(picture!=null){
									String resource=picture.getResource();
									if(resource!=null){
										String imgPath=AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, resource);
										Bitmap bitmap=ImageUtil.loadImage(imgPath);
										pictureView.setImageBitmap(bitmap);
										pictureView.setScaleType(ScaleType.FIT_XY);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 初始化第一个GroupView
	 * @param group
	 */
	private void intialFirstGroupView(Group group){
		List<Hot> hots=group.getHots();
		List<Picture> pictures=group.getPictures();
		List<Layer> layers=group.getLayers();
		List<Animation> animations=group.getAnimations();
		List<Video> videos=group.getVideos();
		List<PictureSet> pictureSets=group.getPictureSets();
		List<Slider> sliders=group.getSliders();
		List<Shutter> shutters=group.getShutters();
		List<Group> groups=group.getGroups();
		List<Rotater> rotater=group.getRotaters();
		int[] frames=FrameUtil.frame2int(group.getFrame());
		if(frames[2]>=1024&&frames[3]>=768
				&&
				(hots.size()!=0
				||layers.size()!=0
				||animations.size()!=0
				||videos.size()!=0
				||pictureSets.size()!=0
				||pictures.size()!=0
				||sliders.size()!=0
				||shutters.size()!=0
				||groups.size()>1
				||rotater.size()!=0)){
			FirstGroupView firstGroupView=new FirstGroupView(context,group,flipperPageView,this);
			getFrameLayout().addView(firstGroupView);
			firstGroup=group;
		}else{
			if(group.getGroups().size()>0){
				intialFirstGroupView(group.getGroups().get(0));
			}
		}
		
		
		/*List<Hot> hots=group.getHots();
		List<Picture> pictures=group.getPictures();
		List<Layer> layers=group.getLayers();
		List<Animation> animations=group.getAnimations();
		List<Video> videos=group.getVideos();
		List<PictureSet> pictureSets=group.getPictureSets();
		List<Slider> sliders=group.getSliders();
		List<Shutter> shutters=group.getShutters();
		//List<Group> groups=group.getGroups();
		if(hots.size()!=0||layers.size()!=0||animations.size()!=0||videos.size()!=0||pictureSets.size()!=0||pictures.size()!=0||sliders.size()!=0||shutters.size()!=0){
			int[] frames=FrameUtil.frame2int(group.getFrame());
			if(frames[2]>=1024&&frames[3]>=768){
				FirstGroupView firstGroupView=new FirstGroupView(context,group,flipperPageView,this);
				getFrameLayout().addView(firstGroupView);
				firstGroup=group;
				return;
			}
		}else{
			List<Group> groups=group.getGroups();
			if(groups!=null&&groups.size()!=0){
				for(Group g:groups){
					intialFirstGroupView(g);
				}
			}
		}*/
	}
	

	/**
	 * 递归构建GroupView
	 * @param group
	 */
	private void buildGroupView(Group group){
		if(group==null){
			return;
		}
		List<Group> groups=group.getGroups();
		if(groups!=null&&groups.size()!=0){
			for(Group g:groups){
				String oritentation=g.getOrientation();
				View groupView=null;
				int childCount;
				if(oritentation.equals(BasicView.HORIZONTAL)){
					HorizontalGroupView hGroupView=new HorizontalGroupView(context,g,flipperPageView,this);
					childCount=hGroupView.getFrameLayout().getChildCount();
					groupView=hGroupView;
					
				}else{
					GroupView2 vGroupView=new GroupView2(context,g,flipperPageView,this);
					childCount=vGroupView.getFrameLayout().getChildCount();
					groupView=vGroupView;
				}
				//如果Group里只有嵌套的Group子控件则不添加到PageView里
				if(childCount!=0){
					String frame=g.getFrame();
					int[] frames=FrameUtil.frame2int(frame);
					//如果该层Group覆盖了整个屏幕，那么就将该Group下的子控件添加到该Group下
					if(getBigGroupView()!=null){
						((GroupView2)getBigGroupView()).getFrameLayout().addView(groupView);
					}else{
						getFrameLayout().addView(groupView);
					}
					if(frames[0]==0&&frames[1]==0&&frames[2]>=1024&&frames[3]>=768&&oritentation.equals(BasicView.VERTICAL)){
						setBigGroupView(groupView);
					}
					groupViewList.add(groupView);
				}else{
					groupView=null;
				}
				List<Group> groups_=g.getGroups();
				if(groups_!=null&&groups_.size()!=0){
					for(Group g_:groups_){
						buildGroupView(g_);
					}
				}
			}
		}else{
			if(firstGroup!=group){
				List<Hot> hots=group.getHots();
				List<Picture> pictures=group.getPictures();
				List<Layer> layers=group.getLayers();
				List<Animation> animations=group.getAnimations();
				List<Video> videos=group.getVideos();
				List<PictureSet> pictureSets=group.getPictureSets();
				List<Slider> sliders=group.getSliders();
				List<Shutter> shutters=group.getShutters();
				if(hots.size()!=0||layers.size()!=0||animations.size()!=0||videos.size()!=0||pictureSets.size()!=0||pictures.size()!=0||sliders.size()!=0||shutters.size()!=0){
					View groupView=null;
					String oritentation=group.getOrientation();
					if(oritentation.equals(BasicView.HORIZONTAL)){
						groupView=new HorizontalGroupView(context, group, flipperPageView,this);
					}else{
						groupView=new GroupView2(context,group,flipperPageView,this);
					}
					if(getBigGroupView()!=null){
						View bigGroupView=getBigGroupView();
						if(bigGroupView instanceof HorizontalGroupView){
							((HorizontalGroupView)bigGroupView).getFrameLayout().addView(groupView);
						}else if(bigGroupView instanceof GroupView2){
							((GroupView2)bigGroupView).getFrameLayout().addView(groupView);
						}
					}else{
						getFrameLayout().addView(groupView);
					}
					groupViewList.add(groupView);
				}
			}
		}
	}

	public void setFrameLayout(FrameLayout frameLayout) {
		this.frameLayout = frameLayout;
	}

	public View getBigGroupView() {
		return bigGroupView;
	}

	public void setBigGroupView(View bigGroupView) {
		this.bigGroupView = bigGroupView;
	}

}