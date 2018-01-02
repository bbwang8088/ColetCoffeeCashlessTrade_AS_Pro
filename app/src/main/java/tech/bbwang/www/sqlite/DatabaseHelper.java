package tech.bbwang.www.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite操作基础类
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "COLET_COFFEE.db";
	private static final int version = 1;

	public DatabaseHelper(Context context) {

		super(context, DB_NAME, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}

}
