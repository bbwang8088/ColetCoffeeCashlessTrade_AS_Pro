/**
  * Copyright 2017 bejson.com 
  */
package tech.bbwang.www.ws;

/**
 * Auto-generated: 2017-09-21 11:8:32
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class ActiveInfo {

    private int status = -1;
    private String message;
    private String franchise;
    private ActiveTerminalInfo data = new ActiveTerminalInfo();
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

    public void setData(ActiveTerminalInfo data) {
         this.data = data;
     }
     public ActiveTerminalInfo getData() {
         return data;
     }

}