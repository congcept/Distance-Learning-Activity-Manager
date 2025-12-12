package com.dlam.dto;

import java.time.LocalDateTime;

public class StudentGradeDTO {
    private String courseTitle;
    private String activityTitle;
    private LocalDateTime dueDate;
    private LocalDateTime submittedDate;
    private String grade;
    private String feedback;
    private String status; // Pending, Submitted, Graded, Late, Missing

    public StudentGradeDTO() {
    }

    public StudentGradeDTO(String courseTitle, String activityTitle, LocalDateTime dueDate, LocalDateTime submittedDate,
            String grade, String feedback, String status) {
        this.courseTitle = courseTitle;
        this.activityTitle = activityTitle;
        this.dueDate = dueDate;
        this.submittedDate = submittedDate;
        this.grade = grade;
        this.feedback = feedback;
        this.status = status;
    }

    // Getters and Setters
    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(LocalDateTime submittedDate) {
        this.submittedDate = submittedDate;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
