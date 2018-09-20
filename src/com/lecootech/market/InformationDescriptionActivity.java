package com.lecootech.market;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

import javax.crypto.spec.PSource;

import android.R.anim;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lecootech.market.adapter.GalleryAdapter;
import com.lecootech.market.adapter.OtherSoftwareGalleryAdapter;
import com.lecootech.market.common.ApplicationConstants;
import com.lecootech.market.common.MyGallery;
import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.common.WebAddress;
import com.lecootech.market.data.ImageData;
import com.lecootech.market.database.CollectionDatabase;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.DownloadDatabase;
import com.lecootech.market.database.GalleryDatabase;
import com.lecootech.market.download.DownladUtil;
import com.lecootech.market.download.ImagePageTask;
import com.lecootech.market.download.MyDownloadService;
import com.lecootech.market.handle.DataSet;
import com.mobclick.android.MobclickAgent;

public class InformationDescriptionActivity extends Activity {
	private LinearLayout layout;
	private LinearLayout layout_progressLayout;
	 private ProgressBar main_progressbar;

	// private ProgressDialog progressDialog;
	private View progressView;

	// display webview
	private boolean actionDisplayIntroduceFlag = false;
	private LinearLayout display_intreduce_view;
	private TextView introduce_more_textview;

	// network failed
	private LinearLayout networklayout = null;

	// common
	private ImageView imageViewIcon;
	private WebView jieshao;
	private LinearLayout layout_webview;

	private LinearLayout layout_download;

	private TextView download_stop;
	private TextView download;

	// gallery display params
	private int currentpos;
	private Bitmap current;
	private Map<Integer, Bitmap> dateCache;
	private int Currentposition = 1;
	public final static LinearLayout.LayoutParams WC_WC = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);
	private LinearLayout layout_display_select;
	private ArrayList<ImageView> displayImageViews = new ArrayList<ImageView>();
	private Gallery galleryImage;

	// 1 最初状态 2 正在下载
	private int currentDownloadState = 1;

	private Button reportButton;
	private LinearLayout reportError;
	private LinearLayout layout_gallery;
	private TextView gallery_number;
	private int currentSelect = 1;

	// download
	private boolean downloadable = true;
	private String packagename = "";
	private String webpath = "";
	private int Noti = 0;
	private String apkpath = "";
	private String swid = "";
	private String software_size = "";
	private String software_name = "";
	private DownloadDatabase downloadDatabase;
	private NetworkBroadcast networkBroadcast = new NetworkBroadcast();

	// category id
	private String categoryID = "";

	// private View download_progress = null;
	// private ProgressBar progressBar;
	// private TextView progressvalue;

	private ProgressBar downloadProgressBar;
	private TextView downloadProgressVaule;

	private int currentDownloadValue = 0;
	private Boolean downloadThread = true;

	// gallery
	private GalleryDatabase galleryDatabase;
	private String[] galleryfilename;
	private ArrayList<String> imageArrayPicStrings;
	private ArrayList<String> imgaeArrayPicIDStrings;
	private int gallery_size;

	// resume loading
	private boolean secondLoading = false;

	private DataSet dataSet;
	private Boolean loadfinish = false;
	private final static int downloadProgress = 0x234;
	private final static int loadingOtherGallery = 0x789;
	private final static int displayDownloading = 0x345;
	private SharedPreferences settingPreferences;
	private final static int loadingGallery = 0x343;
	private final static int updateDownloadDisplay = 0x986;
	private final static int NETWORK_CHANGED = 0x121;
	private final static int GALLERY_UPDATE = 0x122;
	// other software list gallery
	private OtherSoftwareGalleryAdapter otherSoftwareGalleryAdapter;
	private GalleryAdapter galleryAdapter = null;
	private LinearLayout layout_other_software_gallery;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ApplicationConstants.main:
				initIntroduceDescription();
				// main_progressbar.setVisibility(View.INVISIBLE);
				// loadSoftwareIcon();

				handler.sendEmptyMessage(ApplicationConstants.gallery);
				break;
			case ApplicationConstants.cancel:
//				if (main_progressbar!=null) {
//					 main_progressbar.setVisibility(View.INVISIBLE);
//				}
				
				break;
			case loadingGallery:
				// addGalleryDisplay();
				// initGridview(galleryfilename.length);
				// displayImageView();
				displayGalleryImage();
				initGalleyDot();
				handler.sendEmptyMessage(loadingOtherGallery);
				break;
			case ApplicationConstants.gallery:
				SharedPreferences settingPreferences = PreferenceManager
						.getDefaultSharedPreferences(InformationDescriptionActivity.this);
				if (settingPreferences.getBoolean("checkbox_display_icons",
						true)) {
					getGalleyData();
				} else {
					loadingOtherGallery();
					layout_gallery.removeAllViews();
				}
				break;
			case GALLERY_UPDATE:
				Util.print("update-->gallery", "update-->gallery");
				if (galleryAdapter != null)
					galleryAdapter.notifyDataSetChanged();
				break;
			case loadingOtherGallery:
				loadingOtherGallery();
				break;
			case ApplicationConstants.webTimeout:
				
				System.out.println(InformationDescriptionActivity.this.isFinishing()+"");
				if (!InformationDescriptionActivity.this.isFinishing()){
					networkException(getResources().getString(
							R.string.web_load_timeout));
				}
					
				break;
			case InformationDescriptionActivity.downloadProgress:
				// progressBar.setIndeterminate(false);
				downloadProgressBar.setProgress(currentDownloadValue);
				if (currentDownloadValue == 0) {
					downloadProgressVaule.setText("等待中");
				} else {
					downloadProgressVaule.setText(currentDownloadValue + "%");
				}

				if (currentDownloadValue == 100) {
					layout_progressLayout.removeAllViews();
					currentDownloadState = 0;
					initButtomLayoutButton(currentDownloadState);
					download.setText("安装");
					download.getCompoundDrawables()[0].setLevel(2);
				}
				break;
			case ApplicationConstants.exception:
				// main_progressbar.setVisibility(View.INVISIBLE);
				Toast.makeText(InformationDescriptionActivity.this, "加载网络异常",
						Toast.LENGTH_LONG).show();
				break;
			case displayDownloading:
				Util.initNotificationProgress(
						InformationDescriptionActivity.this, software_name,
						Noti);
				Toast.makeText(InformationDescriptionActivity.this, "正在下载...",
						Toast.LENGTH_SHORT).show();
				break;
			case updateDownloadDisplay:
				if (download_stop != null) {
					download_stop.getCompoundDrawables()[0].setLevel(1);
					download_stop.setText("继续");
				}

				break;
			case NETWORK_CHANGED:
				// boolean state =
				// Util.checkNetworkIsActive(InformationDescriptionActivity.this);
				Util.print("info->state", Boolean.toString(downloadThread));
				if (!downloadThread) {
					initDownloadProgress();
				}
				currentDownloadState = getCurrentDownloadState();
				initButtomLayoutButton(currentDownloadState);
				break;
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.software_description_description);

		// Boolean aBoolean = getIntent().getBooleanExtra("downloading", false);
		// String aString = getIntent().getStringExtra("assistant");
		if (getIntent() != null) {
			swid = getIntent().getStringExtra("swid");
			software_name = getIntent().getStringExtra("name");
		}

		Util.print("id", swid);
		Util.print("software_name", software_name);

		if (getIntent().getBooleanExtra("downloading", false)) {
			currentDownloadState = 0; // 正在下载
		}

		registerBroadcast();
		init();
	}

	public void init() {

		// software_share_Button =
		// (Button)findViewById(R.id.software_description_share);
		layout_progressLayout = (LinearLayout) findViewById(R.id.layout_progress);

		settingPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		downloadDatabase = DatabaseManager.getDownloadDatabase(this);
		addLeftLayout();
		leftMain();
	}

	public void addLeftLayout() {
		// layout = (LinearLayout)findViewById(R.id.layout_add_view);

		// layout.removeAllViews();
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		// left_view=layoutInflater.inflate(R.layout.software_description_description,
		// null);

		// layout.addView(left_view);

		// progressDialog =
		// Util.getProgressDialog(InformationDescriptionActivity.this,"加载中...");
		 main_progressbar = (ProgressBar)findViewById(R.id.main_progressbar);
		// init webview
		layout_webview = (LinearLayout) findViewById(R.id.layout_webview);
		 View progressView = layoutInflater.inflate(R.layout.progressbar2,
		 null);
		 layout_webview.addView(progressView);

		// init galleryview
		layout_gallery = (LinearLayout) findViewById(R.id.layout_gallery);

//		View progressView2 = layoutInflater
//				.inflate(R.layout.progressbar2, null);
		// layout_gallery.addView(progressView2);

		layout_download = (LinearLayout) findViewById(R.id.layout_download);

		layout_other_software_gallery = (LinearLayout) findViewById(R.id.layout_other_software_gallery);
		display_intreduce_view = (LinearLayout) findViewById(R.id.layout_introducde_more);
		introduce_more_textview = (TextView) findViewById(R.id.introduce_more);

		currentDownloadState = getCurrentDownloadState();
		initButtomLayoutButton(currentDownloadState);
	}

	/**
	 * 收藏
	 */
	public void ButtonEvent() {

		download = (TextView) findViewById(R.id.software_download);
		download.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				if (download.getText().toString().equals("免费下载")) {
					try {
						if (loadfinish) {
							if (downloadable) {
								Util.print("down", "down");
								currentDownloadState = 0;
								initButtomLayoutButton(currentDownloadState);
								managerDownload();
							} else {
								Util.OpenSoftware(
										InformationDescriptionActivity.this,
										packagename);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Util.print("onclick download software error",
								"onclick download software error");
					}
				} else if (download.getText().toString().equals("更新")) {
					try {
						if (loadfinish) {
							if (downloadable) {
								Util.print("down", "down");
								currentDownloadState = 0;
								initButtomLayoutButton(currentDownloadState);
								managerDownload();
							} else {
								Util.OpenSoftware(
										InformationDescriptionActivity.this,
										packagename);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Util.print("onclick download software error",
								"onclick download software error");
					}
				} else if (download.getText().toString().equals("打开")) {
					Util.OpenSoftware(InformationDescriptionActivity.this,
							packagename);
				} else if (download.getText().toString().equals("安装")) {
					Cursor temCursor = downloadDatabase.queryID(dataSet
							.getSoftwareInfo().getName());
					int size = temCursor.getCount();
					temCursor.moveToFirst();
					if (size > 0)
						apkpath = temCursor.getString(temCursor
								.getColumnIndexOrThrow(DownloadDatabase.KEY_FILEPATH));
					temCursor.close();
					File file = new File(apkpath);
					if (file.exists()) {
						DownladUtil.installFile(new File(apkpath),
								InformationDescriptionActivity.this);
					} else {
						download.setText("免费下载");
						downloadDatabase.deleteData(dataSet.getSoftwareInfo()
								.getName());

						Toast.makeText(InformationDescriptionActivity.this,
								"文件不存在，请重新下载！", Toast.LENGTH_LONG).show();
					}

				} else if (download.getText().toString().equals("取消")) {
					try {
						Thread.sleep(1000);
						layout_progressLayout.removeAllViews();
						// download_progress = null;

						settingPreferences.edit().putBoolean(swid, false)
								.commit();

						downloadThread = false;
						NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
						notificationManager.cancel(Integer.parseInt(swid));
						downloadDatabase.deleteData(software_name);

						// 删除文件
						File file = new File(apkpath);
						if (file.exists())
							file.delete();
						// 删除适配器当前行所在的数据
						Toast.makeText(InformationDescriptionActivity.this,
								R.string.commtent_already_download_cancel,
								Toast.LENGTH_LONG).show();

						currentDownloadState = 1;
						initButtomLayoutButton(currentDownloadState);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		});

	}

	public void initDownloadProgress() {
		// if (download_progress == null) {
		// downloadThread = true;
		// LayoutInflater layoutInflater = LayoutInflater
		// .from(InformationDescriptionActivity.this);
		// download_progress = layoutInflater.inflate(
		// R.layout.download_progress, null);
		// layout_progressLayout.removeAllViews();
		// layout_progressLayout.addView(download_progress);
		// progressBar = (ProgressBar) download_progress
		// .findViewById(R.id.progress);
		// progressvalue = (TextView) download_progress
		// .findViewById(R.id.value);
		//
		// loadingDownloadProgress();
		// } else {
		// loadingDownloadProgress();
		// }

		downloadThread = true;
		loadingDownloadProgress();
	}

	public void managerDownload2() {
		Boolean candownload = settingPreferences.getBoolean(
				"checkbox_allow_without_wifi", true);
		if (!candownload) {
			if (!Util.isWiFiActive(InformationDescriptionActivity.this)) {
				new AlertDialog.Builder(InformationDescriptionActivity.this)
						.setTitle("确认在没有WIFI网络的情况下，下载当前应用?")
						.setPositiveButton(
								getResources().getString(R.string.ok),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {

										String path = Util.getStoreApkPath();
										downloadAction2(path);
									}
								})
						.setNegativeButton(
								getResources().getString(R.string.cancel),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										layout_progressLayout.removeAllViews();
										// download_progress = null;
										downloadThread = false;
										currentDownloadState = 1;
										initButtomLayoutButton(currentDownloadState);
										downloadButtonDisplay(
												dataSet.getSoftwareInfo()
														.getVersion(),
												packagename);

									}
								}).show();
			}
		} else {

			String path = Util.getStoreApkPath();
			downloadAction2(path);
		}

	}

	public void managerDownload() {
		Boolean candownload = settingPreferences.getBoolean(
				"checkbox_allow_without_wifi", true);
		if (!candownload) {
			if (!Util.isWiFiActive(InformationDescriptionActivity.this)) {
				new AlertDialog.Builder(InformationDescriptionActivity.this)
						.setTitle("确认在没有WIFI网络的情况下，下载当前应用?")
						.setPositiveButton(
								getResources().getString(R.string.ok),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {

										String path = Util.getStoreApkPath();

										downloadAction(path);
									}
								})
						.setNegativeButton(
								getResources().getString(R.string.cancel),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										layout_progressLayout.removeAllViews();
										// download_progress = null;
										downloadThread = false;
										currentDownloadState = 1;
										initButtomLayoutButton(currentDownloadState);
										downloadButtonDisplay(
												dataSet.getSoftwareInfo()
														.getVersion(),
												packagename);

									}
								}).show();
			}
		} else {

			String path = Util.getStoreApkPath();
			downloadAction(path);
		}

	}

	/*
	 * 执行下载按钮事件的相应提示及路径设置
	 */

	public void downloadAction2(String downloadPathDir) {
		try {

			webpath = webpath.replaceAll(" ", "%20");
			// download.setBackgroundResource(R.drawable.download_new);
			String[] fileName = Util.fileName(webpath);
			Noti = Integer.parseInt(swid);
			apkpath = downloadPathDir + fileName[1] + "." + fileName[0]; // 下载数据库要的存放文件路径

			downloadDatabaseManager();
			initDownloadProgress();
			downloadExecute2(downloadPathDir);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void downloadAction(String downloadPathDir) {
		try {

			webpath = webpath.replaceAll(" ", "%20");
			// download.setBackgroundResource(R.drawable.download_new);
			String[] fileName = Util.fileName(webpath);
			Noti = Integer.parseInt(swid);
			apkpath = downloadPathDir + fileName[1] + "." + fileName[0]; // 下载数据库要的存放文件路径

			downloadDatabaseManager();
			initDownloadProgress();
			downloadExecute(downloadPathDir);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 执行下载按钮事件的处理过程
	 */

	public void downloadExecute2(final String path) throws Exception {
		new Thread(new Runnable() {

			public void run() {
				try {
					handler.sendEmptyMessage(InformationDescriptionActivity.displayDownloading);
					// 开始启动下载服务
					DownladUtil.startDownloadService(
							InformationDescriptionActivity.this, Noti,
							software_name, path, webpath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	public void downloadExecute(final String path) throws Exception {
		new Thread(new Runnable() {

			public void run() {
				try {
					// Toast.makeText(SoftwareInformationActivity.this,"正在下载...",Toast.LENGTH_SHORT).show();
					handler.sendEmptyMessage(InformationDescriptionActivity.displayDownloading);
					// 开始启动下载服务
					DownladUtil.startDownloadService(
							InformationDescriptionActivity.this, Noti,
							software_name, path, webpath);

					// finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	/**
	 * 下载数据库的管理
	 */
	public void downloadDatabaseManager() {
		String temppicpath = Util.getStorePicPath();
		try {

			Cursor temCursor = downloadDatabase.queryID(software_name);
			temCursor.moveToFirst();
			int size = temCursor.getCount();
			if (size != 0) {
				downloadDatabase.updateDownloadOperation(software_name, 0);
			} else {
				downloadDatabase.insertData(software_name, temppicpath
						+ "software" + swid, packagename, 0, 0, webpath,
						apkpath, 0, 0, software_size);
			}
			temCursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 软件介绍内容切换
	 */
	public void actionDisplayIntroduce() {
		display_intreduce_view.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (actionDisplayIntroduceFlag) // 显示少部分的内容
				{

					display_short_introduce(dataSet.getSoftwareInfo()
							.getRecoment());
					actionDisplayIntroduceFlag = false;
					introduce_more_textview.setText("展开");
				} else { // 显示所有介绍内容
					display_all_introduce();
					actionDisplayIntroduceFlag = true;
					introduce_more_textview.setText("收起");
				}
			}
		});

	}

	/*
	 * @introduce 内容 小部分显示介绍内容
	 */
	public void display_short_introduce(String introduce) {
		layout_webview.removeAllViews();
		TextView textView = new TextView(this);
		textView.setMaxLines(3);
		textView.setTextSize(13);
		introduce = dataSet.getSoftwareInfo().getDescription();
		if (introduce.length() > 60) {
			introduce = introduce.substring(0, 60) + "......";
		} else {
			introduce_more_textview.setVisibility(View.INVISIBLE);
			display_intreduce_view.setVisibility(View.INVISIBLE);
		}

		textView.setText(introduce);
		textView.setTextColor(Color.BLACK);
		layout_webview.addView(textView);

		textView.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				display_all_introduce();
				actionDisplayIntroduceFlag = true;
				introduce_more_textview.setText("收起");
			}
		});
	}

	/*
	 * 通过webview显示所有介绍内容
	 */
	public void display_all_introduce() {

		layout_webview.removeAllViews();
		TextView textView = new TextView(this);
		textView.setText(dataSet.getSoftwareInfo().getDescription());
		textView.setTextColor(Color.BLACK);
		// textView.setLines(3);
		textView.setTextSize(13);
		layout_webview.addView(textView);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);

		currentDownloadState = getCurrentDownloadState();
		if (currentDownloadState == 2) {
			initDownloadProgress();
		}
		if (secondLoading) {
			initButtomLayoutButton(currentDownloadState);
			if (dataSet != null && dataSet.getSoftwareInfo() != null) {
				downloadButtonDisplay(dataSet.getSoftwareInfo().getVersion(),
						packagename);

			}
		}
		secondLoading = true;

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		downloadThread = false;
		if (galleryAdapter != null) {
			galleryAdapter.freeBitmap();
		}

		unRegisterBroadcast();
		InformationTabHost.loadfinish = false;
	}

	public void leftMain() {
		// if(Util.getNetworkState(InformationDescriptionActivity.this))
		// {
		// main_progressbar.setVisibility(View.VISIBLE);
		// 初始化其它参数
		ButtonEvent();
		new Thread(new Runnable() {

			public void run() {
				try {
					Util.print(
							"path",
							SharedPrefsUtil.getValue(
									InformationDescriptionActivity.this,
									"DomainName", WebAddress.comString)
									+ WebAddress.softwareDescription + swid);

					dataSet = Util.getSoftwareWebData(
							InformationDescriptionActivity.this,
							SharedPrefsUtil.getValue(
									InformationDescriptionActivity.this,
									"DomainName", WebAddress.comString)
									+ WebAddress.softwareDescription + swid);
					if (dataSet != null) {
						handler.sendEmptyMessage(ApplicationConstants.main);
						loadfinish = true;

						// set InformationTabHost 加载项
						InformationTabHost.loadfinish = loadfinish;
					} else
						handler.sendEmptyMessage(ApplicationConstants.webTimeout);
					
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

		networklayout = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.information_network_connetion_failed, null);
		layout_gallery.addView(networklayout);

		networklayout.setVisibility(View.VISIBLE);

		 layout_webview.removeAllViews();
		Button flashButton = (Button) findViewById(R.id.reflash);
		flashButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Util.print("reflash", "reflash");
				networklayout.setVisibility(View.INVISIBLE);
				leftMain();
			}
		});

	}

	public View getProgressView() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		return layoutInflater.inflate(R.layout.progressbar, null);
	}

	public void initIntroduceDescription() {

		TextView size = (TextView) findViewById(R.id.size);
		TextView version = (TextView) findViewById(R.id.version);
		version.setSelected(true);
		TextView down_num = (TextView) findViewById(R.id.download_size);

		// TextView language = (TextView) findViewById(R.id.info_language);
		TextView updateData = (TextView) findViewById(R.id.update);
		// TextView deveoper_name = (TextView)findViewById(R.id.developer_name);

		software_name = dataSet.getSoftwareInfo().getName();
		Util.print("software_name", software_name);
		version.setText("软件版本:" + dataSet.getSoftwareInfo().getVersion());
		packagename = dataSet.getSoftwareInfo().getPackage_Name();

		// swid = dataSet.getSoftwareInfo().getSwid();

		software_size = dataSet.getSoftwareInfo().getSoftwareSize();
		size.setText("大小:" + software_size);

		down_num.setText("下载次数:" + dataSet.getSoftwareInfo().getDownload_Num()
				+ "次");
		// language.setText(dataSet.getSoftwareInfo().getLanguage());
		String[] a = dataSet.getSoftwareInfo().getModifyDate().substring(0, 10)
				.split("\\ ");
		updateData.setText("发布时间:" + a[0]);
		// updateData.setText("更新:"+dataSet.getSoftwareInfo().getModifyDate().substring(0,
		// 9));

		// developer_autor_name =
		// dataSet.getSoftwareInfo().getDeveloper_Autor_Name();
		// ratingBar.setRating(Integer.parseInt(dataSet.getSoftwareInfo()
		// .getScore()));

		// deveoper_name.setText(developer_autor_name);
		webpath = dataSet.getSoftwareInfo().getFilepath();

		display_short_introduce(dataSet.getSoftwareInfo().getRecoment());
		introduce_more_textview.setText("展开");
		actionDisplayIntroduce();
		// layout_webview.removeAllViews();
		// //String introduceString =
		// "游戏中操作小鸡，用舌头吃掉天上掉下的食物，如果没吃到，食物掉在地上就会砸掉一块草地，当全部砸掉你无处容身时就算输了";
		// jieshao = new WebView(this);
		// jieshao.setBackgroundColor(0);
		// jieshao.getSettings().setDefaultTextEncodingName("utf-8");
		//
		// jieshao.loadDataWithBaseURL(null,
		// dataSet.getSoftwareInfo().getDescription(), "text/html", "utf-8",
		// null);
		// layout_webview.addView(jieshao);
		Util.print("papa", packagename);
		downloadButtonDisplay(dataSet.getSoftwareInfo().getVersion(),
				packagename);

		Util.print(
				"downloading",
				Boolean.toString(getIntent().getBooleanExtra("downloading",
						false)));

		if (getIntent().getBooleanExtra("downloading", false)) {
			initDownloadProgress();
		}
		categoryID = dataSet.getSoftwareInfo().getCagegoryID();

		// 设置变量在InformationTabHost
		InformationTabHost.currentCategoryID = categoryID;
		InformationTabHost.packagenameString = packagename;
		InformationTabHost.software_version = dataSet.getSoftwareInfo()
				.getVersion();
		InformationTabHost.software_modifydate = dataSet.getSoftwareInfo()
				.getModifyDate();
		InformationTabHost.software_imagepath = dataSet.getSoftwareInfo()
				.getImagePath();
		InformationTabHost.software_score = dataSet.getSoftwareInfo()
				.getScore();
		InformationTabHost.software_filepath = dataSet.getSoftwareInfo()
				.getFilepath();
		InformationTabHost.software_size = dataSet.getSoftwareInfo()
				.getSoftwareSize();

		Intent intent = new Intent();
		intent.setAction("information.top.update");
		sendBroadcast(intent);

	}

	public void loadingDownloadProgress() {

		new Thread(new Runnable() {

			public void run() {
				try {
					Cursor temCursor = null;
					int returnvalue = -1;
					int size;
					while (downloadThread) {
						Util.print("aa", "aa");
						temCursor = downloadDatabase.queryID(software_name);

						size = temCursor.getCount();
						temCursor.moveToFirst();
						if (size != 0) {
							returnvalue = temCursor.getInt(temCursor
									.getColumnIndexOrThrow(DownloadDatabase.KEY_DOWNLOND_NUM));
							int operation = getOperation(software_name); // -1删除
							if (operation != 0) {
								handler.sendEmptyMessage(updateDownloadDisplay);
								break;
							}
						}
						if (!temCursor.isClosed())
							temCursor.close();
						if (returnvalue == -1) {
							break;
						} else {
							if (returnvalue != 100) {
								Thread.sleep(100);

							}
							if (returnvalue != -1) {
								currentDownloadValue = returnvalue;
								if (currentDownloadValue > 0)
									handler.sendEmptyMessage(InformationDescriptionActivity.downloadProgress);
							} else {
								break;
							}
						}
						if (returnvalue == 100) {
							break;
						}
					}
					downloadThread = false;
					Util.print("threadbreak", "threadbreak");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public int getOperation(String name) {
		int operation = -1;
		operation = downloadDatabase.getDownloadOperation(name);
		return operation;
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	public void addGalleryDisplay() {

		// getGalleyData();

		layout_gallery.removeAllViews();

		final Gallery gallery = new Gallery(this);

		gallery.setSpacing(0);
		// gallery.setUnselectedAlpha((float) 0.3);
		galleryAdapter = new GalleryAdapter(
				InformationDescriptionActivity.this, galleryfilename, false);
		gallery.setAdapter(galleryAdapter);
		layout_gallery.addView(gallery);
		gallery.setSelection(galleryfilename.length / 2);

		// gallery_number = (TextView) findViewById(R.id.gallery_text_number);
		// if (gallery_number != null) {
		// gallery_number.setVisibility(View.VISIBLE);
		// gallery_number.setText(gallery_size / 2 + "/" + gallery_size);
		// }

		gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// gallery_number.setText(arg2 + 1 + "/" + gallery_size);
				// Util.print("position->", ""+position);
				// Util.print("select-->",
				// gallery.getSelectedItemPosition()+"");

				// if(position<2)
				// {
				// gallery.setSelection(2);
				// }
				// else if(position>gallery.getCount())
				// {
				// gallery.setSelection(gallery.getCount()-1);
				// }
				// if (position > gallery.getCount() - 2 || position <3)
				// gallery.setSelection(gallery.getSelectedItemPosition());
				// else if (position != gallery.getSelectedItemPosition())
				// gallery.setSelection(position);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});

		gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					Intent intent = new Intent(
							InformationDescriptionActivity.this,
							GalleryFullScreen.class);
					Bundle bundle = new Bundle();
					bundle.putInt("id", position);
					bundle.putStringArray("path", galleryfilename);
					intent.putExtras(bundle);
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		});
	}

	/*
	 * 下载按钮显示的名称 下载、打开、更新
	 */
	public void downloadButtonDisplay(String newVersion, String packagename) {

		downloadDatabase = DatabaseManager
				.getDownloadDatabase(InformationDescriptionActivity.this);
		Cursor temCursor = downloadDatabase.queryID(software_name);
		int returnvalue = -1;
		int size = temCursor.getCount();
		temCursor.moveToFirst();
		if (size > 0)
			returnvalue = temCursor.getInt(temCursor
					.getColumnIndexOrThrow(DownloadDatabase.KEY_DOWNLOND_NUM));
		temCursor.close();

		if (Util.checkPackageIsExist(InformationDescriptionActivity.this,
				packagename))// 包存在
		{
			try {
				PackageManager manager = getPackageManager();
				PackageInfo info = manager.getPackageInfo(packagename, 0);
				String oldversion = info.versionName;
				if (currentDownloadState == 1) {
					if (Util.compareVersions(newVersion, oldversion)) // 如果有新版本则显示更新
					{
						if (returnvalue == 100) {
							download.setText("安装");
							download.getCompoundDrawables()[0].setLevel(2);
						} else {
							Util.print("update", "havingupdate");
							download.setText("更新");
							download.getCompoundDrawables()[0].setLevel(4);
						}
					} else { // 如果已经安装则显示打开软件
						Util.print("open", "open");
						download.setText("打开");
						downloadable = false;
						download.getCompoundDrawables()[0].setLevel(5);// 保留后面
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (!Util.checkPackageIsExist(
				InformationDescriptionActivity.this, packagename)
				&& returnvalue == 100) {
			download.setText("安装");
			download.getCompoundDrawables()[0].setLevel(2);
		}
	}

	public void getGalleyData() {
		gallery_size = dataSet.getImageArrays().size();
		imageArrayPicStrings = dataSet.getImageArrays();
		imgaeArrayPicIDStrings = dataSet.getImageArraysID();
		new Thread(new Runnable() {

			public void run() {
				try {
					galleryDatabase = DatabaseManager
							.getGalleryDatabase(InformationDescriptionActivity.this);
					if (Util.avaiableMedia()) // SD卡存在的情况下
					{
						Cursor cursor = galleryDatabase
								.queryOneData(software_name);
						cursor.moveToFirst();
						int cursorcount = cursor.getCount();
						if (cursorcount == 0) {
							Util.print("nocounet", "nocounet");
							insertGalleryDatabase();
						} else {
							if (gallery_size != cursorcount) {
								galleryDatabase.deleteMulData(software_name); // 把之前的数据删除掉,因为在软件详细页面，要显示第一张截图,
																				// 此图也写入了数据库。所以在下载所有的截图的时候，要删除那张记录
								insertGalleryDatabase();
							} else {
								boolean databaseupdate = false;
								for (int i = 0; i < gallery_size; i++) {
									String tempimageid = imgaeArrayPicIDStrings
											.get(i);
									cursor.moveToFirst();
									boolean searchED = false;
									for (int j = 0; j < cursorcount; j++) // 数据库里面搜索imageid号，如果检测到没有这个，那么代表上传的截图已经有更新，那么要重新下载所有的
									{
										String cursorimageid = cursor.getString(cursor
												.getColumnIndexOrThrow(GalleryDatabase.KEY_IMAGEID));
										if (tempimageid.equals(cursorimageid)) {
											searchED = true;
											break;
										}
										cursor.moveToNext();
									}
									if (!searchED) // 没有搜索到，删除之前所有的记录,且删除图片文件
									{
										Util.print("one no search", "no search");
										databaseupdate = true;
										galleryDatabase
												.deleteMulData(software_name);
										break;
									}
								}
								if (!databaseupdate) // 重新更新下载的过程
								{
									cursor.moveToFirst();
									galleryfilename = new String[cursorcount];
									handler.sendEmptyMessage(InformationDescriptionActivity.loadingGallery);
									for (int i = 0; i < cursorcount; i++) {
										galleryfilename[i] = cursor.getString(cursor
												.getColumnIndexOrThrow(GalleryDatabase.KEY_PATH));
										File file = new File(galleryfilename[i]);
										if (!file.exists()) {
											String picString = imageArrayPicStrings
													.get(i);
											String tempdot = swid + "gallery"
													+ i;
											String temppicpath = Util
													.getStorePicPath();

											Util.getSoftwareImage(
													InformationDescriptionActivity.this,
													software_name, picString,
													tempdot, true, temppicpath);
										}
										cursor.moveToNext();
										if (i == 0 || i == cursorcount - 1)
											handler.sendEmptyMessage(GALLERY_UPDATE);
									}
								} else if (databaseupdate) {
									insertGalleryDatabase();
								}
							}
						}
						if (!cursor.isClosed())
							cursor.close();
					} else { // SD卡不存在的情况下
						galleryfilename = new String[gallery_size];
						handler.sendEmptyMessage(InformationDescriptionActivity.loadingGallery);
						for (int i = 0; i < gallery_size; i++) {
							String picString = imageArrayPicStrings.get(i);
							String tempdot = swid + "gallery" + i;
							String temppicpath = Util.getStorePicPath();
							Util.getSoftwareImage(
									InformationDescriptionActivity.this,
									software_name, picString, tempdot, true,
									temppicpath); // create tempPicFile
							galleryfilename[i] = temppicpath + tempdot;

							if (i == 0 || i == gallery_size - 1)
								handler.sendEmptyMessage(GALLERY_UPDATE);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					Util.print("get gallery web data error",
							"get gallery web data error");
				}
			}
		}).start();

	}

	/*
	 * GalleryDatabase 数据库的管理，插入记录
	 */
	public void insertGalleryDatabase() {
		galleryfilename = new String[gallery_size];
		handler.sendEmptyMessage(InformationDescriptionActivity.loadingGallery);
		Util.print("gallery-->size", "" + galleryfilename.length);
		for (int i = 0; i < gallery_size; i++) {
			String picString = imageArrayPicStrings.get(i);
			String imageid = imgaeArrayPicIDStrings.get(i);
			// Util.print("imageid", imageid);
			String tempdot = swid + "gallery" + i;
			String temppicpath = Util.getStorePicPath();
			Util.getSoftwareImage(InformationDescriptionActivity.this,
					software_name, picString, tempdot, true, temppicpath); // create
																			// tempPicFile
			galleryfilename[i] = temppicpath + tempdot;
			galleryDatabase.insertData(software_name, galleryfilename[i],
					imageid, picString);

			if (i == 0 || i == gallery_size - 1)
				handler.sendEmptyMessage(GALLERY_UPDATE);
		}
	}

	public void loadingOtherGallery() {
		layout_other_software_gallery.removeAllViews();
		Gallery gallery = new Gallery(this);
		gallery.setSpacing(1);
		gallery.setUnselectedAlpha((float) 0.3);
		otherSoftwareGalleryAdapter = new OtherSoftwareGalleryAdapter(this,
				InformationDescriptionActivity.this, dataSet, gallery);
		gallery.setAdapter(otherSoftwareGalleryAdapter);
		final int size = dataSet.getThreeCates().size();
		gallery.setSelection(size / 2 - 1);
		layout_other_software_gallery.addView(gallery);

		final TextView other_gallery_text_name = (TextView) findViewById(R.id.other_gallery_text_name);
		if (other_gallery_text_name != null) {
			other_gallery_text_name.setVisibility(View.VISIBLE);
			other_gallery_text_name.setText(dataSet.getThreeCates()
					.get(size / 2 - 1).getName());
		}

		gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				other_gallery_text_name.setText(dataSet.getThreeCates()
						.get(position).getName());

			}

			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

		gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(InformationDescriptionActivity.this,
						InformationTabHost.class);
				intent.putExtra("swid", dataSet.getThreeCates().get(position)
						.getSwid());
				intent.putExtra("name", dataSet.getThreeCates().get(position)
						.getName());
				startActivity(intent);
				finish();
			}

		});
	}

	// 下载状态为0 或者 初始状态1
	public void initButtomLayoutButton(int downloadState) {

		View layout_buttom_download;
		layout_download.removeAllViews();
		Util.print("state", currentDownloadState + "");
		if (downloadState == 1) {
			layout_buttom_download = LayoutInflater.from(this).inflate(
					R.layout.layout_button_download, null);
			download = (TextView) layout_buttom_download
					.findViewById(R.id.software_download);

		} else {
			layout_buttom_download = LayoutInflater.from(this).inflate(
					R.layout.layout_button_downloading, null);
			download = (TextView) layout_buttom_download
					.findViewById(R.id.software_download);
			download_stop = (TextView) layout_buttom_download
					.findViewById(R.id.software_download_stop);

			downloadProgressBar = (ProgressBar) layout_buttom_download
					.findViewById(R.id.progress);
			downloadProgressVaule = (TextView) layout_buttom_download
					.findViewById(R.id.value);

			Cursor cursor = downloadDatabase.queryID(software_name);
			cursor.moveToFirst();
			int size = cursor.getCount();

			if (size > 0) {
				if (cursor.getInt(cursor
						.getColumnIndexOrThrow(DownloadDatabase.KEY_OPERATION)) == -3) {
					download_stop.setText("继续");
					download_stop.getCompoundDrawables()[0].setLevel(1);
				}
				cursor.close();
			}
			if (!cursor.isClosed())
				cursor.close();
			// download.setText("取消下载");

			download_stop.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					if (!software_name.equals("")) // 暂停下载
					{
						if (download_stop.getText().toString().equals("暂停")) {
							Toast.makeText(InformationDescriptionActivity.this,
									"已经暂停下载", 1000).show();

							downloadDatabase.updateDownloadOperation(
									software_name, -1);
							settingPreferences.edit().putBoolean(swid, false)
									.commit();
							// layout_progressLayout.removeAllViews();
							// download_progress = null;
							download_stop.setText("继续");
							download_stop.getCompoundDrawables()[0].setLevel(1);

							downloadThread = false; // 下载进度条显示的线程结束
						} else if (download_stop.getText().toString()
								.equals("继续")) {
							download_stop.setText("暂停");
							download_stop.getCompoundDrawables()[0].setLevel(0);
							downloadThread = true;
							managerDownload2();
						}
					}

				}
			});

			if (!Util.checkCurrentSoftwareDownloadState(this, software_name)) {
				download_stop.setText("暂停");
				download_stop.getCompoundDrawables()[0].setLevel(0);
			} else {
				download_stop.setText("继续");
				download_stop.getCompoundDrawables()[0].setLevel(1);
				downloadThread = false;
			}

		}

		layout_download.addView(layout_buttom_download);
		ButtonEvent();
	}

	public int getCurrentDownloadState() {
		int result = 1;

		Cursor temCursor = downloadDatabase.queryID(software_name);
		int returnvalue = -1;
		int downloadFlag = 0;
		int size = temCursor.getCount();
		temCursor.moveToFirst();
		if (size > 0) {
			returnvalue = temCursor.getInt(temCursor
					.getColumnIndexOrThrow(DownloadDatabase.KEY_DOWNLOND_NUM));
			downloadFlag = temCursor.getInt(temCursor
					.getColumnIndexOrThrow(DownloadDatabase.KEY_OPERATION));
			Util.print("returnvalue", "" + returnvalue);
			Util.print("downloadFlag", "" + downloadFlag);
		}

		temCursor.close();

		boolean isInstalled = Util.checkPackageIsExist(this, packagename);
		if (isInstalled)// 包存在
		{
			result = 1;
		} else if (!isInstalled && returnvalue == 100) {
			result = 1;
		} else if (!isInstalled && returnvalue != -1 && downloadFlag == 0) {
			result = 2;
		} else if (!isInstalled && returnvalue != -1 && downloadFlag != 0) {
			result = 2;
		} else {
			result = 1;
		}

		Util.print("result", "" + result);

		return result;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Util.print("cofiguration", "con");
		// if(currentSelect==2)
		// Util.setListViewHeight(InformationDescriptionActivity.this,
		// center_listview, center_UseHeight);
		// else if(currentSelect==3)
		// {
		// Util.setListViewHeight(InformationDescriptionActivity.this,
		// relative_listview, right_UseHeight);
		// thridFloorAdapter.changeItemNameWidthFlag(Util.checkCurrentDirection(this));
		// }

		super.onConfigurationChanged(newConfig);
	}

	class NetworkBroadcast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {

				Util.print("----", "+++++");
				// handler.sendEmptyMessage(NETWORK_CHANGED);
			} else if (action.equals("com.lecootech.network")) {
				handler.sendEmptyMessage(NETWORK_CHANGED);
			}
		}
	}

	public void registerBroadcast() {
		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);

		filter.addAction("com.lecootech.network");
		registerReceiver(networkBroadcast, filter);
	}

	public void unRegisterBroadcast() {
		unregisterReceiver(networkBroadcast);
	}

	public void setGalleryWidth(Gallery gallery, int picSize) {
		Util.print("99", "" + picSize);
		ViewGroup.LayoutParams lParams;
		gallery.setLayoutParams(ApplicationConstants.LP_WW);
		lParams = gallery.getLayoutParams();

		LinearLayout imagelayout = (LinearLayout) LayoutInflater.from(this)
				.inflate(R.layout.software_gallery_item, null);
		ImageView imageView = (ImageView) imagelayout
				.findViewById(R.id.software_gallery_image);
		int usedwidth = imageView.getLayoutParams().width;
		Util.print("999", "" + usedwidth);

		lParams.width = usedwidth * picSize + 10;
		gallery.setLayoutParams(lParams);
	}

	public void initGridview(int picSize) {
		layout_gallery.removeAllViews();
		HorizontalScrollView scrollView = (HorizontalScrollView) LayoutInflater
				.from(this).inflate(R.layout.information_gridview, null);
		// LinearLayout layoutGridview =
		// (LinearLayout)scrollView.findViewById(R.id.layout_information_gallery);
		// ViewGroup.LayoutParams lParams;
		// lParams = layoutGridview.getLayoutParams();
		//
		// //获取每张图片的宽度
		// LinearLayout imagelayout = (LinearLayout) LayoutInflater.from(this)
		// .inflate(R.layout.software_gallery_item, null);
		// ImageView imageView = (ImageView) imagelayout
		// .findViewById(R.id.software_gallery_image);
		// int usedwidth = imageView.getLayoutParams().width;
		// Util.print("999", "" + picSize+"--"+usedwidth);
		//
		// lParams.width = usedwidth*picSize + 2*(picSize-1);
		// layoutGridview.setLayoutParams(lParams);

		GridView gridView = (GridView) scrollView
				.findViewById(R.id.image_gridview);
		gridView.setNumColumns(galleryfilename.length);

		int width = Util.getDisplayMetricsWidth(this);
		float realwidth;
		float realheight;
		// if(width<=320)
		// {
		// realwidth = width*(1/(float)2);
		// }
		// else if(width>320 && width<=480)
		// {
		// realwidth = width*(1/(float)3);
		// }
		// else {
		// realwidth = width*(1/(float)4);
		// }
		realwidth = 160;
		realheight = realwidth * (6 / (float) 4);

		gridView.setColumnWidth((int) realwidth);

		ViewGroup.LayoutParams lParams;
		lParams = gridView.getLayoutParams();
		lParams.width = (int) realwidth * galleryfilename.length
				+ galleryfilename.length * 8;
		lParams.height = (int) realheight;
		gridView.setLayoutParams(lParams);

		galleryAdapter = new GalleryAdapter(
				InformationDescriptionActivity.this, galleryfilename, false);
		gridView.setAdapter(galleryAdapter);
		layout_gallery.addView(scrollView);
		Util.print("size", "" + picSize);
		// gridView.setSelection(picSize/2);

	}

	public Bitmap getPhotoItem(String filepath, int size) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = size;
		if (filepath != null) {
			File file = new File(filepath);
			try {
				FileInputStream fis = new FileInputStream(file);
				Bitmap bitmap = BitmapFactory.decodeStream(fis);
				bitmap = Bitmap.createScaledBitmap(bitmap, 320, 480, false);// 预先缩放，避免实时缩放，可以提高更新率
				return bitmap;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;

	}

	public void initGalleyDot() {
		// 初始化显示图标
		layout_display_select = (LinearLayout) findViewById(R.id.layout_gallery_select_dot);
		for (int i = 0; i < galleryfilename.length; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(new LinearLayout.LayoutParams(WC_WC));

			ViewGroup.LayoutParams p = imageView.getLayoutParams();
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) p;
			lp.leftMargin = 4;
			lp.gravity = Gravity.CENTER_VERTICAL;
			imageView.setLayoutParams(p);

			layout_display_select.addView(imageView);
			imageView.setBackgroundResource(R.drawable.gallery_dot);
			displayImageViews.add(imageView);
		}

		if (galleryfilename.length > 0) {
			galleryImage.setSelection(currentpos);

			displayImageViews.get(0).setBackgroundResource(
					R.drawable.gallery_dot_selected);

			galleryImage
					.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							// TODO Auto-generated method stub
							displayDotSelect(arg2 % galleryfilename.length);
						}

						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub

						}

					});
		}

		galleryImage
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						try {
							Intent intent = new Intent(
									InformationDescriptionActivity.this,
									GalleryFullScreen.class);
							Bundle bundle = new Bundle();
							bundle.putInt("id", arg2);
							bundle.putStringArray("path", galleryfilename);
							intent.putExtras(bundle);
							startActivity(intent);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				});
	}

	public void displayDotSelect(int temposition) {
		// Util.print("tempisition", ""+temposition + "##"+
		// galleryfilename.length);
		int id = temposition;
		currentpos = id;

		for (int i = 0; i < galleryfilename.length; i++) {
			if (i == id) {
				displayImageViews.get(i).setBackgroundResource(
						R.drawable.gallery_dot_selected);
			} else {
				displayImageViews.get(i).setBackgroundResource(
						R.drawable.gallery_dot);
			}
		}
	}

	public void displayGalleryImage() {
		layout_gallery.removeAllViews();
		RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(
				this).inflate(R.layout.information_imageview, null);

		galleryImage = (Gallery) relativeLayout
				.findViewById(R.id.gallery_image);
		Button left = (Button) relativeLayout.findViewById(R.id.image_left);
		Button right = (Button) relativeLayout.findViewById(R.id.image_right);

		int width = Util.getDisplayMetricsWidth(this);
		// float realwidth = width*(2/(float)3);
		// float realheight= realwidth*(6/(float)4);
		// ViewGroup.LayoutParams lParams;
		// lParams = galleryImage.getLayoutParams();
		// lParams.width = (int)realwidth;
		// lParams.height = (int)realheight;
		// imageView.setLayoutParams(lParams);
		galleryImage.setSpacing((int) (width / (float) 3));
		galleryAdapter = new GalleryAdapter(
				InformationDescriptionActivity.this, galleryfilename, false);
		if (galleryfilename.length != 0) {
			galleryImage.setAdapter(galleryAdapter);
			layout_gallery.addView(relativeLayout);

			currentpos = Integer.MAX_VALUE / 2 + 1;
		}

		left.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				currentpos--;
				displayDotSelect(currentpos % galleryfilename.length);
				galleryImage.setSelection(currentpos);
			}
		});

		right.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				currentpos++;
				displayDotSelect(currentpos % galleryfilename.length);
				galleryImage.setSelection(currentpos);
			}
		});
	}
}