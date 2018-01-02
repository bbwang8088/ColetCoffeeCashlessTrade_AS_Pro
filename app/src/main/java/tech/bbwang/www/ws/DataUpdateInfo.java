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
public class DataUpdateInfo {

    private String last_ad_version;
    private String last_menu_version;
    private String last_app_version;
    private String last_charge_version;
    
    public void setLast_ad_version(String last_ad_version) {
         this.last_ad_version = last_ad_version;
     }
     public String getLast_ad_version() {
         return last_ad_version;
     }

    public void setLast_menu_version(String last_menu_version) {
         this.last_menu_version = last_menu_version;
     }
     public String getLast_menu_version() {
         return last_menu_version;
     }

    public void setLast_app_version(String last_app_version) {
         this.last_app_version = last_app_version;
     }
     public String getLast_app_version() {
         return last_app_version;
     }
     
	public String getLast_charge_version() {
		return last_charge_version;
	}
	public void setLast_charge_version(String last_charge_version) {
		this.last_charge_version = last_charge_version;
	}
	public String toString(){
    	 return GsonUtil.gson.toJson(this);
     }

}