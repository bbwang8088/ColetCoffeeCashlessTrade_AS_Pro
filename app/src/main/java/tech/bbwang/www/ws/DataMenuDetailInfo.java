/**
  * Copyright 2017 bejson.com 
  */
package tech.bbwang.www.ws;
import java.util.ArrayList;
import java.util.List;

import tech.bbwang.www.util.GsonUtil;

/**
 * Auto-generated: 2017-09-17 0:33:34
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class DataMenuDetailInfo {

    private String menu_version;
    private String menu_name;
    private List<Menu_Category> menu_data_list = new ArrayList<Menu_Category>();
    public void setMenu_version(String menu_version) {
         this.menu_version = menu_version;
     }
     public String getMenu_version() {
         return menu_version;
     }

    public void setMenu_name(String menu_name) {
         this.menu_name = menu_name;
     }
     public String getMenu_name() {
         return menu_name;
     }

    public void setMenu_data_list(List<Menu_Category> menu_data_list) {
         this.menu_data_list = menu_data_list;
     }
     public List<Menu_Category> getMenu_data_list() {
         return menu_data_list;
     }

     public String toString(){
    	 return GsonUtil.gson.toJson(this);
     }
     
     
}