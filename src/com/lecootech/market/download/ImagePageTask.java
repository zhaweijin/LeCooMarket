package com.lecootech.market.download;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.lecootech.market.R;
import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.data.ImageData;

/*
 * @ImageData 传入的对象数据
 * 图标异步加载的线程，且固定当前图标在确定的位置
 */
public class ImagePageTask extends AsyncTask<ImageData, Void, Bitmap> {
	private ImageView gView;
	private boolean bigOrSmall;
	private Activity activity;
	private int width;
	private Handler myhandler;
	private Bitmap bm = null;

	@Override
	protected Bitmap doInBackground(ImageData... views) {
		Bitmap bmp = null;

		ImageView view = views[0].getImageView();

		activity = views[0].getActivity();
		String picString = views[0].getPath();

		String name = views[0].getName();
		String parentname = views[0].getParentName();
		String date = views[0].getDate();
		String swid = views[0].getSwid();
		bigOrSmall = views[0].getBigOrSmall();

		try {
			bmp = Util.getWeBitmap(activity, picString, name, date, swid,
					parentname, bigOrSmall);
		} catch (Exception e) {
			if (bigOrSmall) {
				bmp = BitmapFactory.decodeResource(activity.getResources(),
						R.drawable.listview_top_image_bg);
			}else {
				bmp = BitmapFactory.decodeResource(activity.getResources(),
						R.drawable.icon2);
			}
			
		}

		this.gView = view;
		return bmp;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
        
		myhandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				bm = (Bitmap) msg.obj;
				if (SharedPrefsUtil.getValue(activity, "width", 0) == 0) {
					SharedPrefsUtil.putValue(activity, "width", width);
				}
				bm = Bitmap.createScaledBitmap(bm,
						SharedPrefsUtil.getValue(activity, "width", 0),
						SharedPrefsUtil.getValue(activity, "width", 0) / 2,
						false);
				ImagePageTask.this.gView.setImageBitmap(bm);
			}

		};
	}

	@Override
	protected void onPostExecute(final Bitmap bm) {

		if (bm != null) {
			if (bigOrSmall) {
				width = this.gView.getWidth();
				new Thread(new Runnable() {


					public void run() {
						while (true) {
							if (width > 0) {
								Message msg = myhandler.obtainMessage();
								msg.obj = bm;
								myhandler.sendMessage(msg);
								break;
							} else {
								width = ImagePageTask.this.gView.getWidth();
							}
						}
					}
				}).start();

			} else {
				this.gView.setImageBitmap(bm);
			}

		} else {
			if (bigOrSmall) {
				this.gView.setImageResource(R.drawable.listview_top_image_bg);
			}else {
				this.gView.setImageResource(R.drawable.icon2);
			}
			
		}
	}

}