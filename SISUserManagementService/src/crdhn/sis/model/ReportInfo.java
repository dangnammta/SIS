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
public class ReportInfo {

    public long reportId;
    public String reporterName = "";
    public String content = "";
    public List<String> images;
    public List<CommentInfo> comments;
    public List<String> organizationIds;
    public int status = 0;
    public long createTime;
    public long deadlineTime;
    public String districtAddress;
    public String cityAddress;
    
    public enum ReportStatus {
        Created(10, "Vừa tạo"), Received(11, "Đã tiếp nhận"), 
        Responsed(13, "Đã phản hồi"), Moved(14, "Đã chuyển"), Cancelled(30, "Đã hủy bỏ"), Closed(40, "Đã xử lý");
        private int value;
        private String description;

        ReportStatus(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public int getValue() {
            return this.value;
        }

        public String getDescription() {
            return this.description;
        }
        
        public static ReportStatus getReportStatusByValue(int value) {
            for (ReportStatus reportStatus : ReportStatus.values()) {
                if (reportStatus.value == value) {
                    return reportStatus;
                }
            }
            return null;
        }
    }

    public ReportInfo() {
    }

    public long getReportId() {
        return reportId;
    }

    public void setReportId(long reportId) {
        this.reportId = reportId;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
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

    public List<CommentInfo> getComments() {
        return comments;
    }

    public void setComments(List<CommentInfo> comments) {
        this.comments = comments;
    }

    public List<String> getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(List<String> organizationIds) {
        this.organizationIds = organizationIds;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getDistrictAddress() {
        return districtAddress;
    }

    public void setDistrictAddress(String districtAddress) {
        this.districtAddress = districtAddress;
    }

    public String getCityAddress() {
        return cityAddress;
    }

    public void setCityAddress(String cityAddress) {
        this.cityAddress = cityAddress;
    }

    public long getDeadlineTime() {
        return deadlineTime;
    }

    public void setDeadlineTime(long deadlineTime) {
        this.deadlineTime = deadlineTime;
    }
    
    

}
