/**
  * Copyright 2017 bejson.com 
  */
package tech.bbwang.www.ws;
import java.util.ArrayList;
import java.util.List;

import tech.bbwang.www.util.GsonUtil;

/**
 * Auto-generated: 2017-09-17 0:23:5
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class DataAdDetailInfo {

    private String ad_version;
    private String ad_name;
    private List<ADImage> ad_data_list = new ArrayList<ADImage>();
    public void setAd_version(String ad_version) {
         this.ad_version = ad_version;
     }
     public String getAd_version() {
         return ad_version;
     }

    public void setAd_name(String ad_name) {
         this.ad_name = ad_name;
     }
     public String getAd_name() {
         return ad_name;
     }

    public void setAd_data_list(List<ADImage> ad_data_list) {
         this.ad_data_list = ad_data_list;
     }
     public List<ADImage> getAd_data_list() {
         return ad_data_list;
     }
     
     public String toString(){
    	 return GsonUtil.gson.toJson(this);
     }
     
     

}