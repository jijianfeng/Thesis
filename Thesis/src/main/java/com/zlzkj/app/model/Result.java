package com.zlzkj.app.model;

public class Result {
    private Integer id;

    private Integer thesisId;

    private Integer sendUserId;

    private Integer teacherId;

    private Integer universityId;

    private Integer collegeId;

    private Integer majorOne;

    private Integer majorTwo;

    private Integer status;

    private Integer statusTime;

    private String attachment;

    private Integer returnTime;

    private Integer isHundred;

    private Integer thesisResult;

    private Integer thesisResultOne;

    private Integer thesisResultTwo;

    private Integer thesisResultThree;

    private Integer thesisResultFour;

    private String thesisRemark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getThesisId() {
        return thesisId;
    }

    public void setThesisId(Integer thesisId) {
        this.thesisId = thesisId;
    }

    public Integer getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(Integer sendUserId) {
        this.sendUserId = sendUserId;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public Integer getUniversityId() {
        return universityId;
    }

    public void setUniversityId(Integer universityId) {
        this.universityId = universityId;
    }

    public Integer getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Integer collegeId) {
        this.collegeId = collegeId;
    }

    public Integer getMajorOne() {
        return majorOne;
    }

    public void setMajorOne(Integer majorOne) {
        this.majorOne = majorOne;
    }

    public Integer getMajorTwo() {
        return majorTwo;
    }

    public void setMajorTwo(Integer majorTwo) {
        this.majorTwo = majorTwo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(Integer statusTime) {
        this.statusTime = statusTime;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment == null ? null : attachment.trim();
    }

    public Integer getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Integer returnTime) {
        this.returnTime = returnTime;
    }

    public Integer getIsHundred() {
        return isHundred;
    }

    public void setIsHundred(Integer isHundred) {
        this.isHundred = isHundred;
    }

    public Integer getThesisResult() {
        return thesisResult;
    }

    public void setThesisResult(Integer thesisResult) {
        this.thesisResult = thesisResult;
    }

    public Integer getThesisResultOne() {
        return thesisResultOne;
    }

    public void setThesisResultOne(Integer thesisResultOne) {
        this.thesisResultOne = thesisResultOne;
    }

    public Integer getThesisResultTwo() {
        return thesisResultTwo;
    }

    public void setThesisResultTwo(Integer thesisResultTwo) {
        this.thesisResultTwo = thesisResultTwo;
    }

    public Integer getThesisResultThree() {
        return thesisResultThree;
    }

    public void setThesisResultThree(Integer thesisResultThree) {
        this.thesisResultThree = thesisResultThree;
    }

    public Integer getThesisResultFour() {
        return thesisResultFour;
    }

    public void setThesisResultFour(Integer thesisResultFour) {
        this.thesisResultFour = thesisResultFour;
    }

    public String getThesisRemark() {
        return thesisRemark;
    }

    public void setThesisRemark(String thesisRemark) {
        this.thesisRemark = thesisRemark == null ? null : thesisRemark.trim();
    }
}