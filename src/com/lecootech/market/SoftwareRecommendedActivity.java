package com.lecootech.market;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lecootech.market.adapter.SoftwareThridFloorAdapter;
import com.lecootech.market.adapter.TopImageGalleryAdapter;
import com.lecootech.market.common.ApplicationConstants;
import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.common.WebAddress;
import com.lecootech.market.data.ImageData;
import com.lecootech.market.data.ImagesPush;
import com.lecootech.market.data.MessageControl;
import com.lecootech.market.data.ThridFloorData;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.DownloadDatabase;
import com.lecootech.market.download.ImagePageTask;
import com.lecootech.market.handle.DataSet;

public class SoftwareRecommendedActivity extends Activity {

	private LinearLayout layoutlistView;
	private ListView listView;
	private ProgressBar main_progressbar;
	private ListView leftListView;

	private int domainnamenum = 0;

	private SoftwareThridFloorAdapter thridFloorAdapter;
	private ArrayList<ThridFloorData> thridFloorDatas = new ArrayList<ThridFloorData>();

	private TopImageGalleryAdapter topImageGalleryAdapter = null;
	private ArrayList<ImagesPush> imagesPushs = new ArrayList<ImagesPush>();

	public static AlertDialog alertdialog;
	private ProgressDialog progressDialog;
	private View progressView;

	private Gallery topImageGallery;
	private TextView topImageTextName;
	private LinearLayout layout_display_select;
	private ArrayList<ImageView> displayImageViews = new ArrayList<ImageView>();
	// page flag
	private int Currentposition = 1;
	public final static LinearLayout.LayoutParams WC_WC = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);
	// network failed
	private RelativeLayout networklayout = null;

	// dataset
	private DataSet dataSet;

	// resume loading
	private boolean secondLoading = false;

	SharedPreferences settingPreferences;
	// market update
	// 更新判断使用的变量
	private String newVersionName;

	// 分页
	private boolean loadfinish = false;
	private boolean loaded = false;
	private int page = 1;
	private int perpage = 10;
	private int length;

	private View top_view;

	private boolean checkMarket = false;

	// top flash thread
	private int curTopPosition;
	private int topPicSize;
	private int loadedSize;
	private boolean topFlashThreadState = true;
	private boolean topFlashThreadRuning = false;
	public static HashMap<String, Bitmap> imageCache = new HashMap<String, Bitmap>();
	/**
	 * @author zhaweijin
	 * @function UI线程的处理
	 */
	private final static int update_maket = 0x545;
	private final static int update_market_tips = 0x655;
	private final static int progress_cancel = 0x334;
	private final static int displayResponsibilty = 0x444;
	private final static int topFlashImageView = 0x642;
	Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case displayResponsibilty:
					if (checkIsDisplayResonsibility()) {
						if (!settingPreferences.getBoolean(
								"displayResponsibility", false)) {

							alertdialog = new AlertDialog.Builder(
									SoftwareRecommendedActivity.this
											.getParent())
									.setCancelable(false)
									.setOnKeyListener(new OnKeyListener() {

										public boolean onKey(
												DialogInterface dialog,
												int keyCode, KeyEvent event) {
											if (event.getKeyCode() == KeyEvent.KEYCODE_SEARCH) {
												return true;
											}

											return false;
										}

									})
									.setTitle("免责声明")
									.setMessage(
											"任何使用乐酷市场产品的用户均应仔细阅读本声明，用户可选择不使用乐酷市场产品，用户使用乐酷市场产品的行为将被视为对本声明全部内容的认可。\n"
													+ "1、除乐酷市场注明之服务条款外，其他一切因使用乐酷市场产品而引致之任何意外、疏忽、合约毁坏、诽谤、版权或知识产权侵犯及其所造成的损失（包括因下载而感染手机恶意软件等），乐酷市场概不负责，亦不承担任何法律责任。\n"
													+ "2、任何通过乐酷市场的页面而链接及得到之资讯、产品、下载信息及服务均系用户自行发布，乐酷市场对其合法性概不负责，亦不承担任何法律责任。\n"
													+ "3、乐酷市场所有内容并不反映任何乐酷市场之意见。\n"
													+ "4、任何单位或个人认为通过乐酷市场的内容可能涉嫌侵犯其合法权益，应该及时向乐酷市场书面反馈，并提供身份证明、权属证明及详细侵权情况证明，乐酷市场在收到上述法律文件后，将会尽快移除被控侵权内容。\n"
													+ "以上声明内容的解释权归乐酷市场所有。\n")
									.setPositiveButton(
											"不同意",
											new DialogInterface.OnClickListener() {

												public void onClick(
														DialogInterface dialog,
														int which) {
													finish();
												}
											})
									.setNegativeButton(
											"同意并免费使用",
											new DialogInterface.OnClickListener() {

												public void onClick(
														DialogInterface dialog,
														int which) {
													settingPreferences
															.edit()
															.putBoolean(
																	"displayResponsibility",
																	true)
															.commit();
													myHandler
															.sendEmptyMessage(ApplicationConstants.main);
												}
											}).show();

						} else {
							myHandler
									.sendEmptyMessage(ApplicationConstants.main);
						}
					} else {
						myHandler.sendEmptyMessage(ApplicationConstants.main);
					}

					break;
				case ApplicationConstants.main:

					if (networklayout != null)
						networklayout.setVisibility(View.INVISIBLE);

					if (Currentposition == 1) {
						if (dataSet.getCurrentSoftwareCount() > perpage) {
							leftListView.addFooterView(progressView);
							setLeftListviewOnScrollStateChange();
						}

						imagesPushs = dataSet.getImagesPushs();
						topImageGalleryAdapter = new TopImageGalleryAdapter(
								SoftwareRecommendedActivity.this, imagesPushs,
								topImageGallery);
						topImageGallery.setAdapter(topImageGalleryAdapter);

						topPicSize = imagesPushs.size();

						topImageItemListener();
						loadImageFromUrl(imagesPushs);
						for (int i = 0; i < imagesPushs.size(); i++) {
							Boolean noSort = false;
							// Util.print("aa", imagesPushs.get(i).getSort());
							if (imagesPushs.get(i).getSort().equals("1")) {
								curTopPosition = i;
								topImageGallery.setSelection(Integer.MAX_VALUE
										/ 2 + i);
								topImageTextName.setText(imagesPushs.get(i)
										.getName());
								displayImageViews.get(i).setBackgroundResource(
										R.drawable.top_image_dot_selected);
								noSort = true;
								break;
							}

							if (!noSort) {
								curTopPosition = 0;
								topImageGallery
										.setSelection(Integer.MAX_VALUE / 2);
								topImageTextName.setText(imagesPushs.get(0)
										.getName());
								displayImageViews.get(0).setBackgroundResource(
										R.drawable.top_image_dot_selected);
							}
						}

						if (!checkMarket) {
							autoCheckMarket();
							checkMarket = true;
						}

						thridFloorAdapter = new SoftwareThridFloorAdapter(
								SoftwareRecommendedActivity.this,
								SoftwareRecommendedActivity.this,
								thridFloorDatas,
								Util.checkCurrentDirection(SoftwareRecommendedActivity.this),
								leftListView, this);
						leftListView.setAdapter(thridFloorAdapter);

						myHandler.sendEmptyMessage(ApplicationConstants.cancel);

						loaded = true;
						page++;
					} else if (Currentposition == 3 || Currentposition == 2) {
						leftListView.addFooterView(progressView);
						setLeftListviewOnScrollStateChange();

						thridFloorAdapter = new SoftwareThridFloorAdapter(
								SoftwareRecommendedActivity.this,
								SoftwareRecommendedActivity.this,
								thridFloorDatas,
								Util.checkCurrentDirection(SoftwareRecommendedActivity.this),
								leftListView, this);
						leftListView.setAdapter(thridFloorAdapter);

						loaded = true;
						page++;
					}
					main_progressbar.setVisibility(View.INVISIBLE);
					break;
				case ApplicationConstants.update:
					Util.print("update", "" + dataSet.getThreeCates().size());
					thridFloorAdapter.notifyDataSetChanged();
					if (dataSet.getThreeCates().size() < 10) {
						myHandler
								.sendEmptyMessage(ApplicationConstants.remove_foot_view);
						loadfinish = true;
					}
					loaded = true;
					page++;
					break;
				case ApplicationConstants.webTimeout:
					domainnamenum++;
					main();

					break;
				case ApplicationConstants.webTimeout2:
					networkException(getResources().getString(
							R.string.web_load_timeout));
					break;
				case ApplicationConstants.exception:
					main_progressbar.setVisibility(View.INVISIBLE);
					Toast.makeText(SoftwareRecommendedActivity.this, "加载网络异常",
							Toast.LENGTH_LONG).show();
					break;
				case update_maket:
					MarketUpdate();
					break;
				case ApplicationConstants.remove_foot_view:
					leftListView.removeFooterView(progressView);
					thridFloorAdapter.notifyDataSetChanged();

					break;
				case ApplicationConstants.cancel:
					main_progressbar.setVisibility(View.INVISIBLE);
					break;
				case progress_cancel:
					progressDialog.cancel();
					break;
				case update_market_tips:
					progressDialog.cancel();
					new AlertDialog.Builder(
							SoftwareRecommendedActivity.this.getParent())
							.setTitle("乐酷市场")
							.setMessage("当前市场已经是最新！")
							.setPositiveButton(
									getResources().getString(R.string.ok),
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {

										}
									}).show();
					break;
				case MessageControl.ADAPTER_CHANGE:
					thridFloorAdapter.notifyDataSetChanged();
					break;
				case topFlashImageView:
					if (topImageGalleryAdapter != null) {
						topImageGalleryAdapter.notifyDataSetChanged();
						topImageGallery.setSelection(Integer.MAX_VALUE / 2
								+ curTopPosition);
						topImageTextName.setText(imagesPushs
								.get(curTopPosition).getName());
						displayImageViews.get(curTopPosition)
								.setBackgroundResource(
										R.drawable.top_image_dot_selected);
					}
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
		setContentView(R.layout.software_main);

		try {
			// Util.addActivityToStack(this, "SoftwareRecommendedActivity");

			Currentposition = getIntent().getIntExtra("position", 1);
			init();
			main();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private DataSet setAdapterData() {
		try {

			SharedPrefsUtil.getValue(SoftwareRecommendedActivity.this,
					"DomainName", WebAddress.comString);
			Util.print("domain", "" + domainnamenum);
			Util.print(
					"webpath",
					WebAddress.comString
							+ WebAddress.edit_recommentString
							+ Util.postPerpageNum2(
									page,
									perpage,
									WebAddress
											.getproductID(SoftwareRecommendedActivity.this),
									Util.getAPNType(SoftwareRecommendedActivity.this))
							+ "&version="
							+ getPackageManager().getPackageInfo(
									getPackageName(), 0).versionName);
			if (domainnamenum == 1) {
				if (Currentposition == 1) {

					dataSet = Util
							.getSoftwareWebData(
									this,
									WebAddress.comString
											+ WebAddress.edit_recommentString
											+ Util.postPerpageNum2(
													page,
													perpage,
													WebAddress
															.getproductID(SoftwareRecommendedActivity.this),
													Util.getAPNType(SoftwareRecommendedActivity.this))
											+ "&version="
											+ getPackageManager()
													.getPackageInfo(
															getPackageName(), 0).versionName);
				} else if (Currentposition == 2) {
					dataSet = Util
							.getSoftwareWebData(
									this,
									WebAddress.comString
											+ WebAddress.paihangTotal
											+ Util.postPerpageNum2(
													page,
													perpage,
													WebAddress
															.getproductID(SoftwareRecommendedActivity.this),
													Util.getAPNType(SoftwareRecommendedActivity.this)));

				} else if (Currentposition == 3) {

					dataSet = Util
							.getSoftwareWebData(
									this,
									WebAddress.comString
											+ WebAddress.new_recommentString
											+ Util.postPerpageNum2(
													page,
													perpage,
													WebAddress
															.getproductID(SoftwareRecommendedActivity.this),
													Util.getAPNType(SoftwareRecommendedActivity.this)));

				}
				SharedPrefsUtil.putValue(SoftwareRecommendedActivity.this,
						"DomainName", WebAddress.comString);
			} else if (domainnamenum == 2) {
				if (Currentposition == 1) {

					dataSet = Util
							.getSoftwareWebData(
									this,
									WebAddress.comString2
											+ WebAddress.edit_recommentString
											+ Util.postPerpageNum2(
													page,
													perpage,
													WebAddress
															.getproductID(SoftwareRecommendedActivity.this),
													Util.getAPNType(SoftwareRecommendedActivity.this))
											+ "&version="
											+ getPackageManager()
													.getPackageInfo(
															getPackageName(), 0).versionName);
				} else if (Currentposition == 2) {
					dataSet = Util
							.getSoftwareWebData(
									this,
									WebAddress.comString2
											+ WebAddress.paihangTotal
											+ Util.postPerpageNum2(
													page,
													perpage,
													WebAddress
															.getproductID(SoftwareRecommendedActivity.this),
													Util.getAPNType(SoftwareRecommendedActivity.this)));

				} else if (Currentposition == 3) {

					dataSet = Util
							.getSoftwareWebData(
									this,
									WebAddress.comString2
											+ WebAddress.new_recommentString
											+ Util.postPerpageNum2(
													page,
													perpage,
													WebAddress
															.getproductID(SoftwareRecommendedActivity.this),
													Util.getAPNType(SoftwareRecommendedActivity.this)));

				}

				SharedPrefsUtil.putValue(SoftwareRecommendedActivity.this,
						"DomainName", WebAddress.comString2);
			} else {
				if (Currentposition == 1) {
					// Util.print("url", WebAddress.comString3
					// + WebAddress.edit_recommentString
					// + Util.postPerpageNum2(
					// page,
					// perpage,
					// WebAddress
					// .getproductID(SoftwareRecommendedActivity.this),
					// Util.getAPNType(SoftwareRecommendedActivity.this))
					// +"&version="+getPackageManager().getPackageInfo(
					// getPackageName(), 0).versionName);
					dataSet = Util
							.getSoftwareWebData(
									this,
									WebAddress.comString3
											+ WebAddress.edit_recommentString
											+ Util.postPerpageNum2(
													page,
													perpage,
													WebAddress
															.getproductID(SoftwareRecommendedActivity.this),
													Util.getAPNType(SoftwareRecommendedActivity.this)));
				} else if (Currentposition == 2) {
					dataSet = Util
							.getSoftwareWebData(
									this,
									WebAddress.comString3
											+ WebAddress.paihangTotal
											+ Util.postPerpageNum2(
													page,
													perpage,
													WebAddress
															.getproductID(SoftwareRecommendedActivity.this),
													Util.getAPNType(SoftwareRecommendedActivity.this)));

				} else if (Currentposition == 3) {

					dataSet = Util
							.getSoftwareWebData(
									this,
									WebAddress.comString3
											+ WebAddress.new_recommentString
											+ Util.postPerpageNum2(
													page,
													perpage,
													WebAddress
															.getproductID(SoftwareRecommendedActivity.this),
													Util.getAPNType(SoftwareRecommendedActivity.this)));

				}
				SharedPrefsUtil.putValue(SoftwareRecommendedActivity.this,
						"DomainName", WebAddress.comString3);
			}

			Log.d("MyDebug",
					SharedPrefsUtil.getValue(SoftwareRecommendedActivity.this,
							"DomainName", "mmmmm")
							+ WebAddress.edit_recommentString
							+ Util.postPerpageNum2(
									page,
									perpage,
									WebAddress
											.getproductID(SoftwareRecommendedActivity.this),
									Util.getAPNType(SoftwareRecommendedActivity.this)));
			return dataSet;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (Currentposition == 1) {
			if (!settingPreferences.getBoolean("checkbox_switch_display_start",
					true)) {
				init();
				main();
			}
		}

		try {
			thridFloorAdapter.changeItemNameWidthFlag(Util
					.checkCurrentDirection(this));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		// if(Currentposition==1 || Currentposition==3)
		// {
		// Util.setListViewHeight(SoftwareRecommendedActivity.this,
		// leftListView, displayHasUsedHeight);
		// }
		// Util.navigationEvent(SoftwareRecommendedActivity.this, 1);
		//
		try {
			if (secondLoading) {
				thridFloorAdapter.notifyDataSetChanged();
			}
			secondLoading = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		topFlashThreadState = true;
		if (!topFlashThreadRuning) {
			flashTopImageView();
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		topFlashThreadState = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void setLeftListviewOnScrollStateChange() {
		leftListView.setOnScrollListener(new AbsListView.OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// lastItem = firstVisibleItem + visibleItemCount - 1;
				// Util.print("lastItem", "aa"+page);
				// Util.print("load2----", view.getLastVisiblePosition()+""
				// +(thridFloorDatas.size()+1));
				Boolean tempflag = false;
				if (Currentposition == 1) {
					if (view.getLastVisiblePosition() == thridFloorDatas.size() + 1)
						tempflag = true;
				} else if (Currentposition == 3 || Currentposition == 2) {
					if (view.getLastVisiblePosition() == thridFloorDatas.size())
						tempflag = true;
				}
				// Util.print("tempflag", Boolean.toString(tempflag));
				if (tempflag) {
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
										if (domainnamenum <= 3) {
											myHandler
													.sendEmptyMessage(ApplicationConstants.webTimeout);
										} else {
											domainnamenum = 1;
											myHandler
													.sendEmptyMessage(ApplicationConstants.webTimeout2);
										}

									}

								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
					}
				}
			}
		});
	}

	public void main() {
		loaded = false;
		loadfinish = false;
		page = 1;
		thridFloorDatas.clear();
		if (thridFloorAdapter != null)
			thridFloorAdapter.notifyDataSetChanged();
		// if(Util.getNetworkState(SoftwareRecommendedActivity.this))
		// {
		main_progressbar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			public void run() {
				try {

					loaded = false;
					if (setAdapterData() != null) {
						length = dataSet.getThreeCates().size();
						if (length == 0) {
							loadfinish = true;
						} else {
							// if (page == 1 && Currentposition == 1) {
							// for (int i = 4; i < perpage; i++) {
							// thridFloorDatas.add(dataSet.getThreeCates()
							// .get(i));
							// }
							// } else {
							thridFloorDatas.addAll(dataSet.getThreeCates());
							// }
						}
						if (Currentposition == 1) {
							myHandler
									.sendEmptyMessage(SoftwareRecommendedActivity.displayResponsibilty);
						} else {
							myHandler
									.sendEmptyMessage(ApplicationConstants.main);
						}

					} else {
						if (domainnamenum <= 3) {
							myHandler
									.sendEmptyMessage(ApplicationConstants.webTimeout);
						} else {
							domainnamenum = 1;
							myHandler
									.sendEmptyMessage(ApplicationConstants.webTimeout2);
						}

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
		networklayout = (RelativeLayout) findViewById(R.id.include_network_failed);
		networklayout.setVisibility(View.VISIBLE);

		Button flashButton = (Button) findViewById(R.id.reflash);
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
				Intent intent = new Intent(SoftwareRecommendedActivity.this,
						InformationTabHost.class);

				if (Currentposition == 1) {
					if (arg2 != 0) {
						if (Util.checkCurrentSoftwareDownloading(
								SoftwareRecommendedActivity.this,
								thridFloorDatas.get(arg2 - 1).getName()))
							intent.putExtra("downloading", true);
						intent.putExtra("swid", thridFloorDatas.get(arg2 - 1)
								.getSwid());
						intent.putExtra("name", thridFloorDatas.get(arg2 - 1)
								.getName());
						Util.print("categoryID", thridFloorDatas.get(arg2 - 1)
								.getCategoryID());
						startActivity(intent);
					}
				} else if (Currentposition == 3 || Currentposition == 2) {
					if (Util.checkCurrentSoftwareDownloading(
							SoftwareRecommendedActivity.this, thridFloorDatas
									.get(arg2).getName()))
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
		LayoutInflater layoutInflater = LayoutInflater.from(this.getParent());
		return layoutInflater.inflate(R.layout.progressbar, null);
	}

	public void init() {

		// progressDialog = Util.getProgressDialog(this.getParent(),"加载中...");
		layoutlistView = (LinearLayout) findViewById(R.id.layout_listview);

		main_progressbar = (ProgressBar) findViewById(R.id.main_progressbar);
		// 增加listview底部进度条
		progressView = getProgressView();

		addLeftListview();
		main_progressbar = (ProgressBar) findViewById(R.id.main_progressbar);

		settingPreferences = PreferenceManager
				.getDefaultSharedPreferences(SoftwareRecommendedActivity.this);

	}

	public void addLeftListview() {
		// 初始化 left listview
		layoutlistView.removeAllViews();
		// leftListView = (ListView)Util.getListView(this);
		leftListView = new ListView(SoftwareRecommendedActivity.this);
		leftListView.setDividerHeight(2);
		// leftListView.setBackgroundColor(R.color.grobal_background);
		leftListView.setDivider(getResources().getDrawable(
				R.drawable.gallery_diver));
		leftListView.setVerticalFadingEdgeEnabled(false);
		leftListView.setDrawSelectorOnTop(false);

		// Animation animation = AnimationUtils.loadAnimation(this,
		// R.anim.slide_right);
		// LayoutAnimationController lac=new
		// LayoutAnimationController(animation);
		// lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
		// lac.setDelay(0.5f);
		// leftListView.setLayoutAnimation(lac);

		if (Currentposition == 1) {
			// add left top image view
			LayoutInflater layoutInflater = LayoutInflater
					.from(SoftwareRecommendedActivity.this);
			top_view = layoutInflater.inflate(R.layout.recomment_top_images,
					null);
			leftListView.addHeaderView(top_view);

			topImageGallery = (Gallery) top_view
					.findViewById(R.id.top_image_gallery);
			topImageTextName = (TextView) top_view
					.findViewById(R.id.top_image_text_name);
			layout_display_select = (LinearLayout) top_view
					.findViewById(R.id.layout_display_select);
		}

		// 设置listview的高度

		layoutlistView.addView(leftListView, ApplicationConstants.LP_FF);

		leftListView.setOnItemClickListener(onItemClickListener);

	}

	public void addOtherListview() {
		layoutlistView.removeAllViews();

		listView = new ListView(SoftwareRecommendedActivity.this);
		listView.setDivider(getResources()
				.getDrawable(R.drawable.gallery_diver));
		listView.setVerticalFadingEdgeEnabled(false);
		// 设置listview的高度
		// Util.setListViewHeight(SoftwareRecommendedActivity.this, listView,
		// displayHasUsedHeight);

		layoutlistView.addView(listView);

		listView.setOnItemClickListener(onItemClickListener);
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
			Intent intent = new Intent(
					SoftwareRecommendedActivity.this.getParent(),
					SettingActivity.class);
			startActivityForResult(intent, 1);
			break;
		case R.id.itemUser:
			// Intent intent2 = new
			// Intent(SoftwareRecommendedActivity.this,Login.class);
			// startActivity(intent2);
			marketFeedback();
			break;
		case R.id.itemContact: // 手动更新
			// Util.dialogContact(this);
			handCheckMarket();
			break;
		case R.id.itemAbout:
			Util.dialogAbout(SoftwareRecommendedActivity.this.getParent());
			break;
		}
		return true;
	}

	public void marketFeedback() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.feedback, null);
		final EditText feedbackEditText = (EditText) view
				.findViewById(R.id.feedback_edit);
		final EditText feedbackEditText_contact = (EditText) view
				.findViewById(R.id.feedback_contact_edit);

		new AlertDialog.Builder(this.getParent())
				.setTitle("反馈建议:")
				.setView(view)
				.setPositiveButton("提交", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						String feedback = feedbackEditText.getText().toString();
						String feedback_contact = feedbackEditText_contact
								.getText().toString();
						if (feedback.equals("")) {
							Toast.makeText(SoftwareRecommendedActivity.this,
									"请输入反馈内容再提交!", 1500).show();
						} else {
							if (feedback.length() > 500) {
								Toast.makeText(
										SoftwareRecommendedActivity.this,
										"评论内容超出500个", 1500).show();
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
											"mobiletype", "edition", "email",
											"version" };
									String[] value = {
											Util.getDeviceID(SoftwareRecommendedActivity.this),
											feedback,
											Util.getMODEL(SoftwareRecommendedActivity.this),
											oldedition,
											feedback_contact,
											getPackageManager().getPackageInfo(
													getPackageName(), 0).versionName };

									Util.CommitDownloadNum(
											SoftwareRecommendedActivity.this,
											SharedPrefsUtil
													.getValue(
															SoftwareRecommendedActivity.this,
															"DomainName",
															WebAddress.comString)
													+ WebAddress.send_feedback,
											key, value);

									Toast.makeText(
											SoftwareRecommendedActivity.this,
											"反馈成功!", 1500).show();
								} catch (Exception e) {
									e.printStackTrace();
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		try {
			if (KeyEvent.KEYCODE_BACK == keyCode) {

				new AlertDialog.Builder(this.getParent())
						.setIcon(R.drawable.report_error)
						.setTitle(getResources().getString(R.string.exit_title))
						.setMessage(
								getResources().getString(R.string.exit_message))
						.setPositiveButton(
								getResources().getString(R.string.ok),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										SharedPrefsUtil
												.delValue(
														SoftwareRecommendedActivity.this,
														"DomainName");
										Util.print("finish_recomment",
												"finish recommnet");
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
			} else {
				return super.onKeyDown(keyCode, event);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * @提示是否要求更新最新Market 通过弹出对话框的方式
	 */
	public void MarketUpdate() {
		// Util.print("content", dataSet.getMarketData().getContent());
		String content = dataSet.getMarketData().getContent();

		content = Util.translateWebChar(content);
		new AlertDialog.Builder(

		SoftwareRecommendedActivity.this.getParent())
				.setMessage("系统检测到乐酷市场发布了新的版本，" + "是否更新?\n\n" + content)
				.setTitle("升级提醒")
				.setIcon(R.drawable.about)
				.setPositiveButton(getString(R.string.ok),
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								try {

									DownloadDatabase downloadDatabase = DatabaseManager
											.getDownloadDatabase(SoftwareRecommendedActivity.this);
									Cursor temCursor = downloadDatabase
											.queryID("乐酷市场");
									int returnvalue = -1;
									int size = temCursor.getCount();
									temCursor.moveToFirst();
									if (size > 0) {
										returnvalue = temCursor.getInt(temCursor
												.getColumnIndexOrThrow(DownloadDatabase.KEY_DOWNLOND_NUM));
										temCursor.close();
										if (returnvalue == 100) {
											downloadDatabase.deleteData("乐酷市场");
										}

										if (returnvalue != 0
												&& returnvalue != 100) {
											Toast.makeText(
													SoftwareRecommendedActivity.this,
													"市场正在下载...", 2000).show();
										} else {
											sendMarketUpdateTimes();
											Util.sendMarketDownloadNotification(
													SoftwareRecommendedActivity.this,
													"乐酷市场", dataSet
															.getMarketData()
															.getDownloadPath(),
													"com.lecootech.market",
													10000, "");
										}
									} else {
										sendMarketUpdateTimes();
										Util.sendMarketDownloadNotification(
												SoftwareRecommendedActivity.this,
												"乐酷市场", dataSet.getMarketData()
														.getDownloadPath(),
												"com.lecootech.market", 10000,
												"");
									}
									if (!temCursor.isClosed())
										temCursor.close();

								} catch (Exception e) {
									e.printStackTrace();
								}

							}
						})
				.setNegativeButton(getString(R.string.cancel),
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								myHandler
										.sendEmptyMessage(ApplicationConstants.cancel);

							}
						}).show();
	}

	public void sendMarketUpdateTimes() {
		try {
			String[] key = { "type", "version", "productID" };
			String[] value = { "market", newVersionName,
					WebAddress.getproductID(SoftwareRecommendedActivity.this) };
			Util.CommitDownloadNum(
					this,
					SharedPrefsUtil.getValue(SoftwareRecommendedActivity.this,
							"DomainName", WebAddress.comString)
							+ WebAddress.send_market_times, key, value);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * 自动更新市场处理autoCheckMarket
	 */
	public void autoCheckMarket() {
		new Thread(new Runnable() {

			public void run() {
				checkupdateMarket();
			}
		}).start();
	}

	/*
	 * 手动更新市场处理
	 */
	public void handCheckMarket() {
		progressDialog = Util.getProgressDialog(
				SoftwareRecommendedActivity.this.getParent(), "更新中...");
		progressDialog.show();
		new Thread(new Runnable() {

			public void run() {
				Editor editor = settingPreferences.edit();
				String oldverionName = "";
				long newtime = System.currentTimeMillis();
				// sendMarketUpdateTimes();
				try {
					oldverionName = getPackageManager().getPackageInfo(
							getPackageName(), 0).versionName;
					Util.print("marketoldversion", oldverionName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (dataSet.getMarketData() != null) {
					newVersionName = dataSet.getMarketData().getVersion();
					Util.print("marketnewversion", newVersionName);
				}
				if (newVersionName != null) {
					if (Util.compareVersions(newVersionName, oldverionName)) {
						Message message = new Message();
						message.what = SoftwareRecommendedActivity.update_maket;
						myHandler.sendMessage(message);
					} else {
						myHandler
								.sendEmptyMessage(SoftwareRecommendedActivity.update_market_tips);
					}
					// myHandler.sendEmptyMessage(SoftwareRecommendedActivity.progress_cancel);
				}
				editor.putLong("updateTime", newtime).commit();
			}
		}).start();
	}

	/*
	 * 检测市场是否有更新 默认一周更新一次 。相应的第一次登录时间记录在配置文件当中
	 */
	public void checkupdateMarket() {
		// Editor editor = settingPreferences.edit();
		try {
			String oldverionName = "";
			try {
				oldverionName = getPackageManager().getPackageInfo(
						"com.lecootech.market", 0).versionName;
				Util.print("marketoldversion", oldverionName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (dataSet.getMarketData() != null) {
				newVersionName = dataSet.getMarketData().getVersion();
				Util.print("marketnewversion", newVersionName);
			}
			if (newVersionName != null) {
				if (Util.compareVersions(newVersionName, oldverionName)) {
					Message message = new Message();
					message.what = SoftwareRecommendedActivity.update_maket;
					myHandler.sendMessage(message);
				}
			}
			// editor.putLong("updateTime", newtime).commit();
			// }
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean checkIsDisplayResonsibility() {
		boolean result = false;
		// if (dataSet.getMarketData().getStatement().equals("1")) {
		// result = true;
		// }
		return result;
	}

	public void topImageItemListener()
	{
		
		for(int i=0;i<imagesPushs.size();i++)
		{
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(new LinearLayout.LayoutParams(WC_WC));
			
			ViewGroup.LayoutParams p=imageView.getLayoutParams();
			LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams)p;
//			lp.leftMargin = 4;
			lp.gravity = Gravity.CENTER_VERTICAL;
            imageView.setLayoutParams(p);
			
			layout_display_select.addView(imageView);
			imageView.setBackgroundResource(R.drawable.top_image_dot);
			displayImageViews.add(imageView);
		}
		
		
		topImageGallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				int id = arg2 % imagesPushs.size();
				
				
				curTopPosition = (arg2-(Integer.MAX_VALUE/2));
//				curTopPosition = id;
				
				topImageTextName.setText(imagesPushs.get(id).getName());
				
				
				for(int i=0;i<imagesPushs.size();i++)
				{
					if(i==id)
					{
						displayImageViews.get(i).setBackgroundResource(R.drawable.top_image_dot_selected);
					}
					else {
						displayImageViews.get(i).setBackgroundResource(R.drawable.top_image_dot);
					}
				}
			}


			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		
		
		topImageGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {


			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Util.print("id--->", ""+(arg2 % imagesPushs.size()));
				int id = arg2 % imagesPushs.size();
				
				
				if(imagesPushs.get(id).getSwid()!=null)
				{
					Util.print("info", "go info"+imagesPushs.get(id).getSwid());
					Intent intent = new Intent(SoftwareRecommendedActivity.this,InformationTabHost.class);
					intent.putExtra("swid", imagesPushs.get(id).getSwid());
					intent.putExtra("name", imagesPushs.get(id).getName());
					startActivity(intent);
				}
				else {
					if(imagesPushs.get(arg2 % imagesPushs.size()).getCategoryid().equals("0"))
					{
						Util.print("zhuangji", "zhuangji");
						Intent intent = new Intent(SoftwareRecommendedActivity.this,
								SoftwareInstallActivity.class);
						startActivity(intent);
					}
					else {
						Util.print("fenlei", "fenlei"+imagesPushs.get(arg2 % imagesPushs.size()).
								getCategoryid());
						Intent intent = new Intent(SoftwareRecommendedActivity.this,
								SoftwareThridFloorTabHost.class);
						intent.putExtra("name", imagesPushs.get(id)
								.getName());
						intent.putExtra("ID", imagesPushs.get(id).getCategoryid());
						intent.putExtra("flag", "normal");
						startActivity(intent);
						
					}
				}
			}
			
		});
	}

	public void flashTopImageView() {
		new Thread(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				try {
					topFlashThreadRuning = true;
					while (topFlashThreadState) {
						// Util.print("flash-->", "flash"+loadedSize +
						// imageCache.size());

						Thread.sleep(5 * 1000);
						if (loadedSize == imagesPushs.size()) {
							// Util.print("flash", "flash");
							curTopPosition++;
							if (curTopPosition > topPicSize - 1) {
								curTopPosition = 0;
							}
							myHandler
									.sendEmptyMessage(SoftwareRecommendedActivity.topFlashImageView);
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				topFlashThreadRuning = false;
			}
		}).start();
	}

	public void loadImageFromUrl(final ArrayList<ImagesPush> imagesPushs) {

		if (settingPreferences.getBoolean("checkbox_display_icons", true)) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						Bitmap bitmap = null;
						for (int i = 0; i < imagesPushs.size(); i++) {
							String swid;
							if (imagesPushs.get(i).getSwid() == null) {
								swid = "software" + "big"
										+ imagesPushs.get(i).getCategoryid();
							} else {
								swid = "software" + "big"
										+ imagesPushs.get(i).getSwid();
							}

							// Util.print("loadImageFromUrl",
							// "loadImageFromUrl"+imagesPush.getSoftwareBigIcon());
							// Util.print("loadImageFromUrl",
							// "loadImageFromUrl"+imagesPush.getName());
							// Util.print("loadImageFromUrl",
							// "loadImageFromUrl"+imagesPush.getModifieddate());
							Util.print("loadImageFromUrl", "loadImageFromUrl"
									+ swid);

							bitmap = Util.getWeBitmap(
									SoftwareRecommendedActivity.this,
									imagesPushs.get(i).getSoftwareBigIcon(),
									imagesPushs.get(i).getName(), imagesPushs
											.get(i).getModifieddate(), swid,
									"", true);

							if (!imageCache.containsKey(swid)) {
								imageCache.put(swid, bitmap);
							}
							loadedSize++;
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
}