package com.lecootech.market.adapter;

import java.io.File;
import java.util.ArrayList;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.lecootech.market.R;
import com.lecootech.market.common.Util;
import com.lecootech.market.data.ImageData;
import com.lecootech.market.data.ThridFloorData;
import com.lecootech.market.database.CollectionDatabase;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.DownloadDatabase;
import com.lecootech.market.download.AsyncDownloadLoader;
import com.lecootech.market.download.DownladUtil;
import com.lecootech.market.download.IconAsyncImageLoader;
import com.lecootech.market.download.AsyncDownloadLoader.progressCallback;
import com.lecootech.market.download.IconAsyncImageLoader.ImageCallback;

public class SoftwareCollectionAdapter extends BaseAdapter {

	private ArrayList<ThridFloorData> threFloorDatas;
	private Context context;
	private SharedPreferences settingPreferences;
	private Activity activity;
	private DownloadDatabase downloadDatabase;
	private CollectionDatabase collectionDatabase;
	private IconAsyncImageLoader iconAsyncImageLoader;
	private AsyncDownloadLoader asyncDownloadLoader;
	private PackageManager packageManager;
	private boolean land;
	private ListView listView;
	private Handler handler;

	public SoftwareCollectionAdapter(Context context, Activity activity,
			ArrayList<ThridFloorData> threFloorDatas, boolean land,
			ListView listView,Handler handler) {
		this.context = context;
		this.activity = activity;
		this.threFloorDatas = threFloorDatas;
		this.land = land;
		this.listView = listView;
		this.handler = handler;
		settingPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		downloadDatabase = DatabaseManager.getDownloadDatabase(context);
		collectionDatabase = DatabaseManager.getCollectionDatabase(context);
		iconAsyncImageLoader = new IconAsyncImageLoader();
        asyncDownloadLoader = new AsyncDownloadLoader(context);
		packageManager = context.getPackageManager();

	}


	public int getCount() {
		return threFloorDatas.size();
	}

	public Object getItem(int position) {
		return threFloorDatas.get(position).getName();
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
					R.layout.software_collection_item, null);

			viewHolder.layout_image = (LinearLayout)view.findViewById(R.id.layout_image);
			viewHolder.name = (TextView) view.findViewById(R.id.software_name);
			viewHolder.name.setSelected(true);
			viewHolder.size = (TextView) view.findViewById(R.id.software_size);
			viewHolder.icon_image = (ImageView) view
					.findViewById(R.id.software_image);
			viewHolder.ratingBar = (RatingBar) view.findViewById(R.id.rating);
			viewHolder.download = (TextView) view.findViewById(R.id.download);

			viewHolder.clearButton = (Button)view.findViewById(R.id.clear);
			view.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		viewHolder.name.setWidth(getNameWidth(viewHolder.layout_image, 
				viewHolder.download));
		
		viewHolder.clearButton.setFocusable(false);
		viewHolder.name.setSelected(true);
		Util.print("init data", "init data");
		//init data
		
		final ImageData data = new ImageData();
		data.setActivity(activity);
		data.setPath(threFloorDatas.get(position).getImagePath().replaceAll(" ", "%20"));
		data.setName(threFloorDatas.get(position).getName());
		data.setParentName("");
		data.setBigOrSmall(false);
		data.setDate(threFloorDatas.get(position).getModifyData());
		data.setSwid("software"+ threFloorDatas.get(position).getSwid());
		data.setImageView(viewHolder.icon_image);
		
		
		if (settingPreferences.getBoolean("checkbox_display_icons", true)) {
			try {
					viewHolder.icon_image.setTag(data.getPath());
					
					final Bitmap bitmap = iconAsyncImageLoader.loadBitmap(activity, data, new ImageCallback() {
						

						public void imageLoaded(Bitmap imageBitmap, String imageUrl) {
							// TODO Auto-generated method stub
							ImageView imageView = (ImageView)listView.findViewWithTag(data.getPath());
							if(imageView!=null && imageBitmap!=null)
							  imageView.setImageBitmap(imageBitmap);
						}
					});
					
					if(bitmap==null)
					{
						Util.print("bitmap null", "bitmap null");
						viewHolder.icon_image.setImageResource(R.drawable.icon2);
					}
					else {
						Util.print("bitmap not null", "bitmap not null");
						viewHolder.icon_image.setImageBitmap(bitmap);
					}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			viewHolder.icon_image.setImageResource(R.drawable.icon2);
		}

		try {
			String displayname = getdownloadButtonDisplay(
					threFloorDatas.get(position).getName(),
					threFloorDatas.get(position).getPackageName());
			viewHolder.download.setText(displayname);
			Util.print("displayname", displayname);
			viewHolder.download.setTag(data.getSwid());
			
			
			if (displayname.equals("已安装")) {
				viewHolder.download.getCompoundDrawables()[1].setLevel(3);
				
				
				if(packageManager.getPackageInfo(
						threFloorDatas.get(position).getPackageName(), 0).versionName!=null)
				{
					if(Util.compareVersions(threFloorDatas.get(position).getVersion(),
							packageManager.getPackageInfo(
									threFloorDatas.get(position).getPackageName(), 0).versionName))
					{
						viewHolder.download.getCompoundDrawables()[1].setLevel(4);
						viewHolder.download.setText("更新");
					}
				}
				
			} else if(displayname.equals("免费")||displayname.equals("继续")){
				viewHolder.download.getCompoundDrawables()[1].setLevel(0);
			}
			else if(displayname.equals("安装"))
			{
				viewHolder.download.getCompoundDrawables()[1].setLevel(2);
			}
			else if(displayname.equals("下载中"))
			{

//				Util.print("-------", "--------");
				
				viewHolder.download.getCompoundDrawables()[1].setLevel(1);
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
							else if(progress.equals("继续"))
							{
								downloadView.setText(progress);
							}
							else {
								downloadView.setText(progress+"%");
							}
						}
							
					}
				});
				
				if(progress==null)
				{
					viewHolder.download.setText("下载中");
				}
				else {
					if(progress.equals("0"))
					{
						viewHolder.download.setText("等待中");
					}
					else {
						viewHolder.download.setText(progress+"%");
					}
				}
			}
			
			
			
			
			
		
			
			
			
			final int id = position;
			viewHolder.download.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					downloadEvent(id, v);
				}
			});
            
			

			viewHolder.clearButton.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						collectionDatabase.deleteDataByName(threFloorDatas.get(
								id).getName());
						clearOneData(id);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			// Util.print("maxlength", Util.getItemNameWidth(context, land)+"");
			// Util.print("set value",
			// Boolean.toString(preferences.getBoolean("checkbox_buffer_icons",
			// false)));
			// viewHolder.name.setMaxWidth(Util.getItemNameWidth(context,
			// land));
			viewHolder.name.setWidth(Util.getItemNameWidth(context, land));

			viewHolder.name.setText(threFloorDatas.get(position).getName());
			viewHolder.size.setText(threFloorDatas.get(position).getSize());

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

		RatingBar ratingBar;
		
		
		
		TextView download;
		Button clearButton;
	}

	
	public void downloadEvent(int id,View v)
	{
		TextView downloadText = (TextView) v;
		if (downloadText.getText().toString().equals("免费")
				|| downloadText.getText().toString().equals("继续")
				|| downloadText.getText().toString().equals("更新")) {
			downloadText.setText("下载中");
			
			
			//init download state
			settingPreferences.edit().putBoolean(threFloorDatas.get(id).getSwid(),
					true).commit();
			
			
			notifyDataSetChanged();
			DownladUtil.downloadAction(context,
					threFloorDatas.get(id).getDownloadPath(),
					threFloorDatas.get(id).getSwid(),
					threFloorDatas.get(id).getPackageName(),
					threFloorDatas.get(id).getName(), false,
					threFloorDatas.get(id).getSize(),handler);
			

		} else if (downloadText.getText().toString().equals("安装")) {
			String[] fileName = Util.fileName(threFloorDatas
					.get(id).getDownloadPath());
			String filepath = Util.getStoreApkPath() + fileName[1]
					+ "." + fileName[0];
			File file = new File(filepath);
			if (file.exists()) {
				DownladUtil.installFile(new File(filepath), context);
			} else {
				downloadText.setText("下载");
				downloadDatabase.deleteData(threFloorDatas.get(id).getName());
				Toast.makeText(context, "文件不存在，请重新下载！",Toast.LENGTH_LONG).show();
			}

		}
	}
	
	
	/*
	 * 下载按钮显示的名称 下载、打开、更新
	 */
	public String getdownloadButtonDisplay(String software_name,
			String packagename) {
		Cursor temCursor = downloadDatabase.queryByPackage(packagename);
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

		boolean isInstalled = Util.checkPackageIsExist(context, packagename);
		if(isInstalled)
		{
			if(returnvalue != -1 && downloadFlag == 0)
			{
				return "下载中";
			}
			else if(returnvalue != -1 && downloadFlag != 0)
			{
				return "继续";
			}
			else {
				return "已安装";
			}
		}
		else {
			if(returnvalue == 100)
			{
				return "安装";
			}
			else if(returnvalue != -1 && downloadFlag == 0)
			{
				return "下载中";
			}
			else if(returnvalue != -1 && downloadFlag != 0)
			{
				return "继续";
			}
			else {
				return "免费";
			}
		}
	}

	public void changeItemNameWidthFlag(boolean direction) {
		land = direction;
		notifyDataSetChanged();
	}

	public void clearOneData(int id) {
		threFloorDatas.remove(id);
		notifyDataSetChanged();
	}
	
	public int getNameWidth(LinearLayout leftlayout,TextView rightButton)
	{
		int leftWidth = leftlayout.getLayoutParams().width;
		int rightWidth = rightButton.getLayoutParams().width;
		
		return Util.getDisplayMetricsWidth(activity) - leftWidth - rightWidth*2 - 10;
	}
}