package com.lecootech.market;

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

public class SoftwareThridFloorTabHost extends ActivityGroup {

	private LinearLayout container = null;

	private int Currentposition = 1;
	// top layout background
	private RelativeLayout layoutLeft;
	private RelativeLayout layoutRight;

	private TextView typeTextView;
	private String catetory_name = "";
	private String catetory_ID = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.three_tabhost);

		catetory_name = getIntent().getStringExtra("name");
		catetory_ID = getIntent().getStringExtra("ID");

		typeTextView = (TextView) findViewById(R.id.type_name);
		container = (LinearLayout) findViewById(R.id.containerBody);

		typeTextView.setText(catetory_name);

		layoutLeft = (RelativeLayout) findViewById(R.id.linear_left);
		layoutLeft.setBackgroundResource(R.drawable.topbar);
		layoutLeft.setOnClickListener(onClickListener);

		layoutRight = (RelativeLayout) findViewById(R.id.linear_right);
		layoutRight.setOnClickListener(onClickListener);

		layoutLeft.setBackgroundResource(R.drawable.manager_top_bar_bg_selected);
		layoutLeft.setSelected(true);
		layoutRight.setBackgroundColor(Color.TRANSPARENT);

		Intent intent = new Intent(SoftwareThridFloorTabHost.this,
				SoftwareThridFloorMostUp.class);
		intent.putExtra("position", Currentposition);
		intent.putExtra("flag", "up");
		intent.putExtra("ID", catetory_ID);
		intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		container.removeAllViews();
		container.addView(
				getLocalActivityManager().startActivity("Module1", intent)
						.getDecorView(), LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);

	}

	View.OnClickListener onClickListener = new View.OnClickListener() {

		public void onClick(View v) {
			try {
				Intent intent;
				switch (v.getId()) {

				case R.id.linear_left:
					if (Currentposition != 1) {
						layoutLeft.setBackgroundResource(R.drawable.manager_top_bar_bg_selected);
						layoutLeft.setSelected(true);
						layoutRight.setSelected(false);
						layoutRight.setBackgroundColor(Color.TRANSPARENT);

						Currentposition = 1;
						intent = new Intent(SoftwareThridFloorTabHost.this,
								SoftwareThridFloorMostUp.class);
						intent.putExtra("position", Currentposition);
						intent.putExtra("flag", "up");
						intent.putExtra("ID", catetory_ID);
						intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						container.removeAllViews();
						container.addView(getLocalActivityManager()
								.startActivity("Module1", intent)
								.getDecorView(), LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT);
					}
					break;
				case R.id.linear_right:
					if (Currentposition != 3) {

						layoutLeft.setBackgroundColor(Color.TRANSPARENT);
						layoutRight.setBackgroundResource(R.drawable.manager_top_bar_bg_selected);
						layoutRight.setSelected(true);
						layoutLeft.setSelected(false);

						Currentposition = 3;
						intent = new Intent(SoftwareThridFloorTabHost.this,
								SoftwareThridFloorMostUp.class);
						intent.putExtra("position", Currentposition);
						intent.putExtra("flag", "most");
						intent.putExtra("ID", catetory_ID);
						intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						container.removeAllViews();
						container.addView(getLocalActivityManager()
								.startActivity("Module3", intent)
								.getDecorView(), LayoutParams.FILL_PARENT,
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