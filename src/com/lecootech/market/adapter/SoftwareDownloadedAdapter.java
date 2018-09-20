package com.lecootech.market.adapter;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.lecootech.market.R;
import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.data.ThridFloorData;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.DownloadDatabase;

//需要修改
/**
 * @author zhaweijin
 * @function 更新页数据的适配器
 */
public class SoftwareDownloadedAdapter extends BaseAdapter {

	public ArrayList<ThridFloorData> thridFloorDatas;
	private Context context;
	private Activity activity;
	public static String currentPackage;
	private DownloadDatabase downloadDatabase;
	private boolean land;
	public SoftwareDownloadedAdapter(Context context, Activity activity,
			ArrayList<ThridFloorData> thridFloorDatas, boolean land) {
		this.context = context;
		this.activity = activity;
		this.land = land;

		this.thridFloorDatas = thridFloorDatas;

		downloadDatabase = DatabaseManager.getDownloadDatabase(context);
	}

	public int getCount() {
		return thridFloorDatas.size();
	}

	public Object getItem(int position) {
		return thridFloorDatas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (view == null) {
			viewHolder = new ViewHolder();
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(
					R.layout.software_downloaded_list_item, null);

			viewHolder.layout_image = (LinearLayout)view.findViewById(R.id.layout_image);
			viewHolder.name = (TextView) view.findViewById(R.id.software_name);
			viewHolder.name.setSelected(true);
		
			viewHolder.icon_image = (ImageView) view
					.findViewById(R.id.software_image);

			
			viewHolder.install = (TextView)view.findViewById(R.id.install);
			viewHolder.delete = (Button)view.findViewById(R.id.delete);
			
			
			viewHolder.software_size = (TextView)view.findViewById(R.id.software_updata_version);
			
			DisplayMetrics localDisplayMetrics = context.getResources()
					.getDisplayMetrics();
			int width = localDisplayMetrics.widthPixels;
			// 修改按钮的大小
//			LayoutParams editPara = viewHolder.installButton.getLayoutParams();
//			if (width>320) {
//				editPara.width = 80;
//			}else{
//				editPara.width = 60;
//			}
//			
//			viewHolder.installButton.setLayoutParams(editPara);
//			// viewHolder.updateButton = (Button)view.findViewById(R.id.update);
//			viewHolder.moveButton = (Button) view.findViewById(R.id.move);
//			LayoutParams editParab = viewHolder.moveButton.getLayoutParams();
//			if (width>320) {
//				editParab.width = 80;
//			}else{
//				editParab.width = 60;
//			}
//			
//			viewHolder.moveButton.setLayoutParams(editParab);
//			// viewHolder.ignoreButton = (Button)view.findViewById(R.id.ignore);
			view.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.name.setWidth(getNameWidth(viewHolder.layout_image, 
				viewHolder.delete));
		
		viewHolder.name.setEnabled(true);
		viewHolder.delete.setFocusable(false);
		

		viewHolder.software_size.setText("大小:" + thridFloorDatas.get(position).getSize());
		
		

		viewHolder.install.getCompoundDrawables()[1].setLevel(2);
		

		viewHolder.icon_image.setImageBitmap(BitmapFactory
				.decodeFile(thridFloorDatas.get(position).getImagePath()));
		viewHolder.name.setText(thridFloorDatas.get(position).getName());
		


		final int id = position;
		viewHolder.install.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				try {
					openFile(thridFloorDatas.get(id).getDownloadPath());

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	

		viewHolder.delete.setOnClickListener(new View.OnClickListener() {


			public void onClick(View v) {
				deleteEvent(id);
			}
		});
		

		return view;

	}

	public class ViewHolder {
		LinearLayout layout_image;
		ImageView icon_image;
		TextView name;

		TextView software_size;



		TextView install;
		Button delete;		
	}

	
	public void deleteEvent(final int id)
	{
		new AlertDialog.Builder(activity.getParent())
		.setIcon(R.drawable.report_error)
		.setTitle("删除提示")
		.setMessage(
				"确定要删除【" + thridFloorDatas.get(id).getName()
						+ "】？")
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {


					public void onClick(DialogInterface dialog,
							int which) {
						NotificationManager notificationManager = (NotificationManager) context
								.getSystemService(Context.NOTIFICATION_SERVICE);
						
						notificationManager.cancel(SharedPrefsUtil.getValue(context, thridFloorDatas.get(id).getName(), 0));
						downloadDatabase
								.deleteData(thridFloorDatas
										.get(id).getName());
					    
						Util.deleteFile(new File(
								thridFloorDatas.get(id)
										.getDownloadPath()));
						thridFloorDatas.remove(id);

						sendMyBroadcast();
						notifyDataSetChanged();
					}
				})
		.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {


					public void onClick(DialogInterface dialog,
							int which) {
					}
				}).show();


	}
	
	public void openFile(String filepath) {

		try {
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
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int getSwid(String path) {
		String swid = path.substring(path.lastIndexOf("/") + 9, path.length());
		return Integer.parseInt(swid);
	}

	public void sendMyBroadcast() {
		Intent intent = new Intent("com.lecootech.market.updateNum");
		intent.putExtra("update", "updateCount");
		context.sendBroadcast(intent);
	}
	
	
	public int getNameWidth(LinearLayout leftlayout,Button rightButton)
	{
		int leftWidth = leftlayout.getLayoutParams().width;
		int rightWidth = rightButton.getLayoutParams().width;
		
		return Util.getDisplayMetricsWidth(activity) - leftWidth - rightWidth*2 - 10;
	}
}