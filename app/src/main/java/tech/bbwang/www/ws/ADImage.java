/**
  * Copyright 2017 bejson.com 
  */
package tech.bbwang.www.ws;

import tech.bbwang.www.util.GsonUtil;

/**
 * Auto-generated: 2017-09-17 0:23:5
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class ADImage {

    private int sequence_no;
    private String type;
    private String description;
    private int height;
    private int width;
    private int size;
    private String url;
    public void setSequence_no(int sequence_no) {
         this.sequence_no = sequence_no;
     }
     public int getSequence_no() {
         return sequence_no;
     }

    public void setType(String type) {
         this.type = type;
     }
     public String getType() {
         return type;
     }

    public void setDescription(String description) {
         this.description = description;
     }
     public String getDescription() {
         return description;
     }

    public void setHeight(int height) {
         this.height = height;
     }
     public int getHeight() {
         return height;
     }

    public void setWidth(int width) {
         this.width = width;
     }
     public int getWidth() {
         return width;
     }

    public void setSize(int size) {
         this.size = size;
     }
     public int getSize() {
         return size;
     }

    public void setUrl(String url) {
         this.url = url;
     }
     public String getUrl() {
         return url;
     }
     
     public String toString(){
    	 return GsonUtil.gson.toJson(this);
     }

}