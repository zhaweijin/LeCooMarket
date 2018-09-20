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
public class UpdateManagerDatabase {

	// item_pic table
	public final static String KEY_ROWID = "_id";
	public final static String KEY_PACKAGE = "package";

	private SQLiteDatabase db;
	private final static String DATABASE_NAME = "updateManager";
	private final static String TABLE_NAME = "updateManager";
	private final static String CREATE_DATABASE = "create table if not exists "
			+ TABLE_NAME + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_PACKAGE + " text)";

	Context mContext;

	public UpdateManagerDatabase(Context c) {
		this.mContext = c;
		db = mContext.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
				null);
		try {
			db.execSQL(CREATE_DATABASE);
			Util.print("updateManager", "Create picTable note ok");
		} catch (Exception e) {
			Util.print("talbe already", "table already");
		}
	}

	public long insertData(String packagename) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_PACKAGE, packagename);

		return db.insert(TABLE_NAME, null, contentValues);
	}

	public void deleteData(long row_id) {
		String sqlString = "delete from updateManager where _id =?";
		db.execSQL(sqlString, new Long[] { row_id });
	}

	public void deleteDataByName(String packageName) {
		String sqlString = "delete from updateManager where name =?";
		db.execSQL(sqlString, new String[] { packageName });
	}

	public Cursor queryAllData() {
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select * from updateManager", null);
		} catch (Exception e) {
			cursor.close();
			e.printStackTrace();
		}
		return cursor;
	}

	public Cursor queryOneData(String packageName) {
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(
					"select * from updateManager where package =?",
					new String[] { packageName });
		} catch (Exception e) {
			cursor.close();
			e.printStackTrace();
		}

		return cursor;
	}

	public boolean isOpening() {
		return db.isOpen();
	}

}
