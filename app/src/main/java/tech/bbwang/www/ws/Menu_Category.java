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
public class Menu_Category {

    private String category;
    private String category_image;
    private List<Good> goods_data_list =  new ArrayList<Good>();
    public void setCategory(String category) {
         this.category = category;
     }
     public String getCategory() {
         return category;
     }
     
    public String getCategory_image() {
		return category_image;
	}
	public void setCategory_image(String category_image) {
		this.category_image = category_image;
	}
	public void setGoods_data_list(List<Good> goods_data_list) {
         this.goods_data_list = goods_data_list;
     }
     public List<Good> getGoods_data_list() {
         return goods_data_list;
     }
     
     public String toString(){
    	 return GsonUtil.gson.toJson(this);
     }

}