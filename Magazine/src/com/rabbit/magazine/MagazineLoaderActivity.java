package com.rabbit.magazine;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rabbit.magazine.kernel.Magazine;
import com.rabbit.magazine.kernel.Page;
import com.rabbit.magazine.parser.MagazineReader;
import com.rabbit.magazine.util.ImageUtil;

/**
 * 杂志打开时候先加载资源
 * 
 * @author litingwen
 * 
 */
public abstract class MagazineLoaderActivity extends BaseActivity {

	protected Magazine magazine;
	protected int height;
	protected int width;
	protected String resPath;

	protected LayoutInflater layoutInflater;

	protected List<Page> pages;
	protected ImageButton shareBtn;
	protected ImageButton categoryBtn;
	private ProgressBar progress;
	private Thread cacheThread;

	private Object lock=new Object();

	Map<String, Bitmap> caches = new HashMap<String, Bitmap>();
	private ProgressDialog dialog;

	class ResourceLoader extends Handler {

		private ProgressBar bar;

		private ProgressDialog dialog;
		public ResourceLoader(ProgressBar bar, ProgressDialog dialog) {
			this.bar = bar;
			this.dialog=dialog;
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 2) {
//				bar.setVisibility(View.GONE);
				dialog.dismiss();
				showMagazinePage();
			}
			super.handleMessage(msg);
		}

	}

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Intent intent = getIntent();
		layoutInflater = getLayoutInflater();
		AppConfigUtil.MAGAZINE_ID = intent.getStringExtra(AppConfigUtil.MAG_ID);
//		AppConfigUtil.MAGAZINE_ID="82";
		int height = getWindowManager().getDefaultDisplay().getHeight();
		this.setHeight(height);
		int width = getWindowManager().getDefaultDisplay().getWidth();
		this.setWidth(width);
		if (AppConfigUtil.MAGAZINE_ID == null || AppConfigUtil.MAGAZINE_ID.equals("")) {
			finish();
		} else {
			setContentView(R.layout.main);
			dialog = ProgressDialog.show(this, "正在加载…", "精彩杂志马上呈现");
//			progress = (ProgressBar) findViewById(R.id.progressBar1);
//			progress.setVisibility(View.VISIBLE);
			final ResourceLoader handler = new ResourceLoader(progress,dialog);
			loadPageResource(handler);
			startCacheThread();
		}
	}

	public void addCacheImage(String cachePath, Bitmap bitmap) {
		caches.put(cachePath, bitmap);
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	private void startCacheThread() {
		cacheThread = new Thread(new Runnable() {

			public void run() {
				try {
					// 创建Cache目录
					String cacheDir = AppConfigUtil.getAppCache(AppConfigUtil.MAGAZINE_ID);
					File cache = new File(cacheDir);
					if (!cache.exists()) {
						cache.mkdir();
					}
					while (true) {
						Set<Entry<String, Bitmap>> entrySet = caches.entrySet();
						int size = entrySet.size();
						for (Iterator iterator = entrySet.iterator(); iterator.hasNext();) {
							Entry<String, Bitmap> entry = (Entry<String, Bitmap>) iterator.next();
							Bitmap bitmap = entry.getValue();
							if (bitmap != null) {
								ImageUtil.writeBitmap(bitmap, new File(entry.getKey()));
							}
						}
						caches.clear();
						synchronized (lock) {
							lock.wait();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		cacheThread.start();
	}

	public abstract void showMagazinePage();

	/**
	 * 加载书架资源
	 * 
	 * @param handler
	 */
	private void loadPageResource(final ResourceLoader handler) {
		new Thread(new Runnable() {

			public void run() {
				try {
					MagazineReader magareader = new MagazineReader();
					try {
						String path = AppConfigUtil.getAppContent(AppConfigUtil.MAGAZINE_ID);
						File content = new File(path);
						if (!content.exists()) {
							Toast.makeText(getApplicationContext(), "抱歉,未找到该杂志" + path, 10);
							finish();
							return;
						}
						// 创建Cache目录
						String cacheDir = AppConfigUtil.getAppCache(AppConfigUtil.MAGAZINE_ID);
						File cache = new File(cacheDir);
						if (!cache.exists()) {
							cache.mkdir();
						}
						// 删除缓存目录
//						else {
//							File[] listFiles = cache.listFiles();
//							for (int i = 0; i < listFiles.length; i++) {
//								listFiles[i].delete();
//							}
//							cache.delete();
//							cache.mkdir();
//						}
						InputStream stream = new FileInputStream(content);
						SAXParserFactory factory = SAXParserFactory.newInstance();
						SAXParser parser = factory.newSAXParser();
						XMLReader reader = parser.getXMLReader();
						reader.setContentHandler(magareader.getRootElement().getContentHandler());
						reader.parse(new InputSource(stream));
					} catch (Exception e) {
						e.printStackTrace();
					}
					setMagazine(magareader.getMagazine());
					getMagazine().setMagID(AppConfigUtil.MAGAZINE_ID);
					getMagazine().rebuild();
					Message message = new Message();
					message.what = 2;
					handler.sendMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		Log.d("Magazine", "Pause");
		super.onPause();
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

}
