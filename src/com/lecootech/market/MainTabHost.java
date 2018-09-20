package com.lecootech.market;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.common.WebAddress;
import com.mobclick.android.MobclickAgent;

public class MainTabHost extends ActivityGroup {

	private LinearLayout container = null;
	private int Currentposition = 1;
	// top layout background
	private LinearLayout mainPageLayout;
	private LinearLayout cateGoryLayout;
	private LinearLayout paihangLayout;
	private LinearLayout managerLayout;
	private LinearLayout searchLayout;
	
	private TextView main;
	private TextView cate;
	private TextView paihang;
	private TextView manager;
	private TextView serarch;

	private SharedPreferences settingPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_tabhost);
		settingPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Util.print("fa", Boolean.toString(settingPreferences.getBoolean(
				"checkbox_switch_display_start", true)));
		if (settingPreferences
				.getBoolean("checkbox_switch_display_start", true)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		main = (TextView)findViewById(R.id.tuijian_name);
		cate = (TextView)findViewById(R.id.category_name);
		paihang = (TextView)findViewById(R.id.paihangbang_name);
		manager = (TextView)findViewById(R.id.manager_name);
		serarch = (TextView)findViewById(R.id.search_name);
		main.setOnClickListener(onClickListener);
		cate.setOnClickListener(onClickListener);
		paihang.setOnClickListener(onClickListener);
		manager.setOnClickListener(onClickListener);
		serarch.setOnClickListener(onClickListener);
		
		container = (LinearLayout) findViewById(R.id.containerBody);

		Currentposition = getIntent().getIntExtra("topFloorPosition", 1);

		mainPageLayout = (LinearLayout) findViewById(R.id.linear_tuijian);
		cateGoryLayout = (LinearLayout) findViewById(R.id.linear_category);
		paihangLayout = (LinearLayout) findViewById(R.id.linear_paihangbang);
		managerLayout = (LinearLayout) findViewById(R.id.linear_manager);
		searchLayout = (LinearLayout) findViewById(R.id.linear_search);


		if (Currentposition == 1) {
			mainPageLayout.setBackgroundResource(R.drawable.tab_selected_rounded);
			main.setSelected(true);
			main.getCompoundDrawables()[1].setLevel(1);
		} else if (Currentposition == 4) {
			managerLayout.setBackgroundResource(R.drawable.tab_selected_rounded);
			manager.setSelected(true);
			manager.getCompoundDrawables()[1].setLevel(1);
		} else if (Currentposition == 8) {
			managerLayout.setBackgroundResource(R.drawable.tab_selected_rounded);
			manager.setSelected(true);
			manager.getCompoundDrawables()[1].setLevel(1);
		}

		Intent intent;
		if (Currentposition == 4) {
			intent = new Intent(MainTabHost.this, ManagerTabHost.class);
			intent.putExtra("Currentposition", 1);
		} else if (Currentposition == 8) {
			intent = new Intent(MainTabHost.this, ManagerTabHost.class);
			intent.putExtra("Currentposition", 8);// 跳转到下载管理页面设置为8
		} else {
			intent = new Intent(MainTabHost.this, RecommentTabHost.class);
		}

		intent.putExtra("position", Currentposition);
		intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

		container.removeAllViews();
		container.addView(
				getLocalActivityManager().startActivity("Module1", intent)
						.getDecorView(), LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);

	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_one, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemSetting:
			Intent intent = new Intent(MainTabHost.this, SettingActivity.class);
			startActivityForResult(intent, 1);
			break;
		case R.id.itemUser:
			// Intent intent2 = new
			// Intent(SoftwareRecommendedActivity.this,Login.class);
			// startActivity(intent2);
			marketFeedback();
			break;
		case R.id.itemContact: // 手动更新
			new AlertDialog.Builder(MainTabHost.this)
					.setTitle("乐酷市场")
					.setMessage("请返回首页更新！")
					.setPositiveButton(getResources().getString(R.string.ok),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).show();

			break;
		case R.id.itemAbout:
			Util.dialogAbout(MainTabHost.this);
			break;
		}
		return true;
	}

	public void marketFeedback() {
		LayoutInflater inflater = LayoutInflater.from(MainTabHost.this);
		View view = inflater.inflate(R.layout.feedback, null);
		final EditText feedbackEditText = (EditText) view
				.findViewById(R.id.feedback_edit);
		final EditText feedbackEditText_contact = (EditText) view
				.findViewById(R.id.feedback_contact_edit);

		new AlertDialog.Builder(MainTabHost.this)
				.setTitle("反馈建议:")
				.setView(view)
				.setPositiveButton("提交", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						String feedback = feedbackEditText.getText().toString();
						String feedback_contact = feedbackEditText_contact
								.getText().toString();
						if (feedback.equals("")) {
							Toast.makeText(MainTabHost.this, "请输入反馈内容再提交!",
									1500).show();
						} else {
							if (feedback.length() > 500) {
								Toast.makeText(MainTabHost.this, "评论内容超出500个",
										1500).show();
							} else {
								try {
									String oldedition = "";
									try {
										oldedition = getPackageManager()
												.getPackageInfo(
														"com.lecootech.market",
														0).versionName;
									} catch (Exception e) {
										e.printStackTrace();
									}
									String[] key = { "imei", "message",
											"mobiletype", "edition", "email","version"};
									String[] value = {
											Util.getDeviceID(MainTabHost.this),
											feedback,
											Util.getMODEL(MainTabHost.this),
											oldedition, feedback_contact,
											getPackageManager().getPackageInfo(
													getPackageName(), 0).versionName};

									Util.CommitDownloadNum(MainTabHost.this,
											SharedPrefsUtil.getValue(
													MainTabHost.this,
													"DomainName",
													WebAddress.comString)
													+ WebAddress.send_feedback,
											key, value);

									Toast.makeText(MainTabHost.this, "反馈成功!",
											1500).show();
								} catch (Exception e) {
								}
							}
						}

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	View.OnClickListener onClickListener = new View.OnClickListener() {

		public void onClick(View v) {
			try {
				Intent intent;
				Util.print("id--->", "----------"+v.getId());
				switch (v.getId()) {

				
				case R.id.tuijian_name:
					if (Currentposition != 1) {
						Currentposition = 1;
						mainPageLayout.setBackgroundResource(R.drawable.tab_selected_rounded);
						
						main.setSelected(true);
						main.getCompoundDrawables()[1].setLevel(1);
						cate.setSelected(false);
						cate.getCompoundDrawables()[1].setLevel(0);
						paihang.setSelected(false);
						paihang.getCompoundDrawables()[1].setLevel(0);
						manager.setSelected(false);
						manager.getCompoundDrawables()[1].setLevel(0);
						serarch.setSelected(false);
						serarch.getCompoundDrawables()[1].setLevel(0);
						
						cateGoryLayout.setBackgroundColor(Color.TRANSPARENT);
						paihangLayout.setBackgroundColor(Color.TRANSPARENT);
						managerLayout.setBackgroundColor(Color.TRANSPARENT);
						searchLayout.setBackgroundColor(Color.TRANSPARENT);
						intent = new Intent(MainTabHost.this,
								RecommentTabHost.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						container.removeAllViews();
						container.addView(getLocalActivityManager()
								.startActivity("Module1", intent)
								.getDecorView(), LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT);
					}
					break;
				case R.id.category_name:
					if (Currentposition != 2) {
						Currentposition = 2;
						cateGoryLayout.setBackgroundResource(R.drawable.tab_selected_rounded);
						
						cate.setSelected(true);
						cate.getCompoundDrawables()[1].setLevel(1);
						main.setSelected(false);
						main.getCompoundDrawables()[1].setLevel(0);
						paihang.setSelected(false);
						paihang.getCompoundDrawables()[1].setLevel(0);
						manager.setSelected(false);
						manager.getCompoundDrawables()[1].setLevel(0);
						serarch.setSelected(false);
						serarch.getCompoundDrawables()[1].setLevel(0);
						
						mainPageLayout.setBackgroundColor(Color.TRANSPARENT);
						paihangLayout.setBackgroundColor(Color.TRANSPARENT);
						managerLayout.setBackgroundColor(Color.TRANSPARENT);
						searchLayout.setBackgroundColor(Color.TRANSPARENT);
						intent = new Intent(MainTabHost.this,
								CategoryTabHost.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						container.removeAllViews();
						container.addView(getLocalActivityManager()
								.startActivity("Module2", intent)
								.getDecorView(), LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT);
					}
					break;
				case R.id.paihangbang_name:
					if (Currentposition != 3) {
						Currentposition = 3;
						paihangLayout.setBackgroundResource(R.drawable.tab_selected_rounded);
						
						paihang.setSelected(true);
						paihang.getCompoundDrawables()[1].setLevel(1);
						main.setSelected(false);
						main.getCompoundDrawables()[1].setLevel(0);
						cate.setSelected(false);
						cate.getCompoundDrawables()[1].setLevel(0);
						manager.setSelected(false);
						manager.getCompoundDrawables()[1].setLevel(0);
						serarch.setSelected(false);
						serarch.getCompoundDrawables()[1].setLevel(0);
						
						mainPageLayout.setBackgroundColor(Color.TRANSPARENT);
						cateGoryLayout.setBackgroundColor(Color.TRANSPARENT);
						managerLayout.setBackgroundColor(Color.TRANSPARENT);
						searchLayout.setBackgroundColor(Color.TRANSPARENT);
						intent = new Intent(MainTabHost.this, TopTabHost.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						container.removeAllViews();
						container.addView(getLocalActivityManager()
								.startActivity("Module3", intent)
								.getDecorView(), LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT);
					}
					break;
				case R.id.manager_name:
					if (Currentposition != 4) {
						Currentposition = 4;
						managerLayout.setBackgroundResource(R.drawable.tab_selected_rounded);
						
						manager.setSelected(true);
						manager.getCompoundDrawables()[1].setLevel(1);
						main.setSelected(false);
						main.getCompoundDrawables()[1].setLevel(0);
						cate.setSelected(false);
						cate.getCompoundDrawables()[1].setLevel(0);
						paihang.setSelected(false);
						paihang.getCompoundDrawables()[1].setLevel(0);
						serarch.setSelected(false);
						serarch.getCompoundDrawables()[1].setLevel(0);
						
						mainPageLayout.setBackgroundColor(Color.TRANSPARENT);
						cateGoryLayout.setBackgroundColor(Color.TRANSPARENT);
						paihangLayout.setBackgroundColor(Color.TRANSPARENT);
						searchLayout.setBackgroundColor(Color.TRANSPARENT);
						intent = new Intent(MainTabHost.this,
								ManagerTabHost.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						intent.putExtra("Currentposition", 1);
						container.removeAllViews();
						container.addView(getLocalActivityManager()
								.startActivity("Module4", intent)
								.getDecorView(), LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT);
					}
					break;
				case R.id.search_name:
					// if (Currentposition != 5) {
					Currentposition = 5;
					searchLayout.setBackgroundResource(R.drawable.tab_selected_rounded);
					
					serarch.setSelected(true);
					serarch.getCompoundDrawables()[1].setLevel(1);
					main.setSelected(false);
					main.getCompoundDrawables()[1].setLevel(0);
					cate.setSelected(false);
					cate.getCompoundDrawables()[1].setLevel(0);
					paihang.setSelected(false);
					paihang.getCompoundDrawables()[1].setLevel(0);
					manager.setSelected(false);
					manager.getCompoundDrawables()[1].setLevel(0);
					
					mainPageLayout.setBackgroundColor(Color.TRANSPARENT);
					cateGoryLayout.setBackgroundColor(Color.TRANSPARENT);
					paihangLayout.setBackgroundColor(Color.TRANSPARENT);
					managerLayout.setBackgroundColor(Color.TRANSPARENT);
					intent = new Intent(MainTabHost.this,
							SoftwareSearchActivity.class);
					// intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					container.removeAllViews();
					container.addView(
							getLocalActivityManager().startActivity("Module5",
									intent).getDecorView(),
							LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
					// }
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			event.startTracking();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			Currentposition = 5;
			searchLayout.setBackgroundResource(R.drawable.tab_selected_rounded);
			searchLayout.setSelected(true);
			mainPageLayout.setSelected(false);
			cateGoryLayout.setSelected(false);
			paihangLayout.setSelected(false);
			managerLayout.setSelected(false);
			mainPageLayout.setBackgroundColor(Color.TRANSPARENT);
			cateGoryLayout.setBackgroundColor(Color.TRANSPARENT);
			paihangLayout.setBackgroundColor(Color.TRANSPARENT);
			managerLayout.setBackgroundColor(Color.TRANSPARENT);
			Intent intent = new Intent(MainTabHost.this,
					SoftwareSearchActivity.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			container.removeAllViews();
			container.addView(
					getLocalActivityManager().startActivity("Module5", intent)
							.getDecorView(), LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT);

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
				&& !event.isCanceled()) {
			new AlertDialog.Builder(this)
					.setIcon(R.drawable.report_error)
					.setTitle(getResources().getString(R.string.exit_title))
					.setMessage(getResources().getString(R.string.exit_message))
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									SharedPrefsUtil.delValue(MainTabHost.this,
											"DomainName");
									finish();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();

			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
}