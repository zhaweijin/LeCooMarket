package com.lecootech.market;

import java.net.URLEncoder;
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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lecootech.market.common.ApplicationConstants;
import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.common.WebAddress;

/**
 * @author zhaweijin
 * @function 登录页面
 */
public class Login extends Activity {

	SharedPrefsUtil realmname;
	int real = 0;
	// 登录按钮及输入框变量
	private Button login;
	private Button register;
	private TextView version;
	private SharedPreferences settingPreferences;
	private EditText loginusereEditText;
	private EditText loginpasswordEditText;
	private boolean flag = false;

	private final int code = 1;

	private String swid = "";
	private boolean isCommentActivity = false;

	private ProgressDialog progressdialog;
	private boolean templogin = false;
	Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case ApplicationConstants.main:
					progressdialog.dismiss();
					if (templogin) {
						Editor editor = settingPreferences.edit();
						editor.putString("password", loginpasswordEditText
								.getText().toString());
						editor.putString("user", loginusereEditText.getText()
								.toString());
						editor.commit();
						// Intent intent = new
						// Intent(Login.this,Recoment.class);
						// intent.putExtra("swid", CommonUtil.getSwid());
						Toast.makeText(Login.this, "登录成功!", Toast.LENGTH_LONG)
								.show();
						// startActivityForResult(intent, code);
						// startActivity(intent);
						// if(isCommentActivity)
						// {
						// Intent intent = new
						// Intent(Login.this,AddCommentActivity.class);
						// intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						// startActivity(intent);
						// }
						// else {

						// }
						finish();
					} else {
						if (flag)
							Toast.makeText(Login.this, "账户或密码错误!",
									Toast.LENGTH_LONG).show();
						else {
							Toast.makeText(Login.this, "请注册", Toast.LENGTH_LONG)
									.show();
						}
					}
					break;

				}
			} catch (Exception e) {
				e.printStackTrace();
				Util.print("Login UI thread error", "Login UI thread error");
			}

			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.login);

			Intent mIntent = new Intent();
			setResult(RESULT_OK, mIntent);

			if (getIntent() != null) {
				isCommentActivity = true;
				swid = getIntent().getStringExtra("swid");
			}
			// CommonUtil.addActivityToStack(this,"Login");
			settingPreferences = PreferenceManager
					.getDefaultSharedPreferences(this);

			// 初始化进度条
			progressdialog = Util.getProgressDialog(this, "登录中...");

			// 初始化版本号，且显示版本号
			version = (TextView) findViewById(R.id.init_version);
			version.setText(getPackageManager().getPackageInfo(
					getPackageName(), 0).versionName);

			loginpasswordEditText = (EditText) findViewById(R.id.password);
			login = (Button) findViewById(R.id.login);
			loginusereEditText = (EditText) findViewById(R.id.user);
			register = (Button) findViewById(R.id.register);

			// 注册按钮事件处理
			register.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					try {
						// register.setBackgroundResource(R.drawable.register_two);
						Intent intent = new Intent(Login.this, Register.class);
						intent.putExtra("swid", swid);
						// startActivityForResult(intent, code);
						startActivity(intent);
						finish();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			// 登录事件处理
			login.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					mainEnter();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			Util.print("Login oncreate error", "Login oncreate error");
		}

	}

	// /**
	// * @author zhaweijin
	// * @function 返回键功能重写
	// */
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// try {
	// // if(KeyEvent.KEYCODE_BACK==keyCode)
	// // {
	// //// Intent intent = new Intent(Login.this,Init_Update.class);
	// //// startActivity(intent);
	// //// finish();
	// return true;
	// // }
	// // else {
	// // return super.onKeyDown(keyCode, event);
	// // }
	// } catch (Exception e) {
	// e.printStackTrace();
	// return false;
	// }
	// }

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
	 * @function 获取登录之后网络返回的数据
	 */
	public boolean login(String user, String password) {
		String result = "";
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			String url;
				url = SharedPrefsUtil.getValue(
						Login.this, "DomainName",
						WebAddress.comString)+WebAddress.logingsString;
			HttpPost httpost = new HttpPost(url);
			httpost.getParams().setBooleanParameter(
					CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("userid", user));
			nvps.add(new BasicNameValuePair("password", password));

			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			response = httpclient.execute(httpost);

			HttpEntity entity = response.getEntity();

			/* HTML POST response BODY */
			result = EntityUtils.toString(entity);

			Pattern pattern = Pattern.compile("\\w.*");
			Matcher matcher = pattern.matcher(result);
			if (matcher.find()) {
				result = matcher.group();
			}
			if (entity != null) {
				entity.consumeContent();
			}
			if (result.equals("")) {
				return false;
			} else if (result.equals("no")) {
				flag = true;
				return false;
			}
			// else if(result.equals("密码错误"))
			// {
			// flag = true;
			// return false;
			// }
			else if (result.equals("yes")) {
				Util.print("login", result);
				// settingPreferences.edit().putString("user", result).commit();
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			Util.print("Login error", "Login error");
			return false;
		}
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
	 * @function 登录模块的处理
	 */
	public void mainEnter() {
		// if(Util.getNetworkState(this))
		// {
		progressdialog.show();
		new Thread(new Runnable() {

			public void run() {
				try {
					templogin = login(URLEncoder.encode(loginusereEditText
							.getText().toString()), loginpasswordEditText
							.getText().toString());

					myHandler.sendEmptyMessage(ApplicationConstants.main);
				} catch (Exception e) {
					e.printStackTrace();
					Util.print("Login mainEnter error", "Login mainEnter error");
				}
			}
		}).start();
		// }
		// else {
		// networkException(getResources().getString(R.string.web_cannot_connection));
		// }

	}

}
