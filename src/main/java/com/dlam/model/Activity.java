package com.dlam.model;

import jakarta.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "activities")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "course_id", nullable = false)
    private int courseId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "due_date")
    private Date dueDate;

    public Activity() {
    }

    public Activity(int courseId, String title, String description, Date dueDate) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public Activity(int id, int courseId, String title, String description, Date dueDate) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}
