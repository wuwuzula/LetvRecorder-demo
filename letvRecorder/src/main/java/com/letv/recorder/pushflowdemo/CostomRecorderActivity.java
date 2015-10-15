package com.letv.recorder.pushflowdemo;

import java.util.ArrayList;

import com.letv.recorder.Constant;
import com.letv.recorder.R;
import com.letv.recorder.bean.CameraParams;
import com.letv.recorder.bean.LivesInfo;
import com.letv.recorder.callback.LetvRecorderCallback;
import com.letv.recorder.controller.LetvPublisher;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Here is demo for no skin Recorder
 * @author pys
 *
 */
public class CostomRecorderActivity extends Activity implements Callback, OnClickListener {


	private SurfaceView sv;
	private static LetvPublisher letvPublisher;
	private ListView lv;
	private CameraParams cameraParams;


	/**
	 * activity生命周期
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/**
		 * 全屏五毛特效
		 */
//		Window win = getWindow();
//		win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//		win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		win.requestFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_recorder2);
		initView();

		initSurfaceView();//初始化surfaceView
		initPublish();//初始化推流器

		Recordrequest();//开始请求数据，并且处理数据
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(letvPublisher!=null){
			letvPublisher.stopPublish();
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * 处理UI
	 */


	/**
	 * 初始化surfaceView
	 */
	private void initSurfaceView() {
		sv = (SurfaceView) findViewById(R.id.surfaceView1);
		sv.getHolder().addCallback(this);
	}

	/**
	 * 初始化view
	 */
	private void initView() {
		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.button2).setOnClickListener(this);
		findViewById(R.id.button3).setOnClickListener(this);
		findViewById(R.id.button4).setOnClickListener(this);
		findViewById(R.id.button5).setOnClickListener(this);

		findViewById(R.id.machineContainer);
		lv = (ListView) findViewById(R.id.machineList);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectMachine(position);//选择机位开始录制
			}
		});
	}

	/**
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button1:
				/**
				 * 闪光灯,注意，前置摄像头无闪光灯
				 */
				if(letvPublisher!=null){
					if(cameraParams.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)){
						LetvPublisher.getVideoRecordDevice().setFlashFlag(true);
					}else if(cameraParams.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)){
						LetvPublisher.getVideoRecordDevice().setFlashFlag(false);
					}
				}
				break;
			case R.id.button2:

				/**
				 * 切换摄像头
				 */
				if(letvPublisher!=null){
					if(cameraParams.getCameraId()!=Camera.CameraInfo.CAMERA_FACING_FRONT){
						LetvPublisher.getVideoRecordDevice().switchCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
					}else{
						LetvPublisher.getVideoRecordDevice().switchCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
					}
				}

				break;
			case R.id.button3:
				/**
				 * 停止推流
				 */
				if(letvPublisher!=null){
					letvPublisher.stopPublish();
				}

				break;
			case R.id.button4:

				break;
			case R.id.button5:

				break;

			default:
				break;
		}
	}


	/**
	 * surfaceView的回调事件
	 */

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (letvPublisher != null) {
			LetvPublisher.getVideoRecordDevice().bindingSurface(holder);
			/**
			 * 开始前可以设置摄像头,不过需要先检测下是否有两颗摄像头,Camera.getNumberOfCameras();
			 */
//			cameraParams.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);//切换到前置摄像头
			LetvPublisher.getVideoRecordDevice().start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (letvPublisher != null) {
			LetvPublisher.getVideoRecordDevice().stop();
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 处理内核
	 */


	/**
	 * 初始化推流器
	 */
	private void initPublish() {
		LetvPublisher.init(Constant.activityID, Constant.userId, Constant.secretKey);
		letvPublisher = LetvPublisher.getInstance();
		letvPublisher.initPublisher(this);
		cameraParams = letvPublisher.getCameraParams();
	}

	//////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 业务处理
	 */

	/**
	 * 推荐每次推流前，从服务器获取机位信息，然后再开启推流
	 */
	private void Recordrequest() {
		/**
		 * 处理机位
		 */
		letvPublisher.handleMachine(new LetvRecorderCallback<ArrayList<LivesInfo>>() {
			@Override
			public void onSucess(final ArrayList<LivesInfo> arg0) {
				int size = arg0.size();// 长度表示当前申请的机位，目前最大可返回4个机位供选择,这取决于申请sdk使用的时候，开发者申请了多少个机位
				switch (size) {
					case 0:
						// 当前活动没有可用的机位
						break;
					case 1:
						// 只有一个机位，直接开播
						selectMachine(1);
						break;
					default:
						runOnUiThread(new Runnable() {
							public void run() {
								// 多路机位,供大家选择
								MachineAdapter adapter = new MachineAdapter(CostomRecorderActivity.this, arg0);
								lv.setAdapter(adapter);
							}
						});
						break;
				}
			}

			/**
			 * stateCode 错误码 msg 错误信息
			 */
			@Override
			public void onFailed(final int stateCode, final String msg) {
				// 提示失败
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(CostomRecorderActivity.this, "错误码:"+stateCode+","+msg, Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}

	/**
	 * 选择机位
	 * @param num
	 */
	private void selectMachine(final int num) {
		if (letvPublisher.selectMachine(num)) {// 该机位是空闲的
			letvPublisher.publish();// 开始直播
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(CostomRecorderActivity.this, "开始推流,选择机位："+num, Toast.LENGTH_LONG).show();
				}
			});

		} else {// 机位已经被占用
			// 做出提示
			Toast.makeText(this, "该机位已经在直播中,请选择其他机位", Toast.LENGTH_LONG).show();
		}
	}

}
