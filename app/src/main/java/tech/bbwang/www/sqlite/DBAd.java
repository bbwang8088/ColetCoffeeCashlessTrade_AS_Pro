package tech.bbwang.www.sqlite;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tech.bbwang.www.util.GsonUtil;

/**
 * 广告
 * 
 * @author @author bbwang8088@126.com
 * 
 */
public class DBAd {
	private int primary_id = 0;
	private String ad_version = "0";
	private String ad_name = "";
	private Date createTime = null;
	private List<DBAdElement> ad_data_list = new ArrayList<DBAdElement>();

	public DBAd(){
		
	}
	
	public DBAd(int pid, String ver, String adName, List<DBAdElement> dataList, Date crtTime) {
		super();
		this.primary_id = pid;
		this.ad_version = ver;
		this.ad_name = adName;
		this.ad_data_list = dataList;
		this.createTime = crtTime;
	}

	public int getPrimary_id() {
		return primary_id;
	}

	public void setPrimary_id(int primary_id) {
		this.primary_id = primary_id;
	}

	public String getAd_version() {
		return ad_version;
	}

	public void setAd_version(String ad_version) {
		this.ad_version = ad_version;
	}

	public String getAd_name() {
		return ad_name;
	}

	public void setAd_name(String ad_name) {
		this.ad_name = ad_name;
	}

	public List<DBAdElement> getAd_data_list() {
		return ad_data_list;
	}

	public void setAd_data_list(List<DBAdElement> ad_data_list) {
		this.ad_data_list = ad_data_list;
	}

	public void addAdElement(DBAdElement adElem) {
		if (this.ad_data_list != null) {
			this.ad_data_list.add(adElem);
		}
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String toString() {
		return GsonUtil.gson.toJson(this);
	}
}

// class AD {
// private int primary_id = 0;
// private long ad_version = 0;
// private String ad_name = "";
// private List<_02_AdElement> ad_data_list = new ArrayList<_02_AdElement>();
//
// public int getPrimary_id() {
// return primary_id;
// }
//
// public void setPrimary_id(int primary_id) {
// this.primary_id = primary_id;
// }
//
// public long getAd_version() {
// return ad_version;
// }
//
// public void setAd_version(long ad_version) {
// this.ad_version = ad_version;
// }
//
// public String getAd_name() {
// return ad_name;
// }
//
// public void setAd_name(String ad_name) {
// this.ad_name = ad_name;
// }
//
// public List<_02_AdElement> getAd_data_list() {
// return ad_data_list;
// }
//
// public void setAd_data_list(List<_02_AdElement> ad_data_list) {
// this.ad_data_list = ad_data_list;
// }
//
// public void addAdElement(_02_AdElement adElem) {
// if (this.ad_data_list != null) {
// this.ad_data_list.add(adElem);
// }
// }
// }
