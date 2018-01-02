/**
 * Copyright 2017 bejson.com 
 */
package tech.bbwang.www.ws;

import tech.bbwang.www.util.GsonUtil;

/**
 * Auto-generated: 2017-09-17 1:1:56
 * 
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class DataCheckCode {

	private String terminal_code;
	private String code;
	private CodeInfo code_info;
	private Goods_info goods_info;
	private Goods_sku_info goods_sku_info;

	public String getTerminal_code() {
		return terminal_code;
	}

	public void setTerminal_code(String terminal_code) {
		this.terminal_code = terminal_code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode_info(CodeInfo code_info) {
		this.code_info = code_info;
	}

	public CodeInfo getCode_info() {
		return code_info;
	}

	public void setGoods_info(Goods_info goods_info) {
		this.goods_info = goods_info;
	}

	public Goods_info getGoods_info() {
		return goods_info;
	}

	public void setGoods_sku_info(Goods_sku_info goods_sku_info) {
		this.goods_sku_info = goods_sku_info;
	}

	public Goods_sku_info getGoods_sku_info() {
		return goods_sku_info;
	}

	public String toString() {
		return GsonUtil.gson.toJson(this);
	}

}