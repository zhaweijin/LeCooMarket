package com.lecootech.market.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lecootech.market.R;
import com.lecootech.market.common.Util;
import com.lecootech.market.data.ImageData;
import com.lecootech.market.data.TempPictureThread;
import com.lecootech.market.download.IconAsyncImageLoader;
import com.lecootech.market.download.ImagePageTask;
import com.lecootech.market.download.IconAsyncImageLoader.ImageCallback;
import com.lecootech.market.handle.DataSet;

public class SoftwareSecondFloorAdapter extends BaseAdapter {

	private DataSet dataSet;
	private Context context;
	private Activity activity;
	private IconAsyncImageLoader iconAsyncImageLoader;
	private boolean land;
    private ListView listView;
	private SharedPreferences settingPreferences;

	public SoftwareSecondFloorAdapter(Context context, Activity activity,
			DataSet dataSet, boolean land,ListView listView) {
		this.context = context;
		this.dataSet = dataSet;
		this.activity = activity;
		this.listView = listView;
		this.land = land;
		iconAsyncImageLoader = new IconAsyncImageLoader();
		settingPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
	}

	public int getCount() {
		return dataSet.getSecondCates().size();
	}

	public Object getItem(int position) {
		return dataSet.getSecondCates().get(position).getName();
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
				view = layoutInflater.inflate(
						R.layout.software_secondfloor_item, null);

				viewHolder.name = (TextView) view
						.findViewById(R.id.category_name);
				viewHolder.icon_image = (ImageView) view
						.findViewById(R.id.category_image);

				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			
			
			viewHolder.name.setText(dataSet.getSecondCates().get(position)
					.getName());

			// viewHolder.name.setWidth(Util.getItemNameWidth(context, land));

			if (settingPreferences.getBoolean("checkbox_display_icons", true)) {
				try {
					
					final ImageData data = new ImageData();
					data.setActivity(activity);
					data.setPath(dataSet.getSecondCates().get(position)
							.getImagePath().replaceAll(" ", "%20"));
					data.setName(dataSet.getSecondCates().get(position)
							.getName());
					data.setParentName("");
					data.setBigOrSmall(false);
					data.setDate(dataSet.getSecondCates().get(position)
							.getModifyData());

					String categoryID = dataSet.getSecondCates()
							.get(position).getCategoryID();
					if (categoryID.equals(""))
						categoryID = "00";
					data.setSwid("category" + categoryID);
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

		} catch (Exception e) {
			e.printStackTrace();
		}

		return view;

	}

	public class ViewHolder {
		ImageView icon_image;
		TextView name;
	}

	public void changeItemNameWidthFlag(boolean direction) {
		land = direction;
		notifyDataSetChanged();
	}

}