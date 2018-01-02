package tech.bbwang.www.sqlite;

//import java.text.ParseException;
//import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tech.bbwang.www.activity.ColetApplication;
//import tech.bbwang.www.util.DateUtil;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 储值卡数据库操作类
 * 
 * @author bbwang8088@126.com
 * 
 */
public class PrePayCardDAO implements IDAO {

	public static String ID_PRIMARY = "id";

	public static String TABLE_PREPAYCARD = "PrePayCard";

	public static String ELEM_INDEX = "sequenceNo";
	public static String ELEM_VERSION_CODE = "version_code";
	public static String ELEM_LIST_PRICE = "list_price";
	public static String ELEM_FRESH_PRICE = "fresh_price";
	public static String ELEM_VIP_PRICE = "vip_price";
	public static String ELEM_TITLE = "title";

	private SQLiteDatabase db = null;

	public PrePayCardDAO(SQLiteDatabase db) {

		this.db = db;
	}

	/**
	 * 创建储值卡数据表
	 * 
	 * @return
	 */
	public boolean create() {

		boolean isSuccess = false;
		String createTable = "";
		try {
			db.beginTransaction();
			createTable = "create table " + TABLE_PREPAYCARD + " ( "
					+ ID_PRIMARY + " integer primary key autoincrement, "
					+ ELEM_INDEX + " integer , " + ELEM_VERSION_CODE
					+ " varchar , " + ELEM_LIST_PRICE + " varchar , "
					+ ELEM_FRESH_PRICE + " varchar , " + ELEM_VIP_PRICE
					+ " varchar , " + ELEM_TITLE + " varchar );";
			Log.d("DEMO", createTable);
			db.execSQL(createTable);
			db.setTransactionSuccessful();
			isSuccess = true;
		} catch (SQLException e) {
			ColetApplication.getApp().logError(e.getMessage());
			isSuccess = false;
		} finally {
			// 结束事务
			db.endTransaction();
		}

		return isSuccess;
	}

	/**
	 * 保存一条储值卡信息
	 * 
	 * @param d
	 * @return
	 */
	@Override
	public boolean insert(Object data) {

		boolean ret = false;
		String sql = "";
		db.beginTransaction();

		ContentValues values = new ContentValues();

		try {
			if( data instanceof List<?>){
				for (PrePayCard card : (List<PrePayCard>)data) {
					values.clear();
					values.put(ELEM_INDEX, card.getIndex());
					values.put(ELEM_VERSION_CODE, card.getVersionCode());
					values.put(ELEM_LIST_PRICE, card.getList_price());
					values.put(ELEM_FRESH_PRICE, card.getFresh_price());
					values.put(ELEM_VIP_PRICE, card.getVip_price());
					values.put(ELEM_TITLE, card.getTitle());
					db.insert(TABLE_PREPAYCARD, null, values);
				}
			}

			Log.d("DEMO", sql);
			// 设置事务标志为成功，当结束事务时就会提交事务
			db.setTransactionSuccessful();
			ret = true;
		} catch (Exception e) {
			ColetApplication.getApp().logError(e.getMessage());
		} finally {
			// 结束事务
			db.endTransaction();
		}

		return ret;
	}

	@Override
	public Object get(Map<String, String> params) {

		PrePayCardList ret = new PrePayCardList();
		String sql = "select * from " + TABLE_PREPAYCARD + "  where ";
		String where = "";

		int i = params.size();
		int count = 0;
		for (String key : params.keySet()) {

			if (key.equals("") || params.get(key).equals("")) {
				continue;
			}
			if (count > 0 && count < i) {
				where += " and ";
			}
			where = " " + key + "='" + params.get(key) + "';";
			count++;
		}

		sql += where;

		ColetApplication.getApp().logDebug(sql);
		Cursor c = db.rawQuery(sql + where, null);
		while (c.moveToNext()) {
			// String versionCode, String title, String list_price, String
			// fresh_price, String vip_price, String index
			PrePayCard card = new PrePayCard(c.getString(2), c.getString(6),
					c.getString(3), c.getString(4), c.getString(5), c.getInt(1));
			ret.addPrePayCard(card);
			;
		}
		c.close();

		return ret;

	}

	@Override
	public List<Object> getAll() {

		return null;
	
	}

	@Override
	public List<Object> getAll(Map<String, String> params) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 保存一组储值卡
	 */
	@Override
	public boolean insert(List<Object> data) {

		boolean ret = false;
		String sql = "";
		db.beginTransaction();

		ContentValues values = new ContentValues();

		try {
			for (Object c : data) {
				PrePayCard card = (PrePayCard) c;
				values.clear();
				values.put(ELEM_INDEX, card.getIndex());
				values.put(ELEM_VERSION_CODE, card.getVersionCode());
				values.put(ELEM_LIST_PRICE, card.getList_price());
				values.put(ELEM_FRESH_PRICE, card.getFresh_price());
				values.put(ELEM_VIP_PRICE, card.getVip_price());
				values.put(ELEM_TITLE, card.getTitle());
				db.insert(TABLE_PREPAYCARD, null, values);
			}

			Log.d("DEMO", sql);
			// 设置事务标志为成功，当结束事务时就会提交事务
			db.setTransactionSuccessful();
			ret = true;
		} catch (Exception e) {
			ColetApplication.getApp().logError(e.getMessage());
		} finally {
			// 结束事务
			db.endTransaction();
		}

		return ret;

	}

	@Override
	public int delete(Map<String, String> params) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Map<String, String> setParams,
			Map<String, String> whereParams) {
		// TODO Auto-generated method stub
		return 0;
	}

}
