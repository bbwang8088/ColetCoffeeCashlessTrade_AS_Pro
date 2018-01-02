package tech.bbwang.www.util;

import tech.bbwang.www.activity.ColetApplication;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用于保存一些简单数据的类
 * 
 * @author bbwang8088@126.com
 * 
 */
public class ConfigFileUtil {

	private static final String LONGITUDE = "longitude";
	private static final String LATITUDE = "latitude";
	private static final String LAST_AD_VERSION = "last_ad_version";
	private static final String LAST_MENU_VERSION = "last_menu_version";
	private static final String FRANCHISE_ID = "franchise_id";
	private static final String API_URL = "api_url";
	private static final String ALI_PAY_URL = "ali_pay_url";
	private static final String WECHAT_PAY_URL = "wechat_pay_url";
	private static final String HEARTBEAT_PLUSE = "heartbeat_pluse";
	private static final String ACTIVE_STATUS = "active_status";
	private static final String AD_SWITCH_TIME = "ad_switch_time";
	private static final String AD_SWITCH_EFFECT = "ad_switch_effect";
	private static final String LAST_ERROR = "last_error";
	private static final String LAST_ERROR_TIME = "last_error_time";
	private static final String ANIM_AD_ID = "anim_ad_id";
	private static final String LAST_VIP_CARD_LIST_CODE= "vip_card_list_code";
	
	private SharedPreferences configFile = null;
	private Context context = null;
	private SharedPreferences.Editor editor = null;

	public ConfigFileUtil(Context cxt) {
		this.context = cxt;
		configFile = this.context.getSharedPreferences("ColetCoffee", Activity.MODE_PRIVATE);
		editor = configFile.edit();
	}

	public String getLastAdVersion() {
		String ret = configFile.getString(LAST_AD_VERSION, "0.0.0");
		ColetApplication.getApp().logDebug("Read LAST_AD_VERSION:" + ret + " success!");
		return ret;
	}

	public String getLastMenuVersion() {
		String ret = configFile.getString(LAST_MENU_VERSION, "0.0.0");
		ColetApplication.getApp().logDebug("Read LAST_MENU_VERSION:" + ret + " success!");
		return ret;
	}

	public String getFranchiseId() {
		String ret = configFile.getString(FRANCHISE_ID, "0");
		ColetApplication.getApp().logDebug("Read FRANCHISE_ID:" + ret + " success!");
		return ret;
	}
	
	public String getApiUrl() {
		String ret = configFile.getString(API_URL, "");
		ColetApplication.getApp().logDebug("Read API_URL:" + ret + " success!");
		return ret;
	}
	
	public String getAliPayUrl() {
		String ret = configFile.getString(ALI_PAY_URL, "");
		ColetApplication.getApp().logDebug("Read ALIPAY_PAY_URL:" + ret + " success!");
		return ret;
	}
	
	public String getWechatPayUrl() {
		String ret = configFile.getString(WECHAT_PAY_URL, "");
		ColetApplication.getApp().logDebug("Read WECHAT_PAY_URL:" + ret + " success!");
		return ret;
	}
	
	public String getHeartbeatPluse() {
		String ret = configFile.getString(HEARTBEAT_PLUSE, "0");
		ColetApplication.getApp().logDebug("Read HEARTBEAT_PLUSE:" + ret + " success!");
		return ret;
	}
	
	/**
	 * 0：合法编号，激活成功。
   	 * 1：已被激活，拒绝激活。
     * 2：已被冻结，拒绝激活。
     * 3：已被停用，拒绝激活。
     * 4：非法编号，拒绝激活。
	 * @return
	 */
	public int getActiveStatus() {
		int ret = configFile.getInt(ACTIVE_STATUS, 999);
		ColetApplication.getApp().logDebug("Read ACTIVE_STATUS:" + ret + " success!");
		return ret;
	}
	
	
	public int getADSwitchTime() {
		int ret = configFile.getInt(AD_SWITCH_TIME, 5);
		ColetApplication.getApp().logDebug("Read AD_SWITCH_TIME:" + ret + " success!");
		return ret;
	}
	
	public String getADSwitchEffect() {
		String ret = configFile.getString(AD_SWITCH_EFFECT, "无");
		ColetApplication.getApp().logDebug("Read AD_SWITCH_EFFECT:" + ret + " success!");
		return ret;
	}
	
	public String getLastError() {
		String ret = configFile.getString(LAST_ERROR, "");
		//ColetApplication.getApp().logDebug("Read LAST_ERROR:" + ret + " success!");
		return ret;
	}

	public String getLastErrorTime() {
		String ret = configFile.getString(LAST_ERROR_TIME, "0");
		//ColetApplication.getApp().logDebug("Read LAST_ERROR_TIME:" + ret + " success!");
		return ret;
	}
	
	public String getLongitude(){

		String ret = configFile.getString(LONGITUDE, "0");
		ColetApplication.getApp().logDebug("Read LONGITUDE:" + ret + " success!");
		return ret;
	
	}
	
	public String getLatitude(){

		String ret = configFile.getString(LATITUDE, "0");
		ColetApplication.getApp().logDebug("Read LATITUDE:" + ret + " success!");
		return ret;
	
	}
	
	public int getAnimAdId(){

		int ret = configFile.getInt(ANIM_AD_ID, 0);
		ColetApplication.getApp().logDebug("Read ANIM_AD_ID:" + ret + " success!");
		return ret;
	
	}
	
	public String getVipCardListCode(){

		String ret = configFile.getString(LAST_VIP_CARD_LIST_CODE, "0");
		ColetApplication.getApp().logDebug("Read LAST_VIP_CARD_LIST_CODE:" + ret + " success!");
		return ret;
	
	}
	
	public void setLastAdVersion(String lastAdVersion) {
		editor.putString(LAST_AD_VERSION, lastAdVersion);
		if (editor.commit() == true) {
			ColetApplication.getApp().logDebug("Write LAST_AD_VERSION:" + lastAdVersion + " success!");
		}
	}

	public void setLastMenuVersion(String lastMenuVersion) {
		editor.putString(LAST_MENU_VERSION, lastMenuVersion);
		if (editor.commit() == true) {
			ColetApplication.getApp().logDebug("Write ACTIVE_STATUS:" + lastMenuVersion + " success!");
		}
	}

	public void setFranchiseId(String FranchiseId) {
		editor.putString(FRANCHISE_ID, FranchiseId);
		if (editor.commit() == true) {
			ColetApplication.getApp().logDebug("Write FRANCHISE_ID:" + FranchiseId + " success!");
		}
	}
	
	public void setApiUrl(String apiUrl) {
		editor.putString(API_URL, apiUrl);
		if (editor.commit() == true) {
			ColetApplication.getApp().logDebug("Write API_URL:" + apiUrl + " success!");
		}
	}
	
	public void setAliPayUrl(String payUrl) {
		editor.putString(ALI_PAY_URL, payUrl);
		if (editor.commit() == true) {
			ColetApplication.getApp().logDebug("Write ALI_PAY_URL:" + payUrl + " success!");
		}
	}
	
	
	public void setWechatPayUrl(String payUrl) {
		editor.putString(WECHAT_PAY_URL, payUrl);
		if (editor.commit() == true) {
			ColetApplication.getApp().logDebug("Write WECHAT_PAY_URL:" + payUrl + " success!");
		}
	}
	
	public void setHeartbeatPluse(String heartbeatPluse) {
		editor.putString(HEARTBEAT_PLUSE, heartbeatPluse);
		if (editor.commit() == true) {
			ColetApplication.getApp().logDebug("Write HEARTBEAT_PLUSE:" + heartbeatPluse + " success!");
		}
	}
	
	public void setActiveStatus(int status) {
		editor.putInt(ACTIVE_STATUS, status);
		if (editor.commit() == true) {
			ColetApplication.getApp().logDebug("Write ACTIVE_STATUS:" + status + " success!");
		}
	}
	
	public void setADSwitchTime(int status) {
		editor.putInt(AD_SWITCH_TIME, status);
		if (editor.commit() == true) {
			ColetApplication.getApp().logDebug("Write AD_SWITCH_TIME:" + status + " success!");
		}
	}
	
	public void setADSwitchEffect(String effect) {
		editor.putString(AD_SWITCH_EFFECT, effect);
		if (editor.commit() == true) {
			ColetApplication.getApp().logDebug("Write AD_SWITCH_EFFECT:" + effect + " success!");
		}
	}

	public void setLastError(String error) {
		editor.putString(LAST_ERROR, error);
		if (editor.commit() == true) {
			//ColetApplication.getApp().logDebug("Write LAST_ERROR:" + error + " success!");
		}
	}
	
	public void setLastErrorTime(String errorTime) {
		editor.putString(LAST_ERROR_TIME, errorTime);
		if (editor.commit() == true) {
			//ColetApplication.getApp().logDebug("Write LAST_ERROR_TIME:" + errorTime + " success!");
		}
	}
	
	public void setLongitude(String l){

		editor.putString(LONGITUDE, l);
		if (editor.commit() == true) {
			ColetApplication.getApp().logDebug("Write LONGITUDE:" + l + " success!");
		}
	
	}
	
	public void setLatitude(String l){

		editor.putString(LATITUDE, l);
		if (editor.commit() == true) {
			ColetApplication.getApp().logDebug("Write LATITUDE:" + l + " success!");
		}
	}
	
	
	public void setAnimAdId(int l){

		editor.putInt(ANIM_AD_ID, l);
		if (editor.commit() == true) {
			ColetApplication.getApp().logDebug("Write ANIM_AD_ID:" + l + " success!");
		}
	}
	
	public void setVipCardListCode(String c){

		editor.putString(LAST_VIP_CARD_LIST_CODE, c);
		if (editor.commit() == true) {
			ColetApplication.getApp().logDebug("Write LAST_VIP_CARD_LIST_CODE:" + c + " success!");
		}
	
	}
	
	
}
