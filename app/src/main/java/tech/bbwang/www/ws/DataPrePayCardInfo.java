/**
 * Copyright 2017 bejson.com 
 */
package tech.bbwang.www.ws;

import tech.bbwang.www.util.GsonUtil;

/**
 * Auto-generated: 2017-09-17 0:15:2
 * 
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class DataPrePayCardInfo {

	private String title;
	private String list_price;
	private String fresh_price;
	private String vip_price;
	private String index;

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

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String toString() {
		return GsonUtil.gson.toJson(this);
	}

}