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
public class DataCreateCard {

	private String terminal_id;
	private String terminal_code;
	private String card_no;

	public String getTerminal_id() {
		return terminal_id;
	}

	public void setTerminal_id(String terminal_id) {
		this.terminal_id = terminal_id;
	}

	public String getTerminal_code() {
		return terminal_code;
	}

	public void setTerminal_code(String terminal_code) {
		this.terminal_code = terminal_code;
	}

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}

	public String toString() {
		return GsonUtil.gson.toJson(this);
	}

}