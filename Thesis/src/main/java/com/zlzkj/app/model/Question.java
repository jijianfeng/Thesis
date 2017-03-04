package com.zlzkj.app.model;

public class Question {
    private Integer id;

    private String questionTitle;

    private String questionContent;

    private Integer questionTime;

    private String reply;

    private Integer isSolved;

    private Integer solvedTime;

    private Integer questionerId;

    private Integer solvederId;

    private Integer sendType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle == null ? null : questionTitle.trim();
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent == null ? null : questionContent.trim();
    }

    public Integer getQuestionTime() {
        return questionTime;
    }

    public void setQuestionTime(Integer questionTime) {
        this.questionTime = questionTime;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply == null ? null : reply.trim();
    }

    public Integer getIsSolved() {
        return isSolved;
    }

    public void setIsSolved(Integer isSolved) {
        this.isSolved = isSolved;
    }

    public Integer getSolvedTime() {
        return solvedTime;
    }

    public void setSolvedTime(Integer solvedTime) {
        this.solvedTime = solvedTime;
    }

    public Integer getQuestionerId() {
        return questionerId;
    }

    public void setQuestionerId(Integer questionerId) {
        this.questionerId = questionerId;
    }

    public Integer getSolvederId() {
        return solvederId;
    }

    public void setSolvederId(Integer solvederId) {
        this.solvederId = solvederId;
    }

    public Integer getSendType() {
        return sendType;
    }

    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }
}