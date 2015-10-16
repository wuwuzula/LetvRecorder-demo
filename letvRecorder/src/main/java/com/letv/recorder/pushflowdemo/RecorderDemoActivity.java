package com.letv.recorder.pushflowdemo;

import com.letv.recorder.R;
import com.letv.recorder.controller.LetvPublisher;
import com.letv.recorder.ui.RecorderSkin;
import com.letv.recorder.ui.RecorderView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * 录制视频
 */
public class RecorderDemoActivity extends Activity {

	protected static final String TAG = "RecorderActivity";
	
	private static LetvPublisher publisher;
	private RecorderView rv;
	private RecorderSkin recorderSkin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/**
		 * 全屏五毛特效
		 */
		Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		win.requestFeature(Window.FEATURE_NO_TITLE);

		{
			/**
			 * 获取用户申请的的数据,如果在此之前没有设置的话，记得设置
			 */
			String activityId = getIntent().getStringExtra("activityId");
			String userId = getIntent().getStringExtra("userId");
			String secretKey = getIntent().getStringExtra("secretKey");

			LetvPublisher.init(activityId, userId, secretKey);
		}

		setContentView(R.layout.activigy_recorder1);
		
		rv = (RecorderView) findViewById(R.id.rv);//获取rootView

		initPublish();//初始化推流器
		initSkin();//初始化皮肤
		bindingPublish();//绑定推流器
	}

	@Override
	protected void onResume() {
		super.onResume();
		/**
		 * onResume的时候需要做一些事情
		 */
		if (recorderSkin != null) {
			recorderSkin.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		/**
		 * onPause的时候要作的一些事情
		 */
		if (recorderSkin != null) {
			recorderSkin.onPause();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(recorderSkin!=null){
			recorderSkin.onDestroy();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 皮肤于推流器关联
	 */
	private void bindingPublish() {
		recorderSkin.BindingPublisher(publisher);
	}

	/**
	 * 初始化皮肤
	 */
	private void initSkin() {
		recorderSkin = new RecorderSkin();
		recorderSkin.build(this, rv);
	}

	/**
	 * 初始化推流器
	 */
	private void initPublish() {
		publisher = LetvPublisher.getInstance();
		publisher.initPublisher(this);
	}

}
