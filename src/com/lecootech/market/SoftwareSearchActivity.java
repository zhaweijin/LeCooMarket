package com.lecootech.market;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lecootech.market.adapter.SoftwareThridFloorAdapter;
import com.lecootech.market.common.ApplicationConstants;
import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.common.WebAddress;
import com.lecootech.market.data.MessageControl;
import com.lecootech.market.data.ThridFloorData;
import com.lecootech.market.handle.DataSet;

public class SoftwareSearchActivity extends Activity implements
		OnGestureListener, OnTouchListener, SensorEventListener {
	private GestureDetector m_GestureDetector;
	private SensorManager sensormanager;
	private LinearLayout layoutlistView;
	private ListView listView;
	private ProgressBar main_progressbar;
	private SoftwareThridFloorAdapter thridFloorAdapter;
	private ArrayList<ThridFloorData> thridFloorDatas = new ArrayList<ThridFloorData>();

	// private ProgressDialog progressDialog;
	private int count = 0;
	private View progressView;

	private int displayHasUsedHeight = 40 + 60 + 30 + 40;

	// resume loading
	private boolean secondLoading = false;

	private int search_result_size = 0;

	private LinearLayout layout_search_hot;
	private LinearLayout layout;
	public final static LinearLayout.LayoutParams LP_FW = new LinearLayout.LayoutParams(
			LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

	public final static LinearLayout.LayoutParams LP_FF = new LinearLayout.LayoutParams(
			LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

	public final static LinearLayout.LayoutParams WC_WC = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);

	// top layout background
	private LinearLayout layout_add_view;
	private RelativeLayout layoutLeft;
	private RelativeLayout layoutRight;
	private Button searchButton;
	private AutoCompleteTextView searchInput;
	private Button clearSearchInput;

	private List<Long> time = new ArrayList<Long>();
	private int Currentposition = 1;

	private InputMethodManager imm;

	private DataSet dataSet;
	private DataSet hotDataSet = null;

	private int hot_perpage_size = 12;
	private int current_page = 1;
	private int hot_count = 0;
	private int hot_page_count = 0;
	private boolean hotpageLodingFinish = true;

	//network failed 
	private RelativeLayout networklayout = null;
	
	// 分页
	private boolean loadfinish = false;
	private boolean loaded = false;
	private int page = 1;
	private int perpage = 10;
	private int length;
	private int pagecount = 1;

	private Animation ani1;

	Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case ApplicationConstants.search:
					if(networklayout!=null)
						networklayout.setVisibility(View.INVISIBLE);
					main_progressbar.setVisibility(View.INVISIBLE);
					if (Currentposition == 1) {
						initHotData();
						handleHotPage();
					} else {
						int size = dataSet.getThreeCates().size();
						search_result_size = size;
						if (size <= 0) {
							layout_add_view.removeAllViews();
							TextView textView = new TextView(
									SoftwareSearchActivity.this);
							textView.setTextColor(Color.BLACK);
							textView.setText("无搜索结果");
							layout_add_view.addView(textView);

						} else {
							if (dataSet.getCurrentSoftwareCount() > perpage) {
								listView.addFooterView(progressView);
								setListviewOnScrollStateChange();
							}

							thridFloorAdapter = new SoftwareThridFloorAdapter(
									SoftwareSearchActivity.this,
									SoftwareSearchActivity.this,
									thridFloorDatas,
									Util.checkCurrentDirection(SoftwareSearchActivity.this),
									listView,this);
							listView.setAdapter(thridFloorAdapter);
							
							loaded = true;
							page++;
						}
						imm.hideSoftInputFromWindow(
								searchInput.getWindowToken(), 0); // myEdit是你的EditText对象
					}
					break;
				case ApplicationConstants.update:
					if (Currentposition == 2) {
						thridFloorAdapter.notifyDataSetChanged();

						if (count >= ApplicationConstants.perPageSize)
							listView.removeFooterView(progressView);
						if (dataSet.getThreeCates().size() < 10) {
							myHandler
									.sendEmptyMessage(ApplicationConstants.remove_foot_view);
							loadfinish = true;
						}
						loaded = true;
						page++;
					}
					break;
				case ApplicationConstants.webTimeout:
					networkException(getResources().getString(
							R.string.web_load_timeout));
					break;
				case ApplicationConstants.exception:
					main_progressbar.setVisibility(View.INVISIBLE);
					Toast.makeText(SoftwareSearchActivity.this, "加载网络异常",
							Toast.LENGTH_LONG).show();
					break;
				case ApplicationConstants.remove_foot_view:
					listView.removeFooterView(progressView);
					thridFloorAdapter.notifyDataSetChanged();
					break;
				case MessageControl.ADAPTER_CHANGE:
					thridFloorAdapter.notifyDataSetChanged();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.software_search);
		m_GestureDetector = new GestureDetector(this);
		sensormanager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Toast.makeText(SoftwareSearchActivity.this, "摇晃手机或划动屏幕获取更多热点！", Toast.LENGTH_LONG).show();
		init();

	}

	@Override
	protected void onResume() {
		try {
			if (Currentposition == 2) {
				// 设置listview的高度
				
				if (secondLoading) {
					if (dataSet.getThreeCates().size() > 0) {
					
						thridFloorAdapter.notifyDataSetChanged();
					}
				}
			}
			secondLoading = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		sensormanager.registerListener(this,
				sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);
		super.onResume();
	}

	public void init() {
		ani1 = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.search_tween);

		layout_add_view = (LinearLayout) findViewById(R.id.layout_add_view);

		layoutLeft = (RelativeLayout) findViewById(R.id.hot);
		layoutRight = (RelativeLayout) findViewById(R.id.search_result_button);
		layoutLeft.setOnClickListener(onClickListener);
		layoutRight.setOnClickListener(onClickListener);

		layoutLeft.setBackgroundResource(R.drawable.topbar);
		layoutRight.setBackgroundColor(Color.TRANSPARENT);

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		main_progressbar = (ProgressBar) findViewById(R.id.main_progressbar);

		addHotLayout();
		main();
		ButtonEvent();
	}

	public void main() {
		// if(Util.getNetworkState(SoftwareSearchActivity.this))
		// {
		main_progressbar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			public void run() {
				try {
					
					
					if (Currentposition == 2) {

						loaded = false;
						
						if (setAdapterData() != null) {
							length = dataSet.getThreeCates().size();
							if (length == 0) {
								loadfinish = true;
							} else {
								thridFloorDatas.addAll(dataSet.getThreeCates());
							}
							myHandler
									.sendEmptyMessage(ApplicationConstants.search);
						} else {
							myHandler
									.sendEmptyMessage(ApplicationConstants.webTimeout);
						}
					} else if (Currentposition == 1) {
						if (setAdapterData() != null)
							myHandler
									.sendEmptyMessage(ApplicationConstants.search);
						else
							myHandler
									.sendEmptyMessage(ApplicationConstants.webTimeout);
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

	private DataSet setAdapterData() {
		try {
			if (Currentposition == 1) {
				if (hotDataSet != null) {
					dataSet = hotDataSet;
				} else {
					dataSet = Util.getSoftwareWebData(this,SharedPrefsUtil.getValue(
							SoftwareSearchActivity.this, "DomainName",
							WebAddress.comString)
							+ WebAddress.searchHot);
					hotDataSet = dataSet;

				}
			} else if (Currentposition == 2) {
				dataSet = Util.getSoftwareWebData(this,SharedPrefsUtil.getValue(
						SoftwareSearchActivity.this, "DomainName",
						WebAddress.comString)
						+ WebAddress.searchResult
						// 保留空格，服务器上空格也是一个字符
						+ URLEncoder.encode(searchInput.getText().toString(),
								"gb2312")
						+ "&"
						+ Util.postPerpageNum(page, perpage));
				SharedPrefsUtil.putValue(SoftwareSearchActivity.this,
						"Keyword", searchInput.getText().toString());
			}
			return dataSet;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void ButtonEvent() {
		searchButton = (Button) findViewById(R.id.do_search);
		clearSearchInput = (Button) findViewById(R.id.clear_text);
		searchInput = (AutoCompleteTextView) findViewById(R.id.search_text);

		clearSearchInput.setOnClickListener(onClickListener);
		searchButton.setOnClickListener(onClickListener);
	}

	AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			try {
				Util.print("onitemclick", "onitemclick");
				Intent intent = new Intent(SoftwareSearchActivity.this,
						InformationTabHost.class);
				if (Util.checkCurrentSoftwareDownloading(
						SoftwareSearchActivity.this, thridFloorDatas.get(arg2)
								.getName()))
					intent.putExtra("downloading", true);
				intent.putExtra("swid", thridFloorDatas.get(arg2).getSwid());
				intent.putExtra("name", thridFloorDatas.get(arg2).getName());
				Util.print("categoryID", thridFloorDatas.get(arg2)
						.getCategoryID());
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	};

	public void addSearchResultLayout() {
		layout_add_view.removeAllViews();

		// 初始化listview
		listView = new ListView(SoftwareSearchActivity.this);
		listView.setDivider(getResources().getDrawable(R.drawable.gallery_diver));
		listView.setVerticalFadingEdgeEnabled(false);
		layout_add_view.addView(listView, ApplicationConstants.LP_FF);
		// Util.setListViewHeight(SoftwareSearchActivity.this, listView,
		// displayHasUsedHeight);

		listView.setOnItemClickListener(onItemClickListener);
		// 增加listview底部进度条
		progressView = getProgressView();

		layoutRight.setSelected(true);
		layoutLeft.setSelected(false);
	}

	public void addHotLayout() {
		layout_add_view.removeAllViews();

		View search_hot = LayoutInflater.from(this).inflate(
				R.layout.software_search_hot, null);

		layout_add_view.addView(search_hot);
		layout_search_hot = (LinearLayout) search_hot
				.findViewById(R.id.layout_add_layout);
		layout_add_view.setOnTouchListener(this);
		layout_add_view.setLongClickable(true);
		layoutLeft.setSelected(true);
		layoutRight.setSelected(false);
	}

	public void setHotData(int current_hotpage, int hotperpage) {
		try {
			layout_search_hot.removeAllViews();

			DisplayMetrics localDisplayMetrics = getResources()
					.getDisplayMetrics();
			int width = localDisplayMetrics.widthPixels;
			int hight = localDisplayMetrics.heightPixels;
			int size = hotperpage;
			int i = 0;
			int j = hotperpage * (current_hotpage - 1);
			int tempj = j;

			int layout_width = width;

			while (true) {

				layout_width = width - 20;
				layout = new LinearLayout(this);
				layout.setLayoutParams(LP_FW);
				layout_search_hot.addView(layout);
				if (i > size || j == hot_count)
					break;
				while (true) {
					j = tempj + i;
					if (i > size || j == hot_count)
						break;

					TextView textView = new TextView(this);
					int[] x = new int[4];
					int[] y = new int[4];
					if (hight == 240) {
						for (int k = 0; k < 4; k++) {
							x[k] = (int) (Math.random() * 5);
							y[k] = (int) (Math.random() * 50);
						}
					} else if (hight == 320) {
						for (int k = 0; k < 4; k++) {
							x[k] = (int) (Math.random() * 18);
							y[k] = (int) (Math.random() * 50);
						}
					} else if (hight == 480) {
						for (int k = 0; k < 4; k++) {
							x[k] = (int) (Math.random() * 45);
							y[k] = (int) (Math.random() * 50);
						}
					} else if (hight == 800) {
						for (int k = 0; k < 4; k++) {
							x[k] = (int) (Math.random() * 75);
							y[k] = (int) (Math.random() * 100);
						}
					} else {
						for (int k = 0; k < 4; k++) {
							x[k] = (int) (Math.random() * 75);
							y[k] = (int) (Math.random() * 100);
						}
					}

					textView.setLayoutParams(WC_WC);
					textView.setOnTouchListener(this);
					textView.setLongClickable(true);
					textView.setTextColor(getResources().getColor(R.color.search_tag));
					String str;
					if (dataSet.getSearchHotDatas().get(j) != null) {
						 str = dataSet.getSearchHotDatas().get(j).getName();
					}else {
						 str = dataSet.getSearchHotDatas().get(j-1).getName();
					}
					
					textView.setText(str);
					textView.setTextColor(Color.parseColor(dataSet
							.getSearchHotDatas().get(j).getColor()));
					String tempsize = dataSet.getSearchHotDatas().get(j)
							.getSize();
					textView.setTextSize(Integer.parseInt(tempsize.substring(0,
							tempsize.length() - 2)));

					textView.setPadding(y[0], x[0], 0, 0);
					TextPaint pTextPaint = textView.getPaint();
					int f1 = (int) pTextPaint.measureText(str)
							+ textView.getPaddingLeft()
							+ textView.getPaddingRight();

					if (layout_width < f1) {
						j = tempj + i;
						break;
					}
					/*
					 * 注释下划线
					 */
					// String str2 = "<u>" + str + "</u>";
					String str2 = str;
					textView.setText(Html.fromHtml(str2));
					textView.setOnClickListener(onListener);
					layout.addView(textView);
					layout_search_hot.startAnimation(ani1);
					layout_width = layout_width - f1;
					i++;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	View.OnClickListener onListener = new View.OnClickListener() {

		public void onClick(View v) {
			try {
				loaded = true;
				loadfinish = false;
				page = 1;
				Intent intent = new Intent(SoftwareSearchActivity.this,
						InformationTabHost.class);
				TextView textView = (TextView) v;
				String str = textView.getText().toString();
				int id = findPositionByName(str);
				searchInput.setText(str);

				layoutLeft.setBackgroundColor(Color.TRANSPARENT);
				layoutRight.setBackgroundResource(R.drawable.topbar);
				Currentposition = 2;
				addSearchResultLayout();
				thridFloorDatas.clear();
				main();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

	public int findPositionByName(String name) {
		if (dataSet.getSearchHotDatas() != null) {
			for (int i = 0; i < dataSet.getSearchHotDatas().size(); i++) {
				if (dataSet.getSearchHotDatas().get(i).getName().equals(name)) {
					return i;
				}
			}
		}
		return -1;
	}

	public void underline(int start, int end, TextView text) {
		SpannableStringBuilder spannable = new SpannableStringBuilder(text
				.getText().toString());
		CharacterStyle span = new UnderlineSpan();
		spannable.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		text.setText(spannable);
	}

	public View getProgressView() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		return layoutInflater.inflate(R.layout.progressbar, null);
	}

	View.OnClickListener onClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			try {
				switch (v.getId()) {
				case R.id.hot:
					if (Currentposition != 1) {
						current_page = 1;

						Currentposition = 1;
						count = 0;
						layoutLeft.setBackgroundResource(R.drawable.topbar);
						layoutRight.setBackgroundColor(Color.TRANSPARENT);

						addHotLayout();
						main();
					}
					break;
				case R.id.search_result_button:
					if (Currentposition != 2) {
						Currentposition = 2;
						page = 1;
						loaded = true;
						loadfinish = false;
						count = 0;
						layoutLeft.setBackgroundColor(Color.TRANSPARENT);
						layoutRight.setBackgroundResource(R.drawable.topbar);
						if (!searchInput.getText().toString().equals("")) {
							if (thridFloorDatas != null)
								thridFloorDatas.clear();
							addSearchResultLayout();
							main();
						} else {
							layoutLeft.setSelected(false);
							layoutRight.setSelected(true);

							search_result_size = 0;

							layout_add_view.removeAllViews();
							TextView textView = new TextView(
									SoftwareSearchActivity.this);
							textView.setTextColor(Color.BLACK);
							textView.setText("无搜索结果");
							layout_add_view.addView(textView);
						}
					}
					break;
				case R.id.clear_text:
					searchInput.setText("");
					break;
				case R.id.do_search:

					layoutLeft.setBackgroundColor(Color.TRANSPARENT);
					layoutRight.setBackgroundResource(R.drawable.topbar);
					Currentposition = 2;
					addSearchResultLayout();
					thridFloorDatas.clear();
					if (searchInput.getText().toString().trim().equals("")) {

						layout_add_view.removeAllViews();
						TextView textView = new TextView(
								SoftwareSearchActivity.this);
						textView.setTextColor(Color.BLACK);
						textView.setText("无搜索结果");
						layout_add_view.addView(textView);
					} else {
						page = 1;
						loaded = true;
						loadfinish = false;
						main();
					}

					break;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public void setListviewOnScrollStateChange() {
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_FLING:
					// if (view.getLastVisiblePosition() == view.getCount() - 1)
					// {
					if (loaded && !loadfinish) {
						new Thread(new Runnable() {

							public void run() {
								try {

									loaded = false;
									
									if (setAdapterData() != null) {
										length = dataSet.getThreeCates().size();
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
					// }
					break;
				}
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		try {
			if (Currentposition == 1) {
				handleHotPage();
			} else {
				if (search_result_size > 0) {
					thridFloorAdapter.changeItemNameWidthFlag(Util
							.checkCurrentDirection(this));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onConfigurationChanged(newConfig);
	}

	public void initHotData() {
		hot_count = dataSet.getSearchHotDatas().size();
		hot_page_count = hot_count / hot_perpage_size;
		if (hot_count % hot_perpage_size != 0) {
			hot_page_count = hot_page_count + 1;
		}
		Util.print("hot_count", "" + hot_count);
		Util.print("hot_page_count", "" + hot_page_count);

	}

	public void handleHotPage() {
		Util.print("current", current_page + "");
		if (current_page > hot_page_count)
			current_page = 1;
		setHotData(current_page, hot_perpage_size);
		current_page++;
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
									SharedPrefsUtil.delValue(SoftwareSearchActivity.this, "DomainName");
									Util.print("finish_main", "finish main");
									finish();
									// android.os.Process
									// .killProcess(android.os.Process
									// .myPid());
									// System.exit(0);
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

	public boolean onTouch(View v, MotionEvent event) {
		try {
			m_GestureDetector.onTouchEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean onDown(MotionEvent e) {
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		// if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
		// && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
		// handleHotPage();
		//
		// } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
		// && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
		// handleHotPage();
		//
		// }

		handleHotPage();

		return false;

	}

	public void onLongPress(MotionEvent e) {

	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	public void onShowPress(MotionEvent e) {

	}

	@Override
	protected void onPause() {
		sensormanager.unregisterListener(this);
		super.onPause();
	}

	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}


	public void onSensorChanged(SensorEvent event) {
		float[] valuse = event.values;
		if (Math.abs((int)valuse[0])>7) {

			time.add(System.currentTimeMillis());

			if (time.size() > 1) {
				if (time.get(time.size() - 1) - time.get(time.size() - 2) > 2000) {
					handleHotPage();
				}
			} else {
				handleHotPage();
			}

			if (time.size() > 10) {
				time.clear();
			}

		}
	}


	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

}