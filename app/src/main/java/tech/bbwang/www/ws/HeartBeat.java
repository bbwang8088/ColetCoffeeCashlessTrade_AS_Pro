/**
  * Copyright 2017 bejson.com 
  */
package tech.bbwang.www.ws;

/**
 * Auto-generated: 2017-09-21 11:14:3
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class HeartBeat {

    private int status = -1;
    private String message;
    private String franchise;
    private HeartBeatInfo data = new HeartBeatInfo();
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

    public void setData(HeartBeatInfo data) {
         this.data = data;
     }
     public HeartBeatInfo getData() {
         return data;
     }

}