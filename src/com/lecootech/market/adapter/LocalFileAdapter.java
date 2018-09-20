package com.lecootech.market.adapter;

import java.io.File;
import java.util.ArrayList;

import android.R.integer;
import android.R.raw;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lecootech.market.R;
import com.lecootech.market.SoftwareManagerActivity;
import com.lecootech.market.common.Util;
import com.lecootech.market.data.LocalApkData;


public class LocalFileAdapter extends BaseAdapter {

	private ArrayList<LocalApkData> localApkDatas;
	private Context context;
	private SharedPreferences settingPreferences;
	private Activity activity;

	private boolean land;

	public LocalFileAdapter(Context context, Activity activity,
			ArrayList<LocalApkData> localApkDatas, boolean land) {
		this.context = context;
		this.activity = activity;
		this.localApkDatas = localApkDatas;
		this.land = land;
		settingPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

	}

	public int getCount() {
		return localApkDatas.size();
	}

	public Object getItem(int position) {
		return localApkDatas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (view == null) {
			viewHolder = new ViewHolder();
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			view = layoutInflater.inflate(
					R.layout.software_manager_localfile_item, null);

			viewHolder.name = (TextView) view.findViewById(R.id.software_name);
			viewHolder.name.setSelected(true);
			viewHolder.size = (TextView) view.findViewById(R.id.software_size);
			viewHolder.icon_image = (ImageView) view
					.findViewById(R.id.software_image);

            viewHolder.layout_image = (LinearLayout)view.findViewById(R.id.layout_image);
            viewHolder.new_version = (TextView)view.findViewById(R.id.new_version);
			
			viewHolder.delete = (Button)view.findViewById(R.id.delete);
			viewHolder.install = (TextView)view.findViewById(R.id.install);
			

			
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.new_version.setSelected(true);
		viewHolder.name.setWidth(getNameWidth(viewHolder.layout_image, viewHolder.delete));
		
		viewHolder.delete.setFocusable(false);
		viewHolder.name.setEnabled(true);
		

		if (settingPreferences.getBoolean("checkbox_display_icons", true)) {
			if(localApkDatas.get(position).getDrawable()!=null)
			    viewHolder.icon_image.setBackgroundDrawable(localApkDatas.get(
					position).getDrawable());
			else {
				viewHolder.icon_image.setImageResource(R.drawable.icon2);
			}

		} else {
			viewHolder.icon_image.setImageResource(R.drawable.icon2);
		}

		try {

			final int id = position;

			// viewHolder.name.setWidth(Util.getItemNameWidth(context, land));
			viewHolder.new_version.setText(localApkDatas.get(position).getVersion());
//			viewHolder.new_version.setText("caodfadf.sdfsafdsf.wefwfwefwf");
			viewHolder.name.setText(localApkDatas.get(position).getName());
			viewHolder.size.setText("大小:"
					+ localApkDatas.get(position).getSize());
			String filepathString = localApkDatas.get(position).getFilepath();
			if (filepathString != null)
//				viewHolder.version.setText("版本:"
//						+ localApkDatas.get(position).getVersion());

			if (Util.checkPackageIsExist(context, localApkDatas.get(position)
					.getPackageName())) {
				viewHolder.install.setText("已安装");
				viewHolder.install.setTextColor(R.color.list_btn);
				viewHolder.install.getCompoundDrawables()[1].setLevel(3);
			} else {
				viewHolder.install.setText("安装");
				viewHolder.install.setTextColor(Color.BLACK);
				viewHolder.install.getCompoundDrawables()[1].setLevel(2);

			}

			viewHolder.install.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					
					openFile(v,localApkDatas.get(id).getFilepath());
					
				}
			});
           
			
			

			viewHolder.delete.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					checkIsDelete(id);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		return view;

	}

	public class ViewHolder {
		LinearLayout layout_image;
		ImageView icon_image;
		TextView name;
		TextView size;
		TextView version;


		
		
		Button delete;
		TextView install;
        TextView new_version;
	}

	
	
	
	public void openFile(View v,String filepath) {

		try {
			TextView textView = (TextView)v;
			if (textView.getText().equals("安装")) {
				
				File file = new File(filepath);
				if (!file.exists())
					Toast.makeText(context, "文件不存在", Toast.LENGTH_LONG);
				else {
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setAction(android.content.Intent.ACTION_VIEW);
					String type = Util.getMIMEType(file);
					intent.setDataAndType(Uri.fromFile(file), type);
					activity.startActivityForResult(intent, 2);

				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	public void checkIsDelete(final int id )
	{
		new AlertDialog.Builder(activity.getParent())
		.setIcon(R.drawable.report_error)
		.setTitle("删除提示")
		.setMessage(
				"确定要删除【" + localApkDatas.get(id).getName()
						+ "】？")
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {

					public void onClick(
							DialogInterface dialog,
							int which) {
						Util.deleteFile(new File(
								localApkDatas.get(id)
										.getFilepath()));
						clearOneData(id);
					}
				})
		.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {

					public void onClick(
							DialogInterface dialog,
							int which) {
					}
				}).show();
	}
	
	
	public void changeItemNameWidthFlag(boolean direction) {
		land = direction;
		notifyDataSetChanged();
	}

	public void clearOneData(int id) {
		localApkDatas.remove(id);
		int size = localApkDatas.size();
		notifyDataSetChanged();
		SoftwareManagerActivity.Move.setText("本地APK(" + size + ")");
	}
	
	public int getNameWidth(LinearLayout leftlayout,Button rightButton)
	{
		int leftWidth = leftlayout.getLayoutParams().width;
		int rightWidth = rightButton.getLayoutParams().width;
		
		return Util.getDisplayMetricsWidth(activity) - leftWidth - rightWidth*2 - 10;
	}
}