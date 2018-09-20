package com.lecootech.market;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lecootech.market.common.ApplicationConstants;
import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.common.WebAddress;
import com.mobclick.android.MobclickAgent;

public class AddCommentActivity extends Activity {

	// 跳过来之前已经判断了当前软件包，已经安装过了
	private TextView mainNameTextView;
	private EditText comment_type;

	private String username = "";
	private String password = "";
	private String content = "";
	private String score = "";
	private String swid = "";
	private String packagename = "";
	private EditText contentEditText;
	private RatingBar ratingBar;

	private int numStr = 5;

	private Button commitButton;
	private Button cancelButton;

	private SharedPreferences settingPreferences;
	private ProgressDialog progressDialog;

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ApplicationConstants.main:
				Toast.makeText(AddCommentActivity.this,
						R.string.comment_succes, 1500).show();
				handler.sendEmptyMessage(ApplicationConstants.cancel);

				setResult(RESULT_OK);
				finish();
				break;
			case ApplicationConstants.cancel:
				progressDialog.cancel();
				break;
			case ApplicationConstants.exception:
				Toast.makeText(AddCommentActivity.this, R.string.comment_error,
						1500).show();
				handler.sendEmptyMessage(ApplicationConstants.cancel);
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
		setContentView(R.layout.add_comment);

		settingPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (settingPreferences
				.getBoolean("checkbox_switch_display_start", true)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		if (getIntent() != null)
			swid = getIntent().getStringExtra("swid");

		progressDialog = Util.getProgressDialog(this, "提交中，请稍后！");

		mainNameTextView = (TextView) findViewById(R.id.asset_title);
		mainNameTextView.setText(R.string.comment_my_comment);
		comment_type = (EditText) findViewById(R.id.comment_type);
		commitButton = (Button) findViewById(R.id.ok_button);
		cancelButton = (Button) findViewById(R.id.cancel_button);
		contentEditText = (EditText) findViewById(R.id.comment_text);
		ratingBar = (RatingBar) findViewById(R.id.rating_bar);
		username = SharedPrefsUtil.getValue(AddCommentActivity.this, "type",
				android.os.Build.MODEL);
		comment_type.setHint(username);

		ratingBar.setRating(5);

		
		
		commitButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				commitRecomments();
			}
		});
		
		
		cancelButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		username = settingPreferences.getString("user", "");
		password = settingPreferences.getString("password", "");
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	public void commitRecomments() {
		content = contentEditText.getText().toString();
		if (content.length() > 200) {
			Toast.makeText(this, R.string.comment_substance, 1500).show();
		} else if (comment_type.getText().toString().getBytes().length > 18) {
			Toast.makeText(AddCommentActivity.this, "昵称过长！", Toast.LENGTH_LONG)
					.show();
		} else {
			try {
				if (username.trim().equals("")) {
					if (comment_type.getText().toString().equals("")) {
						username = SharedPrefsUtil.getValue(
								AddCommentActivity.this, "type",
								android.os.Build.MODEL);
					} else {
						username = comment_type.getText().toString();
						SharedPrefsUtil.putValue(AddCommentActivity.this,
								"type", comment_type.getText().toString());

					}

				} else {

				}
				if (content.trim().equals("")) {
					Toast.makeText(AddCommentActivity.this,
							R.string.comment_substance_empty, Toast.LENGTH_LONG)
							.show();
				} else {
					commit();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}



	public void commit() {
		progressDialog.show();
		new Thread(new Runnable() {

			public void run() {
				try {
					int numstr = (int) ratingBar.getRating();
					Boolean temppringrun = commit(swid, username, content,
							numstr + "");

					if (temppringrun)
						handler.sendEmptyMessage(ApplicationConstants.main);
					else
						handler.sendEmptyMessage(ApplicationConstants.exception);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * @author zhaweijin
	 * @function 判断网络返回的数据，评论是否成功 ---------------->提交评论要返回一个成功是否的值　
	 */
	public boolean commit(String swid, String username, String content,
			String soft_score) {
		Util.print("value", swid + "\n" + username + "\n" + content + "\n"
				+ soft_score);

		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			String url;
			url = SharedPrefsUtil.getValue(AddCommentActivity.this,
					"DomainName", WebAddress.comString)
					+ WebAddress.softwareCommitComment;
			HttpPost httpost = new HttpPost(url);
			httpost.getParams().setBooleanParameter(
					CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();

			nvps.add(new BasicNameValuePair("swid", swid));
			nvps.add(new BasicNameValuePair("username", username));
			nvps.add(new BasicNameValuePair("content", content));
			nvps.add(new BasicNameValuePair("soft_score", soft_score));
			nvps.add(new BasicNameValuePair("version", getPackageManager().getPackageInfo(
					getPackageName(), 0).versionName));

			httpost.setEntity(new UrlEncodedFormEntity(nvps, "gb2312"));
			response = httpclient.execute(httpost);
			HttpEntity entity = response.getEntity();

			/* HTML POST response BODY */
			String result = EntityUtils.toString(entity);
			if (entity != null) {
				entity.consumeContent();
			}
			result = result.trim();
			if (result.equals("yes")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
