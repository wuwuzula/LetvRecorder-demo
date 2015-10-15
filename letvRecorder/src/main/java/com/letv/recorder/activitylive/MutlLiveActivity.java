package com.letv.recorder.activitylive;

import com.lecloud.common.base.util.LogUtils;
import com.lecloud.common.base.util.Logger;
import com.lecloud.skin.PlayerStateCallback;
import com.lecloud.skin.actionlive.MultLivePlayCenter;
import com.letv.recorder.Constant;
import com.letv.recorder.R;
import com.letvcloud.sdk.log.FetchLogLoader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class MutlLiveActivity extends Activity {
	private RelativeLayout mPlayerLayoutView;
	private MultLivePlayCenter mPlayerView;
	// private LivePlayCenter mPlayerView1;
	// private LivePlayCenter mPlayerView2;

	private EditText testEditText;
	// private Button testButton;

	// private Button mBtExit;
	private Button mBtShowLog;
	private Button fetchLog;

	private static String Defualt_ActivityID = Constant.activityID;// "201412083000001";
	private boolean isHLS;
	private boolean isBackgroud = false;

	@Override
	protected void onResume() {
		super.onResume();
		if (this.mPlayerView != null) {
			if (isBackgroud) {
				if(mPlayerView.getCurrentPlayState() == PlayerStateCallback.PLAYER_VIDEO_PAUSE){
//	        		this.mPlayerView.resumeVideo();
	        	}else{
	        		Logger.e("LiveActivity", "已回收，重新请求播放");
//	        		mPlayerView.playVideo(testEditText.getText() + "", "测试频道");
//	        		mPlayerView.playAction(Defualt_ActivityID);
	        	}
//				this.mPlayerView.resumeVideo();
			}
			//
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (this.mPlayerView != null) {
//			this.mPlayerView.pauseVideo();
			isBackgroud = true;
		}
		// if (this.mPlayerView1 != null) {
		// this.mPlayerView1.pauseVideo();
		// isBackgroud = true;
		// }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.vedio_layout);
		Intent intent = getIntent();
		Defualt_ActivityID = intent.getStringExtra("activityID");
		isHLS = intent.getBooleanExtra("isHLS", false);

		this.mPlayerLayoutView = (RelativeLayout) this.findViewById(R.id.layout_player);

		mPlayerView = new MultLivePlayCenter(this, true);
		mPlayerView.setRelease(false);
		mPlayerView.isShowSubLiveView(true);
		
		this.mPlayerLayoutView.addView(this.mPlayerView.getPlayerView());
		this.testEditText = (EditText) this.findViewById(R.id.testET);
		this.testEditText.setText(Defualt_ActivityID);
		this.mBtShowLog = (Button) this.findViewById(R.id.bt_showlog);
		mBtShowLog.setVisibility(View.GONE);
		fetchLog = (Button) this.findViewById(R.id.bt_fetchlog);
		fetchLog.setVisibility(View.GONE);
		fetchLog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// mPlayerView.destroyVideo();
		        // mPlayerLayoutView.removeAllViews();
				FetchLogLoader.getInstance(MutlLiveActivity.this).fetchLog();
				// ((MultLivePlayCenter)mPlayerView).setVolmn();
			}
		});
		this.mBtShowLog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startLogActivity();
			}
		});

		// mPlayerView.playAction("A2015072190018");
		mPlayerView.playAction(Defualt_ActivityID);
//		mPlayerView.changeOrientation(Configuration.ORIENTATION_LANDSCAPE);
		mPlayerView.setPlayerStateCallback(new PlayerStateCallback() {
			
			@Override
			public void onStateChange(int state, Object... extra) {
				if(state == PlayerStateCallback.PLAYER_VIDEO_PLAY){
//					mPlayerView.setVisiableActiveSubLiveView(true);
				}
			}
		});
		
	}

	private void startLogActivity() {
		// Intent intent = new Intent(this, LogInfoActivity.class);
		// Bundle bundle = new Bundle();
		// bundle.putString("logInfo", LogUtils.getLog(this));
		// intent.putExtras(bundle);
		// this.startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		this.mPlayerView.destroyVideo();
		// this.mPlayerView1.destroyVideo();
		this.mPlayerLayoutView.removeAllViews();
		super.onDestroy();
		isBackgroud = false;
		LogUtils.clearLog();

	}
}
