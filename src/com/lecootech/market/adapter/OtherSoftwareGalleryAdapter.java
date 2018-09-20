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
import android.widget.Gallery;
import android.widget.ImageView;
import com.lecootech.market.R;
import com.lecootech.market.common.Util;
import com.lecootech.market.data.ImageData;
import com.lecootech.market.download.IconAsyncImageLoader;
import com.lecootech.market.download.IconAsyncImageLoader.ImageCallback;
import com.lecootech.market.handle.DataSet;

public class OtherSoftwareGalleryAdapter extends BaseAdapter {
	private Context mContext;
	int mGalleryItemBackground;
	private DataSet dataSet;
	private int length;
	private SharedPreferences settingPreferences;
	private Activity activity;
	private IconAsyncImageLoader iconAsyncImageLoader;
    private Gallery gallery;

	public OtherSoftwareGalleryAdapter(Context context, Activity activity,
			DataSet dataSet,Gallery gallery) {
		mContext = context;
		this.dataSet = dataSet;
		this.activity = activity;
		this.gallery = gallery;
		length = dataSet.getThreeCates().size();

		settingPreferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		iconAsyncImageLoader = new IconAsyncImageLoader();

	}

	public int getCount() {
		return length;
	}

	public Object getItem(int position) {
		return dataSet.getThreeCates().get(position).getName();
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		try {
			final ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				LayoutInflater layoutInflater = LayoutInflater.from(mContext);
				convertView = layoutInflater.inflate(
						R.layout.software_downloaded_gallery_item, null);

				viewHolder.image = (ImageView) convertView
						.findViewById(R.id.software_download_gallery_image);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			if (settingPreferences.getBoolean("checkbox_display_icons", true)) {
				try {
					
					final ImageData data = new ImageData();
					data.setActivity(activity);
					data.setPath(dataSet.getThreeCates().get(position)
							.getImagePath().replaceAll(" ", "%20"));
					data.setName(dataSet.getThreeCates().get(position)
							.getName());
					data.setParentName("");
					data.setBigOrSmall(false);
					data.setDate(dataSet.getThreeCates().get(position)
							.getModifyData());
					data.setSwid("software"
							+ dataSet.getThreeCates().get(position)
									.getSwid()); 
					data.setImageView(viewHolder.image);
                    viewHolder.image.setTag(data.getPath());
					
					final Bitmap bitmap = iconAsyncImageLoader.loadBitmap(activity, data, new ImageCallback() {
						
						public void imageLoaded(Bitmap imageBitmap, String imageUrl) {
							// TODO Auto-generated method stub
							ImageView imageView = (ImageView)gallery.findViewWithTag(data.getPath());
							if(imageView!=null && imageBitmap!=null)
							  imageView.setImageBitmap(imageBitmap);
						}
					});
					
					if(bitmap==null)
					{
						Util.print("bitmap null", "bitmap null");
						viewHolder.image.setImageResource(R.drawable.icon2);
					}
					else {
						Util.print("bitmap not null", "bitmap not null");
						viewHolder.image.setImageBitmap(bitmap);
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				viewHolder.image.setImageResource(R.drawable.icon2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		return convertView;
	}

	public class ViewHolder {
		ImageView image;
	}
}
