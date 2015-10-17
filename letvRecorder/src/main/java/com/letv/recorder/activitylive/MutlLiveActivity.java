package com.letv.recorder.activitylive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.guagua.player.PlayerConstants;
import com.lecloud.common.base.util.LogUtils;
import com.lecloud.skin.PlayerStateCallback;
import com.lecloud.skin.actionlive.MultLivePlayCenter;
import com.letv.recorder.Constant;
import com.letv.recorder.R;
import com.letv.recorder.utils.AppInfo;
import com.orhanobut.logger.Logger;


/**
 * 视频播放界面
 */
public class MutlLiveActivity extends Activity implements View.OnClickListener{


    private static final String TAG = "malin";

    /**
     * 说明：
     * action_layout_floating_bottom_live_vertical.xml 底部布局，播放，高清，屏幕旋转等等
     * layout_player_view.xml 整个播放容器所在的布局
     */
    private RelativeLayout mPlayerLayoutView;//播放器所在的父容器

    private MultLivePlayCenter mPlayerView;
    // private LivePlayCenter mPlayerView1;


    private static String Defualt_ActivityID = Constant.activityID;// "201412083000001";
    private boolean isHLS;
    private boolean isBackgroud = false;

    RelativeLayout mVideoWindow;

    ProgressBar mProgressbar;


    @Override
    protected void onResume() {
        super.onResume();
        if (this.mPlayerView != null) {
            if (isBackgroud) {
                if (mPlayerView.getCurrentPlayState() == PlayerStateCallback.PLAYER_VIDEO_PAUSE) {
//	        		this.mPlayerView.resumeVideo();
                } else {
                    Logger.e("LiveActivity", "已回收，重新请求播放");
//	        		mPlayerView.playVideo(testEditText.getText() + "", "测试频道");
//	        		mPlayerView.playAction(Defualt_ActivityID);
                }
//				this.mPlayerView.resumeVideo();
            }
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

        initData();
        initView();
        bindListener();








        /**
         *
         * 参数说明：
         * context:Context
         * hasSkin:rue:使用SDK播放器皮肤 false:不使用SDK播放器皮肤
         * sSupportHLS:true-hls播放 false-rtmp播放
         * width:视频宽度
         * height:视频高度
         * mode:PlayerConstants.DISPLAY_SCALE 按比例拉伸 PlayerConstants.DISPLAY_CENTER 传入的款到居中显示
         */

        //TODO:1.创建活动播放器
        mPlayerView = new MultLivePlayCenter(this, false, true, AppInfo.screenWidthForPortrait, AppInfo.screenHeightForPortrait, PlayerConstants.DISPLAY_SCALE_ALL);
        mPlayerView.setRelease(false);


        //设置小视频窗口默认展开、收起
        mPlayerView.isShowSubLiveView(true);


        //TODO:2:获取播放器视图
        mVideoWindow = (RelativeLayout) mPlayerView.getPlayerView();

        measureView();

        mPlayerLayoutView.addView(mVideoWindow);


        //TODO:启动活动直播
        mPlayerView.playAction(Defualt_ActivityID);
//		mPlayerView.changeOrientation(Configuration.ORIENTATION_LANDSCAPE);
        mPlayerView.setPlayerStateCallback(new PlayerStateCallback() {

            @Override
            public void onStateChange(int state, Object... extra) {

                //TODO:state：播放器回调状态 如下
                switch (state){

                    case PlayerStateCallback.PLAYER_IDLE:

                        break;

                    case PlayerStateCallback.PLAYER_INIT:

                        break;


                    //TODO:视频开始播放事件
                    case PlayerStateCallback.PLAYER_VIDEO_PLAY:

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                              mPlayerView.setVisiableActiveSubLiveView(true);
                                mProgressbar.setVisibility(View.GONE);
                            }
                        });

                        break;


                    case PlayerStateCallback.PLAYER_SEEK_FINISH:

                        break;

                    case PlayerStateCallback.PLAYER_VIDEO_COMPLETE:

                        break;

                    //TODO:视频停止播放事件
                    case PlayerStateCallback.PLAYER_STOP:
                        Toast.makeText(MutlLiveActivity.this, "停止播放", Toast.LENGTH_SHORT).show();

                        break;

                    case PlayerStateCallback.PLAYER_VIDEO_RESUME:

                        break;

                    case PlayerStateCallback.PLAYER_BUFFERING_START:

                        break;

                    case PlayerStateCallback.PLAYER_BUFFERING_END:

                        break;

                    //TODO:视频播放出错
                    case PlayerStateCallback.PLAYER_ERROR:
                        Toast.makeText(MutlLiveActivity.this, "播放出错", Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        });

    }

    private void bindListener() {
        mPlayerLayoutView.setOnClickListener(this);
    }

    private void initData() {

        //TODO:init Logger
        Logger.init(TAG);

        //TODO:init AppInfo
        AppInfo.initApp().initScreenInfo(this);


        //TODO:getIntent
        Intent intent = getIntent();
        Defualt_ActivityID = intent.getStringExtra("activityID");
        isHLS = intent.getBooleanExtra("isHLS", false);
    }


    private void initView(){
        Logger.d("MutlLiveActivity onCreate");
        mProgressbar = (ProgressBar) findViewById(R.id.pb_loading);
        mPlayerLayoutView = (RelativeLayout) this.findViewById(R.id.rl_live_video_player_parent_layout);

    }
    @Override
    protected void onDestroy() {
        //TODO:销毁直播播放器
        mPlayerView.destroyVideo();
        // this.mPlayerView1.destroyVideo();
        mPlayerLayoutView.removeAllViews();
        super.onDestroy();
        isBackgroud = false;
        LogUtils.clearLog();

    }


    /**
     * 测量播放器的View大小
     */
    private void measureView() {
        ViewTreeObserver mViewTreeObserver = mVideoWindow.getViewTreeObserver();

		mViewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
                mVideoWindow.getViewTreeObserver().removeOnPreDrawListener(this);
				Logger.d("mplayview.getWidth():" + mVideoWindow.getWidth());
				Logger.d("mplayview.getHeight():" + mVideoWindow.getHeight());
				return true;
			}


		});

        mViewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            boolean isFirst = true;//默认调用两次，这里只让它执行一次回调

            @Override
            public void onGlobalLayout() {

                if (isFirst) {
                    isFirst = false;
                    Logger.d("mplayview.getWidth():2:" + mVideoWindow.getWidth());
                    Logger.d("mplayview.getHeight():2:" + mVideoWindow.getHeight());
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            //TODO：播放器的整个容器
            case R.id.rl_live_video_player_parent_layout:
                Toast.makeText(MutlLiveActivity.this, "+1", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
