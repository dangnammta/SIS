/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.sis.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author namdv
 */
public class UserInfo {

    public String username = "";
    public String password = "";
    public String fullName = "";
    public String address = "";
    public String email = "";
    public String phoneNumber;
    public long birthday = 0;
    public int gender = 0;
    public int type = 0;
    public long lastUpdateTime = 0;
    public List<Long> followingReportIds = new ArrayList<>();
    public String avatar;
    
    public static final int TYPE_ORGANAZATION = 1;
    public static final int TYPE_USER = 0;

    public UserInfo() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Long> getFollowingReportIds() {
        return followingReportIds;
    }

    public void setFollowingReportIds(List<Long> followingReportIds) {
        this.followingReportIds = followingReportIds;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    
}
