package com.lecootech.market;

import com.lecootech.market.common.Util;

import android.app.ActivityGroup;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ManagerTabHost extends ActivityGroup {

	private LinearLayout container = null;

	private int Currentposition = 1;
	// top layout background
	private RelativeLayout layoutLeft;
	private RelativeLayout layoutCenter;
	private RelativeLayout layoutRight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.second_tabhost);
		Currentposition  = getIntent().getIntExtra("Currentposition", 1);
		
		
		container = (LinearLayout) findViewById(R.id.containerBody);

		layoutLeft = (RelativeLayout) findViewById(R.id.linear_left);
		
		TextView leftTextView = (TextView) findViewById(R.id.left_name);
		leftTextView.setText("软件管理");
		layoutLeft.setOnClickListener(onClickListener);

		layoutCenter = (RelativeLayout) findViewById(R.id.linear_center);
		TextView centerTextView = (TextView) findViewById(R.id.center_name);
		centerTextView.setText("下载管理");
		layoutCenter.setOnClickListener(onClickListener);

		layoutRight = (RelativeLayout) findViewById(R.id.linear_right);
		TextView rightTextView = (TextView) findViewById(R.id.right_name);
		rightTextView.setText("收藏管理");
		layoutRight.setOnClickListener(onClickListener);

		
		Intent intent;

		if (Currentposition == 8) {
			
			Util.clearAllExceptionNotification(this);
			
			layoutCenter.setBackgroundResource(R.drawable.topbar);
			layoutCenter.setSelected(true);
			layoutRight.setBackgroundColor(Color.TRANSPARENT);
			layoutLeft.setBackgroundColor(Color.TRANSPARENT);
			intent = new Intent(ManagerTabHost.this,
					SoftwareDownloadActivity.class);
			intent.putExtra("position", Currentposition);
			intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			container.removeAllViews();
			container.addView(
					getLocalActivityManager().startActivity("Module1", intent)
							.getDecorView(), LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT);
		}else {
			layoutLeft.setBackgroundResource(R.drawable.topbar);
			layoutLeft.setSelected(true);
			layoutCenter.setBackgroundColor(Color.TRANSPARENT);
			layoutRight.setBackgroundColor(Color.TRANSPARENT);
			intent = new Intent(ManagerTabHost.this,
					SoftwareManagerActivity.class);
			intent.putExtra("position", Currentposition);
			intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			container.removeAllViews();
			container.addView(
					getLocalActivityManager().startActivity("Module1", intent)
							.getDecorView(), LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT);
		}
		
	}

	View.OnClickListener onClickListener = new View.OnClickListener() {

		public void onClick(View v) {
			try {
				Intent intent;
				switch (v.getId()) {

				case R.id.linear_left:
					if (Currentposition != 1) {
						layoutLeft.setBackgroundResource(R.drawable.topbar);
						layoutLeft.setSelected(true);
						layoutCenter.setSelected(false);
						layoutRight.setSelected(false);
						layoutCenter.setBackgroundColor(Color.TRANSPARENT);
						layoutRight.setBackgroundColor(Color.TRANSPARENT);

						Currentposition = 1;
						intent = new Intent(ManagerTabHost.this,
								SoftwareManagerActivity.class);
						intent.putExtra("position", Currentposition);
						intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						container.removeAllViews();
						container.addView(getLocalActivityManager()
								.startActivity("Module1", intent)
								.getDecorView(),
								LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT);
					}
					break;
				case R.id.linear_center:
					if (Currentposition != 2) {
						layoutLeft.setBackgroundColor(Color.TRANSPARENT);
						layoutCenter.setBackgroundResource(R.drawable.topbar);
						layoutRight.setBackgroundColor(Color.TRANSPARENT);
						layoutCenter.setSelected(true);
						layoutLeft.setSelected(false);
						layoutRight.setSelected(false);

						Currentposition = 2;
						intent = new Intent(ManagerTabHost.this,
								SoftwareDownloadActivity.class);
						intent.putExtra("position", Currentposition);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						container.removeAllViews();
						container.addView(getLocalActivityManager()
								.startActivity("Module2", intent)
								.getDecorView(),
								LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT);
					}
					break;
				case R.id.linear_right:
					if (Currentposition != 3) {

						layoutLeft.setBackgroundColor(Color.TRANSPARENT);
						layoutCenter.setBackgroundColor(Color.TRANSPARENT);
						layoutRight.setBackgroundResource(R.drawable.topbar);
						layoutRight.setSelected(true);
						layoutLeft.setSelected(false);
						layoutCenter.setSelected(false);

						Currentposition = 3;
						intent = new Intent(ManagerTabHost.this,
								SoftwareCollectionActivity.class);
						intent.putExtra("position", Currentposition);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

}