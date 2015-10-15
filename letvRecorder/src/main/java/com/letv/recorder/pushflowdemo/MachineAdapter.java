package com.letv.recorder.pushflowdemo;

import java.util.ArrayList;

import com.letv.recorder.R;
import com.letv.recorder.bean.LivesInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MachineAdapter extends BaseAdapter {
	private ArrayList<LivesInfo> infos;
	private Context context;

	public MachineAdapter(Context context, ArrayList<LivesInfo> infos) {
		this.context = context;
		this.infos = infos;
	}

	@Override
	public int getCount() {
		return infos.size();
	}

	@Override
	public Object getItem(int position) {
		return infos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = LayoutInflater.from(context).inflate(R.layout.item_machine_test, null);
		TextView tv = (TextView) view.findViewById(R.id.test_tv);

		tv.setText("机位" + position);

		return view;
	}

}
