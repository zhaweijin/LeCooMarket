package com.lecootech.market.download;



import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.lecootech.market.SoftwareDownloadActivity;
import com.lecootech.market.common.Util;
import com.lecootech.market.data.ImageData;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.DownloadDatabase;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;


public class AsyncDownloadLoader {

	private BlockingQueue queue ;   
	private ThreadPoolExecutor executor ;
	private DownloadDatabase downloadDatabase;
	private SharedPreferences settingPreferences;
	private HashMap<String, SoftReference<String>> progressCache; 
	
	private Context context;

	public AsyncDownloadLoader(Context context) {
	    	 progressCache = new HashMap<String, SoftReference<String>>();
	    	 queue = new LinkedBlockingQueue();
	    	 this.context = context;
	    	 settingPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	    	 executor = new ThreadPoolExecutor(20, 50, 180, TimeUnit.SECONDS, queue);
   }
	  
   public String loadProgress(final Activity activity, final ImageData imageData, final progressCallback progressCallback) {
	         
	    	 
	    	 if (progressCache.containsKey(imageData.getSwid())) {
	             SoftReference<String> softReference = progressCache.get(imageData.getSwid());
	             String progress = softReference.get();
	             if (progress != null) {
	                 return progress;
	             } 
	         }
	    	 
	         final Handler handler = new Handler() {
	             public void handleMessage(Message message) {
	            	 progressCallback.progressLoaded((String) message.obj, imageData.getSwid());
	             }
	         };
	         

	         executor.execute(new Runnable() {
	             public void run() {
						
	            	 try {
					
						while (!activity.isFinishing()) {
							if (checkIsBreak(imageData.getSwid())) {
								break;
							}
                            
							Integer value = getDownloadNum(imageData);
//							Util.print("asydownload", value+"");
							if (value == -1) {
								break;
							} else {
								if (value != 100) {
									Thread.sleep(1000);
								}
								if (value != -1) {
									progressCache.put(imageData.getSwid(),new SoftReference<String>(value+""));
									Message message = handler.obtainMessage(0,value+"");
									handler.sendMessage(message);
								} else {
									break;
								}
							}

							if (value == 100) {
								break;
							}
						}
						
						if(getOperation(imageData.getName())==-3)
						{
							progressCache.put(imageData.getSwid(),new SoftReference<String>("继续"));
							Message message = handler.obtainMessage(0,"继续");
							handler.sendMessage(message);
						}
						
						Util.print("progress threadbreak", "progress threadbreak");
					    progressCache.clear();
	             } catch (Exception e) {
						e.printStackTrace();
				}
	           }
	         });

	         return null;
	     }
	  
	 	public boolean checkIsBreak(String swid) {
			boolean returnvalue = false;
			swid = swid.substring(swid.indexOf("software")+8);
			
			if(!settingPreferences.getBoolean(swid, false))
			{
				returnvalue = true;
			}
//			Util.print("swid--->", swid+Boolean.toString(returnvalue));
			return returnvalue;
		}
	 	
	 	
	 	public Integer getDownloadNum(ImageData imageData) {

	 		Cursor temCursor = null;
	 		int returnvalue = -1;
	 		int size;
	 		String software_name = imageData.getName();
            downloadDatabase = DatabaseManager.getDownloadDatabase(context);


            
	 		temCursor = downloadDatabase.queryID(software_name);
	 		size = temCursor.getCount();
	 		temCursor.moveToFirst();
	 		if (size != 0) {
	 			returnvalue = temCursor
	 						.getInt(temCursor
	 								.getColumnIndexOrThrow(DownloadDatabase.KEY_DOWNLOND_NUM));
	 		}
	 		
	 		temCursor.close();
	 		return returnvalue;

	 	}

	  
	     public interface progressCallback {
	         public void progressLoaded(String progress, String imageUrl);
	     }

	 	public int getOperation(String name) {
			int operation = -1;
			operation = downloadDatabase.getDownloadOperation(name);
			return operation;
		}

}
