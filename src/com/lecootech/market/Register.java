package com.lecootech.market;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lecootech.market.common.ApplicationConstants;
import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.common.WebAddress;

/**
 * @author zhaweijin
 * @function 注册页面
 */
public class Register extends Activity {

	// 按钮及输入框变量
	private Button next;
	private EditText userEditText;
	private EditText emailEditText;
	private EditText passwordEditText;
	private EditText secondpasswordEditText;

	private final int code = 1;

	// 存放的输入变量
	private String user;
	private String email;
	private String password;
	private String secondpassword;
	private SharedPreferences preference;

	private ProgressDialog progressdialog;
	private boolean tempRegister = false;

	private String swid = "";
	private boolean isCommentActivity = false;
	/**
	 * @author zhaweijin
	 * @function UI线程处理
	 */
	Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case ApplicationConstants.main:
					progressdialog.dismiss();
					if (tempRegister) {
						Toast.makeText(Register.this,
								msg.getData().getString("prompt"),
								Toast.LENGTH_LONG).show();
						Editor editor = preference.edit();
						editor.putString("user", user);
						editor.putString("password", password);
						editor.commit();
						finish();
					} else {
						Toast.makeText(Register.this,
								msg.getData().getString("prompt"),
								Toast.LENGTH_LONG).show();
					}
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				Util.print("Register UI thread error",
						"Register UI thread error");
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.register);

			if (getIntent() != null) {
				isCommentActivity = true;
				swid = getIntent().getStringExtra("swid");
			}
			// 初始化进度条
			progressdialog = Util.getProgressDialog(this, "注册中...");

			// 初始化输入框
			userEditText = (EditText) findViewById(R.id.user);
			emailEditText = (EditText) findViewById(R.id.email);
			passwordEditText = (EditText) findViewById(R.id.password);
			secondpasswordEditText = (EditText) findViewById(R.id.secondpassword);
			preference = PreferenceManager.getDefaultSharedPreferences(this);

			next = (Button) findViewById(R.id.next);

			/**
			 * @author zhaweijin
			 * @function 注册按钮事件
			 */
			next.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					try {
						user = userEditText.getText().toString();
						email = emailEditText.getText().toString();
						password = passwordEditText.getText().toString();
						secondpassword = secondpasswordEditText.getText()
								.toString();
						Util.print("email", email);
						if (user.length() < 2) {
							Toast.makeText(
									Register.this,
									getString(R.string.register_namelength_small),
									Toast.LENGTH_LONG).show();
						}
						if (password.length() < 6
								|| secondpassword.length() < 6) {
							Toast.makeText(
									Register.this,
									getString(R.string.register_passwordlength_small),
									Toast.LENGTH_LONG).show();
						} else if (!password.equals(secondpassword)) {
							Toast.makeText(
									Register.this,
									getString(R.string.register_password_two_discord),
									Toast.LENGTH_LONG).show();
						} else if (!isEmail(email)) {
							Util.print("a", "a");
							Toast.makeText(Register.this, "email格式不正确!",
									Toast.LENGTH_LONG).show();
						} else {
							mainEnter();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Util.print("Register oncreate error",
								"Register oncreate error");
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			Util.print("Register oncreate error", "Register oncreate error");
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case code:
			finish();
			break;
		}
	}

	/**
	 * @author zhaweijin
	 * @function 判断email是否合法
	 */
	public boolean isEmail(String strEmail) {
		int i = strEmail.length();
		int temp = strEmail.indexOf('@');
		int tempd = strEmail.indexOf('.');
		if (temp > 1) {
			if ((i - temp) > 3) {
				if ((i - tempd) > 0) {
					return true;
				}

			}
		}
		return false;
	}

	/**
	 * @author zhaweijin
	 * @function 返回键功能重写
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Intent intent = new Intent(Register.this, Login.class);
		startActivityForResult(intent, 1);
		// finish();
		return super.onKeyDown(keyCode, event);
	}

	public void networkException(String title) {
		new AlertDialog.Builder(this.getParent()).setTitle(title)
				.setIcon(R.drawable.report_error)
				.setMessage(getResources().getString(R.string.web_message))
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						mainEnter();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).show();
	}

	/**
	 * @author zhaweijin
	 * @function 启动加载数据的入口
	 */
	public void mainEnter() {
		// if(Util.getNetworkState(this))
		// {
		progressdialog.show();
		new Thread(new Runnable() {

			public void run() {
				try {
					DefaultHttpClient httpclient = new DefaultHttpClient();
					HttpResponse response;
					String url;
						url = SharedPrefsUtil.getValue(
								Register.this, "DomainName",
								WebAddress.comString)+WebAddress.registString;
					HttpPost httpost = new HttpPost(url);
					httpost.getParams().setBooleanParameter(
							CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					nvps.add(new BasicNameValuePair("userid", user));
					nvps.add(new BasicNameValuePair("password", password));
					nvps.add(new BasicNameValuePair("email", email));

					httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
					response = httpclient.execute(httpost);
					HttpEntity entity = response.getEntity();


					/* HTML POST response BODY */
					String trRet = EntityUtils.toString(entity);
					Pattern pattern = Pattern.compile("\\w.*");
					Matcher matcher = pattern.matcher(trRet);
					if (matcher.find()) {
						trRet = matcher.group();
					}
					String prompt = "";
					if (entity != null) {
						entity.consumeContent();
					}
					if (trRet.equals("")) {
						prompt = getString(R.string.register_error);
						tempRegister = false;
					} else if (trRet.equals("yes")) {
						Util.print("ss", trRet);
						// tempRegister =
						// trRet.equals(getString(R.string.register_success_webback));
						// preference.edit().putString("memberid",
						// trRet).commit();
						prompt = getString(R.string.register_ok);
						tempRegister = true;
					} else {
						prompt = getString(R.string.register_error);
						tempRegister = false;
					}


					Message message = new Message();
					message.what = ApplicationConstants.main;
					Bundle bundle = new Bundle();
					bundle.putString("prompt", prompt);
					message.setData(bundle);
					myHandler.sendMessage(message);

				} catch (Exception e) {
					e.printStackTrace();
					Util.print("register_error", "register main error");
				}
			}
		}).start();
		// }
		// else {
		// networkException(getResources().getString(R.string.web_cannot_connection));
		// }

	}

}
