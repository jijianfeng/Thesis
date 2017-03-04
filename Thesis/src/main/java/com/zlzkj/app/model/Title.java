package com.zlzkj.app.model;

public class Title {
    private Integer id;

    private String titlesName;

    private String titleMark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitlesName() {
        return titlesName;
    }

    public void setTitlesName(String titlesName) {
        this.titlesName = titlesName == null ? null : titlesName.trim();
    }

    public String getTitleMark() {
        return titleMark;
    }

    public void setTitleMark(String titleMark) {
        this.titleMark = titleMark == null ? null : titleMark.trim();
    }
}