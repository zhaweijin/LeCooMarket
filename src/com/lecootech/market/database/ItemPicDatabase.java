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
public class ItemPicDatabase {

	// item_pic table
	public final static String KEY_ROWID = "_id";
	public final static String KEY_NAME = "name";
	public final static String KEY_PARENT_NAME = "parentname";
	public final static String KEY_PATH = "path";
	public final static String KEY_DATE = "date";
	public final static String KEY_BIGIMAGE_PATH = "bigpath";

	private SQLiteDatabase db;
	private final static String DATABASE_NAME = "item_pic";
	private final static String TABLE_NAME = "item_pic";
	private final static String CREATE_DATABASE = "create table if not exists "
			+ TABLE_NAME + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_NAME
			+ " text not null, " + KEY_PARENT_NAME + " text, " + KEY_PATH
			+ " text, " + KEY_BIGIMAGE_PATH + " text, " + KEY_DATE + " text)";

	Context mContext;

	public ItemPicDatabase(Context c) {
		this.mContext = c;
		db = mContext.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
				null);
		try {
			db.execSQL(CREATE_DATABASE);
			Util.print("itempic", "Create picTable note ok");
		} catch (Exception e) {
			Util.print("talbe already", "table already");
		}

	}

	public long insertData(String name, String parentname, String path,
			String date, String bigpath) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(KEY_NAME, name);
			contentValues.put(KEY_PARENT_NAME, parentname);
			contentValues.put(KEY_PATH, path);
			contentValues.put(KEY_DATE, date);
			contentValues.put(KEY_BIGIMAGE_PATH, bigpath);

			db.insert(TABLE_NAME, null, contentValues);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
//Util.print("insert", "insert");

		
		return 0;

	}

	public void updateDataIconPath(String name, String parentname, String path,
			String date) {

		String where = "update item_pic set path = ? ,date = ? where name = ? and parentname = ?";
		db.execSQL(where, new String[] { path, date, name, parentname });

		

	}

	public void updateDataBigPath(String name, String parentname, String date,
			String bigpath) {

		String where = "update item_pic set bigpath = ? ,date = ? where name = ? and parentname = ?";
		db.execSQL(where, new String[] { bigpath, date, name, parentname });

		

	}

	public void deleteData(long row_id) {

		String sqlString = "delete from item_pic where _id =?";
		db.execSQL(sqlString, new Long[] { row_id });

		

	}

	public void deleteDataByPicpath(String picpath) {

		String sqlString = "delete from item_pic where path =?";
		db.execSQL(sqlString, new String[] { picpath });
		
	}

	public void deleteData(String name, String parentname) {

		String sqlString = "delete from item_pic where name =? and parentname =?";
		db.execSQL(sqlString, new String[] { name, parentname });


	}

	public Cursor queryAllData() {

		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select * from item_pic", null);
		} catch (Exception e) {
			cursor.close();
			e.printStackTrace();
		}

		return cursor;
	}

	public Cursor queryOneData(String name, String parentname) {
		

		Cursor cursor = null;
		try {
			cursor = db.rawQuery(
					"select * from item_pic where name =? and parentname=?",
					new String[] { name, parentname });
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

	public void deleteMulNoSDcardData() {
		// Cursor cursor = queryAllData();
		// cursor.moveToFirst();
		// int size = cursor.getCount();
		//
		// for(int i=0;i<size;i++)
		// {
		// String date =
		// cursor.getString(cursor.getColumnIndexOrThrow(ItemPicDatabase.KEY_DATE));
		// if(date.equals("nosdcard"))
		// {
		// Long id =
		// cursor.getLong(cursor.getColumnIndexOrThrow(ItemPicDatabase.KEY_ROWID));
		// deleteData(id);
		// }
		// cursor.moveToNext();
		// }
		// cursor.close();
		String sqlString = "delete from item_pic where date =?";
		db.execSQL(sqlString, new String[] { "nosdcard" });
	}

}
