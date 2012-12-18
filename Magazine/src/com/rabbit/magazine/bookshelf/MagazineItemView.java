package com.rabbit.magazine.bookshelf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import android.widget.FrameLayout;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewParent;
import android.widget.AbsListView.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.BookshelfAcvitity;
import com.rabbit.magazine.MagazineActivity;
import com.rabbit.magazine.MagazineExtActivity;
import com.rabbit.magazine.Magazineinfo;
import com.rabbit.magazine.R;
import com.rabbit.magazine.http.HttpHelper;
import com.rabbit.magazine.util.FileUtil;

public class MagazineItemView{

	public class CoverImageView extends ImageView {

		private String path;

		public CoverImageView(BookshelfAcvitity activity, Magazineinfo magazine) {
			super(activity);
			HttpHelper helper = new HttpHelper();
			File filesDir = activity.getApplicationContext().getFilesDir();
			path = helper.getCoverPath(magazine, filesDir);
			Bitmap map = BitmapFactory.decodeFile(path);
			if (map != null) {
				Drawable drawable = new BitmapDrawable(map);
				setImageBitmap(map);
				activity.addBitMap(map);
			} else {
				Bitmap map1 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.cover);
				setImageBitmap(map1);
				activity.addBitMap(map);
			}

		}

		@Override
		protected void onDraw(Canvas canvas) {
			try {
				BitmapDrawable drawable = (BitmapDrawable) getDrawable();
				if (drawable != null) {
					Bitmap bitmap = drawable.getBitmap();
					if (bitmap != null && bitmap.isRecycled()) {
						Bitmap map = BitmapFactory.decodeFile(path);
						if (map != null) {
							setImageBitmap(map);
							activity.addBitMap(map);
						} else {
							Bitmap map1 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.cover);
							setImageBitmap(map1);
							activity.addBitMap(map);
						}
					}
				}
				setScaleType(ScaleType.FIT_CENTER);
				super.onDraw(canvas);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private int hasRead = 0;
	private int len = 0;
	private byte buffer[] = new byte[1024];
	private int index = 0;
	private LinearLayout content;

	private BookshelfAcvitity activity;
	private Magazineinfo magazine;
	public Magazineinfo getInfo(){
		return magazine;
	}

	public MagazineItemView(Activity activity, Magazineinfo magazine) {
		super();
		this.activity = (BookshelfAcvitity) activity;
		this.magazine = magazine;
		initContent();
	}

	public View initContent() {
		int id = magazine.getId();
		LayoutInflater layoutInflater = activity.getLayoutInflater();
		content = (LinearLayout) layoutInflater.inflate(R.layout.boolshelf_item, null);
		CoverImageView cover = new CoverImageView(activity, magazine);
		LayoutParams param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		param.width = activity.getWindowManager().getDefaultDisplay().getWidth() / 5;
		param.height = activity.getWindowManager().getDefaultDisplay().getHeight() / 3;
		content.addView(cover);
		TextView text = (TextView) content.findViewById(R.id.textView1);
		text.setText(magazine.getTitle());
		final Button button = (Button) content.findViewById(R.id.button1);
		String resource = AppConfigUtil.getAppContent(String.valueOf(id));
		File file = new File(resource);
		button.setTag(magazine.getId());
		if (file.exists()) {
			button.setText(R.string.readBtn);
			cover.setOnClickListener(new ReadClickListener(button));
			button.setOnClickListener(new ReadClickListener(button));
		} else {
			button.setText(R.string.downLoadBtn);
			button.setOnClickListener(new downloadListener(magazine, content, button));
		}
		return content;
	}
	public LinearLayout getContent(){
		return content;
	}

	private final class downloadListener implements OnClickListener {
		private final Magazineinfo magazine;
		private final LinearLayout item;
		private final Button button;
		private ProgressBar bar;

		private downloadListener(Magazineinfo magazine, LinearLayout item, Button button) {
			this.magazine = magazine;
			this.item = item;
			this.button = button;
		}

		class MyHandler extends Handler {

			private ProgressBar bar;

			public MyHandler(ProgressBar bar) {
				this.bar = bar;
			}

			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					bar.setProgress(index);
					if (index >= 99) {
						bar.setVisibility(View.GONE);
						button.setVisibility(View.VISIBLE);
					}
				} else if (msg.what == 2) {
					bar.setVisibility(View.GONE);
					button.setVisibility(View.VISIBLE);
					button.setText(R.string.readBtn);
					button.setOnClickListener(new ReadClickListener(button));
				}
				super.handleMessage(msg);
			}

		}

		@Override
		public void onClick(View v) {
			bar = (ProgressBar) item.findViewById(R.id.progressBar1);
			bar.setVisibility(View.VISIBLE);
			button.setVisibility(View.GONE);
			final MyHandler handler = new MyHandler(bar);
			downloadFile(magazine.getZipUrl(), String.valueOf(magazine.getId()), handler);
		}

		public boolean downloadFile(final String urlStr, final String filename, final MyHandler handler) {
			new Thread(new Runnable() {
				private Message message;
				private boolean flag;

				private static final int REQUEST_TIMEOUT = 10 * 1000;// 设置请求超时10秒钟
				private static final int SO_TIMEOUT = 10 * 1000; // 设置等待数据超时时间10秒钟

				public HttpClient getHttpClient() {
					BasicHttpParams httpParams = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
					HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
					HttpClient client = new DefaultHttpClient(httpParams);
					return client;
				}

				public void run() {
					try {
						// 创建Magazine目录
						String appExtDir = AppConfigUtil.getAppExtDir();
						File file = new File(appExtDir);
						if (!file.exists()) {
							file.mkdir();
						}
						// Zip文件目录
						String path = AppConfigUtil.getAppExtDir(filename + ".zip");
						File zipFile = new File(path);
						if (zipFile.exists()) {
							zipFile.delete();
						}
						zipFile.createNewFile();
						HttpClient client = getHttpClient();
						HttpGet httpGet = new HttpGet(urlStr);
						// HttpGet httpGet = new
						// HttpGet("http://rabbit-android-magazine.googlecode.com/files/39.zip");
						HttpResponse httpResponse = client.execute(httpGet);
						HttpEntity entity = httpResponse.getEntity();
						float length = entity.getContentLength();
						InputStream inputStream = entity.getContent();
						OutputStream outputStream = new FileOutputStream(zipFile);
						while ((len = inputStream.read(buffer)) != -1) {
							outputStream.write(buffer, 0, len);
							hasRead += len;
							index = (int) (hasRead * 100 / length);
							message = new Message();
							message.what = 1;
							handler.sendMessage(message);
						}
						outputStream.flush();
						outputStream.close();
						inputStream.close();
						String dest = AppConfigUtil.getAppResource(filename);
						FileUtil.unZip(path, dest);
						message = new Message();
						message.what = 2;
						handler.sendMessage(message);
					} catch (Exception e) {
						flag = false;
						e.printStackTrace();
					}
				}
			}).start();
			return true;
		}
	}

	private final class ReadClickListener implements OnClickListener {
		private final Button button;

		private ReadClickListener(Button button) {
			this.button = button;
		}

		@Override
		public void onClick(View v) {
			String storageState = Environment.getExternalStorageState();
			if (storageState.equals(Environment.MEDIA_MOUNTED)) {
				Intent intent = new Intent(activity, MagazineActivity.class);
//				Intent intent = new Intent(activity, MagazineActivity.class);
				String tag = String.valueOf( button.getTag());
//				// 书架的46直接变成44放置sony的杂志
//				if(tag.equalsIgnoreCase("46")){
//					tag="44";
//				}
				intent.putExtra(AppConfigUtil.MAG_ID, String.valueOf(tag));
				activity.startActivity(intent);
				activity.recycle();
			}
		}
	}

	public void recycle() {
	}
}
