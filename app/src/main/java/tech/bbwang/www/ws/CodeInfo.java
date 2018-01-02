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
public class CodeInfo {

    private String code;
    private String content;
    private String start_date;
    private String end_date;
    private int is_available;
    private int is_used;
    public void setCode(String code) {
         this.code = code;
     }
     public String getCode() {
         return code;
     }

    public void setContent(String content) {
         this.content = content;
     }
     public String getContent() {
         return content;
     }

    public void setStart_date(String start_date) {
         this.start_date = start_date;
     }
     public String getStart_date() {
         return start_date;
     }

    public void setEnd_date(String end_date) {
         this.end_date = end_date;
     }
     public String getEnd_date() {
         return end_date;
     }

    public void setIs_available(int is_available) {
         this.is_available = is_available;
     }
     public int getIs_available() {
         return is_available;
     }

    public void setIs_used(int is_used) {
         this.is_used = is_used;
     }
     public int getIs_used() {
         return is_used;
     }

     public String toString(){
    	 return GsonUtil.gson.toJson(this);
     }
}