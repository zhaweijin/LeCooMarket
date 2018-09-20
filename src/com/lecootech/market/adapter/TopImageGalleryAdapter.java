package com.lecootech.market.adapter;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

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
import com.lecootech.market.SoftwareRecommendedActivity;
import com.lecootech.market.common.Util;
import com.lecootech.market.data.ImagesPush;
import com.lecootech.market.download.AsyncImageLoader;
import com.lecootech.market.download.AsyncImageLoader.ImageCallback;

public class TopImageGalleryAdapter extends BaseAdapter {


	private ArrayList<ImagesPush> imagesPushs;
	private SharedPreferences settingPreferences;
	private Activity activity;

	private AsyncImageLoader asyncImageLoader;
    private Gallery gallery;
	public TopImageGalleryAdapter(Activity activity,
			ArrayList<ImagesPush> imagesPushs,Gallery gallery) {

		this.imagesPushs = imagesPushs;
		this.activity = activity;
        this.gallery = gallery;
		settingPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        asyncImageLoader = new AsyncImageLoader();

	}

	public int getCount() {
		return Integer.MAX_VALUE;
	}

	public Object getItem(int position) {
		return imagesPushs.get(position).getName();
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		try {
			final ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				LayoutInflater layoutInflater = LayoutInflater.from(activity);
				convertView = layoutInflater.inflate(R.layout.top_image_gallery_item, null);

				viewHolder.image = (ImageView) convertView.findViewById(R.id.software_gallery_image);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			if (position < 0) {

				position = position + imagesPushs.size();

			}
			
			if (settingPreferences.getBoolean("checkbox_display_icons", true)) {
//				try {					

					final ImagesPush imagesPush = imagesPushs.get(position % imagesPushs.size());
//					viewHolder.image.setTag(imagesPush.getSoftwareBigIcon());
//					
//					final Bitmap bitmap = asyncImageLoader.loadBitmap(activity, imagesPush, new ImageCallback() {
//						
//
//						public void imageLoaded(Bitmap imageBitmap, String imageUrl) {
//							// TODO Auto-generated method stub
//							ImageView imageView = (ImageView)gallery.findViewWithTag(imagesPush.getSoftwareBigIcon());
//							if(imageView!=null && imageBitmap!=null)
//							  imageView.setImageBitmap(imageBitmap);
//						}
//					});
//					
//					if(bitmap==null)
//					{
//						viewHolder.image.setImageResource(R.drawable.listview_top_image_bg);
//					}
//					else {
//						viewHolder.image.setImageBitmap(bitmap);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
				
				
				String swid;
				if (imagesPushs.get(position % imagesPushs.size()).getSwid() == null) 
				{
					swid = "software" + "big" + imagesPushs.get(position % imagesPushs.size()).getCategoryid();
				} else {
					swid = "software" + "big" + imagesPushs.get(position % imagesPushs.size()).getSwid();
				}
				
				if(SoftwareRecommendedActivity.imageCache.containsKey(swid))
				{
		            Bitmap bitmap = SoftwareRecommendedActivity.imageCache.get(swid);
		            if (bitmap != null) {
		            	viewHolder.image.setImageBitmap(bitmap);
		            }
		            else {
		            	viewHolder.image.setImageResource(R.drawable.listview_top_image_bg);
					}
				}
				else {
					viewHolder.image.setImageResource(R.drawable.listview_top_image_bg);
				}
				
			
				
			} else {
				viewHolder.image.setImageResource(R.drawable.listview_top_image_bg);
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
