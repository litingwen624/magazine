package com.rabbit.magazine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.rabbit.magazine.kernel.Category;
import com.rabbit.magazine.kernel.Magazine;
import com.rabbit.magazine.kernel.Page;
import com.rabbit.magazine.util.ImageUtil;
import com.rabbit.magazine.view.FlipperPageView2;
import com.rabbit.magazine.view.FlipperPagerAdapter;
import com.rabbit.magazine.view.PageView2;

public class MagazineActivity extends MagazineLoaderActivity {

	private List<PageView2> mListViews = new ArrayList<PageView2>();
	private FlipperPageView2 flipperPageView;
	private int currentItem=-1;
	private boolean isDestory=false;

	@Override
	public void showMagazinePage() {
		// 横向
		List<Category> categorys = magazine.getCategorys();
		// 避免解析不正确直接程序退出
		if(categorys!=null&&categorys.size()>0){
			Category category = categorys.get(0);
			setFlipperPageView(new FlipperPageView2(this));
			List<Page> pages = category.getPages();
			for (int i = 0; i < pages.size(); i++) {
				PageView2 pageView = new PageView2(this, pages.get(i), i,getFlipperPageView());
				mListViews.add(pageView);
			}
			FlipperPagerAdapter adapter = new FlipperPagerAdapter(mListViews);
			getFlipperPageView().setAdapter(adapter);
			setContentView(getFlipperPageView());
			getFlipperPageView().setCurrentItem(0);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		FlipperPageView2 flipperPageView2 = getFlipperPageView();
		if(flipperPageView2!=null){
			currentItem = flipperPageView2.getCurrentItem();
		}
		//销毁杂志资源
		if(mListViews!=null){
			for (PageView2 pageView : mListViews) {
				ImageUtil.releasePageViewImg(pageView);
			}
		}
		if(isDestory){
			finish();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		FlipperPageView2 pager = getFlipperPageView();
		if(currentItem!=-1&&pager!=null){
			PageView2 pageView=mListViews.get(currentItem);
			ImageUtil.loadPageViewImg(pageView);
			if(currentItem>0){
				PageView2 pre=mListViews.get(currentItem-1);
				ImageUtil.loadPageViewImg(pre);
			}
			if(currentItem<(mListViews.size()-1)){
				PageView2 next=mListViews.get(currentItem+1);
				ImageUtil.loadPageViewImg(next);
			}
			pager.setCurrentItem(currentItem);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 返回按键之后先判断是否有视频在播放,如果视频是全屏播放则调用视频停止
		if (keyCode == KeyEvent.KEYCODE_BACK){
			if(listeners!=null&&listeners.size()>0){
				for (Iterator iterator = listeners.iterator(); iterator.hasNext();) {
					OnFullScreenListener listener = (OnFullScreenListener) iterator.next();
					listener.close();
				}
				listeners.clear();
				return true;
			}
			else {
				isDestory=true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public void setMagazine(Magazine magazine) {
		this.magazine = magazine;
	}

	public Magazine getMagazine() {
		return magazine;
	}

	public void setFlipperPageView(FlipperPageView2 flipperPageView) {
		this.flipperPageView = flipperPageView;
	}

	public FlipperPageView2 getFlipperPageView() {
		return flipperPageView;
	}

}
