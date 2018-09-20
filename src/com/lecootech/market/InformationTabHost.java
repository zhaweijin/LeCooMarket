package com.lecootech.market;


import com.lecootech.market.common.Util;
import com.lecootech.market.data.ImageData;
import com.lecootech.market.database.CollectionDatabase;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.download.ImagePageTask;
import com.lecootech.market.notification.Broadcast;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class InformationTabHost extends ActivityGroup {

	private LinearLayout container = null;

	private int Currentposition = 1;
	// top layout background
	private RelativeLayout layoutLeft;
	private RelativeLayout layoutCenter;
	private RelativeLayout layoutRight;

	
	public static String packagenameString = "";
	public static boolean downloading;

	public static String currentCategoryID = ""; // 在informationDescriptionActivity当中描述

	private SharedPreferences settingPreferences;
	
	
	//add new
	private TextView softwareName;
	private RatingBar ratingBar;
	private ImageView iconImageView;
	
//	private LinearLayout layoutShare;
//	private LinearLayout layoutCollection;
	private CollectionDatabase collectionDatabase;
	private TextView collectionTextView;
	private Button Share;
	private Button Collection;
	
	public static boolean loadfinish = false;
	
	//需要的一些变量
	public static String swid = "";
	public static String software_name = "";
	public static String software_modifydate = "";
	public static String software_size = "";
	public static String software_imagepath = "";
	public static String software_filepath = "";
	public static String software_score = "";
	public static String software_version = "";
	
	public UpdateBroadcast updateBroadcast = new UpdateBroadcast();;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.information_tabhost);
		settingPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (settingPreferences
				.getBoolean("checkbox_switch_display_start", true)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		swid = getIntent().getStringExtra("swid");
		software_name = getIntent().getStringExtra("name");
		downloading = getIntent().getBooleanExtra("downloading", false);

		container = (LinearLayout) findViewById(R.id.containerBody);

		layoutLeft = (RelativeLayout) findViewById(R.id.layout_description);
		layoutLeft.setBackgroundResource(R.drawable.topbar);
		layoutLeft.setOnClickListener(onClickListener);

		layoutCenter = (RelativeLayout) findViewById(R.id.layout_pingrun);
		layoutCenter.setOnClickListener(onClickListener);

		layoutRight = (RelativeLayout) findViewById(R.id.layout_relative);
		layoutRight.setOnClickListener(onClickListener);

		layoutLeft.setBackgroundResource(R.drawable.topbar);
		layoutCenter.setBackgroundColor(Color.TRANSPARENT);
		layoutRight.setBackgroundColor(Color.TRANSPARENT);
		layoutLeft.setSelected(true);

		Intent intent = new Intent(InformationTabHost.this,
				InformationDescriptionActivity.class);
		intent.putExtra("swid", swid);
		intent.putExtra("name", software_name);
		intent.putExtra("downloading", downloading);
		intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		container.removeAllViews();
		container.addView(
				getLocalActivityManager().startActivity("Module1", intent)
						.getDecorView(), LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		
		
		collectionDatabase = DatabaseManager.getCollectionDatabase(this);
		init();
		
		
		IntentFilter filter=new IntentFilter();
		filter.addAction("information.top.update");
		registerReceiver(updateBroadcast, filter);
	}

	View.OnClickListener onClickListener = new View.OnClickListener() {

		public void onClick(View v) {
			try {
				Intent intent;
				switch (v.getId()) {

				case R.id.layout_description:
					if (Currentposition != 1) {
						layoutLeft.setBackgroundResource(R.drawable.topbar);
						layoutCenter.setBackgroundColor(Color.TRANSPARENT);
						layoutRight.setBackgroundColor(Color.TRANSPARENT);
						layoutLeft.setSelected(true);
						layoutCenter.setSelected(false);
						layoutRight.setSelected(false);

						Currentposition = 1;
						intent = new Intent(InformationTabHost.this,
								InformationDescriptionActivity.class);
						intent.putExtra("swid", swid);
						intent.putExtra("name", software_name);
						intent.putExtra("downloading", downloading);
						intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						container.removeAllViews();
						container.addView(getLocalActivityManager()
								.startActivity("Module1", intent)
								.getDecorView(),
								LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT);
					}
					break;
				case R.id.layout_pingrun:
					if (Currentposition != 2) {
						layoutLeft.setBackgroundColor(Color.TRANSPARENT);
						layoutCenter.setBackgroundResource(R.drawable.topbar);
						layoutRight.setBackgroundColor(Color.TRANSPARENT);
						layoutLeft.setSelected(false);
						layoutCenter.setSelected(true);
						layoutRight.setSelected(false);

						Currentposition = 2;
						intent = new Intent(InformationTabHost.this,
								InformationCommentActivity.class);
						intent.putExtra("swid", swid);
						intent.putExtra("packagename", packagenameString);
						intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						container.removeAllViews();
						container.addView(getLocalActivityManager()
								.startActivity("Module2", intent)
								.getDecorView(),
								LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT);
					}
					break;
				case R.id.layout_relative:
					if (Currentposition != 3) {

						layoutLeft.setBackgroundColor(Color.TRANSPARENT);
						layoutCenter.setBackgroundColor(Color.TRANSPARENT);
						layoutRight.setBackgroundResource(R.drawable.topbar);
						layoutLeft.setSelected(false);
						layoutCenter.setSelected(false);
						layoutRight.setSelected(true);

						Currentposition = 3;
						intent = new Intent(InformationTabHost.this,
								SoftwareThridFloorActivity.class);
						intent.putExtra("flag", "relative");
						intent.putExtra("ID", currentCategoryID);
						intent.putExtra("name", "");
						intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						container.removeAllViews();
						container.addView(getLocalActivityManager()
								.startActivity("Module3", intent)
								.getDecorView(),
								LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT);
					}
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};
	
	
	
	public void init()
	{
		softwareName = (TextView)findViewById(R.id.software_name);
		ratingBar = (RatingBar)findViewById(R.id.rating);
		iconImageView = (ImageView)findViewById(R.id.software_image);
//		layoutShare = (LinearLayout)findViewById(R.id.layout_share);
//		layoutCollection = (LinearLayout)findViewById(R.id.layout_collection);
		collectionTextView = (TextView)findViewById(R.id.collection);
//		layoutShare = (LinearLayout) findViewById(R.id.layout_share);
		
		Share = (Button)findViewById(R.id.share);
		Collection = (Button)findViewById(R.id.collection);
		
		softwareName.setText(software_name);
		softwareName.setSelected(true);
		ButtonEvent();
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(updateBroadcast);
	}
	
	/**
	 * 收藏
	 */
	public void ButtonEvent() {

		
		Cursor temCursor = collectionDatabase.queryOneData(software_name);
		int size = temCursor.getCount();
		temCursor.close();
		if (size > 0) {
			collectionTextView.setText(R.string.commtent_keep_cancel);
		} else {
			collectionTextView.setText(R.string.commtent_keep);
		}
		

		Collection.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						softwareCollection();
					}
		 });


		
	    Share.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				shareSoftware();
			}
		});
		
	}
	
	public void softwareCollection()
	{
		try {
			if (loadfinish) {
				if (collectionTextView.getText().toString().equals("收藏")) {
					collectionDatabase
						.insertData(swid,
								software_name, 
								software_imagepath, 
								software_modifydate, 
								software_size, 
								software_filepath, 
								packagenameString,
								software_version);
					
					collectionTextView.setText(R.string.commtent_keep_cancel);
					Toast.makeText(
							InformationTabHost.this,
							"收藏成功!", 1500).show();
				} else {
					collectionDatabase.deleteDataByName(software_name);
					collectionTextView.setText(R.string.commtent_keep);
					Toast.makeText(
							InformationTabHost.this,
							"取消成功!", 1500).show();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/*
	 * 共享软件，调用系统方法实现
	 */
	public void shareSoftware() {
		try {
			if(loadfinish)
			{
				Intent intent = new Intent(android.content.Intent.ACTION_SEND);
				String content = "HI,我在乐酷市场发现"
						+ software_name
						+ "挺不错,好东西一定要分享，你懂的，点击查看:http://m.lecoo.com/xiangxi.aspx?id="
						+ swid + "&fenlei=";
				intent.putExtra(Intent.EXTRA_TEXT, content);
				intent.putExtra("sms_body", content);
				intent.setType("text/plain");
				startActivity(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class UpdateBroadcast extends Broadcast
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			super.onReceive(context, intent);
			String action = intent.getAction();
			if(action.equals("information.top.update"))
			{
				ratingBar.setRating(Integer.parseInt(software_score));
				loadSoftwareIcon();
			}
		}
	}
	

	
	public void loadSoftwareIcon() {

		ImagePageTask pt = new ImagePageTask();
		ImageData data = new ImageData();
		data.setActivity(InformationTabHost.this);
		data.setPath(software_imagepath.replaceAll(" ", "%20"));
		data.setName(software_name);
		data.setParentName("");
		data.setBigOrSmall(false);
		data.setDate(software_modifydate);
		data.setSwid("software" + swid);
		data.setImageView(iconImageView);
		pt.execute(data);
	}
}