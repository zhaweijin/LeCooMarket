package com.lecootech.market.adapter;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lecootech.market.InformationTabHost;
import com.lecootech.market.R;
import com.lecootech.market.SoftwareManagerActivity;
import com.lecootech.market.common.Util;
import com.lecootech.market.data.ImageData;
import com.lecootech.market.data.ManagerFloorData;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.DownloadDatabase;
import com.lecootech.market.database.UpdateManagerDatabase;
import com.lecootech.market.download.AsyncDownloadLoader;
import com.lecootech.market.download.DownladUtil;
import com.lecootech.market.download.AsyncDownloadLoader.progressCallback;

public class SoftwareManagerAdapter extends BaseAdapter {

	public ArrayList<ManagerFloorData> managerFloorDatas;
	private static final String SCHEME = "package";
	 boolean managersize=true;
	 int managersum = 1;
	/**
	 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
	 */
	private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
	/**
	 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
	 */
	private static final String APP_PKG_NAME_22 = "pkg";
	/**
	 * InstalledAppDetails所在包名
	 */
	private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
	/**
	 * InstalledAppDetails类名
	 */
	private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
	public Context context;
	public Activity activity;
	public static String currentPackage;
	public DownloadDatabase downloadDatabase;
	public UpdateManagerDatabase updateManagerDatabase;
	private SharedPreferences settingPreferences;
	public boolean land;
	public SoftwareManagerActivity managerActivity;
	private AsyncDownloadLoader asyncDownloadLoader;
	private ListView listView;
	public int adapterFlag = 1; // 1:has installl 2:has update
	public boolean installlocation = true;
	private Handler handler;
	ViewHolder viewHolder = null;

	public void setSizes() {
		if (managerFloorDatas == null || managerFloorDatas.size() == 0) {
			return;
		} else {
			for (int i = 0; i < managerFloorDatas.size(); i++) {
				getpkginfo(managerFloorDatas.get(i).getPackageName(), context);
			}
		}

	}

	public SoftwareManagerAdapter(Context context, Activity activity,
			ArrayList<ManagerFloorData> managerFloorDatas, boolean land,
			int adapterFlag, boolean installlocation,ListView listView,
			Handler handler) {

		this.context = context;
		this.activity = activity;
		this.listView = listView;
		this.handler = handler;
		this.managerActivity = (SoftwareManagerActivity) activity;
		this.land = land;
		this.adapterFlag = adapterFlag;
		this.installlocation = installlocation;
		this.managerFloorDatas = managerFloorDatas;
		downloadDatabase = DatabaseManager.getDownloadDatabase(context);
		updateManagerDatabase = DatabaseManager
				.getUpdateManagerDatabase(context);
        settingPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        asyncDownloadLoader = new AsyncDownloadLoader(context);
		setSizes();
	}

	public int getCount() {
		return managerFloorDatas.size();
	}

	public Object getItem(int position) {
		return managerFloorDatas.get(position).getName();
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {

		try {

			if (view == null) {

				viewHolder = new ViewHolder();
				LayoutInflater layoutInflater = LayoutInflater.from(context);
				view = layoutInflater.inflate(
						R.layout.software_manager_normal_item, null);
				viewHolder.name = (TextView) view
						.findViewById(R.id.software_name);
				viewHolder.name.setSelected(true);
				
				viewHolder.icon_image = (ImageView) view
						.findViewById(R.id.software_image);
	            viewHolder.layout_image = (LinearLayout)view.findViewById(R.id.layout_image);


                viewHolder.software_size = (TextView) view.findViewById(R.id.software_size);
                viewHolder.updata_version = (TextView) view.findViewById(R.id.new_version);
                
				viewHolder.updateTextView = (TextView)view.findViewById(R.id.update);

				
				viewHolder.uninstallButton = (Button)view.findViewById(R.id.uninstall);
				viewHolder.moreTextView = (TextView)view.findViewById(R.id.move);
				viewHolder.updateButton = (Button)view.findViewById(R.id.update);
				viewHolder.ignoreButton = (Button)view.findViewById(R.id.ignore);
				
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

		    viewHolder.name.setWidth(getNameWidth(viewHolder.layout_image,
		    		viewHolder.uninstallButton));

			viewHolder.icon_image.setBackgroundDrawable(managerFloorDatas.get(
					position).getDrawable());
			viewHolder.name.setText(managerFloorDatas.get(position).getName());


			if (adapterFlag == 2) {
				viewHolder.updata_version.setVisibility(View.INVISIBLE);
				viewHolder.software_size.setText("新版本:"
						+ Util.updateManagerDatas.get(position).getVersion());
				viewHolder.moreTextView.setVisibility(View.INVISIBLE);
			}
			else if(adapterFlag == 1)
			{
				viewHolder.updata_version.setVisibility(View.VISIBLE);
				viewHolder.updata_version.setText(managerFloorDatas.get(position).getVersion());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		viewHolder.software_size.setSelected(true);
		viewHolder.updata_version.setSelected(true);

		// init name
		if (adapterFlag == 1) {
			
			viewHolder.uninstallButton.setVisibility(View.VISIBLE);
			viewHolder.updateButton.setVisibility(View.INVISIBLE);
			viewHolder.ignoreButton.setVisibility(View.INVISIBLE);

			
			//管理图标显示
			if(managerFloorDatas.get(position).getinstalllocation())
			{
				viewHolder.moreTextView.getCompoundDrawables()[1].setLevel(1);
				viewHolder.moreTextView.setText("可移动");
			}
			else {
				viewHolder.moreTextView.getCompoundDrawables()[1].setLevel(0);
				viewHolder.moreTextView.setText("管理");
			}
			
			
			while (managersize) {
				if (null == managerFloorDatas.get(position).getSize()) {
					managersum++;
					if (managersum > 500) {
						managersize = false;
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					viewHolder.software_size.setText(managerFloorDatas.get(position).getSize());
					break;
				}

			}

		} else if (adapterFlag == 2) {
			viewHolder.uninstallButton.setVisibility(View.INVISIBLE);
			viewHolder.updateButton.setVisibility(View.VISIBLE);
		    viewHolder.ignoreButton.setVisibility(View.VISIBLE);
			viewHolder.updata_version.setVisibility(View.VISIBLE);
//			viewHolder.version.setVisibility(View.VISIBLE);
			
		} else if (adapterFlag == 3) {

		}


		final int currentPosition = position;
		
		
		

		
		if(adapterFlag==2)
		{
			String displayUpdateString = getdownloadButtonDisplay(managerFloorDatas.get(currentPosition).getPackageName());
			viewHolder.updateTextView.setText(displayUpdateString);
			
			final ImageData data = new ImageData();
			if(Util.updateManagerDatas.size()>0)
			{
				data.setName(Util.updateManagerDatas.get(position).getName());
				data.setSwid("software"+ Util.updateManagerDatas.get(position).getSwid());
				viewHolder.updateTextView.setTag(data.getSwid());
			}
			
			if(displayUpdateString.equals("更新中"))
			{
//				Util.print("-------", "--------");
				final String progress = asyncDownloadLoader.loadProgress(activity, data, new progressCallback() {
					

					public void progressLoaded(String progress, String imageUrl) {
						// TODO Auto-generated method stub
						TextView downloadView = (TextView)listView.findViewWithTag(data.getSwid());
						if(downloadView!=null && progress!=null)
						{
							if(progress.equals("0"))
							{
								downloadView.setText("等待中");
							}
							else {
								downloadView.setText(progress+"%");
							}
						}
					}
				});
				
				if(progress==null)
				{
					viewHolder.updateTextView.setText("更新中");
				}
				else {
					if(progress.equals("0"))
					{
						viewHolder.updateTextView.setText("等待中");
					}
					else {
						viewHolder.updateTextView.setText(progress+"%");
					}
				}
			}
		}

		//uninstall button
		viewHolder.uninstallButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				uninstallEvent(currentPosition);
			}
		});
	
		
		//update button

		viewHolder.updateButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Util.print("+++", v.getParent().toString());
				updateEvent(currentPosition, v);
			      //view 	
			}
		});
		
		//ignore button
		viewHolder.ignoreButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ignoreEvent(currentPosition);
			}
		});
		

		//move button
        viewHolder.moreTextView.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				moveEvent(currentPosition);
			}
		});
       
        
		


		viewHolder.updateButton.setFocusable(false);
		viewHolder.uninstallButton.setFocusable(false);
		viewHolder.ignoreButton.setFocusable(false);
        
		return view;

	}

	public void uninstallPkg(String packagename) {
		Uri packageURI = Uri.parse("package:" + packagename);
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		activity.startActivityForResult(uninstallIntent,
				SoftwareManagerActivity.UN_INSTALL);
	}

	public class ViewHolder {
		LinearLayout layout_image;
		ImageView icon_image;
		TextView name;
		TextView updata_version;

		
		TextView moreTextView;
	    Button uninstallButton;
		Button updateButton;
		Button ignoreButton;
		
		TextView updateTextView;
		TextView software_size;
	}

	public void changeItemNameWidthFlag(boolean direction) {
		land = direction;
		notifyDataSetChanged();
	}

	public int getManagerUpdatePosition(int id, String packagename) {
		int temp_id = -1;
		int size = Util.updateManagerDatas.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (managerFloorDatas.get(id).getPackageName()
						.equals(packagename)) {
					temp_id = i;
					break;
				}
			}
		}
		return temp_id;
	}

	/*
	 * 下载按钮显示的名称 下载、打开、更新
	 */
	public String getdownloadButtonDisplay(String PackageName) {
		Cursor temCursor = downloadDatabase.queryByPackage(PackageName);
		int returnvalue = -1;
		int downloadFlag = 0;

		int size = temCursor.getCount();
		temCursor.moveToFirst();
		if (size > 0) {
			returnvalue = temCursor.getInt(temCursor
					.getColumnIndexOrThrow(DownloadDatabase.KEY_DOWNLOND_NUM));
			downloadFlag = temCursor.getInt(temCursor
					.getColumnIndexOrThrow(DownloadDatabase.KEY_OPERATION));
		}

		temCursor.close();

		if (returnvalue == 100) {
			return "安装";
		}
		else if (returnvalue != -1 && downloadFlag == 0) {
			return "更新中";
		}
		else if (returnvalue != -1 && downloadFlag != 0) {
			return "继续";
		} else {
			return "更新";
		}
	}

	public void getpkginfo(String pkg, Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			Method getPackageSizeInfo = pm.getClass().getMethod(
					"getPackageSizeInfo", String.class,
					IPackageStatsObserver.class);
			getPackageSizeInfo.invoke(pm, pkg, new PkgSizeObserver());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class PkgSizeObserver extends IPackageStatsObserver.Stub {
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) {
			String infoString = "";
			if (pStats != null) {

				infoString = Util.formatFileSize(pStats.codeSize);
				for (int j = 0; j < managerFloorDatas.size(); j++) {
					if (managerFloorDatas.get(j).getPackageName()
							.equals(pStats.packageName)) {
						managerFloorDatas.get(j).setSize(infoString);

					}

				}
			}
		}
	}

	public void uninstallEvent(int currentPosition)
	{
		if (adapterFlag == 3) {
			Intent intent = new Intent();
			final int apiLevel = Build.VERSION.SDK_INT;
			if (apiLevel >= 9) {
				intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				Uri uri = Uri.fromParts(SCHEME,
						managerFloorDatas.get(currentPosition)
								.getPackageName(), null);
				intent.setData(uri);
			} else {
				final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
						: APP_PKG_NAME_21);
				intent.setAction(Intent.ACTION_VIEW);
				intent.setClassName(APP_DETAILS_PACKAGE_NAME,
						APP_DETAILS_CLASS_NAME);
				intent.putExtra(appPkgName, managerFloorDatas
						.get(currentPosition).getPackageName());
			}
			context.startActivity(intent);
		} else {
			try {
				currentPackage = managerFloorDatas.get(currentPosition)
						.getPackageName();
				uninstallPkg(currentPackage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void updateEvent(int currentPosition,View v)
	{
		try {
			
			Button view = (Button) v;

			if (view.getText().toString().equals("更新")
					|| view.getText().toString().equals("继续")) {
				int temp_id = -1;
				int size = Util.updateManagerDatas.size();
				if (size > 0) {
					for (int i = 0; i < size; i++) {
						if (managerFloorDatas
								.get(currentPosition)
								.getPackageName()
								.equals(Util.updateManagerDatas.get(i)
										.getPackageName())) {
							temp_id = i;
							break;
						}
					}
					if (temp_id != -1) {
						if (!Util.checkIsDownloading(context,
								Util.updateManagerDatas.get(temp_id)
										.getName())) {
							
							//init download state
							settingPreferences.edit().putBoolean(Util.updateManagerDatas
									.get(temp_id).getSwid(),true).commit();
							view.setText("更新中");

							
							DownladUtil.downloadAction(context,
									Util.updateManagerDatas
											.get(temp_id).getapkPath(),
									Util.updateManagerDatas
											.get(temp_id).getSwid(),
									Util.updateManagerDatas
											.get(temp_id)
											.getPackageName(),
									Util.updateManagerDatas
											.get(temp_id).getName(),
									true,
									Util.updateManagerDatas
											.get(temp_id).getSize(),
											handler);
							
							managerActivity.setHasupdateTextView();
						} else {
							Toast.makeText(
									context,
									Util.updateManagerDatas
											.get(temp_id).getName()
											+ "正在更新中...", 1000).show();
						}
					}
				}

			} else if (view.getText().toString().equals("安装")) {

				int temp_id = -1;
				int size = Util.updateManagerDatas.size();
				if (size > 0) {
					for (int i = 0; i < size; i++) {
						if (managerFloorDatas
								.get(currentPosition)
								.getPackageName()
								.equals(Util.updateManagerDatas.get(i)
										.getPackageName())) {
							temp_id = i;
							break;
						}
					}
					if (temp_id != -1) {
						Util.print("we",
								Util.updateManagerDatas.get(temp_id)
										.getapkPath());
						String[] fileName = Util
								.fileName(Util.updateManagerDatas.get(
										temp_id).getapkPath());
						String filepath = Util.getStoreApkPath()
								+ fileName[1] + "." + fileName[0];
						DownladUtil.installFile(new File(filepath),
								context);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void ignoreEvent(int currentPosition)
	{
		updateManagerDatabase.insertData(managerFloorDatas.get(currentPosition)
				.getPackageName());
		managerFloorDatas.remove(currentPosition);
		notifyDataSetChanged();
		managerActivity.setHasupdateTextView();
	}
	
	
	
	public void moveEvent(int currentPosition)
	{
		Util.print("--", currentPosition+"--"+Boolean.toString(managerFloorDatas.get(currentPosition).getinstalllocation()));
//		if (managerFloorDatas.get(currentPosition).getinstalllocation()) {
			Intent intent = new Intent();
			final int apiLevel = Build.VERSION.SDK_INT;
			if (apiLevel >= 9) {
				intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				Uri uri = Uri.fromParts(SCHEME,
						managerFloorDatas.get(currentPosition).getPackageName(),
						null);
				intent.setData(uri);
			} else {
				final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
						: APP_PKG_NAME_21);
				intent.setAction(Intent.ACTION_VIEW);
				intent.setClassName(APP_DETAILS_PACKAGE_NAME,
						APP_DETAILS_CLASS_NAME);
				intent.putExtra(appPkgName, managerFloorDatas.get(currentPosition)
						.getPackageName());
			}
			context.startActivity(intent);
//		}
//		else {
//			
//		}
	}
	
	
	
	public int getNameWidth(LinearLayout leftlayout,Button rightButton)
	{
		int leftWidth = leftlayout.getLayoutParams().width;
		int rightWidth = rightButton.getLayoutParams().width;
		
		return Util.getDisplayMetricsWidth(activity) - leftWidth - rightWidth*2 - 10;
	}
	
	
	
}