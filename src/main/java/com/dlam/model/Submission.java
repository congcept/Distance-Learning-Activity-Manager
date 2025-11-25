package com.dlam.model;

import java.sql.Timestamp;

public class Submission {
    private int id;
    private int activityId;
    private int studentId;
    private Timestamp submissionDate;
    private String content;
    private String grade;
    private String feedback;

    public Submission() {
    }

    public Submission(int activityId, int studentId, String content) {
        this.activityId = activityId;
        this.studentId = studentId;
        this.content = content;
    }

    public Submission(int id, int activityId, int studentId, Timestamp submissionDate, String content, String grade,
            String feedback) {
        this.id = id;
        this.activityId = activityId;
        this.studentId = studentId;
        this.submissionDate = submissionDate;
        this.content = content;
        this.grade = grade;
        this.feedback = feedback;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Timestamp getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Timestamp submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
