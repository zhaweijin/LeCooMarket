package com.lecootech.market.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lecootech.market.R;
import com.lecootech.market.common.Util;

public class GalleryAdapter extends BaseAdapter {
	private Context mContext;
	int mGalleryItemBackground;
	private String[] filename;

	private boolean big = false;
    
	private Map<Integer, Bitmap> dateCache = new HashMap<Integer, Bitmap>();
	public GalleryAdapter(Context context, String[] filename, boolean big) {
		mContext = context;
		this.filename = filename;
		Util.print("big", Boolean.toString(big));
		this.big = big;
	}

	public int getCount() {
//		if(big)
//		  return filename.length;
//	    else
		  return Integer.MAX_VALUE;
	}

	public Object getItem(int position) {
		return position;
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
				
				convertView = layoutInflater.inflate(R.layout.software_gallery_item, null);
                viewHolder.layout_image = (RelativeLayout)convertView.findViewById(R.id.layout_software_gallery);
				viewHolder.image = (ImageView) convertView.findViewById(R.id.software_gallery_image);
				
				if(!big)
				{
					int width = Util.getDisplayMetricsWidth((Activity)mContext);
					float realwidth = width*(2/(float)3);
					float realheight= realwidth*(6/(float)4);

					
					//set relativelayout
					ViewGroup.LayoutParams lParams2;
					lParams2 = viewHolder.layout_image.getLayoutParams();
					lParams2.width = (int)realwidth+18;
					lParams2.height = (int)realheight+18;
					viewHolder.layout_image.setLayoutParams(lParams2);
					
					
					//set image
					ViewGroup.LayoutParams lParams;
					lParams = viewHolder.image.getLayoutParams();
					lParams.width = (int)realwidth;
					lParams.height = (int)realheight;
					viewHolder.image.setLayoutParams(lParams);
					
				}
				else {
					int width = Util.getDisplayMetricsWidth((Activity)mContext);
					float realwidth = width;
					float realheight= realwidth*(6/(float)4);
					ViewGroup.LayoutParams lParams;
					lParams = viewHolder.image.getLayoutParams();
					lParams.width = (int)realwidth;
					lParams.height = (int)realheight;
					viewHolder.image.setLayoutParams(lParams);
					
				}
				
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			if (position < 0)
				position = position + filename.length;
			
			if(!big)
            {
				Bitmap current = dateCache.get(position%filename.length);
				if (current != null) {// 如果缓存中已解码该图片，则直接返回缓存中的图片
					viewHolder.image.setAnimation(AnimationUtils.loadAnimation(
							mContext, R.anim.my_scale_action));
					viewHolder.image.setImageBitmap(current);
				} else {
					current = getPhotoItem(filename[position%filename.length], 1); // 缩放二分之一
					if (current != null) {

						viewHolder.image.setAnimation(AnimationUtils.loadAnimation(
										mContext, R.anim.my_scale_action));
						viewHolder.image.setImageBitmap(current);
						dateCache.put(position%filename.length, current);
					}
				}
			}
			else {
				Util.print("222", position+"");
				Bitmap current = dateCache.get(position%filename.length);
				if (current != null) {// 如果缓存中已解码该图片，则直接返回缓存中的图片
//					viewHolder.image.setAnimation(AnimationUtils.loadAnimation(
//							mContext, R.anim.my_scale_action));
					viewHolder.image.setImageBitmap(current);
				} else {
					current = getPhotoItem(filename[position%filename.length], 1); // 缩放二分之一
					if (current != null) {

//						viewHolder.image
//								.setAnimation(AnimationUtils.loadAnimation(
//										mContext, R.anim.my_scale_action));
						viewHolder.image.setImageBitmap(current);
						dateCache.put(position%filename.length, current);
					}
				}
			}
			
			
			if(big)
			{
				convertView.setBackgroundColor(R.color.information_gallery_background);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	public class ViewHolder {
		ImageView image;
		RelativeLayout layout_image;
	}
	
	public Bitmap getPhotoItem(String filepath,int size) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize=size;
		if(filepath!=null)
		{
			File file = new File(filepath);
			try {
				FileInputStream fis = new FileInputStream(file);
				Bitmap bitmap = BitmapFactory.decodeStream(fis);
				bitmap=Bitmap.createScaledBitmap(bitmap, 320, 480, false);//预先缩放，避免实时缩放，可以提高更新率
				return bitmap;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		return null;
		

		} 
	
	
	public void freeBitmap() {
		// 释放之外的bitmap资源
		Bitmap delBitmap;
		for (int del = 0; del < dateCache.size(); del++) {
			delBitmap = dateCache.get(del);
			if (delBitmap != null) {
				dateCache.remove(del);
				delBitmap.recycle();
			}
		}
	}
}
