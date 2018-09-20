package com.lecootech.market;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lecootech.market.adapter.LocalFileAdapter;
import com.lecootech.market.adapter.SoftwareManagerAdapter;
import com.lecootech.market.common.ApplicationConstants;
import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.data.LocalApkData;
import com.lecootech.market.data.ManagerFloorData;
import com.lecootech.market.data.MessageControl;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.DownloadDatabase;
import com.lecootech.market.database.UpdateManagerDatabase;

public class SoftwareManagerActivity extends Activity {

	private AlertDialog main_AalertDialog;
	private LinearLayout layoutlistView;
	private ListView listView;
	private LinearLayout manager_linear;
	private ProgressBar main_progressbar;

	private int ListPos; // 记录listview当前位置
	private TextView Left;
	private TextView Right;
	public static TextView Move;

	private int localApkCount = 0;

	private RelativeLayout layoutLeft;
	private RelativeLayout layoutRight;
	private RelativeLayout layoutMove;

	private LinearLayout view_progressbar;
	private boolean secondLoading = false;

	private PackageManager packageManager;
	private ArrayList<ManagerFloorData> managerFloorDatas;
	private SoftwareManagerAdapter softwareManagerAdapter;

	private UpdateManagerDatabase updateManagerDatabase;
	// has update
	private ArrayList<ManagerFloorData> hasUpdateDatas = null;
	private TextView main_text;
	// local file
	private final static int LocalFileMain = 0x985;
	private LocalFileAdapter localFileAdapter = null;
	private ArrayList<LocalApkData> localApkDatas = new ArrayList<LocalApkData>();
	private ArrayList<LocalApkData> tempLocalApkDatas = new ArrayList<LocalApkData>();
	private static final int KB = 1024;
	private static final int MG = KB * KB;
	private static final int GB = MG * KB;
	private boolean isAleadySearch = false;
	private boolean searching = false;

	public final static int UN_INSTALL = 1;
	private DownloadDatabase downloadDatabase;
	private final static int UpdateAdapter = 0x222;
	private final static int LOCAL_UPATE_ADAPTER = 0x335;
	// page flag
	private int Currentposition = 1; // 1已安装，2可更新，3为可移动
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				super.handleMessage(msg);
				switch (msg.what) {
				case ApplicationConstants.main:
					listView.setVisibility(View.VISIBLE);

					int size = hasUpdateDatas.size();
					if (size > 0) {
						Right.setText("可更新(" + size + ")");
					} else {
						Right.setText("可更新(0)");

					}

					size = managerFloorDatas.size();
					if (size > 0) {
						Left.setText("已安装(" + size + ")");
					} else {
						Left.setText("已安装(0)");
					}

					if (Currentposition == 1) {
						softwareManagerAdapter = new SoftwareManagerAdapter(
								SoftwareManagerActivity.this,
								SoftwareManagerActivity.this,
								managerFloorDatas,
								Util.checkCurrentDirection(SoftwareManagerActivity.this),
								1, true, listView, this);
						softwareManagerAdapter.notifyDataSetChanged();
						listView.setAdapter(softwareManagerAdapter);
						if (softwareManagerAdapter.isEmpty()) {
							listView.setVisibility(View.INVISIBLE);
							manager_linear.setVisibility(View.VISIBLE);
						} else {
							manager_linear.setVisibility(View.INVISIBLE);
						}
						handler.sendEmptyMessage(ApplicationConstants.cancel);
					} else if (Currentposition == 2) {

						softwareManagerAdapter = new SoftwareManagerAdapter(
								SoftwareManagerActivity.this,
								SoftwareManagerActivity.this,
								hasUpdateDatas,
								Util.checkCurrentDirection(SoftwareManagerActivity.this),
								2, true, listView, this);

						if (softwareManagerAdapter.isEmpty()) {
							manager_linear.setVisibility(View.VISIBLE);
							listView.setVisibility(View.INVISIBLE);
						} else {
							manager_linear.setVisibility(View.INVISIBLE);
							listView.setAdapter(softwareManagerAdapter);
						}

						handler.sendEmptyMessage(ApplicationConstants.cancel);
					} else if (Currentposition == 3) {

					}

					break;
				case ApplicationConstants.sdcarde:

					Toast.makeText(SoftwareManagerActivity.this, "SD卡没有正确挂载!",
							Toast.LENGTH_SHORT).show();
					handler.sendEmptyMessage(ApplicationConstants.cancel);
					manager_linear.setVisibility(View.VISIBLE);
					break;
				case ApplicationConstants.dismiss:
					Toast.makeText(
							SoftwareManagerActivity.this,
							"手机存储可用大小:"
									+ Util.formatFileSize((long) Util
											.getAvailableInternalMemorySize()),
							Toast.LENGTH_LONG).show();
					break;
				case ApplicationConstants.cancel:
					main_progressbar.setVisibility(View.INVISIBLE);
					break;

				case 1:
					main_text.setText((String) msg.obj);
					break;
				case LocalFileMain:
					main_AalertDialog.dismiss();
					main_progressbar.setVisibility(View.INVISIBLE);
					localFileAdapter = new LocalFileAdapter(
							SoftwareManagerActivity.this,
							SoftwareManagerActivity.this,
							localApkDatas,
							Util.checkCurrentDirection(SoftwareManagerActivity.this));
					if (localFileAdapter.isEmpty()) {
						manager_linear.setVisibility(View.VISIBLE);
					} else {
						manager_linear.setVisibility(View.INVISIBLE);
					}
					int siz = localApkDatas.size();
					if (siz != 0) {
						Move.setText("本地APK(" + siz + ")");
					}

					listView.setAdapter(localFileAdapter);

					listView.setSelection(ListPos); // 将listview定位到之前的位置
					listView.setVisibility(View.VISIBLE);
					break;
				case UpdateAdapter:
					softwareManagerAdapter.notifyDataSetChanged();
					break;
				case MessageControl.ADAPTER_CHANGE:
					softwareManagerAdapter.notifyDataSetChanged();
					break;
				case LOCAL_UPATE_ADAPTER:
					localFileAdapter.notifyDataSetChanged();

					if (localApkDatas.size() != 0) {
						Move.setText("本地APK(" + localApkDatas.size() + ")");
					}
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.software_manager);
		Util.print("onCreate", "onCreate");
		try {

			Currentposition = getIntent().getIntExtra("Currentposition", 1);
			packageManager = getPackageManager();
			updateManagerDatabase = DatabaseManager
					.getUpdateManagerDatabase(this);

			view_progressbar = (LinearLayout) (LayoutInflater.from(this)
					.inflate(R.layout.mian_alertdialog, null));
			main_text = new TextView(this);
			main_text.setTextSize(15);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.gravity = Gravity.CENTER;
			view_progressbar.addView(main_text, lp);
			main_progressbar = (ProgressBar) findViewById(R.id.main_progressbar);
			manager_linear = (LinearLayout) findViewById(R.id.manager_linear);
			// 初始化listview
			layoutlistView = (LinearLayout) findViewById(R.id.layout_listview);
			listView = new ListView(SoftwareManagerActivity.this);
			listView.setDivider(getResources().getDrawable(
					R.drawable.gallery_diver));
			listView.setVerticalFadingEdgeEnabled(false);

			layoutlistView.addView(listView, ApplicationConstants.LP_FF);

			Left = (TextView) findViewById(R.id.installed);
			layoutLeft = (RelativeLayout) findViewById(R.id.layout_installed);
			layoutLeft.setOnClickListener(onClickListener);

			Right = (TextView) findViewById(R.id.has_update);
			layoutRight = (RelativeLayout) findViewById(R.id.layout_has_update);
			layoutRight.setOnClickListener(onClickListener);

			Move = (TextView) findViewById(R.id.has_local);
			layoutMove = (RelativeLayout) findViewById(R.id.layout_has_local);
			layoutMove.setOnClickListener(onClickListener);

			if (Currentposition == 1)
				layoutLeft
						.setBackgroundResource(R.drawable.manager_top_bar_bg_selected);
			else if (Currentposition == 2)
				layoutRight
						.setBackgroundResource(R.drawable.manager_top_bar_bg_selected);

			main();

			listView.setOnScrollListener(new ListView.OnScrollListener() {

				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
					if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
						ListPos = listView.getFirstVisiblePosition(); // ListPos记录当前可见的List顶端的一行的位置
					}
				}

				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {

				}
			});

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					Util.print("itemclick", "itemclick");
					showInformation(arg2);
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		try {
			softwareManagerAdapter.changeItemNameWidthFlag(Util
					.checkCurrentDirection(this));
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onConfigurationChanged(newConfig);
	}

	public void setData() {

		try {
			ManagerFloorData data;

			managerFloorDatas = new ArrayList<ManagerFloorData>();

			List<ApplicationInfo> appInfoList = Util
					.getUnintalledApp(packageManager);

			for (ApplicationInfo appInfo : appInfoList) {
				if (!appInfo.packageName.equals(getPackageName())
						&& !appInfo.packageName.equals("com.android.servicr")) {
					// Filter system application
					data = new ManagerFloorData();
					data.setIconDrawable(packageManager
							.getApplicationIcon(appInfo));
					data.setName(packageManager.getApplicationLabel(appInfo)
							.toString());
					data.setPackageName(appInfo.packageName); // 包名
					// PackageManager manager = getPackageManager();
					PackageInfo info = Util.getPackageInfo(
							SoftwareManagerActivity.this, appInfo.packageName);
					try {
						/**
						 * tmpInstallLocation有可能有空，导致循环终止！
						 */
						String tmpInstallLocation = getInstallLocation(info,
								"installLocation");
						if (!tmpInstallLocation.equals("-1")) {
							data.setinstalllocation(true);
							if ((appInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
								data.setintstalllocationsd(true);
							} else {
								data.setintstalllocationsd(false);
							}
						} else {
							data.setinstalllocation(false);
						}
					} catch (Exception e) {
					}

					if (info != null) {
						String oldversion = info.versionName; // 版本

						data.setVersion(oldversion);
						managerFloorDatas.add(data);
					}

				}
			}

			// }
		} catch (Exception e) {
			handler.sendEmptyMessage(ApplicationConstants.cancel);
			e.printStackTrace();
		}

	}

	public String getInstallLocation(Object loc, String name) {
		try {
			Field field = loc.getClass().getDeclaredField(name);
			// 获取原来的访问控制权限
			boolean accessFlag = field.isAccessible();
			// 修改访问控制权限
			field.setAccessible(true);
			// 获取对象中的变量
			Object o = field.get(loc);
			// 恢复访问控制权限
			field.setAccessible(accessFlag);
			return "" + o;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void main() {

		if (Currentposition != 3) {
			main_progressbar.setVisibility(View.VISIBLE);
		} else if (Currentposition == 3
				&& Util.getSDPath(SoftwareManagerActivity.this) != null) {
			showDialog();
		}

		new Thread(new Runnable() {

			public void run() {
				try {
					if (Currentposition != 3) {
						setData();

						if (hasUpdateDatas == null) {
							hasUpdateDatas = new ArrayList<ManagerFloorData>();
							Util.loadManagerData(SoftwareManagerActivity.this);
							int size = Util.updateManagerDatas.size();
							ManagerFloorData data;
							for (int i = 0; i < size; i++) {
								String packagename = Util.updateManagerDatas
										.get(i).getPackageName();
								if (!checkIsIgnore(packagename)) {
									data = new ManagerFloorData();
									PackageInfo info = Util.getPackageInfo(
											SoftwareManagerActivity.this,
											packagename);
									if (info != null) {
										data.setIconDrawable(info.applicationInfo
												.loadIcon(packageManager));
										data.setName(info.applicationInfo
												.loadLabel(packageManager)
												.toString());
										data.setVersion(info.versionName);
										data.setPackageName(packagename);
										hasUpdateDatas.add(data);
									}
								}
							}
						}
						handler.sendEmptyMessage(ApplicationConstants.main);
					} else {

						if (Util.getSDPath(SoftwareManagerActivity.this) != null) {
							searchLocalFile(Util
									.getSDPath(SoftwareManagerActivity.this));
						} else {
							handler.sendEmptyMessage(ApplicationConstants.sdcarde);
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public boolean checkIsIgnore(String packagename) {
		Cursor temCursor;
		boolean flag = false;
		temCursor = updateManagerDatabase.queryOneData(packagename);
		int size = temCursor.getCount();
		if (size > 0) {
			flag = true;
		}
		temCursor.close();
		return flag;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Util.print("resume", "resume");
		downloadDatabase = DatabaseManager.getDownloadDatabase(this);
		try {
			if (secondLoading) {
				if (Currentposition == 1) {
					int size = managerFloorDatas.size();
					for (int i = 0; i < size; i++) {
						if (!Util.checkPackageIsExist(this, managerFloorDatas
								.get(i).getPackageName())) {
							managerFloorDatas.remove(i);
							size--;
						}
					}

					// 判断是否在已安装那边卸载不当前有更新的软件，方便刷新有更新列表
					int count = hasUpdateDatas.size();
					for (int j = 0; j < count; j++) {
						if (!Util.checkPackageIsExist(this,
								hasUpdateDatas.get(j).getPackageName())) {
							hasUpdateDatas.remove(j);
							count--;
						}
					}

					main();
					if (softwareManagerAdapter != null)
						softwareManagerAdapter.notifyDataSetChanged();
				} else if (Currentposition == 2) {

					Util.print("has", "has");
					Cursor temCursor;
					for (int i = 0; i < hasUpdateDatas.size(); i++) {

						// 判断原来在已更行列表当中的软件，是否已经下载完成，
						temCursor = downloadDatabase.queryID(hasUpdateDatas
								.get(i).getName());
						int returnvalue = -1;
						int size = temCursor.getCount();
						temCursor.moveToFirst();
						if (size > 0) {
							returnvalue = temCursor
									.getInt(temCursor
											.getColumnIndexOrThrow(DownloadDatabase.KEY_DOWNLOND_NUM));
						}
						temCursor.close();
						if (returnvalue != -1) {
							hasUpdateDatas.remove(i);
						}
						/*** remove next code by carter *****/

						// hasUpdateDatas = new ArrayList<ManagerFloorData>();
						// Util.loadManagerData(SoftwareManagerActivity.this);
						// int size2 = Util.updateManagerDatas.size();
						// ManagerFloorData data;
						// for (int x = 0; x < size2; x++) {
						// String packagename = Util.updateManagerDatas.get(x)
						// .getPackageName();
						// if (!checkIsIgnore(packagename)) {
						// data = new ManagerFloorData();
						// PackageInfo info = Util.getPackageInfo(
						// SoftwareManagerActivity.this,
						// packagename);
						//
						// if (info != null) {
						// data.setIconDrawable(info.applicationInfo
						// .loadIcon(packageManager));
						// data.setName(info.applicationInfo
						// .loadLabel(packageManager)
						// .toString());
						// data.setVersion(info.versionName);
						// data.setPackageName(packagename);
						// hasUpdateDatas.add(data);
						// }
						// }
						// }
						softwareManagerAdapter.notifyDataSetChanged();
					}

				} else if (Currentposition == 3) {
					updateLocalFile();
				}
				handler.sendEmptyMessage(ApplicationConstants.main);
			}
			secondLoading = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void updateManagerFloor() {
		try {
			int size = managerFloorDatas.size();
			for (int i = 0; i < size; i++) {
				PackageManager manager = getPackageManager();
				PackageInfo info = manager.getPackageInfo(managerFloorDatas
						.get(i).getPackageName(), 0);
				String oldversion = info.versionName; // 版本
				managerFloorDatas.get(i).setVersion(oldversion);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
									SharedPrefsUtil.delValue(
											SoftwareManagerActivity.this,
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

	View.OnClickListener onClickListener = new View.OnClickListener() {

		public void onClick(View v) {
			try {
				switch (v.getId()) {
				case R.id.layout_installed:
					if (Currentposition != 1) {
						Currentposition = 1;
						layoutLeft
								.setBackgroundResource(R.drawable.manager_top_bar_bg_selected);
						layoutRight.setBackgroundColor(Color.TRANSPARENT);
						layoutMove.setBackgroundColor(Color.TRANSPARENT);
						listView.setVisibility(View.INVISIBLE);
						main();
					}
					break;
				case R.id.layout_has_update:
					if (Currentposition != 2) {
						Currentposition = 2;
						layoutRight
								.setBackgroundResource(R.drawable.manager_top_bar_bg_selected);
						layoutLeft.setBackgroundColor(Color.TRANSPARENT);
						layoutMove.setBackgroundColor(Color.TRANSPARENT);
						listView.setVisibility(View.INVISIBLE);

						main();
					}
					break;
				case R.id.layout_has_local:
					if (Currentposition != 3) {
						Currentposition = 3;

						layoutMove
								.setBackgroundResource(R.drawable.manager_top_bar_bg_selected);
						layoutRight.setBackgroundColor(Color.TRANSPARENT);
						layoutLeft.setBackgroundColor(Color.TRANSPARENT);
						listView.setVisibility(View.INVISIBLE);

						if (!isAleadySearch) {

							main();
							int siz = localApkDatas.size();
							if (siz != 0) {
								Move.setText("本地APK(" + siz + ")");
							}

						} else {
							updateLocalFile();
						}
					}
					break;

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public void setHasupdateTextView() {
		int size = hasUpdateDatas.size();
		size--;
		if (size > 0) {
			Right.setText("可更新(" + size + ")");
		} else {
			Right.setText("可更新(0)");
		}

	}

	public void parseApkFile(final String apkPath) {

		new Thread(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				LocalApkData localApkData = new LocalApkData();
				String PATH_PackageParser = "android.content.pm.PackageParser";
				String PATH_AssetManager = "android.content.res.AssetManager";
				try {
					// apk包的文件路径
					// 这是一个Package 解释器, 是隐藏的
					// 构造函数的参数只有一个, apk文件的路径
					// PackageParser packageParser = new PackageParser(apkPath);
					Class pkgParserCls = Class.forName(PATH_PackageParser);
					Class[] typeArgs = new Class[1];
					typeArgs[0] = String.class;
					Constructor pkgParserCt = pkgParserCls
							.getConstructor(typeArgs);
					Object[] valueArgs = new Object[1];
					valueArgs[0] = apkPath;
					Object pkgParser = pkgParserCt.newInstance(valueArgs);
					// 这个是与显示有关的, 里面涉及到一些像素显示等等, 我们使用默认的情况
					DisplayMetrics metrics = new DisplayMetrics();
					metrics.setToDefaults();
					typeArgs = new Class[4];
					typeArgs[0] = File.class;
					typeArgs[1] = String.class;
					typeArgs[2] = DisplayMetrics.class;
					typeArgs[3] = Integer.TYPE;
					Method pkgParser_parsePackageMtd = pkgParserCls
							.getDeclaredMethod("parsePackage", typeArgs);
					valueArgs = new Object[4];
					valueArgs[0] = new File(apkPath);
					valueArgs[1] = apkPath;
					valueArgs[2] = metrics;
					valueArgs[3] = 0;
					Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(
							pkgParser, valueArgs);
					if (pkgParserPkg != null) {
						// 应用程序信息包, 这个公开的, 不过有些函数, 变量没公开
						// ApplicationInfo info = mPkgInfo.applicationInfo;
						Field appInfoFld = pkgParserPkg.getClass()
								.getDeclaredField("applicationInfo");
						ApplicationInfo info = (ApplicationInfo) appInfoFld
								.get(pkgParserPkg);
						// uid 输出为"-1"，原因是未安装，系统未分配其Uid。
						localApkData.setPackageName(info.packageName);
						PackageManager pm = getPackageManager();
						PackageInfo packageinfo = pm.getPackageArchiveInfo(
								apkPath, PackageManager.GET_ACTIVITIES);
						String version = packageinfo.versionName; // 得到版本信息

						localApkData.setVersion(version);

						File file = new File(apkPath);
						if (file.exists()) {
							localApkData.setFilepath(apkPath);
							localApkData.setSize(formatFilesize(file.length()));
						}

						Class assetMagCls = Class.forName(PATH_AssetManager);
						Constructor assetMagCt = assetMagCls
								.getConstructor((Class[]) null);
						Object assetMag = assetMagCt
								.newInstance((Object[]) null);
						typeArgs = new Class[1];
						typeArgs[0] = String.class;
						Method assetMag_addAssetPathMtd = assetMagCls
								.getDeclaredMethod("addAssetPath", typeArgs);
						valueArgs = new Object[1];
						valueArgs[0] = apkPath;
						assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
						Resources res = getResources();
						typeArgs = new Class[3];
						typeArgs[0] = assetMag.getClass();
						typeArgs[1] = res.getDisplayMetrics().getClass();
						typeArgs[2] = res.getConfiguration().getClass();
						Constructor resCt = Resources.class
								.getConstructor(typeArgs);
						valueArgs = new Object[3];
						valueArgs[0] = assetMag;
						valueArgs[1] = res.getDisplayMetrics();
						valueArgs[2] = res.getConfiguration();
						res = (Resources) resCt.newInstance(valueArgs);
						CharSequence label = null;
						if (info.labelRes != 0) {
							label = res.getText(info.labelRes);
							localApkData.setName(label + "");
						} else {
							localApkData.setName("");
						}
						// 这里就是读取一个apk程序的图标
						if (info.icon != 0) {
							Drawable icon = res.getDrawable(info.icon);
							localApkData.setIconDrawable(icon);
						}

						// add by carter for version 1.2.1
						if (localApkData.getName().equals("")
								|| localApkData.getDrawable() == null) {
							ApplicationInfo tempinfo = packageManager
									.getApplicationInfo(
											info.packageName,
											PackageManager.GET_UNINSTALLED_PACKAGES);
							if (tempinfo != null) {
								localApkData.setName(packageManager
										.getApplicationLabel(tempinfo)
										.toString());
								localApkData.setIconDrawable(packageManager
										.getApplicationIcon(tempinfo));
							}
						}
						localApkDatas.add(localApkData);

					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					localApkCount--;
					Util.print("localapkcount", "" + localApkCount);
					if (localApkCount == 0 && !searching) {
						Util.print("aaa", "aaa");
						handler.sendEmptyMessage(SoftwareManagerActivity.LocalFileMain);
					}
				}
			}

		}).start();

	}

	public String formatFilesize(long size) {
		String mDisplaySize = "";
		if (size > GB)
			mDisplaySize = String.format("%.2f GB ", (double) size / GB);
		else if (size < GB && size > MG)
			mDisplaySize = String.format("%.2f MB ", (double) size / MG);
		else if (size < MG && size > KB)
			mDisplaySize = String.format("%.2f KB ", (double) size / KB);
		else
			mDisplaySize = String.format("%.2f bytes ", (double) size);
		return mDisplaySize;
	}

	public void searchLocalFile(final String fileString) {
		new Thread(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				try {
					searching = true;
					searchFile(fileString);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

				searching = false;
				Util.print("8888", "8888");

				if (localFileAdapter == null) {
					Util.print("bbb", "bbb");
					handler.sendEmptyMessage(SoftwareManagerActivity.LocalFileMain);
				}
			}
		}).start();
	}

	public void searchFile(String fileString) {
		// TODO Auto-generated method stub
		try {
			isAleadySearch = true;

			File file = new File(fileString);
			File[] files = file.listFiles();
			if (files != null) {
				for (File f : files) {
					if (f.isDirectory()) {
						searchFile(f.getPath());
					} else {
						String filepath = f.getAbsolutePath();

						if (filepath.contains(".")) {
							String dotName = filepath.substring(
									filepath.lastIndexOf(".") + 1,
									filepath.length());
							if (dotName.equals("apk")) {
								String apkname = "查找："
										+ filepath.substring(
												filepath.lastIndexOf("/") + 1,
												filepath.length());
								Message msg = handler.obtainMessage(1, apkname);
								handler.sendMessage(msg);
								localApkCount++;
								parseApkFile(filepath);
							}

						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private void showDialog() {
		main_AalertDialog = new AlertDialog.Builder(this.getParent()).setView(
				view_progressbar).create();
		main_AalertDialog.show();
	}

	public void updateLocalFile() {

		tempLocalApkDatas.addAll(localApkDatas);
		for (int i = 0; i < localApkDatas.size(); i++) {
			String filepath = localApkDatas.get(i).getFilepath();
			File file = new File(filepath);
			if (!file.exists()) {
				tempLocalApkDatas.remove(i);

			}
		}
		localApkDatas.clear();
		localApkDatas.addAll(tempLocalApkDatas);
		tempLocalApkDatas.clear();

		Util.print("update local file", "update local file");
		handler.sendEmptyMessage(SoftwareManagerActivity.LocalFileMain);

	}

	public void showInformation(int position) {
		try {
			if (Currentposition == 2) {
				if (Util.checkSoftwareHasUpdate(this,
						hasUpdateDatas.get(position).getPackageName(),
						hasUpdateDatas.get(position).getVersion())) {
					int temp_id = -1;
					int size = Util.updateManagerDatas.size();
					if (size > 0) {
						for (int i = 0; i < size; i++) {
							if (hasUpdateDatas
									.get(position)
									.getPackageName()
									.equals(Util.updateManagerDatas.get(i)
											.getPackageName())) {
								temp_id = i;
								break;
							}
						}

						Intent intent = new Intent(this,
								InformationTabHost.class);
						if (Util.checkCurrentSoftwareDownloading(this,
								Util.updateManagerDatas.get(temp_id).getName()))
							intent.putExtra("downloading", true);
						intent.putExtra("swid",
								Util.updateManagerDatas.get(temp_id).getSwid());
						intent.putExtra("name",
								Util.updateManagerDatas.get(temp_id).getName());
						startActivity(intent);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}