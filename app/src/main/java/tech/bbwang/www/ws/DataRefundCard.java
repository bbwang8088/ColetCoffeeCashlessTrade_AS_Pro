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
public class DataRefundCard {

	private String remain;
	private String amount;

	public String getRemain() {
		return remain;
	}

	public void setRemain(String remain) {
		this.remain = remain;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String toString() {
		return GsonUtil.gson.toJson(this);
	}

}