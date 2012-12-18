package com.rabbit.magazine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rabbit.magazine.http.HttpHelper;
import com.rabbit.magazine.util.ImageUtil;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

/**
 * 书架浏览
 * 
 * @author litingwen
 * 
 */
public class BookshelfAcvitity extends BaseActivity implements OnClickListener {

	// Forbes
	private static final String Server = "http://imag.nexdoor.cn/api/getmagazinedata.php?code=19&debugger=true";
	// Sony
//	private static final String Server = "http://imag.nexdoor.cn/api/getmagazinedata.php?code=21&debugger=true";
	
//	private static final String Server = "http://imag.nexdoor.cn/api/getmagazinedata.php?code=15&debugger=true";
	
	
	private Map<String,String> magazineMap=new HashMap<String,String>();
	private List<Magazineinfo> list = new ArrayList<Magazineinfo>();
	private Button readBtn;
	private ImageButton pageViewBtn;
	private LayoutInflater layoutInflater;
	private File[] listFiles;
	private String bookdata = "MagazineList.dat";
	private int height;
	private File appDir;
	private ViewFlipper viewFlipper;
	private int width;
	private ImageButton listViewBtn;
	private ConnectivityManager manager;
	private ImageButton infoBtn;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Display defaultDisplay = getWindowManager().getDefaultDisplay();
		width = defaultDisplay.getWidth();
		height = defaultDisplay.getHeight();
		appDir = getApplicationContext().getFilesDir();
		layoutInflater = getLayoutInflater();
		manager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			(new AsyncTask<String, Integer, String>() {
				@Override
				protected void onPreExecute() {
					progressDialog=new ProgressDialog(BookshelfAcvitity.this);
					progressDialog.setMessage("请稍后...");
					progressDialog.setCancelable(false);
					progressDialog.show();
				}
				@Override
				protected String doInBackground(String... params) {
					downloadBookData();
					refreshBooks();
					return null;
				}
				@Override
				protected void onPostExecute(String str) {
					showListView();
					progressDialog.dismiss();
				}
			}).execute();
		}else{
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				String appExtDir = AppConfigUtil.getAppExtDir();
				File magDir=new File(appExtDir);
				if(magDir.exists()){
					File coversDir=new File(magDir,"covers");
					if(coversDir.exists()){
						File[] files=coversDir.listFiles();
						for(File file:files){
							if(file.isDirectory()){
								File cover=new File(file,"cover.jpg");
								File config=new File(file,"config");
								if(cover.exists()&&config.exists()){
									Magazineinfo magazine = new Magazineinfo();
									try {
										BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(config)));
										String id=reader.readLine();
										magazine.setId(Integer.parseInt(id));
										String zip=reader.readLine();
										magazine.setZipUrl(zip);
										String title=reader.readLine();
										magazine.setTitle(title);
										String coverImage=reader.readLine();
										magazine.setCoverImage(coverImage);
										magazine.setCoverImgPath(cover.getAbsolutePath());
										list.add(magazine);
										reader.close();
									} catch (FileNotFoundException e) {
										Log.e("BookshelfAcvitity", e.getMessage());
										e.printStackTrace();
									} catch (IOException e) {
										Log.e("BookshelfAcvitity", e.getMessage());
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
				showListView();
			}
		}
	}
	
	public boolean hasDownloaded(String magId){
		String appExtDir = AppConfigUtil.getAppExtDir();
		File magDir=new File(appExtDir);
		File idDir=new File(magDir,magId);
		if(idDir.exists()&&idDir.isDirectory()){
			File resource=new File(idDir,"resource");
			if(resource.exists()&&resource.isDirectory()){
				File[] files=resource.listFiles();
				if(files.length>1){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void showListView() {
		final View inflate = layoutInflater.inflate(R.layout.bookshelf, null);
		setContentView(inflate);
		// 操作面板
		// 滑动展示
		pageViewBtn = (ImageButton) findViewById(R.id.pageViewBtn);
		pageViewBtn.setOnClickListener(this);
		// 书架展示
		listViewBtn = (ImageButton) findViewById(R.id.listViewBtn);
		listViewBtn.setOnClickListener(this);
		// license
		infoBtn = (ImageButton) findViewById(R.id.magazineInfoBtn);
		infoBtn.setOnClickListener(this);
		GridView grid=(GridView)inflate.findViewById(R.id.grid);
		//right.getLayoutParams().height = (height / 100) * 90;
		
		//GridView grid = new GridView(this);
		//grid.setNumColumns(2);
		grid.setAdapter(new MagazineListAdpater(this, list));
		grid.setTop(5);
		// 当期杂志:LeftPanel
		if (list != null && list.size() > 0) {
			final Magazineinfo info= list.get(list.size()-1);
			ImageView img=(ImageView) inflate.findViewById(R.id.curMag);
			final int width = this.width / 5;
			int height = this.height / 3;
			android.widget.LinearLayout.LayoutParams params=new android.widget.LinearLayout.LayoutParams(width,height);
			img.setLayoutParams(params);
			Bitmap bm=ImageUtil.createThumbnail(info.getCoverImgPath(), height);
			addBitMap(bm);
			img.setImageBitmap(bm);
			Button btn=(Button)inflate.findViewById(R.id.btn);
			if(hasDownloaded(String.valueOf(info.getId()))){
				btn.setText("阅读");
			}else{
				btn.setText("下载");
			}
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Button btn=(Button)v;
					String text=btn.getText().toString();
					if("下载".equals(text)){
						ProgressBar bar = (ProgressBar) inflate.findViewById(R.id.progressBar1);
						android.widget.FrameLayout.LayoutParams params=new android.widget.FrameLayout.LayoutParams(width,20);
						bar.setLayoutParams(params);
						DownloadFileAsync downloader=new DownloadFileAsync(btn,bar);
						downloader.execute(info.getZipUrl(),String.valueOf(info.getId()));
					}
					if("阅读".equals(text)){
						Intent intent = new Intent(BookshelfAcvitity.this, MagazineActivity.class);
						String tag = String.valueOf( info.getId());
						intent.putExtra(AppConfigUtil.MAG_ID, String.valueOf(tag));
						startActivity(intent);
					}
				}
			});
		}
	}
	
	

	private void downloadBookData() {
		HttpClient client = new DefaultHttpClient();
		StringBuilder builder = new StringBuilder();
		HttpGet get = new HttpGet(Server);
		try {
			HttpResponse response = client.execute(get);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			for (String s = reader.readLine(); s != null; s = reader.readLine()) {
				builder.append(s);
			}
			File file = new File(appDir + File.separator + bookdata);
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write(builder.toString());
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void refreshBooks() {
		try {
			StringBuilder builder = new StringBuilder();
			String path = appDir + File.separator + bookdata;
			File datafile=new File(path);
			if(datafile.exists()){
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(datafile)));
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					builder.append(s);
				}
				reader.close();
				JSONArray jsonArray = new JSONArray(builder.toString());
				for (int i = 0; i < jsonArray.length(); ++i) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					Magazineinfo info = new Magazineinfo();
					int id = jsonObject.getInt("id");
					info.setId(id);
					String zip = jsonObject.getString("zip_url");
					info.setZipUrl(zip);
					String title = jsonObject.getString("title");
					info.setTitle(title);
					String coverImage = jsonObject.getString("cover_image");
					info.setCoverImage(coverImage);
					if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
						String appExtDir = AppConfigUtil.getAppExtDir();
						File magDir=new File(appExtDir);
						if(!magDir.exists()){
							magDir.mkdir();
						}
						File coversDir=new File(magDir,"covers");
						if(!coversDir.exists()){
							coversDir.mkdir();
						}
						File idDir=new File(coversDir,String.valueOf(id));
						if(!idDir.exists()){
							idDir.mkdir();
						}
						File config=new File(idDir,"config");
						if(!config.exists()){
							config.createNewFile();
							FileWriter writer=new FileWriter(config);
							writer.write(String.valueOf(id));
							writer.write("\n");
							writer.write(String.valueOf(zip));
							writer.write("\n");
							writer.write(String.valueOf(title));
							writer.write("\n");
							writer.write(String.valueOf(coverImage));
							writer.flush();
							writer.close();
						}
						HttpHelper httpHelper = new HttpHelper();
						String coverPath=idDir.getAbsolutePath()+File.separator+"cover.jpg";
						File cover=new File(idDir,"cover.jpg");
						if(!cover.exists()){
							httpHelper.download(coverImage, coverPath);
						}
						info.setCoverImgPath(coverPath);
					}
					magazineMap.put(id+"", title);
					list.add(info);
				}
			}
			String appExtDir = AppConfigUtil.getAppExtDir();
			File extDir=new File(appExtDir);
			if(extDir.isDirectory()){
				File[] files = extDir.listFiles();
				for (int i = 0; i < files.length; i++) {
					File file = files[i];
					// Resource Zip 和 Resource 
					if(file.isDirectory()){
						String name = file.getName();
						if(!magazineMap.containsKey(name)){
							if(!name.equals("covers")){
								Magazineinfo info = new Magazineinfo();
								info.setId(Integer.parseInt(name));
								info.setTitle(name);
								String coverImgPth=appExtDir+File.separator+"covers"+File.separator+name+File.separator+"cover.jpg";
								info.setCoverImgPath(coverImgPth);
								magazineMap.put(name+"", name);
								list.add(info);
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("", e.getLocalizedMessage());
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.listViewBtn) {
			showListView();
		} else if (v.getId() == R.id.magazineInfoBtn) {
			ImageView imView = new ImageView(this);
			Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.license);
			imView.setImageBitmap(image);
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.height = getWindowManager().getDefaultDisplay().getHeight();
			params.width = getWindowManager().getDefaultDisplay().getWidth();
			imView.setLayoutParams(params);
			LinearLayout view = (LinearLayout) findViewById(R.id.shelfLayer);
			view.removeAllViews();
			view.addView(imView);
		} else if (v.getId() == R.id.pageViewBtn) {
			View page = layoutInflater.inflate(R.layout.bookshelf_page, null);
			viewFlipper = (ViewFlipper) page.findViewById(R.id.viewFlipper1);
			viewFlipper.setOnTouchListener(new OnTouchListener() {

				float downXValue = 0;

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					int i = event.getAction();
					switch (i) {
					case MotionEvent.ACTION_DOWN: {
						downXValue = event.getX();
						break;
					}
					case MotionEvent.ACTION_UP: {
						float currentX = event.getX();
						if ((currentX - downXValue) < -300) {
							viewFlipper.setInAnimation(AnimationUtils.loadAnimation(BookshelfAcvitity.this,
									R.anim.push_left_in));
							viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(BookshelfAcvitity.this,
									R.anim.push_left_out));
							viewFlipper.showNext();
						}
						if ((currentX - downXValue) > 300) {
							viewFlipper.setInAnimation(AnimationUtils.loadAnimation(BookshelfAcvitity.this,
									R.anim.push_right_in));
							viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(BookshelfAcvitity.this,
									R.anim.push_right_out));
							viewFlipper.showPrevious();
						}
						break;
					}
					}
					return true;
				}
			});
			HttpHelper helper = new HttpHelper();
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Magazineinfo magazine = (Magazineinfo) iterator.next();
				String path = helper.getCoverPath(magazine, appDir);
				LinearLayout item = (LinearLayout) layoutInflater.inflate(R.layout.bookshelf_item_2, null);
				LayoutParams params = new LayoutParams(width, height - 100);
				item.setLayoutParams(params);
				ImageView image = (ImageView) item.findViewById(R.id.imageView1);
				FileInputStream ot = null;
				try {
					ot = new FileInputStream(path);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				BitmapDrawable drawable = new BitmapDrawable(ot);
				if (drawable != null) {
					image.setImageDrawable(drawable);
				}
				// TextView text = (TextView) item.findViewById(R.id.textView1);
				// text.setText(magazine.getTitle());
				viewFlipper.addView(item);
			}
			LinearLayout view = (LinearLayout) findViewById(R.id.shelfLayer);
			view.removeAllViews();
			view.addView(page);
		}

	}

}
