package com.iradetskiy.vkaudioplayer.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iradetskiy.vkapi.VKAudioItem;
import com.iradetskiy.vkaudioplayer.R;

public abstract class AudioAdapter extends BaseAdapter{
	
	private int mItemLayoutId = R.layout.music_list_item;
	protected List<VKAudioItem> mAudioList;
	protected Activity mActivity;
	
	@Override
	public int getCount() {
		return mAudioList.size();
	}

	@Override
	public Object getItem(int position) {
		return mAudioList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return Long.parseLong(mAudioList.get(position).aid);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = null;

		LayoutInflater inflater = mActivity.getLayoutInflater();

		v = inflater.inflate(mItemLayoutId, null);

		TextView artist = (TextView) v.findViewById(R.id.artist);
		TextView title = (TextView) v.findViewById(R.id.title);
		TextView duration = (TextView) v.findViewById(R.id.duration);

		VKAudioItem item = mAudioList.get(position);
		artist.setText(item.artist);
		title.setText(item.title);
		duration.setText(item.duration);
		
		if ((position) / (getCount() - 1) > 0.75) {
			loadNext();
		}

		return v;
	}
	
	protected abstract void loadNext();	
}
