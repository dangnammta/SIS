/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.sis.model;

import java.util.List;

/**
 *
 * @author namdv
 */
public class CommentInfo {

    public String ownerId;
    public long reportId;
    public int typeUser;
    public String content;
    public List<String> images;
    public long createTime;

    public CommentInfo() {
    }

    public CommentInfo(String ownerId, long reportId, int typeUser, String content, List<String> images, long createTime) {
        this.ownerId = ownerId;
        this.reportId = reportId;
        this.typeUser = typeUser;
        this.content = content;
        this.images = images;
        this.createTime = createTime;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public int getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(int typeUser) {
        this.typeUser = typeUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getReportId() {
        return reportId;
    }

    public void setReportId(long reportId) {
        this.reportId = reportId;
    }
    
}
