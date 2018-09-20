package com.lecootech.market;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
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

import com.lecootech.market.adapter.SoftwareThridFloorAdapter;
import com.lecootech.market.common.ApplicationConstants;
import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.common.WebAddress;
import com.lecootech.market.data.MessageControl;
import com.lecootech.market.data.ThridFloorData;
import com.lecootech.market.handle.DataSet;
import com.mobclick.android.MobclickAgent;

public class SoftwareThridFloorMostUp extends Activity {
    /** Called when the activity is first created. */
	
	private LinearLayout layoutlistView;
	private ListView listView;
	private ProgressBar main_progressbar;
	
	private SoftwareThridFloorAdapter thridFloorAdapter;
	private ArrayList<ThridFloorData> thridFloorDatas = new ArrayList<ThridFloorData>();
	
	// 最新上传 up 、最多下载 most
	private String currentFlagString="";
	
	private int count=0;
	private View progressView;
	private int displayHasUsedHeight=40+30;
//	
//	private int installDisplayHasUsedHeight = 40+60+35;
	
	//network failed 
	private RelativeLayout networklayout = null;
	
	//resume loading 
	private boolean secondLoading = false;
	
	//分页
	private boolean loadfinish = false;
	private boolean loaded = false;
	private int page=1;
	private int perpage=10;
	private int length;
	private int pagecount=1;

	//dataset
	private DataSet dataSet;
	private String currentCategoryID="";
	
	private SharedPreferences settingPreferences;
	
	/**
	 * @author zhaweijin
	 * @function UI线程的处理
	 */
	Handler myHandler = new Handler()
	{
		
		@Override
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case ApplicationConstants.main:
		                // int size = dataSet.getThreeCates().size(); 
					
					if(networklayout!=null)
						networklayout.setVisibility(View.INVISIBLE);
					
		            if(dataSet.getCurrentSoftwareCount()>perpage)
		            {
		               listView.addFooterView(progressView);
		               setListviewOnScrollStateChange();
		            }
		                 
				    thridFloorAdapter = new SoftwareThridFloorAdapter(SoftwareThridFloorMostUp.this,
								SoftwareThridFloorMostUp.this,thridFloorDatas,
								Util.checkCurrentDirection(SoftwareThridFloorMostUp.this),
								listView,this);
			 		listView.setAdapter(thridFloorAdapter);
			 		    
			 	   
	                loaded = true;
	                page++;
	                main_progressbar.setVisibility(View.INVISIBLE);
					break;
				case ApplicationConstants.update:
					
					thridFloorAdapter.notifyDataSetChanged();
					if(dataSet.getThreeCates().size()<perpage)
					{
						myHandler.sendEmptyMessage(ApplicationConstants.remove_foot_view);
						loadfinish = true;
					}						
					loaded = true;
					page++;
					break;
				case ApplicationConstants.webTimeout:
					networkException(getResources().getString(R.string.web_load_timeout));
					break;
				case ApplicationConstants.exception:
					main_progressbar.setVisibility(View.INVISIBLE);
					Toast.makeText(SoftwareThridFloorMostUp.this, "加载网络异常", Toast.LENGTH_LONG).show();
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
				Util.print("thread error", "thread error");
			}
			super.handleMessage(msg);
		}
	};
	
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        

        currentFlagString = getIntent().getStringExtra("flag");
        currentCategoryID = getIntent().getStringExtra("ID");
        setContentView(R.layout.software_thridfloor_main_ap_game);
		
        
          
        settingPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		if(settingPreferences.getBoolean("checkbox_switch_display_start", true))
		{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
		}


        init();
        main();


    }
    
   
    
    
    
    private  DataSet setAdapterData()
    {
    	try {
 		if(currentFlagString.equals("up"))
 		{
 			dataSet = Util.getSoftwareWebData(this,SharedPrefsUtil.getValue(
					SoftwareThridFloorMostUp.this, "DomainName",
					WebAddress.comString)+WebAddress.thridfloor_software_listString + currentCategoryID + "&" +Util.postPerpageNum(page, perpage));
 		}
 		else if(currentFlagString.equals("most"))
 		{
 			dataSet = Util.getSoftwareWebData(this,SharedPrefsUtil.getValue(
					SoftwareThridFloorMostUp.this, "DomainName",
					WebAddress.comString)+WebAddress.thridfloor_software_listString + currentCategoryID + "&sort=2" + "&" +Util.postPerpageNum(page, perpage));
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
		MobclickAgent.onResume(this);
		Util.print("resume", "resume");
		
		try {
//			if(!isRelative)
//			    Util.setListViewHeight(SoftwareThridFloorMostUp.this, listView, displayHasUsedHeight);		
			if(secondLoading)
			{
				thridFloorAdapter.notifyDataSetChanged();
			}
			secondLoading = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}





	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Util.print("cofiguration", "con");
//		if(!isRelative)
//		    Util.setListViewHeight(SoftwareThridFloorMostUp.this, listView, displayHasUsedHeight);	
		try {
			thridFloorAdapter.changeItemNameWidthFlag(Util.checkCurrentDirection(this));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.onConfigurationChanged(newConfig);
	}


	public void setListviewOnScrollStateChange()
	{
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				try {
						if (view.getLastVisiblePosition() == thridFloorDatas.size()) {
			    				if (loaded && !loadfinish) {    	
			    					Util.print("load", "load");
			    					new Thread(new Runnable() {

										public void run() {
											try {
												loaded = false;
												Util.print("start", "start"+count);
												
												if(setAdapterData()!=null)
												{
													length = dataSet.getThreeCates().size();
										    		if(length!=0)
										    		{
										    			thridFloorDatas.addAll(dataSet.getThreeCates());				    			
										    		}
										    		else {
														loadfinish = true;
													}
										    		myHandler.sendEmptyMessage(ApplicationConstants.update);
												}
												else {
													myHandler.sendEmptyMessage(ApplicationConstants.webTimeout);
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
	

	
	public void main()
	{
//		if(Util.getNetworkState(SoftwareThridFloorActivity.this))
//		{
			main_progressbar.setVisibility(View.VISIBLE);		
			new Thread(new Runnable() {
				
				public void run() {
					try {
						
						loaded = false;
						if(setAdapterData()!=null)
						{
							 length = dataSet.getThreeCates().size();
				             if(length==0)
				             {
				            	 loadfinish = true;
				             }
				             else {	
				                 thridFloorDatas.addAll(dataSet.getThreeCates());
				                 
							 }
				             myHandler.sendEmptyMessage(ApplicationConstants.main);
						}					       	
						else
							myHandler.sendEmptyMessage(ApplicationConstants.webTimeout);
							
					 } catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
//		}
//		else {
//			networkException(getResources().getString(R.string.web_cannot_connection));
//		}
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
				Intent intent = new Intent(SoftwareThridFloorMostUp.this,InformationTabHost.class);			
				if(Util.checkCurrentSoftwareDownloading(SoftwareThridFloorMostUp.this, thridFloorDatas.get(arg2).getName()))
						intent.putExtra("downloading", true);
				intent.putExtra("swid", thridFloorDatas.get(arg2).getSwid());
				intent.putExtra("name", thridFloorDatas.get(arg2).getName());
				Util.print("categoryID", thridFloorDatas.get(arg2).getCategoryID());
				
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
	};

	
	public View getProgressView()
	{
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		return layoutInflater.inflate(R.layout.progressbar, null);
	}

	
	public void init()
	{
		
//		LinearLayout topLayout = (LinearLayout)findViewById(R.id.include_top);
//		int topheight = topLayout.getLayoutParams().height;
//		
//		
//		Util.print("displayHasUsedHeight", displayHasUsedHeight+"");
		
//        progressDialog = Util.getProgressDialog(SoftwareThridFloorActivity.this,"加载中...");
        main_progressbar = (ProgressBar)findViewById(R.id.main_progressbar);
		//初始化listview
		layoutlistView = (LinearLayout)findViewById(R.id.layout_listview);	
		listView = new ListView(SoftwareThridFloorMostUp.this);
		layoutlistView.addView(listView,ApplicationConstants.LP_FF);
		listView.setDivider(getResources().getDrawable(R.drawable.gallery_diver));
		listView.setVerticalFadingEdgeEnabled(false);
		//设置listview的高度
		//Util.setListViewHeight(SoftwareThridFloorActivity.this, listView, displayHasUsedHeight);
		
		//增加listview底部进度条
        progressView = getProgressView();

		listView.setOnItemClickListener(onItemClickListener);

	}
	  
	
}