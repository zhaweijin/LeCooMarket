package com.lecootech.market;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lecootech.market.adapter.InstallNecessaryAdapter;
import com.lecootech.market.adapter.SoftwareThridFloorAdapter;
import com.lecootech.market.common.ApplicationConstants;
import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.common.WebAddress;
import com.lecootech.market.data.ImageData;
import com.lecootech.market.data.InstallNecessaryFloorData;
import com.lecootech.market.data.ThridFloorData;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.DownloadDatabase;
import com.lecootech.market.download.DownladUtil;
import com.lecootech.market.handle.DataSet;

public class SoftwareInstallActivity extends Activity {
	/** Called when the activity is first created. */

	private LinearLayout layoutlistView;
	private ListView listView;
	private ProgressBar main_progressbar;

	private SoftwareThridFloorAdapter thridFloorAdapter;
	private ArrayList<ThridFloorData> thridFloorDatas = new ArrayList<ThridFloorData>();

	private InstallNecessaryAdapter installNecessaryAdapter;
	private ArrayList<InstallNecessaryFloorData> installNecessaryFloorDatas = new ArrayList<InstallNecessaryFloorData>();

	// private ProgressDialog progressDialog;
	private int count = 0;
	private View progressView;
	// private int displayHasUsedHeight=40+35;
	// = 40+60+30;
	private int installDisplayHasUsedHeight = 40 + 85;

	// resume loading
	private boolean secondLoading = false;

	//network failed 
	private RelativeLayout networklayout = null;
	
	// 分页
	private boolean loadfinish = false;
	private boolean loaded = false;
	private int page = 1;
	private int perpage = 10;
	private int length;

	// flag
	private boolean special = false;
	Bundle bundle;


    
	// dataset
	private DataSet dataSet;
	private String currentCategoryID;

	private SharedPreferences settingPreferences;
	private DownloadDatabase downloadDatabase;
	/**
	 * @author zhaweijin
	 * @function UI线程的处理
	 */
	Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case ApplicationConstants.main:
					
					if(networklayout!=null)
						networklayout.setVisibility(View.INVISIBLE);
					
					if (dataSet.getCurrentSoftwareCount() > perpage) {
						listView.addFooterView(progressView);
						setOtherListviewOnScrollStateChange();
					}

					installNecessaryAdapter = new InstallNecessaryAdapter(
							SoftwareInstallActivity.this,
							SoftwareInstallActivity.this,
							installNecessaryFloorDatas,
							Util.checkCurrentDirection(SoftwareInstallActivity.this),listView);
					listView.setAdapter(installNecessaryAdapter);

					loaded = true;
					page++;
					
					main_progressbar.setVisibility(View.INVISIBLE);
					break;
				case ApplicationConstants.update:
					installNecessaryAdapter.notifyDataSetChanged();

					if (dataSet.getInstallNecessaryDatas().size() < 10) {
						myHandler
								.sendEmptyMessage(ApplicationConstants.remove_foot_view);
						loadfinish = true;
					}
					loaded = true;
					page++;				
					break;
				case ApplicationConstants.webTimeout:
					networkException(getResources().getString(
							R.string.web_load_timeout));
					break;
				case ApplicationConstants.exception:
					main_progressbar.setVisibility(View.INVISIBLE);
					Toast.makeText(SoftwareInstallActivity.this, "加载网络异常",
							Toast.LENGTH_LONG).show();
					break;
				case ApplicationConstants.remove_foot_view:
					if (special) {
						listView.removeFooterView(progressView);
						installNecessaryAdapter.notifyDataSetChanged();
					} else {
						listView.removeFooterView(progressView);
						thridFloorAdapter.notifyDataSetChanged();
					}

					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				Util.print("thread error", "thread error");
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.software_install_main);

		settingPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (settingPreferences
				.getBoolean("checkbox_switch_display_start", true)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		try {
			downloadDatabase = DatabaseManager.getDownloadDatabase(this);
			bundle = getIntent().getExtras();
			special = true;
			init();

			
			main();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private DataSet setAdapterData() {
		try {

			dataSet = Util.getSoftwareWebData(this,SharedPrefsUtil.getValue(
					SoftwareInstallActivity.this, "DomainName",
					WebAddress.comString)
					+ WebAddress.special_recommentString
					+ Util.postPerpageNum(page, perpage));

			// Util.print("software_size", dataSet.getCurrentSoftwareCount()+
			// "");
			// }
			// else {
			// dataSet =
			// Util.getSoftwareWebData(WebAddress.thridfloor_software_listString
			// + currentCategoryID + "&" +Util.postPerpageNum(page, perpage));
//			 Util.print("webpath", SharedPrefsUtil.getValue(
//						SoftwareInstallActivity.this, "DomainName",
//						WebAddress.comString)
//						+ WebAddress.special_recommentString
//						+ Util.postPerpageNum(page, perpage));
			// }
			return dataSet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			Util.print("resume", "resume");
			// if(special)
			Util.setListViewHeight(SoftwareInstallActivity.this, listView,
					installDisplayHasUsedHeight);
			// else
			if (secondLoading) {
				
				installNecessaryAdapter.notifyDataSetChanged();
				
			}
			secondLoading = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		Util.print("cofiguration", "con");
//		// if(special)
//		Util.setListViewHeight(SoftwareInstallActivity.this, listView,
//				installDisplayHasUsedHeight);
//		// else
//		// Util.setListViewHeight(SoftwareInstallActivity.this, listView,
//		// displayHasUsedHeight);
//		try {
//			installNecessaryAdapter.changeItemNameWidthFlag(Util
//					.checkCurrentDirection(this));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		super.onConfigurationChanged(newConfig);
//	}

	public void setListviewOnScrollStateChange() {
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				try {
					if (view.getLastVisiblePosition() == thridFloorDatas.size()) {
						if (!special) {
							if (loaded && !loadfinish) {
								Util.print("load", "load");
								new Thread(new Runnable() {

									public void run() {
										try {
											loaded = false;
											Util.print("start", "start" + count);

											if (setAdapterData() != null) {
												length = dataSet
														.getThreeCates().size();
												if (length != 0) {
													thridFloorDatas.addAll(dataSet
															.getThreeCates());
												} else {
													loadfinish = true;
												}
												myHandler
														.sendEmptyMessage(ApplicationConstants.update);
											} else {
												myHandler
														.sendEmptyMessage(ApplicationConstants.webTimeout);
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}).start();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	public void setOtherListviewOnScrollStateChange() {
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				try {
					if (view.getLastVisiblePosition() == installNecessaryFloorDatas
							.size()) {
						Util.print("load", Boolean.toString(loadfinish));
						if (loaded && !loadfinish) {

							new Thread(new Runnable() {

								public void run() {
									try {
										Util.print("start", "start" + count);
										loaded = false;

										if (setAdapterData() != null) {
											length = dataSet
													.getInstallNecessaryDatas()
													.size();
											if (length != 0) {
												InstallNecessaryFloorData data;
												for (int i = 0; i < length; i++) {
													data = new InstallNecessaryFloorData();
													data.setName(dataSet
															.getInstallNecessaryDatas()
															.get(i).getName());
													data.setScroe(dataSet
															.getInstallNecessaryDatas()
															.get(i).getScore());
													data.setSize(dataSet
															.getInstallNecessaryDatas()
															.get(i).getSize());
													data.setDownloadPath(dataSet
															.getInstallNecessaryDatas()
															.get(i)
															.getDownloadPath());
													data.setSwid(dataSet
															.getInstallNecessaryDatas()
															.get(i).getSwid());
													data.setModifyData(dataSet
															.getInstallNecessaryDatas()
															.get(i)
															.getModifyData());
													data.setIntroduce(dataSet
															.getInstallNecessaryDatas()
															.get(i)
															.getIntroduce());
													data.setImagePath(dataSet
															.getInstallNecessaryDatas()
															.get(i)
															.getImagePath());
													data.setPackageName(dataSet
															.getInstallNecessaryDatas()
															.get(i)
															.getPackageName());
													// Util.print("softwaresize",
													// dataSet.getInstallNecessaryDatas().get(i).getSize());
													data.setSelectState(false);
													Util.installcheck
															.add(false);
													installNecessaryFloorDatas
															.add(data);
												}
											} else {
												loadfinish = true;
											}
											myHandler
													.sendEmptyMessage(ApplicationConstants.update);
										} else {
											myHandler
													.sendEmptyMessage(ApplicationConstants.webTimeout);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}).start();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	public void main() {

		main_progressbar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			public void run() {
				try {
					loaded = false;

					if (setAdapterData() != null) {
						if (special) {
							length = dataSet.getInstallNecessaryDatas().size();
							if (length == 0) {
								loadfinish = true;
							} else {
								// thridFloorDatas.addAll(dataSet.getThreeCates());
								Util.installcheck.clear();
								InstallNecessaryFloorData data;
								for (int i = 0; i < length; i++) {
									data = new InstallNecessaryFloorData();
									data.setName(dataSet
											.getInstallNecessaryDatas().get(i)
											.getName());
									data.setScroe(dataSet
											.getInstallNecessaryDatas().get(i)
											.getScore());
									data.setDownloadPath(dataSet
											.getInstallNecessaryDatas().get(i)
											.getDownloadPath());
									data.setSize(dataSet
											.getInstallNecessaryDatas().get(i)
											.getSize());
									data.setSwid(dataSet
											.getInstallNecessaryDatas().get(i)
											.getSwid());
									data.setModifyData(dataSet
											.getInstallNecessaryDatas().get(i)
											.getModifyData());
									data.setIntroduce(dataSet
											.getInstallNecessaryDatas().get(i)
											.getIntroduce());
									data.setImagePath(dataSet
											.getInstallNecessaryDatas().get(i)
											.getImagePath());
									data.setPackageName(dataSet
											.getInstallNecessaryDatas().get(i)
											.getPackageName());

									data.setSelectState(false);
									Util.installcheck.add(false);
									installNecessaryFloorDatas.add(data);

								}
							}
						} else {
							length = dataSet.getThreeCates().size();
							if (length == 0) {
								loadfinish = true;
							} else {
								thridFloorDatas.addAll(dataSet.getThreeCates());
							}
						}
						myHandler.sendEmptyMessage(ApplicationConstants.main);
					} else
						myHandler
								.sendEmptyMessage(ApplicationConstants.webTimeout);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	public void networkException(String title) {

		main_progressbar.setVisibility(View.INVISIBLE);
		networklayout = (RelativeLayout)findViewById(R.id.include_network_failed);
		networklayout.setVisibility(View.VISIBLE);
		
		
		Button flashButton = (Button)findViewById(R.id.reflash);
		flashButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Util.print("reflash", "reflash");
				networklayout.setVisibility(View.INVISIBLE);
				main();
			}
		});
	}

	AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			try {
				Intent intent = new Intent(SoftwareInstallActivity.this,
						InformationTabHost.class);
				if (special) {
					if (Util.checkCurrentSoftwareDownloading(
							SoftwareInstallActivity.this,
							installNecessaryFloorDatas.get(arg2).getName()))
						intent.putExtra("downloading", true);
					intent.putExtra("swid", installNecessaryFloorDatas
							.get(arg2).getSwid());
					intent.putExtra("name", installNecessaryFloorDatas
							.get(arg2).getName());
					// Util.print("categoryID",
					// installNecessaryFloorDatas.get(arg2).getCategoryID());
				} else {
					if (Util.checkCurrentSoftwareDownloading(
							SoftwareInstallActivity.this,
							thridFloorDatas.get(arg2).getName()))
						intent.putExtra("downloading", true);
					intent.putExtra("swid", thridFloorDatas.get(arg2).getSwid());
					intent.putExtra("name", thridFloorDatas.get(arg2).getName());
					;
					// Util.print("categoryID",
					// thridFloorDatas.get(arg2).getCategoryID());
				}
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	};

	public View getProgressView() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		return layoutInflater.inflate(R.layout.progressbar, null);
	}

	public void init() {

		LinearLayout buttomLay = (LinearLayout) findViewById(R.id.special_buttom_navigation);
		int buttomheight = buttomLay.getLayoutParams().height;

		LinearLayout topLayout = (LinearLayout) findViewById(R.id.include_top);
		int topheight = topLayout.getLayoutParams().height;

		installDisplayHasUsedHeight = buttomheight + topheight + 35;
		// Util.print("installDisplayHasUsedHeight",
		// installDisplayHasUsedHeight+"");

		// progressDialog = Util.getProgressDialog(this.getParent(),"加载中...");
		main_progressbar = (ProgressBar) findViewById(R.id.main_progressbar);
		// 初始化listview
		layoutlistView = (LinearLayout) findViewById(R.id.layout_listview);

		listView = new ListView(SoftwareInstallActivity.this);
		layoutlistView.addView(listView);
		listView.setDivider(getResources()
				.getDrawable(R.drawable.gallery_diver));
		listView.setVerticalFadingEdgeEnabled(false);
		// 设置listview的高度
		// Util.setListViewHeight(SoftwareThridFloorActivity.this, listView,
		// displayHasUsedHeight);

		// 增加listview底部进度条
		progressView = getProgressView();

		listView.setOnItemClickListener(onItemClickListener);

		Button install = (Button)findViewById(R.id.install_download);
		install.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				installCheckAllSoftware();
			}
		});
		
	}

	public void installCheckAllSoftware() {
		try {
			int size = Util.installcheck.size();

			String downloadPathDir = Util.getStoreApkPath();
			for (int i = 0; i < size; i++) {
				boolean selected = Util.installcheck.get(i);
				if (selected) {
					if (!Util.checkIsDownloading(SoftwareInstallActivity.this,
							installNecessaryFloorDatas.get(i).getName())) {
						DownladUtil.downloadAction(
								SoftwareInstallActivity.this,
								installNecessaryFloorDatas.get(i)
										.getDownloadPath(),
								installNecessaryFloorDatas.get(i).getSwid(),
								installNecessaryFloorDatas.get(i)
										.getPackageName(),
								installNecessaryFloorDatas.get(i).getName(),
								false, installNecessaryFloorDatas.get(i)
										.getSize(),null);
					} else {
						Toast.makeText(
								SoftwareInstallActivity.this,
								installNecessaryFloorDatas.get(i).getName()
										+ "正在下载...", 1000).show();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}