/**
  * Copyright 2017 bejson.com 
  */
package tech.bbwang.www.ws;

/**
 * Auto-generated: 2017-09-21 11:1:26
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class ErrorCode {

    private int status = -1;
    private String message;
    private String franchise;
    private TerminalInfo data = new TerminalInfo();
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

    public void setData(TerminalInfo data) {
         this.data = data;
     }
     public TerminalInfo getData() {
         return data;
     }

}