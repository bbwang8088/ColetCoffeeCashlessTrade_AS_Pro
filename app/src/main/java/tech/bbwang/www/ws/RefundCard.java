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
public class RefundCard {

	private int status = -1;
	private String message;
	private String franchise;
	private DataRefundCard data = new DataRefundCard();

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setFranchise(String franchise) {
		this.franchise = franchise;
	}

	public String getFranchise() {
		return franchise;
	}

	public void setData(DataRefundCard data) {
		this.data = data;
	}

	public DataRefundCard getData() {
		return data;
	}

	public String toString() {
		return GsonUtil.gson.toJson(this);
	}

}