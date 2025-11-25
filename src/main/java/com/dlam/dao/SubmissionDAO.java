package com.dlam.dao;

import com.dlam.model.Submission;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubmissionDAO {
    private Connection conn;

    public SubmissionDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean addSubmission(Submission submission) throws SQLException {
        String sql = "INSERT INTO submissions (activity_id, student_id, content) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, submission.getActivityId());
        ps.setInt(2, submission.getStudentId());
        ps.setString(3, submission.getContent());
        return ps.executeUpdate() > 0;
    }

    public List<Submission> getSubmissionsByActivity(int activityId) throws SQLException {
        List<Submission> list = new ArrayList<>();
        String sql = "SELECT * FROM submissions WHERE activity_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, activityId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    public Submission getSubmissionByStudent(int activityId, int studentId) throws SQLException {
        String sql = "SELECT * FROM submissions WHERE activity_id = ? AND student_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, activityId);
        ps.setInt(2, studentId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapRow(rs);
        }
        return null;
    }

    public List<Submission> getAllSubmissionsByStudent(int activityId, int studentId) throws SQLException {
        List<Submission> list = new ArrayList<>();
        String sql = "SELECT * FROM submissions WHERE activity_id = ? AND student_id = ? ORDER BY submission_date DESC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, activityId);
        ps.setInt(2, studentId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    public List<Integer> getSubmittedActivityIds(int studentId, int courseId) throws SQLException {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT DISTINCT s.activity_id FROM submissions s " +
                "JOIN activities a ON s.activity_id = a.id " +
                "WHERE s.student_id = ? AND a.course_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, studentId);
        ps.setInt(2, courseId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(rs.getInt("activity_id"));
        }
        return list;
    }

    public boolean updateGrade(int submissionId, String grade, String feedback) throws SQLException {
        String sql = "UPDATE submissions SET grade = ?, feedback = ? WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, grade);
        ps.setString(2, feedback);
        ps.setInt(3, submissionId);
        return ps.executeUpdate() > 0;
    }

    private Submission mapRow(ResultSet rs) throws SQLException {
        return new Submission(
                rs.getInt("id"),
                rs.getInt("activity_id"),
                rs.getInt("student_id"),
                rs.getTimestamp("submission_date"),
                rs.getString("content"),
                rs.getString("grade"),
                rs.getString("feedback"));
    }
}
