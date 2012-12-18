package com.rabbit.magazine;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView;
import com.rabbit.magazine.util.ImageUtil;

public class MagazineListAdpater extends BaseAdapter {

	private List<Magazineinfo> list;
	private BookshelfAcvitity activity;

	public MagazineListAdpater(Activity activity, List<Magazineinfo> list) {
		super();
		this.list = list;
		this.activity = (BookshelfAcvitity) activity;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return list.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final LinearLayout linear;
		if(convertView==null){
			linear=(LinearLayout) activity.getLayoutInflater().inflate(R.layout.boolshelf_item, null);
		}else{
			linear=(LinearLayout)convertView;
		}
		final Magazineinfo info=list.get(position);
		ImageView cover=(ImageView) linear.findViewById(R.id.cover);
		final int width = activity.getWindowManager().getDefaultDisplay().getWidth() / 5;
		int height = activity.getWindowManager().getDefaultDisplay().getHeight() / 3;
		LayoutParams params=new LayoutParams(width, height);
		cover.setLayoutParams(params);
		String imgPath=info.getCoverImgPath();
		Bitmap bm=ImageUtil.createThumbnail(imgPath, height);
		activity.addBitMap(bm);
		cover.setImageBitmap(bm);
		TextView tv=(TextView) linear.findViewById(R.id.textView1);
		tv.setText(String.valueOf(info.getId()));
		Button btn=(Button)linear.findViewById(R.id.button1);
		if(activity.hasDownloaded(String.valueOf(info.getId()))){
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
					ProgressBar bar = (ProgressBar) linear.findViewById(R.id.progressBar1);
					android.widget.FrameLayout.LayoutParams params=new android.widget.FrameLayout.LayoutParams(width,20);
					bar.setLayoutParams(params);
					DownloadFileAsync downloader=new DownloadFileAsync(btn,bar);
					downloader.execute(info.getZipUrl(),String.valueOf(info.getId()));
				}
				if("阅读".equals(text)){
					Intent intent = new Intent(activity, MagazineActivity.class);
					String tag = String.valueOf( info.getId());
					intent.putExtra(AppConfigUtil.MAG_ID, String.valueOf(tag));
					activity.startActivity(intent);
				}
			}
		});
		return linear;
	}

	
	class MyHandler extends Handler {
		
		private int index = 0;

		private ProgressBar bar;
		private Button btn;

		public MyHandler(ProgressBar bar,Button btn) {
			this.bar = bar;
			this.btn=btn;
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				bar.setProgress(index);
				if (index >= 99) {
					bar.setVisibility(View.GONE);
					btn.setVisibility(View.VISIBLE);
				}
			} else if (msg.what == 2) {
				bar.setVisibility(View.GONE);
				btn.setVisibility(View.VISIBLE);
				btn.setText(R.string.readBtn);
			}
			super.handleMessage(msg);
		}

	}
}
