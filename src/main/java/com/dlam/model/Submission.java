package com.dlam.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "submissions")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "activity_id", nullable = false)
    private int activityId;

    @Column(name = "student_id", nullable = false)
    private int studentId;

    @Column(name = "submission_date", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp submissionDate;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String grade;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @ManyToOne
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private User student;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "original_filename")
    private String originalFilename;

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

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }
}
