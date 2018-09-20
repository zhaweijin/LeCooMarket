package com.lecootech.market.download;


import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.lecootech.market.SoftwareRecommendedActivity;
import com.lecootech.market.common.Util;
import com.lecootech.market.data.ImagesPush;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.ItemPicDatabase;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

public class AsyncImageLoader {
	private static final String TAG="AsyncImageLoader";
	private BlockingQueue queue ;  
	private HashMap<String, SoftReference<Bitmap>> imageCache;
	private ThreadPoolExecutor executor ;
	  

	
	     public AsyncImageLoader() {
	    	 imageCache = new HashMap<String, SoftReference<Bitmap>>();
	    	 
	    	 
	    	 queue = new LinkedBlockingQueue();
	    	 executor = new ThreadPoolExecutor(1, 50, 180, TimeUnit.SECONDS, queue);
	     }
	  
	     public Bitmap loadBitmap(final Activity activity, 
	    		 final ImagesPush imagesPush, final ImageCallback imageCallback) {
	         if (imageCache.containsKey(imagesPush.getSoftwareBigIcon())) {
	             SoftReference<Bitmap> softReference = imageCache.get(imagesPush.getSoftwareBigIcon());
	             Bitmap bitmap = softReference.get();
	             if (bitmap != null) {
	                 return bitmap;
	             }
	         }
	         final Handler handler = new Handler() {
	             public void handleMessage(Message message) {
	                 imageCallback.imageLoaded((Bitmap) message.obj, imagesPush.getSoftwareBigIcon());
	             }
	         };
	         
	        
	         executor.execute(new Runnable() {
	             public void run() {
	                 Bitmap bitmap = loadImageFromUrl(activity, imagesPush);
	                 imageCache.put(imagesPush.getSoftwareBigIcon(), new SoftReference<Bitmap>(bitmap));
	                 Message message = handler.obtainMessage(0, bitmap);
	                 handler.sendMessage(message);
	             }
	         });
	         
	         return null;
	     }
	  
	  
		public static synchronized Bitmap loadImageFromUrl(Activity activity, ImagesPush imagesPush) {
			Bitmap bitmap = null;
//			ItemPicDatabase itemPicDatabase = null;			
//			
//	        Cursor myCursor = null;
//	        
//	        itemPicDatabase = DatabaseManager.getItemPicDatabase(activity);
//			myCursor = itemPicDatabase.queryOneData(imagesPush.getName(), "");
//			myCursor.moveToFirst();
//			if(myCursor.getCount()>0)
//			{
//			    String filepath = myCursor.getString(myCursor.getColumnIndexOrThrow(ItemPicDatabase.KEY_BIGIMAGE_PATH));
//			    bitmap = BitmapFactory.decodeFile(filepath);
//			    myCursor.close();
//			}
//			else {
				String swid;
				if(imagesPush.getSwid()==null)
				{
					swid = "software" + "big" + imagesPush.getCategoryid();
				}
				else {
					swid = "software" + "big" + imagesPush.getSwid();
				}
				
//				Util.print("loadImageFromUrl", "loadImageFromUrl"+imagesPush.getSoftwareBigIcon());
//				Util.print("loadImageFromUrl", "loadImageFromUrl"+imagesPush.getName());
//				Util.print("loadImageFromUrl", "loadImageFromUrl"+imagesPush.getModifieddate());
//				Util.print("loadImageFromUrl", "loadImageFromUrl"+swid);
//				myCursor.close();
				
				bitmap = Util.getWeBitmap(activity, imagesPush.getSoftwareBigIcon(),
						imagesPush.getName(), imagesPush.getModifieddate(), 
						swid, "", true);
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
