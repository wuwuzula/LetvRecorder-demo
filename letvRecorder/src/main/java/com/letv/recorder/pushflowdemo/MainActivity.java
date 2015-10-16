package com.letv.recorder.pushflowdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lecloud.common.cde.LeCloud;
import com.letv.recorder.Constant;
import com.letv.recorder.R;
import com.letv.recorder.activitylive.MutlLiveActivity;
import com.letv.recorder.bean.SearchActivityInfo;
import com.letv.recorder.callback.RequestCallback;
import com.letv.recorder.controller.LetvPublisher;
import com.letv.recorder.request.RecorderRequest;
import com.orhanobut.logger.Logger;

import java.util.List;

public class MainActivity extends Activity implements OnClickListener {


	private static String activityID = "";
	protected static String userId = "";
	protected static String secretKey = "";


	private static final String SharedPreferences = "letvsp";
	private static final String key_activityId = "activityId";
	private static final String key_userId = "userId";
	private static final String key_secretKey = "secretKey";


	private EditText editText;//直播Id输入框
	private TextView lvieContentPlay;//播放
	private ImageView liveSearch;//搜索

	private ListView liveListView;//正在直播的列表
	private ImageView livePlay;//底部直播按钮


	private LiveAdapter liveAdapter;
	private List<SearchActivityInfo> flowInfos;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Logger.init("malin");
		readSp();
		context = this;
		LeCloud.init(getApplicationContext());
		initView();
		bindListener();
		// request();
		openAlertDialog();
	}

	private void readSp() {
		android.content.SharedPreferences sp = getSharedPreferences(SharedPreferences, Context.MODE_PRIVATE);
		activityID = sp.getString(key_activityId, Constant.activityID);
		userId = sp.getString(key_userId,Constant.userId);
		secretKey = sp.getString(key_secretKey, Constant.secretKey);
	}

	private void saveSp() {
		android.content.SharedPreferences sp = getSharedPreferences(SharedPreferences, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putString(key_activityId, activityID);
		edit.putString(key_userId, userId);
		edit.putString(key_secretKey, secretKey);
		edit.commit();
	}

	/**
	 * 请求数据
	 */
	private void request() {
		/**
		 * 一定要初始化该参数
		 */
		LetvPublisher.init(activityID, userId, secretKey);

		RecorderRequest recorderRequest = new RecorderRequest();

		recorderRequest.searchActivityByUserIDRequest(userId, new RequestCallback() {
			@Override
			public void onSucess(final Object object) {
				saveSp();

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						List<SearchActivityInfo> searchActivityInfos = (List<SearchActivityInfo>) object;
						Logger.d("请求成功 searchActivityByUserIDRequest");
//						for (int i=0;i<searchActivityInfos.size();i++){
//							Logger.d(i+":" + "activityName:"+searchActivityInfos.get(i).activityName+ " activityStatus:"+searchActivityInfos.get(i).activityStatus);
//						}
						liveAdapter.setFlowInfos((List<SearchActivityInfo>) object);
						liveListView.setAdapter(liveAdapter);
						liveListView.setOnItemClickListener(liveAdapter);
					}
				});
			}

			@Override
			public void onFailed(final int statusCode, final String errorMsg) {
				runOnUiThread(new Runnable() {
					public void run() {
						Logger.d("request onFailed " + "statusCode:" + statusCode + " errorMsg:" + errorMsg.toString());
						Toast.makeText(MainActivity.this, "错误代码：" + statusCode + "," + errorMsg, Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	public void openAlertDialog() {

		View view = getLayoutInflater().from(this).inflate(R.layout.dialog_input2, null);
		final EditText etSecretKey = (EditText) view.findViewById(R.id.et_secretkeyid);
		final EditText etUserId = (EditText) view.findViewById(R.id.et_userid);
		final EditText etActivityId = (EditText) view.findViewById(R.id.et_activityid);

		etUserId.setText(userId);
		etSecretKey.setText(secretKey);
		etActivityId.setText(activityID);

		AlertDialog.Builder builder = new Builder(this);
		builder.setView(view);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				userId = etUserId.getText().toString().trim();
				activityID = etActivityId.getText().toString().trim();
				secretKey = etSecretKey.getText().toString().trim();

				request();
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.show();
	}

	public void initView() {
		liveListView = (ListView) findViewById(R.id.live_listview);
		lvieContentPlay = (TextView) findViewById(R.id.live_content_play);
		lvieContentPlay.setOnClickListener(this);
		liveSearch = (ImageView) findViewById(R.id.live_search);
		editText = (EditText) findViewById(R.id.live_id_edit);
		editText.setText(activityID);
		livePlay = (ImageView) findViewById(R.id.live_recorder);
		liveAdapter = new LiveAdapter(context, flowInfos);

	}

	public void bindListener() {
		livePlay.setOnClickListener(this);
		lvieContentPlay.setOnClickListener(this);
		liveSearch.setOnClickListener(this);
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			//TODO:录制视频
			case R.id.live_recorder:

				AlertDialog.Builder builder = new Builder(MainActivity.this);
				View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_input, null);

				final EditText recorderEdit = (EditText) view.findViewById(R.id.recorder_id_edit);
				recorderEdit.setText(activityID);

				builder.setView(view);
				final AlertDialog alertDialog = builder.create();
				view.findViewById(R.id.recorder_content_play).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String inputCotent = recorderEdit.getText().toString();
						if (!inputCotent.trim().isEmpty()) {
							alertDialog.dismiss();
							// Intent intent=new Intent(MainActivity.this,
							// RecorderActivity.class);
							Intent intent = new Intent(MainActivity.this, RecorderDemoActivity.class);
							// Intent intent=new Intent(MainActivity.this,
							// CostomRecorderActivity.class);

							intent.putExtra("activityId", inputCotent.trim());
							intent.putExtra("userId", userId);
							intent.putExtra("secretKey", secretKey);

							startActivity(intent);
						} else {
							Toast.makeText(getApplicationContext(), "活动id不能为空", Toast.LENGTH_SHORT).show();
						}
					}
				});

				alertDialog.show();

				break;


			//TODO：播放视频
			case R.id.live_content_play:
				if (TextUtils.isEmpty(editText.getText())) {
					Toast.makeText(this, "输入内容不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(context, MutlLiveActivity.class);
				intent.putExtra("activityID", editText.getText().toString());
				context.startActivity(intent);
				break;
			case R.id.live_search:
				Toast.makeText(MainActivity.this, "别点了什么都没有！", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LeCloud.destory();
	}
}
