package com.lecootech.market.download;



import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.lecootech.market.common.Util;
import com.lecootech.market.data.ImageData;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.ItemPicDatabase;
import com.lecootech.market.download.AsyncImageLoader.ImageCallback;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;


public class IconAsyncImageLoader {

	private BlockingQueue queue ;   
	private ThreadPoolExecutor executor ;
	private HashMap<String, SoftReference<Bitmap>> imageCache; 
	     public IconAsyncImageLoader() {
	    	 imageCache = new HashMap<String, SoftReference<Bitmap>>();
	    	 queue = new LinkedBlockingQueue();
	    	 executor = new ThreadPoolExecutor(1, 50, 180, TimeUnit.SECONDS, queue);
	     }
	  
	     public Bitmap loadBitmap(final Activity activity, final ImageData imageData, final ImageCallback imageCallback) {
	         
	    	 
	    	 if (imageCache.containsKey(imageData.getPath())) {
	             SoftReference<Bitmap> softReference = imageCache.get(imageData.getPath());
	             Bitmap bitmap = softReference.get();
	             if (bitmap != null) {
	                 return bitmap;
	             }
	         }
	    	 
	         final Handler handler = new Handler() {
	             public void handleMessage(Message message) {
	                 imageCallback.imageLoaded((Bitmap) message.obj, imageData.getPath());
	             }
	         };
	         

	         executor.execute(new Runnable() {
	             public void run() {
	            	 Bitmap bitmap = loadImageFromUrl(activity, imageData);
	            	 imageCache.put(imageData.getPath(), new SoftReference<Bitmap>(bitmap));
	                 Message message = handler.obtainMessage(0, bitmap);
	                 handler.sendMessage(message);
	             }
	         });
	         
	         return null;
	     }
	  

		public synchronized Bitmap loadImageFromUrl(Activity activity, ImageData imageData) {
			Bitmap bitmap = null;  
	        if(imageData == null )  
	            return null;   
	              
//            ItemPicDatabase itemPicDatabase = null;			
//	        Cursor myCursor = null;
//	        
//	        itemPicDatabase = DatabaseManager.getItemPicDatabase(activity);
//			myCursor = itemPicDatabase.queryOneData(imageData.getName(), "");
//			myCursor.moveToFirst();
////			Util.print("icon=name", imageData.getName()+myCursor.getCount());
//			if(myCursor.getCount()>0)
//			{
//			    String filepath = myCursor.getString(myCursor.getColumnIndexOrThrow(ItemPicDatabase.KEY_PATH));
//			    bitmap = BitmapFactory.decodeFile(filepath);
//			    myCursor.close();
//			}
//			else {
				
//				Util.print("loadImageFromUrl", "loadImageFromUrl"+imageData.getName());
//				Util.print("loadImageFromUrl", "loadImageFromUrl"+imageData.getDate());
//				Util.print("loadImageFromUrl", "loadImageFromUrl"+imageData.getSwid());
//				Util.print("loadImageFromUrl", "loadImageFromUrl"+imageData.getBigOrSmall());
//				myCursor.close();
				if(activity==null)
				{
					Util.print("icon null", "icon null");
				}
				bitmap = Util.getWeBitmap(activity, imageData.getPath(),
						imageData.getName(), imageData.getDate(), 
						imageData.getSwid(), imageData.getParentName(), false);
//			}
//			
//			if(!myCursor.isClosed())
//			   myCursor.close();

	  
	        return bitmap ;
		}
	  
	     public interface ImageCallback {
	         public void imageLoaded(Bitmap imageBitmap, String imageUrl);
	     }

	

}
