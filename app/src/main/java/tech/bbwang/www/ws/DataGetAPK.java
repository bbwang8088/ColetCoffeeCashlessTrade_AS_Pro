/**
 * Copyright 2017 bejson.com 
 */
package tech.bbwang.www.ws;

import tech.bbwang.www.util.GsonUtil;

/**
 * Auto-generated: 2017-09-17 1:23:51
 * 
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class DataGetAPK {

	private String terminal_code;
	private String app_version;
	private String app_url;

	public String getTerminal_code() {
		return terminal_code;
	}

	public void setTerminal_code(String terminal_code) {
		this.terminal_code = terminal_code;
	}

	public void setApp_version(String app_version) {
		this.app_version = app_version;
	}

	public String getApp_version() {
		return app_version;
	}

	public void setApp_url(String app_url) {
		this.app_url = app_url;
	}

	public String getApp_url() {
		return app_url;
	}

	public String toString() {
		return GsonUtil.gson.toJson(this);
	}
}