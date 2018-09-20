package com.lecootech.market.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lecootech.market.common.Util;

/**
 * @author zhaweijin
 * @function图标数据库
 */
public class CollectionDatabase {

	// item_pic table
	public final static String KEY_ROWID = "_id";
	public final static String KEY_SWID = "swid";
	public final static String KEY_NAME = "name";
	public final static String KEY_SIZE = "size";
	public final static String KEY_DATE = "date";
	public final static String KEY_IMAGE_PATH = "image_path";
	public final static String KEY_DOWNLOAD_PATH = "download_path";
	public final static String KEY_PACKAGE = "package";
	public final static String KEY_VERSION = "version";

	private SQLiteDatabase db;
	private final static String DATABASE_NAME = "collection";
	private final static String TABLE_NAME = "collection";
	private final static String CREATE_DATABASE = "create table if not exists "
			+ TABLE_NAME + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_NAME
			+ " text not null, " + KEY_SWID + " text, " + KEY_SIZE + " text, "
			+ KEY_DATE + " text, " + KEY_IMAGE_PATH + " text, "
			+ KEY_DOWNLOAD_PATH + " text, " 
			+ KEY_VERSION + " text, " 
			+ KEY_PACKAGE + " text)";

	Context mContext;

	public CollectionDatabase(Context c) {
		this.mContext = c;
		db = mContext.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
				null);
		try {
			db.execSQL(CREATE_DATABASE);
			Util.print("collection", "Create picTable note ok");
		} catch (Exception e) {
			Util.print("talbe already", "table already");
		}

	}

	public long insertData(String swid, String name, String imagepath,
			String date, String size, String downpath, String packagename,String version) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_NAME, name);
		contentValues.put(KEY_SWID, swid);
		contentValues.put(KEY_SIZE, size);
		contentValues.put(KEY_DATE, date);
		contentValues.put(KEY_IMAGE_PATH, imagepath);
		contentValues.put(KEY_DOWNLOAD_PATH, downpath);
		contentValues.put(KEY_PACKAGE, packagename);
		contentValues.put(KEY_VERSION, version);

		return db.insert(TABLE_NAME, null, contentValues);
	}

	public void deleteData(long row_id) {
		String sqlString = "delete from collection where _id =?";
		db.execSQL(sqlString, new Long[] { row_id });
	}

	public void deleteDataByName(String name) {
		String sqlString = "delete from collection where name =?";
		db.execSQL(sqlString, new String[] { name });
	}

	public Cursor queryAllData() {
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select * from collection", null);
		} catch (Exception e) {
			cursor.close();
			e.printStackTrace();
		}
		return cursor;
	}

	public Cursor queryOneData(String name) {
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select * from collection where name =?",
					new String[] { name });
		} catch (Exception e) {
			cursor.close();
			e.printStackTrace();
		}

		return cursor;
	}

	// public void close()
	// {
	// db.close();
	// }

	public boolean isOpening() {
		return db.isOpen();
	}

	
	public void addVersionColumn()
	{
		
		String sqlString = "alter table collection add version text";
		db.execSQL(sqlString);
	}
}
