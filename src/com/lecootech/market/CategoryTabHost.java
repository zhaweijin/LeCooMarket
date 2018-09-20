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

public class CategoryTabHost extends ActivityGroup {

	private LinearLayout container = null;

	private int Currentposition = 1;
	// top layout background
	private RelativeLayout layoutPaihang;
	private RelativeLayout layoutApp;
	private RelativeLayout layoutGame;
	private RelativeLayout layoutBook;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.top_tabhost);

		
//		Currentposition = getIntent().getIntExtra("position", 1);
		container = (LinearLayout) findViewById(R.id.containerBody);

		layoutPaihang = (RelativeLayout) findViewById(R.id.linear_paihang);
		layoutPaihang.setBackgroundResource(R.drawable.topbar);
		TextView paihangTextView = (TextView) findViewById(R.id.paihang_name);
		paihangTextView.setText("应用");
		layoutPaihang.setOnClickListener(onClickListener);

		layoutApp = (RelativeLayout) findViewById(R.id.linear_app);
		TextView appTextView = (TextView) findViewById(R.id.app_name);
		appTextView.setText("游戏");
		layoutApp.setOnClickListener(onClickListener);

		layoutGame = (RelativeLayout) findViewById(R.id.linear_game);
		TextView gameTextView = (TextView) findViewById(R.id.game_name);
		gameTextView.setText("电子书");
		layoutGame.setOnClickListener(onClickListener);
		
		layoutBook = (RelativeLayout) findViewById(R.id.linear_book);
		TextView bookTextView = (TextView) findViewById(R.id.book_name);
		bookTextView.setText("专题");
		layoutBook.setOnClickListener(onClickListener);

		layoutPaihang.setBackgroundResource(R.drawable.topbar);
		layoutPaihang.setSelected(true);
		layoutApp.setBackgroundColor(Color.TRANSPARENT);
		layoutGame.setBackgroundColor(Color.TRANSPARENT);
		layoutBook.setBackgroundColor(Color.TRANSPARENT);

		Intent intent = new Intent(CategoryTabHost.this, SoftwareCategoryActivity.class);
		intent.putExtra("position", Currentposition);
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

				case R.id.linear_paihang:
					if (Currentposition != 1) {
						layoutPaihang.setBackgroundResource(R.drawable.topbar);
						layoutPaihang.setSelected(true);
						
						layoutApp.setSelected(false);
						layoutGame.setSelected(false);
						layoutBook.setSelected(false);
						
						layoutApp.setBackgroundColor(Color.TRANSPARENT);
						layoutGame.setBackgroundColor(Color.TRANSPARENT);
						layoutBook.setBackgroundColor(Color.TRANSPARENT);

						Currentposition = 1;
						intent = new Intent(CategoryTabHost.this,
								SoftwareCategoryActivity.class);
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
				case R.id.linear_app:
					if (Currentposition != 2) {
						
						layoutApp.setBackgroundResource(R.drawable.topbar);
						layoutApp.setSelected(true);
						
						layoutPaihang.setSelected(false);
						layoutGame.setSelected(false);
						layoutBook.setSelected(false);
						
						layoutPaihang.setBackgroundColor(Color.TRANSPARENT);
						layoutGame.setBackgroundColor(Color.TRANSPARENT);
						layoutBook.setBackgroundColor(Color.TRANSPARENT);
						
						Currentposition = 2;
						intent = new Intent(CategoryTabHost.this,
								SoftwareCategoryActivity.class);
						intent.putExtra("position", Currentposition);
						intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						container.removeAllViews();
						container.addView(getLocalActivityManager()
								.startActivity("Module2", intent)
								.getDecorView(),
								LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT);
					}
					break;
				case R.id.linear_game:
					if (Currentposition != 3) {

						layoutGame.setBackgroundResource(R.drawable.topbar);
						layoutGame.setSelected(true);
						
						layoutPaihang.setSelected(false);
						layoutApp.setSelected(false);
						layoutBook.setSelected(false);

						layoutPaihang.setBackgroundColor(Color.TRANSPARENT);
						layoutApp.setBackgroundColor(Color.TRANSPARENT);
						layoutBook.setBackgroundColor(Color.TRANSPARENT);
						
						Currentposition = 3;
						intent = new Intent(CategoryTabHost.this,
								SoftwareCategoryActivity.class);
						intent.putExtra("position", Currentposition);
						intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						container.removeAllViews();
						container.addView(getLocalActivityManager()
								.startActivity("Module3", intent)
								.getDecorView(),
								LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT);
					}
					break;
				case R.id.linear_book:
					if (Currentposition != 4) {

						layoutBook.setBackgroundResource(R.drawable.topbar);
						layoutBook.setSelected(true);
						
						layoutPaihang.setSelected(false);
						layoutApp.setSelected(false);
						layoutGame.setSelected(false);

						layoutPaihang.setBackgroundColor(Color.TRANSPARENT);
						layoutApp.setBackgroundColor(Color.TRANSPARENT);
						layoutGame.setBackgroundColor(Color.TRANSPARENT);
						
						Currentposition = 4;
						intent = new Intent(CategoryTabHost.this,
								SoftwareCategoryActivity.class);
						intent.putExtra("position", Currentposition);
						intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						container.removeAllViews();
						container.addView(getLocalActivityManager()
								.startActivity("Module4", intent)
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