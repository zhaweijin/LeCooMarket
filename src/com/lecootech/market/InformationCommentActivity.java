package com.lecootech.market;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lecootech.market.adapter.SoftwareCommnetAdapter;
import com.lecootech.market.common.ApplicationConstants;
import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.common.WebAddress;
import com.lecootech.market.data.CommentData;
import com.lecootech.market.handle.DataSet;

public class InformationCommentActivity extends Activity {
	private LinearLayout main_progressbar;
	private Button reportButton;
	private Button addComment;
	// center
	private ArrayList<CommentData> commentDatas = new ArrayList<CommentData>();
	private SoftwareCommnetAdapter commnetAdapter;
	private LinearLayout center_layout_listview;
	private ListView center_listview;
//	private int center_UseHeight = 40 + 100;
	private View progressView;

	private LinearLayout information_linear;
	private ImageView information_image;

	private DataSet dataSet;
	private final static int displayDownloading = 0x345;

	private String swid = "";
	private String packagename = "";

	//network failed 
	private RelativeLayout networklayout = null;
	
	// 评论分页参数
	private boolean pingrun_loadfinish = false;
	private boolean pingrun_loaded = false;
	private int pingrun_page = 1;
	private int pingrun_perpage = 10;
	private int pingrun_length;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ApplicationConstants.main:
				// int size = dataSet.getPingRuns().size();
				if(networklayout!=null)
					networklayout.setVisibility(View.INVISIBLE);
				
				int commentsize = dataSet.getCurrentSoftwareCount();
				Util.print("ping run size", commentsize + "");
				if (commentsize > 0) {
					// pingrunTextView.setText(commentTitle
					// + "当前共有【"+ commentsize + "】条评论"
					// );
					information_linear.setVisibility(View.INVISIBLE);
				} else {
					if (Util.checkPackageIsExist(
							InformationCommentActivity.this, packagename)) {
						// pingrunTextView.setText(R.string.commtent_none);
						information_linear.setVisibility(View.VISIBLE);
					} else {
						// pingrunTextView.setText(commentTitle);
						information_linear.setVisibility(View.VISIBLE);
						information_image
								.setBackgroundResource(R.drawable.pinglun);

					}

				}
				if (commentsize > pingrun_perpage) {
					center_listview.addFooterView(progressView);
					setCenterListviewOnScrollStateChange();
				}
				commnetAdapter = new SoftwareCommnetAdapter(
						InformationCommentActivity.this, commentDatas,dataSet.currentListSize);
				center_listview.setAdapter(commnetAdapter);
				pingrun_loaded = true;
				pingrun_page++;
				center_layout_listview.removeView(main_progressbar);
				main_progressbar.setVisibility(View.INVISIBLE);
				break;
			case ApplicationConstants.update:
				Util.print("update", "update");
				commnetAdapter.notifyDataSetChanged();
				if (dataSet.getPingRuns().size() < 10) {
					handler.sendEmptyMessage(ApplicationConstants.remove_foot_view);
					pingrun_loadfinish = true;
				}
				pingrun_loaded = true;
				pingrun_page++;
				break;
			case ApplicationConstants.cancel:
				main_progressbar.setVisibility(View.INVISIBLE);
				break;
			case ApplicationConstants.webTimeout:
				networkException(getResources().getString(
						R.string.web_load_timeout));
				break;
			case ApplicationConstants.exception:
				main_progressbar.setVisibility(View.INVISIBLE);
				Toast.makeText(InformationCommentActivity.this, "加载网络异常",
						Toast.LENGTH_LONG).show();
				break;
			case ApplicationConstants.remove_foot_view:
				center_listview.removeFooterView(progressView);
				commnetAdapter.notifyDataSetChanged();
				break;
			case displayDownloading:
				Toast.makeText(InformationCommentActivity.this, "正在下载...",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.software_description_recomments);

		if (getIntent() != null) {
			swid = getIntent().getStringExtra("swid");
			packagename = getIntent().getStringExtra("packagename");
		}

		Util.print("id", swid);

		addCenterLayout();
		centerMain();
	}

	public void addCenterLayout() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);

		center_layout_listview = (LinearLayout) findViewById(R.id.layout_listview);
		center_listview = new ListView(this);
		center_listview.setDivider(getResources().getDrawable(
				R.drawable.gallery_diver));
		center_listview.setDividerHeight(2);
		center_layout_listview.addView(center_listview,
				ApplicationConstants.LP_FF);
		center_listview.setCacheColorHint(Color.TRANSPARENT);
		center_listview.setVerticalFadingEdgeEnabled(false);
		main_progressbar = (LinearLayout) findViewById(R.id.main_progressbar);
		// 增加listview底部进度条
		progressView = getProgressView();
		information_linear = (LinearLayout) findViewById(R.id.information_linear);
		information_image = (ImageView) findViewById(R.id.information_image);

		LinearLayout layout_top_install_tips = (LinearLayout) findViewById(R.id.layout_no_install_tips);
		Boolean packageIsExist = false;
		packageIsExist = Util.checkPackageIsExist(this, packagename);
		Util.print("packagename", Boolean.toString(packageIsExist));

		addComment = (Button) findViewById(R.id.addComment);
		addComment.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(InformationCommentActivity.this,
						AddCommentActivity.class);
				intent.putExtra("swid", swid);
				startActivityForResult(intent, 1);
			}
		});
		

		reportButton = (Button) findViewById(R.id.reportError);
		reportButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(InformationCommentActivity.this,
						ReportErrorActivity.class);
				intent.putExtra("swid", swid);
				startActivity(intent);
			}
		});
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				pingrun_page = 1;
				centerMain();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public DataSet setCenterData() {
		try {
			Util.print(
					"--->",
					SharedPrefsUtil.getValue(InformationCommentActivity.this,
							"DomainName", WebAddress.comString)
							+ WebAddress.softwareComment
							+ swid
							+ "&"
							+ Util.postPerpageNum(pingrun_page, pingrun_perpage));
			dataSet = Util.getSoftwareWebData(this,SharedPrefsUtil.getValue(
					InformationCommentActivity.this, "DomainName",
					WebAddress.comString)
					+ WebAddress.softwareComment
					+ swid
					+ "&"
					+ Util.postPerpageNum(pingrun_page, pingrun_perpage));

			Util.print("software_size", dataSet.getCurrentSoftwareCount() + "");
			return dataSet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public void centerMain() {

		main_progressbar.setVisibility(View.VISIBLE);
		commentDatas.clear();
		new Thread(new Runnable() {

			public void run() {
				try {

					if (setCenterData() != null) {
						pingrun_length = dataSet.getPingRuns().size();
						if (pingrun_length == 0) {
							pingrun_loadfinish = true;
						} else {
							commentDatas.addAll(dataSet.getPingRuns());

						}
						handler.sendEmptyMessage(ApplicationConstants.main);
					} else
						handler.sendEmptyMessage(ApplicationConstants.webTimeout);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void setCenterListviewOnScrollStateChange() {
		center_listview.setOnScrollListener(new AbsListView.OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (view.getLastVisiblePosition() == commentDatas.size()) {
					if (pingrun_loaded && !pingrun_loadfinish) {
						Util.print("load", "load");
						pingrun_loaded = false;
						new Thread(new Runnable() {

							public void run() {
								try {
									if (setCenterData() != null) {
										pingrun_length = dataSet.getPingRuns()
												.size();
										Util.print("pingrun_length", ""
												+ pingrun_length);
										if (pingrun_length != 0) {
											commentDatas.addAll(dataSet
													.getPingRuns());
										} else {
											pingrun_loadfinish = true;
										}
										handler.sendEmptyMessage(ApplicationConstants.update);
									} else {
										handler.sendEmptyMessage(ApplicationConstants.webTimeout);
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
					centerMain();
				}
			});
	}

	public View getProgressView() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		return layoutInflater.inflate(R.layout.progressbar, null);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Util.print("cofiguration", "con");

		super.onConfigurationChanged(newConfig);
	}

}