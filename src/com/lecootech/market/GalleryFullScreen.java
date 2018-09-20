/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lecootech.market;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lecootech.market.adapter.GalleryAdapter;
import com.mobclick.android.MobclickAgent;

public class GalleryFullScreen extends Activity {

	// private ImageView image;
	private Gallery gallery;
	private int id;
	private int pre_id;
	private String[] path;
	private int length;
	private LinearLayout layout_image;
	private ArrayList<ImageView> imageViews = new ArrayList<ImageView>();
	public final static LinearLayout.LayoutParams WC_WC = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);
	private SharedPreferences settingPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.galleryfullscreen);
			settingPreferences = PreferenceManager
					.getDefaultSharedPreferences(this);

			if (settingPreferences.getBoolean("checkbox_switch_display_start",
					true)) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}

			Bundle bundle = new Bundle();
			bundle = getIntent().getExtras();
			
			path = bundle.getStringArray("path");
			id = bundle.getInt("id");
			pre_id = id;
			length = path.length;

			layout_image = (LinearLayout) findViewById(R.id.layout_image);
			for (int i = 0; i < length; i++) {
				ImageView image = new ImageView(this);
				image.setLayoutParams(new LinearLayout.LayoutParams(WC_WC));
				
				ViewGroup.LayoutParams p=image.getLayoutParams();
				LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams)p;
				lp.leftMargin = 6;
				lp.gravity = Gravity.CENTER_VERTICAL;
				image.setLayoutParams(p);
	            
				image.setBackgroundResource(R.drawable.gallery_dot);
				imageViews.add(image);

				layout_image.addView(imageViews.get(i));
			}

			gallery = (Gallery) findViewById(R.id.gallery);
			gallery.setAdapter(new GalleryAdapter(GalleryFullScreen.this, path, true));
			gallery.setSelection(id);

			// id = path.length/2;
			imageViews.get(id%path.length).setBackgroundResource(R.drawable.gallery_dot_selected);
			pre_id = id;

			gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					imageViews.get(pre_id%path.length).setBackgroundResource(
							R.drawable.gallery_dot);
					imageViews.get(arg2%path.length).setBackgroundResource(
							R.drawable.gallery_dot_selected);
					pre_id = arg2;
				}

				public void onNothingSelected(AdapterView<?> arg0) {
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

}