package com.lecootech.market.adapter;

import java.util.ArrayList;

import android.R.integer;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lecootech.market.R;
import com.lecootech.market.common.Util;
import com.lecootech.market.data.DownloadManagerSendData;
import com.lecootech.market.data.MessageControl;
import com.lecootech.market.data.ThridFloorData;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.DownloadDatabase;
import com.lecootech.market.download.AsyncDownloadingManagerLoader;
import com.lecootech.market.download.DownladUtil;

/**
 * @author zhaweijin
 * @function 软件、壁纸正在下载页面的适配器
 */
public class SoftwareDownloadingAdapter extends BaseAdapter {

	public ArrayList<ThridFloorData> thridFloorDatas;
	private Context context;
	private Activity activity;
	public static String currentPackage;

	private DownloadDatabase downloadDatabase;
	private boolean land;

	private NotificationManager notificationManager;
	private SharedPreferences settingPreferences;

	private ArrayList<Boolean> downloadAgain;

	private ListView listView;

	private AsyncDownloadingManagerLoader asyncDownloadingManagerLoader;
	private ArrayList<DownloadManagerSendData> sendDatas = new ArrayList<DownloadManagerSendData>();

	private final static int stopDownload = 0x122;
	private final static int continueDownload = 0x133;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case stopDownload:

				break;
			case MessageControl.ADAPTER_CHANGE:
				notifyDataSetChanged();
				break;
			}
		}

	};

	public SoftwareDownloadingAdapter(Context context, Activity activity,
			ArrayList<ThridFloorData> thridFloorDatas, boolean land,
			ListView listView) {
		this.context = context;
		this.activity = activity;
		this.land = land;
		this.listView = listView;
		this.thridFloorDatas = thridFloorDatas;
		downloadDatabase = DatabaseManager.getDownloadDatabase(context);
		settingPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		asyncDownloadingManagerLoader = new AsyncDownloadingManagerLoader(
				context);
		downloadAgain = new ArrayList<Boolean>();
		for (int i = 0; i < thridFloorDatas.size(); i++) {
			sendDatas.add(new DownloadManagerSendData(thridFloorDatas.get(i)
					.getName(), context, 1, activity, thridFloorDatas.get(i)
					.getSwid()));
			downloadAgain.add(false);
		}

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
					R.layout.software_downloading_list_item, null);

			viewHolder.layout_image  = (LinearLayout)view.findViewById(R.id.layout_image);
			viewHolder.name = (TextView) view.findViewById(R.id.software_name);
			viewHolder.name.setSelected(true);

			viewHolder.imageView = (ImageView) view
					.findViewById(R.id.software_image);
			viewHolder.progressBar = (ProgressBar) view
					.findViewById(R.id.progress);
			viewHolder.progressBarValue = (TextView) view
					.findViewById(R.id.progress_bar_value);
			
			
			
			
			viewHolder.stopTextView = (TextView)view.findViewById(R.id.download);
			viewHolder.cancelButton = (Button)view.findViewById(R.id.download_cancel);

			
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}


		viewHolder.name.setWidth(getNameWidth(viewHolder.layout_image,
				viewHolder.cancelButton));
		viewHolder.name.setEnabled(true);
		
		final int id = position;
		
		viewHolder.progressBar.setTag(thridFloorDatas.get(id).getSwid());
		viewHolder.imageView.setImageBitmap(BitmapFactory
				.decodeFile(thridFloorDatas.get(position).getImagePath()));

		if (downloadDatabase.getDownloadOperation(thridFloorDatas.get(position)
				.getName()) == 0) {
			viewHolder.stopTextView.setText("暂停");
			viewHolder.stopTextView.getCompoundDrawables()[1].setLevel(0);
			// 利用不同对象固化初始化进度条的值
			
			viewHolder.progressBarValue.setTag(thridFloorDatas.get(id)
					.getName());
			// 通过回调函数显示实时下载进度值

			ContinuteDownload(id, viewHolder.progressBar,
					viewHolder.progressBarValue, downloadAgain.get(id));

		} else if (downloadDatabase.getDownloadOperation(thridFloorDatas.get(
				position).getName()) == -1||downloadDatabase.getDownloadOperation(thridFloorDatas.get(
						position).getName())==-3) {
			viewHolder.stopTextView.setText("继续");
			viewHolder.stopTextView.getCompoundDrawables()[1].setLevel(1);
			int tempValue = downloadDatabase.getDownloadNum(thridFloorDatas
					.get(position).getName());
			viewHolder.progressBar.setProgress(tempValue);
			
			if(tempValue==0)
			{
				viewHolder.progressBarValue.setText("等待中");
			}
			else {
				viewHolder.progressBarValue.setText(Integer.toString(tempValue)
						+ "%");
			}
			
		}

		viewHolder.name.setText(thridFloorDatas.get(id).getName());


		// 操作按钮事件处理

		viewHolder.cancelButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cancelEvent(id);
			}
		});

		

		viewHolder.stopTextView.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				downloadStopContinute(id, v);
			}
		});
		return view;

	}

	public void cancelDownload(final String swid, final String software_name) {
		new Thread(new Runnable() {

			public void run() {
				try {
					// cancel notification
					notificationManager.cancel(Integer.parseInt(swid));

					// delete database
					downloadDatabase.deleteData(software_name);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	public void downloadStopContinute(final int id,View v)
	{
		try {
			
			TextView textView  = (TextView)v;
			if (textView.getText().toString().equals("暂停")) {
				Util.print("zanting-->", thridFloorDatas.get(id)
						.getSwid());
				settingPreferences
						.edit()
						.putBoolean(thridFloorDatas.get(id).getSwid(),
								false).commit();
				new Thread(new Runnable() {

					public void run() {
						try {
							downloadDatabase.updateDownloadOperation(
									thridFloorDatas.get(id).getName(),
									-1);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
				textView.getCompoundDrawables()[1].setLevel(1);
				textView.setText("继续");
			} else if (textView.getText().toString().equals("继续")) {
				Util.print("jixu-->", thridFloorDatas.get(id).getSwid());
				settingPreferences
						.edit()
						.putBoolean(thridFloorDatas.get(id).getSwid(),
								true).commit();
				
				textView.setText("暂停");
				downloadAgain.set(id, true);
				
				DownladUtil.downloadAction(context, thridFloorDatas
						.get(id).getDownloadPath(), thridFloorDatas
						.get(id).getSwid(), thridFloorDatas.get(id)
						.getPackageName(), thridFloorDatas.get(id)
						.getName(), true, thridFloorDatas.get(id)
						.getSize(),handler);
				textView.getCompoundDrawables()[1].setLevel(0);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cancelEvent(int id)
	{
		try {
			cancelDownload(thridFloorDatas.get(id).getSwid(),
					thridFloorDatas.get(id).getName());
			sendDatas.remove(id);
			downloadAgain.remove(id);
			thridFloorDatas.remove(id);
			notifyDataSetChanged();

			// 发送更新总数的广播
			Intent sendIntent = new Intent();
			sendIntent.putExtra("update", "updateCount");
			sendIntent.setAction("com.lecootech.market.updateNum");
			context.sendBroadcast(sendIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public class ViewHolder {
		LinearLayout layout_image;
		ImageView imageView;
		TextView name;
		ProgressBar progressBar;
		RelativeLayout widget47;
		TextView progressBarValue;		
		

		
		TextView stopTextView;
		Button cancelButton;
		

	}

	public Integer ContinuteDownload(final int id, ProgressBar progressBar,
			TextView progressBarValue, Boolean again) {
		
		Integer value = asyncDownloadingManagerLoader.loadInteger(
				sendDatas.get(id),
				new AsyncDownloadingManagerLoader.ImageCallback() {

					public void imageLoaded(Integer imageInteger,
							DownloadManagerSendData sendData) {
						try {
							ProgressBar progressBar1 = (ProgressBar) listView
									.findViewWithTag(thridFloorDatas.get(id)
											.getSwid());
							TextView textView1 = (TextView) listView
									.findViewWithTag(thridFloorDatas.get(id)
											.getName());

							if (progressBar1 != null) {
								if (imageInteger != null) {
									progressBar1.setProgress(imageInteger);
									if(imageInteger==0)
									{
										textView1.setText("等待中");
									}
									else {
										textView1.setText(Integer.toString(imageInteger) + "%");
									}
									
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, again);
		// 显示当前进度条的值
		if (value == null) {
			int par = downloadDatabase.getDownloadNum(thridFloorDatas.get(id)
					.getName());
				progressBar.setProgress(par);
				if(par==0)
				{
					progressBarValue.setText("等待中");
				}
				else {
					progressBarValue.setText(Integer.toString(par) + "%");
				}
				

		} else {
				progressBar.setProgress(value);
				if(value==0)
				{
					progressBarValue.setText("等待中");
				}
				else {
					progressBarValue.setText(Integer.toString(value) + "%");
				}
		}
		return value;
	}
	
	
	
	public int getNameWidth(LinearLayout leftlayout,Button rightButton)
	{
		int leftWidth = leftlayout.getLayoutParams().width;
		int rightWidth = rightButton.getLayoutParams().width;
		
		return Util.getDisplayMetricsWidth(activity) - leftWidth - rightWidth*2 - 10;
	}
}