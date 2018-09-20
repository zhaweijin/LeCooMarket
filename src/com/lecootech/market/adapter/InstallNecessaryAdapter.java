package com.lecootech.market.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lecootech.market.R;
import com.lecootech.market.common.Util;
import com.lecootech.market.data.ImageData;
import com.lecootech.market.data.InstallNecessaryFloorData;
import com.lecootech.market.data.TempPictureThread;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.DownloadDatabase;
import com.lecootech.market.database.ItemPicDatabase;
import com.lecootech.market.download.IconAsyncImageLoader;
import com.lecootech.market.download.ImagePageTask;
import com.lecootech.market.download.IconAsyncImageLoader.ImageCallback;

/**
 * @author zhaweijin
 * @function 具体数据列表---第三层数据适配
 */
public class InstallNecessaryAdapter extends BaseAdapter {

	private ArrayList<InstallNecessaryFloorData> installNecessaryDatas;
	private Context context;
	private SharedPreferences settingPreferences;
	private Activity activity;

	private static ItemPicDatabase picDatabase;
	private Cursor cursor;
	private boolean installState[];
	private boolean selectState[];
	private DownloadDatabase downloadDatabase;
	private IconAsyncImageLoader iconAsyncImageLoader;
	private boolean land;
    private ListView listView;
	
	public InstallNecessaryAdapter(Context context, Activity activity,
			ArrayList<InstallNecessaryFloorData> installNecessaryDatas,
			boolean land,ListView listView) {

		this.context = context;
		this.activity = activity;
		this.land = land;
		this.listView = listView;
		this.installNecessaryDatas = installNecessaryDatas;
		settingPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		downloadDatabase = DatabaseManager.getDownloadDatabase(context);
		iconAsyncImageLoader = new IconAsyncImageLoader();
	}


	public int getCount() {
		return installNecessaryDatas.size();
	}

	public Object getItem(int position) {
		return installNecessaryDatas.get(position).getName();
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View cc, ViewGroup parent) {
		ViewHolder viewHolder;
		picDatabase = DatabaseManager.getItemPicDatabase(context);
		if (cc == null) {

			viewHolder = new ViewHolder();
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			cc = layoutInflater.inflate(R.layout.installnecessary_row,
					null);

			viewHolder.checkBox = (CheckBox) cc
					.findViewById(R.id.installcheckbox);
			viewHolder.icon_image = (ImageView) cc.findViewById(R.id.image);

			viewHolder.name = (TextView) cc.findViewById(R.id.name);
			viewHolder.size = (TextView) cc.findViewById(R.id.size);
			viewHolder.introduce = (TextView) cc.findViewById(R.id.introduce);

			viewHolder.download = (TextView) cc.findViewById(R.id.download);
			cc.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) cc.getTag();
		}
		
		
		try {
			if (settingPreferences.getBoolean("checkbox_display_icons", true)) {
				try {

					final ImageData data = new ImageData();
					data.setActivity(activity);
					data.setPath(installNecessaryDatas.get(position)
							.getImagePath().replaceAll(" ", "%20"));
					data.setName(installNecessaryDatas.get(position)
							.getName());
					data.setParentName("");
					data.setBigOrSmall(false);
					data.setDate(installNecessaryDatas.get(position)
							.getModifyData());
					data.setSwid("software"
							+ installNecessaryDatas.get(position)
									.getSwid());
					data.setImageView(viewHolder.icon_image);
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

			viewHolder.name.setText(installNecessaryDatas.get(position)
					.getName());
			viewHolder.size.setText(installNecessaryDatas.get(position).getSize());
			viewHolder.introduce.setText(installNecessaryDatas.get(position)
					.getIntroduce());

			String displayname = getdownloadButtonDisplay(installNecessaryDatas
					.get(position).getName(),
					installNecessaryDatas.get(position).getPackageName());
			viewHolder.download.setText(displayname);

			if (Util.checkPackageIsExist(context,
					installNecessaryDatas.get(position).getPackageName())) {
				viewHolder.download.setVisibility(View.VISIBLE);
				viewHolder.checkBox.setVisibility(View.INVISIBLE);
			} else {
				viewHolder.download.setVisibility(View.INVISIBLE);
				viewHolder.checkBox.setVisibility(View.VISIBLE);
			}

			if (displayname.equals("已安装")) {
				viewHolder.download.setText(displayname);
				viewHolder.download.setTextColor(Color.RED);
			} else {
				viewHolder.download.setTextColor(Color.BLACK);
			}

			viewHolder.checkBox.setTag(position);

			if (Util.installcheck.get(position))
				viewHolder.checkBox.setChecked(true);
			else {
				viewHolder.checkBox.setChecked(false);
			}

			final int id = position;
			viewHolder.checkBox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							
								CheckBox box = (CheckBox) buttonView;
								int tempid = (Integer) buttonView.getTag(); // <--
																			// get
																			// the
																			// position
								// Util.print("tempid", tempid+"");

								if (box.isChecked()) {
									// if(Util.checkPackageIsExist(context,
									// installNecessaryDatas.get(id).getPackageName()))
									// {
									// Toast.makeText(context, "已经安装", 1000).show();
									// box.setChecked(false);
									// }
									// else {
									Util.installcheck.set(tempid, true);
									// }
								} else {
									Util.installcheck.set(tempid, false);
								}
						}
					});

		} catch (Exception e) {
			e.printStackTrace();
		}

		return cc;

	}

	public class ViewHolder {
		CheckBox checkBox;
		ImageView icon_image;

		TextView name;
		TextView size;
		TextView introduce;

		TextView download;

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

}