package com.lecootech.market.adapter;

import java.util.ArrayList;

import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lecootech.market.R;
import com.lecootech.market.common.Util;
import com.lecootech.market.data.CommentData;
import com.lecootech.market.handle.DataSet;

public class SoftwareCommnetAdapter extends BaseAdapter {

	private ArrayList<CommentData> commentDatas;
	private Context context;
	private int currentListSize = 0;
	// private DataSet dataSet;
	private SharedPreferences settingPreferences;
	private int totalCount;

	public SoftwareCommnetAdapter(Context context,
			ArrayList<CommentData> commentDatas,int totalCount) {
		this.context = context;
		this.commentDatas = commentDatas;
		this.totalCount = totalCount;
		settingPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
	}

	public int getCount() {
		return commentDatas.size();
	}

	public Object getItem(int position) {
		return commentDatas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {
		try {
			final ViewHolder viewHolder;
			if (view == null) {
				viewHolder = new ViewHolder();
				LayoutInflater layoutInflater = LayoutInflater.from(context);
				view = layoutInflater.inflate(R.layout.comment_list_item, null);
				viewHolder.comment_linear = (LinearLayout) view
						.findViewById(R.id.comment_linear);
				viewHolder.name = (TextView) view.findViewById(R.id.author);
				viewHolder.time = (TextView) view.findViewById(R.id.time);
				viewHolder.contain = (TextView) view
						.findViewById(R.id.comment_body);
				viewHolder.ratingBar = (RatingBar) view
						.findViewById(R.id.rating);
				viewHolder.commnet_image = (TextView) view
						.findViewById(R.id.commnet_image);

				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
		
			viewHolder.time.setSelected(true);
			// if(settingPreferences.getString("user", "").equals(""))
			// viewHolder.name.setText("匿名用户("+commentDatas.get(position).getName()+") 评论");
			// else {
			viewHolder.name.setText("["+commentDatas.get(position).getName()+"]");
			// }
			// Util.print("bar", commentDatas.get(position).getRatin());
			viewHolder.ratingBar.setRating(Integer.parseInt(commentDatas.get(
					position).getRatin()));
			viewHolder.contain.setText(commentDatas.get(position).getContain());
			
			viewHolder.time.setText("发表于"+commentDatas.get(position).getTime()
					.substring(0, commentDatas.get(position).getTime().lastIndexOf(":")));
			if (1 == totalCount - position) {
				
				viewHolder.commnet_image.setTextColor(context.getResources().getColor(R.color.shafa));
				viewHolder.commnet_image.setText("沙发");
				
			} else if (2 == totalCount - position) {
				viewHolder.commnet_image.setTextColor(context.getResources().getColor(R.color.pandeng));
				viewHolder.commnet_image.setText("板凳");
				
			} else if (3 == totalCount - position) {
				viewHolder.commnet_image.setTextColor(context.getResources().getColor(R.color.deban));
				viewHolder.commnet_image.setText("地板");
			} else {
				viewHolder.commnet_image.setTextColor(context.getResources().getColor(R.color.comment_other_textcolor));
				viewHolder.commnet_image.setText(totalCount - position + " 楼");
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			Util.print("error", "error");
		}

		return view;

	}

	public class ViewHolder {
		TextView name;
		TextView time;
		RatingBar ratingBar;
		TextView contain;
		TextView commnet_image;
		LinearLayout comment_linear;

	}

}