package com.rabbit.magazine.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

import com.rabbit.magazine.Magazineinfo;
import com.rabbit.magazine.util.StringUtil;

public class HttpHelper {

	private static final String COVER_PNG = "cover.png";
	private URL url = null;

	public static String parseImageResource(String coverImage) {
		return null;
	}

	public String getCoverPath(Magazineinfo info, File filesDir) {
		String dir = filesDir + File.separator + info.getId();
		File fileDir=new File(dir);
		if (!fileDir.exists()) {
			fileDir.mkdir();
		}
		String path = dir + File.separator + COVER_PNG;
		File file = new File(path);
		if (file.exists()) {
			return path;
		} else {
			String coverImage = info.getCoverImage();
			if(StringUtil.isNotNull(coverImage)){
				download(coverImage, path);
				File file1=new File(path);
				boolean exists = file1.exists();
				System.out.println(file1.canRead());
			}
		}
		return path;
	}

	/**
	 * 根据URL下载文件,前提是这个文件当中的内容是文本,函数的返回值就是文本当中的内容 1.创建一个URL对象
	 * 2.通过URL对象,创建一个HttpURLConnection对象 3.得到InputStream 4.从InputStream当中读取数据
	 * 
	 * @param urlStr
	 * @return
	 */
	public void download(String urlStr, String path) {
		try {
			url = new URL(urlStr);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			File file=new File(path);
			FileOutputStream ot = new FileOutputStream(file);
			InputStream inputStream = urlConn.getInputStream();
			byte[] bt = new byte[1024];
			int len = -1; 
	        while ((len = inputStream.read(bt)) != -1) { 
	        	ot.write(bt, 0, len); 
	        } 
			inputStream.close();
			ot.flush();
			ot.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("", e.getMessage());
		}
	}

}
