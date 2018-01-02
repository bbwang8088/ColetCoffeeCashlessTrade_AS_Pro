package tech.bbwang.www.sqlite;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 广告
 * 
 * @author @author bbwang8088@126.com
 * 
 */
public class DBMenu {
	private int primary_id = 0;
	private String menu_version = "0";
	private String menu_name = "";
	private Date createTime = null;
	private List<DBGood> good_data_list = new ArrayList<DBGood>();

	public DBMenu() {

	}

	public DBMenu(int primary_id, String menu_version, String menu_name, Date createTime, List<DBGood> good_data_list) {
		super();
		this.primary_id = primary_id;
		this.menu_version = menu_version;
		this.menu_name = menu_name;
		this.createTime = createTime;
		this.good_data_list = good_data_list;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public List<DBGood> getGood_data_list() {
		return good_data_list;
	}

	public void setGood_data_list(List<DBGood> good_data_list) {
		this.good_data_list = good_data_list;
	}

	public int getPrimary_id() {
		return primary_id;
	}

	public void setPrimary_id(int primary_id) {
		this.primary_id = primary_id;
	}

	public String getMenu_version() {
		return menu_version;
	}

	public void setMenu_version(String menu_version) {
		this.menu_version = menu_version;
	}

	public String getMenu_name() {
		return menu_name;
	}

	public void setMenu_name(String menu_name) {
		this.menu_name = menu_name;
	}

	public List<DBGood> getGood_list() {
		return good_data_list;
	}

	public void setGood_list(List<DBGood> good_list) {
		this.good_data_list = good_list;
	}

	public void addGood(DBGood good) {
		if (this.good_data_list != null) {
			this.good_data_list.add(good);
		}
	}
}

// class Menu {
// private int primary_id = 0;
// private long menu_version = 0;
// private String menu_name = "";
// private List<_03_Good> good_data_list = new ArrayList<_03_Good>();
//
// public int getPrimary_id() {
// return primary_id;
// }
//
// public void setPrimary_id(int primary_id) {
// this.primary_id = primary_id;
// }
//
// public long getMenu_version() {
// return menu_version;
// }
//
// public void setMenu_version(long menu_version) {
// this.menu_version = menu_version;
// }
//
// public String getMenu_name() {
// return menu_name;
// }
//
// public void setMenu_name(String menu_name) {
// this.menu_name = menu_name;
// }
//
// public List<_03_Good> getGood_list() {
// return good_data_list;
// }
//
// public void setGood_list(List<_03_Good> good_list) {
// this.good_data_list = good_list;
// }
//
// public void addGood(_03_Good good) {
// if (this.good_data_list != null) {
// this.good_data_list.add(good);
// }
// }
// }
