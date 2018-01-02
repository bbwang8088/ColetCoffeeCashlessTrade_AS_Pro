package tech.bbwang.www.sqlite;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import tech.bbwang.www.activity.ColetApplication;
import tech.bbwang.www.util.DateUtil;
import tech.bbwang.www.ws.Good;
import tech.bbwang.www.ws.MenuDetail;
import tech.bbwang.www.ws.Menu_Category;
import tech.bbwang.www.ws.Skus;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库操作类
 * 
 * @author bbwang8088@126.com
 * 
 */
public class MenuDAO implements IDAO {
	public static String ID_PRIMARY = "id";

	public static String TABLE_MENU = "Menu";
	public static String TABLE_GOOD = "Good";

	public static String MENU_NAME = "name";
	public static String MENU_VERSION = "version";

	public static String GOOD_MENUVER = "menuVer";
	public static String GOOD_CATEGORY = "category";
	public static String GOOD_CATEGORY_IMAGE = "category_image";
	public static String GOOD_CATEGORY_SEQUENCE = "good_sequence";
	public static String GOOD_NAME = "name";
	public static String GOOD_LSTPRICE = "list_price";
	public static String GOOD_IMAGE = "image";
	public static String GOOD_PROMPRICE = "prom_price";
	public static String GOOD_PROTYPE = "prom_type";
	public static String CREATE_TIME = "create_time";

	private SQLiteDatabase db = null;

	public MenuDAO(SQLiteDatabase db) {

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
			createTable = "create table " + TABLE_MENU + " (" + ID_PRIMARY + " integer primary key autoincrement, " + MENU_VERSION + " varchar , "
					+ MENU_NAME + " varchar,  "+ CREATE_TIME + " varchar );";
			db.execSQL(createTable);
			createTable = "create table " + TABLE_GOOD + " ( " + ID_PRIMARY + " integer primary key autoincrement, " + GOOD_CATEGORY + " varchar , " 
			+ GOOD_NAME + " varchar , " + GOOD_LSTPRICE + " integer , " + GOOD_IMAGE + " varchar , " + GOOD_PROMPRICE + " integer , "
			+ GOOD_MENUVER + " varchar , " + GOOD_PROTYPE + " varchar, "+GOOD_CATEGORY_IMAGE+" varchar , "+GOOD_CATEGORY_SEQUENCE+" varchar );";
			ColetApplication.getApp().logDebug(createTable);
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
		MenuDetail d = (MenuDetail) obj;
		db.beginTransaction();

		ContentValues values = new ContentValues();

		try {
			values.put(MENU_VERSION, d.getData().getMenu_version());
			values.put(MENU_NAME, d.getData().getMenu_name());
			values.put(CREATE_TIME, DateUtil.sdf_yyyyMMddHHmmss.format(new Date()));
			db.insert(TABLE_MENU, null, values);
			for (Menu_Category e : d.getData().getMenu_data_list()) {
				for (Good g : e.getGoods_data_list()) {
					for (Skus sku : g.getSkus()) {
						values.clear();
						values.put(GOOD_CATEGORY, e.getCategory());
						values.put(GOOD_NAME, g.getName());
						values.put(GOOD_LSTPRICE, sku.getList_price());
						values.put(GOOD_IMAGE, g.getImage());
						values.put(GOOD_PROMPRICE, sku.getPromotion_price());
						values.put(GOOD_MENUVER, d.getData().getMenu_version());
						values.put(GOOD_PROTYPE, sku.getPromotion_type());
						values.put(GOOD_CATEGORY_IMAGE, e.getCategory_image());
						values.put(GOOD_CATEGORY_SEQUENCE, g.getSequence_no());
						db.insert(TABLE_GOOD, null, values);
					}

				}
			}

			ColetApplication.getApp().logDebug(sql);
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

		DBMenu ret = new DBMenu();
		String sql = "select * from " + TABLE_MENU + " as a left join " + TABLE_GOOD + " as b on a." + MENU_VERSION + "=b." + GOOD_MENUVER
				+ " where ";
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
			ret.setMenu_version(c.getString(1));
			ret.setMenu_name(c.getString(2));
			if (c.getString(3).equals("") == false) {
				try {
					ret.setCreateTime(DateUtil.sdf_yyyyMMddHHmmss.parse(c.getString(3)));
				} catch (ParseException e) {
					ret.setCreateTime(null);
				}
			}
			DBGood elm = new DBGood(c.getInt(4), c.getString(10), c.getInt(7), c.getString(5), c.getString(6), c.getInt(9), c.getString(11),
					c.getString(8),c.getString(12),c.getString(13));
			ret.addGood(elm);
		}
		c.close();

		return ret;

	}

	/**
	 * 获取最新版本的菜单信息
	 * 
	 * @return
	 */
	public Object getLastMenu() {

		DBMenu ret = new DBMenu();
		String sql = "select * from " + TABLE_MENU + " as a left join " + TABLE_GOOD + " as b on a." + MENU_VERSION + "=b." + GOOD_MENUVER
				+ " where a." + ID_PRIMARY + "=(select max( " + ID_PRIMARY + " ) from " + TABLE_MENU + ")";

		// ColetApplication.getApp().logDebug(sql);
		Cursor c = db.rawQuery(sql, null);
		while (c.moveToNext()) {
			ret.setPrimary_id(c.getInt(0));
			ret.setMenu_version(c.getString(1));
			ret.setMenu_name(c.getString(2));
			if (c.getString(3).equals("") == false) {
				try {
					ret.setCreateTime(DateUtil.sdf_yyyyMMddHHmmss.parse(c.getString(3)));
				} catch (ParseException e) {
					ret.setCreateTime(null);
				}
			}
			DBGood elm = new DBGood(c.getInt(4), c.getString(10), c.getInt(7), c.getString(5), c.getString(6), c.getInt(9), c.getString(11),
					c.getString(8),c.getString(12),c.getString(13));
			ret.addGood(elm);
		}
		c.close();

		return ret;

	}

	/**
	 * 获取所有版本的菜单信息
	 */
	@Override
	public List<Object> getAll() {

		List<Object> result = new ArrayList<Object>();

		String sql = "select * from " + TABLE_MENU + " as a left join " + TABLE_GOOD + " as b on a." + MENU_VERSION + "=b." + GOOD_MENUVER
				+ " order by a." + ID_PRIMARY + ",a." + MENU_VERSION + ",b." + ID_PRIMARY + ";";

		// ColetApplication.getApp().logDebug(sql);
		Cursor c = db.rawQuery(sql, null);
		String uniqueId = "";
		DBMenu newMu = null;

		while (c.moveToNext()) {
			if (!uniqueId.equals(c.getString(1))) {

				if (newMu != null) {
					result.add(newMu);
				} else {
					newMu = new DBMenu();
					newMu.setPrimary_id(c.getInt(0));
					newMu.setMenu_version(c.getString(1));
					newMu.setMenu_name(c.getString(2));
					if (c.getString(3).equals("") == false) {
						try {
							newMu.setCreateTime(DateUtil.sdf_yyyyMMddHHmmss.parse(c.getString(3)));
						} catch (ParseException e) {
							newMu.setCreateTime(null);
						}
					}

					uniqueId = c.getString(1);
					result.add(newMu);
				}

			}

			DBGood elm = new DBGood(c.getInt(4), c.getString(10), c.getInt(7), c.getString(5), c.getString(6), c.getInt(9), c.getString(11),
					c.getString(8),c.getString(12),c.getString(13));
			newMu.addGood(elm);
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
