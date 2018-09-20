package com.lecootech.market;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lecootech.market.adapter.PaihangAdapter;
import com.lecootech.market.adapter.SoftwareThridFloorAdapter;
import com.lecootech.market.common.ApplicationConstants;
import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.common.WebAddress;
import com.lecootech.market.data.MessageControl;
import com.lecootech.market.data.ThridFloorData;
import com.lecootech.market.handle.DataSet;

public class SoftwareTopActivity extends Activity {
	/** Called when the activity is first created. */

	private LinearLayout layoutlistView;
	private ListView listView;
	private ProgressBar main_progressbar;
	private SoftwareThridFloorAdapter thridFloorAdapter;
	private ArrayList<ThridFloorData> thridFloorDatas = new ArrayList<ThridFloorData>();

	private int count = 0;
	private View progressView;
	private int displayHasUsedHeight = 40 + 60 + 30;

	private PaihangAdapter paihangAdapter;

	// page flag
	private int Currentposition = 1;

	// resume loading
	private boolean secondLoading = false;

	// dataset
	private DataSet dataSet;

	//network failed 
	private RelativeLayout networklayout = null;
	
	// 分页
	private boolean loadfinish = false;
	private boolean loaded = false;
	private int page = 1;
	private int perpage = 10;
	private int length;
	private int pagecount = 1;

	private final static int paihang = 0x894;
	/**
	 * @author zhaweijin
	 * @function UI
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
						setListviewOnScrollStateChange();
					}

					thridFloorAdapter = new SoftwareThridFloorAdapter(
							SoftwareTopActivity.this,
							SoftwareTopActivity.this,
							thridFloorDatas,
							Util.checkCurrentDirection(SoftwareTopActivity.this),
							listView,this);
					listView.setAdapter(thridFloorAdapter);					
					loaded = true;
					page++;
					main_progressbar.setVisibility(View.INVISIBLE);
					break;
				case ApplicationConstants.update:
					Util.print("update", "update"
							+ dataSet.getThreeCates().size());
					thridFloorAdapter.notifyDataSetChanged();
					if (dataSet.getThreeCates().size() < perpage) {
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
					Toast.makeText(SoftwareTopActivity.this, "网络异常!",
							Toast.LENGTH_LONG).show();
					break;
				case ApplicationConstants.remove_foot_view:
					listView.removeFooterView(progressView);
					thridFloorAdapter.notifyDataSetChanged();
					break;
				case paihang:
					paihangAdapter = new PaihangAdapter(
							SoftwareTopActivity.this);
					listView.setAdapter(paihangAdapter);
					main_progressbar.setVisibility(View.INVISIBLE);
					break;
				case MessageControl.ADAPTER_CHANGE:
					thridFloorAdapter.notifyDataSetChanged();
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

		setContentView(R.layout.software_main);

		try {
			// Util.addActivityToStack(this, "SoftwareTopActivity");

			Util.print("oncreate", "oncreate");
			Currentposition = getIntent().getIntExtra("position", 1);
			Util.print("cu", Currentposition + "");
			init();
			main();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void init() {

		// progressDialog = Util.getProgressDialog(this.getParent(),"正在加载...");
		main_progressbar = (ProgressBar) findViewById(R.id.main_progressbar);
		// istview
		layoutlistView = (LinearLayout) findViewById(R.id.layout_listview);
		listView = new ListView(SoftwareTopActivity.this);
		layoutlistView.addView(listView, ApplicationConstants.LP_FF);
		listView.setDivider(getResources().getDrawable(R.drawable.gallery_diver));
		listView.setVerticalFadingEdgeEnabled(false);
		// Util.setListViewHeight(SoftwareTopActivity.this, listView,
		// displayHasUsedHeight);

		progressView = getProgressView();
		listView.setOnItemClickListener(onItemClickListener);

	}

	private DataSet setAdapterData() {
		try {
				if (Currentposition == 1) {
					// //Util.print("software_size",
					// dataSet.getCurrentSoftwareCount()+"");
					// dataSet =
					// Util.getSoftwareWebData(WebAddress.paihangWeek+Util.postPerpageNum(page,
					// perpage));
					// Util.print("webpath1",
					// WebAddress.paihangWeek+Util.postPerpageNum(page, perpage));
					// Util.print("software_size",
					// dataSet.getCurrentSoftwareCount()+"");
				} else if (Currentposition == 2) {
					dataSet = Util.getSoftwareWebData(this,SharedPrefsUtil.getValue(
							SoftwareTopActivity.this, "DomainName",
							WebAddress.comString)+WebAddress.applicationPaihang
							+ Util.postPerpageNum(page, perpage));
				} else if (Currentposition == 3) {
					dataSet = Util.getSoftwareWebData(this,SharedPrefsUtil.getValue(
							SoftwareTopActivity.this, "DomainName",
							WebAddress.comString)+WebAddress.gamePaihang
							+ Util.postPerpageNum(page, perpage));
				
		        } else if (Currentposition == 4) {
		        	Util.print("web", SharedPrefsUtil.getValue(
					     SoftwareTopActivity.this, "DomainName",
					     WebAddress.comString)+WebAddress.fiction_Ranking
					     + Util.postPerpageNum(page, perpage));
			        dataSet = Util.getSoftwareWebData(this,SharedPrefsUtil.getValue(
					     SoftwareTopActivity.this, "DomainName",
					     WebAddress.comString)+WebAddress.fiction_Ranking
					     + Util.postPerpageNum(page, perpage));
		       }
			
			return dataSet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Util.print("onresume", "onresume");
		// Util.setListViewHeight(SoftwareTopActivity.this, listView,
		// displayHasUsedHeight);
		// Util.navigationEvent(SoftwareTopActivity.this, 2);
		try {

			if (secondLoading) {
				if (Currentposition != 1) {
					Util.print("onresume2", "onresume2");
					thridFloorAdapter.notifyDataSetChanged();
				}
			}
			secondLoading = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		try {
			Util.print("cofiguration", "con");
			// Util.setListViewHeight(SoftwareTopActivity.this, listView,
			// displayHasUsedHeight);
			if (Currentposition != 1) {
				thridFloorAdapter.changeItemNameWidthFlag(Util
						.checkCurrentDirection(this));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onConfigurationChanged(newConfig);
	}

	public void setListviewOnScrollStateChange() {
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				try {
					// Util.print("load2----", view.getLastVisiblePosition()+""
					// +(thridFloorDatas.size()));
					if (view.getLastVisiblePosition() == thridFloorDatas.size()) {
						// Util.print("load3----",
						// Boolean.toString(loaded)+"----"+Boolean.toString(loadfinish));
						if (loaded && !loadfinish) {
							Util.print("load", "load");
							new Thread(new Runnable() {

								public void run() {
									try {
										loaded = false;
										Util.print("start", "start" + count);

										if (setAdapterData() != null) {
											length = dataSet.getThreeCates()
													.size();
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
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void main() {
		// if(Util.getNetworkState(SoftwareTopActivity.this))
		// {

		main_progressbar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			public void run() {
				try {
					loaded = false;
					if (Currentposition == 4 || Currentposition == 3 || Currentposition == 2) {

						if (setAdapterData() != null) {
							length = dataSet.getThreeCates().size();
							if (length == 0) {
								loadfinish = true;
							} else {
								thridFloorDatas.addAll(dataSet.getThreeCates());

							}
							myHandler
									.sendEmptyMessage(ApplicationConstants.main);
						} else
							myHandler
									.sendEmptyMessage(ApplicationConstants.webTimeout);
					} else if (Currentposition == 1) {
						myHandler.sendEmptyMessage(SoftwareTopActivity.paihang);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		// }
		// else {
		// networkException(getResources().getString(R.string.web_cannot_connection));
		// }
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
				if (Currentposition == 1) {
					Intent intent = new Intent(SoftwareTopActivity.this,
							SoftwareThridFloorActivity.class);
					if (arg2 == 0) {
						intent.putExtra("flag", "daypaihang");
						intent.putExtra("name", "每日排行");
					} else if (arg2 == 1) {
						intent.putExtra("flag", "weekpaihang");
						intent.putExtra("name", "每周排行");
					} else if (arg2 == 2) {
						intent.putExtra("flag", "monthpaihang");
						intent.putExtra("name", "每月排行");
					}
					intent.putExtra("ID", "");
					startActivity(intent);
				} else {
					Intent intent = new Intent(SoftwareTopActivity.this,
							InformationTabHost.class);
					if (Util.checkCurrentSoftwareDownloading(
							SoftwareTopActivity.this, thridFloorDatas.get(arg2)
									.getName()))
						intent.putExtra("downloading", true);
					intent.putExtra("swid", thridFloorDatas.get(arg2).getSwid());
					intent.putExtra("name", thridFloorDatas.get(arg2).getName());
					Util.print("categoryID", thridFloorDatas.get(arg2)
							.getCategoryID());
					startActivity(intent);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	};

	public View getProgressView() {
		LayoutInflater layoutInflater = LayoutInflater
				.from(SoftwareTopActivity.this);
		return layoutInflater.inflate(R.layout.progressbar, null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			event.startTracking();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
				&& !event.isCanceled()) {
			new AlertDialog.Builder(this.getParent())
					.setIcon(R.drawable.report_error)
					.setTitle(getResources().getString(R.string.exit_title))
					.setMessage(getResources().getString(R.string.exit_message))
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									Util.print("finish_main", "finish main");
									finish();
									SharedPrefsUtil.delValue(SoftwareTopActivity.this, "DomainName");
//									android.os.Process
//											.killProcess(android.os.Process
//													.myPid());
//									System.exit(0);
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