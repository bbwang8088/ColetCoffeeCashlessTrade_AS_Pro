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
public class Good {

    private List<Skus> skus = new ArrayList<Skus>();
    private String name;
    private String image;
    private String describe;
    private String sub_title;
    private String article_no;
    private int sequence_no;
    public void setSkus(List<Skus> skus) {
         this.skus = skus;
     }
     public List<Skus> getSkus() {
         return skus;
     }

    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }

    public void setImage(String image) {
         this.image = image;
     }
     public String getImage() {
         return image;
     }

    public void setDescribe(String describe) {
         this.describe = describe;
     }
     public String getDescribe() {
         return describe;
     }

    public void setSub_title(String sub_title) {
         this.sub_title = sub_title;
     }
     public String getSub_title() {
         return sub_title;
     }

    public void setArticle_no(String article_no) {
         this.article_no = article_no;
     }
     public String getArticle_no() {
         return article_no;
     }

    public void setSequence_no(int sequence_no) {
         this.sequence_no = sequence_no;
     }
     public int getSequence_no() {
         return sequence_no;
     }
     
     public String toString(){
    	 return GsonUtil.gson.toJson(this);
     }

}