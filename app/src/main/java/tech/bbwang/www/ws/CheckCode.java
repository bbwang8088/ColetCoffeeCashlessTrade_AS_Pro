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
public class CheckCode {

    private int status = -1;
    private String message;
    private String franchise;
    private DataCheckCode data = new DataCheckCode();
    public void setStatus(int status) {
         this.status = status;
     }
     public int getStatus() {
         return status;
     }

    public void setMessage(String message) {
         this.message = message;
     }
     public String getMessage() {
         return message;
     }

    public void setFranchise(String franchise) {
         this.franchise = franchise;
     }
     public String getFranchise() {
         return franchise;
     }

    public void setData(DataCheckCode data) {
         this.data = data;
     }
     public DataCheckCode getData() {
         return data;
     }
     
     public String toString(){
    	 return GsonUtil.gson.toJson(this);
     }

}