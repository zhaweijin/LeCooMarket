package com.lecootech.market.database;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lecootech.market.common.Util;

/**
 * @author zhaweijin
 * @function截图数据库
 */
public class GalleryDatabase {

	public final static String KEY_NAME = "name";
	public final static String KEY_ROWID = "_id";
	public final static String KEY_PATH = "path";
	public final static String KEY_WEBPATH = "webpath";
	public final static String KEY_IMAGEID = "imageid";

	private SQLiteDatabase db;
	private final static String DATABASE_NAME = "gallery";
	private final static String TABLE_NAME = "gallery";
	private final static String CREATE_DATABASE = "create table if not exists "
			+ TABLE_NAME + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_NAME
			+ " text not null, " + KEY_PATH + " text, " + KEY_IMAGEID
			+ " text, " + KEY_WEBPATH + " text)";

	Context mContext;

	public GalleryDatabase(Context c) {
		
		this.mContext = c;
		db = mContext.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
				null);
		try {
			db.execSQL(CREATE_DATABASE);
			Util.print("gallery", "Create gallerytable note ok");
		} catch (Exception e) {
			Util.print("table already", " table already");
		}
	}

	public long insertData(String name, String path, String imageid,
			String webpath) {
        
		ContentValues contentValues = new ContentValues();

		contentValues.put(KEY_NAME, name);
		contentValues.put(KEY_PATH, path);
		contentValues.put(KEY_IMAGEID, imageid);
		contentValues.put(KEY_WEBPATH, webpath);
		db.insert(TABLE_NAME, null, contentValues);

		return 0;

	}

	public long updateData(String name, String path, String imageid,
			String webpath) {
		
		String queryString = KEY_NAME + "='" + name + "'";
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_NAME, name);
		contentValues.put(KEY_PATH, path);
		contentValues.put(KEY_IMAGEID, imageid);
		contentValues.put(KEY_WEBPATH, webpath);
		db.update(TABLE_NAME, contentValues, queryString, null);
		
		return 0;
	}

	public void deleteData(long row_id) {
		
		
		String sqlString = "delete from gallery where _id =?";
		db.execSQL(sqlString, new Long[] { row_id });
		

	}

	public void deleteMulData(String name) {
		
		
		Cursor cursor = queryOneData(name);
		cursor.moveToFirst();
		int size = cursor.getCount();
		File file;
		for (int i = 0; i < size; i++) {
			long id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ROWID));
			String filepath = cursor.getString(cursor
					.getColumnIndexOrThrow(GalleryDatabase.KEY_PATH));
			file = new File(filepath);
			if (file.exists())
				file.delete();
			deleteData(id);
			cursor.moveToNext();
		}
		cursor.close();

		// db.execSQL("delete from gallery where name =?",new String[]{name});
	}

	public Cursor queryAllData() {

		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select * from gallery", null);
		} catch (Exception e) {
			cursor.close();
			e.printStackTrace();
		}
		return cursor;
	}

	public Cursor queryOneData(String name) {
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select * from gallery where name =?",
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
}
