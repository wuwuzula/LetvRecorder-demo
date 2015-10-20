package com.letv.recorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.letv.recorder.R;
import com.letv.recorder.data.Watcher;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by malin on 15-10-20.
 */
public class WatcherAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Watcher> mWatcherList;
    private Context mContext;
    public WatcherAdapter(Context context, ArrayList<Watcher> watchers) {
        this.mWatcherList = watchers;
        mContext= context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return (mWatcherList != null) ? mWatcherList.size() : 0;
    }

    @Override
    public Watcher getItem(int position) {
        return (mWatcherList != null && position < mWatcherList.size()) ? mWatcherList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        final Watcher watcher = getItem(position);

        if (convertView == null) {
            // Inflate the view since it does not exist
            convertView = mInflater.inflate(R.layout.watcher_item_layout, parent, false);

            // Create and save off the holder in the tag so we get quick access to inner fields
            // This must be done for performance reasons
            holder = new Holder();

            holder.headimage = (RoundedImageView) convertView.findViewById(R.id.iv_watcher_head_image);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        //String imageUrl = NewsfeedImageHelper.getInstance().getWhiteListUrl(NewsfeedImageHelper.PhotoType.HEAD_ICON_90_90,getItem(position).group_photo);
//        LoadOptions options = new LoadOptions();
//        options.imageOnFail =  R.drawable.newsfeed_round_image_loading_background;
//        options.stubImage =  R.drawable.newsfeed_round_image_loading_background;
//        holder.iv_discover_organization.loadImage(getItem(position).group_photo, options, null);
//
//        holder.iv_discover_organization.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (group != null) {
//                    jump_group_details(group);
//                }
//            }
//        });


        Picasso.with(mContext)
                .load(watcher.url)
                .into(holder.headimage);
        return convertView;
    }



    private static class Holder {
        public RoundedImageView headimage;
    }
}
