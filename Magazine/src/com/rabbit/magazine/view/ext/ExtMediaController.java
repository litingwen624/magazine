package com.rabbit.magazine.view.ext;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController.MediaPlayerControl;

import com.rabbit.magazine.R;

public class ExtMediaController extends FrameLayout {

	private MediaPlayerControl mPlayer;
	private Context mContext;
	private ImageButton playButton;
	private ImageButton stopButton;
	private ImageButton finishButton;
	private ImageButton expandButton;

	public ExtMediaController(Context context) {
		super(context);
		mContext = context;
		Activity act=(Activity) context;
		LayoutInflater inflater = act.getLayoutInflater();
		View controller = inflater.inflate(R.layout.mediacontroller, null);
		finishButton=(ImageButton) controller.findViewById(R.id.videofinishbutton);
		finishButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPlayer.pause();
			}
		});
		playButton=(ImageButton) controller.findViewById(R.id.videoplaybutton);
		playButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPlayer.start();
			}
		});
		stopButton=(ImageButton) controller.findViewById(R.id.videopausebutton);
		stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPlayer.pause();
			}
		});
		expandButton=(ImageButton) controller.findViewById(R.id.videoexpandbutton);
		expandButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPlayer.pause();
			}
		});
		addView(controller);
	}



}
