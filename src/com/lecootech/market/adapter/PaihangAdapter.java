package com.lecootech.market.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lecootech.market.R;

public class PaihangAdapter extends BaseAdapter {

	private Context context;
	private String[] name = { "今日排行", "本周排行", "本月排行" };
	private int[] image = { R.drawable.htot_day, R.drawable.hot_week,
			R.drawable.hot_month };

	public PaihangAdapter(Context context) {
		this.context = context;
	}

	public int getCount() {
		return name.length;
	}

	public Object getItem(int position) {
		return name[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (view == null) {
			viewHolder = new ViewHolder();
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			view = layoutInflater.inflate(R.layout.software_paihang_item, null);

			viewHolder.name = (TextView) view.findViewById(R.id.category_name);
			viewHolder.icon_image = (ImageView) view
					.findViewById(R.id.category_image);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.name.setText(name[position]);
		viewHolder.icon_image.setBackgroundResource(image[position]);

		return view;

	}

	public class ViewHolder {
		ImageView icon_image;
		TextView name;
	}

}