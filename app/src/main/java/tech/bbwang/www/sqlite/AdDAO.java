package tech.bbwang.www.sqlite;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import tech.bbwang.www.activity.ColetApplication;
import tech.bbwang.www.util.DateUtil;
import tech.bbwang.www.ws.ADImage;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 数据库操作类
 * 
 * @author bbwang8088@126.com
 * 
 */
public class AdDAO implements IDAO {
	public static String ID_PRIMARY = "id";

	public static String TABLE_AD = "Ad";
	public static String AD_NAME = "name";
	public static String AD_VERSION = "version";

	public static String TABLE_ADELEM = "AdElem";
	public static String ADELEM_ADVER = "adver";
	public static String ADELEM_WIDTH = "width";
	public static String ADELEM_HEIGHT = "height";
	public static String ADELEM_SEQNO = "seqno";
	public static String ADELEM_SIZE = "size";
	public static String ADELEM_DESC = "description";
	public static String ADELEM_TYPE = "type";
	public static String ADELEM_URL = "url";
	public static String CREATE_TIME = "create_time";
	private SQLiteDatabase db = null;

	public AdDAO(SQLiteDatabase db) {

		this.db = db;
	}

	/**
	 * 创建数据表
	 * 
	 * @return
	 */
	public boolean create() {

		boolean isSuccess = false;
		String createTable = "";
		try {
			db.beginTransaction();
			createTable = "create table " + TABLE_AD + " (" + ID_PRIMARY + " integer primary key autoincrement, " + AD_VERSION + " varchar , "
					+ AD_NAME + " varchar, "+ CREATE_TIME +" varchar );";
			db.execSQL(createTable);
			createTable = "create table " + TABLE_ADELEM + " ( " + ID_PRIMARY + " integer primary key autoincrement, " + ADELEM_WIDTH + " integer , "
					+ ADELEM_HEIGHT + " integer , " + ADELEM_SEQNO + " integer , " + ADELEM_SIZE + " integer , " + ADELEM_DESC + " varchar , "
					+ ADELEM_ADVER + " varchar , " + ADELEM_URL + " varchar , " + ADELEM_TYPE + " varchar );";
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
	 * 保存一条广告信息
	 * 
	 * @param d
	 * @return
	 */
	@Override
	public boolean insert(Object obj) {

		boolean ret = false;
		String sql = "";
		tech.bbwang.www.ws.AdDetail d = (tech.bbwang.www.ws.AdDetail) obj;
		db.beginTransaction();

		ContentValues values = new ContentValues();

		try {
			values.put(AD_VERSION, d.getData().getAd_version());
			values.put(AD_NAME, d.getData().getAd_name());
			values.put(CREATE_TIME, DateUtil.sdf_yyyyMMddHHmmss.format(new Date()));
			db.insert(TABLE_AD, null, values);
			for (ADImage e : d.getData().getAd_data_list()) {
				values.clear();
				values.put(ADELEM_WIDTH, e.getWidth());
				values.put(ADELEM_HEIGHT, e.getHeight());
				values.put(ADELEM_SEQNO, e.getSequence_no());
				values.put(ADELEM_SIZE, e.getSize());
				values.put(ADELEM_DESC, e.getDescription());
				values.put(ADELEM_ADVER, d.getData().getAd_version());
				values.put(ADELEM_TYPE, e.getType());
				values.put(ADELEM_URL, e.getUrl());
				db.insert(TABLE_ADELEM, null, values);
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

		DBAd ret = new DBAd();
		String sql = "select * from " + TABLE_AD + " as a left join " + TABLE_ADELEM + " as b on a." + AD_VERSION + "=b." + ADELEM_ADVER + " where ";
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
			where = "a." + key + "='" + params.get(key) + "';";
			count++;
		}

		sql += where;

		ColetApplication.getApp().logDebug(sql);
		Cursor c = db.rawQuery(sql + where, null);
		while (c.moveToNext()) {
			// Log.d("DEMO", c.getColumnNames().toString());
			ret.setPrimary_id(c.getInt(0));
			ret.setAd_version(c.getString(1));
			ret.setAd_name(c.getString(2));
			if(c.getString(3).equals("") == false ){
				try {
					ret.setCreateTime(DateUtil.sdf_yyyyMMddHHmmss.parse(c.getString(3)));
				} catch (ParseException e) {
					ret.setCreateTime(null);
				}
			}
			DBAdElement elm = new DBAdElement(c.getInt(4), c.getInt(5), c.getInt(6), c.getInt(7), c.getInt(8), c.getString(9), c.getString(10),
					c.getString(11), c.getString(12));
			ret.addAdElement(elm);
		}
		c.close();

		return ret;

	}

	/**
	 * 获取最新版本的广告信息
	 * 
	 * @return
	 */
	public Object getLastAd() {

		DBAd ret = new DBAd();
		String sql = "select * from " + TABLE_AD + " as a left join " + TABLE_ADELEM + " as b on a." + AD_VERSION + "=b." + ADELEM_ADVER
				+ " where a." + ID_PRIMARY + "=(select max( " + ID_PRIMARY + " ) from " + TABLE_AD + ")";

//		ColetApplication.getApp().logDebug(sql);
		Cursor c = db.rawQuery(sql, null);
		while (c.moveToNext()) {
			ret.setPrimary_id(c.getInt(0));
			ret.setAd_version(c.getString(1));
			ret.setAd_name(c.getString(2));
			if(c.getString(3).equals("") == false ){
				try {
					ret.setCreateTime(DateUtil.sdf_yyyyMMddHHmmss.parse(c.getString(3)));
				} catch (ParseException e) {
					ret.setCreateTime(null);
				}
			}
			DBAdElement elm = new DBAdElement(c.getInt(4), c.getInt(5), c.getInt(6), c.getInt(7), c.getInt(8), c.getString(9), c.getString(10),
					c.getString(11), c.getString(12));
			ret.addAdElement(elm);
		}
		c.close();

		return ret;

	}

	/**
	 * 获取所有版本的广告信息
	 */
	@Override
	public List<Object> getAll() {

		List<Object> result = new ArrayList<Object>();

		String sql = "select * from " + TABLE_AD + " as a left join " + TABLE_ADELEM + " as b on a." + AD_VERSION + "=b." + ADELEM_ADVER
				+ " order by a." + ID_PRIMARY + ",a." + AD_VERSION + ",b." + ID_PRIMARY + ";";

//		ColetApplication.getApp().logDebug(sql);
		Cursor c = db.rawQuery(sql, null);
		String uniqueId = "";
		DBAd newAd = null;

		while (c.moveToNext()) {
			if (!uniqueId.equals(c.getString(1))) {

				if (newAd != null) {
					result.add(newAd);
				}else{
					newAd = new DBAd();
					newAd.setPrimary_id(c.getInt(0));
					newAd.setAd_version(c.getString(1));
					newAd.setAd_name(c.getString(2));
					if(c.getString(3).equals("") == false ){
						try {
							newAd.setCreateTime(DateUtil.sdf_yyyyMMddHHmmss.parse(c.getString(3)));
						} catch (ParseException e) {
							newAd.setCreateTime(null);
						}
					}
					uniqueId = c.getString(1);
					result.add(newAd);
				}

			}

			DBAdElement elm = new DBAdElement(c.getInt(4), c.getInt(5), c.getInt(6), c.getInt(7), c.getInt(8), c.getString(9), c.getString(10),
					c.getString(11), c.getString(12));
			newAd.addAdElement(elm);
		}
		c.close();

		return result;
	}

	@Override
	public List<Object> getAll(Map<String, String> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean insert(List<Object> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int delete(Map<String, String> params) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Map<String, String> setParams, Map<String, String> whereParams) {
		// TODO Auto-generated method stub
		return 0;
	}

}
