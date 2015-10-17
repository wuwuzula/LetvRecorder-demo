package com.letv.recorder.pushflowdemo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.letv.recorder.R;
import com.letv.recorder.activitylive.MutlLiveActivity;
import com.letv.recorder.bean.SearchActivityInfo;
import com.letv.recorder.net.LruImageCache;

public class LiveAdapter extends BaseAdapter implements OnItemClickListener{
	private static final int status = 1;
	private List<SearchActivityInfo> flowInfos = new ArrayList<SearchActivityInfo>();
	private Context context;

	public LiveAdapter(Context ctx, List<SearchActivityInfo> flowInfos) {
		this.context = ctx;
		setFlowInfos(flowInfos);
	}

	@Override
	public int getCount() {
		return flowInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return flowInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.live_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.updateUI(convertView, position);
		return convertView;
	}

	class ViewHolder {
		private SearchActivityInfo info;
		public NetworkImageView netWorkImageView;
		public TextView liveTitle;
		public TextView liveType;
		public TextView liveCount;

		public ViewHolder(View view) {
			this.netWorkImageView = (NetworkImageView) view.findViewById(R.id.item_img);
			this.liveTitle = (TextView) view.findViewById(R.id.item_live_title);
			this.liveType = (TextView) view.findViewById(R.id.item_live_angle);
			this.liveCount = (TextView) view.findViewById(R.id.item_live_count);
		}

		public void updateUI(View view, int position) {
			info = flowInfos.get(position);
			netWorkImageView.setDefaultImageResId(R.drawable.default_img_16_10);
			netWorkImageView.setErrorImageResId(R.drawable.default_img_16_10);
			netWorkImageView.setImageUrl(info.coverImgUrl, LruImageCache.getImageLoader(context));
			liveTitle.setText(info.activityName == null ? "" : info.activityName);
			
			if (info.activityStatus == status) {
				Drawable drawable = context.getResources().getDrawable(R.drawable.item_play_blue);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				liveType.setCompoundDrawables(drawable, null, null, null);
				liveType.setCompoundDrawablePadding(18);
				liveType.setText("Live");
				
			} else if (info.activityStatus == 2 || info.activityStatus == 3) {
				Drawable drawable = context.getResources().getDrawable(R.drawable.item_play_gray);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				liveType.setCompoundDrawables(drawable, null, null, null);
				liveType.setCompoundDrawablePadding(18);
				liveType.setText("Test");
			}

			liveCount.setText(info.userCount + "");
		}

	}

	public void setFlowInfos(List<SearchActivityInfo> infos) {
		if(infos == null) return;
		this.flowInfos.clear();
		for (SearchActivityInfo info : infos) {
				this.flowInfos.add(info);
				notifyDataSetChanged();
		}
//		for (SearchActivityInfo info : infos) {
//			if (info.activityStatus == status) {
//				this.flowInfos.add(info);
//				notifyDataSetChanged();
//			}
//		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if(flowInfos != null && flowInfos.size() >0){
			SearchActivityInfo info = flowInfos.get(position);
			String activityId = info.activityId;
			Intent intent = new Intent(context,MutlLiveActivity.class);
			intent.putExtra("activityID", activityId);
			context.startActivity(intent);
		}
	}
	
}
