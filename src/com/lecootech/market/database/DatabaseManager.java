package com.lecootech.market.database;

import android.content.Context;

/**
 * @author zhaweijin 数据库管理类
 */
public class DatabaseManager {

	private static DownloadDatabase downloadDatabase;
	private static ItemPicDatabase itemPicDatabase;
	private static GalleryDatabase galleryDatabase;
	private static CollectionDatabase collectionDatabase;
	private static UpdateManagerDatabase updateManagerDatabase;

	public DatabaseManager() {

	}

	public static DownloadDatabase getDownloadDatabase(Context context) {
		if (downloadDatabase == null) {
			downloadDatabase = new DownloadDatabase(context);
		}
		return downloadDatabase;
	}

	public static void setDownloadDatabase() {
		downloadDatabase = null;
	}

	public static void setCollectionDatabase()
	{
		collectionDatabase = null;
	}
	public static ItemPicDatabase getItemPicDatabase(Context context) {
		if (itemPicDatabase == null) {
			itemPicDatabase = new ItemPicDatabase(context);
		}
		return itemPicDatabase;
	}

	public static GalleryDatabase getGalleryDatabase(Context context) {
		if (galleryDatabase == null) {
			galleryDatabase = new GalleryDatabase(context);
		}
		return galleryDatabase;
	}

	public static CollectionDatabase getCollectionDatabase(Context context) {
		if (collectionDatabase == null) {
			collectionDatabase = new CollectionDatabase(context);
		}
		return collectionDatabase;
	}

	public static UpdateManagerDatabase getUpdateManagerDatabase(Context context) {
		if (updateManagerDatabase == null) {
			updateManagerDatabase = new UpdateManagerDatabase(context);
		}
		return updateManagerDatabase;
	}
	
}
