package com.lecootech.market.download;

import java.io.File;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.lecootech.market.R;
import com.lecootech.market.common.Util;
import com.lecootech.market.data.ImageData;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.GalleryDatabase;

/*
 * @ImageData 传入的对象数据
 * 图标异步加载的线程，且固定当前图标在确定的位置
 */
public class GalleryImagePageTask extends AsyncTask<ImageData, Void, Bitmap> {
	private ImageView gView;
	private String oneGalleryFilepath = "";
	private GalleryDatabase galleryDatabase;

	@Override
	protected Bitmap doInBackground(ImageData... views) {
		Bitmap bmp = null;

		ImageView view = views[0].getImageView();

		Activity activity = views[0].getActivity();
		String picString = views[0].getPath();

		String name = views[0].getName();
		String swid = views[0].getSwid();

		try {
			Util.print("picpath", picString);
			loadingGalleryIcon(activity, name, swid, picString);
			bmp = BitmapFactory.decodeFile(oneGalleryFilepath);
		} catch (Exception e) {
			bmp = BitmapFactory.decodeResource(activity.getResources(),
					R.drawable.icon2);
		}

		this.gView = view;
		return bmp;
	}

	@Override
	protected void onPostExecute(Bitmap bm) {

		if (bm != null) {
			// CommonUtil.print("bm", "bm");
			this.gView.setImageBitmap(bm);
			// this.gView = null;
		} else {
			this.gView.setImageResource(R.drawable.icon2);
		}
	}

	/*
	 * @software_name 软件名称 加载左上角的图标
	 */
	public void loadingGalleryIcon(Activity activity, String software_name,
			String swid, String picString) {
		galleryDatabase = DatabaseManager.getGalleryDatabase(activity);

		final String temppicpath = Util.getStorePicPath();

		Util.print("gallery--", software_name);
		Cursor cursor = galleryDatabase.queryOneData(software_name);
		int cursorcount = cursor.getCount();
		cursor.moveToFirst();
		Util.print("gallerylength", "" + cursorcount);

		// String picString = dataSet.getImageArrays().get(0);
		String tempdot = swid + "gallery" + 1;

		if (Util.avaiableMedia()) {
			// String filepath = temppicpath + tempdot;

			oneGalleryFilepath = Util.getStorePicPath() + tempdot;
			if (cursorcount != 0) {
				String tempwebpath = cursor.getString(cursor
						.getColumnIndexOrThrow(GalleryDatabase.KEY_WEBPATH));
				if (!tempwebpath.equals(picString)) {
					Util.print("new download", "new download");
					Util.getSoftwareImage(activity, software_name, picString,
							tempdot, true, temppicpath); // create tempPicFile
					galleryDatabase.updateData(software_name,
							oneGalleryFilepath, "1", picString);
				} else {
					Util.print("get filepath from database",
							"get filepath from database");
					oneGalleryFilepath = cursor.getString(cursor
							.getColumnIndexOrThrow(GalleryDatabase.KEY_PATH));
					File file = new File(oneGalleryFilepath);
					if (!file.exists()) {
						Util.print("file deleteed", "file deleteed");
						Util.getSoftwareImage(activity, software_name,
								picString, tempdot, true, temppicpath); // create
																		// tempPicFile
						galleryDatabase.insertData(software_name,
								oneGalleryFilepath, "1", picString);
					}
				}
			} else {
				Util.getSoftwareImage(activity, software_name, picString,
						tempdot, true, temppicpath); // create tempPicFile
				galleryDatabase.insertData(software_name, oneGalleryFilepath,
						"1", picString);
			}
		} else {
			Util.getSoftwareImage(activity, software_name, picString, tempdot,
					false, temppicpath); // create tempPicFile
			oneGalleryFilepath = Util.getStorePicPath() + tempdot;
		}
		cursor.close();
	}
}