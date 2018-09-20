package com.lecootech.market;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lecootech.market.common.ApplicationConstants;
import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.common.WebAddress;
import com.mobclick.android.MobclickAgent;

public class ReportErrorActivity extends Activity {
	

	private ImageButton back;
	private ProgressDialog progressdialog;
	private String swid = "";
	private String report = "other";

	private RadioGroup radioGroup;
	private RadioButton radioButton1;
	private RadioButton radioButton2;
	private RadioButton radioButton3;
	private RadioButton radioButton4;
	private RadioButton radioButton5;
	private RadioButton radioButton6;

	private EditText editTextReport;

	private TextView mainNameTextView;


	private SharedPreferences settingPreferences;

	private Button commitButton;
	private Button cancelButton;
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}


	private static final int send_failed = 0x121;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ApplicationConstants.main:
				Toast.makeText(ReportErrorActivity.this, "报告成功", 1500).show();
				progressdialog.cancel();
				finish();
				break;
			case send_failed:
				Toast.makeText(ReportErrorActivity.this, "报告失败", 1500).show();
				progressdialog.cancel();
				finish();
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.report_error_activity);

		settingPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (settingPreferences
				.getBoolean("checkbox_switch_display_start", true)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		progressdialog = Util.getProgressDialog(this, "加载中...");
		if (getIntent() != null)
			swid = getIntent().getStringExtra("swid");

		mainNameTextView = (TextView) findViewById(R.id.asset_title);
		mainNameTextView.setText("报告错误");

		commitButton = (Button) findViewById(R.id.ok_button);
		cancelButton = (Button) findViewById(R.id.cancel_button);
		commitButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (report.equals("other")
						&& editTextReport.getText().toString().equals("")) {
					Toast.makeText(ReportErrorActivity.this, "请输入其它内容再提交", 1500)
							.show();
				} else {
						commentReport(SharedPrefsUtil.getValue(
								ReportErrorActivity.this, "DomainName",
								WebAddress.comString)+WebAddress.softwareErrorReport, swid, report);
				}

			}
		});
		cancelButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});

		editTextReport = (EditText) findViewById(R.id.error_content);
		radioButton1 = (RadioButton) findViewById(R.id.error_type_1);
		radioButton2 = (RadioButton) findViewById(R.id.error_type_2);
		radioButton3 = (RadioButton) findViewById(R.id.error_type_3);
		radioButton4 = (RadioButton) findViewById(R.id.error_type_4);
		radioButton5 = (RadioButton) findViewById(R.id.error_type_5);
		radioButton6 = (RadioButton) findViewById(R.id.error_type_6);

		radioGroup = (RadioGroup) findViewById(R.id.error_type_group);
		radioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					public void onCheckedChanged(RadioGroup group, int checkedId) {
						Util.print("checkid", "" + checkedId);
						if (radioButton1.getId() == checkedId) {
							report = "1";
							editTextReport.setVisibility(View.INVISIBLE);
						} else if (radioButton2.getId() == checkedId) {
							report = "2";
							editTextReport.setVisibility(View.INVISIBLE);
						} else if (radioButton3.getId() == checkedId) {
							report = "3";
							editTextReport.setVisibility(View.INVISIBLE);
						} else if (radioButton4.getId() == checkedId) {
							report = "4";
							editTextReport.setVisibility(View.INVISIBLE);
						} else if (radioButton5.getId() == checkedId) {
							report = "5";
							editTextReport.setVisibility(View.INVISIBLE);
						} else if (radioButton6.getId() == checkedId) {
							report = "other";
							editTextReport.setVisibility(View.VISIBLE);
						}
					}
				});

	}

	public void commentReport(String webpath, String swid, String report) {
		// if(Util.getNetworkState(this))
		// {
		progressdialog.show();
		final String tempwebpath = webpath;
		final String tempswid = swid;
		final String tempreport = report + editTextReport.getText().toString();
		if (tempreport.length() > 100) {
			Toast.makeText(this, "评论内容超出100个", 1500).show();
		} else {
			new Thread(new Runnable() {

				public void run() {
					try {
						if(Util.checkIsCmwapNetwork(ReportErrorActivity.this))
						{
							String parameter = "swid="+ tempswid + "&" + 
							                   "report=" + tempreport + "&" +
							                   "username=" + "" + "&" +
							                   "mobiletype=" + android.os.Build.MODEL +"&" +
							                   "version=" + ReportErrorActivity.this.getPackageManager().getPackageInfo(
														ReportErrorActivity.this.getPackageName(), 0).versionName;
							
							Util.print("cmwap-->", "cmwap-->"+parameter);
							URL url = new URL(WebAddress.proxypath+
									tempwebpath.substring(tempwebpath.lastIndexOf("/")+1, tempwebpath.length())+parameter);
							HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 
							conn.setRequestProperty("X-Online-Host", WebAddress.comString);	
							
							conn.setDoInput(true);
							conn.setConnectTimeout(20000);
							conn.setReadTimeout(40000);
							conn.connect();
						}
						else {
							DefaultHttpClient httpclient = new DefaultHttpClient();
							HttpPost httpost = new HttpPost(tempwebpath);
							httpost.getParams().setBooleanParameter(
									CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
							List<NameValuePair> nvps = new ArrayList<NameValuePair>();
							nvps.add(new BasicNameValuePair("swid", tempswid));
							nvps.add(new BasicNameValuePair("report", tempreport));
							nvps.add(new BasicNameValuePair("username", ""));
							nvps.add(new BasicNameValuePair("mobiletype",
									android.os.Build.MODEL));
							nvps.add(new BasicNameValuePair("version",
									ReportErrorActivity.this.getPackageManager().getPackageInfo(
											ReportErrorActivity.this.getPackageName(), 0).versionName));

							httpost.setEntity(new UrlEncodedFormEntity(nvps,
									"gb2312"));
							httpclient.execute(httpost);
						}
						


						handler.sendEmptyMessage(ApplicationConstants.main);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
			
		}

	}

	

}
