/**
  * Copyright 2017 bejson.com 
  */
package tech.bbwang.www.ws;

import tech.bbwang.www.util.GsonUtil;

/**
 * Auto-generated: 2017-09-17 0:33:34
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Skus {

    private int list_price;
    private int promotion_price;
    private String promotion_type;
    private String attr1;
    private String value1;
    private String desc_attr;
    private String barcode;
    public void setList_price(int list_price) {
         this.list_price = list_price;
     }
     public int getList_price() {
         return list_price;
     }

    public void setPromotion_price(int promotion_price) {
         this.promotion_price = promotion_price;
     }
     public int getPromotion_price() {
         return promotion_price;
     }

    public void setPromotion_type(String promotion_type) {
         this.promotion_type = promotion_type;
     }
     public String getPromotion_type() {
         return promotion_type;
     }

    public void setAttr1(String attr1) {
         this.attr1 = attr1;
     }
     public String getAttr1() {
         return attr1;
     }

    public void setValue1(String value1) {
         this.value1 = value1;
     }
     public String getValue1() {
         return value1;
     }

    public void setDesc_attr(String desc_attr) {
         this.desc_attr = desc_attr;
     }
     public String getDesc_attr() {
         return desc_attr;
     }

    public void setBarcode(String barcode) {
         this.barcode = barcode;
     }
     public String getBarcode() {
         return barcode;
     }

     
     public String toString(){
    	 return GsonUtil.gson.toJson(this);
     }
}