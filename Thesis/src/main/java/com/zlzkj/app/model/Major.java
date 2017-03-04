package com.zlzkj.app.model;

public class Major {
    private Integer id;

    private String majorName;

    private String majorCode;

    private Integer majorStatus;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName == null ? null : majorName.trim();
    }

    public String getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(String majorCode) {
        this.majorCode = majorCode == null ? null : majorCode.trim();
    }

    public Integer getMajorStatus() {
        return majorStatus;
    }

    public void setMajorStatus(Integer majorStatus) {
        this.majorStatus = majorStatus;
    }
}