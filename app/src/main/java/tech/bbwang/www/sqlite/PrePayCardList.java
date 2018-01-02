/**
 * Copyright 2017 bejson.com 
 */
package tech.bbwang.www.sqlite;

import java.util.ArrayList;
import java.util.List;

import tech.bbwang.www.util.GsonUtil;

/**
 * 储值卡列表
 * @author bbwang8088@126.com
 *
 */
public class PrePayCardList {

	List<PrePayCard> prePayCardList = new ArrayList<PrePayCard>();

	public List<PrePayCard> getPrePayCardList() {
		return prePayCardList;
	}

	public void setPrePayCardList(List<PrePayCard> prePayCardList) {
		this.prePayCardList = prePayCardList;
	}

	public void clear() {
		this.prePayCardList.clear();
	}

	public void addPrePayCard(PrePayCard card) {
		this.prePayCardList.add(card);
	}

	public String toString() {
		return GsonUtil.gson.toJson(this);
	}

}