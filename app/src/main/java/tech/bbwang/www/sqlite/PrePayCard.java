/**
 * Copyright 2017 bejson.com 
 */
package tech.bbwang.www.sqlite;

import tech.bbwang.www.util.GsonUtil;

/**
 * 储值卡
 * @author bbwang8088@126.com
 * 
 *
 */
public class PrePayCard {

	private String versionCode;
	private String title;
	private String list_price;
	private String fresh_price;
	private String vip_price;
	private int index;

	public PrePayCard(String versionCode, String title, String list_price,
			String fresh_price, String vip_price, int index) {
		super();
		this.versionCode = versionCode;
		this.title = title;
		this.list_price = list_price;
		this.fresh_price = fresh_price;
		this.vip_price = vip_price;
		this.index = index;
	}

	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getList_price() {
		return list_price;
	}

	public void setList_price(String list_price) {
		this.list_price = list_price;
	}

	public String getFresh_price() {
		return fresh_price;
	}

	public void setFresh_price(String fresh_price) {
		this.fresh_price = fresh_price;
	}

	public String getVip_price() {
		return vip_price;
	}

	public void setVip_price(String vip_price) {
		this.vip_price = vip_price;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String toString() {
		return GsonUtil.gson.toJson(this);
	}

}