/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.sis.model;


/**
 *
 * @author namdv
 */
public class SessionInfo {

    public String accountName;
    public long activeTime;
    public int accountType;
    public long expireTime;

    public SessionInfo() {
    }

    public SessionInfo(String accountName, long activeTime, int accountType, long expireTime) {
        this.accountName = accountName;
        this.activeTime = activeTime;
        this.accountType = accountType;
        this.expireTime = expireTime;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public long getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(long activeTime) {
        this.activeTime = activeTime;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
    
    
}
