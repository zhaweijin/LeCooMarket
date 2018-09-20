package com.lecootech.market.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lecootech.market.common.Util;

/**
 * @author zhaweijin
 * @function 下载数据库
 */
public class DownloadDatabase {

	public final static String KEY_NAME = "name";
	public final static String KEY_ROWID = "_id";
	public final static String KEY_DOWNLOND_NUM = "downloadnum";
	public final static String KEY_ICON_PATH = "iconpath";
	public final static String KEY_PACKAGE_NAME = "package";
	public final static String KEY_INSTALL = "installflag";
	public final static String KEY_WEBPATH = "webpath";
	public final static String KEY_FILEPATH = "filepath";
	public final static String KEY_DOWNLOAD_LENGTH = "downloadlength";
	public final static String KEY_OPERATION = "operation";
	public final static String KEY_SIZE = "size";

	private SQLiteDatabase db;
	private final static String DATABASE_NAME = "downloads";
	private final static String TABLE_NAME = "download";

	private final static String CREATE_DATABASE = "create table if not exists "
			+ TABLE_NAME + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_NAME
			+ " text not null, " + KEY_ICON_PATH + " text, " + KEY_PACKAGE_NAME
			+ " text, " + KEY_INSTALL + " integer, " + KEY_DOWNLOND_NUM
			+ " integer, " + KEY_WEBPATH + " text, " + KEY_FILEPATH + " text, "
			+ KEY_SIZE + " text, " + KEY_DOWNLOAD_LENGTH + " integer, "
			+ KEY_OPERATION + " integer);";

	Context mContext;

	public DownloadDatabase(Context c) {
		this.mContext = c;
		db = mContext.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
				null);
		try {
			db.execSQL(CREATE_DATABASE);
			Util.print("download database", "Create mydownloadTable note ok");
		} catch (Exception e) {
		}
	}

	public long insertData(String name, String iconpath, String packagename,
			int install, int num, String webpath, String filepath,
			int downloadlength, int operation, String size) {

		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_NAME, name);
		contentValues.put(KEY_ICON_PATH, iconpath);
		contentValues.put(KEY_PACKAGE_NAME, packagename);
		contentValues.put(KEY_INSTALL, install);
		contentValues.put(KEY_DOWNLOND_NUM, install);
		contentValues.put(KEY_WEBPATH, webpath);
		contentValues.put(KEY_FILEPATH, filepath);
		contentValues.put(KEY_DOWNLOAD_LENGTH, downloadlength);
		contentValues.put(KEY_OPERATION, operation);
		contentValues.put(KEY_SIZE, size);

		db.insert(TABLE_NAME, null, contentValues);

		return 0;
	}

	public long updateData(String name, String iconpath, String packagename,
			int install, int num, String webpath, String filepath,
			int downloadlength, int operation, String size) {

		String queryString = KEY_NAME + "='" + name + "'";

		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_NAME, name);
		contentValues.put(KEY_ICON_PATH, iconpath);
		contentValues.put(KEY_PACKAGE_NAME, packagename);
		contentValues.put(KEY_INSTALL, install);
		contentValues.put(KEY_DOWNLOND_NUM, install);
		contentValues.put(KEY_WEBPATH, webpath);
		contentValues.put(KEY_FILEPATH, filepath);
		contentValues.put(KEY_DOWNLOAD_LENGTH, downloadlength);
		contentValues.put(KEY_OPERATION, operation);
		contentValues.put(KEY_SIZE, size);

		db.update(TABLE_NAME, contentValues, queryString, null);

		return 0;
	}

	public void updateInstallValue(long row_id, int value) {

		String where = "update download set installflag = " + value
				+ " where _id = " + row_id;
		db.execSQL(where);

	}

	public boolean deleteData(String name) {

		String delete = KEY_NAME + " ='" + name + "'";
		boolean result = db.delete(TABLE_NAME, delete, null) > 0;

		return result;
	}

	public boolean deleteByPackageData(String name) {

		String delete = KEY_PACKAGE_NAME + " ='" + name + "'";
		boolean result = db.delete(TABLE_NAME, delete, null) > 0;

		return result;
	}

	public Cursor queryAllData() {

		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select * from download", null);
		} catch (Exception e) {
			if (cursor != null) {
				cursor.close();
			}
			
			e.printStackTrace();
		}
		return cursor;
	}

	public Cursor queryByPackage(String name) {

		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select * from download where package =?",
					new String[] { name });
		} catch (Exception e) {
			cursor.close();
			e.printStackTrace();
		}

		return cursor;
	}

	public Cursor queryID(String name) {

		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select * from download where name =?",
					new String[] { name });
		} catch (Exception e) {
			cursor.close();
			e.printStackTrace();
		}
		return cursor;
	}

	public void close() {
		db.close();
	}

	public Integer getDownloadNum(String software_name) {

		Cursor temCursor = null;
		int returnvalue = 0;
		int size;

		temCursor = queryID(software_name);
		size = temCursor.getCount();
		temCursor.moveToFirst();
		if (size != 0) {
			returnvalue = temCursor.getInt(temCursor
					.getColumnIndexOrThrow(DownloadDatabase.KEY_DOWNLOND_NUM));
		}

		temCursor.close();

		return returnvalue;

	}

	public void updateDownloadNum(String name, int num) {

		String sqlString = "update download set downloadnum = ? where name=?";
		db.execSQL(sqlString, new Object[] { num, name });

	}

	public void updateDownloadLength(String name, int length) {

		String sqlString = "update download set downloadlength = ? where name=?";
		db.execSQL(sqlString, new Object[] { length, name });

	}

	public void updateAll(String name, long length, int num) {

		String sqlString = "update download set downloadlength = ?, downloadnum = ? where name=?";
		db.execSQL(sqlString, new Object[] { length, num, name });

	}

	public int getUpdateDownloadLength(String name) {

		Cursor cursor = queryID(name);
		cursor.moveToFirst();
		int length = 0;
		if (cursor.getCount() > 0) {
			length = cursor
					.getInt(cursor
							.getColumnIndexOrThrow(DownloadDatabase.KEY_DOWNLOAD_LENGTH));
		}
		cursor.close();

		return length;
	}

	public void updateDownloadInstallFlag(String name, int flag) {

		String sqlString = "update download set installflag = ? where name=?";
		db.execSQL(sqlString, new Object[] { flag, name });

	}

	public void updateDownloadInstallFlagByPackage(String packagename, int flag) {

		String sqlString = "update download set installflag = ? where package=?";
		db.execSQL(sqlString, new Object[] { flag, packagename });

	}

	public String getDownloadUrl(String name) {

		Cursor cursor = queryID(name);
		cursor.moveToFirst();
		String downloadUrl = "";
		if (cursor.getCount() > 0) {
			downloadUrl = cursor.getString(cursor
					.getColumnIndexOrThrow(DownloadDatabase.KEY_WEBPATH));
		}
		cursor.close();

		return downloadUrl;
	}

	public void updateDownloadOperation(String name, int operation) {

		String sqlString = "update download set operation = ? where name=?";
		db.execSQL(sqlString, new Object[] { operation, name });

	}

	public int getDownloadOperation(String name) {
		Cursor cursor = queryID(name);
		cursor.moveToFirst();
		int operation = 0;
		if (cursor.getCount() > 0) {
			operation = cursor.getInt(cursor
					.getColumnIndexOrThrow(DownloadDatabase.KEY_OPERATION));
		} else {
			operation = -2;
		}
		cursor.close();

		return operation;
	}

	public void deleteTable() {
		try {
			String sqlString = "drop table " + TABLE_NAME;
			Util.print("sql", sqlString);
			db.execSQL(sqlString);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
