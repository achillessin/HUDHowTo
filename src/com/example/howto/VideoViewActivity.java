package com.example.howto;

import java.util.ArrayList;
import java.util.List;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoViewActivity extends Activity {

	private class keypoint
	{
		int start;
		int stop;
		public keypoint()
		{
			start = 0;
			stop =0;
		}
		public keypoint(int strt, int stp)
		{
			start = strt;
			stop = stp;
			
		}
	}
	String tutorialvid = "/DCIM/Videos/vid.mp4";
	String techSupportVid = "/DCIM/Videos/techsupport.mp4";
	
	int mCurrentKeyPoint;
	List<keypoint> mKeyPoints = new ArrayList<keypoint>();
	Handler mhandler;
	Handler mVidHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if(msg.obj.equals("0"))
			{
				videoview.pause();
							
				//popup
				AlertDialog.Builder ad = new AlertDialog.Builder(VideoViewActivity.this);
				ad.setTitle("")
	            .setPositiveButton("Continue",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						///return
						mCurrentKeyPoint = mCurrentKeyPoint > (mKeyPoints.size()-2) ? (mKeyPoints.size()-1) : (mCurrentKeyPoint+1);
						keypoint kp = mKeyPoints.get(mCurrentKeyPoint);
						videoview.start();
						mhandler.postDelayed(pausevid,( kp.stop - kp.start)*1000);
						
					}
				});
				ad.setNegativeButton("Replay", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						mCurrentKeyPoint = mCurrentKeyPoint < (1) ? (0) : (mCurrentKeyPoint);
						keypoint kp = mKeyPoints.get(mCurrentKeyPoint);
						videoview.start();
						videoview.seekTo(kp.start*1000);
						mhandler.postDelayed(pausevid,( kp.stop - kp.start)*1000);
					}
				});
				ad.setNeutralButton("Call Help",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						videoview.stopPlayback();
						Toast.makeText(VideoViewActivity.this, "Calling Tech Support...", Toast.LENGTH_LONG).show();
						try{
						loadVideo(videoview,techSupportVid);
						videoview.requestFocus();
						videoview.setOnPreparedListener(new OnPreparedListener() {
							// Close the progress bar and play the video
							public void onPrepared(MediaPlayer mp) {
								videoview.start();
							}
						});
						}
						catch(Exception e)
						{
							Toast.makeText(VideoViewActivity.this, "Tech support is out for coffee.", Toast.LENGTH_LONG).show();
						}
											
					}
				});
				AlertDialog dia = ad.create();
				dia.show();
			}
		}
	};
	
	// Declare variables
	ProgressDialog pDialog;
	VideoView videoview;
    


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the layout from video_main.xml
		setContentView(R.layout.videoview_main);
		// Find your VideoView in your video_main.xml layout
		videoview = (VideoView) findViewById(R.id.VideoView);
		mhandler = new Handler();
		
		// Execute StreamVideo AsyncTask
		mKeyPoints.add(new keypoint(0,3));
		mKeyPoints.add(new keypoint(3,6));
		mKeyPoints.add(new keypoint(6,9));
		mKeyPoints.add(new keypoint(9,12));
		mKeyPoints.add(new keypoint(12,15));
		mKeyPoints.add(new keypoint(15,18));
		mKeyPoints.add(new keypoint(18,21));
		// Create a progressbar
		pDialog = new ProgressDialog(VideoViewActivity.this);
		// Set progressbar title
		pDialog.setTitle("Interactive tutorial");
		// Set progressbar message
		pDialog.setMessage("Buffering...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		// Show progressbar
		pDialog.show();

		try {
			// Start the MediaController
			MediaController mediacontroller = new MediaController(
					VideoViewActivity.this);
			mediacontroller.setAnchorView(videoview);
			videoview.setMediaController(mediacontroller);
			loadVideo(videoview,tutorialvid);
			mCurrentKeyPoint = 0;
			

		} catch (Exception e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}

		videoview.requestFocus();
		videoview.setOnPreparedListener(new OnPreparedListener() {
			// Close the progress bar and play the video
			public void onPrepared(MediaPlayer mp) {
				pDialog.dismiss();
				videoview.start();
				keypoint kp = mKeyPoints.get(mCurrentKeyPoint);
				mhandler.postDelayed( pausevid,( kp.stop - kp.start)*1000);
			}
		});

	}
	private void loadVideo(VideoView v, String video)
	{
		// Insert your Video URL

		//String videoname ="";
		//load from sdcard
		String path = Environment.getExternalStorageDirectory()+video;
	    videoview.setVideoPath(path);
		
		// Get the URL from String VideoURL
		//String VideoURL = "android.resource://com.example.howto/" + R.raw.vid;
		//Uri video = Uri.parse(VideoURL);
		//videoview.setVideoURI(video);
		

	}
	

	
	Runnable pausevid = new Runnable()
	{

		@Override
		public void run()
		{
			
			Message m = mVidHandler.obtainMessage();
			m.obj = new String("0");
			mVidHandler.sendMessage(m);
		}

	};

}
