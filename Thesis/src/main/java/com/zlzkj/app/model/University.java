package com.zlzkj.app.model;

public class University {
    private Integer id;

    private Integer areaId;

    private String universityName;

    private String linkMan;

    private String linkAddress;

    private String linkTel;

    private Integer isNine;

    private Integer isTwo;

    private Integer universityCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName == null ? null : universityName.trim();
    }

    public String getLinkMan() {
        return linkMan;
    }

    public void setLinkMan(String linkMan) {
        this.linkMan = linkMan == null ? null : linkMan.trim();
    }

    public String getLinkAddress() {
        return linkAddress;
    }

    public void setLinkAddress(String linkAddress) {
        this.linkAddress = linkAddress == null ? null : linkAddress.trim();
    }

    public String getLinkTel() {
        return linkTel;
    }

    public void setLinkTel(String linkTel) {
        this.linkTel = linkTel == null ? null : linkTel.trim();
    }

    public Integer getIsNine() {
        return isNine;
    }

    public void setIsNine(Integer isNine) {
        this.isNine = isNine;
    }

    public Integer getIsTwo() {
        return isTwo;
    }

    public void setIsTwo(Integer isTwo) {
        this.isTwo = isTwo;
    }

    public Integer getUniversityCode() {
        return universityCode;
    }

    public void setUniversityCode(Integer universityCode) {
        this.universityCode = universityCode;
    }
}